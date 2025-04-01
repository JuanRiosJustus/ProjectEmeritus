package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.constants.CSVTable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class CSVTableTest {

    private CSVTable mTestTable;

    @BeforeEach
    public void setUp() {
        String csv = "id,name,age,city,salary\n"
                + "1,John,25,\"New York\",50000\n"
                + "2,Alice,30,\"Los Angeles\",60000\n"
                + "3,Bob,20,Chicago,45000\n"
                + "4,\"Charlie, Jr\",35,\"San Francisco\",70000\n"
                + "5,David,40,Miami,65000";
        mTestTable = new CSVTable(csv);
    }

    @Test
    public void testSelectAll() {
        List<Map<String, Object>> result = mTestTable.query("SELECT * FROM table");
        List<Map<String, Object>> expected = List.of(
                Map.of("id", 1, "name", "John", "age", 25, "city", "New York", "salary", 50000),
                Map.of("id", 2, "name", "Alice", "age", 30, "city", "Los Angeles", "salary", 60000),
                Map.of("id", 3, "name", "Bob", "age", 20, "city", "Chicago", "salary", 45000),
                Map.of("id", 4, "name", "Charlie, Jr", "age", 35, "city", "San Francisco", "salary", 70000),
                Map.of("id", 5, "name", "David", "age", 40, "city", "Miami", "salary", 65000)
        );
        assertEquals(expected, result);
    }

    @Test
    public void testSelectColumns() {
        List<Map<String, Object>> result = mTestTable.query("SELECT name, city FROM table");
        List<Map<String, Object>> expected = List.of(
                Map.of("name", "John", "city", "New York"),
                Map.of("name", "Alice", "city", "Los Angeles"),
                Map.of("name", "Bob", "city", "Chicago"),
                Map.of("name", "Charlie, Jr", "city", "San Francisco"),
                Map.of("name", "David", "city", "Miami")
        );
        assertEquals(expected, result);
    }

    @Test
    public void testWhereNumericCondition() {
        List<Map<String, Object>> result = mTestTable.query(
                "SELECT id, name FROM table WHERE age > 25 ORDER BY age ASC"
        );
        List<Map<String, Object>> expected = List.of(
                Map.of("id", 2, "name", "Alice"),
                Map.of("id", 4, "name", "Charlie, Jr"),
                Map.of("id", 5, "name", "David")
        );
        assertEquals(expected, result);
    }

    @Test
    public void testWhereStringEquality() {
        List<Map<String, Object>> result = mTestTable.query(
                "SELECT id, city FROM table WHERE city = 'Chicago'"
        );
        List<Map<String, Object>> expected = List.of(
                Map.of("id", 3, "city", "Chicago")
        );
        assertEquals(expected, result);
    }

    @Test
    public void testWhereStringLike() {
        List<Map<String, Object>> result = mTestTable.query(
                "SELECT id, name FROM table WHERE name LIKE 'Cha'"
        );
        List<Map<String, Object>> expected = List.of(
                Map.of("id", 4, "name", "Charlie, Jr")
        );
        assertEquals(expected, result);
    }

    @Test
    public void testLimitAndOrderBy() {
        List<Map<String, Object>> result = mTestTable.query(
                "SELECT name, salary FROM table ORDER BY salary DESC LIMIT 2"
        );
        List<Map<String, Object>> expected = List.of(
                Map.of("name", "Charlie, Jr", "salary", 70000),
                Map.of("name", "David", "salary", 65000)
        );
        assertEquals(expected, result);
    }

    @Test
    public void testComplexWhere() {
        List<Map<String, Object>> result = mTestTable.query(
                "SELECT id, name, city FROM table WHERE age >= 30 AND (city = 'Los Angeles' OR city = 'Miami')"
        );
        List<Map<String, Object>> expected = List.of(
                Map.of("id", 2, "name", "Alice", "city", "Los Angeles"),
                Map.of("id", 5, "name", "David", "city", "Miami")
        );
        assertEquals(expected, result);
    }

    @Test
    public void testQuotedFieldHandling() {
        List<Map<String, Object>> result = mTestTable.query(
                "SELECT name FROM table WHERE name LIKE 'Charlie'"
        );
        List<Map<String, Object>> expected = List.of(
                Map.of("name", "Charlie, Jr")
        );
        assertEquals(expected, result);
    }

    @Test
    public void testDoubleQuotesParsing() {
        String csv = "id,name,quote\n"
                + "1,John,\"He said, \"\"Hello!\"\"\"";
        CSVTable table = new CSVTable(csv);
        List<Map<String, Object>> result = table.query("SELECT quote FROM table");

        // The expected result should remove the outer quotes and convert "" to "
        List<Map<String, Object>> expected = List.of(
                Map.of("quote", "He said, \"Hello!\"")
        );
        assertEquals(expected, result);
    }

    @Test
    public void testReadsAbilitiesCsv() throws IOException {
        CSVTable table1 = new CSVTable(Files.readString(Path.of("res/database/abilities.csv")));
        System.out.println("flflflf");
    }
}