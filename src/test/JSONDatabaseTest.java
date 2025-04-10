package test;


import main.constants.JSONDatabase;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JSONDatabaseTest {
    // Sample JSON data for two tables: "users" and "products"
    private final String usersData = "[" +
            "{\"id\": 1, \"name\": \"Alice\", \"age\": 30}," +
            "{\"id\": 2, \"name\": \"Bob\", \"age\": 25}" +
            "]";

    private final String productsData = "[" +
            "{\"id\": 101, \"name\": \"Laptop\", \"price\": 999.99}," +
            "{\"id\": 102, \"name\": \"Smartphone\", \"price\": 499.99}" +
            "]";

    /**
     * Tests that a SELECT * query against the "users" table returns the correct data.
     */
    @Test
    public void testSelectAllFromUsersTable() {
        Map<String, String> tableData = new HashMap<>();
        tableData.put("users", usersData);
        tableData.put("products", productsData);

        JSONDatabase db = new JSONDatabase(tableData);
        JSONArray results = db.executeQuery("SELECT * FROM users");

        // Expecting both rows from the "users" table.
        assertEquals(2, results.length());
        JSONObject firstUser = results.getJSONObject(0);
        assertEquals("Alice", firstUser.getString("name"));
    }

    /**
     * Tests that a SELECT * query against the "products" table returns the correct data.
     */
    @Test
    public void testSelectAllFromProductsTable() {
        Map<String, String> tableData = new HashMap<>();
        tableData.put("users", usersData);
        tableData.put("products", productsData);

        JSONDatabase db = new JSONDatabase(tableData);
        JSONArray results = db.executeQuery("SELECT * FROM products");

        // Expecting both rows from the "products" table.
        assertEquals(2, results.length());
        JSONObject firstProduct = results.getJSONObject(0);
        assertEquals("Laptop", firstProduct.getString("name"));
    }

    /**
     * Tests that selecting specific columns from each table returns only those columns.
     */
    @Test
    public void testSelectSpecificColumnsFromMultipleTables() {
        Map<String, String> tableData = new HashMap<>();
        tableData.put("users", usersData);
        tableData.put("products", productsData);

        JSONDatabase db = new JSONDatabase(tableData);

        // Query specific columns from the users table.
        JSONArray userResults = db.executeQuery("SELECT id, name FROM users");
        assertEquals(2, userResults.length());
        JSONObject firstUser = userResults.getJSONObject(0);
        assertTrue(firstUser.has("id"));
        assertTrue(firstUser.has("name"));
        assertFalse(firstUser.has("age"));

        // Query specific columns from the products table.
        JSONArray productResults = db.executeQuery("SELECT id, price FROM products");
        assertEquals(2, productResults.length());
        JSONObject firstProduct = productResults.getJSONObject(0);
        assertTrue(firstProduct.has("id"));
        assertTrue(firstProduct.has("price"));
        assertFalse(firstProduct.has("name"));
    }

    /**
     * Tests that executing a query against a non-existent table results in a NullPointerException.
     */
    @Test
    public void testQueryNonExistentTable() {
        Map<String, String> tableData = new HashMap<>();
        tableData.put("users", usersData);
        tableData.put("products", productsData);

        JSONDatabase db = new JSONDatabase(tableData);

        // Query a table "orders" that does not exist in the database.
        // The getQueryTable method will extract "orders" but there is no such table.
        assertThrows(NullPointerException.class, () -> {
            db.executeQuery("SELECT * FROM orders");
        });
    }

    // Sample JSON data representing a table of "users"
    private final String sampleData = "[" +
            "{\"id\": 1, \"name\": \"Alice\", \"age\": 30}," +
            "{\"id\": 2, \"name\": \"Bob\", \"age\": 25}," +
            "{\"id\": 3, \"name\": \"Charlie\", \"age\": 35}" +
            "]";

    /**
     * Tests that getQueryTable correctly extracts the table name from a valid SQL query.
     */
    @Test
    public void testGetQueryTable_Valid() {
        Map<String, String> tableData = new HashMap<>();
        tableData.put("users", sampleData);
        JSONDatabase db = new JSONDatabase(tableData);

        String tableName = db.getQueryTable("SELECT * FROM users");
        assertEquals("users", tableName);
    }

    /**
     * Tests that getQueryTable returns null when there is no FROM clause.
     */
    @Test
    public void testGetQueryTable_NoFromClause() {
        Map<String, String> tableData = new HashMap<>();
        tableData.put("users", sampleData);
        JSONDatabase db = new JSONDatabase(tableData);

        String tableName = db.getQueryTable("SELECT * WHERE name = 'Alice'");
        assertNull(tableName);
    }

    /**
     * Tests that executing a simple SELECT * query returns all rows.
     */
    @Test
    public void testExecuteQuery_SelectAll() {
        Map<String, String> tableData = new HashMap<>();
        tableData.put("users", sampleData);
        JSONDatabase db = new JSONDatabase(tableData);

        JSONArray results = db.executeQuery("SELECT * FROM users");
        // Expecting all three rows
        assertEquals(3, results.length());
        // Validate that the first row has expected data
        JSONObject firstRow = results.getJSONObject(0);
        assertEquals(1, firstRow.getInt("id"));
        assertEquals("Alice", firstRow.getString("name"));
    }

    /**
     * Tests that executing a query selecting specific columns returns only those columns.
     */
    @Test
    public void testExecuteQuery_SelectSpecificColumns() {
        Map<String, String> tableData = new HashMap<>();
        tableData.put("users", sampleData);
        JSONDatabase db = new JSONDatabase(tableData);

        // Select only id and name, so the age field should be omitted in the result
        JSONArray results = db.executeQuery("SELECT id, name FROM users");
        assertEquals(3, results.length());
        for (int i = 0; i < results.length(); i++) {
            JSONObject row = results.getJSONObject(i);
            assertTrue(row.has("id"));
            assertTrue(row.has("name"));
            assertFalse(row.has("age"));
        }
    }

    /**
     * Tests that executing a query with a WHERE clause filters the results correctly.
     */
    @Test
    public void testExecuteQuery_WhereClause() {
        Map<String, String> tableData = new HashMap<>();
        tableData.put("users", sampleData);
        JSONDatabase db = new JSONDatabase(tableData);

        // Query for users with age greater than 30 (only "Charlie" should match)
        JSONArray results = db.executeQuery("SELECT * FROM users WHERE age > 30");
        assertEquals(1, results.length());
        JSONObject row = results.getJSONObject(0);
        assertEquals("Charlie", row.getString("name"));
    }

    /**
     * Tests that executing a query with an ORDER BY clause returns results sorted accordingly.
     */
    @Test
    public void testExecuteQuery_OrderByClause() {
        Map<String, String> tableData = new HashMap<>();
        tableData.put("users", sampleData);
        JSONDatabase db = new JSONDatabase(tableData);

        // Order users by age in descending order (Charlie should appear first)
        JSONArray results = db.executeQuery("SELECT * FROM users ORDER BY age DESC");
        assertEquals(3, results.length());
        JSONObject firstRow = results.getJSONObject(0);
        assertEquals("Charlie", firstRow.getString("name"));
    }

    /**
     * Tests that executing a query with a LIMIT clause returns only the specified number of rows.
     */
    @Test
    public void testExecuteQuery_LimitClause() {
        Map<String, String> tableData = new HashMap<>();
        tableData.put("users", sampleData);
        JSONDatabase db = new JSONDatabase(tableData);

        // Order by id ascending and limit the result to 2 rows
        JSONArray results = db.executeQuery("SELECT * FROM users ORDER BY id ASC LIMIT 2");
        assertEquals(2, results.length());
        JSONObject firstRow = results.getJSONObject(0);
        assertEquals(1, firstRow.getInt("id"));
        JSONObject secondRow = results.getJSONObject(1);
        assertEquals(2, secondRow.getInt("id"));
    }

    @Test
    void testGetQueryTable_validQuery() {
        Map<String, String> tableData = new HashMap<>();
        tableData.put("users", "[{\"name\": \"Alice\"}]");

        JSONDatabase db = new JSONDatabase(tableData);
        String table = db.getQueryTable("SELECT name FROM users WHERE name = 'Alice'");
        assertEquals("users", table);
    }

    @Test
    void testGetQueryTable_invalidQuery() {
        Map<String, String> tableData = new HashMap<>();
        tableData.put("users", "[{\"name\": \"Alice\"}]");

        JSONDatabase db = new JSONDatabase(tableData);
        String table = db.getQueryTable("SELECT name WHERE name = 'Alice'");
        assertNull(table);
    }

    @Test
    void testExecuteQuery_basic() {
        Map<String, String> tableData = new HashMap<>();
        tableData.put("users", "[{\"name\": \"Alice\"}, {\"name\": \"Bob\"}]");

        JSONDatabase db = new JSONDatabase(tableData);
        JSONArray results = db.executeQuery("SELECT * FROM users");

        assertEquals(2, results.length());
        assertEquals("Alice", results.getJSONObject(0).getString("name"));
        assertEquals("Bob", results.getJSONObject(1).getString("name"));
    }

    @Test
    void testExecuteQuery_noMatch() {
        Map<String, String> tableData = new HashMap<>();
        tableData.put("users", "[{\"name\": \"Charlie\"}]");

        JSONDatabase db = new JSONDatabase(tableData);
        JSONArray results = db.executeQuery("SELECT * FROM users WHERE name = 'Alice'");

        // Depending on implementation of executeQuery inside JSONTable
        assertTrue(results.isEmpty() || !results.toString().contains("Charlie"));
    }

    /**
     * Tests that after adding a table, executing a query on that table returns the expected data.
     */
    @Test
    public void testAddTableAndQuery() {
        JSONDatabase db = new JSONDatabase();
        db.addTable("users", usersData);

        JSONArray results = db.executeQuery("SELECT * FROM users");
        assertEquals(2, results.length(), "Expected one row from 'users' table");

        results = db.executeQuery("SELECT * FROM users WHERE name='Alice'");
        JSONObject row = results.getJSONObject(0);
        assertEquals(1, results.length());
        assertEquals(1, row.getInt("id"), "Expected id to be 1");
        assertEquals("Alice", row.getString("name"), "Expected name to be 'Alice'");
    }

    /**
     * Tests that adding a table with an existing name overwrites the previous data.
     */
    @Test
    public void testOverwriteTableData() {
        JSONDatabase db = new JSONDatabase();
        db.addTable("users", usersData);
        // Overwrite "users" table with updated data.
        String updatedUsersData = "[" +
                "{\"id\": 2, \"name\": \"Bob\"}" +
                "]";
        db.addTable("users", updatedUsersData);

        JSONArray results = db.executeQuery("SELECT * FROM users");
        assertEquals(1, results.length(), "Expected one row from overwritten 'users' table");

        JSONObject row = results.getJSONObject(0);
        assertEquals(2, row.getInt("id"), "Expected id to be 2 after overwrite");
        assertEquals("Bob", row.getString("name"), "Expected name to be 'Bob' after overwrite");
    }

    /**
     * Tests that adding multiple tables works and queries execute against the correct table.
     */
    @Test
    public void testAddMultipleTables() {
        JSONDatabase db = new JSONDatabase();
        db.addTable("users", usersData);
        db.addTable("products", productsData);

        // Query the "users" table.
        JSONArray usersResults = db.executeQuery("SELECT * FROM users");
        assertEquals(2, usersResults.length(), "Expected one row in 'users' table");
        usersResults = db.executeQuery("SELECT * FROM users WHERE name='Alice'");
        assertEquals(1, usersResults.length(), "Expected one row in 'users' table");
        JSONObject userRow = usersResults.getJSONObject(0);
        assertEquals("Alice", userRow.getString("name"), "Expected name to be 'Alice' in 'users' table");

        // Query the "products" table.
        JSONArray productsResults = db.executeQuery("SELECT * FROM products");
        assertEquals(2, productsResults.length(), "Expected one row in 'products' table");
        productsResults = db.executeQuery("SELECT id, name FROM products WHERE id=101 AND name='Laptop'");
        assertEquals(1, productsResults.length(), "Expected one row in 'products' table");
        JSONObject productRow = productsResults.getJSONObject(0);
        assertEquals(101, productRow.getInt("id"), "Expected id to be 101 in 'products' table");
        assertEquals("Laptop", productRow.getString("name"), "Expected name to be 'Laptop' in 'products' table");
    }

    /**
     * Test the customFormat method directly with a matching number of placeholders.
     */
    @Test
    public void testCustomFormatDirect() {
        String formatStr = "Hello, {}! Today is {}.";
        String expected = "Hello, Alice! Today is Monday.";
        String result = JSONDatabase.customFormat(formatStr, "Alice", "Monday");
        assertEquals(expected, result);
    }

    /**
     * Test customFormat when there are more placeholders than provided values.
     * Extra placeholders should be left unchanged.
     */
    @Test
    public void testCustomFormatWithExtraPlaceholders() {
        String formatStr = "Value1: {}, Value2: {}, Value3: {}";
        String expected = "Value1: 10, Value2: 20, Value3: {}";
        String result = JSONDatabase.customFormat(formatStr, 10, 20);
        assertEquals(expected, result);
    }

    /**
     * Test executeQuery(String, Object...) to ensure the custom formatting is applied.
     * Here, we add a table "users" and then execute a query that uses placeholders.
     * We verify that the formatted query is as expected.
     *
     * For the purpose of this test, since the actual JSONTable implementation is not shown,
     * we assume that the query string is used for table lookup.
     */
    @Test
    public void testExecuteQueryWithCustomFormat() {
        // Create an empty JSONDatabase and add a table named "users".
        JSONDatabase db = new JSONDatabase();
        // The table data here is irrelevant for testing the formatting; an empty JSON array suffices.
        db.addTable("users", "[]");

        // Define a query that uses placeholders:
        String queryWithPlaceholders = "SELECT * FROM {} WHERE id = {}";
        // Expected formatted query: placeholders replaced appropriately.
        String expectedFormattedQuery = "SELECT * FROM users WHERE id = 1";

        // Use the customFormat method directly to test formatting.
        String formattedQuery = JSONDatabase.customFormat(queryWithPlaceholders, "users", 1);
        assertEquals(expectedFormattedQuery, formattedQuery, "The formatted query should match the expected string.");

        // Now, call executeQuery with the query and parameters.
        // The executeQuery method internally uses customFormat to format the query before execution.
        // In a full integration test, the JSONTable would process the query; here we mainly
        // validate that the table is correctly identified using the formatted query.
        JSONArray result = db.executeQuery(queryWithPlaceholders, "users", 1);

        // Since we don't have control over JSONTable's executeQuery method in this test,
        // we check that a non-null JSONArray is returned.
        assertNotNull(result, "Expected a non-null JSONArray from executeQuery.");
    }
}
