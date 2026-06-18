package net.simplyrin.gtateleport;

import org.bukkit.plugin.java.JavaPlugin;

import net.simplyrin.gtateleport.commands.CommandGtaTeleport;
import net.simplyrin.gtateleport.core.SmoothMover;

public class GtaTeleport extends JavaPlugin {

    private SmoothMover smoothMover;

    @Override
    public void onEnable() {
        this.getCommand("gtateleport").setExecutor(new CommandGtaTeleport(this));
        this.getCommand("gtatp").setExecutor(new CommandGtaTeleport(this));

        this.smoothMover = new SmoothMover();
    }

    public SmoothMover getSmoothMover() {
        return this.smoothMover;
    }

}
