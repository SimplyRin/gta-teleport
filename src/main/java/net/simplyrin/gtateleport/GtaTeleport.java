package net.simplyrin.gtateleport;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import net.simplyrin.gtateleport.commands.CommandGtaTeleport;
import net.simplyrin.gtateleport.core.SmoothMover;

public class GtaTeleport extends JavaPlugin {

    @Getter
    private SmoothMover smoothMover;

    @Override
    public void onEnable() {
        this.getCommand("gtateleport").setExecutor(new CommandGtaTeleport(this));
        this.getCommand("gtatp").setExecutor(new CommandGtaTeleport(this));

        this.smoothMover = new SmoothMover();
    }

}
