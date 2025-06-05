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

public class JSONTable {
    protected JSONFunctions mFunctions = null;
    protected JSONArray mTable = null;
    private final JSONArray mFlatTable = new JSONArray();
    protected String mName = null;


    public JSONTable() {
        mName = "";
        mTable = new JSONArray();
        mFunctions = new JSONFunctions();
    }

    public JSONTable(String name) {
        mName = name.toLowerCase(Locale.ROOT);
        mTable = new JSONArray();
        mFunctions = new JSONFunctions();
    }

    public JSONTable(String name, String table) {
        mName = name.toLowerCase(Locale.ROOT);
        mTable = JSON.parseArray(table);
        mFunctions = new JSONFunctions();
    }


    public JSONObject getSchema() {
        return mFunctions.getMasterSchema(mTable);
    }

    public JSONArray update(String sql) {
        JSONFunctions.BenchmarkLogger logger = new JSONFunctions.BenchmarkLogger("Update");
        final JSONArray result = new JSONArray();

        String tableName = mFunctions.extractTableName(sql);
        if (!tableName.equalsIgnoreCase(mName)) {
            throw new IllegalArgumentException("Incorrect Table Name detected: " + tableName + ", expected: " + mName);
        }

        List<String> tokens = mFunctions.tokenize(sql);
        int setIndex = mFunctions.getFirstIndexOf(tokens, "SET");
        int whereIndex = mFunctions.getFirstIndexOf(tokens, "WHERE");

        if (setIndex == -1 || whereIndex == -1 || whereIndex <= setIndex + 1) {
            throw new IllegalArgumentException("Missing or malformed SET/WHERE clause.");
        }

        // 🔹 Join tokens between SET and WHERE into a single string
        StringBuilder setBuilder = new StringBuilder();
        for (int i = setIndex + 1; i < whereIndex; i++) {
            if (i > setIndex + 1) { setBuilder.append(" "); }
            setBuilder.append(tokens.get(i));
        }

        List<String> assignments = mFunctions.splitAssignments(setBuilder.toString());
        Map<String, Object> updates = mFunctions.extractUpdates(assignments);
        JSONSQLCondition where = mFunctions.extractWhereConditions(tokens);

        for (int i = 0; i < mTable.size(); i++) {
            JSONObject row = mTable.getJSONObject(i);
            if (!mFunctions.matchesConditions(row, where)) continue;

            for (Map.Entry<String, Object> entry : updates.entrySet()) {
                mFunctions.setAndCreateJSONValue(row, entry.getKey(), entry.getValue());
            }

            result.add(row);
        }

        logger.stop();
        return result;
    }

    public JSONArray insertRaw(String newObject) {
        JSONFunctions.BenchmarkLogger logger = new JSONFunctions.BenchmarkLogger("Insert");
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
        JSONFunctions.BenchmarkLogger logger = new JSONFunctions.BenchmarkLogger("Insert");
        final JSONArray result = new JSONArray();

        List<String> tokens = mFunctions.tokenize(sql);
        if (tokens.size() < 4 || !tokens.get(0).equalsIgnoreCase("INSERT") || !tokens.get(1).equalsIgnoreCase("INTO")) {
            throw new IllegalArgumentException("Malformed INSERT query. Expected: INSERT INTO <table> VALUES (...)");
        }

        String tableName = tokens.get(2);
        if (!tableName.equalsIgnoreCase(mName)) {
            throw new IllegalArgumentException("Incorrect table name: " + tableName + ", expected: " + mName);
        }

        int valuesIndex = mFunctions.getFirstIndexOf(tokens, "VALUES");

        if (valuesIndex == -1 || valuesIndex + 1 >= tokens.size()) {
            throw new IllegalArgumentException("INSERT missing VALUES section.");
        }

        // ✅ Recover everything after VALUES as a string
        StringBuilder valuesPart = new StringBuilder();
        for (int i = valuesIndex + 1; i < tokens.size(); i++) {
            valuesPart.append(tokens.get(i)).append(" ");
        }

        String valuesSection = valuesPart.toString().trim();

        // ✅ Parse blocks inside ( ... )
        List<String> jsonValues = mFunctions.extractJsonValueBlocks(valuesSection);

        // ✅ Parse and add each object
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
        JSONFunctions.BenchmarkLogger logger = new JSONFunctions.BenchmarkLogger("delete");
        final JSONArray result = new JSONArray();

        String tableName = mFunctions.extractTableName(sql);
        if (!tableName.equalsIgnoreCase(mName)) {
            throw new IllegalArgumentException("Incorrect Table Name detected: " + tableName + ", expected: " + mName);
        }

        List<String> tokens = mFunctions.tokenize(sql);

        // Validate query type and table name
        if (!tokens.get(0).equalsIgnoreCase("DELETE") || !tokens.get(1).equalsIgnoreCase("FROM")) {
            throw new IllegalArgumentException("Invalid DELETE query syntax");
        }

        JSONSQLCondition where = mFunctions.extractWhereConditions(tokens);

        // Iterate and collect indexes to delete
        List<Integer> deleteIndexes = new ArrayList<>();
        for (int i = 0; i < mTable.size(); i++) {
            JSONObject row = mTable.getJSONObject(i);

            boolean matchesCondition = mFunctions.matchesConditions(row, where);
            if (!matchesCondition) { continue; }

            deleteIndexes.add(i);
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
        JSONFunctions.BenchmarkLogger logger = new JSONFunctions.BenchmarkLogger("Select");

        String tableName = mFunctions.extractTableName(sql);
        if (!tableName.equalsIgnoreCase(mName)) {
            throw new IllegalArgumentException("Incorrect Table Name detected: " + tableName + ", expected: " + mName);
        }

        List<String> tokens = mFunctions.tokenize(sql);

        List<String> selectedColumns = mFunctions.extractSelectedColumns(tokens);
        JSONSQLCondition whereConditions = mFunctions.extractWhereConditions(tokens);
        List<String[]> orderByColumns = mFunctions.extractOrderBy(tokens);
        int limit = mFunctions.extractLimit(tokens);

        List<JSONObject> matchedRows = new ArrayList<>();

        // Filter rows using flattened representation
        for (int index = 0; index < mTable.size(); index++) {
            JSONObject structuredRow = mTable.getJSONObject(index);

            boolean matchesCondition = mFunctions.matchesConditions(structuredRow, whereConditions);
            if (!matchesCondition) { continue; }

            matchedRows.add(structuredRow);
        }

        // Sort using flattened version
        JSONArray sortedResults = mFunctions.sortResults(new JSONArray(matchedRows), orderByColumns);

        // Apply limit
        if (limit > 0 && sortedResults.size() > limit) {
            JSONArray limitedResults = new JSONArray();
            for (int i = 0; i < limit; i++) {
                limitedResults.add(sortedResults.get(i));
            }
            sortedResults = limitedResults;
        }

        // Final projection
        JSONArray finalResults = new JSONArray();
        for (int index = 0; index < sortedResults.size(); index++) {
            JSONObject row = sortedResults.getJSONObject(index);
            JSONObject filteredRow = new JSONObject();

            if (selectedColumns.size() == 1 && selectedColumns.getFirst().equals("*")) {
                filteredRow = row;
            } else {
                for (String column : selectedColumns) {
                    Object columnValue = mFunctions.getJsonPathValue(row, column);
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

    public void normalize() { mFunctions.normalizeRows(mTable); }
    public JSONObject get(int i) { return mTable.getJSONObject(i); }
    public int size() { return mTable.size(); }
}