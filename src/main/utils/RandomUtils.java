package main.utils;

import java.util.*;

import main.constants.Direction;

public class RandomUtils {

    private static List<Direction> directions = new ArrayList<>();
    private static final Random random = new Random();
    private static final String ALPH_CAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPH_LOWS = "abcdefghijklmnopqrstuvwxyz";
    private static final String ALPH_VOWLS = "aeiouy";
    private static final String NUMS = "1234567890";
    public static final String LOREMIPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit...";

    private RandomUtils() {}

    public static String createRandomName(int min, int max) {
        StringBuilder sb = new StringBuilder();
        sb.append(ALPH_CAPS.charAt(random.nextInt(ALPH_CAPS.length())));
        int length = random.nextInt(max - min) + min;

        for(int i = 0; i < length; ++i) {
            int s = random.nextBoolean() ? 3 : 2;
            if (i % s == 0) {
                sb.append(ALPH_VOWLS.charAt(random.nextInt(ALPH_VOWLS.length())));
            } else {
                sb.append(ALPH_LOWS.charAt(random.nextInt(ALPH_LOWS.length())));
            }
        }

        return sb.toString();
    }
    public static Integer getRandomNumberBetween(int min, int max) {
        StringBuilder sb = new StringBuilder();
        int result = random.nextInt(max - min) + min;
        sb.append(result);

        return Integer.valueOf(sb.toString());
    }

    public static String getRandomFrom(String[] args) {
        return args[random.nextInt(args.length)];
    }
    public static int getRandomFrom(int[] args) {
        return args[random.nextInt(args.length)];
    }
}