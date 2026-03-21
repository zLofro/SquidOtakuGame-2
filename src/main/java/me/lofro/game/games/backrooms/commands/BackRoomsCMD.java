package me.lofro.game.games.backrooms.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.lofro.game.SquidGame;
import me.lofro.game.games.backrooms.BackRoomsManager;
import me.lofro.game.games.backrooms.enums.BackRoomsState;
import me.lofro.game.global.utils.text.HexFormatter;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

@CommandAlias("backrooms")
@CommandPermission("admin.perm")
public class BackRoomsCMD extends BaseCommand {

    private final BackRoomsManager bRManager;

    public BackRoomsCMD(BackRoomsManager bRManager) {
        this.bRManager = bRManager;
    }

    @Subcommand("start")
    @CommandCompletion("safeSeconds winnerLimit")
    public void runGame(CommandSender sender, int safeSeconds, int winnerLimit) {
        if (!bRManager.isRunning()) {
            bRManager.runGame(safeSeconds, winnerLimit);

            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl juego ha sido iniciado con éxito."));
        } else {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cEl juego ya está siendo ejecutado."));
        }
    }

    @Subcommand("stop")
    public void stop(CommandSender sender) {
        if (bRManager.isRunning()) {
            bRManager.stopGame();

            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl juego ha sido desactivado con éxito."));
        } else {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cEl juego no está siendo ejecutado."));
        }
    }

    @Subcommand("preGame")
    public void preGame(CommandSender sender) {
        if (!bRManager.isRunning()) {
            bRManager.preGame();

            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl juego ha sido preparado con éxito."));
        } else {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cEl juego ya está siendo ejecutado."));
        }
    }

    @Subcommand("stopPreGame")
    public void stopPreGame(CommandSender sender) {
        if (bRManager.getBackRoomsState() == BackRoomsState.PRE_START) {
            bRManager.stopPreGame();
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bLa fase de Pre-Game ha sido desactivada con éxito."));
        } else {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cEl juego no está siendo ejecutado en Pre-Game."));
        }
    }

    @Subcommand("setCube")
    @CommandCompletion("@location @location")
    public void setCube(CommandSender sender, Location cubeLower, Location cubeUpper) {
        if (!bRManager.isRunning()) {
            var backRoomsData = bRManager.backRoomsData();

            backRoomsData.setCubeLower(cubeLower);
            backRoomsData.setCubeUpper(cubeUpper);

            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl cubo de juego ha sido actualizado correctamente."));
        } else {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cNo puedes modificar el cubo mientras el juego está siendo ejecutado."));
        }
    }

    @Subcommand("setMiddleCube")
    @CommandCompletion("@location @location")
    public void setMiddleCube(CommandSender sender, Location cubeLower, Location cubeUpper) {
        if (!bRManager.isRunning()) {
            var backRoomsData = bRManager.backRoomsData();

            backRoomsData.setMiddleCubeLower(cubeLower);
            backRoomsData.setMiddleCubeUpper(cubeUpper);

            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl cubo de juego ha sido actualizado correctamente."));
        } else {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cNo puedes modificar el cubo mientras el juego está siendo ejecutado."));
        }
    }

}
