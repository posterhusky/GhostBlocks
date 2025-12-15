package net.vanolex.ghostBlocks.selection;

import net.vanolex.ghostBlocks.Utils;
import net.vanolex.ghostBlocks.listeners.WandInteractListener;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.HashSet;
import java.util.List;

public class ParticleManager {

    final static Color BLUE = new Color(0x55, 0x55, 0xFF);
    final static Color GREEN = new Color(0x55, 0xFF, 0x55);
    final static Color YELLOW = new Color(0xFF, 0xFF, 0x55);

    public static void addParticles(Player p, Location loc, HashSet<Location> set) {
        if (p.getLocation().distanceSquared(loc) > 900) return;

        if (p.getLocation().distanceSquared(loc) > 144) { // low detail highlight
            for (double dx = -0.5; dx <= 0.5; dx += 1.0) for (double dy = 0.0; dy <= 1.0; dy += 1.0) for (double dz = -0.5; dz <= 0.5; dz += 1.0) {
                if (dx == 0.0 && dy == 0.5 && dz == 0.0) continue;
                set.add(loc.clone().add(dx, dy, dz));
            }
            return;
        }

        // high detail highlight
        for (double dx = -0.5; dx <= 0.5; dx += 0.5) for (double dy = 0.0; dy <= 1.0; dy += 0.5) for (double dz = -0.5; dz <= 0.5; dz += 0.5) {
            if (dx == 0.0 && dy == 0.5 && dz == 0.0) continue;
            set.add(loc.clone().add(dx, dy, dz));
        }
    }

    public static void highlightBlocks(Player p) {
        HashSet<ArmorStand> selectedStands = SelectionManager.getOrCreateList(p);

        if (WandInteractListener.getWandReplaceOrNull(p) == null) { // check if player is holding a wand
            HashSet<Location> set = new HashSet<>();
            for (ArmorStand s : selectedStands) {
                addParticles(p, s.getLocation(), set);
            }
            for (Location l : set) {
                Utils.sendColoredDustParticle(p, l, BLUE);
            }
            return;
        }

        List<ArmorStand> hoveredStands = Raycasting.autoSelect(p);

        HashSet<Location> greenSet = new HashSet<>();
        HashSet<Location> yellowSet = new HashSet<>();
        HashSet<Location> blueSet = new HashSet<>();

        for (ArmorStand s : hoveredStands) {
            if (selectedStands.contains(s)) addParticles(p, s.getLocation(), greenSet);
            else addParticles(p, s.getLocation(), yellowSet);
        }
        for (ArmorStand s : selectedStands) {
            if (!hoveredStands.contains(s)) addParticles(p, s.getLocation(), blueSet);
        }

        for (Location l : greenSet) {
            Utils.sendColoredDustParticle(p, l, GREEN);
        }
        for (Location l : yellowSet) {
            Utils.sendColoredDustParticle(p, l, YELLOW);
        }
        for (Location l : blueSet) {
            Utils.sendColoredDustParticle(p, l, BLUE);
        }
    }

}
