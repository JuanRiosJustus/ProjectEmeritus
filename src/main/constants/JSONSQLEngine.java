package main.constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONSQLEngine {
    private static final String SELECT_KEYWORD = "SELECT";
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
    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "'([^']*)'" +  // Match quoted strings like 'New York'
                    "|\\b(SELECT|FROM|WHERE|AND|OR|ORDER|BY|LIMIT|ASC|DESC|IS|NOT)\\b" +  // Match SQL keywords
                    "|[(),=<>!]+" +  // Match standalone operators and parentheses
                    "|[^\\s,()=<>!]+" +  // Match identifiers (column names, table names)
                    "|(?<=\\S)(?=[()])|(?<=[()])(?=\\S)",  // **Fix**: Ensure parentheses are properly tokenized
            Pattern.CASE_INSENSITIVE
    );
    private static final Set<String> SQL_KEYWORDS = Set.of(
            "SELECT", "FROM", "WHERE", "AND", "OR", "ORDER", "BY", "LIMIT", "ASC", "DESC"
    );
    private final Map<Integer, String[]> mIndexToTokenMap = new LinkedHashMap<>();

    public int getFirstIndexOf(String token) {
        return mIndexToTokenMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue()[0].equalsIgnoreCase(token))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(-1);
    }

    public int getLastIndexOf(String token) {
        return mIndexToTokenMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue()[0].equalsIgnoreCase(token))
                .map(Map.Entry::getKey)
                .reduce((first, second) -> second)  // Get the last occurrence
                .orElse(-1);
    }

    public String getTokenAtIndex(int index) {
        String[] data = mIndexToTokenMap.get(index);
        String result = null;
        if (data != null) {
            result = data[0];
        }
        return result;
    }

    public int getTokenCount() {
        return mIndexToTokenMap.size();
    }

    public void tokenize(String sql) {
        mIndexToTokenMap.clear();
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

            mIndexToTokenMap.put(mIndexToTokenMap.size(), new String[]{token, type});
        }
    }

    public List<String> extractSelectedColumns() {
        List<String> columns = new ArrayList<>();

        int selectIndex = getFirstIndexOf(SELECT_KEYWORD);
        int fromIndex = getFirstIndexOf(FROM_KEYWORD);

        if (selectIndex == -1 || fromIndex == -1 || fromIndex < selectIndex) {
            throw new IllegalArgumentException("Invalid SQL query. Expected 'SELECT ... FROM ...'");
        }

        boolean expectColumn = true; // ✅ Ensures commas separate columns properly

        for (int i = selectIndex + 1; i < fromIndex; i++) {
            String token = getTokenAtIndex(i);

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

    public JSONArray executeQuery(String sql, JSONArray jsonData) {
        tokenize(sql);
        List<String> selectedColumns = extractSelectedColumns();
        Condition condition = extractWhereConditions();
        List<String[]> orderByColumns = extractOrderBy();
        int limit = extractLimit();

        JSONArray filteredResults = new JSONArray();

        for (int i = 0; i < jsonData.length(); i++) {
            JSONObject row = jsonData.getJSONObject(i);
            System.out.println("INVESTIGATING " + row.toString());
            if (!matchesConditions(row, condition)) {
                continue;
            }
            filteredResults.put(row);
            System.out.println("ACCEPTING " + row);
        }

        JSONArray sortedResults = sortResults(filteredResults, orderByColumns);

        if (limit > 0 && sortedResults.length() > limit) {
            JSONArray limitedResults = new JSONArray();
            for (int i = 0; i < limit; i++) {
                limitedResults.put(sortedResults.get(i));
            }
            sortedResults = limitedResults;
        }

        // ✅ Fix: Return JSON objects correctly
        JSONArray finalResults = new JSONArray();
        for (int i = 0; i < sortedResults.length(); i++) {
            JSONObject row = sortedResults.getJSONObject(i);
            JSONObject filteredRow = new JSONObject();

            if (selectedColumns.size() == 1 && selectedColumns.get(0).equals("*")) {
                filteredRow = row;
            } else {
                for (String column : selectedColumns) {
                    Object columnValue = getJsonPathValue(row, column);
                    filteredRow.put(column, columnValue);
                }
            }
            finalResults.put(filteredRow);
        }

        return finalResults;
    }

    public Condition extractWhereConditions() {
        Stack<List<Condition>> conditionStack = new Stack<>();
        Stack<List<String>> logicalOperatorStack = new Stack<>();

        int whereIndex = getFirstIndexOf(WHERE_KEYWORD);
        if (whereIndex == -1) {
            return new Condition("*", "*", "*");
        }

        int orderByIndex = getFirstIndexOf(ORDER_KEYWORD);
        int limitIndex = getFirstIndexOf(LIMIT_KEYWORD);
        int endIndex = getTokenCount();
        if (orderByIndex != -1) {
            endIndex = orderByIndex;
        }
        if (limitIndex != -1 && limitIndex < endIndex) {
            endIndex = limitIndex;
        }

        List<Condition> currentConditions = new ArrayList<>();
        List<String> currentLogicalOperators = new ArrayList<>();

        for (int i = whereIndex + 1; i < endIndex; i++) {
            String token = getTokenAtIndex(i);

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
                List<Condition> groupedConditions = new ArrayList<>(currentConditions);
                List<String> groupedOperators = new ArrayList<>(currentLogicalOperators);
                currentConditions = conditionStack.pop();
                currentLogicalOperators = logicalOperatorStack.pop();

                // ✅ Wrap the entire group as a single condition
                currentConditions.add(new Condition(groupedConditions, groupedOperators));
            } else if (token.equalsIgnoreCase(AND_KEYWORD) || token.equalsIgnoreCase(OR_KEYWORD)) {
                // ✅ Store logical operator
                currentLogicalOperators.add(token.toUpperCase());
            } else {
                if (i + 1 < endIndex) {
                    String column = token;
                    String operator = getTokenAtIndex(i + 1);
                    String value = null;

                    if (operator.equalsIgnoreCase(IS_KEYWORD)) {
                        if (i + 2 < endIndex && getTokenAtIndex(i + 2).equalsIgnoreCase(NOT_KEYWORD)) {
                            operator = IS_NOT_KEYWORD;
                            value = getTokenAtIndex(i + 3);
                            i += 3;
                        } else {
                            value = getTokenAtIndex(i + 2);
                            i += 2;
                        }
                    } else {
                        value = getTokenAtIndex(i + 2);
                        i += 2;
                    }

                    if (value == null) {
                        throw new IllegalArgumentException("Unmatched comparisons");
                    }

                    Condition newCondition = new Condition(column, operator, value);
                    currentConditions.add(newCondition);
                } else {
                    throw new IllegalArgumentException("Invalid WHERE clause syntax");
                }
            }
        }

        // ✅ Ensure all top-level conditions are properly grouped
        while (!currentLogicalOperators.isEmpty() && currentConditions.size() > 1) {
            List<Condition> groupedConditions = new ArrayList<>(currentConditions);
            List<String> groupedOperators = new ArrayList<>(currentLogicalOperators);

            // ✅ Wrap the entire group as a single condition
            currentConditions.clear();
            currentConditions.add(new Condition(groupedConditions, groupedOperators));
            currentLogicalOperators.clear();
        }

        if (!conditionStack.isEmpty()) {
            throw new IllegalArgumentException("Unmatched parentheses in WHERE clause.");
        }

        return currentConditions.get(0);
    }

    public List<String[]> extractOrderBy() {
        List<String[]> orderByColumns = new ArrayList<>();
        int orderIndex = getFirstIndexOf(ORDER_KEYWORD);

        if (orderIndex == -1 || orderIndex + 1 >= mIndexToTokenMap.size()) {
            return orderByColumns;
        }
        if (!getTokenAtIndex(orderIndex + 1).equalsIgnoreCase(BY_KEYWORD)) {
            return orderByColumns;
        }

        for (int i = orderIndex + 2; i < mIndexToTokenMap.size(); i++) {
            String column = getTokenAtIndex(i);

            // 🚨 Ensure column name exists
            if (column.equals(",")) {
                throw new IllegalArgumentException("Unexpected comma in ORDER BY clause.");
            }
            if (SQL_KEYWORDS.contains(column.toUpperCase()) || column.isEmpty()) {
                break; // Stop if another SQL keyword appears
            }

            // ✅ Handle quoted column names and functions
            if (column.startsWith("`") || column.startsWith("\"")) {
                while (i + 1 < mIndexToTokenMap.size() && !getTokenAtIndex(i + 1).endsWith("`") && !getTokenAtIndex(i + 1).endsWith("\"")) {
                    column += " " + getTokenAtIndex(++i);
                }
            }

            // ✅ Check if next token is ASC/DESC
            String order = ASC_KEYWORD; // Default order is ASC
            if (i + 1 < mIndexToTokenMap.size()) {
                String nextToken = getTokenAtIndex(i + 1);
                if (nextToken.equalsIgnoreCase(ASC_KEYWORD) || nextToken.equalsIgnoreCase(DESC_KEYWORD)) {
                    order = nextToken.toUpperCase();
                    i++; // ✅ Skip the next token since it's already processed
                }
            }

            orderByColumns.add(new String[]{column, order});

            // ✅ Ensure comma is followed by another column
            if (i + 1 < mIndexToTokenMap.size() && getTokenAtIndex(i + 1).equals(",")) {
                i++; // ✅ Skip the comma
                if (i + 1 >= mIndexToTokenMap.size() || SQL_KEYWORDS.contains(getTokenAtIndex(i + 1).toUpperCase())) {
                    throw new IllegalArgumentException("Invalid ORDER BY clause: trailing comma.");
                }
            }
        }

        return orderByColumns;
    }


    public int extractLimit() {
        int limitIndex = getFirstIndexOf(LIMIT_KEYWORD);
        if (limitIndex == -1 || limitIndex + 1 >= getTokenCount()) {
            return -1;
        }
        return Integer.parseInt(getTokenAtIndex(limitIndex + 1));
    }

    public boolean matchesConditions(JSONObject row, Condition rootCondition) {
        Map<Condition, Condition> mChildToParentMap = new LinkedHashMap<>();
        Stack<Condition> conditionStack = new Stack<>();
        Stack<List<Boolean>> resultStacks = new Stack<>();
        List<Boolean> resultStack = new LinkedList<>();

        // ✅ Push conditions onto stack in reverse order
        conditionStack.push(rootCondition);

        while (!conditionStack.isEmpty()) {
            Condition condition = conditionStack.pop();
            boolean currentResult;

            if (condition.hasSubConditions()) {
                // ✅ Start new scope for grouped conditions
                List<Condition> subConditions = condition.getSubConditions();
                for (Condition subCondition : subConditions) {
                    mChildToParentMap.put(subCondition, condition);
                    conditionStack.push(subCondition);
                }

                // ✅ Push the current result stack (new evaluation scope)
                resultStacks.push(resultStack);
                resultStack = new LinkedList<>();
            } else {
                // ✅ Evaluate atomic condition
                currentResult = evaluateCondition(row, condition);
                System.out.println("Condition Evaluation Complete: " + condition + " = " + currentResult);
                resultStack.add(currentResult);
            }

            Condition parentCondition = mChildToParentMap.get(condition);

            // ✅ If we have completed all sub-conditions of a parent condition, evaluate it
            while (parentCondition != null && resultStack.size() == parentCondition.getSubConditions().size()) {
                Collections.reverse(resultStack);
                boolean evaluation = evaluateBooleanExpression(resultStack, parentCondition.getLogicalOperators());
                System.out.println("Group Evaluation Complete: " + parentCondition + " = " + evaluation);

                // ✅ Restore the previous result stack and push the evaluation result
                resultStack = resultStacks.isEmpty() ? new LinkedList<>() : resultStacks.pop();
                resultStack.add(evaluation);

                // ✅ Move to the next parent up the tree
                parentCondition = mChildToParentMap.get(parentCondition);
            }
        }

        // ✅ Ensure stack has exactly one final value
        boolean finalResult = !resultStack.isEmpty() && resultStack.get(0);
        System.out.println("FINAL MATCHES CONDITIONS RESULT: " + finalResult);
        return finalResult;
    }

    public boolean evaluateCondition(JSONObject row, Condition condition) {
        String column = condition.getColumn();
        String operator = condition.getOperator();
        String value = condition.getValue();

        boolean result = false;
        Object columnValue = getJsonPathValue(row, column);

        if (column.equals("*") && operator.equals("*") && value.equals("*")) {
            result = true;
        } else if (operator.equalsIgnoreCase(IS_KEYWORD)) {
            // ✅ Handle NULL cases for IS / IS NOT
            result = (value.equalsIgnoreCase("NULL") && (columnValue == null || columnValue == JSONObject.NULL));
        } else if (operator.equalsIgnoreCase(IS_NOT_KEYWORD)) {
            result = !(value.equalsIgnoreCase("NULL") && (columnValue == null || columnValue == JSONObject.NULL));
        } else if (columnValue == null || columnValue == JSONObject.NULL) {
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
                    JSONObject conditionJson = new JSONObject(value);
                    result = jsonObj.similar(conditionJson); // Compares structure and values
                } catch (Exception e) {
                    result = false; // Invalid JSON format in query
                }
            } else if (columnValue instanceof JSONArray jsonArray) {
                try {
                    JSONArray conditionArray = new JSONArray(value);
                    result = jsonArray.similar(conditionArray);
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

    private boolean isNumeric(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public Object getJsonPathValue(JSONObject jsonObject, String path) {
        String[] keys = path.split("\\.");
        Object current = jsonObject;

        for (String key : keys) {
            if (current instanceof JSONObject obj && obj.has(key)) {
                current = obj.get(key);
            } else if (current instanceof JSONArray arr) {
                try {
                    int index = Integer.parseInt(key);
                    if (index >= arr.length()) {
                        return null;
                    }
                    current = arr.get(index);
                } catch (NumberFormatException e) {
                    return null; // Invalid array index in path
                }
            } else {
                return null;
            }
        }
        return current;
    }

    public JSONArray sortResults(JSONArray results, List<String[]> orderByColumns) {
        List<JSONObject> sortedList = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
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
        boolean finalResult = evaluatedBooleans.get(0);
        for (int i = 0; i < remainingOperators.size(); i++) {
            finalResult = finalResult || evaluatedBooleans.get(i + 1);
        }

        return finalResult;
    }

    public static class Condition {
        private final String column;       // Column name
        private final String operator;     // Operator (e.g., =, !=, >, <)
        private final String value;        // Condition value
        private final List<Condition> subConditions; // Nested conditions for ( )
        private final List<String> logicalOperators;
        private final String note;

        // ✅ Constructor for Simple Conditions
        public Condition(String mColumn, String mOperator, String mValue) {
            this.column = mColumn;
            this.operator = mOperator;
            this.value = mValue;
            this.subConditions = null;
            this.logicalOperators = null;
            this.note = null;
        }

        public Condition(List<Condition> mSubConditions, List<String> mLogicalOperators) {
            this.column = null;
            this.operator = null;
            this.value = null;
            this.subConditions = mSubConditions;
            this.logicalOperators = mLogicalOperators;
            this.note = null;
        }

        // ✅ Check if it's a grouped condition
        public boolean hasSubConditions() {
            return subConditions != null;
        }

        public String getColumn() {
            return column;
        }

        public String getOperator() {
            return operator;
        }

        public String getValue() {
            return value;
        }

        public List<Condition> getSubConditions() {
            return subConditions;
        }

        public List<String> getLogicalOperators() {
            return logicalOperators;
        }

        @Override
        public String toString() {
            if (hasSubConditions()) {
                StringBuilder sb = new StringBuilder();
                sb.append("(");

                for (int i = 0; i < subConditions.size(); i++) {
                    sb.append(subConditions.get(i).toString());

                    // ✅ Append logical operator only if it's not the last condition
                    if (i < logicalOperators.size()) {
                        sb.append(" ").append(logicalOperators.get(i)).append(" ");
                    }
                }

                sb.append(")");
                return sb.toString();
            } else {
                return column + " " + operator + " " + value;
            }
        }
    }
}