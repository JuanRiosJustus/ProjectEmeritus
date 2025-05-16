package jsonsql.main;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class JSONDBTable {
    protected JSONFunctions mFunctions = null;
    protected JSONDBDiskOperations mDiskOperations = null;
    protected String mTableName = null;
    protected String mTablePath = null;
    private long mNextRowId = 1;

    public JSONDBTable(String tablePath) {
        if (!tablePath.endsWith(".jsondb")) {
            tablePath += ".jsondb";
        };
        mDiskOperations = new JSONDBDiskOperations();
        mFunctions = new JSONFunctions();
        mTablePath = tablePath;
        mTableName = tablePath.substring(tablePath.lastIndexOf("/") + 1, tablePath.lastIndexOf("."));
        mDiskOperations.tryCreatingNewDbFile(tablePath);
    }

    public String toString() { return mTableName; }
    public String getTableName() { return mTableName; }
    public String getTablePah() { return mTablePath; }

    public JSONObject insert(JSONObject row) {
        File file = new File(mTablePath);

        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            // 1. Read footer offset from end of file
            long footerOffset = mDiskOperations.readFooterOffset(file);

            // 2. Load footer JSON (only read one line)
            raf.seek(footerOffset);
            String footerJsonLine = raf.readLine();
            if (footerJsonLine == null || footerJsonLine.trim().isEmpty()) {
                throw new IOException("Missing or empty footer JSON.");
            }
            JSONObject footer = JSON.parseObject(footerJsonLine);
            JSONArray offsets = footer.getJSONArray("offsets");
            JSONObject index = footer.getJSONObject("index");
            mNextRowId = footer.getLongValue("nextRowId");

            if (offsets == null) offsets = new JSONArray();
            if (index == null) index = new JSONObject();

            // 3. Truncate file before footer
            raf.setLength(footerOffset);
            raf.seek(footerOffset);

            // 4. Prepare new row
            if (!row.containsKey("id")) {
                row.put("id", mNextRowId++);
            } else {
                long manualId = row.getLongValue("id");
                mNextRowId = Math.max(mNextRowId, manualId + 1);
            }

            long rowOffset = raf.getFilePointer();
            String rowStr = row.toJSONString();
            raf.write(rowStr.getBytes(StandardCharsets.UTF_8));
            raf.write("\n".getBytes(StandardCharsets.UTF_8));

            // 5. Update footer
            offsets.add(rowOffset);
            index.put("id:" + row.getString("id"), rowOffset);

            JSONObject updatedFooter = new JSONObject();
            updatedFooter.put("offsets", offsets);
            updatedFooter.put("index", index);
            updatedFooter.put("nextRowId", mNextRowId);
            byte[] updatedFooterBytes = updatedFooter.toJSONString().getBytes(StandardCharsets.UTF_8);

            // 6. Write updated footer and its new offset
            long newFooterOffset = raf.getFilePointer();
            raf.write(updatedFooterBytes);
            raf.write("\n".getBytes(StandardCharsets.UTF_8));
            raf.write(String.valueOf(newFooterOffset).getBytes(StandardCharsets.UTF_8));
            raf.write("\n".getBytes(StandardCharsets.UTF_8));

            System.out.println(">>> [DEBUG] Wrote row at offset " + rowOffset + " and footer at " + newFooterOffset);
            System.out.println(">>> [DEBUG] New footer: " + updatedFooter.toJSONString());

        } catch (Exception e) {
            System.err.println(">>> [ERROR] Failed to insert row: " + e.getMessage());
            e.printStackTrace();
        }

        return row;
    }

    public JSONArray update(String sql) {
        JSONFunctions.BenchmarkLogger logger = new JSONFunctions.BenchmarkLogger("Update");
        final JSONArray result = new JSONArray();

        String tableName = mFunctions.extractTableName(sql);
        if (!tableName.equalsIgnoreCase(mTableName)) {
            throw new IllegalArgumentException("Incorrect Table Name detected: " + tableName + ", expected: " + mTableName);
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

//        for (int i = 0; i < mTable.size(); i++) {
//            JSONObject row = mTable.getJSONObject(i);
//            if (!mFunctions.matchesConditions(row, where)) continue;
//
//            for (Map.Entry<String, Object> entry : updates.entrySet()) {
//                mFunctions.setAndCreateJSONValue(row, entry.getKey(), entry.getValue());
//            }
//
//            result.add(row);
//        }

        logger.stop();
        return result;
    }

    public JSONArray insertRaw(String newObject) {
        JSONFunctions.BenchmarkLogger logger = new JSONFunctions.BenchmarkLogger("Insert");
        JSONArray result = new JSONArray();
        try {
            if (newObject == null || newObject.isEmpty()) { return result; }
            JSONObject newRow = JSON.parseObject(newObject);
            System.out.println("Inserting " + newRow.toString());
            insert(newRow);
            result.add(newRow);
        } catch (Exception e) {
            try {
                JSONArray newRows = JSON.parseArray(newObject);
                for (int i = 0; i < newRows.size(); i++) {
                    JSONObject newRow = newRows.getJSONObject(i);
                    System.out.println("Inserting " + newRow.toString());
                    insert(newRow);
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
        if (!tableName.equalsIgnoreCase(mTableName)) {
            throw new IllegalArgumentException("Incorrect table name: " + tableName + ", expected: " + mTableName);
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
//        for (String jsonText : jsonValues) {
//            try {
//                JSONObject obj = JSON.parseObject(jsonText);
//                mTable.add(obj);
//                result.add(obj);
//                appendRowsToDisk(List.of(obj));
//            } catch (Exception e) {
//                throw new IllegalArgumentException("Invalid JSON object: " + jsonText);
//            }
//        }

        logger.stop();
        return result;
    }

    public JSONArray delete(String sql) {
        JSONFunctions.BenchmarkLogger logger = new JSONFunctions.BenchmarkLogger("delete");
        final JSONArray result = new JSONArray();

        String tableName = mFunctions.extractTableName(sql);
        if (!tableName.equalsIgnoreCase(mTableName)) {
            throw new IllegalArgumentException("Incorrect Table Name detected: " + tableName + ", expected: " + mTableName);
        }

        List<String> tokens = mFunctions.tokenize(sql);

        // Validate query type and table name
        if (!tokens.get(0).equalsIgnoreCase("DELETE") || !tokens.get(1).equalsIgnoreCase("FROM")) {
            throw new IllegalArgumentException("Invalid DELETE query syntax");
        }

        JSONSQLCondition where = mFunctions.extractWhereConditions(tokens);
//
//        // Iterate and collect indexes to delete
//        List<Integer> deleteIndexes = new ArrayList<>();
//        for (int i = 0; i < mTable.size(); i++) {
//            JSONObject row = mTable.getJSONObject(i);
//
//            boolean matchesCondition = mFunctions.matchesConditions(row, where);
//            if (!matchesCondition) { continue; }
//
//            deleteIndexes.add(i);
//        }
//
//        // Remove from last index to avoid shifting
//        Collections.reverse(deleteIndexes);
//        for (int index : deleteIndexes) {
//            JSONObject row = mTable.getJSONObject(index);
//            mTable.remove(index);
//            result.add(row);
//        }

        Collections.reverse(result);

        logger.stop();
        return result;
    }

    private JSONObject readRowAtOffset(File file, long offset) {
        JSONObject result = null;
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(offset);
            String rowLine = raf.readLine();
            result = JSON.parseObject(rowLine);
            raf.close();
        } catch (Exception ex) {
            result = null;
        }
        return result;
    }


    public JSONArray select(String sql) {
        JSONFunctions.BenchmarkLogger logger = new JSONFunctions.BenchmarkLogger("Select");

        String tableName = mFunctions.extractTableName(sql);
        if (!tableName.equalsIgnoreCase(mTableName)) {
            throw new IllegalArgumentException("Incorrect Table Name detected: " + tableName + ", expected: " + mTableName);
        }

        List<String> tokens = mFunctions.tokenize(sql);

        List<String> selectedColumns = mFunctions.extractSelectedColumns(tokens);
        JSONSQLCondition whereConditions = mFunctions.extractWhereConditions(tokens);
        List<String[]> orderByColumns = mFunctions.extractOrderBy(tokens);
        int limit = mFunctions.extractLimit(tokens);

        // Filter rows using flattened representation
//        for (int index = 0; index < mTable.size(); index++) {
//            JSONObject structuredRow = mTable.getJSONObject(index);
//
//            boolean matchesCondition = mFunctions.matchesConditions(structuredRow, whereConditions);
//            if (!matchesCondition) { continue; }
//
//            matchedRows.add(structuredRow);
//        }

        JSONObject footer = mDiskOperations.readFooter(new File(mTablePath));
        JSONArray offsets = footer.getJSONArray("offsets");
        List<JSONObject> matchedRows = new ArrayList<>();
        for (int i = 0; i < offsets.size(); i++) {
            long offset = offsets.getLongValue(i);
            JSONObject row = readRowAtOffset(new File(mTablePath), offset);

            if (!mFunctions.matchesConditions(row, whereConditions)) {
                continue;
            }

            matchedRows.add(row);
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
}