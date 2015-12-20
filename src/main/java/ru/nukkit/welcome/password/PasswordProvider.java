package ru.nukkit.welcome.password;


import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import ru.nukkit.welcome.Welcome;

public enum PasswordProvider {
    YAML (PasswordYaml.class),
    DATABASE (PasswordDbLib.class);

    Class<? extends  Password> clazz;
    PasswordProvider(Class<? extends Password> clazz) {
        this.clazz = clazz;
    }

    public Password getProvider(){
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    private static Password passworder;


    public static void init(){
        PasswordProvider pp = getByName(Welcome.getPlugin().getPasswordProvider());
        if (pp == null){
            pp = PasswordProvider.YAML;
            Welcome.getPlugin().getLogger().info("Failed to determine password provider: "+Welcome.getPlugin().getPasswordProvider()+" YAML will be used...");
        }
        passworder = pp.getProvider();
        if (!passworder.isEnabled()) {
            Welcome.getPlugin().getLogger().info("Failed to initialize: "+Welcome.getPlugin().getPasswordProvider()+" YAML will be used...");
            pp = PasswordProvider.YAML;
            passworder = PasswordProvider.YAML.getProvider();
        }
        Welcome.getPlugin().getLogger().info(TextFormat.GREEN+"Password provider: "+pp.name()+" Hash algorithm: "+Welcome.getPlugin().getHashAlgorithm().name());
    }

    public static PasswordProvider getByName(String pwdProv){
        for (PasswordProvider pp : PasswordProvider.values())
            if (pp.name().equalsIgnoreCase(pwdProv)) return pp;
        return null;
    }

    public static boolean checkPassword (String playerName, String pwdStr){
        return passworder.checkPassword(playerName,Welcome.getPlugin().getHashAlgorithm().getHash(pwdStr));
    }

    public static boolean checkPassword (Player player, String pwdStr){
        return checkPassword(player.getName(),Welcome.getPlugin().getHashAlgorithm().getHash(pwdStr));
    }


    public static boolean setPassword (String playerName, String pwdStr){
        return passworder.setPassword(playerName,Welcome.getPlugin().getHashAlgorithm().getHash(pwdStr));
    }

    public static boolean setPassword (Player player, String pwdStr){
        return setPassword(player.getName(),Welcome.getPlugin().getHashAlgorithm().getHash(pwdStr));
    }

    public static boolean hasPassword(String playerName) {
        return passworder.hasPassword(playerName);
    }

    public static boolean hasPassword(Player player) {
        return hasPassword(player.getName());
    }

    public static boolean removePassword(Player player) {
        return  removePassword(player.getName());
    }

    public static boolean removePassword(String playerName) {
        return  passworder.removePassword(playerName);
    }

    public static String hashPassword (String password){
        return Welcome.getPlugin().getHashAlgorithm().getHash(password);
    }


    public static boolean checkAutologin (Player player){
        if (!hasPassword(player)) return false;
        if (Welcome.getPlugin().isAutologinDisabled()) return false;
        return passworder.checkAutoLogin(player.getName(), player.getUniqueId().toString(), player.getAddress());
    }



}
