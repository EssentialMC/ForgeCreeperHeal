package fr.eyzox.forgecreeperheal.builder.blockdata;

import fr.eyzox.forgecreeperheal.blockdata.BlockData;
import fr.eyzox.forgecreeperheal.blockdata.MultiBlockData;
import fr.eyzox.forgecreeperheal.blockdata.multi.selector.PistonMultiSelector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PistonBlockDataBuilder extends MultiBlockDataBuilder {

    public PistonBlockDataBuilder() {
        super(null, new PistonMultiSelector());
    }

    @Override
    public boolean accept(Block block) {
        return BlockPistonBase.class.isAssignableFrom(block.getClass()) || BlockPistonExtension.class.isAssignableFrom(block.getClass()) || BlockPistonMoving.class.isAssignableFrom(block.getClass());
    }

    @Override
    public BlockData create(World w, BlockPos pos, IBlockState state) {
        if (BlockPistonBase.class.isAssignableFrom(state.getBlock().getClass())) {
            if (((Boolean) state.getValue(BlockPistonBase.EXTENDED)).booleanValue()) {
                return super.create(w, pos, state);
            }
            return new BlockData(pos, state);
        }
        return null;
    }

    @Override
    public BlockData create(NBTTagCompound tag) {
        if (MultiBlockData.isMultiple(tag)) {
            return new MultiBlockData(tag);
        }
        return new BlockData(tag);
    }


}
