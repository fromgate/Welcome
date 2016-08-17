package ru.nukkit.welcome.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import ru.nukkit.welcome.players.PlayerManager;
import ru.nukkit.welcome.util.Message;

@CmdDefine(command = "logoff", subCommands = {}, permission = "welcome.login", description = Message.CMD_LGF_DESC)
public class CmdLogOff extends Cmd {

    @Override
    public boolean execute(CommandSender sender, Player player, String[] args) {
        PlayerManager.logOff(player);
        return true;
    }
}
