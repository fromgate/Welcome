package ru.nukkit.welcome.util;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Location;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

import java.io.*;
import java.text.DecimalFormat;


public enum Message {

	//Default (lang) messages
	LNG_LOAD_FAIL ("Failed to load languages from file. Default message used"),
	LNG_SAVE_FAIL ("Failed to save lang file"),
	LNG_PRINT_FAIL ("Failed to print message %1%. Sender object is null."),
	LNG_CONFIG ("[MESSAGES] Messages: %1% Language: %2% Save translate file: %1% Debug mode: %3%"),
	WORD_UNKNOWN ("Unknown"), CMD_REG_DESC("Register player on server"),
	TYPE_LGN("Type /login <password> to login!"),
	TYPE_REG ("Type /register <password> <password> to login!"),
	KICK_TIMEOUT ("Time out! Next time type /login <password> to join the game!"),
	LGN_ALREADY("You're already logged in!"),
	REG_ALREADY("You're already registerd!"),
	LGN_MISS_PWD("Missed password! Type /login <password>"),
	UNREG_MISS_PWD("Missed password! Type /unreg <password>"),
	ERR_PWD_WRONG("Wrong password!"),
	LGN_OK("You successfully logged-in! Welcome!"),
	REG_OK("You successfully registered! Welcome!"),
	ERR_PWD_NOTMATCH("Entered passwords are not match!"),
	CMD_LGN_DESC("Login command"),
	REG_LOG("Player %1% registered!"),
	LGN_LOG("Player %1% logged-in!"),
	UNREG_OK("You was removed from server. Next time you must register again!"),
	CMD_UNREG_DESC("Unregister from server"),
	CMD_RMV_DESC("Remove (unregister) provided player"),
	RMV_NEED_PLAYER("Player name was not provided (/welcome remove <player>)"),
	RMV_FAIL("Failed to remove player: %1%"),
	RMV_OK("Player %1% removed!"),
	LGN_AUTO("Welcome back, %1%!"),
	CPW_DESC("Change password"),
	CPW_USAGE("/changepassword <OldPassword> <NewPassword> <NewPassword>"),
	ERR_NOT_LOGGED("You're not logged in! Operation declined."),
	CPW_OK("Your password was successfully changed"),
	CMD_HELP_DESC ("Show help"),
	HLP_TITLE("%1% | Nukkit authorization system"),
	CMD_WLC_DESC("Welcome plugin command"),
	ERR_PWD_VALIDATE("Your password is not valid!"),
	PWD_VALID_INFO ("Password must contain: %1% Length: %2%-%3%"),
	VLD_CAPITAL("capital letters"),
	VLD_LETTERS("letters"),
	VLD_SPEC_CHR("special chars"),
	VLD_NUMBER("numbers"), PWD_VALID_PATTERN("Password validator regex prepared: %1%");

	private static PluginBase plugin = null;
	private static boolean debugMode = false;
	private static String language = "english";
	//private static boolean languageSave=false;
	private static char c1 = 'a';
	private static char c2 = '2';



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
	 * @return вЂ” always returns true.
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
	 * @return
	 */
	public boolean debug(Object... s){
		if (debugMode) plugin.getLogger().info(TextFormat.clean(getText (s)));
		return true;
	}

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
	 * @return
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
	 * @return
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
	 * @return
	 */
	public String getText (Object... keys){
		if (keys.length ==0) return TextFormat.colorize("&"+c1+this.message);
		String str = this.message;
		boolean noColors = false;
		boolean skipDefaultColors = false;
		boolean fullFloat = false;
		int count=1;
		char [] colors = new char[]{c1,c2};
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
				if (!fullFloat) s = fmt.format((Double) keys[i]);
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

	public void initMessage (String message){
		this.message = message;
	}

	private String message;
	Message (String msg){
		message = msg;
	}


	///////////////////////////////////////////////////////////////////////////////////////////////
	public static void init(PluginBase plg){
		plugin = plg;
		language = plg.getConfig().get("general.language","english");
		plg.getConfig().set("general.language", language);
		debugMode = plg.getConfig().get("general.debug-mode",false);
		plg.getConfig().set("general.debug-mode",debugMode);
		plg.saveConfig();
		initMessages();
		saveMessages();
		LNG_CONFIG.debug(Message.values().length,language,true,debugMode);
	}

	public static void setDebugMode (boolean debug){
		debugMode = debug;
	}

	private static boolean copyLanguage(){
		File f = new File(plugin.getDataFolder(),language+".lng");
		return plugin.saveResource("lang/" +language+".lng",false,f);
	}

	private static void initMessages(){
		copyLanguage();

		Config lng = null;
		try {
			File f = new File (plugin.getDataFolder()+File.separator+language+".lng");
			lng = new Config(f,Config.YAML);
		} catch (Exception e){
			LNG_LOAD_FAIL.log();
			if (debugMode) e.printStackTrace();
			return;
		}
		for (Message key : Message.values())
			key.initMessage((String) lng.get(key.name().toLowerCase(), key.message));
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
			return;
		}
	}

	public static boolean debugMessage (Object... s){
		if (debugMode) plugin.getLogger().info(TextFormat.clean(join (s)));
		return true;

	}

	public static String join (Object... s){
		StringBuilder sb = new StringBuilder();
		for (Object o : s){
			if (sb.length()>0) sb.append(" ");
			sb.append(o.toString());
		}
		return sb.toString();
	}
}