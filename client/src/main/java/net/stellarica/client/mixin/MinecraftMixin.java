package net.stellarica.client.mixin;

import net.minecraft.client.Minecraft;
import net.stellarica.client.StellaricaClient;
import net.stellarica.common.networking.Channel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@Inject(method = "pickBlock", at = @At("RETURN"))
	private void onPickBlock(CallbackInfo ci) {
		StellaricaClient.INSTANCE.getNetworkHandler().sendPacket(Channel.ITEM_SYNC, new byte[0]);
	}
}
