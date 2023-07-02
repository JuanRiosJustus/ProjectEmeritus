package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StringUtils {


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
    public static boolean isNumber(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (Character.isAlphabetic(text.charAt(i))) {
                return false;
            }
        }
        return true;
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
            sb.append(sb.length() > 0 ? " " : "").append(word);
        }
        if (sb.length() > 0) { ret.add(sb.toString()); }

        return ret;
    }

    public static String capitalize(String value) {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }
}
