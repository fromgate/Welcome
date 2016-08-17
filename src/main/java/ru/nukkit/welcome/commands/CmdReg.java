package ru.nukkit.welcome.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import ru.nukkit.welcome.players.PlayerManager;
import ru.nukkit.welcome.util.Message;

@CmdDefine(command = "register", alias = "reg", subCommands = {}, permission = "welcome.register", description = Message.CMD_REG_DESC)
public class CmdReg extends Cmd {
    @Override
    public boolean execute(CommandSender sender, Player player, String[] args) {
        if (player == null) return false;
        return PlayerManager.regCommand(player, args.length > 0 ? args[0] : "", args.length > 1 ? args[1] : "");
    }
}
