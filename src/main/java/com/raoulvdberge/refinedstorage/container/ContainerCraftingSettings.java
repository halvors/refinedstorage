package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.SlotDisabled;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluidDisabled;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackFluid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackItem;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class ContainerCraftingSettings extends ContainerBase {
    public ContainerCraftingSettings(EntityPlayer player, IGridStack stack) {
        super(null, player);

        if (stack instanceof GridStackFluid) {
            FluidInventory inventory = new FluidInventory(1);

            inventory.setFluid(0, ((GridStackFluid) stack).getStack());

            addSlotToContainer(new SlotFilterFluidDisabled(inventory, 0, 89, 48));
        } else if (stack instanceof GridStackItem) {
            ItemStackHandler handler = new ItemStackHandler(1);

            handler.setStackInSlot(0, ItemHandlerHelper.copyStackWithSize(((GridStackItem) stack).getStack(), 1));

            addSlotToContainer(new SlotDisabled(handler, 0, 89, 48));
        }
    }
}
