package net.vanolex.ghostBlocks.commands;

import net.vanolex.ghostBlocks.Utils;
import net.vanolex.ghostBlocks.selection.SelectionManager;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;

public class GhostModifyCmd  implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can execute this command");
            return true;
        }
        Player p = (Player) sender;

        String action = args.length > 0 ? args[0] : "help";

        if (
            action.equalsIgnoreCase("deselect") ||
            action.equalsIgnoreCase("desel") ||
            action.equalsIgnoreCase("ds")
        ) {
            HashSet<ArmorStand> lst = SelectionManager.getOrCreateList(p);
            if (lst.isEmpty()) {
                p.sendMessage("§eNothing changed, your selection was already empty.");
                p.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 2f, 1f);
                return true;
            }
            lst.clear();
            p.sendMessage("§aYour selection was cleared.");
            p.playSound(p.getLocation(), Sound.FIZZ, 2f, 0.8f);
            return true;
        }

        if (
                action.equalsIgnoreCase("deltateleporttiles") ||
                action.equalsIgnoreCase("dtpt") ||
                action.equalsIgnoreCase("tp")
        ) {
            HashSet<ArmorStand> lst = SelectionManager.getOrCreateList(p);
            if (lst.isEmpty()) {
                p.sendMessage("§eYour selection is empty.");
                p.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 2f, 1f);
                return true;
            }
            if (args.length < 4) {
                p.sendMessage("§cNot enough args.");
                p.playSound(p.getLocation(), Sound.NOTE_BASS, 1f, 1f);
                return true;
            }
            try {
                int dx = Integer.parseInt(args[1]);
                int dy = Integer.parseInt(args[2]);
                int dz = Integer.parseInt(args[3]);

                Vector vec = new Vector(dx/32.0, dy/32.0, dz/32.0);

                for (ArmorStand s : lst) {
                    s.teleport(s.getLocation().clone().add(vec));
                }
                p.sendMessage("§aYour selection was teleported.");
                p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1.7f);
                return true;
            } catch (NumberFormatException e) {
                p.sendMessage("§cNumberFormatException: Co-ords must be valid signed integers.");
                p.playSound(p.getLocation(), Sound.NOTE_BASS, 1f, 1f);
                return true;
            }
        }

        if (
                action.equalsIgnoreCase("changeblock") ||
                action.equalsIgnoreCase("cb")
        ) {
            HashSet<ArmorStand> lst = SelectionManager.getOrCreateList(p);
            if (lst.isEmpty()) {
                p.sendMessage("§eYour selection is empty.");
                p.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 2f, 1f);
                return true;
            }
            if (args.length < 2) {
                p.sendMessage("§cNot enough args.");
                p.playSound(p.getLocation(), Sound.NOTE_BASS, 1f, 1f);
                return true;
            }
            Pair<Integer, Integer> materialData = Utils.parseMaterial(args[1]);
            if (materialData == null) {
                sender.sendMessage("§cCouldn't parse the material. Try number IDs.");
                p.playSound(p.getLocation(), Sound.NOTE_BASS, 1f, 1f);
                return true;
            }

            for (ArmorStand s : lst) {
                Integer combinedId = Utils.getCombinedId(s.getCustomName());
                if (combinedId == null) continue;

                if (materialData.getLeft() != -1) {
                    combinedId = (combinedId & 0xF000) | (materialData.getLeft() & 0x0FFF);
                }

                if (materialData.getRight() != -1) {
                    combinedId = (combinedId & 0x0FFF) | ((materialData.getRight() & 0xF) << 12);
                }

                Utils.setCombinedId(s, combinedId);
            }

            p.sendMessage("§aYour selection's blocks were changed.");
            p.playSound(p.getLocation(), Sound.DIG_WOOD, 2f, 1.7f);
            p.playSound(p.getLocation(), Sound.GHAST_FIREBALL, 0.8f, 1f);
            return true;
        }

        if (
                action.equalsIgnoreCase("destroy") ||
                action.equalsIgnoreCase("rem") ||
                action.equalsIgnoreCase("rm")
        ) {
            HashSet<ArmorStand> lst = SelectionManager.getOrCreateList(p);
            if (lst.isEmpty()) {
                p.sendMessage("§eNothing changed, your selection was already empty.");
                p.playSound(p.getLocation(), Sound.NOTE_SNARE_DRUM, 2f, 1f);
                return true;
            }
            for (ArmorStand s : lst) {
                s.remove();
            }
            lst.clear();
            p.sendMessage("§aYour selection was destroyed.");
            p.playSound(p.getLocation(), Sound.BLAZE_DEATH, 1f, 1.2f);
            return true;
        }

        // else display help
        action = args.length > 1 ? args[1] : "help";

        if (
                action.equalsIgnoreCase("deselect") ||
                action.equalsIgnoreCase("desel") ||
                action.equalsIgnoreCase("ds")
        ) {
            p.sendMessage("§9Help §7- deselect (desel, ds)");
            p.sendMessage("§7Clears your selection (i.e. deselects all blocks).");
            p.sendMessage("");
            p.sendMessage("§9Usage: §7/ghostmodify deselect");
            p.playSound(p.getLocation(), Sound.CLICK, 2f, 1.2f);
            return true;
        }

        if (
                action.equalsIgnoreCase("deltateleporttiles") ||
                action.equalsIgnoreCase("dtpt") ||
                action.equalsIgnoreCase("tp")
        ) {
            p.sendMessage("§9Help §7- deltateleporttiles (dtpt, tp)");
            p.sendMessage("§7Teleports all selected armor stands by the set amount of tiles relative to their current position.");
            p.sendMessage("§71 tile represents the smallest unit transmittable through packets, 32 tiles = 1 block.");
            p.sendMessage("");
            p.sendMessage("§9Usage: §7/ghostmodify deltateleporttiles <dx> <dy> <dz>");
            p.sendMessage("§9dx, dy, dz §7- Signed integers representing the new position offset in tiles.");
            p.playSound(p.getLocation(), Sound.CLICK, 2f, 1.2f);
            return true;
        }

        if (
                action.equalsIgnoreCase("changeblock") ||
                action.equalsIgnoreCase("cb")
        ) {
            p.sendMessage("§9Help §7- changeblock (cb)");
            p.sendMessage("§7Changes the block data of all selected ghost blocks.");
            p.sendMessage("§7To keep a value from changing, use ~ instead of id and/or data:");
            p.sendMessage("§b53:~ §7places oak stairs while keeping rotations.");
            p.sendMessage("§b~:0 §7sets the data to 0 while not changing the id.");
            p.sendMessage("");
            p.sendMessage("§9Usage: §7/ghostmodify changeblock <material>");
            p.sendMessage("§9material §7- Material in the form of id:data. Use ~ to keep a value the same. Data defaults to 0 if absent.");
            p.playSound(p.getLocation(), Sound.CLICK, 2f, 1.2f);
            return true;
        }

        if (
                action.equalsIgnoreCase("destroy") ||
                action.equalsIgnoreCase("rem") ||
                action.equalsIgnoreCase("rm")
        ) {
            p.sendMessage("§9Help §7- destroy (rem, rm)");
            p.sendMessage("§7Premanently destroys all blocks in your selection.");
            p.sendMessage("");
            p.sendMessage("§9Usage: §7/ghostmodify destroy");
            p.playSound(p.getLocation(), Sound.CLICK, 2f, 1.2f);
            return true;
        }

        p.sendMessage("§9Help §7- help (h, ?)");
        p.sendMessage("§7Displays description and usage of the selected command.");
        p.sendMessage("§7Commands: deselect, deltateleporttiles, changeblock, destroy");
        p.sendMessage("");
        p.sendMessage("§9Usage: §7/ghostmodify help [command]");
        p.sendMessage("§9command §7- The command you need help with.");
        p.playSound(p.getLocation(), Sound.CLICK, 2f, 1.2f);
        return true;
    }
}
