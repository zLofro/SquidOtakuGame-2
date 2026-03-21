package me.lofro.game.games.greenlight.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import me.lofro.game.games.greenlight.GreenLightManager;
import me.lofro.game.games.greenlight.enums.LightState;
import me.lofro.game.global.utils.text.HexFormatter;

@CommandAlias("greenLight")
@CommandPermission("admin.perm")
public class GreenLightCMD extends BaseCommand {

    private final GreenLightManager gLightManager;

    public GreenLightCMD(GreenLightManager gLightManager) {
        this.gLightManager = gLightManager;
    }

    @Subcommand("run")
    @CommandCompletion("seconds deathLimit greenLowestTimeBound greenHighestTimeBound redLowestTimeBound redHighestTimeBound")
    public void runGame(CommandSender sender, int seconds, int deathLimit, int greenLowestTimeBound, int greenHighestTimeBound,
            int redLowestTimeBound, int redHighestTimeBound) {
        if (gLightManager.isRunning()) {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cEl juego ya está siendo ejecutado."));
        } else {
            gLightManager.setGreenLowestTimeBound(greenLowestTimeBound);
            gLightManager.setGreenHighestTimeBound(greenHighestTimeBound);

            gLightManager.setRedLowestTimeBound(redLowestTimeBound);
            gLightManager.setRedHighestTimeBound(redHighestTimeBound);

            gLightManager.runGame(seconds, deathLimit);
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl juego ha sido iniciado con éxito."));
        }
    }

    @Subcommand("stop")
    public void stopGame(CommandSender sender) {
        if (gLightManager.isRunning()) {
            gLightManager.stopGame();
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl juego ha sido desactivado con éxito."));
        } else {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cEl juego no está siendo ejecutado."));
        }
    }

    @Subcommand("shoot")
    @CommandCompletion("@players")
    public void shoot(CommandSender sender, @Flags("other") Player player) {
        if (!gLightManager.cannonLocations().isEmpty())
            gLightManager.shoot(player);
    }

    @Subcommand("preGame")
    public void preGame(CommandSender sender) {
        if (gLightManager.isRunning()) {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cEl juego ya está siendo ejecutado."));
        } else {
            gLightManager.preStart();
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl juego ha sido preparado con éxito."));
        }
    }

    @Subcommand("stopPreGame")
    public void stopPreGame(CommandSender sender) {
        if (gLightManager.getLightState() == LightState.PRE_START) {
            gLightManager.stopPreStart();
            sender.sendMessage(
                    HexFormatter.hexFormatWithPrefix("&bLa fase de Pre-Game ha sido desactivada con éxito."));
        } else {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cEl juego no está siendo ejecutado en Pre-Game."));
        }
    }

    @Subcommand("setCube")
    @CommandCompletion("@location @location")
    public void setCube(CommandSender sender, Location cubeLower, Location cubeUpper) {
        if (gLightManager.isRunning()) {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cNo puedes modificar el cubo mientras el juego está siendo ejecutado."));
        } else {
            var gLightData = gLightManager.getGManager().gameData().greenLightData();

            gLightData.setCubeLower(cubeLower);
            gLightData.setCubeUpper(cubeUpper);
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl cubo de juego ha sido actualizado correctamente."));
        }
    }

    @Subcommand("setSenseiLocation")
    @CommandCompletion("@location")
    public void setSenseiLocation(CommandSender sender, Location senseiLocation) {
        if (!gLightManager.isRunning()) {
            gLightManager.getGManager().gameData().greenLightData().setSenseiLocation(senseiLocation);

            gLightManager.removeSensei();
            gLightManager.spawnSensei();

            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bLa localización del sensei ha sido modificada correctamente."));
        } else {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cNo puedes modificar al sensei mientras el juego está siendo ejecutado."));
        }
    }

    @Subcommand("spawnSensei")
    public void spawnSensei(CommandSender sender) {
        if (!gLightManager.isRunning()) {

            gLightManager.removeSensei();
            gLightManager.spawnSensei();

            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl sensei ha sido spawneado correctamente."));
        } else {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cNo puedes modificar al sensei mientras el juego está siendo ejecutado."));
        }
    }

    @Subcommand("addCannon")
    @CommandCompletion("@location")
    public void addCannon(CommandSender sender, Location cannon) {
        if (gLightManager.isRunning()) {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cNo puedes modificar los cañones mientras el juego está siendo ejecutado."));
        } else {
            var gLightData = gLightManager.getGManager().gameData().greenLightData();

            gLightData.getCannonLocations().add(cannon);
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl cañón ha sido añadido correctamente."));
        }
    }

    @Subcommand("removeCannon")
    @CommandCompletion("@location")
    public void removeCannon(CommandSender sender, Location cannon) {
        if (gLightManager.isRunning()) {
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&cNo puedes modificar los cañones mientras el juego está siendo ejecutado."));
        } else {
            var gLightData = gLightManager.getGManager().gameData().greenLightData();

            gLightData.getCannonLocations().remove(cannon);
            sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl cañón ha sido eliminado correctamente."));
        }
    }

    @Subcommand("rotate")
    public void rotate(CommandSender sender, Integer degrees, Integer overGivenTicks, Boolean clockwise) {
        gLightManager.rotateProgressively(degrees, clockwise, overGivenTicks);
    }

}
