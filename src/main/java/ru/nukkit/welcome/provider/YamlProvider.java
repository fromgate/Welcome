package ru.nukkit.welcome.provider;

import cn.nukkit.utils.Config;
import ru.nukkit.welcome.Welcome;
import ru.nukkit.welcome.password.PasswordManager;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class YamlProvider implements PasswordProvider {
    private Map<String,String> passwords;
    private Map<String,LoginState> logins;

    public YamlProvider(){
        this.passwords = new HashMap<String, String>();
        logins = new HashMap<String, LoginState>();
        load();
    }

    public boolean isEnabled() {
        return true;
    }

    public boolean checkPassword(String playerName, String password) {
        if (!passwords.containsKey(playerName)) return false;
        return passwords.get(playerName).equals(password);
    }

    public boolean setPassword(String playerName, String password) {
        passwords.put(playerName,password);
        save();
        return true;
    }

    public boolean hasPassword(String playerName) {
        return passwords.containsKey(playerName);
    }

    public boolean removePassword(String playerName) {
        if (!this.passwords.containsKey(playerName)) return false;
        passwords.remove(playerName);
        save();
        return true;
    }

    public Long lastLoginFromIp(String playerName, String ip) {
        long time = 0;
        for (LoginState ls : logins.values()){
            if (ls.ip.equalsIgnoreCase(ip)&&ls.time>time) time = ls.time;
        }
        return time;
    }

    public boolean checkAutoLogin(String playerName, String uuid, String ip) {
        long loginTime = System.currentTimeMillis();
        LoginState prevLogin = this.logins.containsKey(playerName) ? this.logins.get(playerName) : null;
        if (prevLogin==null) return false;
        LoginState newLogin = new LoginState(uuid,ip,loginTime);
        if (!prevLogin.uuid.equalsIgnoreCase(newLogin.uuid)) return false;
        if (!prevLogin.ip.equalsIgnoreCase(newLogin.ip)) return false;
        return (newLogin.time-prevLogin.time)<=Welcome.getCfg().getMaxAutoTime();
    }

    public void updateAutoLogin(String playerName, String uuid, String ip) {
        updateAutoLogin (playerName,uuid,ip,System.currentTimeMillis());
    }

    public void updateAutoLogin(String playerName, String uuid, String ip, long currentTime) {
        LoginState newLogin = new LoginState(uuid,ip,currentTime);
        logins.put(playerName,newLogin);
        save();
    }

    public boolean removeAutoLogin(String playerName) {
        if (logins.containsKey(playerName)) logins.remove(playerName);
        return true;
    }

    public void onDisable() {
    }


    private void save() {
        try {
            File dir = Welcome.getPlugin().getDataFolder();
            dir.mkdirs();
            File f = new File(dir, "passwords.yml");
            if (f.exists()) f.delete();
            Config cfg = new Config (f,Config.YAML);
            for (String password : passwords.keySet())
                cfg.set(password, passwords.get(password));
            cfg.save();
            f = new File(dir, "lastlogin.yml");
            if (f.exists()) f.delete();
            cfg = new Config (f,Config.YAML);
            for (String key : this.logins.keySet()){
                LoginState ls = this.logins.get(key);
                cfg.set(key+".uuid",ls.uuid);
                cfg.set(key+".ip",ls.ip);
                cfg.set(key+".time",ls.time);
            }
            cfg.save();
        } catch (Exception e){
            PasswordManager.setLock(null);
            e.printStackTrace();
        }
    }

    private void load() {
        try {
            File dir = Welcome.getPlugin().getDataFolder();
            dir.mkdirs();
            File f = new File(dir, "passwords.yml");
            if (!f.exists()) f.createNewFile();
            Config cfg = new Config(f,Config.YAML);
            passwords = new HashMap<String, String>();
            for (String key : cfg.getAll().keySet()){
                passwords.put(key, cfg.getString(key));
            }
            f = new File(dir, "lastlogin.yml");
            if (!f.exists()) f.createNewFile();
            cfg = new Config(f,Config.YAML);
            logins = new HashMap<String, LoginState>();
            Set<String> usedKeys = new HashSet<String>();
            for (String key : cfg.getAll().keySet()){
                if (key.contains(".")){
                    String k = key.split(".")[0];
                    if (usedKeys.contains(k)) continue;
                    usedKeys.add(k);
                    String uuid = cfg.getString(key+".uuid");
                    String ip = cfg.getString(key+".uuid");
                    long time = cfg.getLong(key+".time");
                    this.logins.put(k,new LoginState(uuid,ip,time));
                }
            }
        } catch (Exception e){
            PasswordManager.setLock(null);
            e.printStackTrace();
        }
    }

    public class LoginState {
        LoginState(String uuid, String ip, long time){
            this.uuid = uuid;
            this.ip = ip;
            this.time = time;
        }
        String uuid;
        String ip;
        long time;
    }

}

