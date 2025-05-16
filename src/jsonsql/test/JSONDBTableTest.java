package jsonsql.test;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jsonsql.main.JSONDBTable;
import org.junit.Test;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class JSONDBTableTest {

    private Path mTempDbFile = null;
    private JSONDBTable mDB = null;

//    @Before
//    public void setUp() throws IOException {
//        // Create a temp file with a prefix and suffix
//        mTempDbFile = Files.createTempFile("mytemp", ".jsondb");
//        File file = mTempDbFile.toFile();
//        String path = mTempDbFile.toAbsolutePath().toString();
//        mDB = new JSONDBTable(path);
//
//        if (file.exists()) {
//            System.out.println("File created at: " + path);
//        }
//    }
//
//    @After
//    public void tearDown() {
//        File file = mTempDbFile.toFile();
//        String path = mTempDbFile.toAbsolutePath().toString();
//        if (file.exists()) {
//            System.out.println("Deleting file at: " + path);
//            file.delete();
//        }
//    }
//



//    @Test
//    public void testInsertPersistenceWithTempFile() throws IOException {
//        // Create a temp file path, then delete it so JSONDBTable can create a fresh one
//        Path tempFilePath = Files.createTempFile("test_table", ".jsondb");
//        Files.deleteIfExists(tempFilePath);
//        String tablePath = tempFilePath.toAbsolutePath().toString();
//
//        // Step 1: Create table and insert data
//        JSONDBTable table = new JSONDBTable(tablePath);
//
//        JSONObject row1 = new JSONObject();
//        row1.put("name", "Slash");
//        row1.put("power", 15);
//
//        JSONObject row2 = new JSONObject();
//        row2.put("name", "Blizzard");
//        row2.put("power", 25);
//
//        JSONArray batch = new JSONArray();
//        batch.add(row1);
//        batch.add(row2);
//
//        table.insertRaw(batch.toJSONString());
//
//        assertEquals(2, table.size());
//
//        // Step 2: Reload table from disk and verify persistence
//        JSONDBTable reloadedTable = new JSONDBTable(tablePath);
//        assertEquals(2, reloadedTable.size());
//
//        JSONObject loadedRow1 = reloadedTable.get(0);
//        JSONObject loadedRow2 = reloadedTable.get(1);
//
//        assertEquals("Slash", loadedRow1.getString("name"));
//        assertEquals(15, loadedRow1.getIntValue("power"));
//
//        assertEquals("Blizzard", loadedRow2.getString("name"));
//        assertEquals(25, loadedRow2.getIntValue("power"));
//
//        // Cleanup: delete the temporary file
//        Files.deleteIfExists(tempFilePath);
//    }

    @Test
    public void testSelectAll() throws IOException {
        File dbFile = File.createTempFile("testdb", ".jsondb");
        dbFile.delete();
        JSONDBTable table = new JSONDBTable(dbFile.getAbsolutePath());
        table.insert(new JSONObject() {{
            put("name", "Alice");
            put("level", 5);
        }});
        table.insert(new JSONObject() {{
            put("name", "Bob");
            put("level", 10);
        }});
        table.insert(new JSONObject() {{
            put("name", "Charlie");
            put("level", 3);
        }});
        assertNotNull(table);
        JSONArray result = table.select("SELECT * FROM " + table.getTableName());
        assertEquals(3, result.size());
    }

    @Test
    public void testSelectWithWhere() throws IOException {
        File dbFile = File.createTempFile("testdb", ".jsondb");
        dbFile.delete();
        JSONDBTable table = new JSONDBTable(dbFile.getAbsolutePath());
        table.insert(new JSONObject() {{
            put("name", "Alice");
            put("level", 5);
        }});
        table.insert(new JSONObject() {{
            put("name", "Bob");
            put("level", 10);
        }});
        table.insert(new JSONObject() {{
            put("name", "Charlie");
            put("level", 3);
        }});

        JSONArray result = table.select("SELECT * FROM " + table.getTableName() + " WHERE level > 4");
        assertEquals(2, result.size());

        for (int i = 0; i < result.size(); i++) {
            int level = result.getJSONObject(i).getIntValue("level");
            assertTrue(level > 4);
        }
    }

    @Test
    public void testSelectWithProjection() throws IOException {
        File dbFile = File.createTempFile("testdb", ".jsondb");
        dbFile.delete();
        JSONDBTable table = new JSONDBTable(dbFile.getAbsolutePath());
        table.insert(new JSONObject() {{
            put("name", "Alice");
            put("level", 5);
        }});
        table.insert(new JSONObject() {{
            put("name", "Bob");
            put("level", 10);
        }});
        table.insert(new JSONObject() {{
            put("name", "Charlie");
            put("level", 3);
        }});
        assertNotNull(table);
        JSONArray result = table.select("SELECT name FROM " + table.getTableName() + " WHERE level = 10");
        assertEquals(1, result.size());
        JSONObject row = result.getJSONObject(0);
        assertTrue(row.containsKey("name"));
        assertFalse(row.containsKey("level"));
        assertEquals("Bob", row.getString("name"));
    }

    @Test
    public void testSelectNestedObjectField() throws IOException {
        File dbFile = File.createTempFile("test_nested", ".jsondb");
        dbFile.delete();
        JSONDBTable table = new JSONDBTable(dbFile.getAbsolutePath());

        table.insert(new JSONObject() {{
            put("name", "Alice");
            put("age", 30);
            put("address", new JSONObject(Map.of(
                    "street", "123 Main St",
                    "city", "Metropolis",
                    "zip", "12345"
            )));
            put("skills", new JSONArray(List.of("Java", "SQL", "JSON")));
            put("active", true);
        }});

        table.insert(new JSONObject() {{
            put("name", "Bob");
            put("age", 25);
            put("address", new JSONObject(Map.of(
                    "street", "456 Side St",
                    "city", "Gotham",
                    "zip", "67890"
            )));
            put("skills", new JSONArray(List.of("Python", "JavaScript")));
            put("active", false);
        }});

        table.insert(new JSONObject() {{
            put("name", "Charlie");
            put("age", 35);
            put("address", new JSONObject(Map.of(
                    "street", "789 Hidden Ave",
                    "city", "Metropolis",
                    "zip", "12345"
            )));
            put("skills", new JSONArray(List.of("Java", "Go", "Rust")));
            put("active", true);
        }});

        JSONArray result = table.select("SELECT name, age FROM " + table.getTableName() + " WHERE address.city = 'Metropolis'");
        assertEquals(2, result.size());
        for (int i = 0; i < result.size(); i++) {
            JSONObject row = result.getJSONObject(i);
            assertTrue(row.getString("name").equals("Alice") || row.getString("name").equals("Charlie"));
        }
    }

    @Test
    public void testSelectArrayElementMatch() throws IOException {
        File dbFile = File.createTempFile("test_array", ".jsondb");
        dbFile.delete();
        JSONDBTable table = new JSONDBTable(dbFile.getAbsolutePath());

        table.insert(new JSONObject() {{
            put("name", "Alice");
            put("skills", new JSONArray(List.of("Java", "SQL")));
        }});
        table.insert(new JSONObject() {{
            put("name", "Bob");
            put("skills", new JSONArray(List.of("Python", "JavaScript")));
        }});

        JSONArray result = table.select("SELECT name FROM " + table.getTableName() + " WHERE skills[0] = 'Java'");
        assertEquals(1, result.size());
        assertEquals("Alice", result.getJSONObject(0).getString("name"));
    }

    @Test
    public void testSelectWithMultipleConditions() throws IOException {
        File dbFile = File.createTempFile("test_conditions", ".jsondb");
        dbFile.delete();
        JSONDBTable table = new JSONDBTable(dbFile.getAbsolutePath());

        table.insert(new JSONObject() {{
            put("name", "Alice");
            put("age", 30);
            put("active", true);
        }});

        table.insert(new JSONObject() {{
            put("name", "Bob");
            put("age", 25);
            put("active", false);
        }});

        table.insert(new JSONObject() {{
            put("name", "Charlie");
            put("age", 35);
            put("active", true);
        }});

        JSONArray result = table.select("SELECT * FROM " + table.getTableName() + " WHERE active = true AND age > 30");
        assertEquals(1, result.size());
        assertEquals("Charlie", result.getJSONObject(0).getString("name"));
    }

    @Test
    public void testSelectOrderByNestedField() throws IOException {
        File dbFile = File.createTempFile("test_orderby", ".jsondb");
        dbFile.delete();
        JSONDBTable table = new JSONDBTable(dbFile.getAbsolutePath());

        table.insert(new JSONObject() {{
            put("name", "Alice");
            put("age", 30);
            put("address", new JSONObject(Map.of("zip", "12345")));
        }});
        table.insert(new JSONObject() {{
            put("name", "Charlie");
            put("age", 35);
            put("address", new JSONObject(Map.of("zip", "12345")));
        }});
        table.insert(new JSONObject() {{
            put("name", "Bob");
            put("age", 25);
            put("address", new JSONObject(Map.of("zip", "67890")));
        }});

        JSONArray result = table.select("SELECT name FROM " + table.getTableName() + " WHERE address.zip = '12345' ORDER BY age DESC");
        assertEquals(2, result.size());
        assertEquals("Charlie", result.getJSONObject(0).getString("name"));
        assertEquals("Alice", result.getJSONObject(1).getString("name"));
    }


}
