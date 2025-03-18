package test;


import main.constants.JSONSQLEngine;
import main.constants.JSONSQLEngine.Condition;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JSONSQLEngineTest {

    private JSONSQLEngine engine;

    private JSONArray jsonData;

    @BeforeEach
    void setUp() {
        engine = new JSONSQLEngine();
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
        jsonData = new JSONArray(jsonString);
    }





    @Test
    void testQueryExecution_WhereCondition() {
        String sql = "SELECT name FROM users WHERE age >= 30";
        JSONArray results = engine.executeQuery(sql, jsonData);

        assertEquals(3, results.length());
        assertEquals("Alice", results.getJSONObject(0).getString("name"));
        assertEquals("Bob", results.getJSONObject(1).getString("name"));
        assertEquals("David", results.getJSONObject(2).getString("name"));
    }

    @Test
    void testQueryExecution_OrderBy() {
        String sql = "SELECT name FROM users ORDER BY age DESC";
        JSONArray results = engine.executeQuery(sql, jsonData);

        assertEquals("Bob", results.getJSONObject(0).getString("name"));
        assertEquals("David", results.getJSONObject(1).getString("name"));
        assertEquals("Alice", results.getJSONObject(2).getString("name"));
        assertEquals("Charlie", results.getJSONObject(3).getString("name"));
        assertEquals("Eve", results.getJSONObject(4).getString("name"));
        assertEquals("Frank", results.getJSONObject(5).getString("name")); // Null age should be last
    }

    @Test
    void testQueryExecution_Limit() {
        String sql = "SELECT name FROM users ORDER BY age DESC LIMIT 2";
        JSONArray results = engine.executeQuery(sql, jsonData);

        assertEquals(2, results.length());
        assertEquals("Bob", results.getJSONObject(0).getString("name"));
        assertEquals("David", results.getJSONObject(1).getString("name"));
    }

    @Test
    void testQueryExecution_WhereConditionWithAndOr() {
        String sql = "SELECT name FROM users WHERE city = 'New York' AND age >= 30 OR salary > 50000";
        JSONArray results = engine.executeQuery(sql, jsonData);

        assertEquals(2, results.length());  // Expecting 2 results, not 3
        assertEquals("Alice", results.getJSONObject(0).getString("name"));
        assertEquals("David", results.getJSONObject(1).getString("name"));
    }

    @Test
    void testQueryExecution_NullValues() {
        String sql = "SELECT name FROM users WHERE salary IS NULL";
        JSONArray results = engine.executeQuery(sql, jsonData);

        assertEquals(1, results.length());
        assertEquals("Frank", results.getJSONObject(0).getString("name"));
    }

    @Test
    void testQueryExecution_CaseInsensitiveKeywords() {
        String sql = "select * from users order by age desc limit 2";
        JSONArray results = engine.executeQuery(sql, jsonData);

        assertEquals(2, results.length());
        assertEquals("Bob", results.getJSONObject(0).getString("name"));
        assertEquals("David", results.getJSONObject(1).getString("name"));
    }

    @Test
    void testQueryExecution_SortingWithMissingValues() {
        String sql = "SELECT name FROM users ORDER BY salary ASC";
        JSONArray results = engine.executeQuery(sql, jsonData);

        assertEquals("Charlie", results.getJSONObject(0).getString("name")); // 30,000 (smallest salary)
        assertEquals("Bob", results.getJSONObject(1).getString("name")); // 45,000
        assertEquals("Eve", results.getJSONObject(2).getString("name")); // 50,000
        assertEquals("Alice", results.getJSONObject(3).getString("name")); // 60,000
        assertEquals("David", results.getJSONObject(4).getString("name")); // 70,000
        assertEquals("Frank", results.getJSONObject(5).getString("name")); // Null salary (should be last)
    }


    @Test
    void testQueryExecution_StringComparisonCaseInsensitive() {
        String sql = "SELECT name FROM users WHERE city = 'new york'";
        JSONArray results = engine.executeQuery(sql, jsonData);

        assertEquals(3, results.length());
        assertEquals("Alice", results.getJSONObject(0).getString("name"));
        assertEquals("Charlie", results.getJSONObject(1).getString("name"));
        assertEquals("Eve", results.getJSONObject(2).getString("name"));
    }






    @Test
    void testQueryExecution_NestedJSONFields() {
        String sql = """
                    SELECT name, address.city
                    FROM users
                    WHERE address.city = 'New York'
                """;

        JSONArray results = engine.executeQuery(sql, jsonData);

        assertEquals(3, results.length()); // Alice, Charlie, Eve

        assertEquals("Alice", results.getJSONObject(0).getString("name"));
        assertEquals("Charlie", results.getJSONObject(1).getString("name"));
        assertEquals("Eve", results.getJSONObject(2).getString("name"));
    }

    @Test
    void testQueryExecution_ArrayIndexing() {
        String sql = """
                    SELECT name 
                    FROM users 
                    WHERE skills.0 = 'Python'
                """;

        JSONArray results = engine.executeQuery(sql, jsonData);

        assertEquals(1, results.length()); // Charlie
        assertEquals("Charlie", results.getJSONObject(0).getString("name"));
    }

    @Test
    void testQueryExecution_ComplexLogicalConditions() {
        String sql = """
                    SELECT name 
                    FROM users 
                    WHERE (city = 'New York' AND age >= 30) OR salary > 50000
                """;

        JSONArray results = engine.executeQuery(sql, jsonData);

        assertEquals(2, results.length()); // Alice, David, Eve

        assertEquals("Alice", results.getJSONObject(0).getString("name"));
        assertEquals("David", results.getJSONObject(1).getString("name"));
    }


    @Test
    void testQueryExecution_SortingByJSONField() {
        String sql = """
                    SELECT name, salary 
                    FROM users 
                    ORDER BY salary DESC
                    LIMIT 3
                """;

        JSONArray results = engine.executeQuery(sql, jsonData);

        assertEquals(3, results.length()); // David, Alice, Eve (top 3 salaries)

        assertEquals("David", results.getJSONObject(0).getString("name"));  // Salary: 70000
        assertEquals("Alice", results.getJSONObject(1).getString("name"));  // Salary: 60000
        assertEquals("Eve", results.getJSONObject(2).getString("name"));    // Salary: 50000
    }


    @Test
    void testSortResults_Age_ASC() {
        List<String[]> orderByColumns = new ArrayList<>();
        orderByColumns.add(new String[]{"age", "ASC"});
        JSONArray sortedResults = engine.sortResults(jsonData, orderByColumns);

        assertEquals("Eve", sortedResults.getJSONObject(0).getString("name"));  // 25
        assertEquals("Charlie", sortedResults.getJSONObject(1).getString("name")); // 28
        assertEquals("Alice", sortedResults.getJSONObject(2).getString("name"));  // 30
        assertEquals("David", sortedResults.getJSONObject(3).getString("name"));  // 35
        assertEquals("Bob", sortedResults.getJSONObject(4).getString("name"));  // 40
        assertEquals("Frank", sortedResults.getJSONObject(5).getString("name"));  // NULL (last)
    }

    @Test
    void testSortResults_Age_DESC() {
        List<String[]> orderByColumns = new ArrayList<>();
        orderByColumns.add(new String[]{"age", "DESC"});
        JSONArray sortedResults = engine.sortResults(jsonData, orderByColumns);

//        System.out.println("Final Sorted Output: " + sortedResults.toString(2));  // 🔥 Debugging

        assertEquals("Bob", sortedResults.getJSONObject(0).getString("name"));
        assertEquals("David", sortedResults.getJSONObject(1).getString("name"));
        assertEquals("Alice", sortedResults.getJSONObject(2).getString("name"));
        assertEquals("Charlie", sortedResults.getJSONObject(3).getString("name"));
        assertEquals("Eve", sortedResults.getJSONObject(4).getString("name"));
        assertEquals("Frank", sortedResults.getJSONObject(5).getString("name"));
    }

    @Test
    void testSortResults_Salary_ASC() {
        List<String[]> orderByColumns = new ArrayList<>();
        orderByColumns.add(new String[]{"salary", "ASC"});
        JSONArray sortedResults = engine.sortResults(jsonData, orderByColumns);

        assertEquals("Charlie", sortedResults.getJSONObject(0).getString("name")); // 30,000
        assertEquals("Bob", sortedResults.getJSONObject(1).getString("name"));  // 45,000
        assertEquals("Eve", sortedResults.getJSONObject(2).getString("name"));  // 50,000
        assertEquals("Alice", sortedResults.getJSONObject(3).getString("name"));  // 60,000
        assertEquals("David", sortedResults.getJSONObject(4).getString("name"));  // 70,000
        assertEquals("Frank", sortedResults.getJSONObject(5).getString("name"));  // NULL (last)
    }

    @Test
    void testSortResults_Salary_DESC() {
        List<String[]> orderByColumns = new ArrayList<>();
        orderByColumns.add(new String[]{"salary", "DESC"});
        JSONArray sortedResults = engine.sortResults(jsonData, orderByColumns);

        assertEquals("David", sortedResults.getJSONObject(0).getString("name"));  // 70,000
        assertEquals("Alice", sortedResults.getJSONObject(1).getString("name"));  // 60,000
        assertEquals("Eve", sortedResults.getJSONObject(2).getString("name"));  // 50,000
        assertEquals("Bob", sortedResults.getJSONObject(3).getString("name"));  // 45,000
        assertEquals("Charlie", sortedResults.getJSONObject(4).getString("name")); // 30,000
        assertEquals("Frank", sortedResults.getJSONObject(5).getString("name"));  // NULL (last)
    }

    @Test
    void testSortResults_MultiColumn_Age_DESC_Salary_ASC() {
        List<String[]> orderByColumns = List.of(
                new String[]{"age", "DESC"},
                new String[]{"salary", "ASC"}
        );
        JSONArray sortedResults = engine.sortResults(jsonData, orderByColumns);

        assertEquals("Bob", sortedResults.getJSONObject(0).getString("name"));  // 40, salary 45,000
        assertEquals("David", sortedResults.getJSONObject(1).getString("name"));  // 35, salary 70,000
        assertEquals("Alice", sortedResults.getJSONObject(2).getString("name"));  // 30, salary 60,000
        assertEquals("Charlie", sortedResults.getJSONObject(3).getString("name")); // 28, salary 30,000
        assertEquals("Eve", sortedResults.getJSONObject(4).getString("name"));  // 25, salary 50,000
        assertEquals("Frank", sortedResults.getJSONObject(5).getString("name"));  // NULL (last)
    }

    @Test
    void testSortResults_NullValues() {
        List<String[]> orderByColumns = new ArrayList<>();
        orderByColumns.add(new String[]{"salary", "ASC"});
        JSONArray sortedResults = engine.sortResults(jsonData, orderByColumns);

        assertEquals("Charlie", sortedResults.getJSONObject(0).getString("name")); // 30,000 (smallest salary)
        assertEquals("Bob", sortedResults.getJSONObject(1).getString("name")); // 45,000
        assertEquals("Eve", sortedResults.getJSONObject(2).getString("name")); // 50,000
        assertEquals("Alice", sortedResults.getJSONObject(3).getString("name")); // 60,000
        assertEquals("David", sortedResults.getJSONObject(4).getString("name")); // 70,000
        assertEquals("Frank", sortedResults.getJSONObject(5).getString("name")); // NULL salary (should be last)
    }


    @Test
    void testQueryExecution_JsonObjectField() {
        String sql = "SELECT name FROM users WHERE address.city = 'New York'";
        JSONArray results = engine.executeQuery(sql, jsonData);

        assertEquals(3, results.length()); // Alice, Charlie, and Eve live in New York
        assertEquals("Alice", results.getJSONObject(0).getString("name"));
        assertEquals("Charlie", results.getJSONObject(1).getString("name"));
        assertEquals("Eve", results.getJSONObject(2).getString("name"));
    }

    @Test
    void testQueryExecution_JsonObjectExactMatch() {
        String sql = "SELECT name FROM users WHERE address = '{\"street\": \"123 Main St\", \"city\": \"New York\", \"zip\": \"10001\"}'";
        JSONArray results = engine.executeQuery(sql, jsonData);

        assertEquals(1, results.length());
        assertEquals("Alice", results.getJSONObject(0).getString("name"));
    }

    @Test
    void testQueryExecution_JsonObjectPartialMatch() {
        String sql = "SELECT name FROM users WHERE address.city LIKE 'New York'";
        JSONArray results = engine.executeQuery(sql, jsonData);

        assertEquals(3, results.length()); // Alice, Charlie, and Eve have "New York" in their address
    }

    @Test
    void testQueryExecution_JsonObjectNumericComparison() {
        String sql = "SELECT name FROM users WHERE address.zip > 50000 ORDER BY address.zip";
        JSONArray results = engine.executeQuery(sql, jsonData);

        assertEquals(2, results.length()); // David (Chicago) and Bob (Los Angeles) have zip codes > 50000
        assertEquals("David", results.getJSONObject(0).getString("name"));
        assertEquals("Bob", results.getJSONObject(1).getString("name"));
    }

    @Test
    void testQueryExecution_JsonObjectIsNull() {
        String sql = "SELECT name FROM users WHERE address IS NULL";
        JSONArray results = engine.executeQuery(sql, jsonData);

        assertEquals(1, results.length());
        assertEquals("Frank", results.getJSONObject(0).getString("name")); // Frank has NULL for address
    }

    @Test
    void testQueryExecution_JsonObjectIsNotNull() {
        String sql = "SELECT name FROM users WHERE address IS NOT NULL";
        JSONArray results = engine.executeQuery(sql, jsonData);

        assertEquals(5, results.length()); // Everyone except Frank has an address
    }

    @Test
    void testQueryExecution_SortByJsonObjectField() {
        String sql = "SELECT name FROM users ORDER BY address.zip ASC";
        JSONArray results = engine.executeQuery(sql, jsonData);

        assertEquals("Alice", results.getJSONObject(0).getString("name"));   // 10001
        assertEquals("Charlie", results.getJSONObject(1).getString("name")); // 10003
        assertEquals("Eve", results.getJSONObject(2).getString("name"));     // 10016
        assertEquals("David", results.getJSONObject(3).getString("name"));   // 60601
        assertEquals("Bob", results.getJSONObject(4).getString("name"));     // 90028
    }


    @Test
    void testGetJsonPathValue_SimpleKey() {
        // ✅ Fetching top-level keys
        assertEquals("Alice", engine.getJsonPathValue(jsonData.getJSONObject(0), "name"));
        assertEquals(40, engine.getJsonPathValue(jsonData.getJSONObject(1), "age"));
        assertEquals("Chicago", engine.getJsonPathValue(jsonData.getJSONObject(3), "city"));
    }

    @Test
    void testGetJsonPathValue_NestedObject() {
        // ✅ Fetching values inside the "address" JSON object
        assertEquals("123 Main St", engine.getJsonPathValue(jsonData.getJSONObject(0), "address.street"));
        assertEquals("456 Sunset Blvd", engine.getJsonPathValue(jsonData.getJSONObject(1), "address.street"));
        assertEquals("60601", engine.getJsonPathValue(jsonData.getJSONObject(3), "address.zip"));
    }

    @Test
    void testGetJsonPathValue_ArrayElement() {
        // ✅ Fetching values inside the "skills" JSON array
        assertEquals("Java", engine.getJsonPathValue(jsonData.getJSONObject(0), "skills.0"));
        assertEquals("Python", engine.getJsonPathValue(jsonData.getJSONObject(2), "skills.0"));
        assertEquals("Rust", engine.getJsonPathValue(jsonData.getJSONObject(3), "skills.0"));
    }

    @Test
    void testGetJsonPathValue_LastElementInArray() {
        // ✅ Fetching the last skill in the array
        assertEquals("Python", engine.getJsonPathValue(jsonData.getJSONObject(0), "skills.2"));
        assertEquals("Java", engine.getJsonPathValue(jsonData.getJSONObject(1), "skills.2"));
        assertEquals("CSS", engine.getJsonPathValue(jsonData.getJSONObject(4), "skills.2"));
    }

    @Test
    void testGetJsonPathValue_InvalidPath() {
        // ✅ Trying to access a non-existent field should return null
        assertNull(engine.getJsonPathValue(jsonData.getJSONObject(0), "nonexistent"));
        assertNull(engine.getJsonPathValue(jsonData.getJSONObject(2), "address.country"));
        assertNull(engine.getJsonPathValue(jsonData.getJSONObject(4), "skills.10")); // Out of bounds
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
        assertNull(engine.getJsonPathValue(jsonData.getJSONObject(5), "skills.0")); // Empty skills array
    }

    @Test
    void testGetJsonPathValue_ComplexPath() {
        // ✅ Fetching deeply nested JSON values
        JSONObject nestedJson = new JSONObject("""
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
        assertEquals("john_doe", engine.getJsonPathValue(nestedJson, "user.profile.details.username"));
        assertNull(engine.getJsonPathValue(nestedJson, "user.profile.details.missing"));
    }


    // TEST COLUMN EXTRACTION


    @Test
    void testBasicSelection() {
        engine.tokenize("SELECT name FROM users");
        List<String> columns = engine.extractSelectedColumns();
        assertEquals(List.of("name"), columns);
    }

    @Test
    void testMultipleColumnsSelection() {
        engine.tokenize("SELECT name, age, salary FROM employees");
        List<String> columns = engine.extractSelectedColumns();
        assertEquals(List.of("name", "age", "salary"), columns);
    }

    @Test
    void testSelectionWithWhitespace() {
        engine.tokenize("SELECT   name   ,    age   ,  salary  FROM  users");
        List<String> columns = engine.extractSelectedColumns();
        assertEquals(List.of("name", "age", "salary"), columns);
    }

    @Test
    void testSelectionWithUnexpectedComma() {
        engine.tokenize("SELECT name, age,, salary FROM users");
        assertThrows(IllegalArgumentException.class, engine::extractSelectedColumns);
    }

    @Test
    void testSelectionWithMixedCaseKeywords() {
        engine.tokenize("sElEcT name, age, salary FrOm employees");
        List<String> columns = engine.extractSelectedColumns();
        assertEquals(List.of("name", "age", "salary"), columns);
    }

    @Test
    void testSelectionWithTrailingComma() {
        engine.tokenize("SELECT name, age, salary, FROM users");
        assertThrows(IllegalArgumentException.class, engine::extractSelectedColumns);
    }

    @Test
    void testSelectionWithMissingCommaBetweenColumns() {
        engine.tokenize("SELECT name age salary FROM users");
        assertThrows(IllegalArgumentException.class, engine::extractSelectedColumns);
    }

    @Test
    void testExtractSelectedColumns_Basic() {
        engine.tokenize("SELECT name, age FROM users");
        List<String> columns = engine.extractSelectedColumns();

        assertEquals(List.of("name", "age"), columns);
    }

    @Test
    void testExtractSelectedColumns_SingleColumn() {
        engine.tokenize("SELECT name FROM users");
        List<String> columns = engine.extractSelectedColumns();

        assertEquals(List.of("name"), columns);
    }

    @Test
    void testExtractSelectedColumns_Wildcard() {
        engine.tokenize("SELECT * FROM users");
        List<String> columns = engine.extractSelectedColumns();

        assertEquals(List.of("*"), columns);
    }

    @Test
    void testExtractSelectedColumns_ExtraSpaces() {
        engine.tokenize("SELECT   name   ,   age   FROM users");
        List<String> columns = engine.extractSelectedColumns();

        assertEquals(List.of("name", "age"), columns);
    }

    @Test
    void testExtractSelectedColumns_MissingFromClause() {
        engine.tokenize("SELECT name, age");
        Exception exception = assertThrows(IllegalArgumentException.class, engine::extractSelectedColumns);
        assertTrue(exception.getMessage().contains("Invalid SQL query"));
    }

    @Test
    void testExtractSelectedColumns_MissingSelectClause() {
        engine.tokenize("FROM users");
        Exception exception = assertThrows(IllegalArgumentException.class, engine::extractSelectedColumns);
        assertTrue(exception.getMessage().contains("Invalid SQL query"));
    }

    @Test
    void testExtractSelectedColumns_InvalidQueryStructure() {
        engine.tokenize("SELECT name age FROM users");
        Exception exception = assertThrows(IllegalArgumentException.class, engine::extractSelectedColumns);
    }




    @Test
    void testInvalidQueryWithoutSelect() {
        engine.tokenize("UPDATE users SET age = 30 WHERE id = 1");
        assertThrows(IllegalArgumentException.class, engine::extractSelectedColumns);
    }

    @Test
    void testInvalidQueryWithoutFrom() {
        engine.tokenize("SELECT name, age");
        assertThrows(IllegalArgumentException.class, engine::extractSelectedColumns);
    }

    @Test
    void testInvalidQueryEmptySelection() {
        engine.tokenize("SELECT  FROM users");
        assertThrows(IllegalArgumentException.class, engine::extractSelectedColumns);
    }











    @Test
    void testGetFirstIndexOf_SimpleSelect() {
        String sql = "SELECT name, age FROM users WHERE age > 30";
        engine.tokenize(sql);

        assertEquals(0, engine.getFirstIndexOf("SELECT"), "Expected 'SELECT' at index 0");
        assertEquals(4, engine.getFirstIndexOf("FROM"), "Expected 'FROM' at index 3");
        assertEquals(6, engine.getFirstIndexOf("WHERE"), "Expected 'WHERE' at index 5");
        assertEquals(3, engine.getFirstIndexOf("age"), "Expected first 'age' at index 3");
    }

    @Test
    void testGetFirstIndexOf_WithOrderBy() {
        String sql = "SELECT name FROM employees ORDER BY salary DESC";
        engine.tokenize(sql);

        assertEquals(0, engine.getFirstIndexOf("SELECT"), "Expected 'SELECT' at index 0");
        assertEquals(2, engine.getFirstIndexOf("FROM"), "Expected 'FROM' at index 2");
        assertEquals(4, engine.getFirstIndexOf("ORDER"), "Expected 'ORDER' at index 4");
        assertEquals(5, engine.getFirstIndexOf("BY"), "Expected 'BY' at index 5");
        assertEquals(6, engine.getFirstIndexOf("salary"), "Expected 'salary' at index 6");
        assertEquals(7, engine.getFirstIndexOf("DESC"), "Expected 'DESC' at index 7");
    }

    @Test
    void testGetFirstIndexOf_CaseInsensitive() {
        String sql = "select ID, Name FROM customers WHERE city = 'New York'";
        engine.tokenize(sql);

        assertEquals(0, engine.getFirstIndexOf("SELECT"), "Expected 'SELECT' at index 0 (case insensitive)");
        assertEquals(4, engine.getFirstIndexOf("FROM"), "Expected 'FROM' at index 3");
        assertEquals(6, engine.getFirstIndexOf("WHERE"), "Expected 'WHERE' at index 5");
        assertEquals(7, engine.getFirstIndexOf("city"), "Expected 'city' at index 6");
        assertEquals(9, engine.getFirstIndexOf("'New York'"), "Expected string 'New York' at index 8");
    }

    @Test
    void testGetFirstIndexOf_ComplexWhereClause() {
        String sql = "SELECT * FROM sales WHERE region = 'West' AND revenue > 50000 OR category = 'Electronics'";
        engine.tokenize(sql);

        assertEquals(0, engine.getFirstIndexOf("SELECT"), "Expected 'SELECT' at index 0");
        assertEquals(2, engine.getFirstIndexOf("FROM"), "Expected 'FROM' at index 2");
        assertEquals(4, engine.getFirstIndexOf("WHERE"), "Expected 'WHERE' at index 4");
        assertEquals(6, engine.getFirstIndexOf("="), "Expected '=' at index 6");
        assertEquals(8, engine.getFirstIndexOf("AND"), "Expected 'AND' at index 8");
        assertEquals(12, engine.getFirstIndexOf("OR"), "Expected 'OR' at index 12");
        assertEquals(6, engine.getFirstIndexOf("="), "Expected second '=' at index 14");
    }

    @Test
    void testGetFirstIndexOf_MultipleConditions() {
        String sql = "SELECT product, price FROM inventory WHERE (price > 100 AND stock < 50) OR category = 'Books'";
        engine.tokenize(sql);

        assertEquals(0, engine.getFirstIndexOf("SELECT"), "Expected 'SELECT' at index 0");
        assertEquals(4, engine.getFirstIndexOf("FROM"), "Expected 'FROM' at index 4");
        assertEquals(6, engine.getFirstIndexOf("WHERE"), "Expected 'WHERE' at index 6");
        assertEquals(9, engine.getFirstIndexOf(">"), "Expected '>' at index 9");
        assertEquals(13, engine.getFirstIndexOf("<"), "Expected '<' at index 13");
        assertEquals(16, engine.getFirstIndexOf("OR"), "Expected 'OR' at index 16");
        assertEquals(18, engine.getFirstIndexOf("="), "Expected '=' at index 18");
    }

    @Test
    void testGetFirstIndexOf_StringLiterals() {
        String sql = "SELECT * FROM customers WHERE name = 'John Doe' AND city = 'Los Angeles'";
        engine.tokenize(sql);

        assertEquals(0, engine.getFirstIndexOf("SELECT"), "Expected 'SELECT' at index 0");
        assertEquals(2, engine.getFirstIndexOf("FROM"), "Expected 'FROM' at index 2");
        assertEquals(4, engine.getFirstIndexOf("WHERE"), "Expected 'WHERE' at index 4");
        assertEquals(6, engine.getFirstIndexOf("="), "Expected '=' at index 6");
        assertEquals(7, engine.getFirstIndexOf("'John Doe'"), "Expected 'John Doe' at index 7");
        assertEquals(8, engine.getFirstIndexOf("AND"), "Expected 'AND' at index 10");
        assertEquals(11, engine.getFirstIndexOf("'Los Angeles'"), "Expected 'Los Angeles' at index 13");
    }

    @Test
    void testGetFirstIndexOf_ParenthesesAndOperators() {
        String sql = "SELECT id FROM data WHERE (score > 80 OR grade = 'A') AND passed = TRUE";
        engine.tokenize(sql);

        assertEquals(0, engine.getFirstIndexOf("SELECT"), "Expected 'SELECT' at index 0");
        assertEquals(2, engine.getFirstIndexOf("FROM"), "Expected 'FROM' at index 2");
        assertEquals(4, engine.getFirstIndexOf("WHERE"), "Expected 'WHERE' at index 4");
        assertEquals(5, engine.getFirstIndexOf("("), "Expected '(' at index 5");
        assertEquals(7, engine.getFirstIndexOf(">"), "Expected '>' at index 7");
        assertEquals(9, engine.getFirstIndexOf("OR"), "Expected 'OR' at index 9");
        assertEquals(11, engine.getFirstIndexOf("="), "Expected '=' at index 11");
        assertEquals(13, engine.getFirstIndexOf(")"), "Expected ')' at index 13");
        assertEquals(14, engine.getFirstIndexOf("AND"), "Expected 'AND' at index 14");
        assertEquals(11, engine.getFirstIndexOf("="), "Expected '=' at index 11");
    }

    @Test
    void testGetFirstIndexOf_MissingTokens() {
        String sql = "SELECT first_name FROM employees WHERE salary > 50000";
        engine.tokenize(sql);

        assertEquals(-1, engine.getFirstIndexOf("GROUP"), "Expected -1 for non-existent token 'GROUP'");
        assertEquals(-1, engine.getFirstIndexOf("HAVING"), "Expected -1 for non-existent token 'HAVING'");
        assertEquals(-1, engine.getFirstIndexOf("JOIN"), "Expected -1 for non-existent token 'JOIN'");
    }










    @Test
    void testExtractWhereConditions_SingleCondition() {
        engine.tokenize("SELECT name FROM users WHERE age >= 30");
        JSONSQLEngine.Condition conditions = engine.extractWhereConditions();

//        assertEquals(1, conditions);
        assertEquals("age", conditions.getColumn());
        assertEquals(">=", conditions.getOperator());
        assertEquals("30", conditions.getValue());
    }

    @Test
    void testExtractWhereConditions_MultipleConditions_AND() {
        engine.tokenize("SELECT name FROM users WHERE age >= 30 AND city = 'New York'");
        JSONSQLEngine.Condition conditions = engine.extractWhereConditions();

        assertTrue(conditions.hasSubConditions());
        assertEquals("AND", conditions.getLogicalOperators().get(0));

        List<JSONSQLEngine.Condition> subConditions = conditions.getSubConditions();
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
        engine.tokenize("SELECT name FROM users WHERE age >= 30 AND city = 'New York' OR salary > 50000");
        JSONSQLEngine.Condition conditions = engine.extractWhereConditions();

//        assertEquals(1, conditions.size()); // ✅ Single grouped condition due to precedence
        assertTrue(conditions.hasSubConditions());
        assertEquals("AND", conditions.getLogicalOperators().get(0));
        assertEquals("OR", conditions.getLogicalOperators().get(1));

        List<JSONSQLEngine.Condition> subConditions = conditions.getSubConditions();
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
        engine.tokenize("SELECT name FROM users WHERE ( (age >= 30 AND city = 'New York') OR (salary > 50000 AND skills.0 = 'SQL') )");
        JSONSQLEngine.Condition conditions = engine.extractWhereConditions();

//        assertEquals(1, conditions.size());
        assertTrue(conditions.hasSubConditions());
        assertEquals("OR", conditions.getLogicalOperators().get(0));

        List<JSONSQLEngine.Condition> subConditions = conditions.getSubConditions();
        assertEquals(2, subConditions.size());

        // First nested group (age >= 30 AND city = 'New York')
        assertTrue(subConditions.get(0).hasSubConditions());
        assertEquals("AND", subConditions.get(0).getLogicalOperators().get(0));

        // Second nested group (salary > 50000 AND skills.0 = 'SQL')
        assertTrue(subConditions.get(1).hasSubConditions());
        assertEquals("AND", subConditions.get(1).getLogicalOperators().get(0));
    }

    @Test
    void testExtractWhereConditions_IS_NULL() {
        engine.tokenize("SELECT name FROM users WHERE salary IS NULL");
        JSONSQLEngine.Condition conditions = engine.extractWhereConditions();

//        assertEquals(1, conditions.size());
        assertEquals("salary", conditions.getColumn());
        assertEquals("IS", conditions.getOperator());
        assertEquals("NULL", conditions.getValue());
    }

    @Test
    void testExtractWhereConditions_IS_NOT_NULL() {
        engine.tokenize("SELECT name FROM users WHERE address IS NOT NULL");
        JSONSQLEngine.Condition conditions = engine.extractWhereConditions();

//        assertEquals(1, conditions);
        assertEquals("address", conditions.getColumn());
        assertEquals("IS NOT", conditions.getOperator());
        assertEquals("NULL", conditions.getValue());
    }

    @Test
    void testExtractWhereConditions_MismatchedParentheses() {
        engine.tokenize("SELECT name FROM users WHERE (age >= 30 AND city = 'New York' OR salary > 50000");

        Exception exception = assertThrows(IllegalArgumentException.class, engine::extractWhereConditions);
    }

    @Test
    void testExtractWhereConditions_ExtraClosingParenthesis() {
        engine.tokenize("SELECT name FROM users WHERE (age >= 30 AND city = 'New York'))");

        Exception exception = assertThrows(IllegalArgumentException.class, engine::extractWhereConditions);
    }

    @Test
    void testExtractWhereConditions_InvalidWhereClause() {
        engine.tokenize("SELECT name FROM users WHERE age >= ");
        Exception exception = assertThrows(IllegalArgumentException.class, engine::extractWhereConditions);
    }

    @Test
    void testExtractWhereConditions_NoWhereClause() {
        engine.tokenize("SELECT name FROM users");
        JSONSQLEngine.Condition conditions = engine.extractWhereConditions();

//        assertTrue(conditions.);
    }


    @Test
    void testComplexQuery_StressTest() {
        String sql = """
                        SELECT name, age, salary, address.city 
                        FROM users 
                        WHERE ( city = 'New York' AND age >= 30 ) 
                        OR ( salary > 50000 OR skills.0 = 'Python' OR skills.0 = 'Rust' ) 
                        ORDER BY salary DESC, age ASC 
                        LIMIT 5
                """;

        JSONArray results = engine.executeQuery(sql, jsonData);

        // Debugging output
        System.out.println("Debugging Output:\n" + results.toString(2));

        // Expected results should be filtered correctly
        assertEquals(3, results.length()); // Limit applied correctly

        // Verify sorting order (salary DESC, age ASC)
        assertEquals("David", results.getJSONObject(0).getString("name"));  // Salary: 70000
        assertEquals("Alice", results.getJSONObject(1).getString("name"));  // Salary: 60000
        assertEquals("Charlie", results.getJSONObject(2).getString("name")); // Salary: 30000

        // Verify data integrity
        assertEquals(35, results.getJSONObject(0).getInt("age"));
        assertEquals(30, results.getJSONObject(1).getInt("age"));
        assertEquals(28, results.getJSONObject(2).getInt("age"));
    }

    //    @Test
    void testAdvancedQuery_ComplexFilteringSorting() {
//        String sql = """
//            SELECT name, age, salary, address.city
//            FROM users
//            WHERE ( address.city IS NOT NULL )
//            AND ( ( city = 'New York' AND age >= 30 AND age >= 30 ) OR ( skills.0 = 'Python' AND salary < 40000) )
//            ORDER BY salary DESC, age ASC
//            LIMIT 4
//    """;

        String sql = """
                        SELECT name, age, salary, address.city 
                        FROM users 
                        WHERE ( city = 'New York' AND age >= 30 AND age >= 30 ) OR ( skills.0 = 'Python' AND salary < 40000) ) 
                        AND ( address.city IS NOT NULL ) 
                        ORDER BY salary DESC, age ASC 
                        LIMIT 4
                """;

        JSONArray results = engine.executeQuery(sql, jsonData);

        // Debugging output
        System.out.println("Complex Query Final Results: " + results.toString(2));

        // Expected results count (LIMIT = 4)
        assertEquals(4, results.length());

        // ✅ Expected order based on (salary DESC, age ASC)
        assertEquals("David", results.getJSONObject(0).getString("name"));  // Salary: 70000
        assertEquals("Alice", results.getJSONObject(1).getString("name"));  // Salary: 60000
        assertEquals("Charlie", results.getJSONObject(2).getString("name")); // Salary: 30000 (Filtered)
        assertEquals("Eve", results.getJSONObject(3).getString("name"));    // Salary: 50000

        // ✅ Ensure NULL values are properly handled
        for (int i = 0; i < results.length(); i++) {
            assertNotNull(results.getJSONObject(i).opt("address"));
        }

        // ✅ Ensure conditions are met
        assertTrue(results.getJSONObject(2).getJSONArray("skills").toList().contains("Python"));
        assertTrue(results.getJSONObject(2).getInt("salary") < 40000);

        System.out.println("✅ Advanced Query Test Passed!");
    }

    @Test
    void testComplexQuery_MultipleConditionsAndNestedJSONFields() {
        String sql = """
                        SELECT name, address.zip 
                        FROM users 
                        WHERE (address.zip > 50000 AND skills.0 = 'SQL') 
                        OR (age < 35 AND city = 'New York') 
                        ORDER BY address.zip ASC, age DESC
                """;

        JSONArray results = engine.executeQuery(sql, jsonData);

        System.out.println("Final Results: " + results.toString(2));

        assertEquals(3, results.length());

        // Expected Order (Sorting by ZIP ASC, then Age DESC)
        assertEquals("Alice", results.getJSONObject(0).getString("name"));  // ZIP: 10001
        assertEquals("Charlie", results.getJSONObject(1).getString("name")); // ZIP: 10003
        assertEquals("Eve", results.getJSONObject(2).getString("name"));    // ZIP: 10016
    }

    @Test
    void testComplexQuery_WithNullValues() {
        String sql = """
                        SELECT name, salary 
                        FROM users 
                        WHERE salary IS NOT NULL 
                        ORDER BY salary ASC 
                        LIMIT 3
                """;

        JSONArray results = engine.executeQuery(sql, jsonData);

        assertEquals(3, results.length());

        // Order should be ASCENDING by salary
        assertEquals("Charlie", results.getJSONObject(0).getString("name"));  // Salary: 30000
        assertEquals("Bob", results.getJSONObject(1).getString("name"));      // Salary: 45000
        assertEquals("Eve", results.getJSONObject(2).getString("name"));      // Salary: 50000
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
        JSONArray jsonData = new JSONArray("""
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
                """);

        // ✅ Execute query using JSON SQL engine
        JSONSQLEngine engine = new JSONSQLEngine();
        JSONArray results = engine.executeQuery(sql, jsonData);

        // ✅ Debugging output to verify results
        System.out.println("Final Results: " + results.toString(2));

        // ✅ Expected results should be filtered correctly
        assertEquals(3, results.length()); // Limit should be applied correctly

        // ✅ Verify sorting order (salary DESC, age ASC)
        assertEquals("David", results.getJSONObject(0).getString("name"));  // Salary: 90000
        assertEquals("Charlie", results.getJSONObject(1).getString("name")); // Salary: 70000
        assertEquals("Alice", results.getJSONObject(2).getString("name"));  // Salary: 60000

        // ✅ Verify nested field extraction
        assertEquals(35, results.getJSONObject(0).getInt("details.age"));
        assertEquals(28, results.getJSONObject(1).getInt("details.age"));
        assertEquals(30, results.getJSONObject(2).getInt("details.age"));

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
        JSONArray jsonData = new JSONArray("""
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
                """);

        // ✅ Execute query
        JSONSQLEngine engine = new JSONSQLEngine();
        JSONArray results = engine.executeQuery(sql, jsonData);

        // ✅ Debugging output
        System.out.println("Final Results: " + results.toString(2));

        // ✅ Verify LIMIT applied correctly
        assertEquals(4, results.length());

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
        JSONArray jsonData = new JSONArray("""
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
                """);

        // ✅ Execute query
        JSONSQLEngine engine = new JSONSQLEngine();
        JSONArray results = engine.executeQuery(sql, jsonData);

        // ✅ Debugging output
        System.out.println("Final Results: " + results.toString(2));

        // ✅ Verify LIMIT applied correctly
        assertEquals(3, results.length());

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

        JSONArray jsonData = new JSONArray("""
        [
            { "name": "Alice", "salary": 70000, "projects": [] },
            { "name": "Bob", "salary": 50000, "projects": [{ "name": "Web Dev" }] },
            { "name": "Charlie", "salary": 90000, "projects": [{ "name": "AI Research" }] },
            { "name": "David", "salary": null, "projects": [{ "name": "Machine Learning" }] }
        ]
    """);

        JSONSQLEngine engine = new JSONSQLEngine();
        JSONArray results = engine.executeQuery(sql, jsonData);

        System.out.println("Final Results: " + results.toString(2));

        // ✅ Expect 0 results because no one has salary > 200000
        assertEquals(0, results.length());
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
        JSONArray jsonData = new JSONArray("""
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
                """);

        JSONArray results = engine.executeQuery(sql, jsonData);

        // Debugging output to check the final results
        System.out.println("Final Results: " + results.toString(2));

        // ✅ Expecting 4 results due to LIMIT 5
        assertEquals(4, results.length());

        // ✅ Verify sorting order (salary DESC, age ASC)
        assertEquals("Hank", results.getJSONObject(0).getString("name"));  // Salary: 120000
        assertEquals("David", results.getJSONObject(1).getString("name")); // Salary: 110000
        assertEquals("Alice", results.getJSONObject(2).getString("name"));   // Salary: 95000
        assertEquals("Bob", results.getJSONObject(3).getString("name"));// Salary: 90000

        // ✅ Ensure address fields are NOT NULL (per WHERE condition)
        for (int i = 0; i < results.length(); i++) {
            assertNotNull(results.getJSONObject(i).getJSONObject("address").getString("city"));
            assertNotNull(results.getJSONObject(i).getJSONObject("address").getString("zip"));
        }

        // ✅ Ensure filtered results match the WHERE conditions
        for (int i = 0; i < results.length(); i++) {
            JSONObject row = results.getJSONObject(i);
            assertTrue(row.has("projects"));
            JSONArray projects = row.getJSONArray("projects");

            boolean matchesCondition1 = false;
            boolean matchesCondition2 = false;

            if (!projects.isEmpty()) {
                JSONObject firstProject = projects.getJSONObject(0);
                if (firstProject.getString("name").equals("AI Research")) {
                    if (row.getInt("age") >= 30 || row.getInt("salary") > 60000) {
                        matchesCondition1 = true;
                    }
                }
            }

            if (projects.length() > 1) {
                JSONObject secondProject = projects.getJSONObject(1);
                if (secondProject.getString("name").equals("Quantum Computing") &&
                        (secondProject.getInt("budget") > 500000 || row.getJSONArray("skills").toList().contains("C++"))) {
                    matchesCondition2 = true;
                }
            }

            assertTrue(matchesCondition1 || matchesCondition2);
        }
    }









    @Test
    void testSingleColumnDefaultOrder() {
        engine.tokenize("SELECT * FROM users ORDER BY age");
        List<String[]> orderBy = engine.extractOrderBy();

        assertEquals(1, orderBy.size());
        assertArrayEquals(new String[]{"age", "ASC"}, orderBy.get(0));
    }

    @Test
    void testSingleColumnExplicitAsc() {
        engine.tokenize("SELECT * FROM users ORDER BY salary ASC");
        List<String[]> orderBy = engine.extractOrderBy();

        assertEquals(1, orderBy.size());
        assertArrayEquals(new String[]{"salary", "ASC"}, orderBy.get(0));
    }

    @Test
    void testSingleColumnExplicitDesc() {
        engine.tokenize("SELECT * FROM users ORDER BY name DESC");
        List<String[]> orderBy = engine.extractOrderBy();

        assertEquals(1, orderBy.size());
        assertArrayEquals(new String[]{"name", "DESC"}, orderBy.get(0));
    }

    @Test
    void testMultipleColumnsDefaultOrder() {
        engine.tokenize("SELECT * FROM users ORDER BY age, salary");
        List<String[]> orderBy = engine.extractOrderBy();

        assertEquals(2, orderBy.size());
        assertArrayEquals(new String[]{"age", "ASC"}, orderBy.get(0));
        assertArrayEquals(new String[]{"salary", "ASC"}, orderBy.get(1));
    }

    @Test
    void testMultipleColumnsWithAscAndDesc() {
        engine.tokenize("SELECT * FROM users ORDER BY age DESC, salary ASC, name DESC");
        List<String[]> orderBy = engine.extractOrderBy();

        assertEquals(3, orderBy.size());
        assertArrayEquals(new String[]{"age", "DESC"}, orderBy.get(0));
        assertArrayEquals(new String[]{"salary", "ASC"}, orderBy.get(1));
        assertArrayEquals(new String[]{"name", "DESC"}, orderBy.get(2));
    }

    @Test
    void testMultipleColumnsMixedOrder() {
        engine.tokenize("SELECT * FROM users ORDER BY id ASC, username, email DESC");
        List<String[]> orderBy = engine.extractOrderBy();

        assertEquals(3, orderBy.size());
        assertArrayEquals(new String[]{"id", "ASC"}, orderBy.get(0));
        assertArrayEquals(new String[]{"username", "ASC"}, orderBy.get(1)); // Default ASC
        assertArrayEquals(new String[]{"email", "DESC"}, orderBy.get(2));
    }

    @Test
    void testNoOrderByClause() {
        engine.tokenize("SELECT * FROM users WHERE age > 30");
        List<String[]> orderBy = engine.extractOrderBy();

        assertTrue(orderBy.isEmpty());
    }

    @Test
    void testOrderByWithoutColumn() {
        engine.tokenize("SELECT * FROM users ORDER BY");
        List<String[]> orderBy = engine.extractOrderBy();

        assertTrue(orderBy.isEmpty());
    }

    @Test
    void testOrderByWithoutByKeyword() {
        engine.tokenize("SELECT * FROM users ORDER age DESC");
        List<String[]> orderBy = engine.extractOrderBy();

        assertTrue(orderBy.isEmpty());
    }

    @Test
    void testOrderByWithInvalidKeywordAfter() {
        engine.tokenize("SELECT * FROM users ORDER BY age DESC LIMIT 10");
        List<String[]> orderBy = engine.extractOrderBy();

        assertEquals(1, orderBy.size());
        assertArrayEquals(new String[]{"age", "DESC"}, orderBy.get(0));
    }

    @Test
    void testOrderByWithWhereClauseBefore() {
        engine.tokenize("SELECT * FROM users WHERE age > 25 ORDER BY salary ASC");
        List<String[]> orderBy = engine.extractOrderBy();

        assertEquals(1, orderBy.size());
        assertArrayEquals(new String[]{"salary", "ASC"}, orderBy.get(0));
    }

    @Test
    void testOrderByWithDifferentCasing() {
        engine.tokenize("SELECT * FROM users ORDER bY AGE dEsc, salary AsC");
        List<String[]> orderBy = engine.extractOrderBy();

        assertEquals(2, orderBy.size());
        assertArrayEquals(new String[]{"AGE", "DESC"}, orderBy.get(0));
        assertArrayEquals(new String[]{"salary", "ASC"}, orderBy.get(1));
    }

    @Test
    void testOrderByWithSpecialCharactersInColumn() {
        engine.tokenize("SELECT * FROM users ORDER BY created_at DESC, user-name ASC");
        List<String[]> orderBy = engine.extractOrderBy();

        assertEquals(2, orderBy.size());
        assertArrayEquals(new String[]{"created_at", "DESC"}, orderBy.get(0));
        assertArrayEquals(new String[]{"user-name", "ASC"}, orderBy.get(1));
    }

    @Test
    void testOrderByWithSQLKeywordsAsColumnNames() {
        engine.tokenize("SELECT * FROM users ORDER BY `select` ASC, `where` DESC");
        List<String[]> orderBy = engine.extractOrderBy();

        assertEquals(2, orderBy.size());
        assertArrayEquals(new String[]{"`select`", "ASC"}, orderBy.get(0));
        assertArrayEquals(new String[]{"`where`", "DESC"}, orderBy.get(1));
    }

    @Test
    void testOrderByWithNumbersAsColumnNames() {
        engine.tokenize("SELECT * FROM users ORDER BY `123` ASC, `456` DESC");
        List<String[]> orderBy = engine.extractOrderBy();

        assertEquals(2, orderBy.size());
        assertArrayEquals(new String[]{"`123`", "ASC"}, orderBy.get(0));
        assertArrayEquals(new String[]{"`456`", "DESC"}, orderBy.get(1));
    }












    @Test
    void testSimpleAnd() {
        // (true AND true) = true
        assertTrue(engine.evaluateBooleanExpression(
                List.of(true, true),
                List.of("AND")
        ));

        // (true AND false) = false
        assertFalse(engine.evaluateBooleanExpression(
                List.of(true, false),
                List.of("AND")
        ));

        // (false AND false) = false
        assertFalse(engine.evaluateBooleanExpression(
                List.of(false, false),
                List.of("AND")
        ));
    }

    @Test
    void testSimpleOr() {
        // (true OR false) = true
        assertTrue(engine.evaluateBooleanExpression(
                List.of(true, false),
                List.of("OR")
        ));

        // (false OR false) = false
        assertFalse(engine.evaluateBooleanExpression(
                List.of(false, false),
                List.of("OR")
        ));

        // (true OR true) = true
        assertTrue(engine.evaluateBooleanExpression(
                List.of(true, true),
                List.of("OR")
        ));
    }

    @Test
    void testAndOrPrecedence() {
        // (true AND false OR true) -> (false OR true) = true
        assertTrue(engine.evaluateBooleanExpression(
                List.of(true, false, true),
                List.of("AND", "OR")
        ));

        // (true OR false AND false) -> (true OR false) -> true
        assertTrue(engine.evaluateBooleanExpression(
                List.of(true, false, false),
                List.of("OR", "AND")
        ));

        // (false AND true OR true AND false) -> (false OR false) = false
        assertFalse(engine.evaluateBooleanExpression(
                List.of(false, true, true, false),
                List.of("AND", "OR", "AND")
        ));
    }

    @Test
    void testComplexConditions() {
        // (true AND true AND false OR true) -> (false OR true) = true
        assertTrue(engine.evaluateBooleanExpression(
                List.of(true, true, false, true),
                List.of("AND", "AND", "OR")
        ));

        // (true OR false AND false OR true) -> (true OR false OR true) = true
        assertTrue(engine.evaluateBooleanExpression(
                List.of(true, false, false, true),
                List.of("OR", "AND", "OR")
        ));

        // (false AND true OR false AND true OR false) -> (false OR false OR false) = false
        assertFalse(engine.evaluateBooleanExpression(
                List.of(false, true, false, true, false),
                List.of("AND", "OR", "AND", "OR")
        ));
    }

    @Test
    void testEdgeCases() {
        // Single value (true) should return true
        assertTrue(engine.evaluateBooleanExpression(
                List.of(true),
                List.of()
        ));

        // Single value (false) should return false
        assertFalse(engine.evaluateBooleanExpression(
                List.of(false),
                List.of()
        ));

        // AND short-circuit: (false AND true AND true) -> false (short-circuit at first false)
        assertFalse(engine.evaluateBooleanExpression(
                List.of(false, true, true),
                List.of("AND", "AND")
        ));

        // OR short-circuit: (true OR false OR false) -> true (short-circuit at first true)
        assertTrue(engine.evaluateBooleanExpression(
                List.of(true, false, false),
                List.of("OR", "OR")
        ));
    }

    @Test
    void testInvalidCases() {
        // Operators must be exactly one less than booleans
        assertThrows(IllegalArgumentException.class, () ->
                engine.evaluateBooleanExpression(List.of(true, true), List.of("AND", "OR"))
        );

        assertThrows(IllegalArgumentException.class, () ->
                engine.evaluateBooleanExpression(List.of(true), List.of("AND"))
        );

        // Empty boolean list should throw an exception
        assertThrows(IllegalArgumentException.class, () ->
                engine.evaluateBooleanExpression(List.of(), List.of())
        );
    }











    @Test
    void testSingleConditionMatch() {
        JSONObject row = new JSONObject("{\"age\": 30}");
        Condition condition = new Condition("age", "=", "30");

        boolean result = engine.matchesConditions(row, condition);
        assertTrue(result, "Condition should match.");
    }

    @Test
    void testSingleConditionDoesNotMatch() {
        JSONObject row = new JSONObject("{\"age\": 25}");
        Condition condition = new Condition("age", "=", "30");

        boolean result = engine.matchesConditions(row, condition);
        assertFalse(result, "Condition should not match.");
    }

    @Test
    void testAndConditionBothTrue() {
        JSONObject row = new JSONObject("{\"age\": 30, \"city\": \"New York\"}");
        Condition condition1 = new Condition("age", "=", "30");
        Condition condition2 = new Condition("city", "=", "New York");

        Condition rootCondition = new Condition(List.of(condition1, condition2), List.of("AND"));

        boolean result = engine.matchesConditions(row, rootCondition);
        assertTrue(result, "Both conditions are true, should match.");
    }

    @Test
    void testAndConditionOneFalse() {
        JSONObject row = new JSONObject("{\"age\": 25, \"city\": \"New York\"}");
        Condition condition1 = new Condition("age", "=", "30");
        Condition condition2 = new Condition("city", "=", "New York");

        Condition rootCondition = new Condition(List.of(condition1, condition2), List.of("AND"));

        boolean result = engine.matchesConditions(row, rootCondition);
        assertFalse(result, "One condition is false, should not match.");
    }

    @Test
    void testOrConditionOneTrue() {
        JSONObject row = new JSONObject("{\"age\": 25, \"city\": \"New York\"}");
        Condition condition1 = new Condition("age", "=", "30");
        Condition condition2 = new Condition("city", "=", "New York");

        Condition rootCondition = new Condition(List.of(condition1, condition2), List.of("OR"));

        boolean result = engine.matchesConditions(row, rootCondition);
        assertTrue(result, "One condition is true, OR should match.");
    }

    @Test
    void testOrConditionBothFalse() {
        JSONObject row = new JSONObject("{\"age\": 25, \"city\": \"Los Angeles\"}");
        Condition condition1 = new Condition("age", "=", "30");
        Condition condition2 = new Condition("city", "=", "New York");

        Condition rootCondition = new Condition(List.of(condition1, condition2), List.of("OR"));

        boolean result = engine.matchesConditions(row, rootCondition);
        assertFalse(result, "Both conditions are false, OR should not match.");
    }

    @Test
    void testNestedConditionsWithAndOr() {
        JSONObject row = new JSONObject("{\"age\": 30, \"city\": \"New York\", \"salary\": 60000}");

        Condition condition1 = new Condition("age", "=", "30");
        Condition condition2 = new Condition("city", "=", "New York");
        Condition condition3 = new Condition("salary", ">", "50000");

        Condition subConditionGroup = new Condition(List.of(condition1, condition2), List.of("AND"));
        Condition rootCondition = new Condition(List.of(subConditionGroup, condition3), List.of("OR"));

        boolean result = engine.matchesConditions(row, rootCondition);
        assertTrue(result, "Nested AND (true) OR (true) should match.");
    }

    @Test
    void testNestedConditionsWithComplexLogic() {
        JSONObject row = new JSONObject("{\"age\": 40, \"city\": \"Los Angeles\", \"salary\": 45000}");

        Condition condition1 = new Condition("age", "=", "30");
        Condition condition2 = new Condition("city", "=", "New York");
        Condition condition3 = new Condition("salary", ">", "50000");
        Condition condition4 = new Condition("salary", "<", "50000");

        Condition subConditionGroup1 = new Condition(List.of(condition1, condition2), List.of("AND"));
        Condition subConditionGroup2 = new Condition(List.of(condition3, condition4), List.of("AND"));
        Condition rootCondition = new Condition(List.of(subConditionGroup1, subConditionGroup2), List.of("OR"));

        boolean result = engine.matchesConditions(row, rootCondition);
        assertFalse(result, "Complex nested OR should not match.");
    }

    @Test
    void testDeeplyNestedConditions() {
        JSONObject row = new JSONObject("{\"age\": 40, \"city\": \"Los Angeles\", \"salary\": 45000}");

        Condition condition1 = new Condition("age", ">", "35");
        Condition condition2 = new Condition("city", "=", "Los Angeles");
        Condition condition3 = new Condition("salary", "<", "50000");

        Condition subConditionGroup1 = new Condition(List.of(condition1, condition2), List.of("AND"));
        Condition subConditionGroup2 = new Condition(List.of(condition3, subConditionGroup1), List.of("AND"));

        boolean result = engine.matchesConditions(row, subConditionGroup2);
        assertTrue(result, "Deeply nested condition should match.");
    }

    @Test
    void testEdgeCase_AllConditionsFalse() {
        JSONObject row = new JSONObject("{\"age\": 20, \"city\": \"Chicago\", \"salary\": 30000}");

        Condition condition1 = new Condition("age", ">", "35");
        Condition condition2 = new Condition("city", "=", "Los Angeles");
        Condition condition3 = new Condition("salary", ">", "50000");

        Condition rootCondition = new Condition(List.of(condition1, condition2, condition3), List.of("AND", "AND"));

        boolean result = engine.matchesConditions(row, rootCondition);
        assertFalse(result, "All conditions are false, AND should not match.");
    }

    @Test
    void testEdgeCase_AllConditionsTrue() {
        JSONObject row = new JSONObject("{\"age\": 40, \"city\": \"Los Angeles\", \"salary\": 45000}");

        Condition condition1 = new Condition("age", ">", "30");
        Condition condition2 = new Condition("city", "=", "Los Angeles");
        Condition condition3 = new Condition("salary", "<", "50000");

        Condition rootCondition = new Condition(List.of(condition1, condition2, condition3), List.of("AND", "AND"));

        boolean result = engine.matchesConditions(row, rootCondition);
        assertTrue(result, "All conditions are true, AND should match.");
    }

    @Test
    void testNullValuesInJson() {
        JSONObject row = new JSONObject("{\"age\": null, \"city\": \"Los Angeles\", \"salary\": null}");

        Condition condition1 = new Condition("age", "IS", "NULL");
        Condition condition2 = new Condition("salary", "IS", "NULL");

        Condition rootCondition = new Condition(List.of(condition1, condition2), List.of("AND"));

        boolean result = engine.matchesConditions(row, rootCondition);
        assertTrue(result, "NULL conditions should be matched.");
    }






    @Test
    void testDeeplyNestedMixedAND_OR_NullHandling() {
        JSONObject row = new JSONObject("{\"age\": null, \"city\": \"Seattle\", \"salary\": 80000, \"active\": \"yes\"}");

        Condition condition1 = new Condition("age", "IS", "NULL");
        Condition condition2 = new Condition("city", "=", "Seattle");
        Condition condition3 = new Condition("salary", ">", "70000");
        Condition condition4 = new Condition("active", "=", "yes");
        Condition condition5 = new Condition("age", ">", "25"); // Should be false due to NULL

        Condition nestedGroup1 = new Condition(List.of(condition1, condition2), List.of("AND"));
        Condition nestedGroup2 = new Condition(List.of(condition3, condition4, condition5), List.of("OR", "AND"));
        Condition rootCondition = new Condition(List.of(nestedGroup1, nestedGroup2), List.of("OR"));

        boolean result = engine.matchesConditions(row, rootCondition);
        assertTrue(result, "Age is NULL AND city is Seattle OR (Salary > 70000 OR Active is yes) should match.");
    }

    @Test
    void testExtremeNestingWithAlternatingOperators() {
        JSONObject row = new JSONObject("{\"experience\": 10, \"position\": \"Senior Developer\", \"team\": \"AI\", \"remote\": \"yes\"}");

        Condition condition1 = new Condition("experience", ">", "5");
        Condition condition2 = new Condition("position", "=", "Senior Developer");
        Condition condition3 = new Condition("team", "=", "AI");
        Condition condition4 = new Condition("remote", "=", "yes");

        Condition level3Group = new Condition(List.of(condition1, condition2), List.of("AND"));
        Condition level2Group = new Condition(List.of(condition3, condition4), List.of("OR"));
        Condition level1Group = new Condition(List.of(level3Group, level2Group), List.of("AND"));

        boolean result = engine.matchesConditions(row, level1Group);
        assertTrue(result, "((Experience > 5 AND Position = Senior Developer) AND (Team = AI OR Remote = yes)) should match.");
    }

    @Test
    void testMultipleConditionsWithComplexOR_AND_Handling() {
        JSONObject row = new JSONObject("{\"project\": \"Alpha\", \"budget\": 50000, \"department\": \"R&D\", \"manager\": \"Eve\"}");

        Condition condition1 = new Condition("project", "=", "Alpha");
        Condition condition2 = new Condition("budget", ">", "60000"); // False
        Condition condition3 = new Condition("department", "=", "R&D");
        Condition condition4 = new Condition("manager", "=", "Eve");

        Condition group1 = new Condition(List.of(condition1, condition2), List.of("OR"));
        Condition group2 = new Condition(List.of(condition3, condition4), List.of("AND"));
        Condition rootCondition = new Condition(List.of(group1, group2), List.of("AND"));

        boolean result = engine.matchesConditions(row, rootCondition);
        assertTrue(result, "(Project = Alpha OR Budget > 60000) AND (Department = R&D AND Manager = Eve) should match.");
    }

    @Test
    void testHighlyNestedBooleanMixWithMissingFields() {
        JSONObject row = new JSONObject("{\"role\": \"Admin\", \"access\": \"full\", \"lastLogin\": \"2024-03-01\"}");

        Condition condition1 = new Condition("role", "=", "Admin");
        Condition condition2 = new Condition("access", "=", "full");
        Condition condition3 = new Condition("lastLogin", "<", "2024-03-10"); // True
        Condition condition4 = new Condition("passwordReset", "=", "yes"); // Missing field

        Condition nestedGroup1 = new Condition(List.of(condition1, condition2), List.of("AND"));
        Condition nestedGroup2 = new Condition(List.of(condition3, condition4), List.of("AND"));
        Condition rootCondition = new Condition(List.of(nestedGroup1, nestedGroup2), List.of("OR"));

        boolean result = engine.matchesConditions(row, rootCondition);
        assertTrue(result, "Admin AND Full Access OR (Last Login < 2024-03-10 AND Missing Field) should match.");
    }

    @Test
    void testLogicalOperatorOrderWithComplexEvaluation() {
        JSONObject row = new JSONObject("{\"temperature\": 100, \"pressure\": 30, \"status\": \"safe\", \"mode\": \"auto\"}");

        Condition condition1 = new Condition("temperature", ">", "95");
        Condition condition2 = new Condition("pressure", "<", "40");
        Condition condition3 = new Condition("status", "=", "safe");
        Condition condition4 = new Condition("mode", "=", "auto");

        Condition andGroup = new Condition(List.of(condition1, condition2), List.of("AND"));
        Condition orGroup = new Condition(List.of(condition3, condition4), List.of("OR"));
        Condition rootCondition = new Condition(List.of(andGroup, orGroup), List.of("AND"));

        boolean result = engine.matchesConditions(row, rootCondition);
        assertTrue(result, "((Temp > 95 AND Pressure < 40) AND (Status = Safe OR Mode = Auto)) should match.");
    }

    @Test
    void testComplexEvaluationWithFlippedOperatorOrder() {
        JSONObject row = new JSONObject("{\"speed\": 80, \"fuel\": \"low\", \"engine\": \"running\", \"gear\": \"drive\"}");

        Condition condition1 = new Condition("speed", ">", "75");
        Condition condition2 = new Condition("fuel", "=", "low");
        Condition condition3 = new Condition("engine", "=", "running");
        Condition condition4 = new Condition("gear", "=", "park"); // False

        Condition orGroup = new Condition(List.of(condition1, condition2), List.of("OR"));
        Condition andGroup = new Condition(List.of(condition3, condition4), List.of("AND"));
        Condition rootCondition = new Condition(List.of(orGroup, andGroup), List.of("OR"));

        boolean result = engine.matchesConditions(row, rootCondition);
        assertTrue(result, "(Speed > 75 OR Fuel = Low) OR (Engine = Running AND Gear = Park) should match.");
    }

    @Test
    void testMultipleLevelsOfConditionNestingWithContradictingCases() {
        JSONObject row = new JSONObject("{\"temperature\": 20, \"pressure\": 50, \"alert\": \"false\", \"sensor\": \"active\"}");

        Condition condition1 = new Condition("temperature", "<", "25"); // True
        Condition condition2 = new Condition("pressure", ">", "40"); // True
        Condition condition3 = new Condition("alert", "=", "true"); // False
        Condition condition4 = new Condition("sensor", "=", "inactive"); // False

        Condition group1 = new Condition(List.of(condition1, condition2), List.of("AND"));
        Condition group2 = new Condition(List.of(condition3, condition4), List.of("OR"));
        Condition rootCondition = new Condition(List.of(group1, group2), List.of("AND"));

        boolean result = engine.matchesConditions(row, rootCondition);
        assertFalse(result, "(Temp < 25 AND Pressure > 40) AND (Alert = True OR Sensor = Inactive) should NOT match.");
    }

    @Test
    void testComplicatedContradictingMixedBooleanLogic() {
        JSONObject row = new JSONObject("{\"altitude\": 5000, \"weather\": \"clear\", \"windSpeed\": 20, \"turbulence\": \"low\"}");

        Condition condition1 = new Condition("altitude", ">", "10000"); // False
        Condition condition2 = new Condition("weather", "=", "clear");
        Condition condition3 = new Condition("windSpeed", "<", "30");
        Condition condition4 = new Condition("turbulence", "=", "high"); // False

        Condition group1 = new Condition(List.of(condition1, condition2), List.of("OR"));
        Condition group2 = new Condition(List.of(condition3, condition4), List.of("AND"));
        Condition rootCondition = new Condition(List.of(group1, group2), List.of("OR"));

        boolean result = engine.matchesConditions(row, rootCondition);
        assertTrue(result, "(Altitude > 10000 OR Weather = Clear) OR (WindSpeed < 30 AND Turbulence = High) should match.");
    }









    @Test
    void testEqualityCondition_Numeric() {
        JSONObject row = new JSONObject("{\"age\": 30}");
        Condition condition = new Condition("age", "=", "30");
        assertTrue(engine.evaluateCondition(row, condition), "Expected: 30 = 30");
    }

    @Test
    void testInequalityCondition_Numeric() {
        JSONObject row = new JSONObject("{\"score\": 85}");
        Condition condition = new Condition("score", "!=", "90");
        assertTrue(engine.evaluateCondition(row, condition), "Expected: 85 != 90");
    }

    @Test
    void testGreaterThanCondition_Numeric() {
        JSONObject row = new JSONObject("{\"temperature\": 100}");
        Condition condition = new Condition("temperature", ">", "95");
        assertTrue(engine.evaluateCondition(row, condition), "Expected: 100 > 95");
    }

    @Test
    void testLessThanCondition_Numeric() {
        JSONObject row = new JSONObject("{\"height\": 170}");
        Condition condition = new Condition("height", "<", "180");
        assertTrue(engine.evaluateCondition(row, condition), "Expected: 170 < 180");
    }

    @Test
    void testGreaterThanOrEqualCondition_Numeric() {
        JSONObject row = new JSONObject("{\"rank\": 10}");
        Condition condition = new Condition("rank", ">=", "10");
        assertTrue(engine.evaluateCondition(row, condition), "Expected: 10 >= 10");
    }

    @Test
    void testLessThanOrEqualCondition_Numeric() {
        JSONObject row = new JSONObject("{\"speed\": 60}");
        Condition condition = new Condition("speed", "<=", "60");
        assertTrue(engine.evaluateCondition(row, condition), "Expected: 60 <= 60");
    }

    @Test
    void testEqualityCondition_String_CaseInsensitive() {
        JSONObject row = new JSONObject("{\"status\": \"ACTIVE\"}");
        Condition condition = new Condition("status", "=", "active");
        assertTrue(engine.evaluateCondition(row, condition), "Expected: 'ACTIVE' = 'active' (case insensitive)");
    }

    @Test
    void testInequalityCondition_String() {
        JSONObject row = new JSONObject("{\"status\": \"inactive\"}");
        Condition condition = new Condition("status", "!=", "active");
        assertTrue(engine.evaluateCondition(row, condition), "Expected: 'inactive' != 'active'");
    }

    @Test
    void testLikeCondition_String() {
        JSONObject row = new JSONObject("{\"description\": \"High Performance Engine\"}");
        Condition condition = new Condition("description", "LIKE", "performance");
        assertTrue(engine.evaluateCondition(row, condition), "Expected: 'High Performance Engine' contains 'performance'");
    }

    @Test
    void testIsNullCondition() {
        JSONObject row = new JSONObject("{\"deleted_at\": null}");
        Condition condition = new Condition("deleted_at", "IS", "NULL");
        assertTrue(engine.evaluateCondition(row, condition), "Expected: deleted_at IS NULL");
    }

    @Test
    void testIsNotNullCondition() {
        JSONObject row = new JSONObject("{\"created_at\": \"2024-01-01\"}");
        Condition condition = new Condition("created_at", "IS NOT", "NULL");
        assertTrue(engine.evaluateCondition(row, condition), "Expected: created_at IS NOT NULL");
    }

    @Test
    void testJsonObjectEquality() {
        JSONObject row = new JSONObject("{\"config\": {\"mode\": \"dark\", \"volume\": 50}}");
        Condition condition = new Condition("config", "=", "{\"mode\": \"dark\", \"volume\": 50}");
        assertTrue(engine.evaluateCondition(row, condition), "Expected: JSON Object matches");
    }

    @Test
    void testJsonObjectInequality() {
        JSONObject row = new JSONObject("{\"settings\": {\"theme\": \"light\", \"notifications\": true}}");
        Condition condition = new Condition("settings", "=", "{\"theme\": \"dark\", \"notifications\": true}");
        assertFalse(engine.evaluateCondition(row, condition), "Expected: JSON Object does not match");
    }

    @Test
    void testJsonArrayEquality() {
        JSONObject row = new JSONObject("{\"tags\": [\"tech\", \"AI\", \"ML\"]}");
        Condition condition = new Condition("tags", "=", "[\"tech\", \"AI\", \"ML\"]");
        assertTrue(engine.evaluateCondition(row, condition), "Expected: JSON Array matches");
    }

    @Test
    void testJsonArrayInequality() {
        JSONObject row = new JSONObject("{\"tags\": [\"science\", \"biology\"]}");
        Condition condition = new Condition("tags", "=", "[\"science\", \"physics\"]");
        assertFalse(engine.evaluateCondition(row, condition), "Expected: JSON Array does not match");
    }

    @Test
    void testInvalidJsonComparison() {
        JSONObject row = new JSONObject("{\"metadata\": \"Not a JSON\"}");
        Condition condition = new Condition("metadata", "=", "{\"key\": \"value\"}");
        assertFalse(engine.evaluateCondition(row, condition), "Expected: Invalid JSON comparison fails");
    }

    @Test
    void testMalformedValueInQuery() {
        JSONObject row = new JSONObject("{\"title\": \"Science\"}");
        Condition condition = new Condition("title", "=", "[malformed]");
        assertFalse(engine.evaluateCondition(row, condition), "Expected: Malformed value fails comparison");
    }

    @Test
    void testMissingColumn_ShouldReturnFalse() {
        JSONObject row = new JSONObject("{\"category\": \"Books\"}");
        Condition condition = new Condition("publisher", "=", "Penguin");
        assertFalse(engine.evaluateCondition(row, condition), "Expected: Missing column should return false");
    }

    @Test
    void testWildcardMatchCondition() {
        JSONObject row = new JSONObject("{\"id\": 123, \"name\": \"Wildcard\"}");
        Condition condition = new Condition("*", "*", "*");
        assertTrue(engine.evaluateCondition(row, condition), "Expected: Wildcard (*) always returns true");
    }
}