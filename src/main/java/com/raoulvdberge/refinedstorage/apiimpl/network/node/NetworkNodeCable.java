package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.raoulvdberge.refinedstorage.block.BlockCable;
import com.raoulvdberge.refinedstorage.integration.mcmp.IntegrationMCMP;
import com.raoulvdberge.refinedstorage.integration.mcmp.RSMCMPAddon;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class NetworkNodeCable extends NetworkNode implements ICoverable {
    public static final String ID = "cable";

    private static final String NBT_COVERS = "Covers";

    private CoverManager coverManager = new CoverManager(this);

    public NetworkNodeCable(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.cableUsage;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public CoverManager getCoverManager() {
        return coverManager;
    }

    @Override
    public boolean canConduct(@Nullable EnumFacing direction) {
        if (IntegrationMCMP.isLoaded() && direction != null) {
            return BlockCable.hasConnectionWith(world, pos, RSBlocks.CABLE, IntegrationMCMP.isLoaded() ? RSMCMPAddon.unwrapTile(world, pos) : world.getTileEntity(pos), direction);
        }

        return coverManager.canConduct(direction);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setTag(NBT_COVERS, coverManager.writeToNbt());

        return tag;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        if (tag.hasKey(NBT_COVERS)) {
            coverManager.readFromNbt(tag.getTagList(NBT_COVERS, Constants.NBT.TAG_COMPOUND));
        }
    }

    @Nullable
    @Override
    public IItemHandler getDrops() {
        return coverManager.getAsInventory();
    }
}
