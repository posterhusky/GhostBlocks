package net.vanolex.ghostBlocks;

import com.avaje.ebean.validation.NotNull;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang3.tuple.Pair;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class Utils {
    @NotNull
    public static ItemStack modifyItemTags(ItemStack item, Consumer<NBTTagCompound> fun) {
        net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(item);

        if (nms.getTag() == null) {
            nms.setTag(new NBTTagCompound());
        }
        fun.accept(nms.getTag());

        return CraftItemStack.asBukkitCopy(nms);
    }

    @Nullable
    public static Pair<Integer, Integer> readReplaceMaterial(ItemStack item) {
        if (item == null) return null;

        net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nms.getTag();
        if (tag == null) return null;
        if (!tag.hasKeyOfType("GReplaceMat", 3)) return null;
        if (!tag.hasKeyOfType("GReplaceData", 3)) return null;
        return Pair.of(tag.getInt("GReplaceMat"), tag.getInt("GReplaceData"));
    }

    @Nullable
    public static Pair<Integer, Integer> parseMaterial(String string) { // <material id or -1, data or -1>
        if (string == null) return null;

        String[] args = string.split(":");
        if (args.length < 1) {
            return null;
        } // idx 2+ gets unused

        int material;
        try {
            material = Integer.parseInt(args[0]);
            if (Material.getMaterial(material) == null) return null; // index out of range
        } catch (NumberFormatException e) {
            if (args[0].equalsIgnoreCase("~")) {
                material = -1;
            } else {
                Material tmp = Material.getMaterial(args[0].toUpperCase());
                if (tmp == null) return null;
                material = tmp.getId();
            }
        }

        int data;
        if (args.length > 1) {
            try {
                data = Integer.parseInt(args[1]) & 0xF;
            } catch (NumberFormatException e) {
                if (args[1].equalsIgnoreCase("~")) data = -1;
                else return null;
            }
        } else {
            data = 0;
        }

        return Pair.of(material, data);
    }

    @Nullable
    public static String getItemTag(ItemStack item, String key) {
        if (item == null) return null;

        net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(item);
        if (!nms.hasTag()) return null;

        NBTTagCompound tag = nms.getTag();
        if (!tag.hasKey(key)) return null;

        return tag.getString(key);
    }

    public static void modifyNBT(org.bukkit.entity.Entity bukkitEntity, Consumer<NBTTagCompound> fun) {
        Entity nms = ((CraftEntity) bukkitEntity).getHandle();

        NBTTagCompound tag = new NBTTagCompound();
        nms.c(tag);
        fun.accept(tag);
        nms.f(tag);
    }

    public static void setCombinedId(org.bukkit.entity.Entity bukkitEntity, int combinedId) {
        if (bukkitEntity == null) return;
        bukkitEntity.setCustomName("$GHOST:" + Integer.toHexString(combinedId & 0xFFFF));
    }

    public static int getCombinedId(int matId, byte data) {
        return (matId &0xFFF) | (data << 12);
    }

    public static void sendColoredDustParticle(Player p, Location loc, Color color) {
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldParticles(
                EnumParticle.REDSTONE,
                true,
                (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(),
                color.getRed()/256f + 0.003f,
                color.getGreen()/255f,
                color.getBlue()/255f,
                1f,
                0
        ));
    }

    @NotNull
    public static Pair<Double, Double> calcSlab(double iMin, double iMax, double iStart, double di) { // pair<Tenter, Texit>
        if (di == 0.0) {
            return iStart > iMin && iStart < iMax ?
                    Pair.of(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY) : Pair.of(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
        }
        double t1 = (iMin-iStart)/di;
        double t2 = (iMax-iStart)/di;

        return t1 < t2 ? Pair.of(t1, t2) : Pair.of(t2, t1);
    }

    public static void sendActionbar(Player p, String msg) {
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(new ChatComponentText(msg), (byte) 2));
    }

    public static void spawnGhostBlock(Block b) {
        Location loc = b.getLocation().clone().add(0.5, 0.0, 0.5);

        ArmorStand stand = (ArmorStand) b.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        modifyNBT(stand, tag -> {
            tag.setBoolean("Invulnerable", true);
            tag.setBoolean("NoGravity", true);
            tag.setBoolean("Invisible", true);
            tag.setBoolean("Marker", true);
            tag.setBoolean("OnGround", true);
            tag.setBoolean("Small", true);
        });

        setCombinedId(stand, getCombinedId(b.getTypeId(), b.getData()));
    }

    @Nullable
    public static Integer getCombinedId(String name) {
        if (name == null) return null;
        String[] payload = name.split(":");
        if (payload.length < 2) return null;
        if (!payload[0].equals("$GHOST")) return null;

        try {
            return Integer.parseInt(payload[1], 16);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
