package refinedstorage.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageGui;
import refinedstorage.item.ItemBlockStorage;
import refinedstorage.tile.TileStorage;

import java.util.ArrayList;
import java.util.List;

public class BlockStorage extends BlockMachine {
    public static final PropertyEnum TYPE = PropertyEnum.create("type", EnumStorageType.class);

    public BlockStorage() {
        super("storage");
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List subItems) {
        for (int i = 0; i <= 4; ++i) {
            subItems.add(ItemBlockStorage.initNBT(new ItemStack(item, 1, i)));
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{
            DIRECTION,
            CONNECTED,
            TYPE
        });
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, EnumStorageType.getById(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return ((EnumStorageType) state.getValue(TYPE)).getId();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileStorage();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(RefinedStorage.INSTANCE, RefinedStorageGui.STORAGE, world, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, player, stack);

        NBTTagCompound tag = stack.getTagCompound();

        if (tag != null && tag.hasKey(TileStorage.NBT_STORAGE)) {
            ((TileStorage) world.getTileEntity(pos)).setStorageTag((NBTTagCompound) tag.getTag(TileStorage.NBT_STORAGE));
        }
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> drops = new ArrayList<ItemStack>();

        ItemStack stack = new ItemStack(RefinedStorageBlocks.STORAGE, 1, RefinedStorageBlocks.STORAGE.getMetaFromState(state));

        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag(TileStorage.NBT_STORAGE, ((TileStorage) world.getTileEntity(pos)).getStorageTag());
        stack.setTagCompound(tag);

        drops.add(stack);

        return drops;
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (willHarvest) {
            return true;
        }

        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack stack) {
        super.harvestBlock(world, player, pos, state, tile, stack);

        world.setBlockToAir(pos);
    }

    @Override
    public Item createItemForBlock() {
        return new ItemBlockStorage();
    }
}
