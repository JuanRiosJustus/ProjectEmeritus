package main.constants;

import org.json.JSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSVTable {
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
                    "|(?<=\\S)(?=[()])|(?<=[()])(?=\\S)",  // Ensure parentheses are properly tokenized
            Pattern.CASE_INSENSITIVE
    );
    private static final Set<String> SQL_KEYWORDS = Set.of(
            "SELECT", "FROM", "WHERE", "AND", "OR", "ORDER", "BY", "LIMIT", "ASC", "DESC"
    );
    public static class Condition {
        private final String column;       // Column name
        private final String operator;     // Operator (e.g., =, !=, >, <)
        private final String value;        // Condition value
        private final List<Condition> subConditions; // Nested conditions for ( )
        private final List<String> logicalOperators;

        // ✅ Constructor for Simple Conditions
        public Condition(String mColumn, String mOperator, String mValue) {
            this.column = mColumn;
            this.operator = mOperator;
            this.value = mValue;
            this.subConditions = null;
            this.logicalOperators = null;
        }

        public Condition(List<Condition> mSubConditions, List<String> mLogicalOperators) {
            this.column = null;
            this.operator = null;
            this.value = null;
            this.subConditions = mSubConditions;
            this.logicalOperators = mLogicalOperators;
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
    private final Map<Integer, String[]> mIndexToTokenMap = new LinkedHashMap<>();
    private final Map<String, Integer> mHeaders = new LinkedHashMap<>();
    private final List<Map<String, String>> mRows = new ArrayList<>();
    public CSVTable(String csv) {
        String[] rawCsvTable = csv.split(System.lineSeparator());
        List<String> rawHeaders = parseLine(rawCsvTable[0]);

        for (String rawHeader : rawHeaders) {
            mHeaders.put(rawHeader.trim(), mHeaders.size());
        }

        for (int i = 1; i < rawCsvTable.length; i++) {
            List<String> rawRow = parseLine(rawCsvTable[i]);
            Map<String, String> row = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> headerColumn : mHeaders.entrySet()) {
                String columnName = headerColumn.getKey();
                int columnIndex = headerColumn.getValue();
                String columnValue = rawRow.get(columnIndex).trim();

                if (columnValue.startsWith("\"") && columnValue.endsWith("\"")) {
                    columnValue = columnValue.substring(1, columnValue.length() - 1);
                }

                row.put(columnName, columnValue);
            }
            mRows.add(row);
        }
    }

    private static List<String> parseLine(String line) {
        List<String> fields = new ArrayList<>();
        // The regex uses \G to continue from where the last match ended.
        Pattern pattern = Pattern.compile(
                "\\G(?:^|,)" +                   // Start at beginning or after a comma
                        "(?:" +
                        "\"((?:[^\"]|\"\")*)\"" +     // Group 1: A quoted field (allowing escaped quotes)
                        "|" +
                        "([^\",]*)" +                // Group 2: An unquoted field (no comma or quote)
                        ")"
        );
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            String field;
            if (matcher.group(1) != null) { // Remove escaped quotes: replace "" with "
                field = matcher.group(1).replace("\"\"", "\"");
            } else {
                field = matcher.group(2);
            }
            fields.add(field);
        }
        return fields;
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


    public String getTokenAtIndex(int index) {
        String[] data = mIndexToTokenMap.get(index);
        String result = null;
        if (data != null) {
            result = data[0];
        }
        return result;
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

    public int getFirstIndexOf(String token) {
        return mIndexToTokenMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue()[0].equalsIgnoreCase(token))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(-1);
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

            // 🚨 Ensure column name exists and skip commas
            if (column.equals(",")) {
                continue; // Skip commas explicitly
            }
            if (SQL_KEYWORDS.contains(column.toUpperCase()) || column.isEmpty()) {
                break; // Stop if another SQL keyword appears
            }

            // ✅ Handle single-quoted column names (e.g., 'column_name')
            if (column.startsWith("'")) {
                while (i + 1 < mIndexToTokenMap.size() && !getTokenAtIndex(i + 1).endsWith("'")) {
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

    public int getTokenCount() {
        return mIndexToTokenMap.size();
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


    public boolean matchesConditions(Map<String, String> row, Condition rootCondition) {
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

    public boolean evaluateCondition(Map<String, String> row, Condition condition) {
        String column = condition.getColumn();
        String operator = condition.getOperator();
        String value = condition.getValue();

        boolean result = false;
//        Object columnValue = null;
//        Object columnValue = getJsonPathValue(row, column);
        Object columnValue = row.getOrDefault(column, null);

        if (column.equals("*") && operator.equals("*") && value.equals("*")) {
            result = true;
        } else if (operator.equalsIgnoreCase(IS_KEYWORD)) {
            // ✅ Handle NULL cases for IS / IS NOT
            result = (value.equalsIgnoreCase("NULL") && columnValue == null);
        } else if (operator.equalsIgnoreCase(IS_NOT_KEYWORD)) {
            result = !(value.equalsIgnoreCase("NULL") && columnValue == null);
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
        boolean finalResult = evaluatedBooleans.get(0);
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

    public List<Map<String, String>>  sortResults(List<Map<String, String>> results, List<String[]> orderByColumns) {
        List<Map<String, String>>  sortedList = new ArrayList<>(results);
//        for (int i = 0; i < results.length(); i++) {
//            sortedList.add(results.getJSONObject(i));
//        }

        sortedList.sort((a, b) -> {
            for (String[] order : orderByColumns) {
                String column = order[0].toLowerCase(); // Normalize case for columns
                boolean desc = order[1].equalsIgnoreCase(DESC_KEYWORD);

                Object objA = a.get(column);
                Object objB = b.get(column);

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

        return sortedList;
    }




    public static Object tryToConvertToNumber(String input) {
        if (input == null) {
            return null;
        }

        // Trim input to avoid leading/trailing whitespace issues
        input = input.trim();

        // Check if the input is an integer, if not try double
        Object result = null;
        try {
            result = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            try {
                result = Double.parseDouble(input);
            } catch (NumberFormatException ignored) {
                result = input;
            }
        }

        // If neither numeric format applies, return the original string.
        return result;
    }

    public List<Map<String, Object>> query(String sql) {
        tokenize(sql);
        List<String> selectedColumns = extractSelectedColumns();
        Condition whereConditions = extractWhereConditions();
        List<String[]> orderByColumns = extractOrderBy();
        int limit = extractLimit();

        List<Map<String, String>> filteredResults = new ArrayList<>();
        for (Map<String, String> row : mRows) {
            System.out.println("INVESTIGATING " + row.toString());
            if (!matchesConditions(row, whereConditions)) {
                continue;
            }
            filteredResults.add(row);
            System.out.println("ACCEPTING " + row);
        }

        List<Map<String, String>> sortedResults = sortResults(filteredResults, orderByColumns);
        if (limit > 0) {
            List<Map<String, String>> limitedResults = new ArrayList<>();
            for (int i = 0; i < limit; i++) {
                limitedResults.add(sortedResults.get(i));
            }
            sortedResults = limitedResults;
        }

        List<Map<String, Object>> finalResults = new ArrayList<>();
        for (Map<String, String> row : sortedResults) {

            Map<String, String> filteredRow = new LinkedHashMap<>();
            if (selectedColumns.size() == 1 && selectedColumns.get(0).equals("*")) {
                filteredRow = row;
            } else {
                for (String column : selectedColumns) {
                    Object columnValue = row.get(column);
                    filteredRow.put(column, String.valueOf(columnValue));
                }
            }

            // Convert fields into types
            Map<String, Object> converted = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : filteredRow.entrySet()) {
                String column = entry.getKey();
                String rawValue = entry.getValue();
                Object convertedValues = tryToConvertToNumber(rawValue);
                converted.put(column, convertedValues);
            }
            // Convert row values
            finalResults.add(converted);
        }

        return finalResults;
    }


//
//    public List<List<Object>> query(String sql) {
//        tokenize(sql);
//        List<String> selectedColumns = extractSelectedColumns();
//        Condition whereConditions = extractWhereConditions();
//        List<String[]> orderByColumns = extractOrderBy();
//        int limit = extractLimit();
//
//        List<List<String>> filteredResults = new ArrayList<>();
//        for (List<String> row : mRows) {
//            System.out.println("INVESTIGATING " + row.toString());
//            if (!matchesConditions(row, whereConditions)) {
//                continue;
//            }
//            filteredResults.add(row);
//            System.out.println("ACCEPTING " + row);
//        }
//
//        List<List<String>> sortedResults = sortResults(filteredResults, orderByColumns);
//        if (limit > 0) {
//            List<List<String>> limitedResults = new ArrayList<>();
//            for (int i = 0; i < limit; i++) {
//                limitedResults.add(sortedResults.get(i));
//            }
//            sortedResults = limitedResults;
//        }
//
//        List<Map<String, Object>> finalResults = new ArrayList<>();
//        for (List<String> row : sortedResults) {
////            List<String> filteredRow = new ArrayList<>();
////
////            if (selectedColumns.size() == 1 && selectedColumns.get(0).equals("*")) {
////                filteredRow = row;
////            } else {
////                for (String column : selectedColumns) {
////                    Object columnValue = row.get(mHeaders.get(column));
////                    filteredRow.add(String.valueOf(columnValue));
////                }
////            }
//
////            Map<String, Object> filteredRow = new LinkedHashMap<>();
//
//            if (selectedColumns.size() == 1 && selectedColumns.get(0).equals("*")) {
//                filteredRow = row;
//            } else {
//                for (String column : selectedColumns) {
//                    Object columnValue = row.get(mHeaders.get(column));
//                    filteredRow.add(String.valueOf(columnValue));
//                }
//            }
//
//            // Convert row values
//            finalResults.add(filteredRow);
//        }
//
//        return finalResults;
//    }
}