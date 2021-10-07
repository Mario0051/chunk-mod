package net.fabricmc.example.mixin;

import net.minecraft.server.world.ServerChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ServerChunkManager.class})
public class ServerChunkManagerMixin {
  @Inject(method = {"getTotalChunksLoadedCount"}, at = {@At("RETURN")}, cancellable = true)
  private void onGetTotalChunksLoadedCount(CallbackInfoReturnable<Integer> cir) {
    cir.setReturnValue(Integer.valueOf(441));
  }
}
