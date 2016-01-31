package storagecraft.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import storagecraft.container.slot.SlotSpecimen;

public class ContainerStorage extends ContainerBase
{
	public ContainerStorage(EntityPlayer player, IInventory inventory)
	{
		super(player);

		for (int i = 0; i < 9; ++i)
		{
			addSlotToContainer(new SlotSpecimen(inventory, i, 8 + (18 * i), 20));
		}

		addPlayerInventory(8, 129);
	}
}