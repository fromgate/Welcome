package ru.nukkit.welcome;

import cn.nukkit.plugin.PluginBase;
import ru.nukkit.welcome.commands.Commander;
import ru.nukkit.welcome.password.PasswordProvider;
import ru.nukkit.welcome.password.PasswordValidator;
import ru.nukkit.welcome.players.ForbidActions;
import ru.nukkit.welcome.util.Cfg;
import ru.nukkit.welcome.util.Message;

public class Welcome extends PluginBase {

    private static Welcome instance;
    private Cfg cfg;

    public static Welcome getPlugin() {
        return instance;
    }
    public static Cfg getCfg(){
        return getPlugin().cfg;
    }

    @Override
    public void onEnable(){
        instance = this;
        cfg = new Cfg(this);
        cfg.load();
        Message.init(this);
        cfg.update();
        PasswordValidator.init(cfg.validatorSpecialChar,cfg.validatorCapitalLetter,cfg.validatorNumber, cfg.validatorMinLength, cfg.validatorMaxLength);
        PasswordProvider.init();
        this.getServer().getPluginManager().registerEvents(new WelcomeListener(),this);
        this.getServer().getPluginManager().registerEvents(new ForbidActions(), this);
        Commander.init(this);
    }

    @Override
    public void onDisable(){
        PasswordProvider.onDisable();
    }

}
