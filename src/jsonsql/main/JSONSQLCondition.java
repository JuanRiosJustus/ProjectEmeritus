package jsonsql.main;

import java.util.List;
import java.util.Objects;

public class JSONSQLCondition {
    private final String column;       // Column name
    private final String operator;     // Operator (e.g., =, !=, >, <)
    private final String value;        // Condition value
    private final List<JSONSQLCondition> subConditions; // Nested conditions for ( )
    private final List<String> logicalOperators;

    // ✅ Constructor for Simple Conditions
    public JSONSQLCondition(String mColumn, String mOperator, String mValue) {
        this.column = mColumn;
        this.operator = mOperator;
        this.value = mValue;
        this.subConditions = null;
        this.logicalOperators = null;
    }

    public JSONSQLCondition(List<JSONSQLCondition> mSubConditions, List<String> mLogicalOperators) {
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

    public List<JSONSQLCondition> getSubConditions() {
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

            for (int i = 0; i < Objects.requireNonNull(subConditions).size(); i++) {
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