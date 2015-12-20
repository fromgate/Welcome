package ru.nukkit.welcome.password;

import ru.nukkit.welcome.util.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator{
    private static String pattern = null;
    private static String msgValidPwd = null;

    public static void init (boolean useChar, boolean useCapitals, boolean forceNumber, int minLength, int maxLength){
        StringBuilder patternBuilder = new StringBuilder("(?=.*[a-z])");
        if (useChar) patternBuilder.append("(?=.*[@#$%^&+=])");
        if (useCapitals) patternBuilder.append("(?=.*[A-Z])");
        if (forceNumber) patternBuilder.append("(?=.*[0-9])");
        patternBuilder.append("(?=\\S+$).{" + minLength + "," + maxLength + "}");
        pattern = patternBuilder.toString();
        Message.PWD_VALID_PATTERN.log(pattern);
        List<Message> req = new ArrayList<Message>();
        req.add(useCapitals ? Message.VLD_CAPITAL : Message.VLD_LETTERS);
        if (useChar) req.add(Message.VLD_SPEC_CHR);
        if (forceNumber) req.add(Message.VLD_NUMBER);
        StringBuilder sb = new StringBuilder();
        for (Message m : req){
            if (sb.length()>0) sb.append(", ");
            sb.append(m.getText("NOCOLOR"));
        }
        msgValidPwd = Message.PWD_VALID_INFO.getText(sb.toString(),minLength,maxLength);
    }

    public static boolean validatePassword(final String password){
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(password);
        return m.matches();
    }

    public static String getInfo (){
        return msgValidPwd;
    }
}