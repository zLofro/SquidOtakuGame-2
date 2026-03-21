package me.lofro.game.global.item;

import com.google.common.collect.ImmutableMap;
import me.lofro.game.global.utils.text.HexFormatter;
import me.lofro.game.global.utils.rapidinv.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class CustomItems {

    public interface ItemStackGetter {
        ItemStack get();
    }

    private static <T extends Enum<? extends ItemStackGetter> & ItemStackGetter> ImmutableMap<String, ImmutableMap<String, ItemStack>> convertIntoItemStacks() {
        HashMap<String, ImmutableMap<String, ItemStack>> tempGroups = new HashMap<>(0, 1);
        for (@SuppressWarnings("unchecked") Class<T> group : new Class[]{Weapons.class, Decoration.class}) {
            HashMap<String, ItemStack> tempDirectory = new HashMap<>(0, 1);

            T[] values = group.getEnumConstants();
            for (T value : values) {
                tempDirectory.put(value.name(), value.get());
            }

            tempGroups.put(group.getSimpleName(), ImmutableMap.copyOf(tempDirectory));
        }
        return ImmutableMap.copyOf(tempGroups);
    }
    public static final ImmutableMap<String, ImmutableMap<String, ItemStack>> groups = convertIntoItemStacks(
    );

    public enum Weapons implements ItemStackGetter {
        SHOTGUN(new ItemBuilder(Material.CROSSBOW)
                .name(HexFormatter.hexFormat("&cShotgun"))
                .setUnbreakable(true)
                .enchant(Enchantment.QUICK_CHARGE)
                .flags(ItemFlag.HIDE_ENCHANTS)
                .setCustomModelData(0)
                .build()),

        REVOLVER(new ItemBuilder(Material.CROSSBOW)
                .name(HexFormatter.hexFormat("&eRevolver"))
                .setUnbreakable(true)
                .enchant(Enchantment.QUICK_CHARGE, 2)
                .flags(ItemFlag.HIDE_ENCHANTS)
                .setCustomModelData(1)
                .build())
        ,BROKEN_BOTTLE(new ItemBuilder(Material.STONE_SWORD)
                .name(HexFormatter.hexFormat("&2Botella rota"))
                .setUnbreakable(true)
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .flags(ItemFlag.HIDE_UNBREAKABLE)
                .setCustomModelData(1)
                .build()
        ),KNIFE(new ItemBuilder(Material.STONE_SWORD)
                .name(HexFormatter.hexFormat("&cCuchillo"))
                .setUnbreakable(true)
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .flags(ItemFlag.HIDE_UNBREAKABLE)
                .setCustomModelData(2)
                .build()
        );

        private final ItemStack itemStack;
        public ItemStack get() {
            return itemStack;
        }
        Weapons(ItemStack itemStack) {
            this.itemStack = itemStack;
        }
    }

    public enum Decoration implements ItemStackGetter {

        KORO_SENSEI(new ItemBuilder(Material.BRICK)
                .name(HexFormatter.hexFormat("&6Koro Sensei"))
                .setCustomModelData(5)
                .build()
        ),
        GUARD_MASK(new ItemBuilder(Material.BRICK)
                .name(HexFormatter.hexFormat("&cCabeza de Guardia"))
                .setCustomModelData(6)
                .build()
        ),PLAYER_CHESTPLATE(new ItemBuilder(Material.GOLDEN_CHESTPLATE)
                .name(HexFormatter.hexFormat("&bCamisa"))
                .setUnbreakable(true)
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .flags(ItemFlag.HIDE_UNBREAKABLE)
                .addAttributeModifier(Attribute.ARMOR, new AttributeModifier(Attribute.ARMOR.getKey(), 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.CHEST.getGroup()))
                .build()
        ),PLAYER_LEGGINGS(new ItemBuilder(Material.GOLDEN_LEGGINGS)
                .name(HexFormatter.hexFormat("&bPantalón"))
                .setUnbreakable(true)
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .flags(ItemFlag.HIDE_UNBREAKABLE)
                .addAttributeModifier(Attribute.ARMOR, new AttributeModifier(Attribute.ARMOR.getKey(), 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.LEGS.getGroup()))
                .build()
        ),EMPTY_FOOD(new ItemBuilder(Material.BRICK)
                .setCustomModelData(7)
                .build()
        ),FOOD(new ItemBuilder(Material.BRICK)
                .setCustomModelData(8)
                .build()
        ),DEATH_NOTE(new ItemBuilder(Material.BOOK)
                .name(HexFormatter.hexFormat("&8Death Note"))
                .build()
        ),BOTTLE(new ItemBuilder(Material.GLASS_BOTTLE)
                .name(HexFormatter.hexFormat("&2Botella"))
                .build()
        ),TNT(new ItemBuilder(Material.TNT)
                .name(HexFormatter.hexFormat("&cTAG"))
                .build()
        );

        private final ItemStack itemStack;
        public ItemStack get() {
            return itemStack;
        }
        Decoration(ItemStack itemStack) {
            this.itemStack = itemStack;
        }
    }

}
