package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.block.info.BlockDirection;
import com.raoulvdberge.refinedstorage.block.info.IBlockInfo;
import com.raoulvdberge.refinedstorage.integration.mcmp.IntegrationMCMP;
import com.raoulvdberge.refinedstorage.integration.mcmp.RSMCMPAddon;
import com.raoulvdberge.refinedstorage.item.itemblock.ItemBlockBase;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public abstract class BlockBase extends Block {
    protected final IBlockInfo info;

    public BlockBase(IBlockInfo info) {
        super(info.getMaterial());

        this.info = info;

        setHardness(info.getHardness());
        setRegistryName(info.getId());
        setCreativeTab(RS.INSTANCE.tab);
        setSoundType(info.getSoundType());
    }

    @SideOnly(Side.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
    }

    @Override
    public String getTranslationKey() {
        return "block." + info.getId().toString();
    }

    protected BlockStateContainer.Builder createBlockStateBuilder() {
        BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);

        if (getDirection() != null) {
            builder.add(getDirection().getProperty());
        }

        return builder;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return createBlockStateBuilder().build();
    }

    public Item createItem() {
        return new ItemBlockBase(this, false);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (getDirection() != null) {
            TileEntity tile = IntegrationMCMP.isLoaded() ? RSMCMPAddon.unwrapTile(world, pos) : world.getTileEntity(pos);

            if (tile instanceof TileBase) {
                return state.withProperty(getDirection().getProperty(), ((TileBase) tile).getDirection());
            }
        }

        return state;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        if (!world.isRemote && getDirection() != null) {
            TileBase tile = (TileBase) world.getTileEntity(pos);

            EnumFacing newDirection = getDirection().cycle(tile.getDirection());

            tile.setDirection(newDirection);

            WorldUtils.updateBlock(world, pos);

            return true;
        }

        return false;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        dropContents(world, pos);
        removeTile(world, pos, state);
    }

    void removeTile(World world, BlockPos pos, IBlockState state) {
        if (hasTileEntity(state)) {
            world.removeTileEntity(pos);
        }
    }

    void dropContents(World world, BlockPos pos) {
        TileEntity tile = IntegrationMCMP.isLoaded() ? RSMCMPAddon.unwrapTile(world, pos) : world.getTileEntity(pos);

        if (tile instanceof TileBase && ((TileBase) tile).getDrops() != null) {
            WorldUtils.dropInventory(world, pos, ((TileBase) tile).getDrops());
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        return willHarvest || super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack stack) {
        super.harvestBlock(world, player, pos, state, tile, stack);

        world.setBlockToAir(pos);
    }

    @Override
    public final boolean hasTileEntity(IBlockState state) {
        return info.hasTileEntity();
    }

    @Override
    public final TileEntity createTileEntity(World world, IBlockState state) {
        return info.createTileEntity();
    }

    @Nullable
    public BlockDirection getDirection() {
        return null;
    }

    public final IBlockInfo getInfo() {
        return info;
    }
}
