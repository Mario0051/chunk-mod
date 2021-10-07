package net.fabricmc.example.mixin;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
  @Shadow
  @Final
  private static Logger LOGGER = LogManager.getLogger();

  @Shadow
  @Final
  protected LevelStorage.Session session;

  @Shadow
  protected DynamicRegistryManager.Impl registryManager;

  @Shadow
  @Final
  private Map<RegistryKey<World>, ServerWorld> worlds = Maps.newLinkedHashMap();

  @Shadow
  private PlayerManager playerManager;

  @Shadow
  @Final
  private BossBarManager bossBarManager = new BossBarManager();

  @Shadow
  @Final
  protected SaveProperties saveProperties;

  @Inject(at = @At("TAIL"), method = "prepareStartRegion")
  private void onPrepareStartRegion(CallbackInfo info) {
    this.save(false,false,false);
  }

  @Shadow
  public boolean save(boolean bl, boolean bl2, boolean bl3) {
    boolean bl4 = false;
    for (ServerWorld lv : this.getWorlds()) {
      if (!bl) {
        LOGGER.info("Saving chunks for level '{}'/{}", (Object)lv, (Object)lv.getRegistryKey().getValue());
      }
      lv.save(null, bl2, lv.savingDisabled && !bl3);
      bl4 = true;
    }
    ServerWorld lv2 = this.getOverworld();
    ServerWorldProperties lv3 = this.saveProperties.getMainWorldProperties();
    lv3.setWorldBorder(lv2.getWorldBorder().write());
    this.saveProperties.setCustomBossEvents(this.getBossBarManager().toNbt());
    this.session.backupLevelDataFile(this.registryManager, this.saveProperties, this.getPlayerManager().getUserData());
    return bl4;
  }

  @Shadow
  public Iterable<ServerWorld> getWorlds() {
    return this.worlds.values();
  }

  @Shadow
  @Final
  public ServerWorld getOverworld() {
    return this.worlds.get(World.OVERWORLD);
  }

  @Shadow
  public PlayerManager getPlayerManager() {
    return this.playerManager;
  }

  @Shadow
  public BossBarManager getBossBarManager() {
    return this.bossBarManager;
  }
}
