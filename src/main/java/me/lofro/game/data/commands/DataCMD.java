package me.lofro.game.data.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.lofro.game.SquidGame;
import me.lofro.game.data.DataManager;
import me.lofro.game.data.enums.SquidDataType;
import me.lofro.game.global.utils.text.HexFormatter;
import org.bukkit.command.CommandSender;

@CommandAlias("data")
@CommandPermission("admin.perm")
public class DataCMD extends BaseCommand {

    private final DataManager dManager;

    public DataCMD(DataManager dManager) {
        this.dManager = dManager;
    }

    @Subcommand("save")
    public void save(CommandSender sender, SquidDataType squidDataType) {
        dManager.save(squidDataType);
        sender.sendMessage(HexFormatter.hexFormatWithPrefix("&bEl data config de &3" + squidDataType.name() + " &bha sido guardado con éxito."));
    }
}
