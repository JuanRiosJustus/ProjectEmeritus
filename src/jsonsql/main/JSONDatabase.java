package jsonsql.main;

import com.alibaba.fastjson2.JSONArray;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONDatabase {
    private final Map<String, JSONTable> mTables = new HashMap<>();
    private final JSONSQLFunctions mSqlFunctions = new JSONSQLFunctions();

    public JSONDatabase() { }
    public JSONDatabase(Map<String, String> tableData) {
        for (Map.Entry<String, String> entry : tableData.entrySet()) {
            String tableName = entry.getKey().trim().toLowerCase(Locale.ROOT);
            String tableRows = entry.getValue();
            JSONTable table = new JSONTable(tableName, tableRows);
            mTables.put(tableName, table);
        }
    }
    public void addTable(String tableName, String tableData) {
        String name = tableName.trim().toLowerCase(Locale.ROOT);
        mTables.put(name, new JSONTable(name, tableData));
    }

    public void addTable(String tableName) {
        String name = tableName.trim().toLowerCase(Locale.ROOT);
        mTables.put(name, new JSONTable(name));
    }


    private final Pattern mTablePattern = Pattern.compile(
            "(?i)(FROM|INTO|UPDATE)\\s+([a-zA-Z_][a-zA-Z0-9_]*)"
    );

    public String getQueryTable(String query) {
        Matcher matcher = mTablePattern.matcher(query);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(2); // Table name is always the second group
    }

    public JSONArray execute(String sql) {
        String queryType = mSqlFunctions.getQueryType(sql);
        String tableName = getQueryTable(sql);
        JSONTable table = mTables.get(tableName);

        JSONArray result = null;
        switch (queryType) {
            case "SELECT" -> { result = table.select(sql); }
            case "INSERT" -> { result = table.insert(sql); }
            case "UPDATE" -> { result = table.update(sql); }
            case "DELETE" -> { result = table.delete(sql); }
            default -> throw new IllegalArgumentException("Unsupported or unknown query type.");
        };

        return result;
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
