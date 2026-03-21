package me.lofro.game.games.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.common.collect.ImmutableMap;
import me.lofro.game.SquidGame;
import me.lofro.game.games.GameManager;
import me.lofro.game.global.enums.Day;
import me.lofro.game.global.enums.PvPState;
import me.lofro.game.global.item.CustomItems;
import me.lofro.game.global.utils.text.HexFormatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("gManager")
@CommandPermission("admin.perm")
public class GameManagerCMD extends BaseCommand {

    private final GameManager gManager;

    public GameManagerCMD(GameManager gManager) {
        this.gManager = gManager;
    }

    @Subcommand("pvp")
    public void setPvP(CommandSender sender, PvPState pvPState) {
        var gData = gManager.gameData();

        if (!pvPState.equals(gData.getPvPState())) {
            gData.setPvPState(pvPState);
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl pvp ha sido actualizado a &3" + pvPState.name() + "&b."));
        } else {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cEl pvp ya es " + pvPState.name() + "."));
        }
    }

    @Subcommand("day")
    public void setDay(CommandSender sender, Day day) {
        var gData = gManager.gameData();

        if (!day.equals(gData.getDay())) {
            gData.setDay(day);
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl día ha sido actualizado a &3" + day.name() + "&b."));
        } else {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cEl día ya es " + day.name() + "."));
        }
    }

    @Subcommand("give")
    @CommandCompletion("@players customItemGroup customItem")
    public void giveItem(CommandSender sender, @Flags("other") Player player, String group, String item) {
        ItemStack itemStack;
        if (gManager.getSquidInstance().getPManager().isGuard(player)) {
            {
                if (!CustomItems.groups.containsKey(group)) {
                    sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cEl grupo introducido no es válido."));
                    return;
                }

                ImmutableMap<String, ItemStack> customItems = CustomItems.groups.get(group);

                if (customItems == null || !customItems.containsKey(item)) return;

                itemStack = customItems.get(item);
            }
            if (itemStack != null) {
                player.getInventory().addItem(itemStack);
                sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl jugador &3" + player.getName() + "&bha recibido el item &3 " + item + "&b con éxito."));
            } else {
                sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cEl item introducida no es válida."));
            }
        } else {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cEl jugador introducido no es un guardia."));
        }
    }

}
