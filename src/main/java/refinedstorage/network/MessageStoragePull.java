package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.storage.StorageItem;
import refinedstorage.tile.TileController;

public class MessageStoragePull extends MessageHandlerPlayerToServer<MessageStoragePull> implements IMessage
{
	private int x;
	private int y;
	private int z;
	private int id;
	private boolean half;
	private boolean one;
	private boolean shift;

	public MessageStoragePull()
	{
	}

	public MessageStoragePull(int x, int y, int z, int id, boolean half, boolean one, boolean shift)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
		this.half = half;
		this.one = one;
		this.shift = shift;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		id = buf.readInt();
		half = buf.readBoolean();
		one = buf.readBoolean();
		shift = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(id);
		buf.writeBoolean(half);
		buf.writeBoolean(one);
		buf.writeBoolean(shift);
	}

	@Override
	public void handle(MessageStoragePull message, EntityPlayerMP player)
	{
		TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));

		if (tile instanceof TileController)
		{
			TileController controller = (TileController) tile;

			if (message.id < controller.getItems().size())
			{
				StorageItem item = controller.getItems().get(message.id);

				int quantity = 64;

				if (message.half && item.getQuantity() > 1)
				{
					quantity = item.getQuantity() / 2;

					if (quantity > 64)
					{
						quantity = 64;
					}
				}
				else if (message.one)
				{
					quantity = 1;
				}
				else if (message.shift && quantity > item.getType().getItemStackLimit(item.toItemStack()))
				{
					quantity = item.getType().getItemStackLimit(item.toItemStack());
				}

				ItemStack took = controller.take(item.copy(quantity).toItemStack());

				if (took != null)
				{
					if (message.shift)
					{
						if (!player.inventory.addItemStackToInventory(took.copy()))
						{
							controller.push(took);
						}
					}
					else
					{
						player.inventory.setItemStack(took);
						player.updateHeldItem();
					}
				}
			}
		}
	}
}