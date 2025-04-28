package jsonsql.main;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONSQLFunctions {
    private static final String SHOW_TABLE_KEYWORD = "SHOW_TABLES";
    private static final String VALUES_KEYWORD = "VALUES";
    private static final String INSERT_KEYWORD = "INSERT";
    private static final String SELECT_KEYWORD = "SELECT";
    private static final String UPDATE_KEYWORD = "UPDATE";
    private static final String DELETE_KEYWORD = "DELETE";
    private static final String FROM_KEYWORD = "FROM";
    private static final String WHERE_KEYWORD = "WHERE";
    private static final String ORDER_KEYWORD = "ORDER";
    private static final String BY_KEYWORD = "BY";
    private static final String LIMIT_KEYWORD = "LIMIT";
    private static final String ASC_KEYWORD = "ASC";
    private static final String DESC_KEYWORD = "DESC";
    private static final String AND_KEYWORD = "AND";
    private static final String OR_KEYWORD = "OR";
    private static final String IS_KEYWORD = "IS";
    private static final String NOT_KEYWORD = "NOT";
    private static final String IS_NOT_KEYWORD = "IS NOT";
    private static final String OPEN_PARENTHESIS = "(", CLOSE_PARENTHESIS = ")";
    private static final String COMA = ",";

    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "'([^']*)'" + // Quoted strings
                    "|\\b(IS\\s+NOT|SELECT|FROM|WHERE|AND|OR|ORDER|BY|LIMIT|ASC|DESC|IS|NOT)\\b" + // SQL keywords
                    "|[=<>!]+" +  // Operators
                    "|[(),]" +    // Symbols
                    "|[^\\s=<>!(),']+", // Identifiers
            Pattern.CASE_INSENSITIVE
    );
//    private static final Pattern TOKEN_PATTERN = Pattern.compile(
//            "'([^']*)'" +                                // Quoted strings: 'New York'
//                    "|\\b(SELECT|FROM|WHERE|AND|OR|ORDER|BY|LIMIT|ASC|DESC|IS|NOT)\\b" + // SQL keywords
//                    "|[=<>!]+" +                                  // Operators
//                    "|[(),]" +                                    // Symbols: parenthesis and comma
//                    "|[^\\s=<>!(),']+"                            // Identifiers (everything else)
//            , Pattern.CASE_INSENSITIVE);

    private static final Set<String> SQL_KEYWORDS = Set.of(
            "SELECT", "UPDATE", "FROM", "WHERE", "AND", "OR", "ORDER", "BY", "LIMIT", "ASC", "DESC"
    );

    public JSONArray extractInsertInto(String sql) {
        List<String> tokens = tokenize(sql);
        String tableName = extractTableName(sql);

        JSONArray results = new JSONArray();
        if (tokens.isEmpty() || tableName == null) {
            return results;
        }

        List<String> columns = new ArrayList<>();
        boolean parsingColumns = false;

        // Get Column values
        for (String token : tokens) {
            // Phase 1: Before VALUES (collecting, "column", 'names')
            if (token.equals(OPEN_PARENTHESIS)) {
                parsingColumns = true;
                continue;
            }
            if (token.equals(CLOSE_PARENTHESIS)) {
                parsingColumns = false;
                break;
            }

            if (token.equals(COMA)) { continue; }
            if (!parsingColumns) { continue; }

            columns.add(token); // Only add real columns (skip commas)
        }


        List<List<String>> allValueGroups = new ArrayList<>();
        List<String> currentGroup = null;
        boolean parsingValues = false;

        for (String token : tokens) {
            if (token.equalsIgnoreCase(VALUES_KEYWORD)) {
                parsingValues = true; // Switch to parsing values phase
                continue;
            }

            if (!parsingValues) { continue; }

            if (token.equals(OPEN_PARENTHESIS)) {
                currentGroup = new ArrayList<>(); // New row
                continue;
            } else if (token.equals(CLOSE_PARENTHESIS)) {
                if (currentGroup != null) {
                    allValueGroups.add(currentGroup); // Finished a row
                    currentGroup = null;
                }
                continue;
            }

            if (currentGroup != null && !token.equals(COMA)) {
                currentGroup.add(token); // Add value to the current row
            }
        }


        // Now map columns to values
        for (List<String> values : allValueGroups) {
            JSONObject row = new JSONObject();
            if (columns.isEmpty()) {
                // No explicit columns → treat as root keys "col0", "col1", etc.
                for (int j = 0; j < values.size(); j++) {
                    row.put(String.valueOf(j), parseLiteral(values.get(j)));
                }
            } else {
                for (int j = 0; j < columns.size() && j < values.size(); j++) {
                    row.put(columns.get(j), parseLiteral(values.get(j)));
                }
            }
            results.add(row);
        }

        return results;
    }

