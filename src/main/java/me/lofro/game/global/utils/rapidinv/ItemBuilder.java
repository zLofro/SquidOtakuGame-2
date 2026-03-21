package me.lofro.game.global.utils.rapidinv;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Simple {@link ItemStack} builder
 *
 * @author MrMickys
 * @author jcedeno
 *
 * @since v0.0.1
 */
public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this(new ItemStack(material));
    }

    public ItemBuilder(ItemStack item) {
        this.item = Objects.requireNonNull(item, "item");
        this.meta = item.getItemMeta();

        if (this.meta == null) {
            throw new IllegalArgumentException("The type " + item.getType() + " doesn't support item meta");
        }
    }

    public ItemBuilder type(Material material) {
        this.item.setType(material);
        return this;
    }

    public ItemBuilder data(int data) {
        return durability((short) data);
    }

    @SuppressWarnings("deprecation")
    public ItemBuilder durability(short durability) {
        this.item.setDurability(durability);
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment) {
        return enchant(enchantment, 1);
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        this.meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder removeEnchant(Enchantment enchantment) {
        this.meta.removeEnchant(enchantment);
        return this;
    }

    public ItemBuilder removeEnchants() {
        this.meta.getEnchants().keySet().forEach(this.meta::removeEnchant);
        return this;
    }

    public ItemBuilder meta(Consumer<ItemMeta> metaConsumer) {
        metaConsumer.accept(this.meta);
        return this;
    }

    public <T extends ItemMeta> ItemBuilder meta(Class<T> metaClass, Consumer<T> metaConsumer) {
        if (metaClass.isInstance(this.meta)) {
            metaConsumer.accept(metaClass.cast(this.meta));
        }
        return this;
    }

    public ItemBuilder name(Component name) {
        this.meta.displayName(name);
        return this;
    }

    public ItemBuilder lore(String lore) {
        return legacyLore(Collections.singletonList(lore));
    }

    public ItemBuilder lore(String... lore) {
        return legacyLore(Arrays.asList(lore));
    }

    public ItemBuilder legacyLore(List<String> lore) {
        this.meta.lore(fromLegacyStringToComponent(lore));
        return this;
    }

    public ItemBuilder lore(@Nullable List<Component> lore) {
        this.meta.lore(lore);
        return this;
    }

    /**
     * Function to convert list of Strings to List of {@link Component}(s).
     *
     * @param lore The list of Strings to convert.
     * @return A List of {@link Component}(s).
     */
    private static List<@NonNull Component> fromLegacyStringToComponent(final List<String> lore) {
        return lore.stream().map(ItemBuilder::fromLegacyStringToComponent).collect(Collectors.toList());
    }

    /**
     * Function to convert a String to a {@link Component}.
     *
     * @param lore The String to convert.
     * @return A {@link Component}.
     */
    private static Component fromLegacyStringToComponent(final String lore) {
        return Component.text(lore);
    }

    public ItemBuilder addLore(final String line) {
        var lMeta = this.meta;

        if (lMeta.hasLore()) {
            var lore = lMeta.lore();
            lore.add(fromLegacyStringToComponent(line));
            return lore(lore);
        }
        return lore(line);
    }

    public ItemBuilder addLore(String... lines) {
        return addLore(Arrays.asList(lines));
    }

    public ItemBuilder addLore(List<String> lines) {
        if (this.meta.hasLore()) {
            var lore = this.meta.lore();
            lore.addAll(fromLegacyStringToComponent(lines));
            return lore(lore);
        }
        return legacyLore(lines);
    }

    public ItemBuilder flags(ItemFlag... flags) {
        this.meta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder flags() {
        return flags(ItemFlag.values());
    }

    public ItemBuilder removeFlags(ItemFlag... flags) {
        this.meta.removeItemFlags(flags);
        return this;
    }

    public ItemBuilder setUnbreakable(Boolean bool) {
        this.meta.setUnbreakable(bool);
        return this;
    }

    public ItemBuilder setCustomModelData(int data) {
        this.meta.setCustomModelData(data);
        return this;
    }

    public ItemBuilder addAttributeModifier (Attribute attribute, AttributeModifier attributeModifier) {
        this.meta.addAttributeModifier(attribute, attributeModifier);
        this.item.setItemMeta(this.meta);
        return this;
    }

    public ItemBuilder removeFlags() {
        return removeFlags(ItemFlag.values());
    }

    public ItemBuilder armorColor(Color color) {
        return meta(LeatherArmorMeta.class, m -> m.setColor(color));
    }

    public ItemStack build() {
        this.item.setItemMeta(this.meta);
        return this.item;
    }
}
