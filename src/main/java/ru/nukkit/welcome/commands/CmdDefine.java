package ru.nukkit.welcome.commands;

import ru.nukkit.welcome.util.Message;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CmdDefine {
    public String command();

    public String alias() default "";

    public String[] subCommands();

    public String permission();

    public boolean allowConsole() default false;

    public Message description();
}

