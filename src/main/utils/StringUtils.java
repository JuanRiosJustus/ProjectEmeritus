package main.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StringUtils {

    private static String beautifyPercentage(double value) {
        if (value == 0) {
            return String.valueOf(0);
        } else if (value <= 1) {
            double percentage = value * 100;
            return String.format("%.0f%%", percentage);
        } else {
            return String.format("%.0f", value);
        }
    }

    public static String spaceFillers(int num, int total) {
        StringBuilder sb = new StringBuilder();
        sb.append(num);
        if (sb.length() < total) {
            sb.insert(0, " ".repeat(total - sb.length()));
        }
        return sb.toString();
    }

    public static int getCharacterCount(String txt, char c) {
        int count = 0;
        for (int i = 0; i < txt.length(); i++) {
            char current = txt.charAt(i);
            if (current == c) { count++; }
        }
        return count;
    }
    public static boolean containsNonDigits(String text) {
        for (char c : text.toCharArray()) {
            if (Character.isDigit(c)) { continue; }
            return true;
        }
        return false;
    }
    public static boolean isNumber(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (Character.isAlphabetic(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String valueToPercentOrInteger(float value) {
        boolean isSmall = value >= 0 && value <= 1;
        int closestValue = (int)Math.ceil(value);
        if (isSmall && closestValue != value) {
            String strVal = String.valueOf(value * 100f);
            return strVal.substring(0, strVal.indexOf(".")) + "%";
        }
        return String.valueOf(value);
    }

    public static char getMostFrequentNonAlphaNumericCharacter(String text) {
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < text.length(); i++) {
            if (Character.isAlphabetic(text.charAt(i))) { continue; }
            if (Character.isDigit(text.charAt(i))) { continue; }
            if (Character.isWhitespace(text.charAt(i))) { continue; }
            map.put(text.charAt(i), map.getOrDefault(text.charAt(i), 0) + 1);
        }
        int count = -1;
        char chara = ' ';
        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            if (entry.getValue() >= count) {
                chara = entry.getKey();
                count = entry.getValue();
            }
        }
        return chara;
    }

//    public static List<String> splitEqually(String text, int size) {
//        // Give the list the right capacity to start with.
//        List<String> ret = new ArrayList<>((text.length() + size - 1) / size);
//
//        for (int start = 0; start < text.length(); start += size) {
//            String substring = text.substring(start, Math.min(text.length(), start + size));
//            ret.add(substring);
//        }
//        return ret;
//    }

    public static ArrayList<String> splitWordsIntoLines(String text, int lineLength) {
        String[] words = text.split(" ");
        ArrayList<String> ret = new ArrayList<>();
        if (words.length == 1) { ret.add(words[0]); return ret; }
        StringBuilder sb = new StringBuilder();

        for (String word : words) {
            if (sb.length() + word.length() > lineLength) {
                ret.add(sb.toString());
                sb.delete(0, sb.length());
            }
            sb.append(!sb.isEmpty() ? " " : "").append(word);
        }
        if (!sb.isEmpty()) { ret.add(sb.toString()); }

        return ret;
    }

    public static String capitalize(String value) {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    public static String spaceByCapitalization(String value) {
        StringBuilder sb = new StringBuilder(value);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        for (int index = 0; index < sb.length(); index++) {
            if (index == 0) { continue; }
            if (!Character.isUpperCase(sb.charAt(index))) { continue; }
            sb.insert(index, " ");
            index++;
        }
        return sb.toString();
    }
}
