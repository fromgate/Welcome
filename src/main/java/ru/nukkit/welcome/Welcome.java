package ru.nukkit.welcome;

import cn.nukkit.plugin.PluginBase;
import ru.nukkit.welcome.commands.Commander;
import ru.nukkit.welcome.password.HashType;
import ru.nukkit.welcome.password.PasswordProvider;
import ru.nukkit.welcome.password.PasswordValidator;
import ru.nukkit.welcome.players.ForbidActions;
import ru.nukkit.welcome.util.Cfg;
import ru.nukkit.welcome.util.Message;
import ru.nukkit.welcome.util.TimeUtil;

public class Welcome extends PluginBase {

    private static Welcome instance;
    private Cfg cfg = null;




    public static Welcome getPlugin() {
        return instance;
    }
    public static Cfg getCfg(){
        return getPlugin().cfg;
    }

    /*
    private String passwordProvider;
    String timeWaitLogin;
    String hashMethod;
    boolean autologinDisabled;
    String autoLoginMaxTime;
    boolean validatorSpecialChar;
    boolean validatorCapitalLetter;
    boolean validatorNumber;
    int validatorMinLength;
    int validatorMaxLength;
    */

    boolean setBlindEffect;

    @Override
    public void onEnable(){
        instance = this;
        cfg = new Cfg(this);
        cfg.load();
        Message.init(this);
        PasswordValidator.init(cfg.validatorSpecialChar,cfg.validatorCapitalLetter,cfg.validatorNumber, cfg.validatorMinLength, cfg.validatorMaxLength);
        PasswordProvider.init();
        this.getServer().getPluginManager().registerEvents(new WelcomeListener(),this);
        this.getServer().getPluginManager().registerEvents(new ForbidActions(), this);
        Commander.init(this);
    }

    /*
    public void loadCfg(){
        if (cfg==null) cfg = new Cfg(this);
        cfg.load();

        this.hashMethod = this.getConfig().getString("password.hash-algorithm", HashType.SHA256.name());
        this.timeWaitLogin = this.getConfig().getString("password.wait-time","3m");
        this.passwordProvider = this.getConfig().getString("database.provider",PasswordProvider.DATABASE.name());
        this.autologinDisabled = this.getConfig().getBoolean("autologin.disable", false);
        this.autoLoginMaxTime = this.getConfig().getString("autologin.time", "15m");
        this.validatorCapitalLetter = this.getConfig().getBoolean("validate-force-capitals",false);
        this.validatorSpecialChar = this.getConfig().getBoolean("password.validator.force-specials",false);
        this.validatorNumber = this.getConfig().getBoolean("password.validator.force-numbers",true);
        this.validatorMinLength = this.getConfig().getInt("password.validator.min-length",6);
        this.validatorMaxLength = this.getConfig().getInt("password.validator.max-length",16);
        this.setBlindEffect = this.getConfig().getBoolean("before-login.blind-effect",true);
    } */


    public HashType getHashAlgorithm() {
        return HashType.getAlgorithm(cfg.hashMethod);
    }
    public String getPasswordProvider(){
        return cfg.passwordProvider;
    }

    public boolean isAutologinDisabled() {
        return cfg.autologinDisabled;
    }

    public long getMaxAutoTime() {
        return TimeUtil.parseTime(cfg.autoLoginMaxTime);
    }

    public Long getWaitTime(){
        return TimeUtil.parseTime(cfg.timeWaitLogin);
    }

    public boolean useBlindEffect(){
        return this.setBlindEffect;
    }

}
