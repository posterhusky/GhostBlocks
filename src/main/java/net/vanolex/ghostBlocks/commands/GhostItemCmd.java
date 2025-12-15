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

public class GhostItemCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can execute this command");
            return true;
        }
        Player p = (Player) sender;

        ItemStack item = p.getInventory().getItemInHand();
        if (item == null || !item.getType().isBlock() || item.getType() == Material.AIR) {
            sender.sendMessage("§cYou need to be holding a placeable item in your hand");
            p.playSound(p.getLocation(), Sound.NOTE_BASS, 1f, 1f);
            return true;
        }

        String arg = args.length >= 1 ? args[0] : null;
        Pair<Integer, Integer> materialData;
        if (arg == null) {
            materialData = Pair.of(-1, -1);
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
        meta.setDisplayName("§r§eGhost Block");
        meta.setLore(List.of(
                "§bPlace it §7to spawn a ghost.",
                "",
                "§7Ghost Material: §5" + item.getType().name(),
                "§7Ghost Data: §5" + item.getDurability(),
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
        p.sendMessage("§aA ghost block was given to you.");
        p.sendMessage("§bPlace it §7to spawn a ghost.");
        p.playSound(p.getLocation(), "random.successful_hit", 1f, 1f);
        p.playSound(p.getLocation(), "random.pop", 2f, 1.3f);
        return true;
    }
}
