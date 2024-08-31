package net.fabricmc.example.mixin;

import com.google.common.util.concurrent.AbstractFuture;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import net.minecraft.world.SpawnHelper;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerChunkManager.class)
public abstract class ServerChunkManagerMixin {
    @Shadow public abstract int getTotalChunksLoadedCount();

    @Shadow @Final private ServerWorld world;

    @Shadow public abstract int getLoadedChunkCount();

    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z", ordinal = 0))
    public boolean dontSpawnMobsIfSpawnChunksNotLoaded(GameRules instance, GameRules.Key<GameRules.BooleanRule> rule) {
        if (this.world.getChunkManager().getTotalChunksLoadedCount() < Math.pow(this.world.getServer().getPlayerManager().getViewDistance(), 2)) {
            return false;
        } else {
            return true;
        }
    }
}