//    public String extractTableName(String sql) {
//        if (sql == null || sql.isBlank()) {
//            return null;
//        }
//
//        List<String> tokens = getTokens(sql);
//        if (tokens.isEmpty()) {
//            return null;
//        }
//
//        String queryType = tokens.get(0).toUpperCase();
//
//        switch (queryType) {
//            case "SELECT":
//            case "DELETE": {
//                // Look for "FROM <table>"
//                for (int i = 0; i < tokens.size() - 1; i++) {
//                    if (tokens.get(i).equalsIgnoreCase("FROM")) {
//                        return tokens.get(i + 1);
//                    }
//                }
//                break;
//            }
//            case "UPDATE": {
//                // "UPDATE <table>"
//                if (tokens.size() >= 2) {
//                    return tokens.get(1);
//                }
//                break;
//            }
//            case "INSERT": {
//                // "INSERT INTO <table>"
//                for (int i = 0; i < tokens.size() - 1; i++) {
//                    if (tokens.get(i).equalsIgnoreCase("INTO")) {
//                        return tokens.get(i + 1);
//                    }
//                }
//                break;
//            }
//        }
//        return null;
//    }

    public static class BenchmarkLogger {
        private final long mStart;
        private final String mLabel;

        public BenchmarkLogger(String label) {
            mLabel = label;
            mStart = System.nanoTime();
        }

        public void stop() {
            long elapsed = System.nanoTime() - mStart;
            System.out.printf("[%s] Time: %,d ns (%.3f µs | %.3f ms)%n",
                    mLabel, elapsed, elapsed / 1_000.0, elapsed / 1_000_000.0);
        }
    }

    private final Pattern mTablePattern = Pattern.compile(
            "(?i)(FROM|INTO|UPDATE)\\s+([a-zA-Z_][a-zA-Z0-9_]*)"
    );

    public String extractTableName(String sql) {
        if (sql == null || sql.isBlank()) {
            return null;
        }

        List<String> tokens = tokenize(sql);
        if (tokens.isEmpty()) {
            return null;
        }

        String queryType = tokens.get(0).toUpperCase();

        switch (queryType) {
            case SELECT_KEYWORD:
            case DELETE_KEYWORD: {
                // Look for "FROM <table>"
                for (int i = 0; i < tokens.size() - 1; i++) {
                    if (tokens.get(i).equalsIgnoreCase("FROM")) {
                        return tokens.get(i + 1);
                    }
                }
                break;
            }
            case UPDATE_KEYWORD: {
                // "UPDATE <table>"
                if (tokens.size() >= 2) {
                    return tokens.get(1);
                }
                break;
            }
            case INSERT_KEYWORD: {
                // "INSERT INTO <table>"
                for (int i = 0; i < tokens.size() - 1; i++) {
                    if (tokens.get(i).equalsIgnoreCase("INTO")) {
                        return tokens.get(i + 1);
                    }
                }
                break;
            }
        }
        return null; // Unknown format
    }

    public int getFirstIndexOf(Map<Integer, String[]> tokens, String token) {
        return tokens.entrySet()
                .stream()
                .filter(entry -> entry.getValue()[0].equalsIgnoreCase(token))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(-1);
    }

    public int getFirstIndexOf(List<String> tokens, String token) {
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).equalsIgnoreCase(token)) {
                return i;
            }
        }
        return -1;
    }

    public JSONArray sortResults(JSONArray results, List<String[]> orderByColumns) {
        List<JSONObject> sortedList = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            sortedList.add(results.getJSONObject(i));
        }

        sortedList.sort((a, b) -> {
            for (String[] order : orderByColumns) {
                String column = order[0].toLowerCase(); // Normalize case for columns
                boolean desc = order[1].equalsIgnoreCase(DESC_KEYWORD);

                Object objA = getJsonPathValue(a, column);
                Object objB = getJsonPathValue(b, column);

                // ✅ Handle NULL values explicitly
                if (objA == null && objB == null) {
                    continue;
                } // Both NULL -> equal
                if (objA == null) {
                    return desc ? -1 : 1;
                }// NULL should be last in ASC, first in DESC
                if (objB == null) {
                    return desc ? 1 : -1;
                }

                int cmp = 0;
                if (objA instanceof Number numA && objB instanceof Number numB) {
                    cmp = Double.compare(numA.doubleValue(), numB.doubleValue());
                } else if (objA instanceof String strA && objB instanceof String strB) {
                    cmp = strA.compareTo(strB);
                }

                if (cmp != 0) {
                    return desc ? -cmp : cmp;
                }
            }
            return 0; // Equal values, maintain order
        });

        return new JSONArray(sortedList);
    }

    public List<String> tokenizeJsonPath(String path) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = Pattern.compile("[^.\\[\\]]+|\\[\\d+]").matcher(path);
        while (matcher.find()) {
            String token = matcher.group();
            if (token.matches("\\[\\d+]")) {
                tokens.add(token.substring(1, token.length() - 1)); // Only number
            } else {
                tokens.add(token);
            }
        }
        return tokens;
    }

    public Object getJsonPathValue(JSONObject jsonObject, String path) {
        List<String> tokens = tokenizeJsonPath(path);
        Object current = jsonObject;

        for (String token : tokens) {
            if (current instanceof JSONObject obj) {
                current = obj.get(token);
            } else if (current instanceof JSONArray arr) {
                int index = Integer.parseInt(token);
                if (index >= arr.size()) { return null; }
                current = arr.get(index);
            } else {
                return null;
            }
        }
        return current;
    }

    public Map<String, Object> extractUpdates(List<String> assignments) {
        Map<String, Object> updates = new LinkedHashMap<>();

        for (String assign : assignments) {
            String[] parts = assign.split("=", 2);
            if (parts.length != 2) throw new IllegalArgumentException("Malformed assignment: " + assign);

            String key = parts[0].trim();
            String value = parts[1].trim();

            Object parsedValue = null;

            if (value.matches("^'(.*)'$")) {
                parsedValue = value.substring(1, value.length() - 1);
            } else if (value.equalsIgnoreCase("null")) {
                parsedValue = null;
            } else {
                try {
                    if (value.startsWith("{")) {
                        parsedValue = JSON.parseObject(value);
                    } else if (value.startsWith("[")) {
                        parsedValue = JSON.parseArray(value);
                    } else if (value.matches("-?\\d+\\.\\d+")) {
                        parsedValue = Double.parseDouble(value);
                    } else if (value.matches("-?\\d+")) {
                        parsedValue = Integer.parseInt(value);
                    } else {
                        parsedValue = value;
                    }
                } catch (Exception e) {
                    parsedValue = value;
                }
            }

            updates.put(key, parsedValue);
        }
        return updates;
    }


    public Map<Integer, String[]> getTokensWithIdentifiers(String sql) {
        Map<Integer, String[]> map = new LinkedHashMap<>();
        Matcher matcher = TOKEN_PATTERN.matcher(sql);

        while (matcher.find()) {
            String token = null;
            String type = null;

            if (matcher.group(1) != null) {
                // ✅ Handle Quoted Strings (e.g., 'New York')
                token = "'" + matcher.group(1) + "'";
                type = "STRING";
            } else {
                token = matcher.group().trim();
                if (SQL_KEYWORDS.contains(token.toUpperCase())) {
                    type = "KEYWORD"; // ✅ SQL Keywords (SELECT, WHERE, ORDER BY)
                    token = token.toUpperCase();
                } else if (token.matches("-?\\d+(\\.\\d+)?")) {
                    type = "NUMBER"; // ✅ Numeric values
                } else if (token.matches("[=<>!]+")) {
                    type = "OPERATOR"; // ✅ Operators (=, !=, <, >)
                } else if (token.matches("[(),]")) {
                    type = "SYMBOL"; // ✅ Symbols (comma, parentheses)
                } else {
                    type = "IDENTIFIER"; // ✅ Column names, table names, etc.
                }
            }

            map.put(map.size(), new String[]{token, type});
        }
        return map;
    }


    public List<String> tokenize(String sql) {
        List<String> tokens = new ArrayList<>();
        if (sql == null || sql.isEmpty()) {
            return tokens;
        }

        StringBuilder current = new StringBuilder();
        boolean insideSingleQuote = false;
        boolean insideDoubleQuote = false;

        for (int index = 0; index < sql.length(); index++) {
            char c = sql.charAt(index);

            if (c == '\'' && !insideDoubleQuote) {
                insideSingleQuote = !insideSingleQuote;
                current.append(c);
                continue;
            }
            if (c == '"' && !insideSingleQuote) {
                insideDoubleQuote = !insideDoubleQuote;
                current.append(c);
                continue;
            }

            if (insideSingleQuote || insideDoubleQuote) {
                current.append(c);
                continue;
            }


            if (Character.isWhitespace(c)) {
                if (!current.isEmpty()) {
                    tokens.add(processToken(current.toString()));
                    current.setLength(0);
                }
                continue;
            }

            // 🔥 Handle two-character operators like >=, <=, !=
            if (isOperatorChar(c)) {
                if (!current.isEmpty()) {
                    tokens.add(processToken(current.toString()));
                    current.setLength(0);
                }

                // Look ahead one character
                if (index + 1 < sql.length()) {
                    char next = sql.charAt(index + 1);
                    if (isOperatorChar(next)) {
                        tokens.add("" + c + next);
                        index++; // Skip the next character
                        continue;
                    }
                }
                tokens.add(String.valueOf(c));
                continue;
            }

            if (isSpecialSingleChar(c)) {
                if (!current.isEmpty()) {
                    tokens.add(processToken(current.toString()));
                    current.setLength(0);
                }
                tokens.add(String.valueOf(c));
                continue;
            }

            // Special cases, special word here
            String specialWord = isOperatorKeyword(sql, index);
            if (specialWord != null) {
                index += specialWord.length();
                tokens.add(specialWord);
                continue;
            }

            current.append(c);
        }

        if (!current.isEmpty()) {
            tokens.add(processToken(current.toString()));
        }

        return tokens;
    }

    private boolean isSpecialSingleChar(char c) {
        return c == '(' || c == ')' || c == ',';
    }

    private boolean isOperatorChar(char c) {
        return c == '=' || c == '!' || c == '<' || c == '>';
    }

    private String isOperatorKeyword(String str, int index) {
        List<String> words = List.of("IS", "IS NOT", "LIKE");

        String result = null;
        for (String word : words) {
            if (!str.startsWith(word, index)) { continue; }
            result = word;
        }
        return result;
    }


    private String processToken(String raw) {
        String token = raw.trim();
        if (SQL_KEYWORDS.contains(token.toUpperCase())) {
            return token.toUpperCase(); // Normalize keywords
        }
        return token;
    }


    public List<String[]> extractOrderBy(List<String> mIndexToTokenMap) {
        List<String[]> orderByColumns = new ArrayList<>();
        int orderIndex = getFirstIndexOf(mIndexToTokenMap, ORDER_KEYWORD);

        if (orderIndex == -1 || orderIndex + 1 >= mIndexToTokenMap.size()) {
            return orderByColumns;
        }
        if (!mIndexToTokenMap.get(orderIndex + 1).equalsIgnoreCase(BY_KEYWORD)) {
            return orderByColumns;
        }

        for (int i = orderIndex + 2; i < mIndexToTokenMap.size(); i++) {
            StringBuilder column = new StringBuilder(mIndexToTokenMap.get( i));

            // 🚨 Ensure column name exists and skip commas
            if (column.toString().equals(",")) {
                continue; // Skip commas explicitly
            }
            if (SQL_KEYWORDS.contains(column.toString().toUpperCase()) || (column.isEmpty())) {
                break; // Stop if another SQL keyword appears
            }

            // ✅ Handle single-quoted column names (e.g., 'column_name')
            if (column.toString().startsWith("'")) {
                while (i + 1 < mIndexToTokenMap.size() && !mIndexToTokenMap.get(i + 1).endsWith("'")) {
                    column.append(" ").append(mIndexToTokenMap.get( ++i));
                }
            }

            // ✅ Check if next token is ASC/DESC
            String order = ASC_KEYWORD; // Default order is ASC
            if (i + 1 < mIndexToTokenMap.size()) {
                String nextToken = mIndexToTokenMap.get(i + 1);
                if (nextToken.equalsIgnoreCase(ASC_KEYWORD) || nextToken.equalsIgnoreCase(DESC_KEYWORD)) {
                    order = nextToken.toUpperCase();
                    i++; // ✅ Skip the next token since it's already processed
                }
            }

            orderByColumns.add(new String[]{column.toString(), order});
        }

        return orderByColumns;
    }


    public List<String> extractSelectedColumns(Map<Integer, String[]> mIndexToTokenMap) {
        List<String> columns = new ArrayList<>();

        int selectIndex = getFirstIndexOf(mIndexToTokenMap, SELECT_KEYWORD);
        int fromIndex = getFirstIndexOf(mIndexToTokenMap, FROM_KEYWORD);

        if (selectIndex == -1 || fromIndex == -1 || fromIndex < selectIndex) {
            throw new IllegalArgumentException("Invalid SQL query. Expected 'SELECT ... FROM ...'");
        }

        boolean expectColumn = true; // ✅ Ensures commas separate columns properly

        for (int i = selectIndex + 1; i < fromIndex; i++) {
            String token = getTokenAtIndex(mIndexToTokenMap, i);

            if (token.equals(",")) {
                if (expectColumn) {
                    throw new IllegalArgumentException("Unexpected comma in column list.");
                }
                expectColumn = true; // ✅ Next token must be a column name
            } else {
                if (!expectColumn) {
                    throw new IllegalArgumentException("Missing comma between column names.");
                }
                columns.add(token.toLowerCase());
                expectColumn = false; // ✅ Next token must be a comma or FROM
            }
        }

        if (expectColumn || columns.isEmpty()) {
            throw new IllegalArgumentException("Malformed commas in column list.");
        }

        return columns;
    }

    public List<String> extractSelectedColumns(List<String> queryTokens) {
        List<String> columns = new ArrayList<>();

        int selectIndex = getFirstIndexOf(queryTokens, SELECT_KEYWORD);
        int fromIndex = getFirstIndexOf(queryTokens, FROM_KEYWORD);

        if (selectIndex == -1 || fromIndex == -1 || fromIndex < selectIndex) {
            throw new IllegalArgumentException("Invalid SQL query. Expected 'SELECT ... FROM ...'");
        }

        boolean expectColumn = true; // ✅ Ensures commas separate columns properly

        for (int i = selectIndex + 1; i < fromIndex; i++) {
            String token = queryTokens.get(i);

            if (token.equals(",")) {
                if (expectColumn) {
                    throw new IllegalArgumentException("Unexpected comma in column list.");
                }
                expectColumn = true; // ✅ Next token must be a column name
            } else {
                if (!expectColumn) {
                    throw new IllegalArgumentException("Missing comma between column names.");
                }
                columns.add(token.toLowerCase());
                expectColumn = false; // ✅ Next token must be a comma or FROM
            }
        }

        if (expectColumn || columns.isEmpty()) {
            throw new IllegalArgumentException("Malformed commas in column list.");
        }

        return columns;
    }


    public JSONSQLCondition extractWhereConditions(Map<Integer, String[]> mIndexToTokenMap) {
        Stack<List<JSONSQLCondition>> conditionStack = new Stack<>();
        Stack<List<String>> logicalOperatorStack = new Stack<>();

        int whereIndex = getFirstIndexOf(mIndexToTokenMap, WHERE_KEYWORD);
        if (whereIndex == -1) {
            return new JSONSQLCondition("*", "*", "*");
        }

        int orderByIndex = getFirstIndexOf(mIndexToTokenMap, ORDER_KEYWORD);
        int limitIndex = getFirstIndexOf(mIndexToTokenMap, LIMIT_KEYWORD);
        int endIndex = mIndexToTokenMap.size();
        if (orderByIndex != -1) {
            endIndex = orderByIndex;
        }
        if (limitIndex != -1 && limitIndex < endIndex) {
            endIndex = limitIndex;
        }

        List<JSONSQLCondition> currentConditions = new ArrayList<>();
        List<String> currentLogicalOperators = new ArrayList<>();

        for (int i = whereIndex + 1; i < endIndex; i++) {
            String token = getTokenAtIndex(mIndexToTokenMap, i);

            if (token.equals("(")) {
                // ✅ Push the current condition list and start a new one
                conditionStack.push(currentConditions);
                logicalOperatorStack.push(currentLogicalOperators);

                currentConditions = new ArrayList<>();
                currentLogicalOperators = new ArrayList<>();
            } else if (token.equals(")")) {
                // ✅ Closing parenthesis - Process and merge all conditions inside
                if (conditionStack.isEmpty()) {
                    throw new IllegalArgumentException("Mismatched parentheses in WHERE clause.");
                }

                // ✅ Restore previous conditions and operator
                List<JSONSQLCondition> groupedConditions = new ArrayList<>(currentConditions);
                List<String> groupedOperators = new ArrayList<>(currentLogicalOperators);
                currentConditions = conditionStack.pop();
                currentLogicalOperators = logicalOperatorStack.pop();

                // ✅ Wrap the entire group as a single condition
                currentConditions.add(new JSONSQLCondition(groupedConditions, groupedOperators));
            } else if (token.equalsIgnoreCase(AND_KEYWORD) || token.equalsIgnoreCase(OR_KEYWORD)) {
                // ✅ Store logical operator
                currentLogicalOperators.add(token.toUpperCase());
            } else {
                if (i + 1 < endIndex) {
                    String column = token;
                    String operator = getTokenAtIndex(mIndexToTokenMap, i + 1);
                    String value = null;

                    if (operator.equalsIgnoreCase(IS_KEYWORD)) {
                        if (i + 2 < endIndex && getTokenAtIndex(mIndexToTokenMap, i + 2).equalsIgnoreCase(NOT_KEYWORD)) {
                            operator = IS_NOT_KEYWORD;
                            value = getTokenAtIndex(mIndexToTokenMap, i + 3);
                            i += 3;
                        } else {
                            value = getTokenAtIndex(mIndexToTokenMap, i + 2);
                            i += 2;
                        }
                    } else {
                        value = getTokenAtIndex(mIndexToTokenMap, i + 2);
                        i += 2;
                    }

                    if (value == null) {
                        throw new IllegalArgumentException("Unmatched comparisons");
                    }

                    JSONSQLCondition newCondition = new JSONSQLCondition(column, operator, value);
                    currentConditions.add(newCondition);
                } else {
                    throw new IllegalArgumentException("Invalid WHERE clause syntax");
                }
            }
        }

        // ✅ Ensure all top-level conditions are properly grouped
        while (!currentLogicalOperators.isEmpty() && currentConditions.size() > 1) {
            List<JSONSQLCondition> groupedConditions = new ArrayList<>(currentConditions);
            List<String> groupedOperators = new ArrayList<>(currentLogicalOperators);

            // ✅ Wrap the entire group as a single condition
            currentConditions.clear();
            currentConditions.add(new JSONSQLCondition(groupedConditions, groupedOperators));
            currentLogicalOperators.clear();
        }

        if (!conditionStack.isEmpty()) {
            throw new IllegalArgumentException("Unmatched parentheses in WHERE clause.");
        }

        return currentConditions.getFirst();
    }


    public List<String> smartSplitAssignments(String input) {
        List<String> parts = new ArrayList<>();
        int braceDepth = 0;
        int bracketDepth = 0;
        boolean inString = false;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '\'' && (i == 0 || input.charAt(i - 1) != '\\')) {
                inString = !inString;
            }

            if (!inString) {
                if (c == '{') {
                    braceDepth++;
                } else if (c == '}') {
                    braceDepth--;
                } else if (c == '[') {
                    bracketDepth++;
                } else if (c == ']') {
                    bracketDepth--;
                } else if (c == ',' && braceDepth == 0 && bracketDepth == 0) {
                    parts.add(current.toString().trim());
                    current.setLength(0);
                    continue;
                }
            }

            current.append(c);
        }

        if (!current.isEmpty()) {
            parts.add(current.toString().trim());
        }

        return parts;
    }

    public JSONSQLCondition extractWhereConditions(List<String> tokens) {
        Stack<List<JSONSQLCondition>> conditionStack = new Stack<>();
        Stack<List<String>> logicalOperatorStack = new Stack<>();

        int whereIndex = getFirstIndexOf(tokens, WHERE_KEYWORD);
        if (whereIndex == -1) {
            return new JSONSQLCondition("1", "=", "1");
        }

        int orderByIndex = getFirstIndexOf(tokens, ORDER_KEYWORD);
        int limitIndex = getFirstIndexOf(tokens, LIMIT_KEYWORD);
        int endIndex = tokens.size();
        if (orderByIndex != -1) {
            endIndex = orderByIndex;
        }
        if (limitIndex != -1 && limitIndex < endIndex) {
            endIndex = limitIndex;
        }

        List<JSONSQLCondition> currentConditions = new ArrayList<>();
        List<String> currentLogicalOperators = new ArrayList<>();

        for (int i = whereIndex + 1; i < endIndex; i++) {
            String token = tokens.get(i);

            if (token.equals("(")) {
                // ✅ Push the current condition list and start a new one
                conditionStack.push(currentConditions);
                logicalOperatorStack.push(currentLogicalOperators);

                currentConditions = new ArrayList<>();
                currentLogicalOperators = new ArrayList<>();
            } else if (token.equals(")")) {
                // ✅ Closing parenthesis - Process and merge all conditions inside
                if (conditionStack.isEmpty()) {
                    throw new IllegalArgumentException("Mismatched parentheses in WHERE clause.");
                }

                // ✅ Restore previous conditions and operator
                List<JSONSQLCondition> groupedConditions = new ArrayList<>(currentConditions);
                List<String> groupedOperators = new ArrayList<>(currentLogicalOperators);
                currentConditions = conditionStack.pop();
                currentLogicalOperators = logicalOperatorStack.pop();

                // ✅ Wrap the entire group as a single condition
                currentConditions.add(new JSONSQLCondition(groupedConditions, groupedOperators));
            } else if (token.equalsIgnoreCase(AND_KEYWORD) || token.equalsIgnoreCase(OR_KEYWORD)) {
                // ✅ Store logical operator
                currentLogicalOperators.add(token.toUpperCase());
            } else {
                if (i + 1 < endIndex) {
                    String column = token;
                    String operator = tokens.get(i + 1);
                    String value = null;

                    if (operator.equalsIgnoreCase(IS_KEYWORD)) {
                        if (i + 2 < endIndex && tokens.get(i + 1).equalsIgnoreCase(NOT_KEYWORD)) {
                            operator = IS_NOT_KEYWORD;
                            value = tokens.get(i + 3);
                            i += 3;
                        } else {
                            value = tokens.get(i + 2);
                            i += 2;
                        }
                    } else {
                        value = tokens.get(i + 2);
                        i += 2;
                    }

                    if (value == null) {
                        throw new IllegalArgumentException("Unmatched comparisons");
                    }

                    JSONSQLCondition newCondition = new JSONSQLCondition(column, operator, value);
                    currentConditions.add(newCondition);
                } else {
                    throw new IllegalArgumentException("Invalid WHERE clause syntax");
                }
            }
        }

        // ✅ Ensure all top-level conditions are properly grouped
        while (!currentLogicalOperators.isEmpty() && currentConditions.size() > 1) {
            List<JSONSQLCondition> groupedConditions = new ArrayList<>(currentConditions);
            List<String> groupedOperators = new ArrayList<>(currentLogicalOperators);

            // ✅ Wrap the entire group as a single condition
            currentConditions.clear();
            currentConditions.add(new JSONSQLCondition(groupedConditions, groupedOperators));
            currentLogicalOperators.clear();
        }

        if (!conditionStack.isEmpty()) {
            throw new IllegalArgumentException("Unmatched parentheses in WHERE clause.");
        }

        return currentConditions.getFirst();
    }


    public String getTokenAtIndex(Map<Integer, String[]> tokens, int index) {
        String[] data = tokens.get(index);
        String result = null;
        if (data != null) {
            result = data[0];
        }
        return result;
    }

    public boolean matchesConditions(JSONObject row, JSONSQLCondition rootCondition) {
        Map<JSONSQLCondition, JSONSQLCondition> mChildToParentMap = new LinkedHashMap<>();
        Stack<JSONSQLCondition> conditionStack = new Stack<>();
        Stack<List<Boolean>> resultStacks = new Stack<>();
        List<Boolean> resultStack = new LinkedList<>();

        // ✅ Push conditions onto stack in reverse order
        conditionStack.push(rootCondition);

        while (!conditionStack.isEmpty()) {
            JSONSQLCondition condition = conditionStack.pop();
            boolean currentResult;

            if (condition.hasSubConditions()) {
                // ✅ Start new scope for grouped conditions
                List<JSONSQLCondition> subConditions = condition.getSubConditions();
                for (JSONSQLCondition subCondition : subConditions) {
                    mChildToParentMap.put(subCondition, condition);
                    conditionStack.push(subCondition);
                }

                // ✅ Push the current result stack (new evaluation scope)
                resultStacks.push(resultStack);
                resultStack = new LinkedList<>();
            } else {
                // ✅ Evaluate atomic condition
                currentResult = evaluateCondition(row, condition);
//                System.out.println("Condition Evaluation Complete: " + condition + " = " + currentResult);
                resultStack.add(currentResult);
            }

            JSONSQLCondition parentCondition = mChildToParentMap.get(condition);

            // ✅ If we have completed all sub-conditions of a parent condition, evaluate it
            while (parentCondition != null && resultStack.size() == parentCondition.getSubConditions().size()) {
                Collections.reverse(resultStack);
                boolean evaluation = evaluateBooleanExpression(resultStack, parentCondition.getLogicalOperators());
//                System.out.println("Group Evaluation Complete: " + parentCondition + " = " + evaluation);

                // ✅ Restore the previous result stack and push the evaluation result
                resultStack = resultStacks.isEmpty() ? new LinkedList<>() : resultStacks.pop();
                resultStack.add(evaluation);

                // ✅ Move to the next parent up the tree
                parentCondition = mChildToParentMap.get(parentCondition);
            }
        }

        // ✅ Ensure stack has exactly one final value
        boolean finalResult = !resultStack.isEmpty() && resultStack.get(0);
//        System.out.println("FINAL MATCHES CONDITIONS RESULT: " + finalResult);
        return finalResult;
    }


    public int extractLimit(List<String> tokens) {
        int limitIndex = getFirstIndexOf(tokens, LIMIT_KEYWORD);
        if (limitIndex == -1 || limitIndex + 1 >= tokens.size()) {
            return -1;
        }
        return Integer.parseInt(tokens.get(limitIndex + 1));
    }


    public boolean compareValues(Object left, Object right, String operator) {
        if (left == null || right == null) {
            return false;
        } else if (left instanceof Number && right instanceof Number) {
            double a = ((Number) left).doubleValue();
            double b = ((Number) right).doubleValue();

            return switch (operator) {
                case "=" -> a == b;
                case "!=" -> a != b;
                case ">" -> a > b;
                case "<" -> a < b;
                case ">=" -> a >= b;
                case "<=" -> a <= b;
                default -> false;
            };
        }

        // Everything else: treat as strings (case-sensitive)
        String leftStr = left.toString();
        String rightStr = right.toString();

        switch (operator.toUpperCase()) {
            case "=": { return leftStr.equals(rightStr); }
            case "!=": { return !leftStr.equals(rightStr); }
            case ">": { return leftStr.compareTo(rightStr) > 0; }
            case "<": { return leftStr.compareTo(rightStr) < 0; }
            case ">=": { return leftStr.compareTo(rightStr) >= 0; }
            case "<=": { return leftStr.compareTo(rightStr) <= 0; }
            case "LIKE": {
                // Convert SQL LIKE pattern to regex (case-sensitive)
                String pattern = rightStr
                        .replace(".", "\\.")   // Escape dot
                        .replace("?", "\\?")   // Escape question mark (optional)
                        .replace("%", ".*");   // Convert SQL wildcard to regex
                return leftStr.matches("^" + pattern + "$");
            }
            default:
                return false;
        }
    }

