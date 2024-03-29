package main.utils;

import java.util.SplittableRandom;

public class MathUtils {

    private static final SplittableRandom random = new SplittableRandom();

    public static boolean passesChanceOutOf100(float successChance) {
        if (successChance >= 1) {
            return true;
        } else {
            return successChance > random.nextFloat();
        }
    }
    public static String floatToPercent(float value) {
        return (int)(value * 100) + "%";
    }

    public static float map(float inVal, float inMin, float inMax, float outMin, float outMax) {
        return (inVal - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }

    public static int diff(int row1, int row2) {
        return Math.abs(Math.max(row1, row2) - Math.min(row1, row2));
    }

    public static float getNthPercentageOf(float nth, float of) {
        return (of / 100) * nth;
    }
}
