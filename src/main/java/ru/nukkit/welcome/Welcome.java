package ru.nukkit.welcome;

import cn.nukkit.plugin.PluginBase;
import ru.nukkit.welcome.commands.Commander;
import ru.nukkit.welcome.password.HashType;
import ru.nukkit.welcome.password.PasswordProvider;
import ru.nukkit.welcome.password.PasswordValidator;
import ru.nukkit.welcome.players.ForbidActions;
import ru.nukkit.welcome.util.Message;
import ru.nukkit.welcome.util.TimeUtil;

public class Welcome extends PluginBase {

    private static Welcome instance;
    private String passwordProvider;

    public static Welcome getPlugin() {
        return instance;
    }

    String timeWaitLogin;
    String hashMethod;
    boolean autologinDisabled;
    String autoLoginMaxTime;
    boolean validatorSpecialChar;
    boolean validatorCapitalLetter;
    boolean validatorNumber;
    int validatorMinLength;
    int validatorMaxLength;

    boolean setBlindEffect;

    @Override
    public void onEnable(){
        instance = this;
        loadCfg();
        Message.init(this);
        PasswordValidator.init(this.validatorSpecialChar,this.validatorCapitalLetter,this.validatorNumber, this.validatorMinLength, this.validatorMaxLength);
        PasswordProvider.init();
        this.getServer().getPluginManager().registerEvents(new WelcomeListener(),this);
        this.getServer().getPluginManager().registerEvents(new ForbidActions(), this);
        Commander.init(this);
    }

    public void loadCfg(){
        this.getDataFolder().mkdirs();
        this.saveResource("config.yml");
        this.reloadConfig();
        this.hashMethod = this.getConfig().getNested("password.hash-algorithm", HashType.SHA256.name());
        this.timeWaitLogin = this.getConfig().getNested("password.wait-time","3m");
        this.passwordProvider = this.getConfig().getNested("database.provider",PasswordProvider.DATABASE.name());
        this.autologinDisabled = this.getConfig().getNested("autologin.disable", false);
        this.autoLoginMaxTime = this.getConfig().getNested("autologin.time", "15m");
        this.validatorCapitalLetter = this.getConfig().getNested("validate-force-capitals",false);
        this.validatorSpecialChar = this.getConfig().getNested("password.validator.force-specials",false);
        this.validatorNumber = this.getConfig().getNested("password.validator.force-numbers",true);
        this.validatorMinLength = this.getConfig().getNested("password.validator.min-length",6);
        this.validatorMaxLength = this.getConfig().getNested("password.validator.max-length",16);
        this.setBlindEffect = this.getConfig().getNested("before-login.blind-effect",true);
    }


    public HashType getHashAlgorithm() {
        return HashType.getAlgorithm(this.hashMethod);
    }
    public String getPasswordProvider(){
        return this.passwordProvider;
    }

    public boolean isAutologinDisabled() {
        return autologinDisabled;
    }

    public long getMaxAutoTime() {
        return TimeUtil.parseTime(this.autoLoginMaxTime);
    }

    public Long getWaitTime(){
        return TimeUtil.parseTime(timeWaitLogin);
    }

    public boolean useBlindEffect(){
        return this.setBlindEffect;
    }

}
