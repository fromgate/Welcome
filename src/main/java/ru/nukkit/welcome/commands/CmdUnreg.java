package ru.nukkit.welcome.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import ru.nukkit.welcome.players.PlayerManager;
import ru.nukkit.welcome.util.Message;

@CmdDefine(command = "unregister", alias = "unreg", subCommands = {}, permission = "welcome.unregister", description = Message.CMD_UNREG_DESC)
public class CmdUnreg extends Cmd {

    @Override
    public boolean execute(CommandSender sender, Player player, String[] args) {
        if (player == null) return false;
        return PlayerManager.unregCommand(player, args.length > 0 ? args[0] : "");
    }

}
