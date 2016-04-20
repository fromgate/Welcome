package ru.nukkit.welcome.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import ru.nukkit.welcome.Welcome;
import ru.nukkit.welcome.password.PasswordManager;
import ru.nukkit.welcome.util.Message;

@CmdDefine(command = "welcome", alias = "wel", subCommands = "remove", permission = "welcome.remove" , description = Message.CMD_RMV_DESC, allowConsole = true)
public class CmdWelRemove extends Cmd {
    @Override
    public boolean execute(CommandSender sender, Player player, String[] args) {
        if (args.length<2) return Message.RMV_NEED_PLAYER.print(sender,'c');
        if (!PasswordManager.hasPassword(args[1])||!PasswordManager.removePassword(args[1])) return Message.RMV_FAIL.print(sender,'c',args[1]);
        PasswordManager.removeAutologin(args[1]);
        Player target = Welcome.getPlugin().getServer().getPlayerExact(args[1]);
        if (target != null) target.kick(Message.UNREG_OK.getText('6'),false);

        return Message.RMV_OK.print(sender,args[1]);
    }
}
