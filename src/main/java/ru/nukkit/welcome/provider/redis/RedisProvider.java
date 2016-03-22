package ru.nukkit.welcome.provider.redis;

import cn.nukkit.Server;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import ru.nukkit.nedis.Nedis;
import ru.nukkit.welcome.Welcome;
import ru.nukkit.welcome.password.PasswordManager;
import ru.nukkit.welcome.provider.PasswordProvider;
import ru.nukkit.welcome.util.Message;

import java.io.File;
import java.util.Set;

public class RedisProvider implements PasswordProvider {

    private RedisCfg cfg;
    private boolean enabled;
    private JedisPool jedisPool;


    public RedisProvider(){
        enabled = false;
        if (Server.getInstance().getPluginManager().getPlugin("Nedis") == null){
            Message.DB_NEDIS_NOTFOUND.log();
            return;
        }
        cfg = new RedisCfg(new File(Welcome.getPlugin().getDataFolder()+File.separator+"redis.yml"));
        cfg.load();
        cfg.save();

        jedisPool = cfg.useDefault ? Nedis.getJedisPool() :
            Nedis.createJedisPool(cfg.host,cfg.port,cfg.timeout,cfg.password,cfg.database,cfg.clientName);
        enabled = (jedisPool != null);
    }


    public boolean isEnabled() {
        return enabled;
    }


    public boolean checkPassword(String playerName, String password) {
        Jedis jedis = null;
        boolean result = false;
        try {
            jedis = jedisPool.getResource();
            String key = key("password",playerName);
            if (jedis.exists(key))
                result = password.equals(key);
        } catch (Exception e){
            PasswordManager.setLock(playerName);
            return false;
        } finally {
            if (jedis!=null) jedis.close();
        }
        return result;
    }

    public boolean setPassword(String playerName, String password) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(key("password",playerName),password);
        } catch (Exception e){
            PasswordManager.setLock(playerName);
            return false;
        } finally {
            if (jedis!=null) jedis.close();
        }
        return true;
    }

    public boolean hasPassword(String playerName) {
        Jedis jedis = null;
        boolean result = false;
        try {
            jedis = jedisPool.getResource();
            result = (jedis.exists(key("password",playerName)));
        } catch (Exception e){
            PasswordManager.setLock(playerName);
            return false;
        } finally {
            if (jedis!=null) jedis.close();
        }
        return result;
    }

    public boolean removePassword(String playerName) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String key = key("password",playerName);
            if (jedis.exists(key)) jedis.del(key);
        } catch (Exception e){
            PasswordManager.setLock(playerName);
            return false;
        } finally {
            if (jedis!=null) jedis.close();
        }
        return true;
    }

    public Long lastLoginFromIp(String playerName, String ip){
        Jedis jedis = null;
        long time = 0;
        try {
            jedis = jedisPool.getResource();
            Set<String> keys = jedis.keys(key("ip","*"));
            for (String key : keys){
                if (!ip.equalsIgnoreCase(jedis.get(key))) continue;
                String name = key.split(":")[1];
                if (!jedis.exists(key("time",name))) continue;
                long ipTime = Long.parseLong(jedis.get(key("time",name)));
                if (ipTime>time) time = ipTime;
            }
        } catch (Exception e){
            PasswordManager.setLock(playerName);
            return 0L;
        } finally {
            if (jedis!=null) jedis.close();
        }
        return time;
    }

    public boolean checkAutoLogin(String playerName, String uuid, String ip) {
        Jedis jedis = null;
        long time = 0;
        try {
            jedis = jedisPool.getResource();
            if (jedis.exists(key("uuid",playerName))&&
                    jedis.exists(key("ip",playerName))&&
                    jedis.exists(key("time",playerName))&&
                    jedis.get(key("uuid",playerName)).equals(uuid)&&
                    jedis.get(key("ip",playerName)).equals(ip)){
                time = Long.parseLong(jedis.get(key("time",playerName)));
            }
        } catch (Exception e){
            PasswordManager.setLock(playerName);
        } finally {
            if (jedis!=null) jedis.close();
        }
        return (System.currentTimeMillis()-time)<=Welcome.getCfg().getMaxAutoTime();
    }

    public void updateAutoLogin(String playerName, String uuid, String ip, long currentTime) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(key("uuid",playerName),uuid);
            jedis.set(key("ip",playerName),ip);
            jedis.set(key("time",playerName),String.valueOf(currentTime));
        } catch (Exception e){
            PasswordManager.setLock(playerName);
        } finally {
            if (jedis!=null) jedis.close();
        }
    }

    public boolean removeAutoLogin(String playerName) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(key("time",playerName),"0");
        } catch (Exception e){
            PasswordManager.setLock(playerName);
            return false;
        } finally {
            if (jedis!=null) jedis.close();
        }
        return true;
    }

    public void onDisable() {
        if (jedisPool !=null) jedisPool.destroy();
        jedisPool = null;
    }

    private String key(String fieldName, String playerName){
        return new StringBuilder(cfg.prefix==null? "" :cfg.prefix).append(fieldName).append(":").append(playerName).toString();
    }
}