package net.vanolex.ghostBlocks.selection;

import com.avaje.ebean.validation.NotNull;
import net.vanolex.ghostBlocks.Utils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import java.util.ArrayList;
import java.util.List;

public class Raycasting {

    @NotNull
    public static List<ArmorStand> raycast(Player p) {
        Location eyeLoc = p.getEyeLocation(); // eye level
        Vector dir = p.getLocation().getDirection().normalize();

        ArmorStand hitStand = null;
        double closestDist = 10.0;
        for (ArmorStand s : p.getWorld().getEntitiesByClass(ArmorStand.class)) {
            Location l = s.getLocation();
            if (s.getCustomName() == null || !s.getCustomName().startsWith("$GHOST:")) continue;
            if (l.getX() < eyeLoc.getX()-11 || l.getX() > eyeLoc.getX()+11) continue;
            if (l.getY() < eyeLoc.getY()-11 || l.getY() > eyeLoc.getY()+11) continue;
            if (l.getZ() < eyeLoc.getZ()-11 || l.getZ() > eyeLoc.getZ()+11) continue;

            Pair<Double, Double> xT = Utils.calcSlab(l.getX()-0.5, l.getX()+0.5, eyeLoc.getX(), dir.getX());
            if (xT.getLeft() > closestDist) continue;
            if (xT.getRight() < 0) continue;

            Pair<Double, Double> yT = Utils.calcSlab(l.getY(), l.getY()+1, eyeLoc.getY(), dir.getY());
            if (yT.getLeft() > closestDist) continue;
            if (yT.getRight() < 0) continue;

            Pair<Double, Double> zT = Utils.calcSlab(l.getZ()-0.5, l.getZ()+0.5, eyeLoc.getZ(), dir.getZ());
            if (zT.getLeft() > closestDist) continue;
            if (zT.getRight() < 0) continue;

            double Tenter = Math.max(Math.max(xT.getLeft(), yT.getLeft()), zT.getLeft());
            double Texit = Math.min(Math.min(xT.getRight(), yT.getRight()), zT.getRight());
            if (Tenter > Texit) continue; // no need for additional checks because each pair was checked individually

            hitStand = s;
            closestDist = Tenter;
        }
        return hitStand == null ? List.of() : List.of(hitStand);
    }

    @NotNull
    public static List<ArmorStand> multiSelect(Player p) {
        Location loc = p.getEyeLocation().clone().add(p.getLocation().getDirection().clone().multiply(5));
        ArrayList<ArmorStand> hitCandidates = new ArrayList<>();
        for (ArmorStand s : p.getWorld().getEntitiesByClass(ArmorStand.class)) {
            Location l = s.getLocation();
            if (l.getX() < loc.getX()-5 || l.getX() > loc.getX()+5) continue;
            if (l.getY() < loc.getY()-5 || l.getY() > loc.getY()+6) continue;
            if (l.getZ() < loc.getZ()-5 || l.getZ() > loc.getZ()+5) continue;
            if (s.getCustomName() == null || !s.getCustomName().startsWith("$GHOST:")) continue;
            hitCandidates.add(s);
        }
        return hitCandidates;
    }

    @NotNull
    public static List<ArmorStand> autoSelect(Player p) {
        return p.isSneaking() ? multiSelect(p) : raycast(p);
    }

}
