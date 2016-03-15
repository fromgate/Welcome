package ru.nukkit.welcome.util;

import cn.nukkit.plugin.Plugin;
import ru.nukkit.welcome.password.HashType;
import ru.nukkit.welcome.password.PasswordProvider;

public class Cfg extends cn.nukkit.utils.SimpleConfig {
    public Cfg(Plugin plugin) {
        super(plugin);
        plugin.saveResource("config.yml");
    }

    @Path (value = "password.hash-algorithm")
    public String hashMethod = HashType.SHA256.name();

    @Path (value = "password.wait-time")
    public String timeWaitLogin = "3m";

    @Path (value = "database.provider")
    public String passwordProvider = PasswordProvider.DATABASE.name();

    @Path (value = "autologin.disable")
    public boolean autologinDisabled = false;

    @Path (value = "autologin.time")
    public String autoLoginMaxTime = "15m";

    @Path (value = "login-atempts.kick-on-exceed")
    public boolean loginAtempts = true;

    @Path (value = "login-atempts.max-atempts-allowed")
    public int loginAtemptsMax = 3;

    @Path (value = "validate-force-capitals")
    public boolean validatorCapitalLetter = false;

    @Path (value = "password.validator.force-specials")
    public boolean validatorSpecialChar = false;

    @Path (value = "password.validator.force-numbers")
    public boolean validatorNumber = true;

    @Path (value = "password.validator.min-length")
    public int validatorMinLength = 6;

    @Path (value = "password.validator.max-length")
    public int validatorMaxLength = 16;

    @Path (value = "before-login.blind-effect")
    public boolean setBlindEffect = true;
}
