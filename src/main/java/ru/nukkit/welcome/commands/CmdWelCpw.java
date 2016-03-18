package ru.nukkit.welcome.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import ru.nukkit.welcome.password.PasswordProvider;
import ru.nukkit.welcome.password.PasswordValidator;
import ru.nukkit.welcome.players.PlayerManager;
import ru.nukkit.welcome.util.Message;

@CmdDefine(command = "welcome", alias = "wel", subCommands = "(?i)setpassword|setpass|changepassword|changepass|cpass|cpw", permission = "welcome.changepassword.other" , description = Message.CPWO_DESC, allowConsole = true)
public class CmdWelCpw extends Cmd {
    public boolean execute(CommandSender sender, Player player, String[] args) {
        if (args.length<3) return Message.CPWO_USAGE.print(player);
        if (!PasswordValidator.validatePassword(args[2])) {
            Message.CRT_PWD_INVALID.print(sender);
            sender.sendMessage(PasswordValidator.getInfo());
            return true;
        }
        if (!PasswordProvider.setPassword(args[1],args[2])) return Message.CPWO_FAIL.print(sender,args[1]);
        Player otherPlayer = Server.getInstance().getPlayerExact(args[2]);
        if (otherPlayer!=null&&PlayerManager.isPlayerLoggedIn(otherPlayer))
            (player == null ? Message.CPWO_OK_INFORM_CONSOLE : Message.CPWO_OK_INFORM).print(sender, sender.getName(), args[2]);
        return Message.CPWO_OK.print(sender,args[1]);
    }
}
