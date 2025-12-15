package net.vanolex.ghostBlocks;

import net.vanolex.ghostBlocks.commands.GhostItemCmd;
import net.vanolex.ghostBlocks.commands.GhostModifyCmd;
import net.vanolex.ghostBlocks.commands.GhostWandCmd;
import net.vanolex.ghostBlocks.listeners.GhostItemPlaceListener;
import net.vanolex.ghostBlocks.listeners.SelectionListener;
import net.vanolex.ghostBlocks.listeners.WandInteractListener;
import net.vanolex.ghostBlocks.selection.ParticleManager;
import net.vanolex.ghostBlocks.selection.SelectionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class GhostBlocks extends JavaPlugin {

    public static GhostBlocks plugin;

    @Override
    public void onEnable() {
        plugin = this;

        getServer().getPluginManager().registerEvents(new GhostItemPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new WandInteractListener(), this);
        getServer().getPluginManager().registerEvents(new SelectionListener(), this);

        getCommand("ghostitem").setExecutor(new GhostItemCmd());
        getCommand("ghostwand").setExecutor(new GhostWandCmd());
        getCommand("ghostmodify").setExecutor(new GhostModifyCmd());

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                ParticleManager.highlightBlocks(p);
                int selSize = SelectionManager.getOrCreateList(p).size();
                if (selSize > 0) {
                    Utils.sendActionbar(p, "§7Selected blocks: §e§l" + selSize);
                }
            }
        }, 0L, 5L);

        getLogger().info("Plugin enabled!");

    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled!");
    }
}
