package me.lofro.game.global.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.lofro.game.SquidGame;
import me.lofro.game.global.utils.text.HexFormatter;
import me.lofro.game.global.utils.credits.Credits;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("customCredits")
@CommandPermission("admin.perm")
public class CreditsCMD extends BaseCommand {

    @Subcommand("show")
    public void showCredits(CommandSender sender, Player player) {
        Credits.showCredits(player);
        sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bLos créditos han sido mostrados para &3" + player.getName() + " &b."));
    }

}
