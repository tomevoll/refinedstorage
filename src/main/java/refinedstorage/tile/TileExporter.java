package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import refinedstorage.container.ContainerExporter;
import refinedstorage.inventory.InventorySimple;
import refinedstorage.tile.config.ICompareConfig;
import refinedstorage.util.InventoryUtils;

public class TileExporter extends TileMachine implements ICompareConfig {
    public static final String NBT_COMPARE = "Compare";

    public static final int SPEED = 3;

    private InventorySimple inventory = new InventorySimple("exporter", 9, this);

    private int compare = 0;

    @Override
    public int getEnergyUsage() {
        return 2;
    }

    @Override
    public void updateMachine() {
        TileEntity connectedTile = worldObj.getTileEntity(pos.offset(getDirection()));

        if (connectedTile instanceof IInventory) {
            IInventory connectedInventory = (IInventory) connectedTile;

            if (ticks % SPEED == 0) {
                for (int i = 0; i < inventory.getSizeInventory(); ++i) {
                    ItemStack slot = inventory.getStackInSlot(i);

                    if (slot != null) {
                        ItemStack toTake = slot.copy();
                        toTake.stackSize = 1;

                        ItemStack took = controller.take(toTake, compare);

                        if (took != null) {
                            ItemStack remaining = TileEntityHopper.putStackInInventoryAllSlots(connectedInventory, took, getDirection().getOpposite());

                            if (remaining != null) {
                                controller.push(remaining);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getCompare() {
        return compare;
    }

    @Override
    public void setCompare(int compare) {
        markDirty();

        this.compare = compare;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        if (nbt.hasKey(NBT_COMPARE)) {
            compare = nbt.getInteger(NBT_COMPARE);
        }

        InventoryUtils.restoreInventory(inventory, 0, nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setInteger(NBT_COMPARE, compare);

        InventoryUtils.saveInventory(inventory, 0, nbt);
    }

    @Override
    public void receiveContainerData(ByteBuf buf) {
        super.receiveContainerData(buf);

        compare = buf.readInt();
    }

    @Override
    public void sendContainerData(ByteBuf buf) {
        super.sendContainerData(buf);

        buf.writeInt(compare);
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerExporter.class;
    }

    public IInventory getInventory() {
        return inventory;
    }
}
