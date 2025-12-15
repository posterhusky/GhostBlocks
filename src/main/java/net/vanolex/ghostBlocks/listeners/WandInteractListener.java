package net.vanolex.ghostBlocks.listeners;

import net.vanolex.ghostBlocks.GhostBlocks;
import net.vanolex.ghostBlocks.Utils;
import net.vanolex.ghostBlocks.selection.Raycasting;
import net.vanolex.ghostBlocks.selection.SelectionManager;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;

public class WandInteractListener implements Listener {

    @Nullable
    public static Pair<Integer, Integer> getWandReplaceOrNull(Player p) {
        ItemStack i = p.getItemInHand();
        if (i == null || i.getType() != Material.FEATHER) return null;
        return Utils.readReplaceMaterial(i);
    }

    @EventHandler
    void onBreak(BlockBreakEvent e) {
        Pair<Integer, Integer> materialData = getWandReplaceOrNull(e.getPlayer());
        if (materialData == null) return;

        e.setCancelled(true);

        if (!Raycasting.autoSelect(e.getPlayer()).isEmpty()) return;

        Utils.spawnGhostBlock(e.getBlock());

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

    @EventHandler
    void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (getWandReplaceOrNull(p) == null) return;

        List<ArmorStand> hoveredStands = Raycasting.autoSelect(p);

        if (hoveredStands.isEmpty()) return;

        e.setCancelled(true);

        HashSet<ArmorStand> selection = SelectionManager.getOrCreateList(p);

        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            boolean sound = false;
            for (ArmorStand s : hoveredStands) {
                if (selection.remove(s)) sound = true;
            }
            if (sound) p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 2f, 0.5f);
            if (selection.isEmpty()) Utils.sendActionbar(p, "");
        } else {
            boolean sound = selection.addAll(hoveredStands);
            if (sound) p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 2f, 0.5f);
        }
    }
}
