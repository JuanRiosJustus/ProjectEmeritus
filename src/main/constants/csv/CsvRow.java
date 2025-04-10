package main.constants.csv;

import java.util.*;
import java.util.stream.Collectors;

public class CsvRow extends HashMap<String, String> {
    private final char mDelimiterChar;
    private final String mDelimiterString;
    private final String mEquivalenceChar = "=";

    public CsvRow() { this(','); }

    public CsvRow(char delimiter) {
        mDelimiterChar = delimiter;
        mDelimiterString = String.valueOf(mDelimiterChar);
    }

    public List<String> getList(String column) {
        String[] value = get(column)
                .trim()
                .split(String.valueOf(mDelimiterChar));
        return new ArrayList<>(List.of(value));
    }

    public float getNumber(String column) { return Float.parseFloat(get(column)); }
    public int getInt(String column) { return Integer.parseInt(get(column)); }
    public String getString(String column) { return get(column); }
    public List<String> getColumnsLike(String like) {
        return keySet()
                .stream()
                .filter(s -> s.contains(like))
                .toList();
    }
    public Map<String, Float> getNumberMap(String column) {
        String data = getString(column);
        if (data.isBlank()) { return new HashMap<>(); }
        return Arrays.stream(data.split(mDelimiterString))
                .map(s -> s.split(mEquivalenceChar))
                .collect(Collectors.toMap(e -> e[0], e -> Float.parseFloat(e[1])));
    }

    public Map<String, String> getStringMap(String column) {
        String data = getString(column);
        if (data.isBlank()) { return new HashMap<>(); }
        return Arrays.stream(data.split(mDelimiterString))
                .map(s -> s.split(mEquivalenceChar))
                .collect(Collectors.toMap(e -> e[0], e -> e[1]));
    }
}