//    public boolean compareValues(Object left, Object right, String operator) {
//        if (left == null || right == null) {
//            return false;
//        }
//
//        if (left instanceof Number && right instanceof Number) {
//            double a = ((Number) left).doubleValue();
//            double b = ((Number) right).doubleValue();
//
//            return switch (operator) {
//                case "=" -> a == b;
//                case "!=" -> a != b;
//                case ">" -> a > b;
//                case "<" -> a < b;
//                case ">=" -> a >= b;
//                case "<=" -> a <= b;
//                default -> false;
//            };
//        }
//
//        // Case-sensitive string and general object comparison
//        String leftStr = left.toString();
//        String rightStr = right.toString();
//
//        return switch (operator) {
//            case "=" -> leftStr.equals(rightStr);
//            case "!=" -> !leftStr.equals(rightStr);
//            case ">" -> leftStr.compareTo(rightStr) > 0;
//            case "<" -> leftStr.compareTo(rightStr) < 0;
//            case ">=" -> leftStr.compareTo(rightStr) >= 0;
//            case "<=" -> leftStr.compareTo(rightStr) <= 0;
//            case "LIKE" -> leftStr.contains(rightStr);
//            default -> false;
//        };
//    }

//    public Object parseLiteral(String raw) {
//        if (raw == null || raw.isEmpty()) return null;
//
//        raw = raw.trim();
//
//        // Handle quoted strings: unwrap single quotes
//        if (raw.matches("^'(.*)'$")) {
//            raw = raw.substring(1, raw.length() - 1).trim();
//
//            // If it looks like JSON, try parsing it
//            if (raw.startsWith("{") && raw.endsWith("}")) {
//                try { return JSON.parseObject(raw); } catch (Exception ignored) {}
//            }
//            if (raw.startsWith("[") && raw.endsWith("]")) {
//                try { return JSON.parseArray(raw); } catch (Exception ignored) {}
//            }
//
//            // Otherwise, treat as string
//            return raw;
//        }
//
//        // Handle JSON values outside of quotes
//        try {
//            if (raw.startsWith("{") && raw.endsWith("}")) {
//                return JSON.parseObject(raw);
//            }
//            if (raw.startsWith("[") && raw.endsWith("]")) {
//                return JSON.parseArray(raw);
//            }
//            if (raw.equalsIgnoreCase("true") || raw.equalsIgnoreCase("false")) {
//                return Boolean.parseBoolean(raw);
//            }
//            if (raw.equalsIgnoreCase("null")) {
//                return null;
//            }
//            if (raw.matches("-?\\d+\\.\\d+")) {
//                return Double.parseDouble(raw);
//            }
//            if (raw.matches("-?\\d+")) {
//                return Integer.parseInt(raw);
//            }
//        } catch (Exception ignored) {}
//
//        return raw;
//    }


    public Object parseLiteral(String raw) {
        if (raw == null || raw.isEmpty()) { return null; }

        raw = raw.trim();

        // Unwrap quoted strings (single or double)
        if ((raw.startsWith("'") && raw.endsWith("'")) || (raw.startsWith("\"") && raw.endsWith("\""))) {
            raw = raw.substring(1, raw.length() - 1).trim();

            // Try parsing JSON inside quotes
            try {
                if (raw.startsWith("{") && raw.endsWith("}")) { return JSON.parseObject(raw); }
                if (raw.startsWith("[") && raw.endsWith("]")) { return JSON.parseArray(raw); }
            } catch (Exception ignored) {}

            return raw; // Fallback to plain string
        }

        // JSON objects and arrays without quotes
        try {
            if (raw.startsWith("{") && raw.endsWith("}")) { return JSON.parseObject(raw); }
            if (raw.startsWith("[") && raw.endsWith("]")) { return JSON.parseArray(raw); }
        } catch (Exception ignored) {}

        // Booleans
        if (raw.equalsIgnoreCase("true")) { return true; }
        if (raw.equalsIgnoreCase("false")) { return false; }

        // Null
        if (raw.equalsIgnoreCase("null")) { return null; }

        // Numbers
        try {
            if (raw.matches("-?\\d+\\.\\d+")) { return Double.parseDouble(raw); }
            if (raw.matches("-?\\d+")) { return Integer.parseInt(raw); }
        } catch (Exception ignored) {}

        // Fallback to raw text
        return raw;
    }


    public boolean evaluateCondition(JSONObject row, JSONSQLCondition condition) {
        String column = condition.getColumn();
        String operator = condition.getOperator();
        String value = condition.getValue();

        if (column.equals("*") && operator.equals("=") && value.equals("*")) {
            return true; // Wildcard match
        }

        boolean looksLikeConstant = column.matches("^-?\\d+(\\.\\d+)?$")     // numeric
                || column.matches("^'.*'$")                                       // quoted string
                || column.matches("^\".*\"$");                                    // double quoted

        if (looksLikeConstant) {
            // constant = constant
            Object leftVal = parseLiteral(column);
            Object rightVal = parseLiteral(value);

            if (leftVal == null || rightVal == null) { return false; }
            boolean comparison = compareValues(leftVal, rightVal, operator);
            return comparison;
        }

        Object columnValue = getJsonPathValue(row, column);
        Object rightVal = parseLiteral(value);

        if (rightVal == null && operator.equalsIgnoreCase("IS")) {
            return columnValue == null;
        } else if (rightVal == null && operator.equalsIgnoreCase("IS NOT")) {
            return columnValue != null;
        } else {
            return compareValues(columnValue, rightVal, operator);
        }
    }

    public boolean evaluateConditionV1(JSONObject row, JSONSQLCondition condition) {
        String column = condition.getColumn();
        String operator = condition.getOperator();
        String value = condition.getValue();

//        if (row.containsKey(column))

        boolean result = false;
        Object columnValue = getJsonPathValue(row, column);


        if (column.equals("*") && operator.equals("*") && value.equals("*")) {
            result = true;
        } else if (operator.equalsIgnoreCase(IS_KEYWORD)) {
            // ✅ Handle NULL cases for IS / IS NOT
            result = (value.equalsIgnoreCase("NULL") && (columnValue == null));
        } else if (operator.equalsIgnoreCase(IS_NOT_KEYWORD)) {
            result = !(value.equalsIgnoreCase("NULL") && (columnValue == null));
        } else if (columnValue == null) {
            // ✅ Handle NULL cases for other comparisons
            result = false;
        } else {
            // if value starts with double or single quotes, treat as a string
            boolean surroundedSingleQuotes = (value.charAt(0) == '\'' && value.charAt(value.length() - 1) == '\'');
            boolean surroundedDoubleQuotes = (value.charAt(0) == '\"' && value.charAt(value.length() - 1) == '\"');
            if (surroundedSingleQuotes || surroundedDoubleQuotes) {
                value = value.substring(1, value.length() - 1);
            }

            // ✅ Handle JSON column comparison
            if (columnValue instanceof JSONObject jsonObj) {
                try {
                    JSONObject conditionJson = JSON.parseObject(value);
                    result = jsonObj.equals(conditionJson); // Compares structure and values
                } catch (Exception e) {
                    result = false; // Invalid JSON format in query
                }
            } else if (columnValue instanceof JSONArray jsonArray) {
                try {
                    JSONArray conditionArray = JSON.parseArray(value);
                    result = jsonArray.equals(conditionArray);
                } catch (Exception e) {
                    result = false; // Invalid JSON format
                }
            } else {
                boolean isConditionNumeric = isNumeric(value);
                boolean isDataNumeric = columnValue instanceof Number || isNumeric(String.valueOf(columnValue));
                if (isConditionNumeric && isDataNumeric) {
                    double columnValue2 = Double.parseDouble(String.valueOf(columnValue));
                    double conditionValue = Double.parseDouble(value);
                    switch (operator) {
                        case "=" -> result = columnValue2 == conditionValue;
                        case "!=" -> result = columnValue2 != conditionValue;
                        case ">" -> result = columnValue2 > conditionValue;
                        case "<" -> result = columnValue2 < conditionValue;
                        case ">=" -> result = columnValue2 >= conditionValue;
                        case "<=" -> result = columnValue2 <= conditionValue;
                    }
                } else {
                    // ✅ String comparisons (Case-Insensitive)
                    String rowValueStr = columnValue.toString();
                    switch (operator.toUpperCase()) {
                        case "=" -> result = rowValueStr.equalsIgnoreCase(value);
                        case "!=" -> result = !rowValueStr.equalsIgnoreCase(value);
                        case "LIKE" ->
                                result = rowValueStr.toLowerCase().contains(value.toLowerCase()); // Partial match
                        case ">" -> result = rowValueStr.compareToIgnoreCase(value) > 0;  // Lexicographical
                        case "<" -> result = rowValueStr.compareToIgnoreCase(value) < 0;
                        case ">=" -> result = rowValueStr.compareToIgnoreCase(value) >= 0;
                        case "<=" -> result = rowValueStr.compareToIgnoreCase(value) <= 0;
                    }
                }
            }
        }
        return result;
    }





    public boolean evaluateBooleanExpression(List<Boolean> booleans, List<String> logicalOperators) {
        if (booleans.isEmpty()) {
            throw new IllegalArgumentException("No booleans passed");
        }
        if (booleans.size() - 1 != logicalOperators.size()) {
            throw new IllegalArgumentException("Operators must be exactly one less than booleans");
        }

        // First pass: Evaluate all AND operations first
        List<Boolean> evaluatedBooleans = new ArrayList<>(booleans);
        List<String> remainingOperators = new ArrayList<>(logicalOperators);

        for (int i = 0; i < remainingOperators.size(); ) {
            if (remainingOperators.get(i).equalsIgnoreCase(AND_KEYWORD)) {
                boolean result = evaluatedBooleans.get(i) && evaluatedBooleans.get(i + 1);
                evaluatedBooleans.set(i, result);
                evaluatedBooleans.remove(i + 1);
                remainingOperators.remove(i);
            } else {
                i++; // Move to next operator
            }
        }

        // Second pass: Evaluate remaining OR operations left to right
        boolean finalResult = evaluatedBooleans.getFirst();
        for (int i = 0; i < remainingOperators.size(); i++) {
            finalResult = finalResult || evaluatedBooleans.get(i + 1);
        }

        return finalResult;
    }


    public boolean isNumeric(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void setAndCreateJSONValue(JSONObject root, String path, Object value) {
        List<String> tokens = tokenizeJsonPath(path);
        Object current = root;

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            boolean isLast = (i == tokens.size() - 1);

            if (current instanceof JSONObject obj) {
                if (isLast) {
                    obj.put(token, value);
                } else {
                    Object next = obj.get(token);
                    if (next == null) {
                        next = tokens.get(i + 1).matches("\\d+") ? new JSONArray() : new JSONObject();
                        obj.put(token, next);
                    }
                    current = next;
                }
            } else if (current instanceof JSONArray arr) {
                int index = Integer.parseInt(token);
                while (arr.size() <= index) { arr.add(null); }

                if (isLast) {
                    arr.set(index, value);
                } else {
                    Object next = arr.get(index);
                    if (next == null) {
                        next = tokens.get(i + 1).matches("\\d+") ? new JSONArray() : new JSONObject();
                        arr.set(index, next);
                    }
                    current = next;
                }
            } else {
                throw new IllegalStateException("Invalid path structure at: " + token);
            }
        }
    }

    public String getQueryType(String sql) {
        if (sql == null || sql.isBlank()) { return null; }

        String normalized = sql.trim();
        boolean isSquareBracketSurrounded = normalized.startsWith("[") && normalized.endsWith("]");
        boolean isCurlyBracketSurrounded = normalized.startsWith("{") && normalized.endsWith("}");

        String result = null;
        if (isSquareBracketSurrounded || isCurlyBracketSurrounded) {
            result = "INSERT";
        } else {
            normalized = normalized.toUpperCase(Locale.ROOT).substring(0, normalized.indexOf(" "));
            switch (normalized) {
                case INSERT_KEYWORD -> result = INSERT_KEYWORD;
                case SELECT_KEYWORD -> result = SELECT_KEYWORD;
                case UPDATE_KEYWORD -> result = UPDATE_KEYWORD;
                case DELETE_KEYWORD -> result = DELETE_KEYWORD;
            }
        }

        return result;
    }



    public JSONObject flattenRow(JSONObject root) {
        Map<String, Object> result = new LinkedHashMap<>();
        Deque<Map.Entry<String, Object>> stack = new ArrayDeque<>();
        stack.push(Map.entry("", root));

        while (!stack.isEmpty()) {
            Map.Entry<String, Object> current = stack.pop();
            String prefix = current.getKey();
            Object value = current.getValue();

            if (value instanceof JSONObject obj) {
                for (String key : obj.keySet()) {
                    String newKey = prefix.isEmpty() ? key : prefix + "." + key;
                    stack.push(new AbstractMap.SimpleEntry<>(newKey, obj.get(key)));
                }
            } else if (value instanceof JSONArray arr) {
                for (int i = 0; i < arr.size(); i++) {
                    String newKey = prefix + "[" + i + "]";
                    stack.push(new AbstractMap.SimpleEntry<>(newKey, arr.get(i)));
                }
            } else {
                result.put(prefix, value);
            }
        }

        JSONObject row = new JSONObject(result);
        return row;
    }



    public void normalizeRows(JSONArray table) {
        JSONObject schema = getMasterSchema(table); // Master schema
        for (int i = 0; i < table.size(); i++) {
            JSONObject row = table.getJSONObject(i);
            applySchemaNormalization(row, schema);
        }
    }

    public void applySchemaNormalization(JSONObject row, JSONObject schema) {
        for (Map.Entry<String, Object> entry : schema.entrySet()) {
            String key = entry.getKey();
            Object schemaValue = entry.getValue();

            if (!row.containsKey(key)) {
                // Add missing key with appropriate placeholder
                if (schemaValue instanceof JSONObject) {
                    JSONObject newObject = new JSONObject();
                    row.put(key, newObject);
                    applySchemaNormalization(newObject, (JSONObject) schemaValue);
                } else if (schemaValue instanceof JSONArray) {
                    JSONArray newArray = new JSONArray();
                    row.put(key, newArray);
                    // Optionally recurse if schemaArray contains object
                } else {
                    row.put(key, null);
                }
            } else {
                Object currentValue = row.get(key);

                if (schemaValue instanceof JSONObject) {
                    if (currentValue instanceof JSONObject) {
                        applySchemaNormalization((JSONObject) currentValue, (JSONObject) schemaValue);
                    } else {
                        JSONObject newObj = new JSONObject();
                        row.put(key, newObj);
                        applySchemaNormalization(newObj, (JSONObject) schemaValue);
                    }
                } else if (schemaValue instanceof JSONArray && currentValue instanceof JSONArray) {
                    JSONArray schemaArray = (JSONArray) schemaValue;
                    if (!schemaArray.isEmpty() && schemaArray.getFirst() instanceof JSONObject) {
                        for (Object item : (JSONArray) currentValue) {
                            if (item instanceof JSONObject) {
                                applySchemaNormalization((JSONObject) item, schemaArray.getJSONObject(0));
                            }
                        }
                    }
                }
            }
        }
    }





    public JSONObject joinSchemas(JSONObject target, JSONObject source) {
        Deque<Frame> stack = new ArrayDeque<>();
        stack.push(new Frame(target, source));

        while (!stack.isEmpty()) {
            Frame frame = stack.pop();
            JSONObject dest = frame.target;
            JSONObject src = frame.source;

            for (Map.Entry<String, Object> entry : src.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof JSONObject srcObj) {
                    JSONObject destObj = dest.getJSONObject(key);
                    if (destObj == null) {
                        destObj = new JSONObject();
                        dest.put(key, destObj);
                    }
                    stack.push(new Frame(destObj, srcObj));
                }
                else if (value instanceof JSONArray srcArray) {
                    JSONArray destArray = dest.getJSONArray(key);
                    if (destArray == null) {
                        destArray = new JSONArray();
                        dest.put(key, destArray);
                    }

                    JSONObject mergedSchema = null;
                    if (!destArray.isEmpty() && destArray.getFirst() instanceof JSONObject) {
                        mergedSchema = destArray.getJSONObject(0);
                    } else {
                        mergedSchema = new JSONObject();
                    }

                    for (Object item : srcArray) {
                        if (item instanceof JSONObject itemObj) {
                            stack.push(new Frame(mergedSchema, itemObj));
                        }
                    }

                    if (destArray.isEmpty()) {
                        destArray.add(mergedSchema);
                    } else if (destArray.getFirst() instanceof JSONObject) {
                        destArray.set(0, mergedSchema);
                    }
                }
                else if (!dest.containsKey(key)) {
                    dest.put(key, value);
                }
            }
        }

        return target;
    }

    public JSONObject getMasterSchema(JSONArray rows) {
        JSONObject result = new JSONObject();
        for (int i = 0; i < rows.size(); i++) {
            JSONObject row = rows.getJSONObject(i);
            joinSchemas(result, row);
        }
        return result;
    }

    private static class Frame {
        JSONObject target;
        JSONObject source;

        Frame(JSONObject target, JSONObject source) {
            this.target = target;
            this.source = source;
        }
    }





}
