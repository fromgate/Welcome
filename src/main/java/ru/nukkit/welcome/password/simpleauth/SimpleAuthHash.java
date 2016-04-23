package ru.nukkit.welcome.password.simpleauth;

import java.security.MessageDigest;

public class SimpleAuthHash {

    public static String getHash(String userName, String password){
        byte[] hashSha = sha512(password+userName);
        byte[] hashWhpl = whirlpool(userName+password);
        if (hashSha==null) return null;
        byte[] result = xor(hashSha,hashWhpl);
        if (result == null) return null;
        return Whirlpool.display(result);
    }

    public static byte[] xor (byte [] arr1, byte [] arr2){
        if (arr1.length!=arr2.length) return null;
        byte [] rst = new byte[arr1.length];
        for (int i = 0; i<arr1.length;i++)
            rst [i] = (byte) (arr1[i]^arr2[i]);
        return  rst;
    }

    public static byte[] sha512 (String toHash) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            return digest.digest(toHash.getBytes("UTF-8"));
        } catch (Exception e){
        }
        return  null;
    }

    public static byte[] whirlpool (String toHash) {
        Whirlpool w = new Whirlpool();
        byte[] digest = new byte[64];
        w.NESSIEinit();
        w.NESSIEadd(toHash);
        w.NESSIEfinalize(digest);
        return digest;
    }
}
