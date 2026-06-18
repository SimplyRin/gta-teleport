package net.simplyrin.gtateleport.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.simplyrin.gtateleport.GtaTeleport;

public class CommandGtaTeleport implements CommandExecutor {

  private final GtaTeleport instance;

  public CommandGtaTeleport(GtaTeleport instance) {
    this.instance = instance;
  }

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
    }

    if (args.length == 1) {
      
      if (args[0].equalsIgnoreCase("bed")) {
        this.instance.getServer().getScheduler().runTask(this.instance, () -> {
          Player player = (Player) sender;
         
          try {
            if (player.getRespawnLocation() == null) {
              sender.sendMessage("§cRespawn location not found. -1");
              return;
            }
            this.instance.getSmoothMover().startWarp(this.instance, player, player.getLocation(), player.getRespawnLocation());
          } catch (Exception e) {
            sender.sendMessage("§cRespawn location not found. -2");
          }
        });
        return true;
      }

      String targetPlayerName = args[0];
      this.instance.getServer().getScheduler().runTask(this.instance, () -> {
        Player player = instance.getServer().getPlayer(targetPlayerName);
        if (player == null) {
          sender.sendMessage("§cPlayer not found.");
          return;
        }
        this.instance.getSmoothMover().startWarp(this.instance, player, player.getLocation(), player.getBedLocation());
      });
      return true;
    } else if (args.length == 3) {
      try {
        double x = Double.parseDouble(args[0]);
        double y = Double.parseDouble(args[1]);
        double z = Double.parseDouble(args[2]);

        this.instance.getServer().getScheduler().runTask(this.instance, () -> {
          Player player = (Player) sender;
          this.instance.getSmoothMover().startWarp(this.instance, player, player.getLocation(), player.getWorld().getBlockAt((int) x, (int) y, (int) z).getLocation());
        });
      } catch (NumberFormatException e) {
        sender.sendMessage("§cInvalid coordinates.");
      } 
    }
    return true;
  }
  
}
