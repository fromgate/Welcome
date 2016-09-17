package ru.nukkit.welcome.commands;


import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import ru.nukkit.welcome.password.PasswordManager;
import ru.nukkit.welcome.password.PasswordValidator;
import ru.nukkit.welcome.util.Message;


@CmdDefine(command = "welcome", alias = "wel", subCommands = "create", permission = "welcome.create", description = Message.CMD_CRT_DESC, allowConsole = true)
public class CmdWelCreate extends Cmd {
    public boolean execute(CommandSender sender, Player player, String[] args) {
        if (args.length < 3) return Message.CRT_NEED_PLAYER.print(sender);
        if (!PasswordValidator.validatePassword(args[2])) {
            Message.CRT_PWD_INVALID.print(sender);
            sender.sendMessage(PasswordValidator.getInfo());
            return true;
        }
        PasswordManager.hasPassword(args[1]).whenComplete((hasPassword, e) -> {
            if (hasPassword) {
                Message.CRT_ALREADY_REGISTERED.print(sender, args[1]);
            } else {
                PasswordManager.setPassword(args[1], args[2]).whenComplete((create, e2) -> {
                    if (e2 != null && create) {
                        Message.CRT_OK.print(sender);
                    } else {
                        Message.CRT_FAIL.print(sender, args[1]);
                    }
                });
            }
        });
        return true;
    }
}
