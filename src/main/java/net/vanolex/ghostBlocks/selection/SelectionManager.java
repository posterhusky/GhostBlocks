package net.vanolex.ghostBlocks.selection;

import com.avaje.ebean.validation.NotNull;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.*;

public class SelectionManager {

    public static HashMap<Player, HashSet<ArmorStand>> selectionMap = new HashMap<>();

    @NotNull
    public static HashSet<ArmorStand> getOrCreateList(Player p) {
        if (selectionMap.containsKey(p)) {
            return selectionMap.get(p);
        }
        HashSet<ArmorStand> newList = new HashSet<>();
        selectionMap.put(p, newList);
        return newList;
    }

}
