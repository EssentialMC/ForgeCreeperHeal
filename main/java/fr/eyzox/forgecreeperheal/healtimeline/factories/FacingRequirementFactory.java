package fr.eyzox.forgecreeperheal.healtimeline.factories;

import net.minecraft.block.properties.IProperty;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import fr.eyzox.forgecreeperheal.healtimeline.BlockData;

public class FacingRequirementFactory extends PropertyRequirementFactory {
	
	public FacingRequirementFactory(IProperty property) {
		super(property);
	}

	public FacingRequirementFactory(Class<?> clazz, IProperty property) {
		super(clazz, property);
	}

	@Override
	public BlockPos[] getRequiredBlockPos(BlockData blockData, Enum e) {
		return getRequiredBlockPos(blockData, (EnumFacing)e);
	}
	
	public static BlockPos[] getRequiredBlockPos(BlockData blockData, EnumFacing e) {
		return new BlockPos[]{blockData.getBlockPos().offset(e)};
	}


}
