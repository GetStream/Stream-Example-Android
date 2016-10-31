package io.getstream.example.utils;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;

import io.getstream.example.R;

public class Gravatar {

    public static String md5(String s)
    {
        MessageDigest digest;
        try
        {
            digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes(Charset.forName("US-ASCII")),0,s.length());
            byte[] magnitude = digest.digest();
            BigInteger bi = new BigInteger(1, magnitude);
            String hash = String.format("%0" + (magnitude.length << 1) + "x", bi);
            return hash;
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public static int pickRandomAnimalAvatar() {
        int[] i = new int[]{
                R.drawable.alligator,
                R.drawable.bear,
                R.drawable.beaver,
                R.drawable.cheetah,
                R.drawable.cow,
                R.drawable.deer};
        int rnd = new Random().nextInt(i.length);
        return i[rnd];
    }
}
