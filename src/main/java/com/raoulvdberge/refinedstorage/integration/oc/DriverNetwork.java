package com.raoulvdberge.refinedstorage.integration.oc;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.capability.CapabilityNetworkNodeProxy;
import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.DriverBlock;
import li.cil.oc.api.network.ManagedEnvironment;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DriverNetwork implements DriverBlock {
    @Override
    public boolean worksWith(World world, BlockPos pos, EnumFacing facing) {
        TileEntity tile = world.getTileEntity(pos);

        // Avoid bug #1855 (https://github.com/raoulvdberge/refinedstorage/issues/1855)
        if (tile instanceof INetwork) {
            return false;
        }

        return tile != null && tile.hasCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, facing);
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing facing) {
        return new EnvironmentNetwork(world.getTileEntity(pos).getCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, facing).getNode());
    }

    public static void register() {
        Driver.add(new DriverNetwork());

        Driver.add(new ConverterCraftingPattern());
        Driver.add(new ConverterCraftingTask());
        Driver.add(new ConverterCraftingRequestInfo());
    }
}
