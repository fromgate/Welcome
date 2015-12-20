package ru.nukkit.welcome.password;

import cn.nukkit.utils.Config;
import ru.nukkit.welcome.Welcome;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PasswordYaml implements Password {
    private Map<String,String> passwords;
    private Map<String,LoginState> logins;
    //private Map<String,String> ips;

    public PasswordYaml(){
        this.passwords = new HashMap<String, String>();
        logins = new HashMap<String, LoginState>();
        /* this.uuids = new HashMap<String, String>();
        this.ips = new HashMap<String, String>(); */
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

    public boolean checkAutoLogin(String playerName, String uuid, String ip) {
        long loginTime = System.currentTimeMillis();
        LoginState prevLogin = this.logins.containsKey(playerName) ? this.logins.get(playerName) : null;
        LoginState newLogin = new LoginState(uuid,ip,loginTime);
        logins.put(playerName,newLogin);
        save();
        if (prevLogin==null) return false;
        if (!prevLogin.uuid.equalsIgnoreCase(newLogin.uuid)) return false;
        if (!prevLogin.ip.equalsIgnoreCase(newLogin.ip)) return false;
        return (newLogin.time-prevLogin.time)<=Welcome.getPlugin().getMaxAutoTime();
    }


    private void save() {
        try {
            File dir = Welcome.getPlugin().getDataFolder();
            dir.mkdirs();
            File f = new File(dir, "passwords.yml");
            if (f.exists()) f.delete();
            Config cfg = new Config (f,Config.YAML);
            for (String password : passwords.keySet())
                cfg.setNested(password, passwords.get(password));
            cfg.save();
            f = new File(dir, "lastlogin.yml");
            if (f.exists()) f.delete();
            cfg = new Config (f,Config.YAML);
            for (String key : this.logins.keySet()){
                LoginState ls = this.logins.get(key);
                cfg.setNested(key+".uuid",ls.uuid);
                cfg.setNested(key+".ip",ls.ip);
                cfg.setNested(key+".time",ls.time);
            }
        } catch (Exception e){
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
                passwords.put(key, (String) cfg.getNested(key));
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
                    String uuid = (String) cfg.getNested(key+".uuid");
                    String ip = (String) cfg.getNested(key+".uuid");
                    long time = (Long) cfg.getNested(key+".time");
                    this.logins.put(k,new LoginState(uuid,ip,time));
                }
            }
        } catch (Exception e){
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

