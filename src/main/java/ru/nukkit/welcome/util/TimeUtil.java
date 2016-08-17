package ru.nukkit.welcome.util;

public class TimeUtil {

    public static Long timeToTicks(Long time) {
        //1000 ms = 20 ticks
        return Math.max(1, (time / 50));
    }

    public static Long parseTime(String time) {
        int hh = 0; // часы
        int mm = 0; // минуты
        int ss = 0; // секунды
        int tt = 0; // тики
        int ms = 0; // миллисекунды
        if (time.matches("\\d+")) {
            ss = Integer.parseInt(time);
        } else if (time.matches("^[0-5][0-9]:[0-5][0-9]$")) {
            String[] ln = time.split(":");
            mm = Integer.parseInt(ln[0]);
            ss = Integer.parseInt(ln[1]);
        } else if (time.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$")) {
            String[] ln = time.split(":");
            hh = Integer.parseInt(ln[0]);
            mm = Integer.parseInt(ln[1]);
            ss = Integer.parseInt(ln[2]);
        } else if (time.matches("^\\d+ms")) {
            ms = Integer.parseInt(time.replace("ms", ""));
        } else if (time.matches("^\\d+h")) {
            hh = Integer.parseInt(time.replace("h", ""));
        } else if (time.matches("^\\d+m$")) {
            mm = Integer.parseInt(time.replace("m", ""));
        } else if (time.matches("^\\d+s$")) {
            ss = Integer.parseInt(time.replace("s", ""));
        } else if (time.matches("^\\d+t$")) {
            tt = Integer.parseInt(time.replace("t", ""));
        }
        return (hh * 3600000L) + (mm * 60000L) + (ss * 1000L) + (tt * 50L) + ms;
    }
}
