package main.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StringUtils {

    private final static String EMPTY_STRING = "";

    /**
     * Converts a snake_case string to a capitalized format with spaces.
     * Example: "snake_case_example" -> "Snake Case Example"
     *
     * @param snakeCaseString The input string in snake_case format.
     * @return The converted string in capitalized format.
     */
    public static String convertSnakeCaseToCapitalized(String snakeCaseString) {
        if (snakeCaseString == null || snakeCaseString.isEmpty()) {
            return "";
        }

        // Split the string by underscores
        String[] words = snakeCaseString.split("_");

        // Capitalize each word and join them with spaces
        StringBuilder capitalized = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                capitalized.append(Character.toUpperCase(word.charAt(0))) // Capitalize the first letter
                        .append(word.substring(1).toLowerCase()) // Append the rest in lowercase
                        .append(" "); // Add a space
            }
        }

        // Remove the trailing space and return the result
        return capitalized.toString().trim();
    }

    /**
     * Splits a string into multiple lines, ensuring that words are not split.
     *
     * @param text       The input string with no newlines.
     * @param lineLength The maximum number of characters per line.
     * @return The formatted string with newline characters.
     */
    public static String wrapText(String text, int lineLength) {
        if (text == null || lineLength <= 0) {
            throw new IllegalArgumentException("Text cannot be null and lineLength must be greater than 0.");
        }

        StringBuilder result = new StringBuilder();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            // Check if adding the word exceeds the line length
            if (currentLine.length() + word.length() > lineLength) {
                // If the current line isn't empty, append it to the result
                if (!currentLine.isEmpty()) {
                    result.append(currentLine.toString().stripTrailing()).append("\n");
                    currentLine.setLength(0);
                }
            }

            // Add the word to the current line
            if (!currentLine.isEmpty()) {
                currentLine.append(" ");
            }
            currentLine.append(word);
        }

        // Add any remaining text in the current line
        if (!currentLine.isEmpty()) {
            result.append(currentLine.toString().stripTrailing());
        }

        return result.toString();
    }

    /**
     * Converts a float value to a percentage string without decimal places.
     *
     * @param value the float value to convert (e.g., 0.75 for 75%)
     * @return the formatted percentage string (e.g., "75%")
     */
    public static String floatToPercentage(float value) {
        // Multiply by 100 to convert to percentage and cast to an integer
        int percentage = Math.round(value * 100);

        // Return formatted percentage string
        return percentage + "%";
    }


    public static String getInitials(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        // Add the first character
        result.append(input.charAt(0));

        // Loop through the string to find underscores and the character after them
        for (int i = 0; i < input.length() - 1; i++) {
            if (input.charAt(i) == '_') {
                result.append(input.charAt(i + 1));
            }
        }

        return result.toString();
    }

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

    public static String toEmptyIfNonZero(float value) {
        return (value == 0 ? EMPTY_STRING : String.valueOf((int)value));
    }


    public static String capitalizeFirstAndAfterUnderscores(String input) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            // Always add the first character
            if (i == 0) {
                result.append(Character.toUpperCase(input.charAt(i)));
            }
            // If the current char is '_', and there's a next char
            else if (input.charAt(i) == '_' && i + 1 < input.length()) {
                result.append(Character.toUpperCase(input.charAt(i + 1)));
            }
        }

        return result.toString();
    }
}
