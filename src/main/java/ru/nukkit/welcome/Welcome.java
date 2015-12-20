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


    @Override
    public void onEnable(){
        instance = this;
        reloadCfg();
        Message.init(this);
        PasswordValidator.init(this.validatorSpecialChar,this.validatorCapitalLetter,this.validatorNumber, this.validatorMinLength, this.validatorMaxLength);
        PasswordProvider.init();
        this.getServer().getPluginManager().registerEvents(new WelcomeListener(),this);
        this.getServer().getPluginManager().registerEvents(new ForbidActions(), this);
        Commander.init(this);
    }

    public void reloadCfg(){
        this.getDataFolder().mkdirs();
        this.reloadConfig();
        this.hashMethod = this.getConfig().getNested("hash-algorithm", HashType.SHA256.name());
        this.getConfig().setNested("hash-algorithm", getHashAlgorithm().name());
        this.timeWaitLogin = this.getConfig().getNested("password-wait-time","3m");
        this.getConfig().setNested("password-wait-time",this.timeWaitLogin);
        this.passwordProvider = this.getConfig().getNested("password-provider",PasswordProvider.DATABASE.name());
        this.getConfig().setNested("password-provider",this.passwordProvider);
        this.autologinDisabled = this.getConfig().getNested("autologin-disable", false);
        this.getConfig().setNested("autologin-disable", this.autologinDisabled);
        this.autoLoginMaxTime = this.getConfig().getNested("autologin-time", "15m");
        this.getConfig().setNested("autologin-time", this.autoLoginMaxTime);
        this.validatorCapitalLetter = this.getConfig().getNested("validate-force-capitals",false);
        this.getConfig().setNested("validate-force-capitals",this.validatorCapitalLetter);
        this.validatorSpecialChar = this.getConfig().getNested("validate-force-specials",false);
        this.getConfig().setNested("validate-force-specials",this.validatorSpecialChar );
        this.validatorNumber = this.getConfig().getNested("validate-force-numbers",true);
        this.getConfig().setNested("validate-force-numbers",this.validatorNumber );
        this.validatorMinLength = this.getConfig().getNested("validate-min-length",6);
        this.getConfig().setNested("validate-min-length",this.validatorMinLength);
        this.validatorMaxLength = this.getConfig().getNested("validate-max-length",16);
        this.getConfig().setNested("validate-max-length",this.validatorMaxLength);
        this.saveConfig();
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

    public int getWaitTimeTicks(){
        return TimeUtil.timeToTicks(TimeUtil.parseTime(timeWaitLogin)).intValue();
    }
}
