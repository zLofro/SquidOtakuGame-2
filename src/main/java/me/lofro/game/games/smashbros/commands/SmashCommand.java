package me.lofro.game.games.smashbros.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.lofro.game.games.smashbros.SmashBrosManager;
import me.lofro.game.global.utils.text.HexFormatter;
import org.bukkit.command.CommandSender;

@CommandAlias("smash")
@CommandPermission("admin.perm")
public class SmashCommand extends BaseCommand {

    private final SmashBrosManager smashManager;

    public SmashCommand(SmashBrosManager smashManager) {
        this.smashManager = smashManager;
    }

    @Subcommand("start")
    @CommandCompletion("time safeLimit")
    public void startGame(CommandSender sender, int deathLimit) {
        if (!smashManager.isRunning()) {
            smashManager.start(deathLimit);

            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl juego ha sido iniciado con éxito."));
        } else {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cEl juego ya está siendo ejecutado."));
        }
    }

    @Subcommand("stop")
    public void stopGame(CommandSender sender) {
        if (smashManager.isRunning()) {
            smashManager.stop();

            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl juego ha sido desactivado con éxito."));
        } else {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cEl juego no está siendo ejecutado."));
        }
    }

}

