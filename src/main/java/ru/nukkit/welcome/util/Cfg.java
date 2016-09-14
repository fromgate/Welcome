package ru.nukkit.welcome.util;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.SimpleConfig;
import cn.nukkit.utils.TextFormat;
import ru.nukkit.welcome.Welcome;
import ru.nukkit.welcome.password.HashType;
import ru.nukkit.welcome.password.PasswordManager;

import java.io.File;
import java.lang.reflect.Field;

public class Cfg extends cn.nukkit.utils.SimpleConfig {

    public Cfg(Plugin plugin) {
        super(plugin);
        plugin.saveResource("config.yml");
    }

    //# Plugin language. Supported: default, eng, rus
    @Path("general.language")
    public String language = "default";

    //# Enable/Disable debug messages. Don't touch this ;)
    @Path("general.debug-mode")
    public boolean debugMode = false;

    //# Enable/Disable debug messages. Don't touch this ;)
    @Path("general.save-translation")
    public boolean saveLanguage;

    @Path("message.allow-tips")
    public boolean useTips = true;

    @Path("message.delay-between-repeats")
    public String messageDelay = "10s";

    @Path("password.type-password-in-chat")
    public boolean typeInChat = false;

    //# Password hash algorithm. Supported values: MD5, SHA1, SHA256, SHA512
    @Path("password.hash-algorithm")
    public String hashMethod = HashType.SHA256.name();

    //# Time to wait until user login.
    //# If time passed - user will be kicked out from the server
    @Path("password.wait-time")
    public String timeWaitLogin = "3m";

    //# Where to store you passwords
    //# DATABASE - SQLite or MySQL database configured at DBLib plugin
    //# YAML - in yaml files
    @Path("database.provider")
    public String passwordProvider = PasswordManager.YAML.name();

    //# Delay between reinitialization tries (on database connections lost)
    @Path("database.reinit-on-fail-time")
    public String providerReinitTime = "30s";

    //# Remember user IP, UUID and Name. Next time (during the provided time limit)
    //# client with same parameters will be logged in automatically
    @Path("login.auto.enable")
    public boolean autologinEnable = true;

    @Path("login.auto.time")
    public String autoLoginMaxTime = "15m";

    //#Login settings
    //#Enable failed logins kick, when exceeded number of allowed attempts
    @Path("login.attempts.kick-on-exceed")
    public boolean loginAtempts = true;

    //#Max number of attempts
    @Path("login.attempts.max-attempts-allowed")
    public int loginAtemptsMax = 5;

    @Path("login.join-message.enable")
    public boolean joinMessageEnable = true;

    @Path("login.join-message.pre-login")
    public String joinMessagePre = Message.JOIN_MSG_PRE.getText("%player%").replace("§", "&");

    @Path("login.join-message.after-login")
    public String joinMessageAfter = Message.JOIN_MSG_BC.getText("%player%").replace("§", "&");

    //# Password validator options
    @Path("password.validator.force-capitals")
    public boolean validatorCapitalLetter = false;

    @Path("password.validator.force-specials")
    public boolean validatorSpecialChar = false;

    @Path("password.validator.force-numbers")
    public boolean validatorNumber = true;

    @Path("password.validator.min-length")
    public int validatorMinLength = 6;

    @Path("password.validator.max-length")
    public int validatorMaxLength = 16;

    //# Debuff effects for not-loged users
    //# Blind effect until user is not logged-in
    @Path("before-login.blind-effect")
    public boolean setBlindEffect = true;

    @Path("before-login.block-chat")
    public boolean blockChat = true;

    @Path("before-login.block-item-pickup")
    public boolean blockPickup = true;

    @Path("register.password-confirmation")
    public boolean passwordConfirmation = true;

    @Path("register.ip-restriction.enable")
    public boolean registerRestrictions = true;

    @Path("register.ip-restriction.time")
    public String registerRestrictionTime = "3h";

    /////////////////////////////////////////////////////////////////////////////////
    public HashType getHashAlgorithm() {
        return HashType.getAlgorithm(hashMethod);
    }

    public long getMaxAutoTime() {
        return TimeUtil.parseTime(autoLoginMaxTime);
    }

    public Long getWaitTime() {
        return TimeUtil.parseTime(timeWaitLogin);
    }

    public int getReinitTimeTicks() {
        return TimeUtil.timeToTicks(TimeUtil.parseTime(providerReinitTime)).intValue();
    }

    public int getMessageRepeatTicks() {
        return TimeUtil.timeToTicks(TimeUtil.parseTime(this.messageDelay)).intValue();
    }

    public long getRestrictIpTime() {
        return TimeUtil.parseTime(registerRestrictionTime);
    }

    public Message getTypeReg() {
        return Welcome.getCfg().typeInChat ?
                (this.passwordConfirmation ? Message.TYPE_REG_CHAT : Message.TYPE_REG1_CHAT)
                : (this.passwordConfirmation ? Message.TYPE_REG : Message.TYPE_REG1);
    }

    public void sendPreLoginMessage(Player player) {
        if (this.joinMessageEnable)
            player.sendMessage(TextFormat.colorize(this.joinMessagePre.replace("%player%", player.getName())));
    }

    public void broadcastLoginMessage(Player player) {
        if (this.joinMessageEnable)
            for (Player p : Server.getInstance().getOnlinePlayers().values())
                p.sendMessage(TextFormat.colorize(this.joinMessageAfter.replace("%player%", player.getName())));
    }


    public void update() {
        File file;
        try {
            Field field = SimpleConfig.class.getDeclaredField("configFile");
            field.setAccessible(true);
            file = (File) field.get((SimpleConfig) this);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Config cfg = new Config(file, Config.YAML);
        if (!cfg.exists("password.type-password-in-chat")) {
            save();
            Message.CFG_UPDATED.log();
        }
    }

}
