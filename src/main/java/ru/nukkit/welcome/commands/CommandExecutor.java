package ru.nukkit.welcome.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import ru.nukkit.welcome.util.Message;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandExecutor extends Command {
    public CommandExecutor(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        Message.debugMessage(commandSender.getName(), s, new ArrayList<String>(Arrays.asList(strings)).toString());
        return Commander.execute(commandSender, s, strings);
    }
}
