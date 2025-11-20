package me.clin.disableItems4Ivo;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class DisableItems4Ivo extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new InteractionListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
