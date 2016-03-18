package ru.nukkit.welcome.commands;


import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import ru.nukkit.welcome.password.PasswordProvider;
import ru.nukkit.welcome.password.PasswordValidator;
import ru.nukkit.welcome.util.Message;


@CmdDefine(command = "welcome", alias = "wel", subCommands = "create", permission = "welcome.create" , description = Message.CMD_CRT_DESC, allowConsole = true)
public class CmdWelCreate extends Cmd{
    public boolean execute(CommandSender sender, Player player, String[] args) {
        if (args.length<3) return Message.CRT_NEED_PLAYER.print(sender);
        if (PasswordProvider.hasPassword(args[1])) return Message.CRT_ALREADY_REGISTERED.print(sender,args[1]);
        if (!PasswordValidator.validatePassword(args[2])) {
            Message.CRT_PWD_INVALID.print(sender);
            sender.sendMessage(PasswordValidator.getInfo());
            return true;
        }
        return  (PasswordProvider.setPassword(args[1],args[2]) ? Message.CRT_OK : Message.CRT_FAIL ).print(sender,args[1]);
    }
}
