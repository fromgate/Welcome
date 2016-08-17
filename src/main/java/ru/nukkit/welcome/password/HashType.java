package ru.nukkit.welcome.password;

import ru.nukkit.welcome.password.simpleauth.SimpleAuthHash;
import ru.nukkit.welcome.password.simpleauth.Whirlpool;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public enum HashType {
    PLAIN("PLAIN"),
    MD5("MD5"),
    SHA1("SHA-1"),
    SHA256("SHA-256"),
    SHA512("SHA-512"),
    SIMPLEAUTH("SIMPLEAUTH"),
    WHIRLPOOL("WHIRLPOOL");

    private String algStr;

    HashType(String algStr) {
        this.algStr = algStr;
    }

    public String getHash(String salt, String str) {
        switch (this) {
            case PLAIN:
                return str;
            case MD5:
            case SHA1:
            case SHA256:
            case SHA512:
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

            case WHIRLPOOL:
                return Whirlpool.display(SimpleAuthHash.whirlpool(str));
            case SIMPLEAUTH:
                return SimpleAuthHash.getHash(salt, str);
        }
        return str;
    }

    public static String getHash(String userName, String password, HashType algorithmType) {
        return algorithmType.getHash(userName, password);
    }

    public static HashType getAlgorithm(String id) {
        for (HashType at : HashType.values())
            if (at.name().equalsIgnoreCase(id) || at.algStr.equalsIgnoreCase(id)) return at;
        return HashType.PLAIN;
    }
}
