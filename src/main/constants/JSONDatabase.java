package main.constants;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONDatabase {
    private final Pattern mPattern = Pattern.compile("(?i)\\bFROM\\s+([a-zA-Z_][a-zA-Z0-9_]*)");
    private final Map<String, JSONTable> mTables = new HashMap<>();

    private final LRUCache<String, JSONArray> mCached = new LRUCache<>(5);

    public JSONDatabase() { }
    public JSONDatabase(Map<String, String> tableData) {
        for (Map.Entry<String, String> entry : tableData.entrySet()) {
            mTables.put(entry.getKey(), new JSONTable(entry.getValue()));
        }
    }
    public void addTable(String tableName, String tableData) {
        mTables.put(tableName.trim().toLowerCase(Locale.ROOT), new JSONTable(tableData));
    }

    public String getQueryTable(String query) {
        Matcher matcher = mPattern.matcher(query);
        if (!matcher.find()) { return null; }
        String tableName = matcher.group(1);
        System.out.println("Table name: " + tableName);
        return tableName;
    }

    public JSONArray executeQuery(String sql, Object... values) {
        String formattedQuery = customFormat(sql, values);
        String tableName = getQueryTable(formattedQuery);
        JSONTable table = mTables.get(tableName);
        JSONArray query = table.executeQuery(formattedQuery);
        return query;
    }

    public JSONArray executeQuery(String sql) {
        String tableName = getQueryTable(sql);
        JSONTable table = mTables.get(tableName);
        JSONArray query = table.executeQuery(sql);
        return query;
    }

    /**
     * Replaces all occurrences of "{}" in the format string with the corresponding value
     * from the values array. If there are more placeholders than values, the extra placeholders
     * are left as-is.
     *
     * @param format the string containing "{}" placeholders
     * @param values the values to substitute into the placeholders
     * @return a new string with placeholders replaced by the corresponding values
     */
    public static String customFormat(String format, Object... values) {
        if (format == null || values == null) {
            return format;
        }

        StringBuilder result = new StringBuilder();
        int startIndex = 0;
        int valueIndex = 0;
        int placeholderIndex;

        // Loop until no more placeholders are found.
        while ((placeholderIndex = format.indexOf("{}", startIndex)) != -1) {
            // Append text before the placeholder.
            result.append(format, startIndex, placeholderIndex);

            // Append the next value if available.
            if (valueIndex < values.length) {
                Object replacement = values[valueIndex++];
                result.append(replacement == null ? "null" : replacement.toString());
            } else {
                // If there are no more values, leave the placeholder unchanged.
                result.append("{}");
            }

            // Move past the current placeholder.
            startIndex = placeholderIndex + 2;
        }

        // Append any remaining text after the last placeholder.
        result.append(format.substring(startIndex));

        return result.toString();
    }
}
