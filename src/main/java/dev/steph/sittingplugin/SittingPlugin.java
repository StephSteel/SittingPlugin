package dev.steph.sittingplugin;

import org.bukkit.plugin.java.JavaPlugin;
import dev.steph.sittingplugin.Commands.SitCommand;

public final class SittingPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getCommand("sit").setExecutor(new SitCommand());
        this.getServer().getPluginManager().registerEvents(new SitCommand(), this);
    }
}
