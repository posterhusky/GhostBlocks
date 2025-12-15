package net.vanolex.ghostBlocks.commands;

import net.vanolex.ghostBlocks.Utils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GhostWandCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can execute this command");
            return true;
        }
        Player p = (Player) sender;

        ItemStack item = new ItemStack(Material.FEATHER);

        String arg = args.length >= 1 ? args[0] : null;
        Pair<Integer, Integer> materialData;
        if (arg == null) {
            materialData = Pair.of(0, 0);
        } else {
            materialData = Utils.parseMaterial(arg);
        }
        if (materialData == null) {
            sender.sendMessage("§cCouldn't parse the material. Try number IDs.");
            p.playSound(p.getLocation(), Sound.NOTE_BASS, 1f, 1f);
            return true;
        }

        String materialName = materialData.getLeft() == -1 ? "Keep" : Material.getMaterial(materialData.getLeft()).name();
        String dataName = materialData.getRight() == -1 ? "Keep" : materialData.getRight().toString();

        item = item.clone();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§r§eGhost Wand");
        meta.setLore(List.of(
                "§aRight-click §7to add to selection.",
                "§cPunch §7to remove from selection.",
                "§dSneak §7to multi-select.",
                "§3Break a block §7to turn it into a ghost.",
                "",
                "§7Replace Material: §9" + materialName,
                "§7Replace Data: §9" + dataName
        ));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.LUCK, 1, true);
        item.setItemMeta(meta);
        item.setAmount(1);


        p.getInventory().addItem(Utils.modifyItemTags(item, tag -> {
            tag.setInt("GReplaceMat", materialData.getLeft());
            tag.setInt("GReplaceData", materialData.getRight());
        }));
        p.sendMessage("§aA ghost wand was given to you.");
        p.sendMessage("§aRight-click §7to add to selection.");
        p.sendMessage("§cPunch §7to remove from selection.");
        p.sendMessage("§dSneak §7to multi-select.");
        p.sendMessage("§3Break a block §7to turn it into a ghost.");
        p.playSound(p.getLocation(), "random.successful_hit", 1f, 1f);
        p.playSound(p.getLocation(), "random.pop", 2f, 1.3f);
        return true;
    }
}
