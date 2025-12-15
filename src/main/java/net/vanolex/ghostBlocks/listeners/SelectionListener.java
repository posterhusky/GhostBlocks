package net.vanolex.ghostBlocks.listeners;

import net.vanolex.ghostBlocks.selection.SelectionManager;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;

public class SelectionListener implements Listener {

    @EventHandler
    void onQuit(PlayerQuitEvent e) {
        SelectionManager.selectionMap.remove(e.getPlayer());
    }

    @EventHandler
    void onWorldChange(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        HashSet<ArmorStand> selection = SelectionManager.getOrCreateList(p);
        if (selection.isEmpty()) return;
        selection.clear();
        p.playSound(e.getPlayer().getLocation(), Sound.NOTE_SNARE_DRUM, 2f, 1f);
        p.sendMessage("§eYour ghost block selection was cleared because you changed worlds.");
    }

}
