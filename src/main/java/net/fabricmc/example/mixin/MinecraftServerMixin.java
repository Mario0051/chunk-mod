package net.fabricmc.example.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
  @Redirect(method = "prepareStartRegion", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerChunkManager;getTotalChunksLoadedCount()I"))
  private int redirectGetTotalChunksLoadedCount(ServerChunkManager serverChunkManager) {
    return 1;
  }

  @ModifyConstant(method = "prepareStartRegion", constant = @Constant(intValue = 441))
  private int modifyNumChunksToWaitFor(int value) {
    return 1;
  }

  /**
   * @author Gregor0410
   * @reason https://github.com/Mario0051/chunk-mod/issues/3
   */
  @Inject(method = "prepareStartRegion", at = @At("TAIL"))
  private void onPrepareStartRegion(CallbackInfo info) {
    ((MinecraftServer) (Object) this).save(false,false,false);
  }
}