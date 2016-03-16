package ru.nukkit.welcome.util;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.SimpleConfig;
import ru.nukkit.welcome.password.HashType;
import ru.nukkit.welcome.password.PasswordProvider;

import java.io.File;
import java.lang.reflect.Field;

public class Cfg extends cn.nukkit.utils.SimpleConfig {
    public Cfg(Plugin plugin) {
        super(plugin);
        plugin.saveResource("config.yml");
    }

    //# Plugin language. Supported: english, russian
    @Path (value = "general.language")
    public String language = "english";

    //# Enable/Disable debug messages. Don't touch this ;)
    @Path (value = "general.debug-mode")
    public boolean debugMode = false;

    @Path (value = "message.allow-tips")
    public boolean useTips = true;

    @Path (value = "message.delay-between-repeats")
    public String messageDelay = "10s";

    //# Password hash algorithm. Supported values: MD5, SHA1, SHA256, SHA512
    @Path (value = "password.hash-algorithm")
    public String hashMethod = HashType.SHA256.name();

    //# Time to wait until user login.
    //# If time passed - user will be kicked out from the server
    @Path (value = "password.wait-time")
    public String timeWaitLogin = "3m";

    //# Where to store you passwords
    //# DATABASE - SQLite or MySQL database configured at DBLib plugin
    //# YAML - in yaml files
    @Path (value = "database.provider")
    public String passwordProvider = PasswordProvider.DATABASE.name();

    //# Delay between reinitialization tries (on database connections lost)
    @Path (value = "database.reinit-on-fail-time")
    public String providerReinitTime = "30s";

    //# Remember user IP, UUID and Name. Next time (during the provided time limit)
    //# client with same parameters will be logged in automatically
    @Path (value = "autologin.disable")
    public boolean autologinDisabled = false;

    @Path (value = "autologin.time")
    public String autoLoginMaxTime = "15m";

    //#Login settings
    //#Enable failed logins kick, when exceeded number of allowed attempts
    @Path (value = "login-attempts.kick-on-exceed")
    public boolean loginAtempts = true;

    //#Max number of attempts
    @Path (value = "login-attempts.max-attempts-allowed")
    public int loginAtemptsMax = 5;

    //# Password validator options
    @Path (value = "password.validator.force-capitals")
    public boolean validatorCapitalLetter = false;

    @Path (value = "password.validator.force-specials")
    public boolean validatorSpecialChar = false;

    @Path (value = "password.validator.force-numbers")
    public boolean validatorNumber = true;

    @Path (value = "password.validator.min-length")
    public int validatorMinLength = 6;

    @Path (value = "password.validator.max-length")
    public int validatorMaxLength = 16;

    //# Debuff effects for not-loged users
    //# Blind effect until user is not logged-in
    @Path (value = "before-login.blind-effect")
    public boolean setBlindEffect = true;

    @Path (value = "before-login.block-chat")
    public boolean blockChat = true;


    /////////////////////////////////////////////////////////////////////////////////
    public HashType getHashAlgorithm() {
        return HashType.getAlgorithm(hashMethod);
    }

    public long getMaxAutoTime() {
        return TimeUtil.parseTime(autoLoginMaxTime);
    }

    public Long getWaitTime(){
        return TimeUtil.parseTime(timeWaitLogin);
    }

    public int getReinitTimeTicks(){
        return TimeUtil.timeToTicks(TimeUtil.parseTime(providerReinitTime)).intValue();
    }

    public int getMessageRepeatTicks(){
        return TimeUtil.timeToTicks(TimeUtil.parseTime(this.messageDelay)).intValue();
    }

    public void update(){
        File file;
        try {
            Field field = SimpleConfig.class.getDeclaredField("configFile");
            field.setAccessible(true);
            file = (File) field.get((SimpleConfig) this);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Config cfg = new Config(file,Config.YAML);
        if (cfg.get("login-attempts.max-attempts-allowed")==null) {
            save();
            Message.CFG_UPDATED.log();
        }
    }

}
