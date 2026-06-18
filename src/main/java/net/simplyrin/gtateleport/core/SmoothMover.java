package net.simplyrin.gtateleport.core;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SmoothMover {

    private static final double TOTAL_TIME = 3.0;
    private static final double ACCEL_TIME = 0.3;
    private static final double DECEL_TIME = 0.3;
    private static final float LOOK_DOWN_PITCH = 90.0f;

    private static final Sound ASCEND_SOUND = Sound.ITEM_TRIDENT_RIPTIDE_2;
    private static final float ASCEND_SOUND_VOLUME = 1.0f;
    private static final float ASCEND_SOUND_PITCH = 0.7f; 

    // ===== 飛行ON・移動禁止にしてから一連の流れを開始 =====
    public void startWarp(JavaPlugin plugin, Player player, Location from, Location to) {
        final boolean originalAllowFlight = player.getAllowFlight();
        final boolean originalFlying = player.isFlying();
        final float originalWalkSpeed = player.getWalkSpeed();
        final float originalFlySpeed = player.getFlySpeed();
        final float originalYaw = player.getLocation().getYaw();
		final float originalPitch = player.getLocation().getPitch();

        player.setAllowFlight(true);
        player.setFlying(true);
        player.setWalkSpeed(0f);
        player.setFlySpeed(0f);

        ascendThenMoveSmoothly(plugin, player, from, to, () -> {
            player.setWalkSpeed(originalWalkSpeed);
            player.setFlySpeed(originalFlySpeed);
            player.setFlying(originalFlying);
            player.setAllowFlight(originalAllowFlight);

            Location location = player.getLocation();
            player.teleport(new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), originalYaw, originalPitch));
        });
    }

    public void ascendThenMoveSmoothly(JavaPlugin plugin, Entity entity, Location from, Location to, Runnable onComplete) {
        final World world = from.getWorld();
        final int stepTicks = 12; // 0.6秒
        final double stepHeight = 30.0;

        float startYaw = entity.getLocation().getYaw();
        entity.teleport(new Location(world, from.getX(), from.getY(), from.getZ(), startYaw, LOOK_DOWN_PITCH));

        new BukkitRunnable() {
            int stage = 0;

            @Override
            public void run() {
                if (!entity.isValid()) {
                    cancel();
                    return;
                }

                stage++;
                float yaw = entity.getLocation().getYaw();
                Location next = new Location(world, from.getX(),
                        from.getY() + stepHeight * stage, from.getZ(), yaw, LOOK_DOWN_PITCH);
                entity.teleport(next);

                if (entity instanceof Player player) {
                    player.playSound(next, ASCEND_SOUND, ASCEND_SOUND_VOLUME, ASCEND_SOUND_PITCH);
                }

                if (stage >= 3) {
                    cancel();
                    moveSmoothly(plugin, entity, next, to, () -> descendUntilGround(plugin, entity, onComplete));
                }
            }
        }.runTaskTimer(plugin, stepTicks, stepTicks);
    }

    public void moveSmoothly(JavaPlugin plugin, Entity entity, Location from, Location to, Runnable onComplete) {
        final World world = from.getWorld();
        final int totalTicks = (int) Math.round(TOTAL_TIME * 20);

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (!entity.isValid()) {
                    cancel();
                    return;
                }
                if (tick > totalTicks) {
                    cancel();
                    if (onComplete != null) onComplete.run();
                    return;
                }

                double t = tick / 20.0;
                double progress = calcProgress(t);

                double x = from.getX() + (to.getX() - from.getX()) * progress;
                double z = from.getZ() + (to.getZ() - from.getZ()) * progress;

                float yaw = entity.getLocation().getYaw();
                entity.teleport(new Location(world, x, from.getY(), z, yaw, LOOK_DOWN_PITCH));
                tick++;
            }
        }.runTaskTimer(plugin, 20L, 1L);
    }

    private double calcProgress(double t) {
        double cruiseTime = TOTAL_TIME - ACCEL_TIME - DECEL_TIME;
        double vMax = 1.0 / (cruiseTime + (ACCEL_TIME + DECEL_TIME) / 2.0);

        if (t <= ACCEL_TIME) {
            return vMax * t * t / (2 * ACCEL_TIME);
        } else if (t <= TOTAL_TIME - DECEL_TIME) {
            double base = vMax * ACCEL_TIME / 2.0;
            return base + vMax * (t - ACCEL_TIME);
        } else {
            double s = t - (TOTAL_TIME - DECEL_TIME);
            double base = vMax * ACCEL_TIME / 2.0 + vMax * cruiseTime;
            return base + vMax * s - vMax * s * s / (2 * DECEL_TIME);
        }
    }

    private void descendUntilGround(JavaPlugin plugin, Entity entity, Runnable onComplete) {
        final World world = entity.getWorld();
        final int stepTicks = 12;
        final double stepDown = 30.0;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!entity.isValid()) {
                    cancel();
                    return;
                }

                Location current = entity.getLocation();
                int groundY = world.getHighestBlockYAt(
                        (int) Math.floor(current.getX()),
                        (int) Math.floor(current.getZ())) + 1;
                double nextY = current.getY() - stepDown;
                float yaw = current.getYaw();

                if (nextY <= groundY) {
					Location location = new Location(world, current.getX(), groundY, current.getZ(), yaw, LOOK_DOWN_PITCH);
                    entity.teleport(location);
					if (entity instanceof Player player) {
						player.playSound(location, ASCEND_SOUND, ASCEND_SOUND_VOLUME, ASCEND_SOUND_PITCH);
					}
                    cancel();
                    if (onComplete != null) {
                      onComplete.run();
                    }
                    return;
                }

				Location location = new Location(world, current.getX(), nextY, current.getZ(), yaw, LOOK_DOWN_PITCH);

                entity.teleport(location);
				if (entity instanceof Player player) {
					player.playSound(location, ASCEND_SOUND, ASCEND_SOUND_VOLUME, ASCEND_SOUND_PITCH);
				}
            }
        }.runTaskTimer(plugin, stepTicks, stepTicks);
    }
}
