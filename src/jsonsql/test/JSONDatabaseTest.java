package jsonsql.test;


import jsonsql.main.JSONDatabase;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
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
        JSONArray results = db.execute("SELECT * FROM users");

        // Expecting both rows from the "users" table.
        assertEquals(2, results.size());
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
        JSONArray results = db.execute("SELECT * FROM products");

        // Expecting both rows from the "products" table.
        assertEquals(2, results.size());
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
        JSONArray userResults = db.execute("SELECT id, name FROM users");
        assertEquals(2, userResults.size());
        JSONObject firstUser = userResults.getJSONObject(0);
        assertTrue(firstUser.containsKey("id"));
        assertTrue(firstUser.containsKey("name"));
        assertFalse(firstUser.containsKey("age"));

        // Query specific columns from the products table.
        JSONArray productResults = db.execute("SELECT id, price FROM products");
        assertEquals(2, productResults.size());
        JSONObject firstProduct = productResults.getJSONObject(0);
        assertTrue(firstProduct.containsKey("id"));
        assertTrue(firstProduct.containsKey("price"));
        assertFalse(firstProduct.containsKey("name"));
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
            db.execute("SELECT * FROM orders");
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

        JSONArray results = db.execute("SELECT * FROM users");
        // Expecting all three rows
        assertEquals(3, results.size());
        // Validate that the first row has expected data
        JSONObject firstRow = results.getJSONObject(0);
        assertEquals(1, firstRow.getInteger("id"));
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
        JSONArray results = db.execute("SELECT id, name FROM users");
        assertEquals(3, results.size());
        for (int i = 0; i < results.size(); i++) {
            JSONObject row = results.getJSONObject(i);
            assertTrue(row.containsKey("id"));
            assertTrue(row.containsKey("name"));
            assertFalse(row.containsKey("age"));
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
        JSONArray results = db.execute("SELECT * FROM users WHERE age > 30");
        assertEquals(1, results.size());
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
        JSONArray results = db.execute("SELECT * FROM users ORDER BY age DESC");
        assertEquals(3, results.size());
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
        JSONArray results = db.execute("SELECT * FROM users ORDER BY id ASC LIMIT 2");
        assertEquals(2, results.size());
        JSONObject firstRow = results.getJSONObject(0);
        assertEquals(1, firstRow.getInteger("id"));
        JSONObject secondRow = results.getJSONObject(1);
        assertEquals(2, secondRow.getInteger("id"));
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
        JSONArray results = db.execute("SELECT * FROM users");

        assertEquals(2, results.size());
        assertEquals("Alice", results.getJSONObject(0).getString("name"));
        assertEquals("Bob", results.getJSONObject(1).getString("name"));
    }

    @Test
    void testExecuteQuery_noMatch() {
        Map<String, String> tableData = new HashMap<>();
        tableData.put("users", "[{\"name\": \"Charlie\"}]");

        JSONDatabase db = new JSONDatabase(tableData);
        JSONArray results = db.execute("SELECT * FROM users WHERE name = 'Alice'");

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

        JSONArray results = db.execute("SELECT * FROM users");
        assertEquals(2, results.size(), "Expected one row from 'users' table");

        results = db.execute("SELECT * FROM users WHERE name='Alice'");
        JSONObject row = results.getJSONObject(0);
        assertEquals(1, results.size());
        assertEquals(1, row.getInteger("id"), "Expected id to be 1");
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

        JSONArray results = db.execute("SELECT * FROM users");
        assertEquals(1, results.size(), "Expected one row from overwritten 'users' table");

        JSONObject row = results.getJSONObject(0);
        assertEquals(2, row.getInteger("id"), "Expected id to be 2 after overwrite");
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
        JSONArray usersResults = db.execute("SELECT * FROM users");
        assertEquals(2, usersResults.size(), "Expected one row in 'users' table");
        usersResults = db.execute("SELECT * FROM users WHERE name='Alice'");
        assertEquals(1, usersResults.size(), "Expected one row in 'users' table");
        JSONObject userRow = usersResults.getJSONObject(0);
        assertEquals("Alice", userRow.getString("name"), "Expected name to be 'Alice' in 'users' table");

        // Query the "products" table.
        JSONArray productsResults = db.execute("SELECT * FROM products");
        assertEquals(2, productsResults.size(), "Expected one row in 'products' table");
        productsResults = db.execute("SELECT id, name FROM products WHERE id=101 AND name='Laptop'");
        assertEquals(1, productsResults.size(), "Expected one row in 'products' table");
        JSONObject productRow = productsResults.getJSONObject(0);
        assertEquals(101, productRow.getInteger("id"), "Expected id to be 101 in 'products' table");
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




    @Test
    public void testSelectQuery() {
        JSONDatabase db = new JSONDatabase();
        String initialData = """
            [
              {"id": 1, "name": "Alice", "age": 30},
              {"id": 2, "name": "Bob", "age": 25}
            ]
        """;
        db.addTable("people", initialData);

        JSONArray result = db.execute("SELECT * FROM people WHERE name = 'Alice'");
        assertEquals(1, result.size());
        assertEquals("Alice", result.getJSONObject(0).getString("name"));
    }

    @Test
    public void testInsertQuery() {
        JSONDatabase db = new JSONDatabase();
        String initialData = """
            [
              {"id": 1, "name": "Alice", "age": 30},
              {"id": 2, "name": "Bob", "age": 25}
            ]
        """;
        db.addTable("people", initialData);
        JSONArray inserted = db.execute("""
            INSERT INTO people VALUES (
                {"id": 3, "name": "Charlie", "age": 22}
            )
        """);

        assertEquals(1, inserted.size());
        JSONObject charlie = inserted.getJSONObject(0);
        assertEquals("Charlie", charlie.getString("name"));

        JSONArray result = db.execute("SELECT * FROM people WHERE name = 'Charlie'");
        assertEquals(1, result.size());
    }

    @Test
    public void testUpdateQuery() {
        JSONDatabase db = new JSONDatabase();
        String initialData = """
            [
              {"id": 1, "name": "Alice", "age": 30},
              {"id": 2, "name": "Bob", "age": 25}
            ]
        """;
        db.addTable("people", initialData);
        JSONArray updated = db.execute("""
            UPDATE people SET age = 40 WHERE name = 'Bob'
        """);

        assertEquals(1, updated.size());
        assertEquals(40, updated.getJSONObject(0).getIntValue("age"));

        JSONArray result = db.execute("SELECT * FROM people WHERE name = 'Bob'");
        assertEquals(40, result.getJSONObject(0).getIntValue("age"));
    }

    @Test
    public void testDeleteQuery() {
        JSONDatabase db = new JSONDatabase();
        String initialData = """
            [
              {"id": 1, "name": "Alice", "age": 30},
              {"id": 2, "name": "Bob", "age": 25}
            ]
        """;
        db.addTable("people", initialData);
        JSONArray deleted = db.execute("""
            DELETE FROM people WHERE name = 'Alice'
        """);

        assertEquals(1, deleted.size());
        assertEquals("Alice", deleted.getJSONObject(0).getString("name"));

        JSONArray remaining = db.execute("SELECT * FROM people");
        assertEquals(1, remaining.size());
        assertEquals("Bob", remaining.getJSONObject(0).getString("name"));
    }

    @Test
    public void testInsertMultipleValues() {
        JSONDatabase db = new JSONDatabase();
        String initialData = """
            [
              {"id": 1, "name": "Alice", "age": 30},
              {"id": 2, "name": "Bob", "age": 25}
            ]
        """;
        db.addTable("people", initialData);

        JSONArray result = db.execute("""
            INSERT INTO people VALUES
            (
                {"id": 4, "name": "Dora", "age": 28}
            ),
            (
                {"id": 5, "name": "Eve", "age": 33}
            )
        """);

        assertEquals(2, result.size());

        JSONArray all = db.execute("SELECT * FROM people WHERE age > 27");
        assertTrue(all.size() >= 3); // includes Alice, Dora, and Eve (if Alice wasn't deleted)
    }








    @Test
    public void testSelectAll() {
        JSONDatabase db = new JSONDatabase();
        db.addTable("employees");
        db.execute("""
            INSERT INTO employees VALUES
            ({"id": 1, "name": "Alice", "age": 30, "department": "Engineering"}),
            ({"id": 2, "name": "Bob", "age": 25, "department": "Marketing"}),
            ({"id": 3, "name": "Charlie", "age": 35, "department": "Engineering"}),
            ({"id": 4, "name": "Dana", "age": 28, "department": "HR"}),
            ({"id": 5, "name": "Eve", "age": 22, "department": "Engineering"})
        """);
        JSONArray result = db.execute("SELECT * FROM employees");
        assertEquals(5, result.size());
    }

    @Test
    public void testSelectByDepartment() {
        JSONDatabase db = new JSONDatabase();
        db.addTable("employees");
        db.execute("""
            INSERT INTO employees VALUES
            ({"id": 1, "name": "Alice", "age": 30, "department": "Engineering"}),
            ({"id": 2, "name": "Bob", "age": 25, "department": "Marketing"}),
            ({"id": 3, "name": "Charlie", "age": 35, "department": "Engineering"}),
            ({"id": 4, "name": "Dana", "age": 28, "department": "HR"}),
            ({"id": 5, "name": "Eve", "age": 22, "department": "Engineering"})
        """);
        JSONArray result = db.execute("SELECT * FROM employees WHERE department = 'Engineering'");
        assertEquals(3, result.size());
        for (Object obj : result) {
            assertEquals("Engineering", ((JSONObject) obj).getString("department"));
        }
    }

    @Test
    public void testSelectByAgeGreaterThan() {
        JSONDatabase db = new JSONDatabase();
        db.addTable("employees");
        db.execute("""
            INSERT INTO employees VALUES
            ({"id": 1, "name": "Alice", "age": 30, "department": "Engineering"}),
            ({"id": 2, "name": "Bob", "age": 25, "department": "Marketing"}),
            ({"id": 3, "name": "Charlie", "age": 35, "department": "Engineering"}),
            ({"id": 4, "name": "Dana", "age": 28, "department": "HR"}),
            ({"id": 5, "name": "Eve", "age": 22, "department": "Engineering"})
        """);
        JSONArray result = db.execute("SELECT * FROM employees WHERE age > 28");
        assertEquals(2, result.size());
        assertTrue(result.toString().contains("Alice"));
        assertTrue(result.toString().contains("Charlie"));
    }

    @Test
    public void testSelectSingleColumn() {
        JSONDatabase db = new JSONDatabase();
        db.addTable("employees");
        db.execute("""
            INSERT INTO employees VALUES
            ({"id": 1, "name": "Alice", "age": 30, "department": "Engineering"}),
            ({"id": 2, "name": "Bob", "age": 25, "department": "Marketing"}),
            ({"id": 3, "name": "Charlie", "age": 35, "department": "Engineering"}),
            ({"id": 4, "name": "Dana", "age": 28, "department": "HR"}),
            ({"id": 5, "name": "Eve", "age": 22, "department": "Engineering"})
        """);
        JSONArray result = db.execute("SELECT name FROM employees WHERE id = 4");
        assertEquals(1, result.size());
        assertEquals("Dana", result.getJSONObject(0).getString("name"));
    }

    @Test
    public void testSelectWithLimit() {
        JSONDatabase db = new JSONDatabase();
        db.addTable("employees");
        db.execute("""
            INSERT INTO employees VALUES
            ({"id": 1, "name": "Alice", "age": 30, "department": "Engineering"}),
            ({"id": 2, "name": "Bob", "age": 25, "department": "Marketing"}),
            ({"id": 3, "name": "Charlie", "age": 35, "department": "Engineering"}),
            ({"id": 4, "name": "Dana", "age": 28, "department": "HR"}),
            ({"id": 5, "name": "Eve", "age": 22, "department": "Engineering"})
        """);
        JSONArray result = db.execute("SELECT * FROM employees ORDER BY age ASC LIMIT 2");
        assertEquals(2, result.size());
        assertEquals("Eve", result.getJSONObject(0).getString("name")); // age 22
        assertEquals("Bob", result.getJSONObject(1).getString("name")); // age 25
    }

    @Test
    public void testSelectLikeQuery() {
        JSONDatabase db = new JSONDatabase();
        db.addTable("employees");
        db.execute("""
            INSERT INTO employees VALUES
            ({"id": 1, "name": "Alice", "age": 30, "department": "Engineering"}),
            ({"id": 2, "name": "Bob", "age": 25, "department": "Marketing"}),
            ({"id": 3, "name": "Charlie", "age": 35, "department": "Engineering"}),
            ({"id": 4, "name": "Dana", "age": 28, "department": "HR"}),
            ({"id": 5, "name": "Eve", "age": 22, "department": "Engineering"})
        """);
        JSONArray result = db.execute("SELECT * FROM employees WHERE name LIKE 'A%'");
        assertEquals(1, result.size());
        assertEquals("Alice", result.getJSONObject(0).getString("name"));
    }












    @Test
    public void testDeleteSingleMatch() {
        JSONDatabase db = new JSONDatabase();
        db.addTable("employees");
        db.execute("""
            INSERT INTO employees VALUES
            ({"id": 1, "name": "Alice", "role": "Engineer", "age": 30}),
            ({"id": 2, "name": "Bob", "role": "Manager", "age": 40}),
            ({"id": 3, "name": "Charlie", "role": "Engineer", "age": 35}),
            ({"id": 4, "name": "Dana", "role": "HR", "age": 28}),
            ({"id": 5, "name": "Eve", "role": "Engineer", "age": 26})
        """);
        JSONArray deleted = db.execute("DELETE FROM employees WHERE id = 2");
        assertEquals(1, deleted.size());
        assertEquals("Bob", deleted.getJSONObject(0).getString("name"));

        JSONArray remaining = db.execute("SELECT * FROM employees");
        assertEquals(4, remaining.size());
        for (Object obj : remaining) {
            assertNotEquals(2, ((JSONObject) obj).getIntValue("id"));
        }
    }

    @Test
    public void testDeleteByRole() {
        JSONDatabase db = new JSONDatabase();
        db.addTable("employees");
        db.execute("""
            INSERT INTO employees VALUES
            ({"id": 1, "name": "Alice", "role": "Engineer", "age": 30}),
            ({"id": 2, "name": "Bob", "role": "Manager", "age": 40}),
            ({"id": 3, "name": "Charlie", "role": "Engineer", "age": 35}),
            ({"id": 4, "name": "Dana", "role": "HR", "age": 28}),
            ({"id": 5, "name": "Eve", "role": "Engineer", "age": 26})
        """);

        JSONArray deleted = db.execute("DELETE FROM employees WHERE role = 'Engineer'");
        assertEquals(3, deleted.size());

        JSONArray remaining = db.execute("SELECT * FROM employees");
        assertEquals(2, remaining.size());
        for (Object obj : remaining) {
            assertNotEquals("Engineer", ((JSONObject) obj).getString("role"));
        }
    }

    @Test
    public void testDeleteWithAgeCondition() {
        JSONDatabase db = new JSONDatabase();
        db.addTable("employees");
        db.execute("""
            INSERT INTO employees VALUES
            ({"id": 1, "name": "Alice", "role": "Engineer", "age": 30}),
            ({"id": 2, "name": "Bob", "role": "Manager", "age": 40}),
            ({"id": 3, "name": "Charlie", "role": "Engineer", "age": 35}),
            ({"id": 4, "name": "Dana", "role": "HR", "age": 28}),
            ({"id": 5, "name": "Eve", "role": "Engineer", "age": 26})
        """);

        JSONArray deleted = db.execute("DELETE FROM employees WHERE age < 30");
        assertEquals(2, deleted.size());

        JSONArray remaining = db.execute("SELECT * FROM employees");
        assertEquals(3, remaining.size());
        for (Object obj : remaining) {
            assertTrue(((JSONObject) obj).getIntValue("age") >= 30);
        }
    }

    @Test
    public void testDeleteNoMatch() {
        JSONDatabase db = new JSONDatabase();
        db.addTable("employees");
        db.execute("""
            INSERT INTO employees VALUES
            ({"id": 1, "name": "Alice", "role": "Engineer", "age": 30}),
            ({"id": 2, "name": "Bob", "role": "Manager", "age": 40}),
            ({"id": 3, "name": "Charlie", "role": "Engineer", "age": 35}),
            ({"id": 4, "name": "Dana", "role": "HR", "age": 28}),
            ({"id": 5, "name": "Eve", "role": "Engineer", "age": 26})
        """);

        JSONArray deleted = db.execute("DELETE FROM employees WHERE name = 'Zach'");
        assertEquals(0, deleted.size());

        JSONArray all = db.execute("SELECT * FROM employees");
        assertEquals(5, all.size());
    }

    @Test
    public void testDeleteAllRows() {
        JSONDatabase db = new JSONDatabase();
        db.addTable("employees");
        db.execute("""
            INSERT INTO employees VALUES
            ({"id": 1, "name": "Alice", "role": "Engineer", "age": 30}),
            ({"id": 2, "name": "Bob", "role": "Manager", "age": 40}),
            ({"id": 3, "name": "Charlie", "role": "Engineer", "age": 35}),
            ({"id": 4, "name": "Dana", "role": "HR", "age": 28}),
            ({"id": 5, "name": "Eve", "role": "Engineer", "age": 26})
        """);

        JSONArray deleted = db.execute("DELETE FROM employees WHERE * = *");
        assertEquals(5, deleted.size());

        JSONArray remaining = db.execute("SELECT * FROM employees");
        assertEquals(0, remaining.size());
    }






    @Test
    public void testUpdateSingleField() {
        JSONDatabase db = new JSONDatabase();
        db.addTable("users");

        db.execute("""
            INSERT INTO users VALUES
            ({"id": 1, "name": "Alice", "role": "Developer"}),
            ({"id": 2, "name": "Bob", "role": "Manager"})
        """);

        JSONArray updated = db.execute("UPDATE users SET role = 'Lead Developer' WHERE name = 'Alice'");
        assertEquals(1, updated.size());
        assertEquals("Lead Developer", updated.getJSONObject(0).getString("role"));

        JSONArray check = db.execute("SELECT * FROM users WHERE name = 'Alice'");
        assertEquals("Lead Developer", check.getJSONObject(0).getString("role"));
    }

    @Test
    public void testUpdateMultipleRows() {
        JSONDatabase db = new JSONDatabase();
        db.addTable("users");

        db.execute("""
            INSERT INTO users VALUES
            ({"id": 1, "name": "Alice", "role": "Dev"}),
            ({"id": 2, "name": "Bob", "role": "Dev"}),
            ({"id": 3, "name": "Charlie", "role": "QA"})
        """);

        JSONArray updated = db.execute("UPDATE users SET role = 'Engineer' WHERE role = 'Dev'");
        assertEquals(2, updated.size());

        JSONArray check = db.execute("SELECT * FROM users WHERE role = 'Engineer'");
        assertEquals(2, check.size());
    }

    @Test
    public void testUpdateNestedField() {
        JSONDatabase db = new JSONDatabase();
        db.addTable("profiles");

        db.execute("""
            INSERT INTO profiles VALUES
            ({"id": 1, "user": {"name": "Alice", "location": "NY"}}),
            ({"id": 2, "user": {"name": "Bob", "location": "LA"}})
        """);

        JSONArray updated = db.execute("UPDATE profiles SET user.location = 'San Francisco' WHERE user.name = 'Bob'");
        assertEquals(1, updated.size());
        assertEquals("San Francisco", updated.getJSONObject(0).getJSONObject("user").getString("location"));
    }

    @Test
    public void testUpdateWithJsonObject() {
        JSONDatabase db = new JSONDatabase();
        db.addTable("devices");

        db.execute("""
            INSERT INTO devices VALUES
            ({"id": 1, "config": {"cpu": "i5", "ram": "8GB"}})
        """);

        JSONArray updated = db.execute("UPDATE devices SET config = {\"cpu\": \"i7\", \"ram\": \"16GB\"} WHERE id = 1");
        JSONObject config = updated.getJSONObject(0).getJSONObject("config");

        assertEquals("i7", config.getString("cpu"));
        assertEquals("16GB", config.getString("ram"));
    }

    @Test
    public void testUpdateArrayElement() {
        JSONDatabase db = new JSONDatabase();
        db.addTable("inventory");

        db.execute("""
            INSERT INTO inventory VALUES
            ({"id": 1, "items": ["apple", "banana", "cherry"]})
        """);

        JSONArray updated = db.execute("UPDATE inventory SET items.1 = 'blueberry' WHERE id = 1");
        JSONArray items = updated.getJSONObject(0).getJSONArray("items");

        assertEquals("blueberry", items.getString(1));
    }



//    @Test
//    public void testShowTables_EmptyDatabase() {
//        JSONDatabase db = new JSONDatabase();
//
//        JSONArray result = db.execute("SHOW TABLES");
//
//        assertNotNull(result);
//        assertEquals(0, result.size(), "Database should have no tables");
//    }
//
//    @Test
//    public void testShowTables_SingleTable() {
//        JSONDatabase db = new JSONDatabase();
//        db.addTable("users");
//
//        JSONArray result = db.execute("SHOW TABLES");
//
//        assertNotNull(result);
//        assertEquals(1, result.size(), "Database should have 1 table");
//
//        JSONObject tableObj = result.getJSONObject(0);
//        assertEquals("users", tableObj.getString("Table"));
//    }
//
//    @Test
//    public void testShowTables_MultipleTables() {
//        JSONDatabase db = new JSONDatabase();
//        db.addTable("users");
//        db.addTable("products");
//        db.addTable("orders");
//
//        JSONArray result = db.execute("SHOW TABLES");
//
//        assertNotNull(result);
//        assertEquals(3, result.size(), "Database should have 3 tables");
//
//        // You can collect all table names and assert they exist
//        String[] expectedTables = {"users", "products", "orders"};
//        for (String tableName : expectedTables) {
//            boolean found = result.stream()
//                    .map(obj -> ((JSONObject) obj).getString("Table"))
//                    .anyMatch(name -> name.equals(tableName));
//            assertTrue(found, "Expected table not found: " + tableName);
//        }
//    }
//
//    @Test
//    public void testShowTables_TableNamesAreLowercased() {
//        JSONDatabase db = new JSONDatabase();
//        db.addTable("Users"); // intentionally with capital U
//
//        JSONArray result = db.execute("SHOW TABLES");
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//
//        JSONObject tableObj = result.getJSONObject(0);
//        assertEquals("users", tableObj.getString("Table"), "Table names should be lowercased");
//    }
}

