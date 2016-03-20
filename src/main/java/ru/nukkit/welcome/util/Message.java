package ru.nukkit.welcome.util;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Location;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import ru.nukkit.welcome.Welcome;

import java.io.*;
import java.text.DecimalFormat;


public enum Message {

    LNG_LOAD_FAIL ("Failed to load languages from file. Default message used"),
    LNG_SAVE_FAIL ("Failed to save lang file"),
    LNG_PRINT_FAIL ("Failed to print message to 'null': \"%1%\""),
    LNG_CONFIG ("[MESSAGES] Messages: %1% Language: %2% Save translate file: %1% Debug mode: %3%"),
    WORD_UNKNOWN ("Unknown"),
    PERMISSION_FAIL ("You have not enough permissions to execute this command",'c'),
    PLAYER_COMMAD_ONLY ("You can use this command in-game only!",'c'),
    CMD_REGISTERED("Command registered: %1%"),
    CMD_REG_DESC("/register <password> [password] - register player on server"),
    TYPE_LGN("Type /login <password> to login!"),
    TYPE_REG ("Type /register <password> <password> to login!"),
    TYPE_REG1 ("Type /register <password> to login!"),
    KICK_TIMEOUT ("Time out! Next time type /login <password> to join the game!",'c'),
    LGN_ATTEMPT_EXCEED("Too many login attempts!",'c'),
    LGN_ATTEMPT_EXCEED_LOG("Player %1% exceeded login attempts. Hacker?!"),
    LGN_ALREADY("You're already logged in!",'c'),
    REG_ALREADY("You're already registered!",'c'),
    LGN_MISS_PWD("Missed password! Type /login <password>",'c'),
    UNREG_MISS_PWD("Missed password! Type /unreg <password>",'c'),
    ERR_PWD_WRONG("Wrong password!",'c'),
    LGN_OK("You successfully logged-in! Welcome!"),
    REG_OK("You successfully registered! Welcome!"),
    ERR_PWD_NOTMATCH("Entered passwords are not match!",'c'),
    CMD_LGN_DESC("/login <password> - login to server"),
    CMD_LGF_DESC("/logoff - logoff from server (to prevent autologin time)"),
    REG_LOG("Player %1% registered!"),
    LGN_LOG("Player %1% logged-in!"),
    UNREG_OK("You was removed from server. Next time you must register again!", 'c'),
    LOGOFF_OK("Logged off from server. Come back soon :)"),
    CMD_UNREG_DESC("/unregister <password> - unregister from server"),
    CMD_RMV_DESC("/welcome remove <player> - remove (unregister) provided player"),
    RMV_NEED_PLAYER("Player name was not provided (/welcome remove <player>)"),
    RMV_FAIL("Failed to remove player: %1%",'c'),
    RMV_OK("Player %1% removed!"),
    LGN_AUTO("Welcome back, %1%!"),
    CPW_DESC("/changepassword <OldPassword> <NewPassword> <NewPassword> - change password"),
    CPW_USAGE("/changepassword <OldPassword> <NewPassword> <NewPassword>"),
    ERR_NOT_LOGGED("You're not logged in! Operation declined."),
    CPW_OK("Your password was successfully changed"),
    CMD_HELP_DESC ("/welcome help - Show help"),
    HLP_TITLE("%1% | Nukkit authorization system"),
    CMD_WLC_DESC("/welcome - Welcome plugin command"),
    ERR_PWD_VALIDATE("Your password is not valid!"),
    PWD_VALID_INFO ("Password must contain: %1% Length: %2%-%3%"),
    VLD_CAPITAL("capital letters"),
    VLD_LETTERS("letters"),
    VLD_SPEC_CHR("special chars"),
    VLD_NUMBER("numbers"),
    PWD_VALID_PATTERN("Password validator regex prepared: %1%"),
    ALREADY_LOGGED_IN("Player %1% is already logged on server!",'c','4'),
    DB_LOCK("Password provider failed. Please check your database connections.",'c'),
    DB_DBLIB_NOTFOUND("DbLib plugin not found. Please download it at: http://nukkit.ru/resources/dblib.14/"),
    DB_DBLIB_FOUND("DbLib detected. You can enable database support, by setting \"database.provider: DATABASE\" in config.yml"),
    LOCK_INFORM("Server is locked. Please contact server admin",'c'),
    DB_INIT ("Password provider: %1% Hash algorithm: %2%"),
    DB_REINIT ("Password provider %1% successfully reinitialized"),
    DB_RENIT_TRY("Trying to reinitialize password provider...",'c'),
    CFG_UPDATED("Config file updated. New variables added but we lost all comments..."),
    REG_RESTRICED_IP("You cannot register again. Change back your player name and login.",'c'),
    RELOAD_CMD_WARNING("Welcome plugin detected that command RELOAD was executed. Sorry, further plugin work cannot be guaranteed.",'c'),
    JOIN_MSG_PRE ("Hello %1%! You must login to play here!",'e','6'),
    JOIN_MSG_BC ("%1% joined the game!",'e','6'),
    CMD_CRT_DESC("/welcome create <player> <password> - Create (register) new player with provided password"),
    CRT_NEED_PLAYER("Player name and password was not provided (/welcome create <player> <password>)",'c'),
    CRT_ALREADY_REGISTERED("Player %1% already registered!",'c'),
    CRT_PWD_INVALID("Provided password is not valid!",'c'),
    CRT_FAIL ("Failed to register player %1%"),
    CRT_OK ("Player %1% successfully registered!"),
    CPWO_DESC ("/welcome setpassword <player> <newPassword> - change password of another player"),
    CPWO_USAGE ("You need to provide player name and new password (/welcome cpw <player> <password>)",'c'),
    CPWO_OK("Password of player %1% was changed"),
    CPWO_OK_INFORM("Your password was changed by %1%, new password is %2%"),
    CPWO_OK_INFORM_CONSOLE("Your password was changed, new password is %2%"),
    CPWO_FAIL("Failed to change password of player %1%");

