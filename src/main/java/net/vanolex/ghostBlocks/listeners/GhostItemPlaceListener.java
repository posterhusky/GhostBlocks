package net.vanolex.ghostBlocks.listeners;

import net.vanolex.ghostBlocks.GhostBlocks;
import net.vanolex.ghostBlocks.Utils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class GhostItemPlaceListener implements Listener {

    @EventHandler
    void onPlace(BlockPlaceEvent e) {
        Pair<Integer, Integer> materialData = Utils.readReplaceMaterial(e.getItemInHand());
        if (materialData == null) return;

        Utils.spawnGhostBlock(e.getBlockPlaced());

        e.setCancelled(true);

        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.NOTE_STICKS, 2f, 1.3f);
        Bukkit.getScheduler().scheduleSyncDelayedTask(GhostBlocks.plugin, () -> {
            if (materialData.getLeft() != -1) {
                e.getBlock().setTypeId(materialData.getLeft(), false);
            }
            if (materialData.getRight() != -1) {
                e.getBlock().setData(materialData.getRight().byteValue(), false);
            }
        }, 1L);
    }
}
