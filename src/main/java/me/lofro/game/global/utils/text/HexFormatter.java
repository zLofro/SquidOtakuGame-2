package me.lofro.game.global.utils.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentIteratorType;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.EnumSet;

public class HexFormatter {

    private static final Component name = hexFormat("&f&lSquid&d&lOtaku&f&lGame");
    private static final Component prefix = hexFormat(name + " &7>> &r");

    public static Component hexFormat(Component text) {
        return hexFormat(deserialize(text));
    }

    public static Component hexFormat(String text) {

        TextComponent.Builder builder = Component.text();

        TextColor currentColor = TextColor.fromHexString("#ffffff");
        EnumSet<TextDecoration> decorations = EnumSet.noneOf(TextDecoration.class);

        StringBuilder currentText = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {

            char c = text.charAt(i);

            if (c == '&' && i + 1 < text.length()) {

                append(builder, currentText, currentColor, decorations);
                currentText.setLength(0);

                char code = Character.toLowerCase(text.charAt(i + 1));

                TextColor legacy = getLegacyColor(code);
                if (legacy != null) {
                    currentColor = legacy;
                    decorations.clear();
                    i++;
                    continue;
                }

                TextDecoration deco = getDecoration(code);
                if (deco != null) {
                    decorations.add(deco);
                    i++;
                    continue;
                }
            }

            if (c == '#' && i + 6 < text.length()) {

                String hex = text.substring(i, i + 7);

                if (hex.matches("#[a-fA-F0-9]{6}")) {

                    append(builder, currentText, currentColor, decorations);
                    currentText.setLength(0);

                    currentColor = TextColor.fromHexString(hex);
                    decorations.clear();

                    i += 6;
                    continue;
                }
            }

            currentText.append(c);
        }

        append(builder, currentText, currentColor, decorations);

        return builder.build();
    }

    private static void append(TextComponent.Builder builder, StringBuilder text, TextColor color, EnumSet<TextDecoration> decorations) {

        if (text.isEmpty()) return;

        Component part = Component.text(text.toString()).color(color);

        for (TextDecoration deco : decorations) {
            part = part.decorate(deco);
        }

        builder.append(part);
    }

    public static String deserialize(Component text) {

        StringBuilder result = new StringBuilder();

        text.spliterator(ComponentIteratorType.DEPTH_FIRST).forEachRemaining(component -> {

            if (component instanceof TextComponent tc) {

                if (tc.color() != null) {
                    result.append(tc.color().asHexString());
                }

                result.append(tc.content());
            }
        });

        return result.toString();
    }

    public static Component hexFormatWithPrefix(String text) {
        return prefix.append(hexFormat(text));
    }

    private static TextColor getLegacyColor(char symbol) {

        return switch(symbol) {
            case '0' -> TextColor.fromHexString("#000000");
            case '1' -> TextColor.fromHexString("#0000AA");
            case '2' -> TextColor.fromHexString("#00AA00");
            case '3' -> TextColor.fromHexString("#00AAAA");
            case '4' -> TextColor.fromHexString("#AA0000");
            case '5' -> TextColor.fromHexString("#AA00AA");
            case '6' -> TextColor.fromHexString("#FFAA00");
            case '7' -> TextColor.fromHexString("#AAAAAA");
            case '8' -> TextColor.fromHexString("#555555");
            case '9' -> TextColor.fromHexString("#5555FF");
            case 'a' -> TextColor.fromHexString("#55FF55");
            case 'b' -> TextColor.fromHexString("#55FFFF");
            case 'c' -> TextColor.fromHexString("#FF5555");
            case 'd' -> TextColor.fromHexString("#FF55FF");
            case 'e' -> TextColor.fromHexString("#FFFF55");
            case 'f' -> TextColor.fromHexString("#FFFFFF");
            default -> null;
        };
    }

    private static TextDecoration getDecoration(char symbol) {

        return switch (symbol) {
            case 'l' -> TextDecoration.BOLD;
            case 'k' -> TextDecoration.OBFUSCATED;
            case 'm' -> TextDecoration.STRIKETHROUGH;
            case 'n' -> TextDecoration.UNDERLINED;
            case 'o' -> TextDecoration.ITALIC;
            default -> null;
        };
    }
}
