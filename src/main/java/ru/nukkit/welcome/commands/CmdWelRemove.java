package ru.nukkit.welcome.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import ru.nukkit.welcome.Welcome;
import ru.nukkit.welcome.password.PasswordManager;
import ru.nukkit.welcome.util.Message;

@CmdDefine(command = "welcome", alias = "wel", subCommands = "remove", permission = "welcome.remove", description = Message.CMD_RMV_DESC, allowConsole = true)
public class CmdWelRemove extends Cmd {
    @Override
    public boolean execute(CommandSender sender, Player player, String[] args) {
        if (args.length < 2) return Message.RMV_NEED_PLAYER.print(sender, 'c');

        PasswordManager.removePassword(args[1]).whenComplete((removeOk, e) ->{
            if (e == null && removeOk) {
                Player target = Welcome.getPlugin().getServer().getPlayerExact(args[1]);
                if (target != null) target.kick(Message.UNREG_OK.getText('6'), false);
                Message.RMV_OK.print(sender, args[1]);
            } else {
                Message.RMV_FAIL.print(sender, 'c', args[1]);
            }
        });
        return true;
    }
}
