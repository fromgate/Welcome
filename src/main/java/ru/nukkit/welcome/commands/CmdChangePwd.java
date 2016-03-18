package ru.nukkit.welcome.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import ru.nukkit.welcome.password.PasswordProvider;
import ru.nukkit.welcome.players.PlayerManager;
import ru.nukkit.welcome.util.Message;

@CmdDefine(command = "changepassword", alias = "changepwd,changepass,cpw",subCommands = {}, allowConsole = false, permission = "welcome.changepassword" , description = Message.CPW_DESC)
public class CmdChangePwd extends Cmd{
    //changepwd <old> <new> <new>
    @Override
    public boolean execute(CommandSender sender, Player player, String[] args) {
        if (args.length<3) return Message.CPW_USAGE.print(player,'c');
        if (!PlayerManager.isPlayerLoggedIn(player))  return Message.ERR_NOT_LOGGED.print(player);
        if (!PasswordProvider.checkPassword(player,args[0])) return Message.ERR_PWD_WRONG.print(player);
        if (!args[1].equals(args[2])) return Message.ERR_PWD_NOTMATCH.print(player,'c');
        PasswordProvider.setPassword(player,args[1]);
        return Message.CPW_OK.print(player,'6');
    }
}
