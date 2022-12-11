package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.Random;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.block.CustomBurnabilityBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.util.CaughtFireBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin {
	@Inject(
			method = "checkBurnOut",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;"),
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true
	)
	public void port_lib$onCaughtFire(Level level, BlockPos pos, int chance, Random random, int age, CallbackInfo ci, int i, BlockState blockState) {
		if (blockState.getBlock() instanceof CaughtFireBlock fireBlock) {
			fireBlock.onCaughtFire(blockState, level, pos, null, null);
			ci.cancel();
		}
	}

	@ModifyExpressionValue(method = "canBurn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FireBlock;getFlameOdds(Lnet/minecraft/world/level/block/state/BlockState;)I"))
	private int port_lib$customBurnability(int igniteOdds, BlockState state) {
		if (state.getBlock() instanceof CustomBurnabilityBlock custom) {
			boolean burnable = custom.canBurn(state);
			// replace igniteOdds. normally, burnable if igniteOdds > 0
			return burnable ? 1 : 0;
		}
		return igniteOdds;
	}
}
