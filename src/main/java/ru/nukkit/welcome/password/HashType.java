package ru.nukkit.welcome.password;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public enum HashType {
    PLAIN ("PLAIN"),
    MD5 ("MD5"),
    SHA1 ("SHA-1"),
    SHA256 ("SHA-256"),
    SHA512 ("SHA-512");

    private String algStr;

    HashType(String algStr){
        this.algStr = algStr;
    }
    public String getHash(String str){
        if (this==PLAIN) return str;
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(this.algStr);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] byteData = new byte[0];
        try {
            byteData = digest.digest(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static String getHash (String password, HashType algorithmType){
        return algorithmType.getHash(password);
    }

    public static HashType getAlgorithm(String id){
        for (HashType at : HashType.values())
            if (at.name().equalsIgnoreCase(id)||at.algStr.equalsIgnoreCase(id)) return at;
        return HashType.PLAIN;
    }
}