    private static boolean debugMode = false;
    private static String language = "english";
    private static char c1 = 'a';
    private static char c2 = '2';

    private static PluginBase plugin = null;

    /**
     * This is my favorite debug routine :) I use it everywhere to print out variable values
     * @param s - array of any object that you need to print out.
     * Example:
     * Message.BC ("variable 1:",var1,"variable 2:",var2)
     */
    public static void BC (Object... s){
        if (!debugMode) return;
        if (s.length==0) return;
        StringBuilder sb = new StringBuilder("&3[").append(plugin.getDescription().getName()).append("]&f ");
        for (Object str : s)
            sb.append(str.toString()).append(" ");
        plugin.getServer().broadcastMessage(TextFormat.colorize(sb.toString().trim()));
    }



    /**
     * Send current message to log files
     * @param s
     * @return — always returns true.
     * Examples:
     * Message.ERROR_MESSAGE.log(variable1); // just print in log
     * return Message.ERROR_MESSAGE.log(variable1); // print in log and return value true
     */
    public boolean log(Object... s){
        plugin.getLogger().info(getText (s));
        return true;
    }

    /**
     * Same as log, but will printout nothing if debug mode is disabled
     * @param s
     * @return — always returns true.
     */
    public boolean debug(Object... s){
        if (debugMode) plugin.getLogger().info(TextFormat.clean(getText (s)));
        return true;
    }

    /**
     * Show a message to player in center of screen (this routine unfinished yet)
     * @param seconds — how much time (in seconds) to show message
     * @param sender — Player
     * @param s
     * @return — always returns true.
     */
    public boolean tip (int seconds, CommandSender sender, Object... s){
        if (sender == null) return Message.LNG_PRINT_FAIL.log(this.name());
        final Player player = sender instanceof Player ? (Player) sender : null;
        final String message = getText(s);
        if (player==null) sender.sendMessage(message);
        else for (int i=0;i<seconds;i++) Server.getInstance().getScheduler().scheduleDelayedTask(new Runnable() {
            public void run() {
                if (player.isOnline()) player.sendTip(message);
            }
        },20*i);
        return true;
    }

    /**
     * Show a message to player in center of screen
     * @param sender — Player
     * @param s
     * @return — always returns true.
     */
    public boolean tip (CommandSender sender, Object... s){
        if (sender == null) return Message.LNG_PRINT_FAIL.log(this.name());
        Player player = sender instanceof Player ? (Player) sender : null;
        String message = getText(s);
        if (player==null) sender.sendMessage(message);
        else player.sendTip(message);
        return true;
    }

    /**
     * Send message to Player or to ConsoleSender
     * @param sender
     * @param s
     * @return — always returns true.
     */
    public boolean print (CommandSender sender, Object... s){
        if (sender == null) return Message.LNG_PRINT_FAIL.log(this.name());
        sender.sendMessage(getText(s));
        return true;
    }

    /**
     * Send message to all players or to players with defined permission
     * @param permission
     * @param s
     * @return — always returns true.
     *
     * Examples:
     * Message.MSG_BROADCAST.broadcast ("pluginname.broadcast"); // send message to all players with permission "pluginname.broadcast"
     * Message.MSG_BROADCAST.broadcast (null); // send message to all players
     */
    public boolean broadcast (String permission, Object... s){
        for (Player player : plugin.getServer().getOnlinePlayers().values()){
            if (permission==null || player.hasPermission(permission)) print (player,s);
        }
        return true;
    }


