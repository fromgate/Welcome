package ru.nukkit.welcome.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import ru.nukkit.welcome.players.PlayerManager;
import ru.nukkit.welcome.util.Message;

@CmdDefine(command = "login", alias = "lgn,l",subCommands = {} , permission = "welcome.login" , description = Message.CMD_LGN_DESC)
public class CmdLgn extends Cmd {
    @Override
    public boolean execute(CommandSender sender, Player player, String[] args) {
        Message.debugMessage("command: login player: "+(player==null ? "null" : player.getName()));
        if (player == null) return false;
        return PlayerManager.loginCommand(player, args.length>0 ? args[0] : "");
    }
}
