package me.lofro.game.games.tntTag.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.lofro.game.games.tntTag.TNTManager;
import me.lofro.game.global.utils.text.HexFormatter;
import org.bukkit.command.CommandSender;

@CommandAlias("tntGame")
@CommandPermission("admin.perm")
public class TNTCMD extends BaseCommand {

    private final TNTManager tntManager;

    public TNTCMD(TNTManager tntManager) {
        this.tntManager = tntManager;
    }

    @Subcommand("start")
    @CommandCompletion("time taggedLimit")
    public void startGame(CommandSender sender, int time, int taggedLimit) {
        if (!tntManager.isRunning()) {
            tntManager.runGame(time, taggedLimit);

            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl juego ha sido iniciado con éxito."));
        } else {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cEl juego ya está siendo ejecutado."));
        }
    }

    @Subcommand("stop")
    public void stopGame(CommandSender sender) {
        if (tntManager.isRunning()) {
            tntManager.stopGame();

            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl juego ha sido desactivado con éxito."));
        } else {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cEl juego no está siendo ejecutado."));
        }
    }

}
