package net.stellarica.server.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nullable;

@Mixin(NoteBlock.class)
public class NoteBlockMixin {

	/**
	 * @author trainb0y
	 * @reason Compatibility be damned
	 */
	@Overwrite
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return InteractionResult.SUCCESS;
	}

	/**
	 * @author trainb0y
	 * @reason Compatibility be damned
	 */
	@Overwrite
	public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int type, int data) {
		return false;
	}

	/**
	 * @author trainb0y
	 * @reason Do I really need to type this out again...
	 */
	@Overwrite
	private void playNote(@Nullable Entity entity, BlockState state, Level world, BlockPos pos) {

	}

	/**
	 * @author trainb0y
	 * @reason You get the point by now
	 */
	@Overwrite
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {

	}
}
