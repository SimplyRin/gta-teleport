package net.simplyrin.gtateleport.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import lombok.RequiredArgsConstructor;
import net.simplyrin.gtateleport.GtaTeleport;

@RequiredArgsConstructor
public class CommandGtaTeleport implements CommandExecutor {

    private final GtaTeleport instance;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
            @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /gtateleport <x> <y> <z> or /gtateleport @player");
            return true;
        }

        if (sender instanceof Player player) {
            player.setAllowFlight(true);
            player.setFlying(true);
        } else {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("bed")) {
                if (!player.hasPermission("gtateleport.bed")) {
                    player.sendMessage("§cYou do not have access to this command");
                    return true;
                }

                if (!player.getWorld().getEnvironment().equals(org.bukkit.World.Environment.NORMAL)) {
                    player.sendMessage("§cYou can only use this command in the overworld.");
                    return true;
                }

                this.instance.getServer().getScheduler().runTask(this.instance, () -> {
                    if (player.getRespawnLocation() == null) {
                        sender.sendMessage("§cRespawn location not found.");
                        return;
                    }
                    this.instance.getSmoothMover().startWarp(this.instance, player, player.getLocation(),
                            player.getRespawnLocation());
                });
                return true;
            }

            if (args[0].equalsIgnoreCase("spawn")) {
                if (!player.hasPermission("gtateleport.spawn")) {
                    player.sendMessage("§cYou do not have access to this command");
                    return true;
                }

                if (!player.getWorld().getEnvironment().equals(org.bukkit.World.Environment.NORMAL)) {
                    player.sendMessage("§cYou can only use this command in the overworld.");
                    return true;
                }

                this.instance.getServer().getScheduler().runTask(this.instance, () -> {
                    this.instance.getSmoothMover().startWarp(this.instance, player, player.getLocation(),
                            player.getWorld().getSpawnLocation());
                });
                return true;
            }

            if (!player.hasPermission("gtateleport.others")) {
                player.sendMessage("§cYou do not have access to this command");
                return true;
            }

            Player targetPlayer = instance.getServer().getPlayer(args[0]);
            if (targetPlayer == null) {
                sender.sendMessage("§cPlayer not found.");
                return true;
            }
            this.instance.getServer().getScheduler().runTask(this.instance, () -> {
                this.instance.getSmoothMover().startWarp(this.instance, player, player.getLocation(),
                        player.getBedLocation());
            });
            return true;
        } else if (args.length == 2 || args.length == 3 && player.hasPermission("gtateleport.coordinates")) {
            try {
                double x = Double.parseDouble(args[0]);
                double z = Double.parseDouble(args[args.length == 2 ? 1 : 2]);

                this.instance.getServer().getScheduler().runTask(this.instance, () -> {
                    this.instance.getSmoothMover().startWarp(this.instance, player, player.getLocation(),
                            player.getWorld().getBlockAt((int) x, 0, (int) z).getLocation());
                });
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid coordinates.");
            }
        } else {
            sender.sendMessage("§cUsage: /gtateleport <x> <y> <z> or /gtateleport @player");
        }
        return true;
    }

}