    /**
     * Get formated text.
     * @param keys
     *
     ** Keys - are parameters for message and control-codes.
     * Parameters will be shown in position in original message according for position.
     * This keys are used in every method that prints or sends message.
     *
     * Example:
     *
     * EXAMPLE_MESSAGE ("Message with parameters: %1%, %2% and %3%");
     * Message.EXAMPLE_MESSAGE.getText("one","two","three"); //will return text "Message with parameters: one, two and three"
     *
     ** Color codes
     * You can use two colors to define color of message, just use character symbol related for color.
     *
     * Message.EXAMPLE_MESSAGE.getText("one","two","three",'c','4');  // this message will be red, but word one, two, three - dark red
     *
     ** Control codes
     * Control codes are text parameteres, that will be ignored and don't shown as ordinary parameter
     * - "SKIPCOLOR" - use this to disable colorizing of parameters
     * - "NOCOLOR" (or "NOCOLORS") - return uncolored text, clear all colors in text
     * - "FULLFLOAT" - show full float number, by default it limit by two symbols after point (0.15 instead of 0.1483294829)
     *
     * @return
     */
    public String getText (Object... keys){
        char [] colors = new char[]{color1 == null ? c1 : color1 , color2 == null ? c2 : color2};
        if (keys.length ==0) return TextFormat.colorize("&"+ colors[0] +this.message);
        String str = this.message;
        boolean noColors = false;
        boolean skipDefaultColors = false;
        boolean fullFloat = false;
        int count=1;
        int c = 0;
        DecimalFormat fmt = new DecimalFormat("####0.##");
        for (int i = 0; i<keys.length; i++){
            String s = keys[i].toString();
            if (c<2&&keys[i] instanceof Character){
                colors[c] = (Character) keys[i];
                c++;
                continue;
            } else if (s.equals("SKIPCOLOR")) {
                skipDefaultColors = true;
                continue;
            } else if (s.equals("NOCOLORS")||s.equals("NOCOLOR")) {
                noColors = true;
                continue;
            } else if (s.equals("FULLFLOAT")) {
                fullFloat = true;
                continue;
            } else if (keys[i] instanceof Location) {
                Location loc = (Location) keys[i];
                if (fullFloat) s = loc.getLevel().getName()+"["+loc.getX()+", "+loc.getY()+", "+loc.getZ()+"]";
                else s = loc.getLevel().getName()+"["+fmt.format(loc.getX())+", "+fmt.format(loc.getY())+", "+fmt.format(loc.getZ())+"]";
            } else if (keys[i] instanceof Double || keys[i] instanceof Float) {
                if (!fullFloat) s = fmt.format(keys[i]);
            }

            String from = (new StringBuilder("%").append(count).append("%")).toString();
            String to = skipDefaultColors ? s :(new StringBuilder("&").append(colors[1]).append(s).append("&").append(colors[0])).toString();
            str = str.replace(from, to);
            count++;
        }
        str = TextFormat.colorize("&"+colors[0]+str);
        if (noColors) str = TextFormat.clean(str);
        return str;
    }

    private void initMessage (String message){
        this.message = message;
    }

    private String message;
    private Character color1;
    private Character color2;
    Message (String msg){
        message = msg;
        this.color1 = null;
        this.color2 = null;
    }
    Message (String msg, char color1, char color2){
        this.message = msg;
        this.color1 = color1;
        this.color2 = color2;
    }
    Message (String msg, char color){
        this (msg,color,color);
    }

    @Override
    public String toString(){
        return this.getText("NOCOLOR");
    }

    /**
     * Initialize current class, load messages, etc.
     * Call this file in onEnable method after initializing plugin configuration
     * @param plg
     */
    public static void init(PluginBase plg){
        plugin = plg;
        language = Welcome.getCfg().language;
        if (language.equalsIgnoreCase("default")) language = Server.getInstance().getLanguage().getLang();
        else if (language.length() > 3) language= language.substring(0,3);
        debugMode = Welcome.getCfg().debugMode;
        initMessages();
        saveMessages();
        LNG_CONFIG.debug(Message.values().length,language,true,debugMode);
    }

    /**
     * Enable debugMode
     * @param debug
     */
    public static void setDebugMode (boolean debug){
        debugMode = debug;
    }


    private static void initMessages(){
        File f = new File (plugin.getDataFolder()+File.separator+language+".lng");
        if (!f.delete()){
            System.gc();
            f.delete();
        }
        plugin.saveResource("lang/" +language+".lng",language+".lng",true);
        Config lng = new Config(f,Config.YAML);
        /* Reserved for future API update ;)
        InputStream is = plugin.getClass().getResourceAsStream("/lang/"+language+".lng");
        lng.load(is);
        */

        for (Message key : Message.values())
            key.initMessage(lng.getString(key.name().toLowerCase(), key.message));
    }

    private static void saveMessages(){
        File f = new File (plugin.getDataFolder()+File.separator+language+".lng");
        Config lng = new Config(f,Config.YAML);
        for (Message key : Message.values())
            lng.set(key.name().toLowerCase(), key.message);
        try {
            lng.save();
        } catch (Exception e){
            LNG_SAVE_FAIL.log();
            if (debugMode) e.printStackTrace();
        }
    }

    /**
     * Send message (formed using join method) to server log if debug mode is enabled
     * @param s
     */
    public static boolean debugMessage (Object... s){
        if (debugMode) plugin.getLogger().info(TextFormat.clean(join (s)));
        return true;
    }

    /**
     * Join object array to string (separated by space)
     * @param s
     */
    public static String join (Object... s){
        StringBuilder sb = new StringBuilder();
        for (Object o : s){
            if (sb.length()>0) sb.append(" ");
            sb.append(o.toString());
        }
        return sb.toString();
    }
}