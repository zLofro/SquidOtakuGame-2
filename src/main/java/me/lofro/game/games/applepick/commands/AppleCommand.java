package me.lofro.game.games.applepick.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.lofro.game.games.applepick.ApplePickManager;
import me.lofro.game.global.utils.text.HexFormatter;
import org.bukkit.command.CommandSender;

@CommandAlias("appleGame")
@CommandPermission("admin.perm")
public class AppleCommand extends BaseCommand {

    private final ApplePickManager appleManager;

    public AppleCommand(ApplePickManager appleManager) {
        this.appleManager = appleManager;
    }

    @Subcommand("start")
    @CommandCompletion("time safeLimit")
    public void startGame(CommandSender sender, int time, int safeLimit) {
        if (!appleManager.isRunning()) {
            appleManager.runGame(time, safeLimit);

            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl juego ha sido iniciado con éxito."));
        } else {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cEl juego ya está siendo ejecutado."));
        }
    }

    @Subcommand("stop")
    public void stopGame(CommandSender sender) {
        if (appleManager.isRunning()) {
            appleManager.stopGame();

            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl juego ha sido desactivado con éxito."));
        } else {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cEl juego no está siendo ejecutado."));
        }
    }

}
