package ru.nukkit.welcome.commands;


import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import ru.nukkit.welcome.util.Message;

@CmdDefine(command = "welcome", alias = "wel", subCommands ={"help"} , permission = "welcome.help", description = Message.CMD_HELP_DESC)
public class CmdHelp extends Cmd{


    @Override
    public boolean execute(CommandSender sender, Player player, String[] args) {
        int pageNum = args.length>1&&args[1].matches("\\d+") ? Integer.parseInt(args[1]) : 1;
        Commander.printHelp(player,pageNum);
        return true;
    }
}
