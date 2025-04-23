package jsonsql.main;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONTable {
    protected List<String> mQueryTokens = null;
    protected JSONSQLFunctions mJSONSQLFunctions = null;
    protected JSONArray mTable = null;
    protected String mName = null;


    public JSONTable() {
        mName = "";
        mTable = new JSONArray();
        mQueryTokens = new ArrayList<>();
        mJSONSQLFunctions = new JSONSQLFunctions();
    }

    public JSONTable(String name) {
        mName = name.toLowerCase(Locale.ROOT);
        mTable = new JSONArray();
        mQueryTokens = new ArrayList<>();
        mJSONSQLFunctions = new JSONSQLFunctions();
    }

    public JSONTable(String name, String table) {
        mName = name.toLowerCase(Locale.ROOT);
        mTable = JSON.parseArray(table);
        mQueryTokens = new ArrayList<>();
        mJSONSQLFunctions = new JSONSQLFunctions();
    }


    public JSONObject getSchema() {
        return mJSONSQLFunctions.getMasterSchema(mTable);
    }

    public JSONArray update(String sql) {
        JSONSQLFunctions.BenchmarkLogger logger = new JSONSQLFunctions.BenchmarkLogger("Update");
        final JSONArray result = new JSONArray();

        String tableName = mJSONSQLFunctions.extractTableName(sql);
        if (!tableName.equalsIgnoreCase(mName)) {
            throw new IllegalArgumentException("Incorrect Table Name detected: " + tableName + ", expected: " + mName);
        }

        mQueryTokens.clear();
        List<String> tokens = mJSONSQLFunctions.getTokens(sql);
        mQueryTokens.addAll(tokens);


        // Extract everything between SET and WHERE
        Pattern setPattern = Pattern.compile("(?i)SET\\s+(.*?)\\s+WHERE", Pattern.DOTALL);
        Matcher setMatcher = setPattern.matcher(sql);
        if (!setMatcher.find()) {
            throw new IllegalArgumentException("Missing or malformed SET clause.");
        }

        String setClause = setMatcher.group(1);

        List<String> assignments = mJSONSQLFunctions.smartSplitAssignments(setClause);
        Map<String, Object> updates = mJSONSQLFunctions.extractUpdates(assignments);

        // Extract WHERE clause and parse conditions
        JSONSQLCondition where = mJSONSQLFunctions.extractWhereConditions(mQueryTokens);

        for (int i = 0; i < mTable.size(); i++) {
            JSONObject row = mTable.getJSONObject(i);
            if (!mJSONSQLFunctions.matchesConditions(row, where)) continue;

            for (Map.Entry<String, Object> entry : updates.entrySet()) {
                mJSONSQLFunctions.setAndCreateJSONValue(row, entry.getKey(), entry.getValue());
            }

            result.add(row);
        }

        logger.stop();
        return result;
    }

    public JSONArray insertRaw(String newObject) {
        JSONSQLFunctions.BenchmarkLogger logger = new JSONSQLFunctions.BenchmarkLogger("Insert");
        JSONArray result = new JSONArray();
        try {
            if (newObject == null || newObject.isEmpty()) { return result; }
            JSONObject newRow = JSON.parseObject(newObject);
            mTable.add(newRow);
            result.add(newRow);
        } catch (Exception e) {
            try {
                JSONArray newRows = JSON.parseArray(newObject);
                for (int i = 0; i < newRows.size(); i++) {
                    JSONObject newRow = newRows.getJSONObject(i);
                    mTable.add(newRow);
                    result.add(newRow);
                }
            } catch (Exception ignored) {
            }
        }

        logger.stop();
        return result;
    }

    public JSONArray insert(String sql) {
        JSONSQLFunctions.BenchmarkLogger logger = new JSONSQLFunctions.BenchmarkLogger("Insert");
        final JSONArray result = new JSONArray();

        String tableName = mJSONSQLFunctions.extractTableName(sql);
        if (!tableName.equalsIgnoreCase(mName)) {
            throw new IllegalArgumentException("Incorrect Table Name detected: " + tableName + ", expected: " + mName);
        }


        Pattern pattern = Pattern.compile(
                "(?i)^INSERT\\s+INTO\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s+VALUES\\s*(.*)$",
                Pattern.DOTALL
        );
        Matcher matcher = pattern.matcher(sql.trim());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid INSERT SQL format.");
        }

        String valuesSection = matcher.group(2).trim();

        if (!tableName.equalsIgnoreCase(mName)) {
            throw new IllegalArgumentException("INSERT table name does not match this table instance.");
        }

        // Use stack-based parsing to safely extract JSON blocks inside () even if nested
        List<String> jsonValues = new ArrayList<>();
        int depth = 0;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < valuesSection.length(); i++) {
            char c = valuesSection.charAt(i);

            if (c == '(') {
                if (depth > 0) current.append(c);
                depth++;
            } else if (c == ')') {
                depth--;
                if (depth == 0) {
                    jsonValues.add(current.toString().trim());
                    current.setLength(0);
                } else {
                    current.append(c);
                }
            } else {
                if (depth > 0) {
                    current.append(c);
                }
            }
        }

        for (String jsonText : jsonValues) {
            try {
                JSONObject obj = JSON.parseObject(jsonText);
                mTable.add(obj);
                result.add(obj);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid JSON object: " + jsonText);
            }
        }

        logger.stop();
        return result;
    }

    public JSONArray delete(String sql) {
        JSONSQLFunctions.BenchmarkLogger logger = new JSONSQLFunctions.BenchmarkLogger("delete");
        final JSONArray result = new JSONArray();

        String tableName = mJSONSQLFunctions.extractTableName(sql);
        if (!tableName.equalsIgnoreCase(mName)) {
            throw new IllegalArgumentException("Incorrect Table Name detected: " + tableName + ", expected: " + mName);
        }

        List<String> tokens = mJSONSQLFunctions.getTokens(sql);
        mQueryTokens.clear();
        mQueryTokens.addAll(tokens);

        // Validate query type and table name
        if (!tokens.get(0).equalsIgnoreCase("DELETE") || !tokens.get(1).equalsIgnoreCase("FROM")) {
            throw new IllegalArgumentException("Invalid DELETE query syntax");
        }

        JSONSQLCondition where = mJSONSQLFunctions.extractWhereConditions(tokens);

        // Iterate and collect indexes to delete
        List<Integer> deleteIndexes = new ArrayList<>();
        for (int i = 0; i < mTable.size(); i++) {
            JSONObject row = mTable.getJSONObject(i);
            if (mJSONSQLFunctions.matchesConditions(row, where)) {
                deleteIndexes.add(i);
            }
        }

        // Remove from last index to avoid shifting
        Collections.reverse(deleteIndexes);
        for (int index : deleteIndexes) {
            JSONObject row = mTable.getJSONObject(index);
            mTable.remove(index);
            result.add(row);
        }

        Collections.reverse(result);

        logger.stop();
        return result;
    }


    public JSONArray select(String sql) {
        JSONSQLFunctions.BenchmarkLogger logger = new JSONSQLFunctions.BenchmarkLogger("Select");
        final JSONArray result = new JSONArray();

        String tableName = mJSONSQLFunctions.extractTableName(sql);
        if (!tableName.equalsIgnoreCase(mName)) {
            throw new IllegalArgumentException("Incorrect Table Name detected: " + tableName + ", expected: " + mName);
        }

        mQueryTokens.clear();
        List<String> tokens = mJSONSQLFunctions.getTokens(sql);
        mQueryTokens.addAll(tokens);

        List<String> selectedColumns = mJSONSQLFunctions.extractSelectedColumns(mQueryTokens);
        JSONSQLCondition whereConditions = mJSONSQLFunctions.extractWhereConditions(mQueryTokens);
        List<String[]> orderByColumns = mJSONSQLFunctions.extractOrderBy(mQueryTokens);
        int limit = mJSONSQLFunctions.extractLimit(mQueryTokens);

        JSONArray filteredResults = new JSONArray();


        JSONArray table = mTable;
        for (int index = 0; index < table.size(); index++) {
            JSONObject row = table.getJSONObject(index);
            boolean matchedConditions = mJSONSQLFunctions.matchesConditions(row, whereConditions);
            if (!matchedConditions) { continue; }
            filteredResults.add(row);
        }

        JSONArray sortedResults = mJSONSQLFunctions.sortResults(filteredResults, orderByColumns);

        if (limit > 0 && sortedResults.size() > limit) {
            JSONArray limitedResults = new JSONArray();
            for (int i = 0; i < limit; i++) {
                limitedResults.add(sortedResults.get(i));
            }
            sortedResults = limitedResults;
        }

        // ✅ Fix: Return JSON objects correctly
        JSONArray finalResults = new JSONArray();
        for (int index = 0; index < sortedResults.size(); index++) {
            JSONObject row = sortedResults.getJSONObject(index);
            JSONObject filteredRow = new JSONObject();

            if (selectedColumns.size() == 1 && selectedColumns.getFirst().equals("*")) {
                filteredRow = row;
            } else {
                for (String column : selectedColumns) {
                    Object columnValue = mJSONSQLFunctions.getJsonPathValue(row, column);
                    filteredRow.put(column, columnValue);
                }
            }
            finalResults.add(filteredRow);
        }

        logger.stop();
        return finalResults;
    }


    public boolean persist() { return persist(false); }
    public boolean persist(boolean beautify) {
        boolean result = false;
        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream(mName + ".json"), StandardCharsets.UTF_8);
            if (beautify) {
                writer.write(JSON.toJSONString(mTable, JSONWriter.Feature.PrettyFormat));
            } else {
                writer.write(mTable.toJSONString());
            }
            writer.flush();
        } catch (Exception ex) {
            result = false;
        }
        return result;
    }


    public JSONObject flattenedSchema() {
        normalize();
        JSONObject row = mTable.getJSONObject(0);
        JSONObject flattenedRow = mJSONSQLFunctions.flattenRow(row);
        return flattenedRow;
    }


    public void normalize() { mJSONSQLFunctions.normalizeRows(mTable); }
    public JSONObject get(int i) { return mTable.getJSONObject(i); }
    public int size() { return mTable.size(); }


}