package me.lofro.game.games.glass.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.lofro.game.SquidGame;
import me.lofro.game.games.glass.GlassGameManager;
import me.lofro.game.games.glass.enums.GlassGameState;
import me.lofro.game.global.utils.text.HexFormatter;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@CommandAlias("glassGame")
@CommandPermission("admin.perm")
public class GlassGameCMD extends BaseCommand {

    private final GlassGameManager glassGManager;

    public GlassGameCMD(GlassGameManager glassGManager) {
        this.glassGManager = glassGManager;
    }

    @Subcommand("start")
    @CommandCompletion("time maxDepth playerLimit")
    public void startGame(CommandSender sender, int time, int maxDepth, int playerLimit) {
        if (!glassGManager.isRunning()) {
            glassGManager.runGame(time, maxDepth, playerLimit);

            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl juego ha sido iniciado con éxito."));
        } else {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cEl juego ya está siendo ejecutado."));
        }
    }

    @Subcommand("stop")
    public void stopGame(CommandSender sender) {
        if (glassGManager.isRunning()) {
            glassGManager.stopGame();
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl juego ha sido desactivado con éxito."));
        } else {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cEl juego no está siendo ejecutado."));
        }
    }

    @Subcommand("preGame")
    public void preGame(CommandSender sender) {
        if (glassGManager.isRunning()) {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cEl juego ya está siendo ejecutado."));
        } else {
            glassGManager.preGame();
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl juego ha sido preparado con éxito."));
        }
    }

    @Subcommand("test")
    public void test(CommandSender sender, Player player, int maxDepth) {
        var block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        var startTime = System.nanoTime();
        glassGManager.recursiveBreak(block, new ArrayList<>(), true, 0, maxDepth);
        sender.sendMessage(String.valueOf(System.nanoTime() - startTime));
    }

    @Subcommand("stopPreGame")
    public void stopPreGame(CommandSender sender) {
        if (glassGManager.getGlassGameState() == GlassGameState.PRE_START) {
            glassGManager.stopPreGame();
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bLa fase de Pre-Game ha sido desactivada con éxito."));
        } else {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cEl juego no está siendo ejecutado en Pre-Game."));
        }
    }

    @Subcommand("setArea")
    @CommandCompletion("@location @location")
    public void setArea(CommandSender sender, Location areaLower, Location areaUpper) {
        if (glassGManager.isRunning()) {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cNo puedes modificar el cubo mientras el juego está siendo ejecutado."));
        } else {
            var glassGameData = glassGManager.glassGameData();

            glassGameData.setAreaLower(areaLower);
            glassGameData.setAreaUpper(areaUpper);
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl cubo de juego ha sido actualizado correctamente."));
        }
    }

}
