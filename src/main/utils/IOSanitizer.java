package main.utils;

import main.constants.Constants;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class IOSanitizer {

    public static Map<String, Float> parseKeyValueMap(String entry) throws Exception {
        if (entry == null || entry.isBlank()) { return new HashMap<>(); }

        String keyValueSignifier = "=";
        String keyValueDelimiter = ";";

        Map<String, Float> map = new HashMap<>();
        String[] entries = entry.split(keyValueDelimiter);
        for (String scalingEntry : entries) {
            if (scalingEntry.split(keyValueSignifier).length != 2) {
                String message = MessageFormat.format("Incorrect Key-Value pairing {0} for {1}. Should be "
                        + System.lineSeparator() + " in the form of <MapKey>=<MapValue>", scalingEntry, entry);
            }
            String key = scalingEntry.split(keyValueSignifier)[0].strip();
            String value = scalingEntry.split(keyValueSignifier)[1].strip();
            map.put(key, parseFloat(value));
        }
        return map;
    }

    public static float parseFloat(String toParse) {
        return (toParse == null || toParse.isBlank() ? 0 : Float.parseFloat(toParse.strip()));
    }

    public static int parseInt(String toParse) {
        return (toParse == null || toParse.isBlank() ? 0 : Integer.parseInt(toParse.strip()));
    }

    public static String parseString(String toParse) {
        return (toParse == null || toParse.isBlank() ? "" : toParse);
    }

    public static boolean parseBoolean(String toParse) {
        return (toParse == null || toParse.isBlank() ? false : Boolean.parseBoolean(toParse));
    }
}
