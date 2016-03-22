package ru.nukkit.welcome.provider.serverauth;

/**
 * Created by Igor on 20.03.2016.
 */


/*
Максим
if(\mysqli_num_rows($db->query("SHOW TABLES LIKE '" . $table_prefix . "serverauth'")) == 0){
$query = "CREATE TABLE " . $table_prefix . "serverauth (version VARCHAR(50), api_version VARCHAR(50), password_hash VARCHAR(50))";
$db->query($query);
}
if(\mysqli_num_rows($db->query("SHOW TABLES LIKE '" . $table_prefix . "serverauthdata'")) == 0){
$query = "CREATE TABLE " . $table_prefix . "serverauthdata (user VARCHAR(50), password VARCHAR(200), ip VARCHAR(50), firstlogin VARCHAR(50), lastlogin VARCHAR(50))";
$db->query($query);
не сильно отличаются )
я просто заменил serverauthdata на players
 */

//@DatabaseTable(tableName = "serverauthdata")
//@DatabaseTable
public class ServerauthTable {


    public ServerauthTable(){}

        // Hacking the annotations :)

        /*DatabaseTable dt = this.getClass().getAnnotation(DatabaseTable.class);
        Welcome.getPlugin().getLogger().info(dt.tableName());
        Object handler = Proxy.getInvocationHandler(dt);
        Field f;
        try {
            f = handler.getClass().getDeclaredField("memberValues");
            f.setAccessible(true);
            Map<String, Object> memberValues;
            memberValues = (Map<String, Object>) f.get(handler);
            Object oldValue = memberValues.get("tableName");

            Welcome.getPlugin().getLogger().info("oldValue "+oldValue.toString());

            if (oldValue == null || oldValue.getClass() != String.class) {
                throw new IllegalArgumentException();
            }
            memberValues.put("tableName",prefix+"serverauthdata");

            Welcome.getPlugin().getLogger().info("newValue "+memberValues.get("tableName").toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        dt = this.getClass().getAnnotation(DatabaseTable.class);
        Welcome.getPlugin().getLogger().info(dt.tableName());


*/


        /*DatabaseTable dt = this.getClass().getAnnotation(DatabaseTable.class);
        try {
            Annotation newAnnotation = new DatabaseTable() {
                public Class<? extends Annotation> annotationType() {
                    return null;
                }
                public String tableName() {
                    return prefix+"serverauthdata";
                }
                public Class<?> daoClass() {
                    return Void.class;
                }

            };
            Field field = null;
            field = Class.class.getDeclaredField("annotations");
            field.setAccessible(true);
            Map<Class<? extends Annotation>, Annotation> annotations = (Map<Class<? extends Annotation>, Annotation>) field.get(ServerauthTable.class);
            annotations.put(DatabaseTable.class, newAnnotation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    //}


    public ServerauthTable(String name, String password){
        //this();
        this.user = name;
        this.password = password;
        this.firstlogin = String.valueOf(System.currentTimeMillis());
        this.lastlogin = String.valueOf(System.currentTimeMillis());
    }



   // @DatabaseField(id=true, canBeNull = false, columnName = "user")
    String user;

    //@DatabaseField(canBeNull = false, columnName = "password")
    String password;

    //@DatabaseField
    String ip;

    //1458450879015
    //@DatabaseField (columnName = "firstlogin")
    String firstlogin;

    //@DatabaseField (columnName = "lastlogin")
    String lastlogin;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstlogin() {
        return firstlogin;
    }

    public void setFirstlogin(String firstlogin) {
        this.firstlogin = firstlogin;
    }

    public String getLastlogin() {
        return lastlogin;
    }

    public void setLastlogin(String lastlogin) {
        this.lastlogin = lastlogin;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}