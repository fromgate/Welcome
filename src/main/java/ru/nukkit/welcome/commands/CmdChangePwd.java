package ru.nukkit.welcome.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import ru.nukkit.welcome.password.PasswordManager;
import ru.nukkit.welcome.password.PasswordValidator;
import ru.nukkit.welcome.players.PlayerManager;
import ru.nukkit.welcome.util.Message;

@CmdDefine(command = "changepassword", alias = "changepwd,changepass,cpw", subCommands = {}, allowConsole = false, permission = "welcome.changepassword", description = Message.CPW_DESC)
public class CmdChangePwd extends Cmd {
    //changepwd <old> <new> <new>
    @Override
    public boolean execute(CommandSender sender, Player player, String[] args) {
        if (args.length < 3) return Message.CPW_USAGE.print(player, 'c');
        if (!PlayerManager.isPlayerLoggedIn(player)) return Message.ERR_NOT_LOGGED.print(player);

        if (!PasswordValidator.validatePassword(args[0])) {
            Message.ERR_PWD_VALIDATE.print(player, 'c');
            player.sendMessage(PasswordValidator.getInfo());
            return true;
        }

        if (!args[1].equals(args[2])) return Message.ERR_PWD_NOTMATCH.print(player, 'c');

        PasswordManager.checkPassword(player, args[0]).whenComplete((pwdOk, e) -> {
            if (e != null) {
                e.printStackTrace();
            } else {
                if (pwdOk) {
                    PasswordManager.setPassword(player, args[1]).whenComplete((pwdSet, e2) -> {
                        if (e2 != null) {
                            e2.printStackTrace();
                        } else {
                            if (pwdSet) {
                                Message.CPW_OK.print(player, '6');
                            } else {
                                Message.CPW_FAIL.print(player, '6');
                            }
                        }
                    });
                } else {
                    Message.ERR_PWD_WRONG.print(player);
                }
            }
        });
        return true;
    }
}
