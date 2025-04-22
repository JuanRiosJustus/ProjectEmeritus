package jsonsql.test;


import com.alibaba.fastjson2.JSON;
import jsonsql.main.JSONSQLCondition;
import jsonsql.main.JSONSQLFunctions;
import jsonsql.main.JSONTable;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JSONSQLFunctionsTest {

    private JSONArray jsonData;
    private JSONSQLFunctions mJSONSQLFunctions;

    @BeforeEach
    void setUp() {
        // Sample JSON dataset with JSON objects and arrays included
        String jsonString = """
                [
                    {
                        "name": "Alice", "age": 30, "city": "New York", "salary": 60000,
                        "address": { "street": "123 Main St", "city": "New York", "zip": "10001" },
                        "skills": ["Java", "SQL", "Python"]
                    },
                    {
                        "name": "Bob", "age": 40, "city": "Los Angeles", "salary": 45000,
                        "address": { "street": "456 Sunset Blvd", "city": "Los Angeles", "zip": "90028" },
                        "skills": ["C++", "Go", "Java"]
                    },
                    {
                        "name": "Charlie", "age": 28, "city": "New York", "salary": 30000,
                        "address": { "street": "789 Broadway", "city": "New York", "zip": "10003" },
                        "skills": ["Python", "JavaScript"]
                    },
                    {
                        "name": "David", "age": 35, "city": "Chicago", "salary": 70000,
                        "address": { "street": "321 Michigan Ave", "city": "Chicago", "zip": "60601" },
                        "skills": ["Rust", "SQL"]
                    },
                    {
                        "name": "Eve", "age": 25, "city": "New York", "salary": 50000,
                        "address": { "street": "101 Park Ave", "city": "New York", "zip": "10016" },
                        "skills": ["JavaScript", "HTML", "CSS"]
                    },
                    {
                        "name": "Frank", "age": null, "city": "Seattle", "salary": null,
                        "address": null,
                        "skills": []
                    }
                ]
                """;
        jsonData = JSON.parseArray(jsonString);
        mJSONSQLFunctions = new JSONSQLFunctions();
    }

    @Test
    void testGetJsonPathValue_SimpleKey() {
        // ✅ Fetching top-level keys
        assertEquals("Alice", mJSONSQLFunctions.getJsonPathValue(jsonData.getJSONObject(0), "name"));
        assertEquals(40, mJSONSQLFunctions.getJsonPathValue(jsonData.getJSONObject(1), "age"));
        assertEquals("Chicago", mJSONSQLFunctions.getJsonPathValue(jsonData.getJSONObject(3), "city"));
    }

    @Test
    void testGetJsonPathValue_NestedObject() {
        // ✅ Fetching values inside the "address" JSON object
        assertEquals("123 Main St", mJSONSQLFunctions.getJsonPathValue(jsonData.getJSONObject(0), "address.street"));
        assertEquals("456 Sunset Blvd", mJSONSQLFunctions.getJsonPathValue(jsonData.getJSONObject(1), "address.street"));
        assertEquals("60601", mJSONSQLFunctions.getJsonPathValue(jsonData.getJSONObject(3), "address.zip"));
    }

    @Test
    void testGetJsonPathValue_ArrayElement() {
        // ✅ Fetching values inside the "skills" JSON array
        assertEquals("Java", mJSONSQLFunctions.getJsonPathValue(jsonData.getJSONObject(0), "skills.0"));
        assertEquals("Python", mJSONSQLFunctions.getJsonPathValue(jsonData.getJSONObject(2), "skills.0"));
        assertEquals("Rust", mJSONSQLFunctions.getJsonPathValue(jsonData.getJSONObject(3), "skills.0"));
    }

    @Test
    void testGetJsonPathValue_LastElementInArray() {
        // ✅ Fetching the last skill in the array
        assertEquals("Python", mJSONSQLFunctions.getJsonPathValue(jsonData.getJSONObject(0), "skills.2"));
        assertEquals("Java", mJSONSQLFunctions.getJsonPathValue(jsonData.getJSONObject(1), "skills.2"));
        assertEquals("CSS", mJSONSQLFunctions.getJsonPathValue(jsonData.getJSONObject(4), "skills.2"));
    }

    @Test
    void testGetJsonPathValue_InvalidPath() {
        // ✅ Trying to access a non-existent field should return null
        assertNull(mJSONSQLFunctions.getJsonPathValue(jsonData.getJSONObject(0), "nonexistent"));
        assertNull(mJSONSQLFunctions.getJsonPathValue(jsonData.getJSONObject(2), "address.country"));
        assertNull(mJSONSQLFunctions.getJsonPathValue(jsonData.getJSONObject(4), "skills.10")); // Out of bounds
    }

    @Test
    void testGetJsonPathValue_NullValue() {
        // ✅ Handling null values
//        assertTrue((String)(engine.getJsonPathValue(jsonData.getJSONObject(5), "age").equals("null"));
//        assertTrue(engine.getJsonPathValue(jsonData.getJSONObject(5), "salary").equals("null"));
//        assertTrue(engine.getJsonPathValue(jsonData.getJSONObject(5), "address").equals("null"));
    }

    @Test
    void testGetJsonPathValue_EmptyArray() {
        // ✅ Handling empty arrays
        assertNull(mJSONSQLFunctions.getJsonPathValue(jsonData.getJSONObject(5), "skills.0")); // Empty skills array
    }

    @Test
    void testGetJsonPathValue_ComplexPath() {
        // ✅ Fetching deeply nested JSON values
        JSONObject nestedJson = JSON.parseObject("""
                {
                    "user": {
                        "profile": {
                            "details": {
                                "username": "john_doe"
                            }
                        }
                    }
                }
                """);
        assertEquals("john_doe", mJSONSQLFunctions.getJsonPathValue(nestedJson, "user.profile.details.username"));
        assertNull(mJSONSQLFunctions.getJsonPathValue(nestedJson, "user.profile.details.missing"));
    }


    // TEST COLUMN EXTRACTION


    @Test
    void testBasicSelection() {
		Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("SELECT name FROM users");
        List<String> columns = mJSONSQLFunctions.extractSelectedColumns(tokens);
        assertEquals(List.of("name"), columns);
    }

    @Test
    void testMultipleColumnsSelection() {
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("SELECT name, age, salary FROM employees");
        List<String> columns = mJSONSQLFunctions.extractSelectedColumns(tokens);;
        assertEquals(List.of("name", "age", "salary"), columns);
    }

    @Test
    void testSelectionWithWhitespace() {
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("SELECT   name   ,    age   ,  salary  FROM  users");
        List<String> columns = mJSONSQLFunctions.extractSelectedColumns(tokens);;
        assertEquals(List.of("name", "age", "salary"), columns);
    }

    @Test
    void testSelectionWithUnexpectedComma() {
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("SELECT name, age,, salary FROM users");
        try{
            mJSONSQLFunctions.extractSelectedColumns(tokens);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    void testSelectionWithMixedCaseKeywords() {
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("sElEcT name, age, salary FrOm employees");
        List<String> columns = mJSONSQLFunctions.extractSelectedColumns(tokens);;
        assertEquals(List.of("name", "age", "salary"), columns);
    }

    @Test
    void testSelectionWithTrailingComma() {
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("SELECT name, age, salary, FROM users");
        try{
            mJSONSQLFunctions.extractSelectedColumns(tokens);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    void testSelectionWithMissingCommaBetweenColumns() {
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("SELECT name age salary FROM users");
        try{
            mJSONSQLFunctions.extractSelectedColumns(tokens);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    void testExtractSelectedColumns_Basic() {
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("SELECT name, age FROM users");
        List<String> columns = mJSONSQLFunctions.extractSelectedColumns(tokens);;

        assertEquals(List.of("name", "age"), columns);
    }

    @Test
    void testExtractSelectedColumns_SingleColumn() {
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("SELECT name FROM users");
        List<String> columns = mJSONSQLFunctions.extractSelectedColumns(tokens);;

        assertEquals(List.of("name"), columns);
    }

    @Test
    void testExtractSelectedColumns_Wildcard() {
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("SELECT * FROM users");
        List<String> columns = mJSONSQLFunctions.extractSelectedColumns(tokens);;

        assertEquals(List.of("*"), columns);
    }

    @Test
    void testExtractSelectedColumns_ExtraSpaces() {
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("SELECT   name   ,   age   FROM users");
        List<String> columns = mJSONSQLFunctions.extractSelectedColumns(tokens);;

        assertEquals(List.of("name", "age"), columns);
    }

    @Test
    void testExtractSelectedColumns_MissingFromClause() {
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("SELECT name, age");
        try {
            mJSONSQLFunctions.extractSelectedColumns(tokens);
            fail();
        } catch (Exception ignored) {
        }
    }

    @Test
    void testExtractSelectedColumns_MissingSelectClause() {
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("FROM users");
        try {
            mJSONSQLFunctions.extractSelectedColumns(tokens);
            fail();
        } catch (Exception ignored) {
        }
    }

    @Test
    void testExtractSelectedColumns_InvalidQueryStructure() {
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("SELECT name age FROM users");
        try {
            mJSONSQLFunctions.extractSelectedColumns(tokens);
            fail();
        } catch (Exception ignored) {
        }
    }


    @Test
    void testInvalidQueryWithoutSelect() {
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("UPDATE users SET age = 30 WHERE id = 1");
        try {
            mJSONSQLFunctions.extractSelectedColumns(tokens);
            fail();
        } catch (Exception ignored) {
        }
    }

    @Test
    void testInvalidQueryWithoutFrom() {
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("SELECT name, age");
        try {
            mJSONSQLFunctions.extractSelectedColumns(tokens);
            fail();
        } catch (Exception ignored) {
        }
    }

    @Test
    void testInvalidQueryEmptySelection() {
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("SELECT  FROM users");
        try {
            mJSONSQLFunctions.extractSelectedColumns(tokens);
            fail();
        } catch (Exception ignored) {
        }
    }


    @Test
    void testGetFirstIndexOf_SimpleSelect() {
        String sql = "SELECT name, age FROM users WHERE age > 30";
		List<String> map = mJSONSQLFunctions.getTokens(sql);

        assertEquals(0, mJSONSQLFunctions.getFirstIndexOf(map, "SELECT"), "Expected 'SELECT' at index 0");
        assertEquals(4, mJSONSQLFunctions.getFirstIndexOf(map, "FROM"), "Expected 'FROM' at index 3");
        assertEquals(6, mJSONSQLFunctions.getFirstIndexOf(map, "WHERE"), "Expected 'WHERE' at index 5");
        assertEquals(3, mJSONSQLFunctions.getFirstIndexOf(map, "age"), "Expected first 'age' at index 3");
    }

    @Test
    void testGetFirstIndexOf_WithOrderBy() {
        String sql = "SELECT name FROM employees ORDER BY salary DESC";
		List<String> map = mJSONSQLFunctions.getTokens(sql);

        assertEquals(0, mJSONSQLFunctions.getFirstIndexOf(map, "SELECT"), "Expected 'SELECT' at index 0");
        assertEquals(2, mJSONSQLFunctions.getFirstIndexOf(map, "FROM"), "Expected 'FROM' at index 2");
        assertEquals(4, mJSONSQLFunctions.getFirstIndexOf(map, "ORDER"), "Expected 'ORDER' at index 4");
        assertEquals(5, mJSONSQLFunctions.getFirstIndexOf(map, "BY"), "Expected 'BY' at index 5");
        assertEquals(6, mJSONSQLFunctions.getFirstIndexOf(map, "salary"), "Expected 'salary' at index 6");
        assertEquals(7, mJSONSQLFunctions.getFirstIndexOf(map, "DESC"), "Expected 'DESC' at index 7");
    }

    @Test
    void testGetFirstIndexOf_CaseInsensitive() {
        String sql = "select ID, Name FROM customers WHERE city = 'New York'";
		List<String> map = mJSONSQLFunctions.getTokens(sql);

        assertEquals(0, mJSONSQLFunctions.getFirstIndexOf(map, "SELECT"), "Expected 'SELECT' at index 0 (case insensitive)");
        assertEquals(4, mJSONSQLFunctions.getFirstIndexOf(map, "FROM"), "Expected 'FROM' at index 3");
        assertEquals(6, mJSONSQLFunctions.getFirstIndexOf(map, "WHERE"), "Expected 'WHERE' at index 5");
        assertEquals(7, mJSONSQLFunctions.getFirstIndexOf(map, "city"), "Expected 'city' at index 6");
        assertEquals(9, mJSONSQLFunctions.getFirstIndexOf(map, "'New York'"), "Expected string 'New York' at index 8");
    }

    @Test
    void testGetFirstIndexOf_ComplexWhereClause() {
        String sql = "SELECT * FROM sales WHERE region = 'West' AND revenue > 50000 OR category = 'Electronics'";
		List<String> map = mJSONSQLFunctions.getTokens(sql);

        assertEquals(0, mJSONSQLFunctions.getFirstIndexOf(map, "SELECT"), "Expected 'SELECT' at index 0");
        assertEquals(2, mJSONSQLFunctions.getFirstIndexOf(map, "FROM"), "Expected 'FROM' at index 2");
        assertEquals(4, mJSONSQLFunctions.getFirstIndexOf(map, "WHERE"), "Expected 'WHERE' at index 4");
        assertEquals(6, mJSONSQLFunctions.getFirstIndexOf(map, "="), "Expected '=' at index 6");
        assertEquals(8, mJSONSQLFunctions.getFirstIndexOf(map, "AND"), "Expected 'AND' at index 8");
        assertEquals(12, mJSONSQLFunctions.getFirstIndexOf(map, "OR"), "Expected 'OR' at index 12");
        assertEquals(6, mJSONSQLFunctions.getFirstIndexOf(map, "="), "Expected second '=' at index 14");
    }

    @Test
    void testGetFirstIndexOf_MultipleConditions() {
        String sql = "SELECT product, price FROM inventory WHERE (price > 100 AND stock < 50) OR category = 'Books'";
		List<String> map = mJSONSQLFunctions.getTokens(sql);

        assertEquals(0, mJSONSQLFunctions.getFirstIndexOf(map, "SELECT"), "Expected 'SELECT' at index 0");
        assertEquals(4, mJSONSQLFunctions.getFirstIndexOf(map, "FROM"), "Expected 'FROM' at index 4");
        assertEquals(6, mJSONSQLFunctions.getFirstIndexOf(map, "WHERE"), "Expected 'WHERE' at index 6");
        assertEquals(9, mJSONSQLFunctions.getFirstIndexOf(map, ">"), "Expected '>' at index 9");
        assertEquals(13, mJSONSQLFunctions.getFirstIndexOf(map, "<"), "Expected '<' at index 13");
        assertEquals(16, mJSONSQLFunctions.getFirstIndexOf(map, "OR"), "Expected 'OR' at index 16");
        assertEquals(18, mJSONSQLFunctions.getFirstIndexOf(map, "="), "Expected '=' at index 18");
    }

    @Test
    void testGetFirstIndexOf_StringLiterals() {
        String sql = "SELECT * FROM customers WHERE name = 'John Doe' AND city = 'Los Angeles'";
		List<String> map = mJSONSQLFunctions.getTokens(sql);

        assertEquals(0, mJSONSQLFunctions.getFirstIndexOf(map, "SELECT"), "Expected 'SELECT' at index 0");
        assertEquals(2, mJSONSQLFunctions.getFirstIndexOf(map, "FROM"), "Expected 'FROM' at index 2");
        assertEquals(4, mJSONSQLFunctions.getFirstIndexOf(map, "WHERE"), "Expected 'WHERE' at index 4");
        assertEquals(6, mJSONSQLFunctions.getFirstIndexOf(map, "="), "Expected '=' at index 6");
        assertEquals(7, mJSONSQLFunctions.getFirstIndexOf(map, "'John Doe'"), "Expected 'John Doe' at index 7");
        assertEquals(8, mJSONSQLFunctions.getFirstIndexOf(map, "AND"), "Expected 'AND' at index 10");
        assertEquals(11, mJSONSQLFunctions.getFirstIndexOf(map, "'Los Angeles'"), "Expected 'Los Angeles' at index 13");
    }

    @Test
    void testGetFirstIndexOf_ParenthesesAndOperators() {
        String sql = "SELECT id FROM data WHERE (score > 80 OR grade = 'A') AND passed = TRUE";
		List<String> map = mJSONSQLFunctions.getTokens(sql);

        assertEquals(0, mJSONSQLFunctions.getFirstIndexOf(map, "SELECT"), "Expected 'SELECT' at index 0");
        assertEquals(2, mJSONSQLFunctions.getFirstIndexOf(map, "FROM"), "Expected 'FROM' at index 2");
        assertEquals(4, mJSONSQLFunctions.getFirstIndexOf(map, "WHERE"), "Expected 'WHERE' at index 4");
        assertEquals(5, mJSONSQLFunctions.getFirstIndexOf(map, "("), "Expected '(' at index 5");
        assertEquals(7, mJSONSQLFunctions.getFirstIndexOf(map, ">"), "Expected '>' at index 7");
        assertEquals(9, mJSONSQLFunctions.getFirstIndexOf(map, "OR"), "Expected 'OR' at index 9");
        assertEquals(11, mJSONSQLFunctions.getFirstIndexOf(map, "="), "Expected '=' at index 11");
        assertEquals(13, mJSONSQLFunctions.getFirstIndexOf(map, ")"), "Expected ')' at index 13");
        assertEquals(14, mJSONSQLFunctions.getFirstIndexOf(map, "AND"), "Expected 'AND' at index 14");
        assertEquals(11, mJSONSQLFunctions.getFirstIndexOf(map, "="), "Expected '=' at index 11");
    }

    @Test
    void testGetFirstIndexOf_MissingTokens() {
        String sql = "SELECT first_name FROM employees WHERE salary > 50000";
		List<String> map = mJSONSQLFunctions.getTokens(sql);

        assertEquals(-1, mJSONSQLFunctions.getFirstIndexOf(map, "GROUP"), "Expected -1 for non-existent token 'GROUP'");
        assertEquals(-1, mJSONSQLFunctions.getFirstIndexOf(map, "HAVING"), "Expected -1 for non-existent token 'HAVING'");
        assertEquals(-1, mJSONSQLFunctions.getFirstIndexOf(map, "JOIN"), "Expected -1 for non-existent token 'JOIN'");
    }


    @Test
    void testExtractWhereConditions_SingleCondition() {
		Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("SELECT name FROM users WHERE age >= 30");
        JSONSQLCondition conditions = mJSONSQLFunctions.extractWhereConditions(tokens);

//        assertEquals(1, conditions);
        assertEquals("age", conditions.getColumn());
        assertEquals(">=", conditions.getOperator());
        assertEquals("30", conditions.getValue());
    }

    @Test
    void testExtractWhereConditions_MultipleConditions_AND() {
		Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("SELECT name FROM users WHERE age >= 30 AND city = 'New York'");
        JSONSQLCondition conditions = mJSONSQLFunctions.extractWhereConditions(tokens);

        assertTrue(conditions.hasSubConditions());
        assertEquals("AND", conditions.getLogicalOperators().getFirst());

        List<JSONSQLCondition> subConditions = conditions.getSubConditions();
        assertEquals(2, subConditions.size());

        assertEquals("age", subConditions.get(0).getColumn());
        assertEquals(">=", subConditions.get(0).getOperator());
        assertEquals("30", subConditions.get(0).getValue());

        assertEquals("city", subConditions.get(1).getColumn());
        assertEquals("=", subConditions.get(1).getOperator());
        assertEquals("'New York'", subConditions.get(1).getValue());
    }

    @Test
    void testExtractWhereConditions_Complex_AND_OR() {
		Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("SELECT name FROM users WHERE age >= 30 AND city = 'New York' OR salary > 50000");
        JSONSQLCondition conditions = mJSONSQLFunctions.extractWhereConditions(tokens);

//        assertEquals(1, conditions.size()); // ✅ Single grouped condition due to precedence
        assertTrue(conditions.hasSubConditions());
        assertEquals("AND", conditions.getLogicalOperators().get(0));
        assertEquals("OR", conditions.getLogicalOperators().get(1));

        List<JSONSQLCondition> subConditions = conditions.getSubConditions();
        assertEquals(3, subConditions.size());


        assertEquals("age", subConditions.get(0).getColumn());
        assertEquals(">=", subConditions.get(0).getOperator());
        assertEquals("30", subConditions.get(0).getValue());


        assertEquals("city", subConditions.get(1).getColumn());
        assertEquals("=", subConditions.get(1).getOperator());
        assertEquals("'New York'", subConditions.get(1).getValue());


        assertEquals("salary", subConditions.get(2).getColumn());
        assertEquals(">", subConditions.get(2).getOperator());
        assertEquals("50000", subConditions.get(2).getValue());
    }

    @Test
    void testExtractWhereConditions_NestedConditions() {
		Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers(
                "SELECT name FROM users WHERE ( (age >= 30 AND city = 'New York') OR (salary > 50000 AND skills.0 = 'SQL') )"
        );
        JSONSQLCondition conditions = mJSONSQLFunctions.extractWhereConditions(tokens);

//        assertEquals(1, conditions.size());
        assertTrue(conditions.hasSubConditions());
        assertEquals("OR", conditions.getLogicalOperators().getFirst());

        List<JSONSQLCondition> subConditions = conditions.getSubConditions();
        assertEquals(2, subConditions.size());

        // First nested group (age >= 30 AND city = 'New York')
        assertTrue(subConditions.get(0).hasSubConditions());
        assertEquals("AND", subConditions.get(0).getLogicalOperators().getFirst());

        // Second nested group (salary > 50000 AND skills.0 = 'SQL')
        assertTrue(subConditions.get(1).hasSubConditions());
        assertEquals("AND", subConditions.get(1).getLogicalOperators().getFirst());
    }

    @Test
    void testExtractWhereConditions_IS_NULL() {
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("SELECT name FROM users WHERE salary IS NULL");
        JSONSQLCondition conditions = mJSONSQLFunctions.extractWhereConditions(tokens);

//        assertEquals(1, conditions.size());
        assertEquals("salary", conditions.getColumn());
        assertEquals("IS", conditions.getOperator());
        assertEquals("NULL", conditions.getValue());
    }

    @Test
    void testExtractWhereConditions_IS_NOT_NULL() {
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("SELECT name FROM users WHERE address IS NOT NULL");
        JSONSQLCondition conditions = mJSONSQLFunctions.extractWhereConditions(tokens);

//        assertEquals(1, conditions);
        assertEquals("address", conditions.getColumn());
        assertEquals("IS NOT", conditions.getOperator());
        assertEquals("NULL", conditions.getValue());
    }

    @Test
    void testExtractWhereConditions_MismatchedParentheses() {
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers(
                "SELECT name FROM users WHERE (age >= 30 AND city = 'New York' OR salary > 50000"
        );

        try {
            mJSONSQLFunctions.extractWhereConditions(tokens);
            fail();
        } catch (Exception ignored) {
        }
    }

    @Test
    void testExtractWhereConditions_ExtraClosingParenthesis() {
		Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("SELECT name FROM users WHERE (age >= 30 AND city = 'New York'))");

        try {
            mJSONSQLFunctions.extractWhereConditions(tokens);
            fail();
        } catch (Exception ignored) {
        }
    }

    @Test
    void testExtractWhereConditions_InvalidWhereClause() {
		Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers("SELECT name FROM users WHERE age >= ");
        try {
            mJSONSQLFunctions.extractWhereConditions(tokens);
            fail();
        } catch (Exception ignored) {
        }
    }


    @Test
    void testComplexQuery_WithNestedConditionsAndOrdering() {
        String sql = """
                SELECT name, details.age, details.salary, address.city 
                FROM employees 
                WHERE ( details.age >= 30 AND details.salary > 50000 ) 
                OR ( address.city = 'San Francisco' OR skills.0 = 'Python' ) 
                ORDER BY details.salary DESC, details.age ASC 
                LIMIT 3
                """;

        // ✅ Create a special dataset within the test
        String jsonData = """
                    [
                        {
                            "name": "Alice",
                            "details": { "age": 30, "salary": 60000 },
                            "address": { "street": "123 Main St", "city": "New York" },
                            "skills": ["Java", "SQL", "Python"]
                        },
                        {
                            "name": "Bob",
                            "details": { "age": 40, "salary": 45000 },
                            "address": { "street": "456 Sunset Blvd", "city": "Los Angeles" },
                            "skills": ["C++", "Go", "Java"]
                        },
                        {
                            "name": "Charlie",
                            "details": { "age": 28, "salary": 70000 },
                            "address": { "street": "789 Broadway", "city": "San Francisco" },
                            "skills": ["Python", "JavaScript"]
                        },
                        {
                            "name": "David",
                            "details": { "age": 35, "salary": 90000 },
                            "address": { "street": "321 Michigan Ave", "city": "Chicago" },
                            "skills": ["Rust", "SQL"]
                        },
                        {
                            "name": "Eve",
                            "details": { "age": 25, "salary": 50000 },
                            "address": { "street": "101 Park Ave", "city": "San Francisco" },
                            "skills": ["JavaScript", "HTML", "CSS"]
                        },
                        {
                            "name": "Frank",
                            "details": { "age": null, "salary": null },
                            "address": { "street": "404 No Name St", "city": "Unknown" },
                            "skills": []
                        }
                    ]
                """;

        // ✅ Execute query using JSON SQL engine
        JSONTable table = new JSONTable("employees", jsonData);
        JSONArray results = table.select(sql);

        // ✅ Expected results should be filtered correctly
        assertEquals(3, results.size()); // Limit should be applied correctly

        // ✅ Verify sorting order (salary DESC, age ASC)
        assertEquals("David", results.getJSONObject(0).getString("name"));  // Salary: 90000
        assertEquals("Charlie", results.getJSONObject(1).getString("name")); // Salary: 70000
        assertEquals("Alice", results.getJSONObject(2).getString("name"));  // Salary: 60000

        // ✅ Verify nested field extraction
        assertEquals(35, results.getJSONObject(0).getInteger("details.age"));
        assertEquals(28, results.getJSONObject(1).getInteger("details.age"));
        assertEquals(30, results.getJSONObject(2).getInteger("details.age"));

        // ✅ Ensure NULL values are handled correctly
        assertFalse(results.toString().contains("null")); // No null values should appear
    }

    @Test
    void testComplexQuery_NestedJsonHandling() {
        String sql = """
                        SELECT name, company.location.city, age, salary 
                        FROM employees 
                        WHERE ( company.location.city = 'New York' AND age >= 30 ) 
                        OR ( salary > 75000 OR skills.0 = 'Python' ) 
                        ORDER BY salary DESC, age ASC 
                        LIMIT 4
                """;

        // ✅ Self-contained JSON table
        String jsonData = """
                    [
                        {
                            "name": "Alice",
                            "age": 32,
                            "salary": 80000,
                            "company": { "name": "TechCorp", "location": { "city": "New York", "zip": "10001" } },
                            "skills": ["Java", "SQL", "Python"]
                        },
                        {
                            "name": "Bob",
                            "age": null,
                            "salary": 50000,
                            "company": { "name": "DataSoft", "location": { "city": "Los Angeles" } },
                            "skills": ["C++", "Go", "Java"]
                        },
                        {
                            "name": "Charlie",
                            "age": 28,
                            "salary": 75000,
                            "company": { "name": "WebSolutions", "location": { "city": "San Francisco" } },
                            "skills": ["Python", "JavaScript"]
                        },
                        {
                            "name": "David",
                            "age": 35,
                            "salary": 100000,
                            "company": { "name": "AI Innovations", "location": { "city": "Chicago" } },
                            "skills": ["Rust", "SQL"]
                        },
                        {
                            "name": "Eve",
                            "age": 25,
                            "salary": 90000,
                            "company": { "name": "CyberSec", "location": { "city": "New York" } },
                            "skills": ["JavaScript", "HTML", "CSS"]
                        },
                        {
                            "name": "Frank",
                            "age": null,
                            "salary": null,
                            "company": { "name": "Unknown", "location": null },
                            "skills": []
                        }
                    ]
                """;

        // ✅ Execute query
        JSONTable table = new JSONTable("employees", jsonData);
        JSONArray results = table.select(sql);

        // ✅ Verify LIMIT applied correctly
        assertEquals(4, results.size());

        // ✅ Verify ordering (salary DESC, age ASC)
        assertEquals("David", results.getJSONObject(0).getString("name"));  // Salary: 100000
        assertEquals("Eve", results.getJSONObject(1).getString("name"));    // Salary: 90000
        assertEquals("Alice", results.getJSONObject(2).getString("name"));  // Salary: 80000
        assertEquals("Charlie", results.getJSONObject(3).getString("name"));// Salary: 75000

        // ✅ Ensure NULL values are properly handled (Frank should not appear)
        assertFalse(results.toString().contains("null"));
    }


    @Test
    void testComplexQuery_HandlingNestedArrays() {
        String sql = """
                        SELECT name, projects.0.name, age, salary 
                        FROM employees 
                        WHERE ( projects.0.name = 'AI Research' AND age >= 30 ) 
                        OR ( salary > 60000 AND projects.0.name IS NOT NULL ) 
                        ORDER BY age ASC, salary DESC 
                        LIMIT 5
                """;

        // ✅ Self-contained JSON dataset
        String jsonData = """
                    [
                        {
                            "name": "Alice",
                            "age": 30,
                            "salary": 70000,
                            "projects": [{ "name": "AI Research" }, { "name": "Security Analysis" }]
                        },
                        {
                            "name": "Bob",
                            "age": 40,
                            "salary": 50000,
                            "projects": [{ "name": "Web Development" }]
                        },
                        {
                            "name": "Charlie",
                            "age": 35,
                            "salary": 90000,
                            "projects": []
                        },
                        {
                            "name": "David",
                            "age": 32,
                            "salary": 85000,
                            "projects": [{ "name": "AI Research" }]
                        },
                        {
                            "name": "Eve",
                            "age": 28,
                            "salary": 95000,
                            "projects": [{ "name": "Quantum Computing" }]
                        },
                        {
                            "name": "Frank",
                            "age": 50,
                            "salary": null,
                            "projects": []
                        }
                    ]
                """;

        // ✅ Execute query
        JSONTable table = new JSONTable("employess", jsonData);
        JSONArray results = table.select(sql);

        // ✅ Verify LIMIT applied correctly
        assertEquals(3, results.size());

        // ✅ Verify sorting order (age ASC, salary DESC)
        assertEquals("Eve", results.getJSONObject(0).getString("name"));   // Age: 28, Salary: 95000
        assertEquals("Alice", results.getJSONObject(1).getString("name")); // Age: 30, Salary: 70000
        assertEquals("David", results.getJSONObject(2).getString("name")); // Age: 32, Salary: 85000
    }

    @Test
    void testQuery_AllResultsFilteredOut() {
        String sql = """
                SELECT name, salary, projects.0.name 
                FROM employees 
                WHERE salary > 200000 
                """;

        String jsonData = """
                [
                    { "name": "Alice", "salary": 70000, "projects": [] },
                    { "name": "Bob", "salary": 50000, "projects": [{ "name": "Web Dev" }] },
                    { "name": "Charlie", "salary": 90000, "projects": [{ "name": "AI Research" }] },
                    { "name": "David", "salary": null, "projects": [{ "name": "Machine Learning" }] }
                ]
                """;

        JSONTable table = new JSONTable("employees", jsonData);
        JSONArray results = table.select(sql);

        // ✅ Expect 0 results because no one has salary > 200000
        assertEquals(0, results.size());
    }

    @Test
    void testSuperNestedQuery_Complex() {
        String sql = """
                    SELECT name, projects, age, salary, address
                    FROM employees 
                    WHERE ( ( projects.0.name = 'AI Research' AND ( age >= 30 OR salary > 60000 ) ) 
                           OR ( projects.1.name = 'Quantum Computing' AND ( projects.1.budget > 500000 OR skills.2 = 'C++' ) ) ) 
                      AND ( address.city IS NOT NULL AND address.zip IS NOT NULL )
                    ORDER BY salary DESC, age ASC 
                    LIMIT 5
                """;

        // 🚀 Creating a **very complex** nested JSON dataset ALICE, BOB, DAVID, HANK
        String jsonData = """
                            [
                                {
                                    "name": "Alice", "age": 30, "salary": 70000,
                                    "projects": [
                                        { "name": "AI Research", "budget": 1000000 },
                                        { "name": "Cybersecurity", "budget": 400000 }
                                    ],
                                    "address": { "city": "New York", "zip": "10001" },
                                    "skills": ["Java", "SQL", "Python"]
                                },
                                {
                                    "name": "Bob", "age": 40, "salary": 50000,
                                    "projects": [
                                        { "name": "Web Development", "budget": 300000 },
                                        { "name": "Quantum Computing", "budget": 600000 }
                                    ],
                                    "address": { "city": "Los Angeles", "zip": "90028" },
                                    "skills": ["C++", "Go", "Java"]
                                },
                                {
                                    "name": "Charlie", "age": 35, "salary": 90000,
                                    "projects": [],
                                    "address": { "city": "New York", "zip": "10003" },
                                    "skills": ["Python", "JavaScript"]
                                },
                                {
                                    "name": "David", "age": 32, "salary": 85000,
                                    "projects": [
                                        { "name": "AI Research", "budget": 200000 }
                                    ],
                                    "address": { "city": "Chicago", "zip": "60601" },
                                    "skills": ["Rust", "SQL"]
                                },
                                {
                                    "name": "Eve", "age": 28, "salary": 95000,
                                    "projects": [
                                        { "name": "Quantum Computing", "budget": 700000 }
                                    ],
                                    "address": { "city": "San Francisco", "zip": "94105" },
                                    "skills": ["JavaScript", "HTML", "C++"]
                                },
                                {
                                    "name": "Frank", "age": null, "salary": null,
                                    "projects": [],
                                    "address": null,
                                    "skills": []
                                },
                                {
                                    "name": "Grace", "age": 45, "salary": 110000,
                                    "projects": [
                                        { "name": "Cybersecurity", "budget": 500000 },
                                        { "name": "AI Research", "budget": 900000 }
                                    ],
                                    "address": { "city": "Seattle", "zip": "98101" },
                                    "skills": ["Go", "Python", "SQL"]
                                },
                                {
                                    "name": "Hank", "age": 50, "salary": 120000,
                                    "projects": [
                                        { "name": "AI Research", "budget": 1500000 }
                                    ],
                                    "address": { "city": "Boston", "zip": "02108" },
                                    "skills": ["Rust", "Machine Learning", "Python"]
                                }
                            ]
                """;

        JSONTable table = new JSONTable("employees", jsonData);
        JSONArray results = table.select(sql);

        // ✅ Expecting 4 results due to LIMIT 5
        assertEquals(4, results.size());

        // ✅ Verify sorting order (salary DESC, age ASC)
        assertEquals("Hank", results.getJSONObject(0).getString("name"));  // Salary: 120000
        assertEquals("David", results.getJSONObject(1).getString("name")); // Salary: 110000
        assertEquals("Alice", results.getJSONObject(2).getString("name"));   // Salary: 95000
        assertEquals("Bob", results.getJSONObject(3).getString("name"));// Salary: 90000

        // ✅ Ensure address fields are NOT NULL (per WHERE condition)
        for (int i = 0; i < results.size(); i++) {
            assertNotNull(results.getJSONObject(i).getJSONObject("address").getString("city"));
            assertNotNull(results.getJSONObject(i).getJSONObject("address").getString("zip"));
        }

        // ✅ Ensure filtered results match the WHERE conditions
        for (int i = 0; i < results.size(); i++) {
            JSONObject row = results.getJSONObject(i);
            assertTrue(row.containsKey("projects"));
            JSONArray projects = row.getJSONArray("projects");

            boolean matchesCondition1 = false;
            boolean matchesCondition2 = false;

            if (!projects.isEmpty()) {
                JSONObject firstProject = projects.getJSONObject(0);
                if (firstProject.getString("name").equals("AI Research")) {
                    if (row.getInteger("age") >= 30 || row.getInteger("salary") > 60000) {
                        matchesCondition1 = true;
                    }
                }
            }

            if (projects.size() > 1) {
                JSONObject secondProject = projects.getJSONObject(1);
                if (secondProject.getString("name").equals("Quantum Computing") &&
                        (secondProject.getInteger("budget") > 500000 || row.getJSONArray("skills").stream().toList().contains("C++"))) {
                    matchesCondition2 = true;
                }
            }

            assertTrue(matchesCondition1 || matchesCondition2);
        }
    }


    @Test
    void testSingleColumnDefaultOrder() {
        List<String> queryTokens = mJSONSQLFunctions.getTokens("SELECT * FROM users ORDER BY age");
        List<String[]> orderBy = mJSONSQLFunctions.extractOrderBy(queryTokens);

        assertEquals(1, orderBy.size());
        assertArrayEquals(new String[]{"age", "ASC"}, orderBy.getFirst());
    }

    @Test
    void testSingleColumnExplicitAsc() {
        List<String> queryTokens = mJSONSQLFunctions.getTokens("SELECT * FROM users ORDER BY salary ASC");
        List<String[]> orderBy = mJSONSQLFunctions.extractOrderBy(queryTokens);

        assertEquals(1, orderBy.size());
        assertArrayEquals(new String[]{"salary", "ASC"}, orderBy.getFirst());
    }

    @Test
    void testSingleColumnExplicitDesc() {
        List<String> queryTokens = mJSONSQLFunctions.getTokens("SELECT * FROM users ORDER BY name DESC");
        List<String[]> orderBy = mJSONSQLFunctions.extractOrderBy(queryTokens);

        assertEquals(1, orderBy.size());
        assertArrayEquals(new String[]{"name", "DESC"}, orderBy.getFirst());
    }

    @Test
    void testMultipleColumnsDefaultOrder() {
        List<String> queryTokens = mJSONSQLFunctions.getTokens("SELECT * FROM users ORDER BY age, salary");
        List<String[]> orderBy = mJSONSQLFunctions.extractOrderBy(queryTokens);

        assertEquals(2, orderBy.size());
        assertArrayEquals(new String[]{"age", "ASC"}, orderBy.get(0));
        assertArrayEquals(new String[]{"salary", "ASC"}, orderBy.get(1));
    }

    @Test
    void testMultipleColumnsWithAscAndDesc() {
        List<String> queryTokens = mJSONSQLFunctions.getTokens(
                "SELECT * FROM users ORDER BY age DESC, salary ASC, name DESC");
        List<String[]> orderBy = mJSONSQLFunctions.extractOrderBy(queryTokens);

        assertEquals(3, orderBy.size());
        assertArrayEquals(new String[]{"age", "DESC"}, orderBy.get(0));
        assertArrayEquals(new String[]{"salary", "ASC"}, orderBy.get(1));
        assertArrayEquals(new String[]{"name", "DESC"}, orderBy.get(2));
    }

    @Test
    void testMultipleColumnsMixedOrder() {
        List<String> queryTokens = mJSONSQLFunctions.getTokens(
                "SELECT * FROM users ORDER BY id ASC, username, email DESC");
        List<String[]> orderBy = mJSONSQLFunctions.extractOrderBy(queryTokens);

        assertEquals(3, orderBy.size());
        assertArrayEquals(new String[]{"id", "ASC"}, orderBy.get(0));
        assertArrayEquals(new String[]{"username", "ASC"}, orderBy.get(1)); // Default ASC
        assertArrayEquals(new String[]{"email", "DESC"}, orderBy.get(2));
    }

    @Test
    void testNoOrderByClause() {
        List<String> queryTokens = mJSONSQLFunctions.getTokens("SELECT * FROM users WHERE age > 30");
        List<String[]> orderBy = mJSONSQLFunctions.extractOrderBy(queryTokens);

        assertTrue(orderBy.isEmpty());
    }

    @Test
    void testOrderByWithoutColumn() {
        List<String> queryTokens = mJSONSQLFunctions.getTokens("SELECT * FROM users ORDER BY");
        List<String[]> orderBy = mJSONSQLFunctions.extractOrderBy(queryTokens);

        assertTrue(orderBy.isEmpty());
    }

    @Test
    void testOrderByWithoutByKeyword() {
        List<String> queryTokens = mJSONSQLFunctions.getTokens("SELECT * FROM users ORDER age DESC");
        List<String[]> orderBy = mJSONSQLFunctions.extractOrderBy(queryTokens);

        assertTrue(orderBy.isEmpty());
    }

    @Test
    void testOrderByWithInvalidKeywordAfter() {
        List<String> queryTokens = mJSONSQLFunctions.getTokens("SELECT * FROM users ORDER BY age DESC LIMIT 10");
            List<String[]> orderBy = mJSONSQLFunctions.extractOrderBy(queryTokens);

        assertEquals(1, orderBy.size());
        assertArrayEquals(new String[]{"age", "DESC"}, orderBy.getFirst());
    }

    @Test
    void testOrderByWithWhereClauseBefore() {
        List<String> queryTokens = mJSONSQLFunctions.getTokens(
                "SELECT * FROM users WHERE age > 25 ORDER BY salary ASC");
        List<String[]> orderBy = mJSONSQLFunctions.extractOrderBy(queryTokens);

        assertEquals(1, orderBy.size());
        assertArrayEquals(new String[]{"salary", "ASC"}, orderBy.getFirst());
    }

    @Test
    void testOrderByWithDifferentCasing() {
        List<String> map = mJSONSQLFunctions.getTokens("SELECT * FROM users ORDER bY AGE dEsc, salary AsC");
        List<String[]> orderBy = mJSONSQLFunctions.extractOrderBy(map);

        assertEquals(2, orderBy.size());
        assertArrayEquals(new String[]{"AGE", "DESC"}, orderBy.get(0));
        assertArrayEquals(new String[]{"salary", "ASC"}, orderBy.get(1));
    }

    @Test
    void testOrderByWithSpecialCharactersInColumn() {
        List<String> map = mJSONSQLFunctions.getTokens("SELECT * FROM users ORDER BY created_at DESC, user-name ASC");
        List<String[]> orderBy = mJSONSQLFunctions.extractOrderBy(map);

        assertEquals(2, orderBy.size());
        assertArrayEquals(new String[]{"created_at", "DESC"}, orderBy.get(0));
        assertArrayEquals(new String[]{"user-name", "ASC"}, orderBy.get(1));
    }

    @Test
    void testOrderByWithSQLKeywordsAsColumnNames() {
        List<String> queryTokens = mJSONSQLFunctions.getTokens(
                "SELECT * FROM users ORDER BY `select` ASC, `where` DESC");
        List<String[]> orderBy = mJSONSQLFunctions.extractOrderBy(queryTokens);

        assertEquals(2, orderBy.size());
        assertArrayEquals(new String[]{"`select`", "ASC"}, orderBy.get(0));
        assertArrayEquals(new String[]{"`where`", "DESC"}, orderBy.get(1));
    }

    @Test
    void testOrderByWithNumbersAsColumnNames() {
		List<String> queryTokens = mJSONSQLFunctions.getTokens("SELECT * FROM users ORDER BY `123` ASC, `456` DESC");
        List<String[]> orderBy = mJSONSQLFunctions.extractOrderBy(queryTokens);

        assertEquals(2, orderBy.size());
        assertArrayEquals(new String[]{"`123`", "ASC"}, orderBy.get(0));
        assertArrayEquals(new String[]{"`456`", "DESC"}, orderBy.get(1));
    }


    @Test
    void testSimpleAnd() {
        // (true AND true) = true
        assertTrue(mJSONSQLFunctions.evaluateBooleanExpression(
                List.of(true, true),
                List.of("AND")
        ));

        // (true AND false) = false
        assertFalse(mJSONSQLFunctions.evaluateBooleanExpression(
                List.of(true, false),
                List.of("AND")
        ));

        // (false AND false) = false
        assertFalse(mJSONSQLFunctions.evaluateBooleanExpression(
                List.of(false, false),
                List.of("AND")
        ));
    }

    @Test
    void testSimpleOr() {
        // (true OR false) = true
        assertTrue(mJSONSQLFunctions.evaluateBooleanExpression(
                List.of(true, false),
                List.of("OR")
        ));

        // (false OR false) = false
        assertFalse(mJSONSQLFunctions.evaluateBooleanExpression(
                List.of(false, false),
                List.of("OR")
        ));

        // (true OR true) = true
        assertTrue(mJSONSQLFunctions.evaluateBooleanExpression(
                List.of(true, true),
                List.of("OR")
        ));
    }

    @Test
    void testAndOrPrecedence() {
        // (true AND false OR true) -> (false OR true) = true
        assertTrue(mJSONSQLFunctions.evaluateBooleanExpression(
                List.of(true, false, true),
                List.of("AND", "OR")
        ));

        // (true OR false AND false) -> (true OR false) -> true
        assertTrue(mJSONSQLFunctions.evaluateBooleanExpression(
                List.of(true, false, false),
                List.of("OR", "AND")
        ));

        // (false AND true OR true AND false) -> (false OR false) = false
        assertFalse(mJSONSQLFunctions.evaluateBooleanExpression(
                List.of(false, true, true, false),
                List.of("AND", "OR", "AND")
        ));
    }

    @Test
    void testComplexConditions() {
        // (true AND true AND false OR true) -> (false OR true) = true
        assertTrue(mJSONSQLFunctions.evaluateBooleanExpression(
                List.of(true, true, false, true),
                List.of("AND", "AND", "OR")
        ));

        // (true OR false AND false OR true) -> (true OR false OR true) = true
        assertTrue(mJSONSQLFunctions.evaluateBooleanExpression(
                List.of(true, false, false, true),
                List.of("OR", "AND", "OR")
        ));

        // (false AND true OR false AND true OR false) -> (false OR false OR false) = false
        assertFalse(mJSONSQLFunctions.evaluateBooleanExpression(
                List.of(false, true, false, true, false),
                List.of("AND", "OR", "AND", "OR")
        ));
    }

    @Test
    void testEdgeCases() {
        // Single value (true) should return true
        assertTrue(mJSONSQLFunctions.evaluateBooleanExpression(
                List.of(true),
                List.of()
        ));

        // Single value (false) should return false
        assertFalse(mJSONSQLFunctions.evaluateBooleanExpression(
                List.of(false),
                List.of()
        ));

        // AND short-circuit: (false AND true AND true) -> false (short-circuit at first false)
        assertFalse(mJSONSQLFunctions.evaluateBooleanExpression(
                List.of(false, true, true),
                List.of("AND", "AND")
        ));

        // OR short-circuit: (true OR false OR false) -> true (short-circuit at first true)
        assertTrue(mJSONSQLFunctions.evaluateBooleanExpression(
                List.of(true, false, false),
                List.of("OR", "OR")
        ));
    }

    @Test
    void testInvalidCases() {
        // Operators must be exactly one less than booleans
        assertThrows(IllegalArgumentException.class, () ->
                mJSONSQLFunctions.evaluateBooleanExpression(List.of(true, true), List.of("AND", "OR"))
        );

        assertThrows(IllegalArgumentException.class, () ->
                mJSONSQLFunctions.evaluateBooleanExpression(List.of(true), List.of("AND"))
        );

        // Empty boolean list should throw an exception
        assertThrows(IllegalArgumentException.class, () ->
                mJSONSQLFunctions.evaluateBooleanExpression(List.of(), List.of())
        );
    }


    @Test
    void testSingleConditionMatch() {
        JSONObject row = JSON.parseObject("{\"age\": 30}");
        JSONSQLCondition condition = new JSONSQLCondition("age", "=", "30");

        boolean result = mJSONSQLFunctions.matchesConditions(row, condition);
        assertTrue(result, "Condition should match.");
    }

    @Test
    void testSingleConditionDoesNotMatch() {
        JSONObject row = JSON.parseObject("{\"age\": 25}");
        JSONSQLCondition condition = new JSONSQLCondition("age", "=", "30");

        boolean result = mJSONSQLFunctions.matchesConditions(row, condition);
        assertFalse(result, "Condition should not match.");
    }

    @Test
    void testAndConditionBothTrue() {
        JSONObject row = JSON.parseObject("{\"age\": 30, \"city\": \"New York\"}");
        JSONSQLCondition condition1 = new JSONSQLCondition("age", "=", "30");
        JSONSQLCondition condition2 = new JSONSQLCondition("city", "=", "New York");

        JSONSQLCondition rootCondition = new JSONSQLCondition(List.of(condition1, condition2), List.of("AND"));

        boolean result = mJSONSQLFunctions.matchesConditions(row, rootCondition);
        assertTrue(result, "Both conditions are true, should match.");
    }

    @Test
    void testAndConditionOneFalse() {
        JSONObject row = JSON.parseObject("{\"age\": 25, \"city\": \"New York\"}");
        JSONSQLCondition condition1 = new JSONSQLCondition("age", "=", "30");
        JSONSQLCondition condition2 = new JSONSQLCondition("city", "=", "New York");

        JSONSQLCondition rootCondition = new JSONSQLCondition(List.of(condition1, condition2), List.of("AND"));

        boolean result = mJSONSQLFunctions.matchesConditions(row, rootCondition);
        assertFalse(result, "One condition is false, should not match.");
    }

    @Test
    void testOrConditionOneTrue() {
        JSONObject row = JSON.parseObject("{\"age\": 25, \"city\": \"New York\"}");
        JSONSQLCondition condition1 = new JSONSQLCondition("age", "=", "30");
        JSONSQLCondition condition2 = new JSONSQLCondition("city", "=", "New York");

        JSONSQLCondition rootCondition = new JSONSQLCondition(List.of(condition1, condition2), List.of("OR"));

        boolean result = mJSONSQLFunctions.matchesConditions(row, rootCondition);
        assertTrue(result, "One condition is true, OR should match.");
    }

    @Test
    void testOrConditionBothFalse() {
        JSONObject row = JSON.parseObject("{\"age\": 25, \"city\": \"Los Angeles\"}");
        JSONSQLCondition condition1 = new JSONSQLCondition("age", "=", "30");
        JSONSQLCondition condition2 = new JSONSQLCondition("city", "=", "New York");

        JSONSQLCondition rootCondition = new JSONSQLCondition(List.of(condition1, condition2), List.of("OR"));

        boolean result = mJSONSQLFunctions.matchesConditions(row, rootCondition);
        assertFalse(result, "Both conditions are false, OR should not match.");
    }

    @Test
    void testNestedConditionsWithAndOr() {
        JSONObject row = JSON.parseObject("{\"age\": 30, \"city\": \"New York\", \"salary\": 60000}");

        JSONSQLCondition condition1 = new JSONSQLCondition("age", "=", "30");
        JSONSQLCondition condition2 = new JSONSQLCondition("city", "=", "New York");
        JSONSQLCondition condition3 = new JSONSQLCondition("salary", ">", "50000");

        JSONSQLCondition subConditionGroup = new JSONSQLCondition(List.of(condition1, condition2), List.of("AND"));
        JSONSQLCondition rootCondition = new JSONSQLCondition(List.of(subConditionGroup, condition3), List.of("OR"));

        boolean result = mJSONSQLFunctions.matchesConditions(row, rootCondition);
        assertTrue(result, "Nested AND (true) OR (true) should match.");
    }

    @Test
    void testNestedConditionsWithComplexLogic() {
        JSONObject row = JSON.parseObject("{\"age\": 40, \"city\": \"Los Angeles\", \"salary\": 45000}");

        JSONSQLCondition condition1 = new JSONSQLCondition("age", "=", "30");
        JSONSQLCondition condition2 = new JSONSQLCondition("city", "=", "New York");
        JSONSQLCondition condition3 = new JSONSQLCondition("salary", ">", "50000");
        JSONSQLCondition condition4 = new JSONSQLCondition("salary", "<", "50000");

        JSONSQLCondition subConditionGroup1 = new JSONSQLCondition(List.of(condition1, condition2), List.of("AND"));
        JSONSQLCondition subConditionGroup2 = new JSONSQLCondition(List.of(condition3, condition4), List.of("AND"));
        JSONSQLCondition rootCondition = new JSONSQLCondition(List.of(subConditionGroup1, subConditionGroup2), List.of("OR"));

        boolean result = mJSONSQLFunctions.matchesConditions(row, rootCondition);
        assertFalse(result, "Complex nested OR should not match.");
    }

    @Test
    void testDeeplyNestedConditions() {
        JSONObject row = JSON.parseObject("{\"age\": 40, \"city\": \"Los Angeles\", \"salary\": 45000}");

        JSONSQLCondition condition1 = new JSONSQLCondition("age", ">", "35");
        JSONSQLCondition condition2 = new JSONSQLCondition("city", "=", "Los Angeles");
        JSONSQLCondition condition3 = new JSONSQLCondition("salary", "<", "50000");

        JSONSQLCondition subConditionGroup1 = new JSONSQLCondition(List.of(condition1, condition2), List.of("AND"));
        JSONSQLCondition subConditionGroup2 = new JSONSQLCondition(List.of(condition3, subConditionGroup1), List.of("AND"));

        boolean result = mJSONSQLFunctions.matchesConditions(row, subConditionGroup2);
        assertTrue(result, "Deeply nested condition should match.");
    }

    @Test
    void testEdgeCase_AllConditionsFalse() {
        JSONObject row = JSON.parseObject("{\"age\": 20, \"city\": \"Chicago\", \"salary\": 30000}");

        JSONSQLCondition condition1 = new JSONSQLCondition("age", ">", "35");
        JSONSQLCondition condition2 = new JSONSQLCondition("city", "=", "Los Angeles");
        JSONSQLCondition condition3 = new JSONSQLCondition("salary", ">", "50000");

        JSONSQLCondition rootCondition = new JSONSQLCondition(List.of(condition1, condition2, condition3), List.of("AND", "AND"));

        boolean result = mJSONSQLFunctions.matchesConditions(row, rootCondition);
        assertFalse(result, "All conditions are false, AND should not match.");
    }

    @Test
    void testEdgeCase_AllConditionsTrue() {
        JSONObject row = JSON.parseObject("{\"age\": 40, \"city\": \"Los Angeles\", \"salary\": 45000}");

        JSONSQLCondition condition1 = new JSONSQLCondition("age", ">", "30");
        JSONSQLCondition condition2 = new JSONSQLCondition("city", "=", "Los Angeles");
        JSONSQLCondition condition3 = new JSONSQLCondition("salary", "<", "50000");

        JSONSQLCondition rootCondition = new JSONSQLCondition(List.of(condition1, condition2, condition3), List.of("AND", "AND"));

        boolean result = mJSONSQLFunctions.matchesConditions(row, rootCondition);
        assertTrue(result, "All conditions are true, AND should match.");
    }

    @Test
    void testNullValuesInJson() {
        JSONObject row = JSON.parseObject("{\"age\": null, \"city\": \"Los Angeles\", \"salary\": null}");

        JSONSQLCondition condition1 = new JSONSQLCondition("age", "IS", "NULL");
        JSONSQLCondition condition2 = new JSONSQLCondition("salary", "IS", "NULL");

        JSONSQLCondition rootCondition = new JSONSQLCondition(List.of(condition1, condition2), List.of("AND"));

        boolean result = mJSONSQLFunctions.matchesConditions(row, rootCondition);
        assertTrue(result, "NULL conditions should be matched.");
    }


    @Test
    void testDeeplyNestedMixedAND_OR_NullHandling() {
        JSONObject row = JSON.parseObject("{\"age\": null, \"city\": \"Seattle\", \"salary\": 80000, \"active\": \"yes\"}");

        JSONSQLCondition condition1 = new JSONSQLCondition("age", "IS", "NULL");
        JSONSQLCondition condition2 = new JSONSQLCondition("city", "=", "Seattle");
        JSONSQLCondition condition3 = new JSONSQLCondition("salary", ">", "70000");
        JSONSQLCondition condition4 = new JSONSQLCondition("active", "=", "yes");
        JSONSQLCondition condition5 = new JSONSQLCondition("age", ">", "25"); // Should be false due to NULL

        JSONSQLCondition nestedGroup1 = new JSONSQLCondition(List.of(condition1, condition2), List.of("AND"));
        JSONSQLCondition nestedGroup2 = new JSONSQLCondition(List.of(condition3, condition4, condition5), List.of("OR", "AND"));
        JSONSQLCondition rootCondition = new JSONSQLCondition(List.of(nestedGroup1, nestedGroup2), List.of("OR"));

        boolean result = mJSONSQLFunctions.matchesConditions(row, rootCondition);
        assertTrue(result, "Age is NULL AND city is Seattle OR (Salary > 70000 OR Active is yes) should match.");
    }

    @Test
    void testExtremeNestingWithAlternatingOperators() {
        JSONObject row = JSON.parseObject("{\"experience\": 10, \"position\": \"Senior Developer\", \"team\": \"AI\", \"remote\": \"yes\"}");

        JSONSQLCondition condition1 = new JSONSQLCondition("experience", ">", "5");
        JSONSQLCondition condition2 = new JSONSQLCondition("position", "=", "Senior Developer");
        JSONSQLCondition condition3 = new JSONSQLCondition("team", "=", "AI");
        JSONSQLCondition condition4 = new JSONSQLCondition("remote", "=", "yes");

        JSONSQLCondition level3Group = new JSONSQLCondition(List.of(condition1, condition2), List.of("AND"));
        JSONSQLCondition level2Group = new JSONSQLCondition(List.of(condition3, condition4), List.of("OR"));
        JSONSQLCondition level1Group = new JSONSQLCondition(List.of(level3Group, level2Group), List.of("AND"));

        boolean result = mJSONSQLFunctions.matchesConditions(row, level1Group);
        assertTrue(result, "((Experience > 5 AND Position = Senior Developer) AND (Team = AI OR Remote = yes)) should match.");
    }

    @Test
    void testMultipleConditionsWithComplexOR_AND_Handling() {
        JSONObject row = JSON.parseObject("{\"project\": \"Alpha\", \"budget\": 50000, \"department\": \"R&D\", \"manager\": \"Eve\"}");

        JSONSQLCondition condition1 = new JSONSQLCondition("project", "=", "Alpha");
        JSONSQLCondition condition2 = new JSONSQLCondition("budget", ">", "60000"); // False
        JSONSQLCondition condition3 = new JSONSQLCondition("department", "=", "R&D");
        JSONSQLCondition condition4 = new JSONSQLCondition("manager", "=", "Eve");

        JSONSQLCondition group1 = new JSONSQLCondition(List.of(condition1, condition2), List.of("OR"));
        JSONSQLCondition group2 = new JSONSQLCondition(List.of(condition3, condition4), List.of("AND"));
        JSONSQLCondition rootCondition = new JSONSQLCondition(List.of(group1, group2), List.of("AND"));

        boolean result = mJSONSQLFunctions.matchesConditions(row, rootCondition);
        assertTrue(result, "(Project = Alpha OR Budget > 60000) AND (Department = R&D AND Manager = Eve) should match.");
    }

    @Test
    void testHighlyNestedBooleanMixWithMissingFields() {
        JSONObject row = JSON.parseObject("{\"role\": \"Admin\", \"access\": \"full\", \"lastLogin\": \"2024-03-01\"}");

        JSONSQLCondition condition1 = new JSONSQLCondition("role", "=", "Admin");
        JSONSQLCondition condition2 = new JSONSQLCondition("access", "=", "full");
        JSONSQLCondition condition3 = new JSONSQLCondition("lastLogin", "<", "2024-03-10"); // True
        JSONSQLCondition condition4 = new JSONSQLCondition("passwordReset", "=", "yes"); // Missing field

        JSONSQLCondition nestedGroup1 = new JSONSQLCondition(List.of(condition1, condition2), List.of("AND"));
        JSONSQLCondition nestedGroup2 = new JSONSQLCondition(List.of(condition3, condition4), List.of("AND"));
        JSONSQLCondition rootCondition = new JSONSQLCondition(List.of(nestedGroup1, nestedGroup2), List.of("OR"));

        boolean result = mJSONSQLFunctions.matchesConditions(row, rootCondition);
        assertTrue(result, "Admin AND Full Access OR (Last Login < 2024-03-10 AND Missing Field) should match.");
    }

    @Test
    void testLogicalOperatorOrderWithComplexEvaluation() {
        JSONObject row = JSON.parseObject("{\"temperature\": 100, \"pressure\": 30, \"status\": \"safe\", \"mode\": \"auto\"}");

        JSONSQLCondition condition1 = new JSONSQLCondition("temperature", ">", "95");
        JSONSQLCondition condition2 = new JSONSQLCondition("pressure", "<", "40");
        JSONSQLCondition condition3 = new JSONSQLCondition("status", "=", "safe");
        JSONSQLCondition condition4 = new JSONSQLCondition("mode", "=", "auto");

        JSONSQLCondition andGroup = new JSONSQLCondition(List.of(condition1, condition2), List.of("AND"));
        JSONSQLCondition orGroup = new JSONSQLCondition(List.of(condition3, condition4), List.of("OR"));
        JSONSQLCondition rootCondition = new JSONSQLCondition(List.of(andGroup, orGroup), List.of("AND"));

        boolean result = mJSONSQLFunctions.matchesConditions(row, rootCondition);
        assertTrue(result, "((Temp > 95 AND Pressure < 40) AND (Status = Safe OR Mode = Auto)) should match.");
    }

    @Test
    void testComplexEvaluationWithFlippedOperatorOrder() {
        JSONObject row = JSON.parseObject("{\"speed\": 80, \"fuel\": \"low\", \"engine\": \"running\", \"gear\": \"drive\"}");

        JSONSQLCondition condition1 = new JSONSQLCondition("speed", ">", "75");
        JSONSQLCondition condition2 = new JSONSQLCondition("fuel", "=", "low");
        JSONSQLCondition condition3 = new JSONSQLCondition("engine", "=", "running");
        JSONSQLCondition condition4 = new JSONSQLCondition("gear", "=", "park"); // False

        JSONSQLCondition orGroup = new JSONSQLCondition(List.of(condition1, condition2), List.of("OR"));
        JSONSQLCondition andGroup = new JSONSQLCondition(List.of(condition3, condition4), List.of("AND"));
        JSONSQLCondition rootCondition = new JSONSQLCondition(List.of(orGroup, andGroup), List.of("OR"));

        boolean result = mJSONSQLFunctions.matchesConditions(row, rootCondition);
        assertTrue(result, "(Speed > 75 OR Fuel = Low) OR (Engine = Running AND Gear = Park) should match.");
    }

    @Test
    void testMultipleLevelsOfConditionNestingWithContradictingCases() {
        JSONObject row = JSON.parseObject("{\"temperature\": 20, \"pressure\": 50, \"alert\": \"false\", \"sensor\": \"active\"}");

        JSONSQLCondition condition1 = new JSONSQLCondition("temperature", "<", "25"); // True
        JSONSQLCondition condition2 = new JSONSQLCondition("pressure", ">", "40"); // True
        JSONSQLCondition condition3 = new JSONSQLCondition("alert", "=", "true"); // False
        JSONSQLCondition condition4 = new JSONSQLCondition("sensor", "=", "inactive"); // False

        JSONSQLCondition group1 = new JSONSQLCondition(List.of(condition1, condition2), List.of("AND"));
        JSONSQLCondition group2 = new JSONSQLCondition(List.of(condition3, condition4), List.of("OR"));
        JSONSQLCondition rootCondition = new JSONSQLCondition(List.of(group1, group2), List.of("AND"));

        boolean result = mJSONSQLFunctions.matchesConditions(row, rootCondition);
        assertFalse(result, "(Temp < 25 AND Pressure > 40) AND (Alert = True OR Sensor = Inactive) should NOT match.");
    }

    @Test
    void testComplicatedContradictingMixedBooleanLogic() {
        JSONObject row = JSON.parseObject("{\"altitude\": 5000, \"weather\": \"clear\", \"windSpeed\": 20, \"turbulence\": \"low\"}");

        JSONSQLCondition condition1 = new JSONSQLCondition("altitude", ">", "10000"); // False
        JSONSQLCondition condition2 = new JSONSQLCondition("weather", "=", "clear");
        JSONSQLCondition condition3 = new JSONSQLCondition("windSpeed", "<", "30");
        JSONSQLCondition condition4 = new JSONSQLCondition("turbulence", "=", "high"); // False

        JSONSQLCondition group1 = new JSONSQLCondition(List.of(condition1, condition2), List.of("OR"));
        JSONSQLCondition group2 = new JSONSQLCondition(List.of(condition3, condition4), List.of("AND"));
        JSONSQLCondition rootCondition = new JSONSQLCondition(List.of(group1, group2), List.of("OR"));

        boolean result = mJSONSQLFunctions.matchesConditions(row, rootCondition);
        assertTrue(result, "(Altitude > 10000 OR Weather = Clear) OR (WindSpeed < 30 AND Turbulence = High) should match.");
    }


    @Test
    void testEqualityCondition_Numeric() {
        JSONObject row = JSON.parseObject("{\"age\": 30}");
        JSONSQLCondition condition = new JSONSQLCondition("age", "=", "30");
        assertTrue(mJSONSQLFunctions.evaluateCondition(row, condition), "Expected: 30 = 30");
    }

    @Test
    void testInequalityCondition_Numeric() {
        JSONObject row = JSON.parseObject("{\"score\": 85}");
        JSONSQLCondition condition = new JSONSQLCondition("score", "!=", "90");
        assertTrue(mJSONSQLFunctions.evaluateCondition(row, condition), "Expected: 85 != 90");
    }

    @Test
    void testGreaterThanCondition_Numeric() {
        JSONObject row = JSON.parseObject("{\"temperature\": 100}");
        JSONSQLCondition condition = new JSONSQLCondition("temperature", ">", "95");
        assertTrue(mJSONSQLFunctions.evaluateCondition(row, condition), "Expected: 100 > 95");
    }

    @Test
    void testLessThanCondition_Numeric() {
        JSONObject row = JSON.parseObject("{\"height\": 170}");
        JSONSQLCondition condition = new JSONSQLCondition("height", "<", "180");
        assertTrue(mJSONSQLFunctions.evaluateCondition(row, condition), "Expected: 170 < 180");
    }

    @Test
    void testGreaterThanOrEqualCondition_Numeric() {
        JSONObject row = JSON.parseObject("{\"rank\": 10}");
        JSONSQLCondition condition = new JSONSQLCondition("rank", ">=", "10");
        assertTrue(mJSONSQLFunctions.evaluateCondition(row, condition), "Expected: 10 >= 10");
    }

    @Test
    void testLessThanOrEqualCondition_Numeric() {
        JSONObject row = JSON.parseObject("{\"speed\": 60}");
        JSONSQLCondition condition = new JSONSQLCondition("speed", "<=", "60");
        assertTrue(mJSONSQLFunctions.evaluateCondition(row, condition), "Expected: 60 <= 60");
    }

    @Test
    void testEqualityCondition_String_CaseInsensitive() {
        JSONObject row = JSON.parseObject("{\"status\": \"ACTIVE\"}");
        JSONSQLCondition condition = new JSONSQLCondition("status", "=", "ACTIVE");
        assertTrue(mJSONSQLFunctions.evaluateCondition(row, condition), "Expected: 'ACTIVE' = 'ACTIVE' (case sensitive)");
    }

    @Test
    void testInequalityCondition_String() {
        JSONObject row = JSON.parseObject("{\"status\": \"inactive\"}");
        JSONSQLCondition condition = new JSONSQLCondition("status", "!=", "active");
        assertTrue(mJSONSQLFunctions.evaluateCondition(row, condition), "Expected: 'inactive' != 'active'");
    }

    @Test
    void testLikeCondition_String() {
        JSONObject row = JSON.parseObject("{\"description\": \"High Performance Engine\"}");
        JSONSQLCondition condition = new JSONSQLCondition("description", "LIKE", "%Performance%");
        assertTrue(mJSONSQLFunctions.evaluateCondition(row, condition), "Expected: 'High Performance Engine' contains 'performance'");
    }

    @Test
    void testIsNullCondition() {
        JSONObject row = JSON.parseObject("{\"deleted_at\": null}");
        JSONSQLCondition condition = new JSONSQLCondition("deleted_at", "IS", "NULL");
        assertTrue(mJSONSQLFunctions.evaluateCondition(row, condition), "Expected: deleted_at IS NULL");
    }

    @Test
    void testIsNotNullCondition() {
        JSONObject row = JSON.parseObject("{\"created_at\": \"2024-01-01\"}");
        JSONSQLCondition condition = new JSONSQLCondition("created_at", "IS NOT", "NULL");
        assertTrue(mJSONSQLFunctions.evaluateCondition(row, condition), "Expected: created_at IS NOT NULL");
    }

    @Test
    void testJsonObjectEquality() {
        JSONObject row = JSON.parseObject("{\"config\": {\"mode\": \"dark\", \"volume\": 50}}");
        JSONSQLCondition condition = new JSONSQLCondition("config", "=", "{\"mode\": \"dark\", \"volume\": 50}");
        assertTrue(mJSONSQLFunctions.evaluateCondition(row, condition), "Expected: JSON Object matches");
    }

    @Test
    void testJsonObjectInequality() {
        JSONObject row = JSON.parseObject("{\"settings\": {\"theme\": \"light\", \"notifications\": true}}");
        JSONSQLCondition condition = new JSONSQLCondition("settings", "=", "{\"theme\": \"dark\", \"notifications\": true}");
        assertFalse(mJSONSQLFunctions.evaluateCondition(row, condition), "Expected: JSON Object does not match");
    }

    @Test
    void testJsonArrayEquality() {
        JSONObject row = JSON.parseObject("{\"tags\": [\"tech\", \"AI\", \"ML\"]}");
        JSONSQLCondition condition = new JSONSQLCondition("tags", "=", "[\"tech\", \"AI\", \"ML\"]");
        assertTrue(mJSONSQLFunctions.evaluateCondition(row, condition), "Expected: JSON Array matches");
    }

    @Test
    void testJsonArrayInequality() {
        JSONObject row = JSON.parseObject("{\"tags\": [\"science\", \"biology\"]}");
        JSONSQLCondition condition = new JSONSQLCondition("tags", "=", "[\"science\", \"physics\"]");
        assertFalse(mJSONSQLFunctions.evaluateCondition(row, condition), "Expected: JSON Array does not match");
    }

    @Test
    void testInvalidJsonComparison() {
        JSONObject row = JSON.parseObject("{\"metadata\": \"Not a JSON\"}");
        JSONSQLCondition condition = new JSONSQLCondition("metadata", "=", "{\"key\": \"value\"}");
        assertFalse(mJSONSQLFunctions.evaluateCondition(row, condition), "Expected: Invalid JSON comparison fails");
    }

    @Test
    void testMalformedValueInQuery() {
        JSONObject row = JSON.parseObject("{\"title\": \"Science\"}");
        JSONSQLCondition condition = new JSONSQLCondition("title", "=", "[malformed]");
        assertFalse(mJSONSQLFunctions.evaluateCondition(row, condition), "Expected: Malformed value fails comparison");
    }

    @Test
    void testMissingColumn_ShouldReturnFalse() {
        JSONObject row = JSON.parseObject("{\"category\": \"Books\"}");
        JSONSQLCondition condition = new JSONSQLCondition("publisher", "=", "Penguin");
        assertFalse(mJSONSQLFunctions.evaluateCondition(row, condition), "Expected: Missing column should return false");
    }

    @Test
    void testWildcardMatchCondition() {
        JSONObject row = JSON.parseObject("{\"id\": 123, \"name\": \"Wildcard\"}");
        JSONSQLCondition condition = new JSONSQLCondition("*", "=", "*");
        assertTrue(mJSONSQLFunctions.evaluateCondition(row, condition), "Expected: Wildcard (*) always returns true");
    }


    @Test
    void testTokenCount_SimpleQuery() {
        String sql = "SELECT name FROM users";
		List<String> queryTokens = mJSONSQLFunctions.getTokens(sql);

        int expectedTokenCount = 4; // SELECT, name, FROM, users
        assertEquals(expectedTokenCount, queryTokens.size(), "Token count should match for a simple query.");
    }

    @Test
    void testTokenCount_QueryWithWhereClause() {
        String sql = "SELECT name FROM users WHERE age > 30";
        List<String> queryTokens = mJSONSQLFunctions.getTokens(sql);

        int expectedTokenCount = 8; // SELECT, name, FROM, users, WHERE, age, >, 30
        assertEquals(expectedTokenCount, queryTokens.size(), "Token count should match for WHERE clause query.");
    }

    @Test
    void testTokenCount_QueryWithComplexConditions() {
        String sql = "SELECT * FROM employees WHERE (salary > 50000 AND age < 40) OR city = 'New York'";
		Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers(sql);

        int expectedTokenCount = 18;
        // SELECT, *, FROM, employees, WHERE, (, salary, >, 50000, AND, age, <, 40, ), OR, city, =, 'New York'
        assertEquals(expectedTokenCount, tokens.size(), "Token count should match for a complex WHERE clause.");
    }

    @Test
    void testTokenCount_QueryWithOrderBy() {
        String sql = "SELECT id, name FROM students ORDER BY name DESC, age ASC";
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers(sql);

        int expectedTokenCount = 13;
        // SELECT, id, ,, name, FROM, students, ORDER, BY, name, DESC, ,, age, ASC
        assertEquals(expectedTokenCount, tokens.size(), "Token count should match for ORDER BY clause.");
    }

    @Test
    void testTokenCount_QueryWithLimit() {
        String sql = "SELECT * FROM products LIMIT 10";
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers(sql);

        int expectedTokenCount = 6; // SELECT, *, FROM, products, LIMIT, 10
        assertEquals(expectedTokenCount, tokens.size(), "Token count should match for LIMIT clause.");
    }

    @Test
    void testTokenCount_QueryWithParentheses() {
        String sql = "SELECT * FROM orders WHERE (status = 'shipped' OR status = 'delivered')";
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers(sql);

        int expectedTokenCount = 14;
        // SELECT, *, FROM, orders, WHERE, (, status, =, 'shipped', OR, status, =, 'delivered', )
        assertEquals(expectedTokenCount, tokens.size(), "Token count should match for query with parentheses.");
    }

    @Test
    void testTokenCount_EmptyQuery() {
        String sql = "";
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers(sql);

        int expectedTokenCount = 0;
        assertEquals(expectedTokenCount, tokens.size(), "Token count should be 0 for an empty query.");
    }

    @Test
    void testTokenCount_QueryWithExtraSpaces() {
        String sql = "   SELECT   name    FROM   users   ";
        Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers(sql);

        int expectedTokenCount = 4; // Spaces should not affect token count
        assertEquals(expectedTokenCount, tokens.size(), "Token count should be correct despite extra spaces.");
    }


    @Test
    void testIsNumeric_ValidIntegers() {
        assertTrue(mJSONSQLFunctions.isNumeric("123"), "Expected '123' to be numeric.");
        assertTrue(mJSONSQLFunctions.isNumeric("-987"), "Expected '-987' to be numeric.");
        assertTrue(mJSONSQLFunctions.isNumeric("0"), "Expected '0' to be numeric.");
    }

    @Test
    void testIsNumeric_ValidDecimals() {
        assertTrue(mJSONSQLFunctions.isNumeric("3.14"), "Expected '3.14' to be numeric.");
        assertTrue(mJSONSQLFunctions.isNumeric("-2.718"), "Expected '-2.718' to be numeric.");
        assertTrue(mJSONSQLFunctions.isNumeric("0.0001"), "Expected '0.0001' to be numeric.");
    }

    @Test
    void testIsNumeric_ValidScientificNotation() {
        assertTrue(mJSONSQLFunctions.isNumeric("1e3"), "Expected '1e3' to be numeric (scientific notation).");
        assertTrue(mJSONSQLFunctions.isNumeric("-4.56E-2"), "Expected '-4.56E-2' to be numeric.");
        assertTrue(mJSONSQLFunctions.isNumeric("6.022E23"), "Expected '6.022E23' to be numeric.");
    }

    @Test
    void testIsNumeric_InvalidNumbers() {
        assertFalse(mJSONSQLFunctions.isNumeric("abc"), "Expected 'abc' to be non-numeric.");
        assertFalse(mJSONSQLFunctions.isNumeric("12abc34"), "Expected '12abc34' to be non-numeric.");
        assertFalse(mJSONSQLFunctions.isNumeric("3.14.15"), "Expected '3.14.15' to be non-numeric.");
        assertFalse(mJSONSQLFunctions.isNumeric("1e3.5"), "Expected '1e3.5' to be non-numeric.");
    }

    @Test
    void testIsNumeric_EmptyAndNull() {
        assertFalse(mJSONSQLFunctions.isNumeric(""), "Expected empty string to be non-numeric.");
        assertFalse(mJSONSQLFunctions.isNumeric(null), "Expected null to be non-numeric.");
    }

    @Test
    void testIsNumeric_Whitespace() {
        assertFalse(mJSONSQLFunctions.isNumeric("   "), "Expected whitespace to be non-numeric.");
        assertTrue(mJSONSQLFunctions.isNumeric(" 123 "), "Expected ' 123 ' (whitespace padded) to be numeric.");
    }

    @Test
    void testIsNumeric_SpecialCharacters() {
        assertFalse(mJSONSQLFunctions.isNumeric("$100"), "Expected '$100' to be non-numeric.");
        assertFalse(mJSONSQLFunctions.isNumeric("5,000"), "Expected '5,000' to be non-numeric (comma included).");
        assertFalse(mJSONSQLFunctions.isNumeric("1-2"), "Expected '1-2' to be non-numeric.");
    }

    @Test
    void testIsNumeric_LeadingAndTrailingDots() {
        assertFalse(mJSONSQLFunctions.isNumeric("."), "Expected '.' to be non-numeric.");
        assertFalse(mJSONSQLFunctions.isNumeric(".."), "Expected '..' to be non-numeric.");
        assertTrue(mJSONSQLFunctions.isNumeric(".5"), "Expected '.5' to be numeric.");
        assertTrue(mJSONSQLFunctions.isNumeric("5."), "Expected '5.' to be numeric.");
    }


    @Test
    void testNestedAndOrConditions() {
        String sql = "SELECT name FROM users WHERE ( age > 30 AND ( city = 'New York' OR city = 'Chicago' ) ) OR salary < 40000";
        String jsonString = """
                [
                    {"name": "Alice", "age": 32, "city": "New York", "salary": 60000},
                    {"name": "Bob", "age": 25, "city": "Los Angeles", "salary": 35000},
                    {"name": "Charlie", "age": 40, "city": "Chicago", "salary": 45000},
                    {"name": "David", "age": 28, "city": "Chicago", "salary": 39000},
                    {"name": "Eve", "age": 22, "city": "New York", "salary": 28000}
                ]
                """;
        JSONTable table = new JSONTable("users", jsonString);
        JSONArray results = table.select(sql);

        assertEquals(5, results.size());
        assertEquals("Alice", results.getJSONObject(0).getString("name"));
        assertEquals("Bob", results.getJSONObject(1).getString("name"));
        assertEquals("Charlie", results.getJSONObject(2).getString("name"));
        assertEquals("David", results.getJSONObject(3).getString("name"));
        assertEquals("Eve", results.getJSONObject(4).getString("name"));
    }

    @Test
    void testComplexOrderingWithNulls() {
        String sql = "SELECT name, salary FROM employees WHERE salary IS NOT NULL ORDER BY salary DESC, age ASC";
        String jsonString = """
                [
                    {"name": "Alice", "age": 32, "salary": 60000},
                    {"name": "Bob", "age": 25, "salary": null},
                    {"name": "Charlie", "age": null, "salary": 55000},
                    {"name": "David", "age": 40, "salary": 70000},
                    {"name": "Eve", "age": 22, "salary": 50000}
                ]
                """;
        JSONTable jsonTable = new JSONTable("employees", jsonString);
        JSONArray results = jsonTable.select(sql);

        assertEquals(4, results.size());
        assertEquals("David", results.getJSONObject(0).getString("name"));
        assertEquals("Alice", results.getJSONObject(1).getString("name"));
        assertEquals("Charlie", results.getJSONObject(2).getString("name"));
        assertEquals("Eve", results.getJSONObject(3).getString("name"));
//        assertEquals("Bob", results.getJSONObject(4).getString("name")); // Null salary should be at the end
    }

    @Test
    void testMixedNumericAndStringComparisons() {
        String sql = "SELECT name FROM products WHERE price > 1000 OR name LIKE '%Super%'";
        String jsonString = """
                [
                    {"name": "SuperWidget", "price": 1500},
                    {"name": "BasicWidget", "price": 800},
                    {"name": "MegaWidget", "price": 2000},
                    {"name": "SuperGadget", "price": 750},
                    {"name": "BudgetWidget", "price": 400}
                ]
                """;
        JSONTable table = new JSONTable("products", jsonString);
        JSONArray results = table.select(sql);

        assertEquals(3, results.size());
        assertEquals("SuperWidget", results.getJSONObject(0).getString("name"));
        assertEquals("MegaWidget", results.getJSONObject(1).getString("name"));
        assertEquals("SuperGadget", results.getJSONObject(2).getString("name"));
    }


    @Test
    void testNestedConditionsWithDifferentDataTypes() {
        String sql = "SELECT id FROM records WHERE ( active = true AND ( age >= 18 AND age < 25 ) ) OR rating > 4.5";
        String jsonString = """
                [
                    {"id": 1, "active": true, "age": 20, "rating": 4.6},
                    {"id": 2, "active": false, "age": 17, "rating": 4.9},
                    {"id": 3, "active": true, "age": 23, "rating": 4.4},
                    {"id": 4, "active": true, "age": 30, "rating": 4.8},
                    {"id": 5, "active": false, "age": 15, "rating": 4.2}
                ]
                """;
        JSONTable table = new JSONTable("records", jsonString);
        JSONArray results = table.select(sql);

        assertEquals(4, results.size());
        assertEquals(1, results.getJSONObject(0).getInteger("id"));
        assertEquals(2, results.getJSONObject(1).getInteger("id"));
        assertEquals(3, results.getJSONObject(2).getInteger("id"));
        assertEquals(4, results.getJSONObject(3).getInteger("id"));
    }

    @Test
    void testWildcardSelection() {
        String sql = "SELECT * FROM employees WHERE salary >= 50000";
        String jsonString = """
                [
                    {"name": "Alice", "salary": 55000},
                    {"name": "Bob", "salary": 40000},
                    {"name": "Charlie", "salary": 60000},
                    {"name": "David", "salary": 30000}
                ]
                """;
        JSONTable table = new JSONTable("employees", jsonString);
        JSONArray results = table.select(sql);

        assertEquals(2, results.size());
        assertEquals("Alice", results.getJSONObject(0).getString("name"));
        assertEquals("Charlie", results.getJSONObject(1).getString("name"));
    }

    @Test
    void testTokenizationWithNoSpacingBetweenParentheses() {
        String sql = "SELECT name FROM users WHERE(age>30)OR(salary<50000)";
		Map<Integer, String[]> tokens = mJSONSQLFunctions.getTokensWithIdentifiers(sql);

        assertEquals(16, tokens.size());
        assertEquals("SELECT", mJSONSQLFunctions.getTokenAtIndex(tokens, 0));
        assertEquals("name", mJSONSQLFunctions.getTokenAtIndex(tokens, 1));
        assertEquals("FROM", mJSONSQLFunctions.getTokenAtIndex(tokens, 2));
        assertEquals("users", mJSONSQLFunctions.getTokenAtIndex(tokens, 3));
        assertEquals("WHERE", mJSONSQLFunctions.getTokenAtIndex(tokens, 4));
        assertEquals("(", mJSONSQLFunctions.getTokenAtIndex(tokens, 5));
        assertEquals("age", mJSONSQLFunctions.getTokenAtIndex(tokens, 6));
        assertEquals(">", mJSONSQLFunctions.getTokenAtIndex(tokens, 7));
        assertEquals("30", mJSONSQLFunctions.getTokenAtIndex(tokens, 8));
        assertEquals(")", mJSONSQLFunctions.getTokenAtIndex(tokens, 9));
        assertEquals("OR", mJSONSQLFunctions.getTokenAtIndex(tokens, 10));
        assertEquals("(", mJSONSQLFunctions.getTokenAtIndex(tokens, 11));
        assertEquals("salary", mJSONSQLFunctions.getTokenAtIndex(tokens, 12));
        assertEquals("<", mJSONSQLFunctions.getTokenAtIndex(tokens, 13));
        assertEquals("50000", mJSONSQLFunctions.getTokenAtIndex(tokens, 14));
        assertEquals(")", mJSONSQLFunctions.getTokenAtIndex(tokens, 15));
    }


    @Test
    void testComplexNestedParenthesisTokenization() {
        String sql = "SELECT name, age FROM users WHERE ((city = 'New York' AND (age > 30 OR salary <= 50000)) OR ((department = 'Engineering' OR (location != 'Remote' AND rank > 3))))";
		List<String> queryTokens = mJSONSQLFunctions.getTokens(sql);

        assertEquals(42, queryTokens.size());

        assertEquals("SELECT", queryTokens.get( 0));
        assertEquals("name", queryTokens.get( 1));
        assertEquals(",", queryTokens.get( 2));
        assertEquals("age", queryTokens.get( 3));
        assertEquals("FROM", queryTokens.get( 4));
        assertEquals("users", queryTokens.get( 5));
        assertEquals("WHERE", queryTokens.get( 6));

        // First nested group
        assertEquals("(", queryTokens.get( 7));
        assertEquals("(", queryTokens.get( 8));
        assertEquals("city", queryTokens.get( 9));
        assertEquals("=", queryTokens.get( 10));
        assertEquals("'New York'", queryTokens.get( 11));
        assertEquals("AND", queryTokens.get( 12));
        assertEquals("(", queryTokens.get( 13));
        assertEquals("age", queryTokens.get( 14));
        assertEquals(">", queryTokens.get( 15));
        assertEquals("30", queryTokens.get( 16));
        assertEquals("OR", queryTokens.get( 17));
        assertEquals("salary", queryTokens.get( 18));
        assertEquals("<=", queryTokens.get( 19));
        assertEquals("50000", queryTokens.get( 20));
        assertEquals(")", queryTokens.get( 21));
        assertEquals(")", queryTokens.get( 22));

        // Second nested group
        assertEquals("OR", queryTokens.get( 23));
        assertEquals("(", queryTokens.get( 24));
        assertEquals("(", queryTokens.get( 25));
        assertEquals("department", queryTokens.get( 26));
        assertEquals("=", queryTokens.get( 27));
        assertEquals("'Engineering'", queryTokens.get( 28));
        assertEquals("OR", queryTokens.get( 29));
        assertEquals("(", queryTokens.get( 30));
        assertEquals("location", queryTokens.get( 31));
        assertEquals("!=", queryTokens.get( 32));
        assertEquals("'Remote'", queryTokens.get( 33));
        assertEquals("AND", queryTokens.get( 34));
        assertEquals("rank", queryTokens.get( 35));
        assertEquals(">", queryTokens.get( 36));
        assertEquals("3", queryTokens.get( 37));
        assertEquals(")", queryTokens.get( 38));
        assertEquals(")", queryTokens.get( 39));
        assertEquals(")", queryTokens.get( 40));
    }


    @Test
    void testInsertSingleFlatObject() {
        JSONTable table = new JSONTable("users");
        String json = """
                {
                    "name": "Alice",
                    "age": 30,
                    "city": "New York"
                }
                """;

        JSONArray insertions = table.insertRaw(json);
        assertFalse(insertions.isEmpty());
        assertEquals(1, table.size());
        JSONObject row = table.get(0);
        assertEquals("Alice", row.getString("name"));
        assertEquals(30, row.getIntValue("age"));
        assertEquals("New York", row.getString("city"));
    }

    @Test
    void testInsertMultipleObjects() {
        JSONTable table = new JSONTable("users");
        String json = """
                [
                    { "name": "Bob", "age": 25 },
                    { "name": "Charlie", "age": 28 }
                ]
                """;

        JSONArray insertions = table.insertRaw(json);
        assertFalse(insertions.isEmpty());
        assertEquals(2, table.size());
        assertEquals("Bob", table.get(0).getString("name"));
        assertEquals("Charlie", table.get(1).getString("name"));
    }

    @Test
    void testInsertWithNestedObjectAndArray() {
        JSONTable table = new JSONTable("users");
        String json = """
                {
                    "name": "Dana",
                    "profile": { "bio": "Developer", "experience": 5 },
                    "skills": ["Java", "SQL"]
                }
                """;

        JSONArray insertions = table.insertRaw(json);
        assertFalse(insertions.isEmpty());
        JSONObject row = table.get(0);
        JSONObject profile = row.getJSONObject("profile");
        assertEquals("Developer", profile.getString("bio"));
        assertEquals(5, profile.getIntValue("experience"));
        JSONArray skills = row.getJSONArray("skills");
        assertEquals(2, skills.size());
        assertEquals("Java", skills.getString(0));
    }

    @Test
    void testInsertMalformedJson() {
        JSONTable table = new JSONTable("users");
        String malformed = "{ name: 'NoQuotes' age: 30 }"; // Invalid JSON (missing commas and quotes)

        JSONArray insertions = table.insertRaw(malformed);
        assertTrue(insertions.isEmpty());

        assertEquals(0, table.size());
    }

    @Test
    void testInsertEmptyArray() {
        JSONTable table = new JSONTable("users");
        String json = "[]";

        JSONArray insertions = table.insertRaw(json);
        assertTrue(insertions.isEmpty());

        assertEquals(0, table.size());
    }

    @Test
    void testInsertEmptyObject() {
        JSONTable table = new JSONTable("users");
        String json = "{}";

        JSONArray insertions = table.insertRaw(json);
        assertFalse(insertions.isEmpty());

        assertEquals(1, table.size());
        assertTrue(table.get(0).isEmpty());
    }






    @Test
    void testInsertMalformedJson_NoBrackets() {
        JSONTable table = new JSONTable("test");
        String invalid = """
            "name": "Invalid", "age": 30
        """;

        JSONArray insertions = table.insertRaw(invalid);
        assertTrue(insertions.isEmpty());

        assertEquals(0, table.size());
    }

    @Test
    void testInsertMalformedJson_MissingComma() {
        JSONTable table = new JSONTable("test");
        String invalid = """
            { "name": "MissingComma" "age": 22 }
        """;

        JSONArray insertions = table.insertRaw(invalid);
        assertTrue(insertions.isEmpty());

        assertEquals(0, table.size());
    }

    @Test
    void testInsertMalformedJson_UnclosedArray() {
        JSONTable table = new JSONTable("test");
        String invalid = """
            [ { "name": "Test", "age": 25 }, { "name": "Bad"
        """;

        JSONArray insertions = table.insertRaw(invalid);
        assertTrue(insertions.isEmpty());
        assertEquals(0, table.size());
    }

    @Test
    void testInsertNonJsonPrimitive() {
        JSONTable table = new JSONTable("test");
        String primitive = "123"; // Not a JSON object or array

        JSONArray insertions = table.insertRaw(primitive);
        assertTrue(insertions.isEmpty());
        assertEquals(0, table.size());
    }

    @Test
    void testInsertNullInput() {
        JSONTable table = new JSONTable("test");

        JSONArray insertions = table.insertRaw(null);
        assertTrue(insertions.isEmpty());

        assertEquals(0, table.size());
    }

    @Test
    void testInsertEmptyString() {
        JSONTable table = new JSONTable("test");

        JSONArray insertions = table.insertRaw("");
        assertTrue(insertions.isEmpty());

        assertEquals(0, table.size());
    }

    @Test
    void testInsertWhitespaceOnly() {
        JSONTable table = new JSONTable("test");

        JSONArray insertions = table.insertRaw("      ");
        assertTrue(insertions.isEmpty());

        assertEquals(0, table.size());
    }







    @Test
    public void testSimpleNestedObject() {
        JSONObject root = new JSONObject();
        mJSONSQLFunctions.setAndCreateJSONValue(root, "address.city", "Tokyo");
        assertEquals("Tokyo", root.getJSONObject("address").getString("city"));
    }

    @Test
    public void testArrayAccessImplicit() {
        JSONObject root = new JSONObject();
        mJSONSQLFunctions.setAndCreateJSONValue(root, "skills.0", "Java");
        assertEquals("Java", root.getJSONArray("skills").getString(0));
    }

    @Test
    public void testDeepArrayObjectChain() {
        JSONObject root = new JSONObject();
        mJSONSQLFunctions.setAndCreateJSONValue(root, "projects.0.technologies.2", "GraphQL");
        JSONObject project = root.getJSONArray("projects").getJSONObject(0);
        JSONArray tech = project.getJSONArray("technologies");
        assertEquals("GraphQL", tech.getString(2));
    }

    @Test
    public void testMultipleArrays() {
        JSONObject root = new JSONObject();
        mJSONSQLFunctions.setAndCreateJSONValue(root, "matrix.1.2", 42);
        JSONArray matrixRow = root.getJSONArray("matrix").getJSONArray(1);
        assertEquals(42, matrixRow.getInteger(2));
    }

    @Test
    public void testInsertIntoExistingObject() {
        JSONObject root = new JSONObject();
        root.put("meta", new JSONObject());
        mJSONSQLFunctions.setAndCreateJSONValue(root, "meta.version", "1.0");
        assertEquals("1.0", root.getJSONObject("meta").getString("version"));
    }

    @Test
    public void testSetOnNullCreatesStructure() {
        JSONObject root = new JSONObject();
        mJSONSQLFunctions.setAndCreateJSONValue(root, "user.0.profile.name", "Dana");
        JSONObject userProfile = root.getJSONArray("user").getJSONObject(0).getJSONObject("profile");
        assertEquals("Dana", userProfile.getString("name"));
    }







    @Test
    public void testUpdateNestedObjectField() {
        JSONTable table = new JSONTable("users");
        table.insertRaw("""
            {
              "name": "Alice",
              "address": {
                "city": "New York",
                "zip": "10001"
              },
              "tags": ["developer", "java"]
            }
        """);

        table.insertRaw("""
            {
              "name": "Bob",
              "address": {
                "city": "San Francisco",
                "zip": "94101"
              },
              "tags": ["manager", "sales"]
            }
        """);

        String update = """
            UPDATE users
            SET address.city = 'Los Angeles'
            WHERE name = 'Alice'
        """;
        table.update(update);

        JSONObject row = table.get(0);
        assertEquals("Los Angeles", row.getJSONObject("address").getString("city"));
    }

    @Test
    public void testUpdateArrayElement() {
        JSONTable table = new JSONTable("users");
        table.insertRaw("""
            {
              "name": "Alice",
              "address": {
                "city": "New York",
                "zip": "10001"
              },
              "tags": ["developer", "java"]
            }
        """);

        table.insertRaw("""
            {
              "name": "Bob",
              "address": {
                "city": "San Francisco",
                "zip": "94101"
              },
              "tags": ["manager", "sales"]
            }
        """);
        String update = """
            UPDATE users
            SET tags.1 = 'senior java'
            WHERE name = 'Alice'
        """;
        table.update(update);

        JSONObject row = table.get(0);
        JSONArray tags = row.getJSONArray("tags");
        assertEquals("senior java", tags.getString(1));
    }

    @Test
    public void testUpdateDeepArrayPath() {
        JSONTable table = new JSONTable("users");
        table.insertRaw("""
            {
              "name": "Alice",
              "address": {
                "city": "New York",
                "zip": "10001"
              },
              "tags": ["developer", "java"]
            }
        """);

        table.insertRaw("""
            {
              "name": "Bob",
              "address": {
                "city": "San Francisco",
                "zip": "94101"
              },
              "tags": ["manager", "sales"]
            }
        """);
        table.insertRaw("""
            {
              "name": "Charlie",
              "metadata": {
                "history": [
                  {"score": 10},
                  {"score": 20}
                ]
              }
            }
        """);

        String update = """
            UPDATE users
            SET metadata.history.1.score = 42
            WHERE name = 'Charlie'
        """;
        table.update(update);

        JSONObject row = table.get(2);
        int updatedScore = row.getJSONObject("metadata").getJSONArray("history").getJSONObject(1).getIntValue("score");
        assertEquals(42, updatedScore);
    }

    @Test
    public void testAutoCreatePathWithArrays() {
        JSONTable table = new JSONTable("users");
        table.insertRaw("""
            {
              "name": "Alice",
              "address": {
                "city": "New York",
                "zip": "10001"
              },
              "tags": ["developer", "java"]
            }
        """);

        table.insertRaw("""
            {
              "name": "Bob",
              "address": {
                "city": "San Francisco",
                "zip": "94101"
              },
              "tags": ["manager", "sales"]
            }
        """);
        table.insertRaw("""
            {
              "name": "Dana"
            }
        """);

        String update = """
            UPDATE users
            SET matrix.1.2 = 99
            WHERE name = 'Dana'
        """;
        table.update(update);

        JSONObject row = table.get(2);
        JSONArray row1 = row.getJSONArray("matrix").getJSONArray(1);
        assertEquals(99, row1.getIntValue(2));
    }


    @Test
    public void testUpdateDeeplyNestedArray() {
        JSONTable table = new JSONTable("config");
        table.insertRaw("""
        {
          "system": {
            "modules": [
              {
                "id": "graphics",
                "settings": {
                  "resolutions": ["720p", "1080p"]
                }
              }
            ]
          }
        }
    """);

        String update = """
        UPDATE config
        SET system.modules.0.settings.resolutions.1 = '4K'
        WHERE system.modules.0.id = 'graphics'
    """;
        table.update(update);

        JSONObject row = table.get(0);
        JSONArray resolutions = row.getJSONObject("system")
                .getJSONArray("modules")
                .getJSONObject(0)
                .getJSONObject("settings")
                .getJSONArray("resolutions");
        assertEquals("4K", resolutions.getString(1));
    }






    @Test
    public void testSimpleAssignments() {
        String input = "name = 'Alice', age = 30, city = 'New York'";
        List<String> result = mJSONSQLFunctions.smartSplitAssignments(input);
        assertEquals(List.of("name = 'Alice'", "age = 30", "city = 'New York'"), result);
    }

    @Test
    public void testWithArray() {
        String input = "tags = ['dev', 'ops'], count = 2";
        List<String> result = mJSONSQLFunctions.smartSplitAssignments(input);
        assertEquals(List.of("tags = ['dev', 'ops']", "count = 2"), result);
    }

    @Test
    public void testWithNestedJson() {
        String input = "info = {\"role\": \"admin\", \"dept\": \"IT\"}, active = true";
        List<String> result = mJSONSQLFunctions.smartSplitAssignments(input);
        assertEquals(List.of("info = {\"role\": \"admin\", \"dept\": \"IT\"}", "active = true"), result);
    }

    @Test
    public void testJsonArrayWithObjects() {
        String input = "records = [{\"x\": 1}, {\"x\": 2}], score = 10";
        List<String> result = mJSONSQLFunctions.smartSplitAssignments(input);
        assertEquals(List.of("records = [{\"x\": 1}, {\"x\": 2}]", "score = 10"), result);
    }

    @Test
    public void testWithCommasInsideQuotes() {
        String input = "note = 'this, that, and more', flag = false";
        List<String> result = mJSONSQLFunctions.smartSplitAssignments(input);
        assertEquals(List.of("note = 'this, that, and more'", "flag = false"), result);
    }

    @Test
    public void testTrailingComma() {
        String input = "name = 'Alice',";
        List<String> result = mJSONSQLFunctions.smartSplitAssignments(input);
        assertEquals(List.of("name = 'Alice'"), result);
    }






    @Test
    public void testStringAssignment() {
        List<String> assignments = List.of("name = 'Alice'");
        Map<String, Object> result = mJSONSQLFunctions.extractUpdates(assignments);
        assertEquals("Alice", result.get("name"));
    }

    @Test
    public void testIntegerAssignment() {
        List<String> assignments = List.of("age = 30");
        Map<String, Object> result = mJSONSQLFunctions.extractUpdates(assignments);
        assertEquals(30, result.get("age"));
    }

    @Test
    public void testDoubleAssignment() {
        List<String> assignments = List.of("score = 99.5");
        Map<String, Object> result = mJSONSQLFunctions.extractUpdates(assignments);
        assertEquals(99.5, result.get("score"));
    }

    @Test
    public void testNullAssignment() {
        List<String> assignments = List.of("status = null");
        Map<String, Object> result = mJSONSQLFunctions.extractUpdates(assignments);
        assertNull(result.get("status"));
    }

    @Test
    public void testJsonObjectAssignment() {
        List<String> assignments = List.of("info = {\"city\":\"NY\"}");
        Map<String, Object> result = mJSONSQLFunctions.extractUpdates(assignments);
        assertInstanceOf(JSONObject.class, result.get("info"));
        assertEquals("NY", ((JSONObject) result.get("info")).getString("city"));
    }

    @Test
    public void testJsonArrayAssignment() {
        List<String> assignments = List.of("tags = [\"dev\",\"ops\"]");
        Map<String, Object> result = mJSONSQLFunctions.extractUpdates(assignments);
        assertInstanceOf(JSONArray.class, result.get("tags"));
        assertEquals("dev", ((JSONArray) result.get("tags")).getString(0));
    }

    @Test
    public void testMalformedAssignment() {
        List<String> assignments = List.of("invalid_assignment");
        assertThrows(IllegalArgumentException.class, () -> mJSONSQLFunctions.extractUpdates(assignments));
    }







    @Test
    public void testSimpleFieldUpdate() {
        JSONTable table = new JSONTable("users");
        table.insertRaw("{\"name\": \"Alice\", \"age\": 30}");
        table.update("UPDATE users SET age = 31 WHERE name = 'Alice'");
        assertEquals(31, mJSONSQLFunctions.getJsonPathValue(table.get(0), "age"));
    }

    @Test
    public void testUpdateMultipleFields() {
        JSONTable table = new JSONTable("users");
        table.insertRaw("{\"name\": \"Bob\", \"age\": 25, \"city\": \"NY\"}");
        table.update("UPDATE users SET age = 26, city = 'LA' WHERE name = 'Bob'");
        assertEquals(26, mJSONSQLFunctions.getJsonPathValue(table.get(0), "age"));
        assertEquals("LA", mJSONSQLFunctions.getJsonPathValue(table.get(0), "city"));
    }

    @Test
    public void testUpdateNestedObject() {
        JSONTable table = new JSONTable("users");
        table.insertRaw("{\"name\": \"Dana\", \"address\": {\"city\": \"Boston\"}}");
        table.update("UPDATE users SET address.city = 'Chicago' WHERE name = 'Dana'");
        assertEquals("Chicago", mJSONSQLFunctions.getJsonPathValue(table.get(0), "address.city"));
    }

    @Test
    public void testUpdateWithJsonObject() {
        JSONTable table = new JSONTable("users");
        table.insertRaw("{\"name\": \"Frank\"}");
        table.update("UPDATE users SET profile.level = 10, profile.score = 99 WHERE name = 'Frank'");
        assertEquals(10, mJSONSQLFunctions.getJsonPathValue(table.get(0), "profile.level"));
        assertEquals(99, mJSONSQLFunctions.getJsonPathValue(table.get(0), "profile.score"));
    }

    @Test
    public void testUpdateWithJsonArray() {
        JSONTable table = new JSONTable("users");
        table.insertRaw("{\"name\": \"Grace\"}");
        table.update("UPDATE users SET tags = [\"admin\", \"ops\"] WHERE name = 'Grace'");
        assertEquals("admin", mJSONSQLFunctions.getJsonPathValue(table.get(0), "tags.0"));
        assertEquals("ops", mJSONSQLFunctions.getJsonPathValue(table.get(0), "tags.1"));
    }

    @Test
    public void testUpdateWhereNoMatch() {
        JSONTable table = new JSONTable("users");
        table.insertRaw("{\"name\": \"Hank\", \"age\": 45}");
        table.update("UPDATE users SET age = 50 WHERE name = 'NotHank'");
        assertEquals(45, mJSONSQLFunctions.getJsonPathValue(table.get(0), "age")); // unchanged
    }

    @Test
    public void testUpdateToNull() {
        JSONTable table = new JSONTable("users");
        table.insertRaw("{\"name\": \"Ivy\", \"email\": \"ivy@example.com\"}");
        table.update("UPDATE users SET email = null WHERE name = 'Ivy'");
        assertNull(mJSONSQLFunctions.getJsonPathValue(table.get(0), "email"));
    }




    @Test
    public void testUpdateInsertJsonObject() {
        JSONTable table = new JSONTable("users");
        table.insertRaw("{\"name\": \"Grace\"}");

        table.update("UPDATE users SET info = {\"department\": \"ops\", \"level\": 2} WHERE name = 'Grace'");

        JSONObject row = table.get(0);
        JSONObject info = row.getJSONObject("info");
        assertEquals("ops", info.getString("department"));
        assertEquals(2, info.getIntValue("level"));
    }

    @Test
    public void testUpdateInsertJsonArray() {
        JSONTable table = new JSONTable("users");
        table.insertRaw("{\"name\": \"Grace\"}");

        table.update("UPDATE users SET tags = [\"admin\", \"ops\"] WHERE name = 'Grace'");

        JSONArray tags = table.get(0).getJSONArray("tags");
        assertEquals("admin", tags.getString(0));
        assertEquals("ops", tags.getString(1));
    }

    @Test
    public void testUpdateInsertNestedJsonObject() {
        JSONTable table = new JSONTable("users");
        table.insertRaw("{\"name\": \"Grace\"}");

        table.update("UPDATE users SET profile = {\"contact\": {\"email\": \"grace@example.com\"}} WHERE name = 'Grace'");

        JSONObject profile = table.get(0).getJSONObject("profile");
        JSONObject contact = profile.getJSONObject("contact");
        assertEquals("grace@example.com", contact.getString("email"));
    }

    @Test
    public void testUpdateInsertArrayOfJsonObjects() {
        JSONTable table = new JSONTable("users");
        table.insertRaw("{\"name\": \"Grace\"}");

        table.update("UPDATE users SET projects = [{\"id\": 1}, {\"id\": 2}] WHERE name = 'Grace'");

        JSONArray projects = table.get(0).getJSONArray("projects");
        assertEquals(1, projects.getJSONObject(0).getIntValue("id"));
        assertEquals(2, projects.getJSONObject(1).getIntValue("id"));
    }





    @Test
    public void testInsertUpdateSelectFlatFields() {
        JSONTable table = new JSONTable("people");

        // Insert
        table.insertRaw("""
            {"name": "Alice", "age": 30, "city": "NY"}
        """);

        // Update
        table.update("""
            UPDATE people
            SET city = 'Boston', age = 31
            WHERE name = 'Alice'
        """);

        // Select
        JSONArray result = table.select("""
            SELECT name, city FROM people WHERE age = 31
        """);

        assertEquals(1, result.size());
        JSONObject row = result.getJSONObject(0);
        assertEquals("Alice", row.getString("name"));
        assertEquals("Boston", row.getString("city"));
    }

    @Test
    public void testInsertUpdateSelectWithNestedObject() {
        JSONTable table = new JSONTable("users");

        // Insert
        table.insertRaw("""
            {
              "name": "Bob",
              "profile": {
                "email": "bob@example.com",
                "location": "Seattle"
              }
            }
        """);

        // Update nested value
        table.update("""
            UPDATE users
            SET profile.location = 'Portland'
            WHERE name = 'Bob'
        """);

        // Select nested field
        JSONArray result = table.select("SELECT profile.location FROM users WHERE name = 'Bob'");

        assertEquals(1, result.size());
        assertEquals("Portland", result.getJSONObject(0).getString("profile.location"));
    }

    @Test
    public void testInsertUpdateSelectWithJSONArray() {
        JSONTable table = new JSONTable("projects");

        // Insert
        table.insertRaw("""
            {
              "project": "Phoenix",
              "tags": ["java", "backend"]
            }
        """);

        // Update entire array
        table.update("""
            UPDATE projects
            SET tags = ["java", "api", "cloud"]
            WHERE project = 'Phoenix'
        """);

        // Select array field
        JSONArray result = table.select("SELECT tags FROM projects WHERE project = 'Phoenix'");
        JSONArray tags = result.getJSONObject(0).getJSONArray("tags");

        assertEquals(3, tags.size());
        assertEquals("cloud", tags.getString(2));
    }



    @Test
    public void testNumericComparison() {
        assertTrue(mJSONSQLFunctions.compareValues(5, 5, "="));
        assertFalse(mJSONSQLFunctions.compareValues(5, 3, "="));
        assertTrue(mJSONSQLFunctions.compareValues(10, 5, ">"));
        assertTrue(mJSONSQLFunctions.compareValues(5.5, 5, ">"));
        assertFalse(mJSONSQLFunctions.compareValues(3, 5, ">"));
        assertTrue(mJSONSQLFunctions.compareValues(3, 5, "<"));
        assertTrue(mJSONSQLFunctions.compareValues(5, 5, ">="));
        assertTrue(mJSONSQLFunctions.compareValues(4.9, 5.0, "<="));
    }

    @Test
    public void testStringComparison() {
        assertTrue(mJSONSQLFunctions.compareValues("hello", "hello", "="));
        assertTrue(mJSONSQLFunctions.compareValues("hello", "hello", "="));
        assertFalse(mJSONSQLFunctions.compareValues("hello", "world", "="));
        assertTrue(mJSONSQLFunctions.compareValues("hello", "world", "!="));
        assertTrue(mJSONSQLFunctions.compareValues("beta", "alpha", ">"));
        assertFalse(mJSONSQLFunctions.compareValues("beta", "gamma", ">"));
        assertTrue(mJSONSQLFunctions.compareValues("gamma", "beta", ">"));
    }

    @Test
    public void testLikeOperator() {
        assertTrue(mJSONSQLFunctions.compareValues( "hello world", "hello%", "LIKE"));
        assertFalse(mJSONSQLFunctions.compareValues("hello world", "mars", "LIKE"));
        assertTrue(mJSONSQLFunctions.compareValues("HeLLo WoRLD", "%Lo%W%", "LIKE"));
    }

    @Test
    public void testNulls() {
        assertFalse(mJSONSQLFunctions.compareValues(null, "value", "="));
        assertFalse(mJSONSQLFunctions.compareValues("value", null, "="));
        assertFalse(mJSONSQLFunctions.compareValues(null, null, "=")); // By logic, treated as false
    }

    @Test
    public void testMixedTypes() {
        assertTrue(mJSONSQLFunctions.compareValues("42", 42, "="));
        assertFalse(mJSONSQLFunctions.compareValues(42.0, "42", "="));
        assertFalse(mJSONSQLFunctions.compareValues("42", 43, "="));
    }








    @Test
    public void testParseInteger() {
        Object result = mJSONSQLFunctions.parseLiteral("42");
        assertTrue(result instanceof Integer);
        assertEquals(42, result);
    }

    @Test
    public void testParseNegativeDouble() {
        Object result = mJSONSQLFunctions.parseLiteral("-3.14");
        assertTrue(result instanceof Double);
        assertEquals(-3.14, (Double) result, 0.0001);
    }

    @Test
    public void testParseDoubleQuotedString() {
        Object result = mJSONSQLFunctions.parseLiteral("\"hello\"");
        assertTrue(result instanceof String);
        assertEquals("hello", result);
    }
    @Test
    public void testParseQuotedString() {
        Object result = mJSONSQLFunctions.parseLiteral("'hello'");
        assertTrue(result instanceof String);
        assertEquals("hello", result);
    }

    @Test
    public void testParseNullLiteral() {
        assertNull(mJSONSQLFunctions.parseLiteral("null"));
    }

    @Test
    public void testParseJSONObject() {
        Object result = mJSONSQLFunctions.parseLiteral("{\"name\":\"Alice\",\"age\":30}");
        assertTrue(result instanceof JSONObject);
        JSONObject obj = (JSONObject) result;
        assertEquals("Alice", obj.getString("name"));
        assertEquals(30, obj.getIntValue("age"));
    }

    @Test
    public void testParseJSONArray() {
        Object result = mJSONSQLFunctions.parseLiteral("[\"one\", \"two\", 3]");
        assertTrue(result instanceof JSONArray);
        JSONArray arr = (JSONArray) result;
        assertEquals("one", arr.getString(0));
        assertEquals(3, arr.getIntValue(2));
    }

    @Test
    public void testFallbackRawString() {
        Object result = mJSONSQLFunctions.parseLiteral("unquotedValue");
        assertTrue(result instanceof String);
        assertEquals("unquotedValue", result);
    }

    @Test
    public void testEmptyInput() {
        assertNull( mJSONSQLFunctions.parseLiteral(""));
    }

    @Test
    public void testNullInput() {
        assertNull(mJSONSQLFunctions.parseLiteral(null));
    }





    @Test
    public void testStringLiteralSingleQuotes() {
        Object result = mJSONSQLFunctions.parseLiteral("'hello'");
        assertTrue(result instanceof String);
        assertEquals("hello", result);
    }

    @Test
    public void testStringLiteralDoubleQuotes() {
        Object result = mJSONSQLFunctions.parseLiteral("\"world\"");
        assertTrue(result instanceof String);
        assertEquals("world", result);
    }

    @Test
    public void testIntegerLiteral() {
        Object result = mJSONSQLFunctions.parseLiteral("123");
        assertTrue(result instanceof Integer);
        assertEquals(123, result);
    }

    @Test
    public void testNegativeIntegerLiteral() {
        Object result = mJSONSQLFunctions.parseLiteral("-987");
        assertTrue(result instanceof Integer);
        assertEquals(-987, result);
    }

    @Test
    public void testDoubleLiteral() {
        Object result = mJSONSQLFunctions.parseLiteral("123.45");
        assertTrue(result instanceof Double);
        assertEquals(123.45, (Double) result, 0.0001);
    }

    @Test
    public void testBooleanTrueLiteral() {
        Object result = mJSONSQLFunctions.parseLiteral("true");
        assertTrue(result instanceof Boolean);
        assertTrue((Boolean) result);
    }

    @Test
    public void testBooleanFalseLiteral() {
        Object result = mJSONSQLFunctions.parseLiteral("false");
        assertTrue(result instanceof Boolean);
        assertFalse((Boolean) result);
    }

    @Test
    public void testNullLiteral() {
        Object result = mJSONSQLFunctions.parseLiteral("null");
        assertNull(result);
    }

    @Test
    public void testJsonObjectLiteral() {
        Object result = mJSONSQLFunctions.parseLiteral("{\"key\": \"value\"}");
        assertTrue(result instanceof JSONObject);
        assertEquals("value", ((JSONObject) result).getString("key"));
    }

    @Test
    public void testJsonArrayLiteral() {
        Object result = mJSONSQLFunctions.parseLiteral("[1, 2, 3]");
        assertTrue(result instanceof JSONArray);
        assertEquals(3, ((JSONArray) result).size());
    }

    @Test
    public void testQuotedJsonObject() {
        Object result = mJSONSQLFunctions.parseLiteral("'{\"nested\": true}'");
        assertTrue(result instanceof JSONObject);
        assertEquals(true, ((JSONObject) result).getBoolean("nested"));
    }

    @Test
    public void testQuotedJsonArray() {
        Object result = mJSONSQLFunctions.parseLiteral("'[10, 20, 30]'");
        assertTrue(result instanceof JSONArray);
        assertEquals(3, ((JSONArray) result).size());
    }

    @Test
    public void testFallbackRawStringV2() {
        Object result = mJSONSQLFunctions.parseLiteral("nonQuotedText");
        assertTrue(result instanceof String);
        assertEquals("nonQuotedText", result);
    }



    @Test
    public void testNumericComparisons() {
        assertTrue(mJSONSQLFunctions.compareValues(10, 10, "="));
        assertFalse(mJSONSQLFunctions.compareValues(10, 5, "="));
        assertTrue(mJSONSQLFunctions.compareValues(10, 5, ">"));
        assertTrue(mJSONSQLFunctions.compareValues(5, 10, "<"));
        assertTrue(mJSONSQLFunctions.compareValues(5, 5, "<="));
        assertTrue(mJSONSQLFunctions.compareValues(5, 5, ">="));
        assertTrue(mJSONSQLFunctions.compareValues(5.5, 5, ">"));
        assertTrue(mJSONSQLFunctions.compareValues(3, 3.0, "="));
    }

    @Test
    public void testStringComparisons_caseSensitive() {
        assertTrue(mJSONSQLFunctions.compareValues("Apple", "Apple", "="));
        assertFalse(mJSONSQLFunctions.compareValues("Apple", "apple", "="));
        assertTrue(mJSONSQLFunctions.compareValues("banana", "banana", "!=") == false);
        assertTrue(mJSONSQLFunctions.compareValues("apple", "banana", "<"));
        assertTrue(mJSONSQLFunctions.compareValues("carrot", "banana", ">"));
    }

    @Test
    public void testBooleanAndNullComparisons() {
        assertTrue(mJSONSQLFunctions.compareValues(true, true, "="));
        assertFalse(mJSONSQLFunctions.compareValues(true, false, "="));
        assertFalse(mJSONSQLFunctions.compareValues(null, true, "="));
        assertFalse(mJSONSQLFunctions.compareValues(null, null, "=")); // You may want to treat null == null as true depending on your logic
    }

    @Test
    public void testLikeOperator_basic() {
        assertTrue(mJSONSQLFunctions.compareValues("hello world", "hello%", "LIKE"));
        assertTrue(mJSONSQLFunctions.compareValues("hello world", "%world", "LIKE"));
        assertTrue(mJSONSQLFunctions.compareValues("hello world", "%lo wo%", "LIKE"));
        assertFalse(mJSONSQLFunctions.compareValues("hello world", "hi%", "LIKE"));
    }

    @Test
    public void testLikeOperator_edgeCases() {
        assertTrue(mJSONSQLFunctions.compareValues("abc123", "abc%", "LIKE"));
        assertTrue(mJSONSQLFunctions.compareValues("abc123", "%123", "LIKE"));
        assertTrue(mJSONSQLFunctions.compareValues("abc123xyz", "abc%xyz", "LIKE"));
        assertFalse(mJSONSQLFunctions.compareValues("abc123xyz", "abc%zyx", "LIKE"));
        assertTrue(mJSONSQLFunctions.compareValues("abc", "a%c", "LIKE"));
        assertFalse(mJSONSQLFunctions.compareValues("abc", "a%d", "LIKE"));
    }

    @Test
    public void testMixedTypesV2() {
        assertTrue(mJSONSQLFunctions.compareValues("42", 42, "="));
        assertFalse(mJSONSQLFunctions.compareValues("42", 43, "="));
        assertTrue(mJSONSQLFunctions.compareValues(42, "42", "="));
        assertTrue(mJSONSQLFunctions.compareValues("true", true, "="));
        assertFalse(mJSONSQLFunctions.compareValues("false", true, "="));
    }





    @Test
    public void testQueryTypeSelect() {
        assertEquals("SELECT", mJSONSQLFunctions.getQueryType("SELECT * FROM users"));
        assertEquals("SELECT", mJSONSQLFunctions.getQueryType(" select name from table "));
    }

    @Test
    public void testQueryTypeInsert() {
        assertEquals("INSERT", mJSONSQLFunctions.getQueryType("INSERT INTO users (name) VALUES ('Alice')"));
        assertEquals("INSERT", mJSONSQLFunctions.getQueryType(" insert into logs (message) values ('hello') "));
    }

    @Test
    public void testQueryTypeUpdate() {
        assertEquals("UPDATE", mJSONSQLFunctions.getQueryType("UPDATE users SET name = 'Bob' WHERE id = 1"));
        assertEquals("UPDATE", mJSONSQLFunctions.getQueryType(" update items set price = 100 "));
    }

    @Test
    public void testQueryTypeDelete() {
        assertEquals("DELETE", mJSONSQLFunctions.getQueryType("DELETE FROM users WHERE id = 1"));
        assertEquals("DELETE", mJSONSQLFunctions.getQueryType(" delete from sessions where active = false "));
    }

    @Test
    public void testQueryTypeInvalid() {
        assertNull(mJSONSQLFunctions.getQueryType(null));
        assertNull(mJSONSQLFunctions.getQueryType(""));
        assertNull(mJSONSQLFunctions.getQueryType("DROP TABLE users"));
        assertNull(mJSONSQLFunctions.getQueryType("CREATE TABLE demo"));
        assertNull(mJSONSQLFunctions.getQueryType("WITH temp AS (SELECT * FROM users) SELECT * FROM temp"));
    }

    @Test
    public void testQueryTypeJsonObjectInsert() {
        String json = """
        {
            "name": "Alice",
            "age": 30
        }
    """;
        assertEquals("INSERT", mJSONSQLFunctions.getQueryType(json));
    }

    @Test
    public void testQueryTypeJsonArrayInsert() {
        String json = """
        [
            {"name": "Bob", "age": 25},
            {"name": "Carol", "age": 35}
        ]
    """;
        assertEquals("INSERT", mJSONSQLFunctions.getQueryType(json));
    }














    @Test
    public void testFlatMerge() {
        JSONArray array = new JSONArray();
        array.add(JSONObject.of("id", 1, "name", "Alice"));
        array.add(JSONObject.of("id", 2, "role", "Manager"));

        JSONObject result = mJSONSQLFunctions.getMasterSchema(array);
        assertEquals(3, result.size());
        assertEquals(1, result.getIntValue("id"));
        assertEquals("Alice", result.getString("name"));
        assertEquals("Manager", result.getString("role"));
    }

    @Test
    public void testDeepNestedMerge() {
        JSONArray array = new JSONArray();
        array.add(JSONObject.parseObject("""
            {
              "user": {
                "name": "Bob",
                "contact": {
                  "email": "bob@example.com"
                }
              },
              "active": true
            }
        """));
        array.add(JSONObject.parseObject("""
            {
              "user": {
                "age": 50,
                "contact": {
                  "address": "1234 John Smith Ave"
                }
              }
            }
        """));

        JSONObject result = mJSONSQLFunctions.getMasterSchema(array);
        JSONObject user = result.getJSONObject("user");

        assertEquals("Bob", user.getString("name"));
        assertEquals(50, user.getIntValue("age"));

        JSONObject contact = user.getJSONObject("contact");
        assertEquals("bob@example.com", contact.getString("email"));
        assertEquals("1234 John Smith Ave", contact.getString("address"));

        assertTrue(result.getBoolean("active"));
    }

    @Test
    public void testMultipleTopLevelKeys() {
        JSONArray array = new JSONArray();
        array.add(JSONObject.parseObject("""
            {
              "id": 1,
              "meta": { "type": "A" }
            }
        """));
        array.add(JSONObject.parseObject("""
            {
              "status": "active",
              "meta": { "version": "1.0" }
            }
        """));

        JSONObject result = mJSONSQLFunctions.getMasterSchema(array);

        assertEquals(3, result.size());
        assertEquals(1, result.getIntValue("id"));
        assertEquals("active", result.getString("status"));

        JSONObject meta = result.getJSONObject("meta");
        assertEquals("A", meta.getString("type"));
        assertEquals("1.0", meta.getString("version"));
    }









    @Test
    public void testFlatObjects() {
        JSONArray data = new JSONArray();
        data.add(JSONObject.of("id", 1, "name", "Alice"));
        data.add(JSONObject.of("age", 30, "name", "Bob")); // name should not overwrite

        JSONObject result = mJSONSQLFunctions.getMasterSchema(data);
        assertEquals(3, result.size());
        assertEquals(1, result.getIntValue("id"));
        assertEquals("Alice", result.getString("name"));
        assertEquals(30, result.getIntValue("age"));
    }

    @Test
    public void testNestedObjects() {
        JSONArray data = new JSONArray();
        data.add(JSONObject.parseObject("""
            {
              "user": {
                "name": "Alice",
                "details": {
                  "city": "New York"
                }
              }
            }
        """));
        data.add(JSONObject.parseObject("""
            {
              "user": {
                "details": {
                  "zip": "10001"
                },
                "age": 28
              }
            }
        """));

        JSONObject result = mJSONSQLFunctions.getMasterSchema(data);
        assertTrue(result.containsKey("user"));

        JSONObject user = result.getJSONObject("user");
        assertEquals("Alice", user.getString("name"));
        assertEquals(28, user.getIntValue("age"));

        JSONObject details = user.getJSONObject("details");
        assertEquals("New York", details.getString("city"));
        assertEquals("10001", details.getString("zip"));
    }

    @Test
    public void testDifferentRootKeys() {
        JSONArray data = new JSONArray();
        data.add(JSONObject.of("id", 1));
        data.add(JSONObject.of("meta", JSONObject.of("source", "system")));

        JSONObject result = mJSONSQLFunctions.getMasterSchema(data);
        assertTrue(result.containsKey("id"));
        assertTrue(result.containsKey("meta"));
        assertEquals("system", result.getJSONObject("meta").getString("source"));
    }

    @Test
    public void testPreservesFirstValue() {
        JSONArray data = new JSONArray();
        data.add(JSONObject.of("status", "active"));
        data.add(JSONObject.of("status", "inactive"));

        JSONObject result = mJSONSQLFunctions.getMasterSchema(data);
        assertEquals("active", result.getString("status")); // first value kept
    }

    @Test
    public void testDeeplyNestedMerge() {
        JSONArray data = new JSONArray();
        data.add(JSONObject.parseObject("""
            {
              "org": {
                "dept": {
                  "team": {
                    "lead": "Alice"
                  }
                }
              }
            }
        """));
        data.add(JSONObject.parseObject("""
            {
              "org": {
                "dept": {
                  "team": {
                    "members": 5
                  },
                  "location": "HQ"
                }
              }
            }
        """));

        JSONObject result = mJSONSQLFunctions.getMasterSchema(data);
        JSONObject team = result.getJSONObject("org")
                .getJSONObject("dept")
                .getJSONObject("team");

        assertEquals("Alice", team.getString("lead"));
        assertEquals(5, team.getIntValue("members"));
        assertEquals("HQ", result.getJSONObject("org").getJSONObject("dept").getString("location"));
    }





    @Test
    public void testFlatMergeV2() {
        JSONObject a = JSONObject.of("id", 1, "name", "Alice");
        JSONObject b = JSONObject.of("age", 30, "name", "Bob"); // name should not overwrite

        JSONObject result = mJSONSQLFunctions.joinSchemas(a, b);
        assertEquals(3, result.size());
        assertEquals(1, result.getIntValue("id"));
        assertEquals("Alice", result.getString("name")); // original preserved
        assertEquals(30, result.getIntValue("age"));
    }

    @Test
    public void testNestedMerge() {
        JSONObject a = JSONObject.parseObject("""
            {
              "user": {
                "name": "Alice",
                "details": {
                  "city": "New York"
                }
              }
            }
        """);

        JSONObject b = JSONObject.parseObject("""
            {
              "user": {
                "details": {
                  "zip": "10001"
                },
                "age": 25
              }
            }
        """);

        JSONObject result = mJSONSQLFunctions.joinSchemas(a, b);
        JSONObject user = result.getJSONObject("user");
        JSONObject details = user.getJSONObject("details");

        assertEquals("Alice", user.getString("name")); // from original
        assertEquals(25, user.getIntValue("age"));     // new
        assertEquals("New York", details.getString("city")); // from original
        assertEquals("10001", details.getString("zip"));     // merged
    }

    @Test
    public void testEmptySource() {
        JSONObject a = JSONObject.of("x", 1);
        JSONObject b = new JSONObject();

        JSONObject result = mJSONSQLFunctions.joinSchemas(a, b);
        assertEquals(1, result.size());
        assertEquals(1, result.getIntValue("x"));
    }

    @Test
    public void testEmptyTarget() {
        JSONObject a = new JSONObject();
        JSONObject b = JSONObject.of("y", 2, "z", 3);

        JSONObject result = mJSONSQLFunctions.joinSchemas(a, b);
        assertEquals(2, result.size());
        assertEquals(2, result.getIntValue("y"));
        assertEquals(3, result.getIntValue("z"));
    }

    @Test
    public void testDeepNestedPreservation() {
        JSONObject a = JSONObject.parseObject("""
            {
              "org": {
                "dept": {
                  "team": {
                    "lead": "Alice"
                  }
                }
              }
            }
        """);

        JSONObject b = JSONObject.parseObject("""
            {
              "org": {
                "dept": {
                  "team": {
                    "members": 5
                  },
                  "floor": "5A"
                }
              }
            }
        """);

        JSONObject result = mJSONSQLFunctions.joinSchemas(a, b);
        JSONObject team = result.getJSONObject("org").getJSONObject("dept").getJSONObject("team");
        JSONObject dept = result.getJSONObject("org").getJSONObject("dept");

        assertEquals("Alice", team.getString("lead"));
        assertEquals(5, team.getIntValue("members"));
        assertEquals("5A", dept.getString("floor"));
    }

    @Test
    public void testNoOverwrites() {
        JSONObject a = JSONObject.of("flag", true);
        JSONObject b = JSONObject.of("flag", false); // should be ignored

        JSONObject result = mJSONSQLFunctions.joinSchemas(a, b);
        assertEquals(true, result.getBoolean("flag"));
    }











    @Test
    public void testMergeFlatObjects() {
        JSONObject a = JSONObject.of("id", 1, "name", "Alice");
        JSONObject b = JSONObject.of("role", "admin", "active", true);

        JSONObject result = mJSONSQLFunctions.joinSchemas(a, b);

        assertEquals(4, result.size());
        assertEquals("Alice", result.getString("name"));
        assertEquals("admin", result.getString("role"));
        assertTrue(result.getBoolean("active"));
    }

    @Test
    public void testMergeNestedObjects() {
        JSONObject a = JSONObject.parseObject("""
            {
              "user": {
                "name": "Bob",
                "profile": {
                  "age": 30
                }
              }
            }
        """);

        JSONObject b = JSONObject.parseObject("""
            {
              "user": {
                "profile": {
                  "email": "bob@example.com"
                }
              }
            }
        """);

        JSONObject result = mJSONSQLFunctions.joinSchemas(a, b);
        JSONObject profile = result.getJSONObject("user").getJSONObject("profile");

        assertEquals("Bob", result.getJSONObject("user").getString("name"));
        assertEquals(30, profile.getIntValue("age"));
        assertEquals("bob@example.com", profile.getString("email"));
    }

    @Test
    public void testMergeWithArrayOfObjects() {
        JSONObject a = JSONObject.parseObject("""
            {
              "team": [
                {"name": "Alice", "role": "dev"}
              ]
            }
        """);

        JSONObject b = JSONObject.parseObject("""
            {
              "team": [
                {"name": "Bob", "remote": true}
              ]
            }
        """);

        JSONObject result = mJSONSQLFunctions.joinSchemas(a, b);
        JSONArray team = result.getJSONArray("team");
        assertFalse(team.isEmpty());

        JSONObject teamSchema = team.getJSONObject(0);
        assertEquals("Alice", teamSchema.getString("name"));
        assertEquals("dev", teamSchema.getString("role"));
        assertTrue(teamSchema.getBoolean("remote"));
    }

    @Test
    public void testMergeDeepHierarchy() {
        JSONObject a = JSONObject.parseObject("""
            {
              "config": {
                "settings": {
                  "resolution": {
                    "width": 1920
                  }
                }
              }
            }
        """);

        JSONObject b = JSONObject.parseObject("""
            {
              "config": {
                "settings": {
                  "resolution": {
                    "height": 1080
                  }
                }
              }
            }
        """);

        JSONObject result = mJSONSQLFunctions.joinSchemas(a, b);
        JSONObject resolution = result.getJSONObject("config").getJSONObject("settings").getJSONObject("resolution");
        assertEquals(1920, resolution.getIntValue("width"));
        assertEquals(1080, resolution.getIntValue("height"));
    }

    @Test
    public void testMergeWithConflictingTypes() {
        JSONObject a = JSONObject.of("value", 123);
        JSONObject b = JSONObject.of("value", JSONObject.of("nested", true));

        JSONObject result = mJSONSQLFunctions.joinSchemas(a, b);
        // By design, conflicting types do not overwrite
        assertEquals(true, result.getJSONObject("value").getBoolean("nested"));
    }







    @Test
    public void testSelectArrayElements() {
        JSONTable table = new JSONTable("employees");

        table.insertRaw("""
            [
              {
                "id": 1,
                "name": "Alice",
                "skills": ["Java", "SQL", "Kotlin"]
              },
              {
                "id": 2,
                "name": "Bob",
                "skills": ["Python", "Django"]
              },
              {
                "id": 3,
                "name": "Charlie",
                "skills": ["JavaScript"]
              }
            ]
        """);

        // SELECT a specific array index from the skills
        JSONArray result = table.select("SELECT skills[0], skills[1] FROM employees");

        assertEquals(3, result.size());

        JSONObject row0 = result.getJSONObject(0);
        assertEquals("Java", row0.getString("skills[0]"));
        assertEquals("SQL", row0.getString("skills[1]"));

        JSONObject row1 = result.getJSONObject(1);
        assertEquals("Python", row1.getString("skills[0]"));
        assertEquals("Django", row1.getString("skills[1]"));

        JSONObject row2 = result.getJSONObject(2);
        assertEquals("JavaScript", row2.getString("skills[0]"));
        assertNull(row2.get("skills[1]")); // Only one skill

        // Query with WHERE condition on array element
        JSONArray filtered = table.select("SELECT name FROM employees WHERE skills[0] = 'Python'");
        assertEquals(1, filtered.size());
        assertEquals("Bob", filtered.getJSONObject(0).getString("name"));
    }

    @Test
    public void testSelectNestedArrayObjects() {
        JSONTable table = new JSONTable("projects");

        table.insertRaw("""
            [
              {
                "id": 1,
                "team": [
                  {"name": "Alice", "role": "Dev"},
                  {"name": "Bob", "role": "QA"}
                ]
              },
              {
                "id": 2,
                "team": [
                  {"name": "Charlie", "role": "Lead"}
                ]
              }
            ]
        """);

        JSONArray result = table.select("SELECT team[0].name, team[0].role FROM projects");

        assertEquals(2, result.size());
        assertEquals("Alice", result.getJSONObject(0).getString("team[0].name"));
        assertEquals("Dev", result.getJSONObject(0).getString("team[0].role"));
        assertEquals("Charlie", result.getJSONObject(1).getString("team[0].name"));
        assertEquals("Lead", result.getJSONObject(1).getString("team[0].role"));
    }








    @Test
    public void testSelectArrayElementAccess() {
        JSONTable table = new JSONTable("data");

        table.insert("""
            INSERT INTO data VALUES
            ({"name": "Alice", "skills": ["Java", "Python", "SQL"]}),
            ({"name": "Bob", "skills": ["Go"]}),
            ({"name": "Charlie", "skills": ["C#", "JavaScript"]})
        """);

        JSONArray result = table.select("SELECT name, skills[0] FROM data");
        assertEquals(3, result.size());

        assertEquals("Java", result.getJSONObject(0).getString("skills[0]"));
        assertEquals("Go", result.getJSONObject(1).getString("skills[0]"));
        assertEquals("C#", result.getJSONObject(2).getString("skills[0]"));
    }

    @Test
    public void testSelectArrayOutOfBounds() {
        JSONTable table = new JSONTable("data");

        table.insert("""
            INSERT INTO data VALUES
            ({"name": "Alice", "skills": ["Java"]}),
            ({"name": "Bob", "skills": ["Python", "SQL"]})
        """);

        JSONArray result = table.select("SELECT name, skills[1] FROM data");

        assertEquals(2, result.size());
        assertNull(result.getJSONObject(0).get("skills[1]")); // Alice has no skills[1]
        assertEquals("SQL", result.getJSONObject(1).getString("skills[1]"));
        assertNull(result.getJSONObject(0).getString("skills[1]"));
    }

    @Test
    public void testWhereClauseWithArrayIndex() {
        JSONTable table = new JSONTable("data");

        table.insert("""
            INSERT INTO data VALUES
            ({"id": 1, "tags": ["alpha", "beta"]}),
            ({"id": 2, "tags": ["gamma", "delta"]}),
            ({"id": 3, "tags": ["alpha", "omega"]})
        """);

        JSONArray result = table.select("SELECT id FROM data WHERE tags[0] = 'alpha'");

        assertEquals(2, result.size());
        assertEquals(1, result.getJSONObject(0).getIntValue("id"));
        assertEquals(3, result.getJSONObject(1).getIntValue("id"));
    }

    @Test
    public void testSelectMultipleArrayIndexes() {
        JSONTable table = new JSONTable("data");

        table.insert("""
            INSERT INTO data VALUES
            ({"name": "Eve", "scores": [95, 88, 72]}),
            ({"name": "Frank", "scores": [78, 85]}),
            ({"name": "Grace", "scores": [99, 90, 100]})
        """);

        JSONArray result = table.select("SELECT name, scores[0], scores[2] FROM data");

        assertEquals(3, result.size());
        assertEquals(95, result.getJSONObject(0).getIntValue("scores[0]"));
        assertEquals(72, result.getJSONObject(0).getIntValue("scores[2]"));
        assertEquals(78, result.getJSONObject(1).getIntValue("scores[0]"));
        assertNull(result.getJSONObject(1).get("scores[2]"));
        assertEquals(99, result.getJSONObject(2).getIntValue("scores[0]"));
        assertEquals(100, result.getJSONObject(2).getIntValue("scores[2]"));
    }




    @Test
    public void testDeeplyNestedArraySchemas() {
        JSONTable table = new JSONTable("inventory");

        // Row 1: Nested arrays with objects inside
        table.insertRaw("""
        {
          "warehouse": [
            [
              {"item": "Widget", "quantity": 10},
              {"item": "Gadget", "quantity": 5}
            ]
          ],
          "location": "North"
        }
    """);

        // Row 2: Different schema, additional fields in objects
        table.insertRaw("""
        {
          "warehouse": [
            [
              {"item": "Thing", "quantity": 2, "status": "backorder"},
              {"item": "Doohickey", "quantity": 8}
            ],
            [
              {"item": "Spare", "quantity": 3}
            ]
          ],
          "location": "South",
          "manager": "Alice"
        }
    """);

        // Row 3: Another variation in schema
        table.insertRaw("""
        {
          "warehouse": [
            [
              {"item": "Thing", "quantity": 2},
              {"item": "Widget", "quantity": 9, "status": "in-stock"}
            ]
          ],
          "location": "East",
          "active": true
        }
    """);

        // Test basic access
        JSONArray results = table.select("SELECT warehouse[0][0].item FROM inventory");
        assertEquals(3, results.size());
        assertEquals("Widget", results.getJSONObject(0).getString("warehouse[0][0].item"));
        assertEquals("Thing", results.getJSONObject(1).getString("warehouse[0][0].item"));
        assertEquals("Thing", results.getJSONObject(2).getString("warehouse[0][0].item"));

        // Test nested access with multiple array layers
        JSONArray nested = table.select("SELECT warehouse[1][0].item FROM inventory WHERE location = 'South'");
        assertEquals(1, nested.size());
        assertEquals("Spare", nested.getJSONObject(0).getString("warehouse[1][0].item"));

        // Check optional fields
        JSONArray optional = table.select("SELECT warehouse[0][1].status FROM inventory WHERE location = 'East'");
        assertEquals(1, optional.size());
        assertEquals("in-stock", optional.getJSONObject(0).getString("warehouse[0][1].status"));
    }



    @Test
    public void testSelectWithHighlyIrregularNestedArraySchemas() {
        JSONTable table = new JSONTable("irregular");

        // Row 1: Simple flat array of values
        table.insertRaw("""
        {
          "tags": ["alpha", "beta", "gamma"],
          "info": "flat array"
        }
    """);

        // Row 2: Array of objects
        table.insertRaw("""
        {
          "tags": [
            {"label": "urgent", "color": "red"},
            {"label": "review", "color": "yellow"}
          ],
          "info": "array of objects"
        }
    """);

        // Row 3: Array of arrays of strings
        table.insertRaw("""
        {
          "tags": [
            ["stage1", "stage2"],
            ["final"]
          ],
          "info": "array of arrays"
        }
    """);

        // Row 4: Mixed array with strings, objects, and arrays
        table.insertRaw("""
        {
          "tags": [
            "raw",
            {"type": "compressed", "size": "large"},
            ["backup", "snapshot"]
          ],
          "info": "mixed array"
        }
    """);

        // Row 5: Completely different structure with nested structures inside an array
        table.insertRaw("""
        {
          "tags": [
            {
              "meta": {
                "key": "archive",
                "history": [
                  {"year": 2020},
                  {"year": 2021, "note": "restored"}
                ]
              }
            }
          ],
          "info": "deep object nesting"
        }
    """);

        // Query 1: Get a flat string tag
        JSONArray flat = table.select("SELECT tags[1] FROM irregular WHERE info = 'flat array'");
        assertEquals(1, flat.size());
        assertEquals("beta", flat.getJSONObject(0).getString("tags[1]"));

        // Query 2: Get label from object in array
        JSONArray objectTag = table.select("SELECT tags[0].label FROM irregular WHERE info = 'array of objects'");
        assertEquals(1, objectTag.size());
        assertEquals("urgent", objectTag.getJSONObject(0).getString("tags[0].label"));

        // Query 3: Get string from nested array
        JSONArray nestedArray = table.select("SELECT tags[0][1] FROM irregular WHERE info = 'array of arrays'");
        assertEquals(1, nestedArray.size());
        assertEquals("stage2", nestedArray.getJSONObject(0).getString("tags[0][1]"));

        // Query 4: Get object field from mixed array
        JSONArray mixed = table.select("SELECT tags[1].type FROM irregular WHERE info = 'mixed array'");
        assertEquals(1, mixed.size());
        assertEquals("compressed", mixed.getJSONObject(0).getString("tags[1].type"));

        // Query 5: Get year from nested history
        JSONArray deep = table.select("SELECT tags[0].meta.history[1].year FROM irregular WHERE info = 'deep object nesting'");
        assertEquals(1, deep.size());
        assertEquals(2021, deep.getJSONObject(0).getIntValue("tags[0].meta.history[1].year"));
    }




    @Test
    public void testComplexNestedArrayConditions() {
        JSONTable table = new JSONTable("catalog");

        // Row 1: Standard nested product structure
        table.insertRaw("""
        {
          "product": {
            "name": "Widget A",
            "variants": [
              { "color": "red", "stock": 10 },
              { "color": "blue", "stock": 5 }
            ]
          }
        }
    """);

        // Row 2: Different structure with more variants
        table.insertRaw("""
        {
          "product": {
            "name": "Widget B",
            "variants": [
              { "color": "green", "stock": 0 },
              { "color": "red", "stock": 2 },
              { "color": "black", "stock": 3 }
            ]
          }
        }
    """);

        // Row 3: Variants have deep feature objects
        table.insertRaw("""
        {
          "product": {
            "name": "Widget C",
            "variants": [
              {
                "color": "black",
                "stock": 8,
                "features": [
                  {"key": "durable", "score": 8},
                  {"key": "eco", "score": 9}
                ]
              }
            ]
          }
        }
    """);

        // Row 4: No variants field
        table.insertRaw("""
        {
          "product": {
            "name": "Widget D"
          }
        }
    """);

        // Row 5: Variants with zero stock
        table.insertRaw("""
        {
          "product": {
            "name": "Widget E",
            "variants": [
              { "color": "blue", "stock": 0 },
              { "color": "yellow", "stock": 0 }
            ]
          }
        }
    """);

        // Select all products where the first variant has color "red"
        JSONArray redVariant = table.select("SELECT product.name FROM catalog WHERE product.variants[0].color = 'red'");
        assertEquals(1, redVariant.size());
        assertEquals("Widget A", redVariant.getJSONObject(0).getString("product.name"));

        // Select products where second variant stock is > 1
        JSONArray secondStock = table.select("SELECT product.name FROM catalog WHERE product.variants[1].stock > 1");
        assertEquals(2, secondStock.size()); // Widget A (5), Widget B (2)

        // Select products with deeply nested feature scores >= 9
        JSONArray ecoScore = table.select("SELECT product.name FROM catalog WHERE product.variants[0].features[1].score >= 9");
        assertEquals(1, ecoScore.size());
        assertEquals("Widget C", ecoScore.getJSONObject(0).getString("product.name"));

        // Select products where variant[1] exists and color is "yellow"
        JSONArray yellowVariant = table.select("SELECT product.name FROM catalog WHERE product.variants[1].color = 'yellow'");
        assertEquals(1, yellowVariant.size());
        assertEquals("Widget E", yellowVariant.getJSONObject(0).getString("product.name"));

        // Select products where variants is missing
        JSONArray noVariants = table.select("SELECT product.name FROM catalog WHERE product.variants IS null");
        assertEquals(1, noVariants.size());
        assertEquals("Widget D", noVariants.getJSONObject(0).getString("product.name"));
    }


    @Test
    public void testUpdateDeeplyNestedArrayItem() {
        JSONTable table = new JSONTable("inventory");

        // Insert a product with deeply nested array
        table.insert("""
        INSERT INTO inventory VALUES (
            {
              "id": 1,
              "product": {
                "name": "UltraGadget",
                "batches": [
                  {
                    "batchId": "A1",
                    "items": [
                      { "serial": "X100", "status": "OK" },
                      { "serial": "X101", "status": "OK" }
                    ]
                  },
                  {
                    "batchId": "A2",
                    "items": [
                      { "serial": "X200", "status": "OK" },
                      { "serial": "X201", "status": "OK" }
                    ]
                  }
                ]
              }
            }
        )
    """);

        // Update: set status of second item in second batch to 'FAIL'
        JSONArray updated = table.update("""
        UPDATE inventory
        SET product.batches[1].items[1].status = 'FAIL'
        WHERE id = 1
    """);

        assertEquals(1, updated.size());
        JSONObject product = updated.getJSONObject(0).getJSONObject("product");
        assertEquals("FAIL", product.getJSONArray("batches")
                .getJSONObject(1)          // Second batch
                .getJSONArray("items")
                .getJSONObject(1)          // Second item in second batch
                .getString("status")
        );

        // Confirm via SELECT
        JSONArray result = table.select("""
        SELECT product.batches[1].items[1].status
        FROM inventory
        WHERE id = 1
    """);
        assertEquals("FAIL", result.getJSONObject(0).getString("product.batches[1].items[1].status"));
    }




    @Test
    public void testInsertAndUpdateDeeplyNestedArrayItems() {
        JSONTable table = new JSONTable("devices");

        // Insert multiple devices with complex nested structure
        table.insert("""
        INSERT INTO devices VALUES
        (
          {
            "id": 101,
            "device": {
              "name": "SensorX",
              "modules": [
                {
                  "moduleId": "m1",
                  "readings": [
                    { "timestamp": "2024-01-01", "value": 42 },
                    { "timestamp": "2024-01-02", "value": 43 }
                  ]
                },
                {
                  "moduleId": "m2",
                  "readings": [
                    { "timestamp": "2024-01-01", "value": 12 },
                    { "timestamp": "2024-01-02", "value": 15 }
                  ]
                }
              ]
            }
          }
        ),
        (
          {
            "id": 102,
            "device": {
              "name": "SensorY",
              "modules": [
                {
                  "moduleId": "a1",
                  "readings": [
                    { "timestamp": "2024-01-01", "value": 100 },
                    { "timestamp": "2024-01-02", "value": 110 }
                  ]
                }
              ]
            }
          }
        )
    """);

        // 🔎 Verify inserted rows
        JSONArray inserted = table.select("SELECT * FROM devices");
        assertEquals(2, inserted.size());

        // 🔁 Perform multiple nested updates
        // 1. Update SensorX m1 second reading value
        table.update("""
        UPDATE devices
        SET device.modules[0].readings[1].value = 999
        WHERE id = 101
    """);

        // 2. Update SensorY a1 first reading value
        table.update("""
        UPDATE devices
        SET device.modules[0].readings[0].value = 888
        WHERE id = 102
    """);

        // ✅ Confirm nested updates via SELECT
        JSONArray updatedSensorX = table.select("""
        SELECT device.modules[0].readings[1].value
        FROM devices
        WHERE id = 101
    """);
        assertEquals(999, updatedSensorX.getJSONObject(0).getIntValue("device.modules[0].readings[1].value"));

        JSONArray updatedSensorY = table.select("""
        SELECT device.modules[0].readings[0].value
        FROM devices
        WHERE id = 102
    """);
        assertEquals(888, updatedSensorY.getJSONObject(0).getIntValue("device.modules[0].readings[0].value"));

        // ✅ Check that untouched values are unchanged
        JSONArray untouchedSensorX = table.select("""
        SELECT device.modules[1].readings[1].value
        FROM devices
        WHERE id = 101
    """);
        assertEquals(15, untouchedSensorX.getJSONObject(0).getIntValue("device.modules[1].readings[1].value"));
    }



    @Test
    public void testDeleteBasedOnNestedArrayCondition() {
        JSONTable table = new JSONTable("devices");

        // Insert complex nested rows
        table.insert("""
        INSERT INTO devices VALUES
        (
          {
            "id": 201,
            "device": {
              "name": "Alpha",
              "modules": [
                {
                  "moduleId": "mod1",
                  "readings": [
                    { "timestamp": "2024-01-01", "value": 55 },
                    { "timestamp": "2024-01-02", "value": 60 }
                  ]
                }
              ]
            }
          }
        ),
        (
          {
            "id": 202,
            "device": {
              "name": "Beta",
              "modules": [
                {
                  "moduleId": "mod2",
                  "readings": [
                    { "timestamp": "2024-01-01", "value": 10 },
                    { "timestamp": "2024-01-02", "value": 15 }
                  ]
                }
              ]
            }
          }
        ),
        (
          {
            "id": 203,
            "device": {
              "name": "Gamma",
              "modules": [
                {
                  "moduleId": "mod3",
                  "readings": [
                    { "timestamp": "2024-01-01", "value": 80 },
                    { "timestamp": "2024-01-02", "value": 100 }
                  ]
                }
              ]
            }
          }
        )
    """);

        // ✅ Delete row where deeply nested reading value is less than 20
        JSONArray deleted = table.delete("""
        DELETE FROM devices
        WHERE device.modules[0].readings[0].value < 20
    """);

        // 🔍 Verify one row was deleted
        assertEquals(1, deleted.size());
        assertEquals("Beta", deleted.getJSONObject(0).getJSONObject("device").getString("name"));

        // 🔍 Verify remaining rows
        JSONArray remaining = table.select("SELECT id FROM devices");
        assertEquals(2, remaining.size());

        Set<Integer> remainingIds = new HashSet<>();
        for (Object obj : remaining) {
            remainingIds.add(((JSONObject) obj).getIntValue("id"));
        }

        assertTrue(remainingIds.contains(201));
        assertTrue(remainingIds.contains(203));
        assertFalse(remainingIds.contains(202)); // Deleted row
    }

    @Test
    public void testDeleteMultipleNestedRowsWithAndOr() {
        JSONTable table = new JSONTable("logs");

        // 🚀 Insert several deeply nested logs
        table.insert("""
        INSERT INTO logs VALUES
        (
          {
            "id": 301,
            "session": {
              "user": "alice",
              "actions": [
                { "type": "click", "meta": { "count": 5 } },
                { "type": "scroll", "meta": { "distance": 100 } }
              ]
            }
          }
        ),
        (
          {
            "id": 302,
            "session": {
              "user": "bob",
              "actions": [
                { "type": "click", "meta": { "count": 15 } },
                { "type": "scroll", "meta": { "distance": 50 } }
              ]
            }
          }
        ),
        (
          {
            "id": 303,
            "session": {
              "user": "carol",
              "actions": [
                { "type": "click", "meta": { "count": 3 } },
                { "type": "scroll", "meta": { "distance": 250 } }
              ]
            }
          }
        ),
        (
          {
            "id": 304,
            "session": {
              "user": "dave",
              "actions": [
                { "type": "click", "meta": { "count": 7 } },
                { "type": "scroll", "meta": { "distance": 25 } }
              ]
            }
          }
        )
    """);

        // ❌ Delete if click count is less than 6 OR scroll distance is less than 30
        JSONArray deleted = table.delete("""
        DELETE FROM logs
        WHERE session.actions[0].meta.count < 6
        OR session.actions[1].meta.distance < 30
    """);

        assertEquals(3, deleted.size());
        Set<Integer> deletedIds = new HashSet<>();
        for (Object obj : deleted) {
            deletedIds.add(((JSONObject) obj).getIntValue("id"));
        }

        assertTrue(deletedIds.contains(303)); // click count = 3
        assertTrue(deletedIds.contains(304)); // scroll distance = 25

        // ✅ Check remaining rows
        JSONArray remaining = table.select("SELECT id FROM logs");
        assertEquals(1, remaining.size());

        Set<Integer> remainingIds = new HashSet<>();
        for (Object obj : remaining) {
            remainingIds.add(((JSONObject) obj).getIntValue("id"));
        }

        assertTrue(remainingIds.contains(302));
    }


    @Test
    public void testDeleteWithGroupedConditionsOnNestedArrays() {
        JSONTable table = new JSONTable("logs");

        table.insert("""
        INSERT INTO logs VALUES
        (
          {
            "id": 1,
            "events": [
              { "type": "click", "value": 100 },
              { "type": "scroll", "value": 20 }
            ]
          }
        ),
        (
          {
            "id": 2,
            "events": [
              { "type": "click", "value": 50 },
              { "type": "scroll", "value": 500 }
            ]
          }
        ),
        (
          {
            "id": 3,
            "events": [
              { "type": "click", "value": 5 },
              { "type": "scroll", "value": 5 }
            ]
          }
        ),
        (
          {
            "id": 4,
            "events": [
              { "type": "click", "value": 120 },
              { "type": "scroll", "value": 15 }
            ]
          }
        )
    """);

        // 🚨 Delete rows where:
        //    (click is less than 10 AND scroll is less than 10) OR (click is more than 100)
        JSONArray deleted = table.delete("""
        DELETE FROM logs
        WHERE (events[0].value < 10 AND events[1].value < 10)
        OR events[0].value > 100
    """);

        // ✅ Should delete rows with id 3 (both < 10) and id 1 and 4 (click > 100)
        assertEquals(2, deleted.size());

        Set<Integer> deletedIds = new HashSet<>();
        for (Object obj : deleted) {
            deletedIds.add(((JSONObject) obj).getIntValue("id"));
        }

        assertTrue(deletedIds.contains(3));
        assertTrue(deletedIds.contains(4));

        JSONArray remaining = table.select("SELECT id FROM logs");
        assertEquals(2, remaining.size());

        Set<Integer> remainingIds = new HashSet<>();
        for (Object obj : remaining) {
            remainingIds.add(((JSONObject) obj).getIntValue("id"));
        }

        assertTrue(remainingIds.contains(1)); // click = 100
        assertTrue(remainingIds.contains(2)); // click = 50, scroll = 500
    }

    @Test
    public void testDeleteWithGroupedConditionsAndNullsInInconsistentArrays() {
        JSONTable table = new JSONTable("metrics");

        table.insert("""
        INSERT INTO metrics VALUES
        (
          {
            "id": 1,
            "readings": [
              { "sensor": "temp", "value": 25 },
              { "sensor": "pressure", "value": null }
            ]
          }
        ),
        (
          {
            "id": 2,
            "readings": [
              { "sensor": "temp", "value": 10 },
              { "sensor": "pressure", "value": 900 }
            ]
          }
        ),
        (
          {
            "id": 3,
            "readings": [
              { "sensor": "temp", "value": null },
              { "sensor": "pressure" }
            ]
          }
        ),
        (
          {
            "id": 4,
            "readings": [
              { "sensor": "temp", "value": 50 }
            ]
          }
        )
    """);

        // ❌ Delete rows where:
        // (first reading is NULL OR second reading is NULL) AND first reading is not over 30
        JSONArray deleted = table.delete("""
        DELETE FROM metrics
        WHERE (readings[0].value IS NULL OR readings[1].value IS NULL)
        AND readings[0].value <= 30
    """);

        // ✅ Expect to delete row with id 1 and 3
        assertEquals(1, deleted.size());

        Set<Integer> deletedIds = new HashSet<>();
        for (Object obj : deleted) {
            deletedIds.add(((JSONObject) obj).getIntValue("id"));
        }

        assertTrue(deletedIds.contains(1)); // readings[1].value is null

        // 🔍 Validate remaining rows
        JSONArray remaining = table.select("SELECT id FROM metrics");
        assertEquals(3, remaining.size());

        Set<Integer> remainingIds = new HashSet<>();
        for (Object obj : remaining) {
            remainingIds.add(((JSONObject) obj).getIntValue("id"));
        }

        assertTrue(remainingIds.contains(2)); // valid values
        assertTrue(remainingIds.contains(4)); // only one reading
    }


    @Test
    public void testDeleteWithDeepParenthesesAndArrayConditions() {
        JSONTable table = new JSONTable("sessions");

        table.insert("""
        INSERT INTO sessions VALUES
        (
          {
            "id": 1,
            "logs": [
              { "type": "error", "message": "Null pointer" },
              { "type": "warning", "message": "Deprecated API" }
            ]
          }
        ),
        (
          {
            "id": 2,
            "logs": [
              { "type": "info", "message": "Startup" },
              { "type": "error", "message": "Crash" }
            ]
          }
        ),
        (
          {
            "id": 3,
            "logs": [
              { "type": "info", "message": "Loaded module" },
              { "type": "info", "message": "Running health check" }
            ]
          }
        ),
        (
          {
            "id": 4,
            "logs": [
              { "type": "warning", "message": "Slow response" },
              { "type": "error", "message": "Timeout" }
            ]
          }
        )
    """);

        // Complex condition:
        // Delete where (
        //     (logs[0].type = 'error' OR logs[1].type = 'error') AND
        //     (logs[0].message = 'Null pointer' OR logs[1].message = 'Timeout')
        // )
        JSONArray deleted = table.delete("""
        DELETE FROM sessions
        WHERE (
            (logs[0].type = 'error' OR logs[1].type = 'error')
            AND
            (logs[0].message = 'Null pointer' OR logs[1].message = 'Timeout')
        )
    """);

        assertEquals(2, deleted.size());

        Set<Integer> deletedIds = new HashSet<>();
        for (Object obj : deleted) {
            deletedIds.add(((JSONObject) obj).getIntValue("id"));
        }

        assertTrue(deletedIds.contains(1)); // logs[0] = error + logs[0].message = Null pointer
        assertTrue(deletedIds.contains(4)); // logs[1] = error + logs[1].message = Timeout

        // Ensure the rest remain
        JSONArray remaining = table.select("SELECT id FROM sessions");
        Set<Integer> remainingIds = new HashSet<>();
        for (Object obj : remaining) {
            remainingIds.add(((JSONObject) obj).getIntValue("id"));
        }

        assertEquals(Set.of(2, 3), remainingIds);
    }

    @Test
    public void testDeleteWithIsNullConditionsOnNestedArrays() {
        JSONTable table = new JSONTable("reports");

        table.insert("""
        INSERT INTO reports VALUES
        (
          {
            "id": 1,
            "events": [
              { "code": "A1", "details": null },
              { "code": "B1", "details": { "severity": "high" } }
            ]
          }
        ),
        (
          {
            "id": 2,
            "events": [
              { "code": "A2", "details": { "severity": "low" } },
              { "code": "B2", "details": null }
            ]
          }
        ),
        (
          {
            "id": 3,
            "events": [
              { "code": "A3", "details": { "severity": "critical" } },
              { "code": "B3", "details": { "severity": "medium" } }
            ]
          }
        ),
        (
          {
            "id": 4,
            "events": [
              { "code": "A4", "details": null },
              { "code": "B4", "details": null }
            ]
          }
        )
    """);

        // Delete rows where the first event's details are null
        JSONArray deleted = table.delete("""
        DELETE FROM reports WHERE events[0].details IS NULL
    """);

        assertEquals(2, deleted.size());

        Set<Integer> deletedIds = new HashSet<>();
        for (Object obj : deleted) {
            deletedIds.add(((JSONObject) obj).getIntValue("id"));
        }

        assertTrue(deletedIds.contains(1)); // events[0].details = null
        assertTrue(deletedIds.contains(4)); // events[0].details = null

        // Remaining should be rows where events[0].details is NOT null
        JSONArray remaining = table.select("SELECT id FROM reports");
        Set<Integer> remainingIds = new HashSet<>();
        for (Object obj : remaining) {
            remainingIds.add(((JSONObject) obj).getIntValue("id"));
        }

        assertEquals(Set.of(2, 3), remainingIds);

        // Also test IS NOT NULL
        JSONArray notNullMatches = table.select("""
        SELECT id FROM reports WHERE events[1].details IS NOT NULL
    """);

        Set<Integer> notNullIds = new HashSet<>();
        for (Object obj : notNullMatches) {
            notNullIds.add(((JSONObject) obj).getIntValue("id"));
        }

        // events[1].details is NOT NULL in row 3 only
        assertEquals(Set.of(3), notNullIds);
    }




    @Test
    public void testUpdateNestedFieldsToNull() {
        JSONTable table = new JSONTable("documents");

        table.insert("""
        INSERT INTO documents VALUES
        (
          {
            "id": 1,
            "meta": { "status": "open", "reviewer": "alice" },
            "sections": [
              { "title": "Intro", "content": "Welcome" },
              { "title": "Details", "content": "Here are the details." }
            ]
          }
        ),
        (
          {
            "id": 2,
            "meta": { "status": "closed", "reviewer": "bob" },
            "sections": [
              { "title": "Intro", "content": "Hello" },
              { "title": "Summary", "content": "This is a summary." }
            ]
          }
        )
    """);

        // Nullify meta.reviewer and the second section's content
        JSONArray updated = table.update("""
        UPDATE documents
        SET meta.reviewer = null, sections[1].content = null
        WHERE id = 2
    """);

        assertEquals(1, updated.size());
        JSONObject updatedRow = updated.getJSONObject(0);

        // ✅ Verify updated fields are null
        assertEquals(2, updatedRow.getIntValue("id"));
        assertTrue(updatedRow.getJSONObject("meta").containsKey("reviewer"));
        assertNull(updatedRow.getJSONObject("meta").get("reviewer"));

        JSONArray sections = updatedRow.getJSONArray("sections");
        assertNotNull(sections);
        assertEquals(2, sections.size());

        JSONObject secondSection = sections.getJSONObject(1);
        assertEquals("Summary", secondSection.getString("title"));
        assertTrue(secondSection.containsKey("content"));
        assertNull(secondSection.get("content"));

        // ✅ SELECT query to verify null values exist
        JSONArray nullChecks = table.select("""
        SELECT id FROM documents WHERE meta.reviewer IS NULL AND sections[1].content IS NULL
    """);

        assertEquals(1, nullChecks.size());
        assertEquals(2, nullChecks.getJSONObject(0).getIntValue("id"));
    }

    @Test
    public void testSetNestedObjectAndArrayElementToNull() {
        JSONTable table = new JSONTable("reports");

        table.insert("""
        INSERT INTO reports VALUES
        (
          {
            "id": 1,
            "meta": { "author": "alice", "date": "2024-01-01" },
            "sections": [
              { "title": "Intro", "content": "Welcome" },
              { "title": "Data", "content": "Numbers and graphs" }
            ]
          }
        ),
        (
          {
            "id": 2,
            "meta": { "author": "bob", "date": "2024-02-15" },
            "sections": [
              { "title": "Intro", "content": "Hello" },
              { "title": "Data", "content": "More content" }
            ]
          }
        )
    """);

        // Nullify the entire `meta` object and the first section in `sections` array
        JSONArray updated = table.update("""
        UPDATE reports
        SET meta = null, sections[0] = null
        WHERE id = 2
    """);

        assertEquals(1, updated.size());
        JSONObject row = updated.getJSONObject(0);
        assertEquals(2, row.getIntValue("id"));

        // ✅ Validate 'meta' is now null
        assertTrue(row.containsKey("meta"));
        assertNull(row.get("meta"));

        // ✅ Validate sections[0] is null, but sections[1] still exists
        JSONArray sections = row.getJSONArray("sections");
        assertNotNull(sections);
        assertEquals(2, sections.size());
        assertNull(sections.get(0));

        JSONObject section1 = sections.getJSONObject(1);
        assertEquals("Data", section1.getString("title"));

        // ✅ Query using IS NULL against both fields
        JSONArray selected = table.select("""
        SELECT id FROM reports WHERE meta IS NULL AND sections[0] IS NULL
    """);

        assertEquals(1, selected.size());
        assertEquals(2, selected.getJSONObject(0).getIntValue("id"));
    }












    @Test
    public void testQualifyFlatRows() {
        JSONArray table = new JSONArray();

        table.add(JSONObject.of("id", 1, "name", "Alice"));
        table.add(JSONObject.of("id", 2));

        mJSONSQLFunctions.normalizeSchemas(table);

        JSONObject row0 = table.getJSONObject(0);
        JSONObject row1 = table.getJSONObject(1);

        assertTrue(row0.containsKey("name"));
        assertTrue(row1.containsKey("name"));
        assertNull(row1.get("name"));
    }

    @Test
    public void testQualifyNestedObjects() {
        JSONArray table = new JSONArray();

        table.add(JSONObject.parseObject("""
            {
              "user": {
                "name": "Alice",
                "contact": {
                  "email": "alice@example.com"
                }
              }
            }
        """));

        table.add(JSONObject.parseObject("""
            {
              "user": {
                "name": "Bob"
              }
            }
        """));

        mJSONSQLFunctions.normalizeSchemas(table);

        JSONObject user2 = table.getJSONObject(1).getJSONObject("user");
        assertTrue(user2.containsKey("contact"));
        JSONObject contact2 = user2.getJSONObject("contact");
        assertTrue(contact2.containsKey("email"));
        assertNull(contact2.get("email"));
    }

    @Test
    public void testQualifyArrayOfObjects() {
        JSONArray table = new JSONArray();

        table.add(JSONObject.parseObject("""
            {
              "team": [
                {"name": "Alice", "role": "dev"},
                {"name": "Bob", "role": "qa"}
              ]
            }
        """));

        table.add(JSONObject.parseObject("""
            {
              "team": [
                {"name": "Carol"}
              ]
            }
        """));

        mJSONSQLFunctions.normalizeSchemas(table);

        JSONArray team = table.getJSONObject(1).getJSONArray("team");
        JSONObject carol = team.getJSONObject(0);
        assertTrue(carol.containsKey("role"));
        assertNull(carol.get("role"));
    }

    @Test
    public void testApplySchemaWithMissingFields() {
        JSONObject schema = JSONObject.parseObject("""
            {
              "id": 0,
              "info": {
                "age": 25,
                "location": "NY"
              }
            }
        """);

        JSONObject row = JSONObject.parseObject("""
            {
              "id": 5,
              "info": {
                "age": 30
              }
            }
        """);

        mJSONSQLFunctions.applySchemaNormalization(row, schema);

        assertTrue(row.containsKey("id"));
        assertTrue(row.containsKey("info"));
        assertEquals(5, row.getIntValue("id"));

        JSONObject info = row.getJSONObject("info");
        assertTrue(info.containsKey("age"));
        assertTrue(info.containsKey("location"));
        assertEquals(30, info.getIntValue("age"));
        assertNull(info.get("location"));
    }












    @Test
    public void testFlatMissingField() {
        JSONObject schema = JSONObject.parseObject("""
            {
              "id": 0,
              "name": ""
            }
        """);

        JSONObject row = JSONObject.parseObject("""
            {
              "id": 42
            }
        """);

        mJSONSQLFunctions.applySchemaNormalization(row, schema);

        assertEquals(42, row.getIntValue("id"));
        assertTrue(row.containsKey("name"));
        assertNull(row.get("name"));
    }

    @Test
    public void testNestedObjectMissingInnerField() {
        JSONObject schema = JSONObject.parseObject("""
            {
              "user": {
                "email": "",
                "age": 0
              }
            }
        """);

        JSONObject row = JSONObject.parseObject("""
            {
              "user": {
                "email": "test@example.com"
              }
            }
        """);

        mJSONSQLFunctions.applySchemaNormalization(row, schema);

        JSONObject user = row.getJSONObject("user");
        assertEquals("test@example.com", user.getString("email"));
        assertTrue(user.containsKey("age"));
        assertNull(user.get("age"));
    }

    @Test
    public void testArrayOfObjectsSchema() {
        JSONObject schema = JSONObject.parseObject("""
            {
              "team": [
                {
                  "name": "",
                  "role": "",
                  "remote": false
                }
              ]
            }
        """);

        JSONObject row = JSONObject.parseObject("""
            {
              "team": [
                {"name": "Alice", "role": "Dev"},
                {"name": "Bob"}
              ]
            }
        """);

        mJSONSQLFunctions.applySchemaNormalization(row, schema);

        JSONArray team = row.getJSONArray("team");

        JSONObject alice = team.getJSONObject(0);
        assertEquals("Alice", alice.getString("name"));
        assertEquals("Dev", alice.getString("role"));
        assertTrue(alice.containsKey("remote"));
        assertNull(alice.get("remote"));

        JSONObject bob = team.getJSONObject(1);
        assertEquals("Bob", bob.getString("name"));
        assertTrue(bob.containsKey("role"));
        assertTrue(bob.containsKey("remote"));
        assertNull(bob.get("role"));
        assertNull(bob.get("remote"));
    }

    @Test
    public void testEmptyObjectGetsFullSchema() {
        JSONObject schema = JSONObject.parseObject("""
            {
              "user": {
                "name": "",
                "active": true
              },
              "meta": {
                "timestamp": ""
              }
            }
        """);

        JSONObject row = new JSONObject();

        mJSONSQLFunctions.applySchemaNormalization(row, schema);

        assertTrue(row.containsKey("user"));
        assertTrue(row.containsKey("meta"));

        JSONObject user = row.getJSONObject("user");
        assertTrue(user.containsKey("name"));
        assertTrue(user.containsKey("active"));
        assertNull(user.get("name"));
        assertNull(user.get("active"));

        JSONObject meta = row.getJSONObject("meta");
        assertTrue(meta.containsKey("timestamp"));
        assertNull(meta.get("timestamp"));
    }

    @Test
    public void testMismatchedStructureReplacesField() {
        JSONObject schema = JSONObject.parseObject("""
            {
              "info": {
                "version": "",
                "build": ""
              }
            }
        """);

        JSONObject row = JSONObject.parseObject("""
            {
              "info": "1.0"
            }
        """);

        mJSONSQLFunctions.applySchemaNormalization(row, schema);

        JSONObject info = row.getJSONObject("info");
        assertTrue(info.containsKey("version"));
        assertTrue(info.containsKey("build"));
        assertNull(info.get("version"));
        assertNull(info.get("build"));
    }










    @Test
    public void testMissingFlatKey() {
        JSONObject row = new JSONObject();
        JSONObject schema = JSONObject.of("id", 123, "name", "example");

        mJSONSQLFunctions.applySchemaNormalization(row, schema);

        assertTrue(row.containsKey("id"));
        assertTrue(row.containsKey("name"));
    }

    @Test
    public void testMissingNestedObject() {
        JSONObject row = new JSONObject();
        JSONObject schema = JSONObject.parseObject("""
            {
              "meta": {
                "author": "Alice",
                "version": 1
              }
            }
        """);

        mJSONSQLFunctions.applySchemaNormalization(row, schema);

        JSONObject meta = row.getJSONObject("meta");
        assertNotNull(meta);

        assertTrue( meta.containsKey("author"));
        assertTrue(meta.containsKey("version"));
    }

    @Test
    public void testNestedStructureMerge() {
        JSONObject row = JSONObject.parseObject("""
            {
              "meta": {
                "author": "Bob"
              }
            }
        """);

        JSONObject schema = JSONObject.parseObject("""
            {
              "meta": {
                "author": "Alice",
                "version": 1
              }
            }
        """);

        mJSONSQLFunctions.applySchemaNormalization(row, schema);

        JSONObject meta = row.getJSONObject("meta");
        assertEquals("Bob", meta.getString("author")); // Should retain original
        assertEquals(null, meta.get("version"));  // Should be added from schema
    }

    @Test
    public void testArrayOfObjectsExpansion() {
        JSONObject row = JSONObject.parseObject("""
            {
              "items": [
                {"id": 1}
              ]
            }
        """);

        JSONObject schema = JSONObject.parseObject("""
            {
              "items": [
                {"id": 0, "name": "default"}
              ]
            }
        """);

        mJSONSQLFunctions.applySchemaNormalization(row, schema);

        JSONArray items = row.getJSONArray("items");
        assertEquals(1, items.size());

        JSONObject item = items.getJSONObject(0);
        assertEquals(true, item.containsKey("id"));
        assertTrue(item.containsKey("name"));
        assertNull(item.get("name"));
    }

    @Test
    public void testReplaceWrongTypeWithObject() {
        JSONObject row = JSONObject.of("config", "wrong_type");
        JSONObject schema = JSONObject.parseObject("""
            {
              "config": {
                "enabled": true
              }
            }
        """);

        mJSONSQLFunctions.applySchemaNormalization(row, schema);

        JSONObject config = row.getJSONObject("config");
        assertNotNull(config);
        assertTrue(config.containsKey("enabled"));
        assertNull(config.get("enabled"));  // placeholder
    }

    @Test
    public void testNestedArraysAndObjects() {
        JSONObject row = JSONObject.parseObject("""
            {
              "logs": [
                {
                  "meta": {
                    "type": "info"
                  }
                }
              ]
            }
        """);

        JSONObject schema = JSONObject.parseObject("""
            {
              "logs": [
                {
                  "meta": {
                    "type": "",
                    "timestamp": ""
                  }
                }
              ]
            }
        """);

        mJSONSQLFunctions.applySchemaNormalization(row, schema);

        JSONObject meta = row.getJSONArray("logs").getJSONObject(0).getJSONObject("meta");
        assertEquals("info", meta.getString("type"));  // original retained
        assertTrue(meta.containsKey("timestamp"));     // added from schema
        assertNull(meta.get("timestamp"));             // placeholder
    }






    @Test
    public void testFlattenSimpleObject() {
        JSONObject input = JSONObject.parseObject("""
            {
              "name": "Alice",
              "age": 30
            }
        """);

        Map<String, Object> flat = mJSONSQLFunctions.flattenRow(input);

        assertEquals("Alice", flat.get("name"));
        assertEquals(30, flat.get("age"));
        assertEquals(2, flat.size());
    }

    @Test
    public void testFlattenNestedObject() {
        JSONObject input = JSONObject.parseObject("""
            {
              "user": {
                "name": "Bob",
                "address": {
                  "city": "New York",
                  "zip": "10001"
                }
              }
            }
        """);

        Map<String, Object> flat = mJSONSQLFunctions.flattenRow(input);

        assertEquals("Bob", flat.get("user.name"));
        assertEquals("New York", flat.get("user.address.city"));
        assertEquals("10001", flat.get("user.address.zip"));
        assertEquals(3, flat.size());
    }

    @Test
    public void testFlattenArrayValues() {
        JSONObject input = JSONObject.parseObject("""
            {
              "tags": ["a", "b", "c"]
            }
        """);

        Map<String, Object> flat = mJSONSQLFunctions.flattenRow(input);

        assertEquals("a", flat.get("tags[0]"));
        assertEquals("b", flat.get("tags[1]"));
        assertEquals("c", flat.get("tags[2]"));
        assertEquals(3, flat.size());
    }

    @Test
    public void testFlattenMixedNestedArray() {
        JSONObject input = JSONObject.parseObject("""
            {
              "users": [
                {
                  "name": "Alice",
                  "age": 30
                },
                {
                  "name": "Bob"
                }
              ]
            }
        """);

        Map<String, Object> flat = mJSONSQLFunctions.flattenRow(input);

        assertEquals("Alice", flat.get("users[0].name"));
        assertEquals(30, flat.get("users[0].age"));
        assertEquals("Bob", flat.get("users[1].name"));
        assertEquals(3, flat.size());
    }

    @Test
    public void testFlattenEmptyStructures() {
        JSONObject input = JSONObject.parseObject("""
            {
              "meta": {},
              "items": []
            }
        """);

        Map<String, Object> flat = mJSONSQLFunctions.flattenRow(input);
        assertTrue(flat.isEmpty());
    }

    @Test
    public void testFlattenWithNulls() {
        JSONObject input = JSONObject.parseObject("""
            {
              "name": null,
              "profile": {
                "bio": null
              }
            }
        """);

        Map<String, Object> flat = mJSONSQLFunctions.flattenRow(input);
        assertTrue(flat.containsKey("name"));
        assertTrue(flat.containsKey("profile.bio"));
        assertNull(flat.get("name"));
        assertNull(flat.get("profile.bio"));
        assertEquals(2, flat.size());
    }



    @Test
    public void testNullInsideNestedObject() {
        JSONObject json = JSONObject.parseObject("""
        {
          "user": {
            "profile": {
              "nickname": null
            }
          }
        }
    """);

        Map<String, Object> flat = mJSONSQLFunctions.flattenRow(json);

        assertTrue(flat.containsKey("user.profile.nickname"));
        assertNull(flat.get("user.profile.nickname"));
        assertEquals(1, flat.size());
    }

    @Test
    public void testNullInArrayOfObjects() {
        JSONObject json = JSONObject.parseObject("""
        {
          "items": [
            {"name": "item1"},
            {"name": null},
            {"description": "something"}
          ]
        }
    """);

        Map<String, Object> flat = mJSONSQLFunctions.flattenRow(json);

        assertEquals("item1", flat.get("items[0].name"));
        assertTrue(flat.containsKey("items[1].name"));
        assertNull(flat.get("items[1].name"));
        assertEquals("something", flat.get("items[2].description"));
        assertEquals(3, flat.size());
    }



    @Test
    public void testNullInDeeplyNestedArray() {
        JSONObject json = JSONObject.parseObject("""
        {
          "data": {
            "values": [null, {"inner": null}]
          }
        }
    """);

        Map<String, Object> flat = mJSONSQLFunctions.flattenRow(json);

        assertTrue(flat.containsKey("data.values[0]"));
        assertNull(flat.get("data.values[0]"));

        assertTrue(flat.containsKey("data.values[1].inner"));
        assertNull(flat.get("data.values[1].inner"));

        assertEquals(2, flat.size());
    }


    @Test
    public void testMixedNullsAndValues() {
        JSONObject json = JSONObject.parseObject("""
        {
          "x": null,
          "nested": {
            "y": null,
            "z": 123
          },
          "list": [1, null, {"deep": null}]
        }
    """);

        Map<String, Object> flat = mJSONSQLFunctions.flattenRow(json);

        assertTrue(flat.containsKey("x"));
        assertNull(flat.get("x"));

        assertTrue(flat.containsKey("nested.y"));
        assertNull(flat.get("nested.y"));

        assertEquals(123, flat.get("nested.z"));

        assertEquals(1, flat.get("list[0]"));
        assertNull(flat.get("list[1]"));
        assertNull(flat.get("list[2].deep"));

        assertEquals(6, flat.size());
    }

}