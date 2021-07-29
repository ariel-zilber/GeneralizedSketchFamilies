package utils;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public class HashingUtils {
    private static Random rand = new Random();

    public static Random getSeed() {
        return rand;
    }

    /**
     * A hash function that maps the string value to int value
     */
    public static int FNVHash1(String data) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < data.length(); i++)
            hash = (hash ^ data.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        return hash;
    }

    /**
     * FNVHash fucntion that takes two keys.
     */
    public static int FNVHash1(long key1, long key2) {
        return FNVHash1((key1 << 32) | key2);
    }

    /**
     * FNVHash function
     **/
    public static int FNVHash1(long key) {
        key = (~key) + (key << 18);
        key = key ^ (key >>> 31);
        key = key * 21;
        key = key ^ (key >>> 11);
        key = key + (key << 6);
        key = key ^ (key >>> 22);
        return (int) key;
    }

    /**
     * Thomas Wang hash
     */
    public static int intHash(int key) {
        key += ~(key << 15);
        key ^= (key >>> 10);
        key += (key << 3);
        key ^= (key >>> 6);
        key += ~(key << 11);
        key ^= (key >>> 16);
        return key;
    }
}