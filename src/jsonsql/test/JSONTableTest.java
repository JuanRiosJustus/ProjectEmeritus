package jsonsql.test;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jsonsql.main.JSONTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JSONTableTest {

    private JSONTable mTable;
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
        JSONArray jsonData = JSON.parseArray(jsonString);
        mTable = new JSONTable("users", jsonString);
    }




    @Test
    void testQueryExecution_WhereCondition() {
        String sql = "SELECT name FROM users WHERE age >= 30";
        JSONArray results = mTable.select(sql);

        assertEquals(3, results.size());
        assertEquals("Alice", results.getJSONObject(0).getString("name"));
        assertEquals("Bob", results.getJSONObject(1).getString("name"));
        assertEquals("David", results.getJSONObject(2).getString("name"));
    }

    @Test
    void testQueryExecution_OrderBy() {
        String sql = "SELECT name FROM users ORDER BY age DESC";
        JSONArray results = mTable.select(sql);

        assertEquals("Frank", results.getJSONObject(0).getString("name"));
        assertEquals("Bob", results.getJSONObject(1).getString("name"));
        assertEquals("David", results.getJSONObject(2).getString("name"));
        assertEquals("Alice", results.getJSONObject(3).getString("name"));
        assertEquals("Charlie", results.getJSONObject(4).getString("name"));
        assertEquals("Eve", results.getJSONObject(5).getString("name"));
    }

    @Test
    void testQueryExecution_Limit() {
        String sql = "SELECT name FROM users ORDER BY age DESC LIMIT 2";
        JSONArray results = mTable.select(sql);

        assertEquals(2, results.size());
        assertEquals("Frank", results.getJSONObject(0).getString("name"));
        assertEquals("Bob", results.getJSONObject(1).getString("name"));
    }

    @Test
    void testQueryExecution_WhereConditionWithAndOr() {
        String sql = "SELECT name FROM users WHERE city = 'New York' AND age >= 30 OR salary > 50000";
        JSONArray results = mTable.select(sql);

        assertEquals(2, results.size());  // Expecting 2 results, not 3
        assertEquals("Alice", results.getJSONObject(0).getString("name"));
        assertEquals("David", results.getJSONObject(1).getString("name"));
    }

    @Test
    void testQueryExecution_NullValues() {
        String sql = "SELECT name FROM users WHERE salary IS NULL";
        JSONArray results = mTable.select(sql);

        assertEquals(1, results.size());
        assertEquals("Frank", results.getJSONObject(0).getString("name"));
    }

    @Test
    void testQueryExecution_CaseInsensitiveKeywords() {
        String sql = "select * from users order by age desc limit 2";
        JSONArray results = mTable.select(sql);

        assertEquals(2, results.size());
        assertEquals("Frank", results.getJSONObject(0).getString("name"));
        assertEquals("Bob", results.getJSONObject(1).getString("name"));
    }

    @Test
    void testQueryExecution_SortingWithMissingValues() {
        String sql = "SELECT name FROM users ORDER BY salary ASC";
        JSONArray results = mTable.select(sql);

        assertEquals("Charlie", results.getJSONObject(0).getString("name")); // 30,000 (smallest salary)
        assertEquals("Bob", results.getJSONObject(1).getString("name")); // 45,000
        assertEquals("Eve", results.getJSONObject(2).getString("name")); // 50,000
        assertEquals("Alice", results.getJSONObject(3).getString("name")); // 60,000
        assertEquals("David", results.getJSONObject(4).getString("name")); // 70,000
        assertEquals("Frank", results.getJSONObject(5).getString("name")); // Null salary (should be last)
    }


    @Test
    void testQueryExecution_StringComparisonCaseSsensitive() {
        String sql = "SELECT name FROM users WHERE city = 'New York'";
        JSONArray results = mTable.select(sql);

        assertEquals(3, results.size());
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

        JSONArray results = mTable.select(sql);

        assertEquals(3, results.size()); // Alice, Charlie, Eve

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

        JSONArray results = mTable.select(sql);

        assertEquals(1, results.size()); // Charlie
        assertEquals("Charlie", results.getJSONObject(0).getString("name"));
    }

    @Test
    void testQueryExecution_ComplexLogicalConditions() {
        String sql = """
                    SELECT name 
                    FROM users 
                    WHERE (city = 'New York' AND age >= 30) OR salary > 50000
                """;

        JSONArray results = mTable.select(sql);

        assertEquals(2, results.size()); // Alice, David, Eve

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

        JSONArray results = mTable.select(sql);

        assertEquals(3, results.size()); // David, Alice, Eve (top 3 salaries)

        assertEquals("Frank", results.getJSONObject(0).getString("name"));  // Salary: 70000
        assertEquals("David", results.getJSONObject(1).getString("name"));  // Salary: 60000
        assertEquals("Alice", results.getJSONObject(2).getString("name"));    // Salary: 50000
    }



    @Test
    void testQueryExecution_JsonObjectField() {
        String sql = "SELECT name FROM users WHERE address.city = 'New York'";
        JSONArray results = mTable.select(sql);

        assertEquals(3, results.size()); // Alice, Charlie, and Eve live in New York
        assertEquals("Alice", results.getJSONObject(0).getString("name"));
        assertEquals("Charlie", results.getJSONObject(1).getString("name"));
        assertEquals("Eve", results.getJSONObject(2).getString("name"));
    }

    @Test
    void testQueryExecution_JsonObjectExactMatch() {
        String sql = "SELECT name FROM users WHERE address = '{\"street\": \"123 Main St\", \"city\": \"New York\", \"zip\": \"10001\"}'";
        JSONArray results = mTable.select(sql);

        assertEquals(1, results.size());
        assertEquals("Alice", results.getJSONObject(0).getString("name"));
    }

    @Test
    void testQueryExecution_JsonObjectPartialMatch() {
        String sql = "SELECT name FROM users WHERE address.city LIKE 'New York'";
        JSONArray results = mTable.select(sql);

        assertEquals(3, results.size()); // Alice, Charlie, and Eve have "New York" in their address
    }

    @Test
    void testQueryExecution_JsonObjectNumericComparison() {
        String sql = "SELECT name FROM users WHERE address.zip > 50000 ORDER BY address.zip";
        JSONArray results = mTable.select(sql);

        assertEquals(2, results.size()); // David (Chicago) and Bob (Los Angeles) have zip codes > 50000
        assertEquals("David", results.getJSONObject(0).getString("name"));
        assertEquals("Bob", results.getJSONObject(1).getString("name"));
    }

    @Test
    void testQueryExecution_JsonObjectIsNull() {
        String sql = "SELECT name FROM users WHERE address IS NULL";
        JSONArray results = mTable.select(sql);

        assertEquals(1, results.size());
        assertEquals("Frank", results.getJSONObject(0).getString("name")); // Frank has NULL for address
    }

    @Test
    void testQueryExecution_JsonObjectIsNotNull() {
        String sql = "SELECT name FROM users WHERE address IS NOT NULL";
        JSONArray results = mTable.select(sql);

        assertEquals(5, results.size()); // Everyone except Frank has an address
    }

    @Test
    void testQueryExecution_SortByJsonObjectField() {
        String sql = "SELECT name FROM users ORDER BY address.zip ASC";
        JSONArray results = mTable.select(sql);

        assertEquals("Alice", results.getJSONObject(0).getString("name"));   // 10001
        assertEquals("Charlie", results.getJSONObject(1).getString("name")); // 10003
        assertEquals("Eve", results.getJSONObject(2).getString("name"));     // 10016
        assertEquals("David", results.getJSONObject(3).getString("name"));   // 60601
        assertEquals("Bob", results.getJSONObject(4).getString("name"));     // 90028
    }


    @Test
    void testComplexQuery_StressTest() {
        String sql = """
                        SELECT name, age, salary, address.city 
                        FROM users 
                        WHERE ( city = 'New York' AND age >= 30 ) 
                        OR ( salary > 50000 OR skills.0. = 'Python' OR skills.0. = 'Rust' ) 
                        ORDER BY salary DESC, age ASC 
                        LIMIT 5
                """;

        JSONArray results = mTable.select(sql);

        // Expected results should be filtered correctly
        assertEquals(3, results.size()); // Limit applied correctly

        // Verify sorting order (salary DESC, age ASC)
        assertEquals("David", results.getJSONObject(0).getString("name"));  // Salary: 70000
        assertEquals("Alice", results.getJSONObject(1).getString("name"));  // Salary: 60000
        assertEquals("Charlie", results.getJSONObject(2).getString("name")); // Salary: 30000

        // Verify data integrity
        assertEquals(35, results.getJSONObject(0).getInteger("age"));
        assertEquals(30, results.getJSONObject(1).getInteger("age"));
        assertEquals(28, results.getJSONObject(2).getInteger("age"));
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

        JSONArray results = mTable.select(sql);

        assertEquals(3, results.size());

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

        JSONArray results = mTable.select(sql);

        assertEquals(3, results.size());

        // Order should be ASCENDING by salary
        assertEquals("Charlie", results.getJSONObject(0).getString("name"));  // Salary: 30000
        assertEquals("Bob", results.getJSONObject(1).getString("name"));      // Salary: 45000
        assertEquals("Eve", results.getJSONObject(2).getString("name"));      // Salary: 50000
    }

    @Test
    void testEmptyResultSet() {
        String sql = "SELECT name FROM users WHERE age > 100";
        JSONArray results = mTable.select(sql);

        assertEquals(0, results.size()); // Expecting no matches
    }



    @Test
    void testAdvancedQuery_ComplexFilteringSorting() {
        String sql = """
                        SELECT name, age, salary, address.city 
                        FROM users 
                        WHERE ( city = 'New York' AND age >= 30 AND age >= 30 ) OR ( skills.0 = 'Python' AND salary < 40000 )
                        AND ( address.city IS NOT NULL ) 
                        ORDER BY salary DESC, age ASC 
                        LIMIT 4
                """;

        JSONArray results = mTable.select(sql);

        // Expected results count (LIMIT = 4)
        assertEquals(2, results.size());

        // ✅ Expected order based on (salary DESC, age ASC)
        assertEquals("Alice", results.getJSONObject(0).getString("name"));  // Salary: 60000
        assertEquals("Charlie", results.getJSONObject(1).getString("name")); // Salary: 30000 (Filtered)

        // ✅ Ensure NULL values are properly handled
        for (int i = 0; i < results.size(); i++) {
            assertNotNull(results.getJSONObject(i).get("address.city"));
        }

        // ✅ Ensure conditions are met
        assertTrue(results.getJSONObject(1).getInteger("salary") < 40000);
    }


    @Test
    public void testInsertUpdateSelectFlow() {
        JSONTable table = new JSONTable("items");
        table.insertRaw("""
        [
            {"id": 1, "name": "Sword", "type": "weapon", "stats": {"attack": 10, "weight": 5}},
            {"id": 2, "name": "Shield", "type": "armor", "stats": {"defense": 15, "weight": 8}},
            {"id": 3, "name": "Potion", "type": "consumable", "stats": {"healing": 25}},
            {"id": 4, "name": "Helmet", "type": "armor", "stats": {"defense": 8, "weight": 3}},
            {"id": 5, "name": "Bow", "type": "weapon", "stats": {"attack": 7, "range": 15}}
        ]
    """);

        table.update("UPDATE items SET stats.attack = 12 WHERE name = 'Sword'");

        JSONArray weapons = table.select("SELECT name FROM items WHERE type = 'weapon'");
        assertEquals(2, weapons.size());
        List<String> weaponNames = weapons.stream().map(o -> ((JSONObject) o).getString("name")).toList();
        assertTrue(weaponNames.contains("Sword"));
        assertTrue(weaponNames.contains("Bow"));

        JSONArray heavy = table.select("SELECT name FROM items WHERE stats.weight > 6");
        assertEquals(1, heavy.size());  // only Shield matches

        JSONArray top2 = table.select("SELECT * FROM items ORDER BY id DESC LIMIT 2");
        assertEquals("Bow", top2.getJSONObject(0).getString("name"));
        assertEquals("Helmet", top2.getJSONObject(1).getString("name"));
    }


    @Test
    public void testComplexInsertUpdateSelectFlow() {
        JSONTable table = new JSONTable("employees");

        // Insert multiple employees
        table.insertRaw("""
        [
            {
              "name": "Alice",
              "age": 30,
              "department": {"name": "Engineering", "floor": 5},
              "skills": ["Java", "SQL"]
            },
            {
              "name": "Bob",
              "age": 45,
              "department": {"name": "HR", "floor": 2},
              "skills": ["Communication", "Recruitment"]
            },
            {
              "name": "Charlie",
              "age": 28,
              "department": {"name": "Engineering", "floor": 5},
              "skills": ["C++", "Python"]
            },
            {
              "name": "Dana",
              "age": 50,
              "department": {"name": "Finance", "floor": 3},
              "skills": ["Excel", "Accounting"]
            },
            {
              "name": "Eve",
              "age": 35,
              "department": {"name": "Engineering", "floor": 5},
              "skills": ["Kotlin", "Java"]
            }
        ]
        """);

        // ✅ Update: Promote Engineers over 30 to "Lead Engineer"
        table.update("""
            UPDATE employees
            SET department.title = 'Lead Engineer'
            WHERE department.name = 'Engineering' AND age > 30
        """);

        assertNull(table.get(0).getJSONObject("department").get("title"));
        assertNull(table.get(2).getJSONObject("department").get("title")); // Charlie is only 28
        assertEquals("Lead Engineer", table.get(4).getJSONObject("department").get("title"));

        // ✅ Update: Change second skill of Dana
        table.update("""
            UPDATE employees
            SET skills.1 = 'Budgeting'
            WHERE name = 'Dana'
        """);

        JSONArray danaSkills = table.get(3).getJSONArray("skills");
        assertEquals("Budgeting", danaSkills.getString(1));

        // ✅ Insert a new employee with metadata
        table.insertRaw("""
            {
              "name": "Frank",
              "age": 40,
              "metadata": {
                "status": "on leave",
                "projects": [
                  {"name": "X", "duration": 3},
                  {"name": "Y", "duration": 6}
                ]
              }
            }
        """);

        // ✅ Update: Change duration of project Y to 8
        table.update("""
            UPDATE employees
            SET metadata.projects.1.duration = 8
            WHERE name = 'Frank'
        """);

        int updatedDuration = table.get(table.size() - 1)
                .getJSONObject("metadata")
                .getJSONArray("projects")
                .getJSONObject(1)
                .getIntValue("duration");

        assertEquals(8, updatedDuration);

        // ✅ Select: Get all employees in Engineering department
        JSONArray engineering = table.select("""
            SELECT name FROM employees WHERE department.name = 'Engineering'
        """);

        assertEquals(3, engineering.size());
        assertEquals("Alice", engineering.getJSONObject(0).getString("name"));
        assertEquals("Charlie", engineering.getJSONObject(1).getString("name"));
        assertEquals("Eve", engineering.getJSONObject(2).getString("name"));
    }





    @Test
    public void testDeepNestingInsertSelectUpdate() {
        JSONTable table = new JSONTable("projects");

        // INSERT deeply nested structures
        table.insertRaw("""
        [
          {
            "id": 1,
            "meta": {
              "owner": {
                "name": "Alice",
                "contact": {
                  "email": "alice@example.com",
                  "phones": ["1234", "5678"]
                }
              },
              "status": "active"
            },
            "tasks": [
              {"name": "Design", "hours": 10},
              {"name": "Development", "hours": 25}
            ]
          },
          {
            "id": 2,
            "meta": {
              "owner": {
                "name": "Bob",
                "contact": {
                  "email": "bob@example.com",
                  "phones": ["9999", "0000"]
                }
              },
              "status": "paused"
            },
            "tasks": [
              {"name": "Planning", "hours": 8},
              {"name": "Execution", "hours": 30}
            ]
          }
        ]
        """);

        // ✅ UPDATE nested array field (change hours of a specific task)
        table.update("""
            UPDATE projects
            SET tasks.1.hours = 40
            WHERE id = 1
        """);

        int updatedHours = table.get(0)
                .getJSONArray("tasks")
                .getJSONObject(1)
                .getIntValue("hours");
        assertEquals(40, updatedHours);

        // ✅ UPDATE nested object path (change contact email)
        table.update("""
            UPDATE projects
            SET meta.owner.contact.email = 'alice.new@example.com'
            WHERE id = 1
        """);

        String newEmail = table.get(0)
                .getJSONObject("meta")
                .getJSONObject("owner")
                .getJSONObject("contact")
                .getString("email");
        assertEquals("alice.new@example.com", newEmail);

        // ✅ UPDATE: Auto-create nested path in new column
        table.update("""
            UPDATE projects
            SET meta.reviewer.name = 'Reviewer Joe'
            WHERE id = 2
        """);

        JSONObject reviewer = table.get(1)
                .getJSONObject("meta")
                .getJSONObject("reviewer");
        assertEquals("Reviewer Joe", reviewer.getString("name"));

        // ✅ SELECT from nested path and check return values
        JSONArray emails = table.select("""
            SELECT meta.owner.contact.email FROM projects
        """);

        assertEquals(2, emails.size());
        assertEquals("alice.new@example.com", emails.getJSONObject(0).getString("meta.owner.contact.email"));
        assertEquals("bob@example.com", emails.getJSONObject(1).getString("meta.owner.contact.email"));

        // ✅ UPDATE: deeply nested array of objects (add a third task with auto-create)
        table.update("""
            UPDATE projects
            SET tasks.2 = {"name": "Testing", "hours": 12}
            WHERE id = 2
        """);

        JSONObject newTask = table.get(1)
                .getJSONArray("tasks")
                .getJSONObject(2);
        assertEquals("Testing", newTask.getString("name"));
        assertEquals(12, newTask.getIntValue("hours"));
    }


    @Test
    public void testNestedEmployeeUpdatesAndSelects() {
        JSONTable table = new JSONTable("departments");

        table.insertRaw("""
        [
          {
            "deptId": 101,
            "name": "Engineering",
            "employees": [
              {
                "name": "Eve",
                "profile": {
                  "position": "Lead Engineer",
                  "experience": 7
                },
                "skills": ["Java", "Kubernetes"]
              },
              {
                "name": "Frank",
                "profile": {
                  "position": "Developer",
                  "experience": 2
                },
                "skills": ["Python"]
              }
            ]
          },
          {
            "deptId": 102,
            "name": "Design",
            "employees": [
              {
                "name": "Grace",
                "profile": {
                  "position": "Designer",
                  "experience": 4
                },
                "skills": ["Photoshop", "Figma"]
              }
            ]
          }
        ]
        """);

        // ✅ Update nested experience field
        table.update("""
            UPDATE departments
            SET employees.0.profile.experience = 8
            WHERE name = 'Engineering'
        """);
        assertEquals(8, table.get(0).getJSONArray("employees").getJSONObject(0)
                .getJSONObject("profile").getIntValue("experience"));

        // ✅ Add new skill in the skill array
        table.update("""
            UPDATE departments
            SET employees.0.skills.2 = 'Docker'
            WHERE name = 'Engineering'
        """);
        JSONArray skills = table.get(0).getJSONArray("employees").getJSONObject(0).getJSONArray("skills");
        assertEquals("Docker", skills.getString(2));

        // ✅ Add entire new employee object to array
        table.update("""
            UPDATE departments
            SET employees.2 = {
              "name": "Heidi",
              "profile": { "position": "Intern", "experience": 0 },
              "skills": []
            }
            WHERE name = 'Design'
        """);
        JSONObject newEmployee = table.get(1).getJSONArray("employees").getJSONObject(2);
        assertEquals("Heidi", newEmployee.getString("name"));
        assertEquals("Intern", newEmployee.getJSONObject("profile").getString("position"));

        // ✅ Select and verify all department names
        JSONArray results = table.select("SELECT name FROM departments");
        assertEquals(2, results.size());
        assertEquals("Engineering", results.getJSONObject(0).getString("name"));
        assertEquals("Design", results.getJSONObject(1).getString("name"));

        // ✅ Select and verify deeply nested skill
        JSONArray skillQuery = table.select("SELECT employees.0.skills.1 FROM departments WHERE name = 'Engineering'");
        assertEquals("Kubernetes", skillQuery.getJSONObject(0).getString("employees.0.skills.1"));

        // ✅ Update nested object that didn't previously exist
        table.update("""
            UPDATE departments
            SET meta.audit.reviewer = 'Jane'
            WHERE name = 'Design'
        """);
        JSONObject audit = table.get(1).getJSONObject("meta").getJSONObject("audit");
        assertEquals("Jane", audit.getString("reviewer"));
    }





    @Test
    public void testDeleteSingleMatch() {
        JSONTable table = new JSONTable("users");
        table.insertRaw("{\"name\": \"Alice\", \"age\": 30}");
        table.insertRaw("{\"name\": \"Bob\", \"age\": 25}");

        table.delete("DELETE FROM users WHERE name = 'Alice'");

        assertEquals(1, table.size());
        assertEquals("Bob", table.get(0).getString("name"));
    }

    @Test
    public void testDeleteNoMatch() {
        JSONTable table = new JSONTable("users");
        table.insertRaw("{\"name\": \"Alice\"}");
        table.insertRaw("{\"name\": \"Bob\"}");

        table.delete("DELETE FROM users WHERE name = 'Charlie'");

        assertEquals(2, table.size());
    }

    @Test
    public void testDeleteAllMatches() {
        JSONTable table = new JSONTable("users");
        table.insertRaw("{\"role\": \"admin\"}");
        table.insertRaw("{\"role\": \"admin\"}");
        table.insertRaw("{\"role\": \"user\"}");

        table.delete("DELETE FROM users WHERE role = 'admin'");

        assertEquals(1, table.size());
        assertEquals("user", table.get(0).getString("role"));
    }

    @Test
    public void testDeleteNestedCondition() {
        JSONTable table = new JSONTable("users");
        table.insertRaw("""
            {
              "name": "Alice",
              "profile": { "status": "inactive" }
            }
        """);
        table.insertRaw("""
            {
              "name": "Bob",
              "profile": { "status": "active" }
            }
        """);

        table.delete("DELETE FROM users WHERE profile.status = 'inactive'");

        assertEquals(1, table.size());
        assertEquals("Bob", table.get(0).getString("name"));
    }

    @Test
    public void testDeleteUsingNumberComparison() {
        JSONTable table = new JSONTable("inventory");
        table.insertRaw("{\"item\": \"Potion\", \"stock\": 0}");
        table.insertRaw("{\"item\": \"Elixir\", \"stock\": 10}");
        table.insertRaw("{\"item\": \"Bomb\", \"stock\": 0}");

        table.delete("DELETE FROM inventory WHERE stock = 0");

        assertEquals(1, table.size());
        assertEquals("Elixir", table.get(0).getString("item"));
    }


    @Test
    public void testDeleteWithNestedCondition_ObjectMatch() {
        JSONTable table = new JSONTable("projects");

        table.insertRaw("""
            {
              "title": "AI Research",
              "lead": { "name": "Alice", "department": "R&D" },
              "status": "active"
            }
        """);
        table.insertRaw("""
            {
              "title": "Web Overhaul",
              "lead": { "name": "Bob", "department": "Design" },
              "status": "inactive"
            }
        """);

        table.delete("DELETE FROM projects WHERE lead.department = 'Design'");

        assertEquals(1, table.size());
        assertEquals("AI Research", table.get(0).getString("title"));
    }

    @Test
    public void testDeleteWithNestedArrayElement() {
        JSONTable table = new JSONTable("groups");

        table.insertRaw("""
            {
              "group": "Alpha",
              "members": [
                { "name": "Xavier", "role": "lead" },
                { "name": "Yvonne", "role": "dev" }
              ]
            }
        """);
        table.insertRaw("""
            {
              "group": "Beta",
              "members": [
                { "name": "Zane", "role": "lead" },
                { "name": "Wendy", "role": "design" }
              ]
            }
        """);

        table.delete("DELETE FROM groups WHERE members.1.role = 'design'");

        assertEquals(1, table.size());
        assertEquals("Alpha", table.get(0).getString("group"));
    }

    @Test
    public void testDeleteDeeplyNestedArrayOfObjects() {
        JSONTable table = new JSONTable("records");

        table.insertRaw("""
            {
              "log": {
                "entries": [
                  { "timestamp": "2024-01-01", "type": "error" },
                  { "timestamp": "2024-01-02", "type": "info" }
                ]
              }
            }
        """);
        table.insertRaw("""
            {
              "log": {
                "entries": [
                  { "timestamp": "2024-01-03", "type": "warning" },
                  { "timestamp": "2024-01-04", "type": "error" }
                ]
              }
            }
        """);

        table.delete("DELETE FROM records WHERE log.entries.0.type = 'warning'");

        assertEquals(1, table.size());
        JSONArray entries = table.get(0).getJSONObject("log").getJSONArray("entries");
        assertEquals("2024-01-01", entries.getJSONObject(0).getString("timestamp"));
    }

    @Test
    public void testDeleteAllWithWildcardCondition() {
        JSONTable table = new JSONTable("misc");

        table.insertRaw("{\"foo\": 123}");
        table.insertRaw("{\"bar\": 456}");
        table.insertRaw("{\"baz\": 789}");

        table.delete("DELETE FROM misc WHERE * = *");

        assertEquals(0, table.size());
    }

    @Test
    public void testDeleteSingleRow() {
        JSONTable table = new JSONTable("users");

        table.insertRaw("{\"id\": 1, \"name\": \"Alice\"}");
        table.insertRaw("{\"id\": 2, \"name\": \"Bob\"}");
        table.insertRaw("{\"id\": 3, \"name\": \"Charlie\"}");

        JSONArray deleted = table.delete("DELETE FROM users WHERE name = 'Bob'");

        assertEquals(1, deleted.size());
        assertEquals("Bob", deleted.getJSONObject(0).getString("name"));
        assertEquals(2, table.size());
    }

    @Test
    public void testDeleteMultipleRows() {
        JSONTable table = new JSONTable("products");

        table.insertRaw("{\"id\": 1, \"category\": \"fruit\"}");
        table.insertRaw("{\"id\": 2, \"category\": \"vegetable\"}");
        table.insertRaw("{\"id\": 3, \"category\": \"fruit\"}");

        JSONArray deleted = table.delete("DELETE FROM products WHERE category = 'fruit'");

        assertEquals(2, deleted.size());
        assertEquals("fruit", deleted.getJSONObject(0).getString("category"));
        assertEquals("fruit", deleted.getJSONObject(1).getString("category"));
        assertEquals(1, table.size());
    }

    @Test
    public void testDeleteNoMatchV2() {
        JSONTable table = new JSONTable("employees");

        table.insertRaw("{\"id\": 1, \"name\": \"Alice\"}");
        table.insertRaw("{\"id\": 2, \"name\": \"Bob\"}");

        JSONArray deleted = table.delete("DELETE FROM employees WHERE name = 'Charlie'");

        assertEquals(0, deleted.size());
        assertEquals(2, table.size());
    }

    @Test
    public void testDeleteWithNestedCondition() {
        JSONTable table = new JSONTable("accounts");

        table.insertRaw("""
            {
                "username": "john",
                "profile": { "status": "inactive", "type": "user" }
            }
        """);
        table.insertRaw("""
            {
                "username": "jane",
                "profile": { "status": "active", "type": "admin" }
            }
        """);

        JSONArray deleted = table.delete("DELETE FROM accounts WHERE profile.status = 'inactive'");

        assertEquals(1, deleted.size());
        assertEquals("john", deleted.getJSONObject(0).getString("username"));
        assertEquals(1, table.size());
    }

    @Test
    public void testDeleteAllRows() {
        JSONTable table = new JSONTable("logs");

        table.insertRaw("{\"msg\": \"error\", \"level\": 3}");
        table.insertRaw("{\"msg\": \"warning\", \"level\": 2}");
        table.insertRaw("{\"msg\": \"info\", \"level\": 1}");

        JSONArray deleted = table.delete("DELETE FROM logs WHERE * = *");

        assertEquals(3, deleted.size());
        assertEquals(0, table.size());
    }



    @Test
    public void testUpdateFlatField() {
        JSONTable table = new JSONTable("users");
        table.insertRaw("{\"id\": 1, \"name\": \"Alice\"}");
        table.insertRaw("{\"id\": 2, \"name\": \"Bob\"}");

        JSONArray updated = table.update("UPDATE users SET name = 'Charlie' WHERE id = 2");

        assertEquals(1, updated.size());
        assertEquals("Charlie", updated.getJSONObject(0).getString("name"));
        assertEquals("Charlie", table.get(1).getString("name"));
    }

    @Test
    public void testUpdateNestedObjectField() {
        JSONTable table = new JSONTable("profiles");
        table.insertRaw("""
            {
              "id": 1,
              "info": { "email": "old@example.com", "active": true }
            }
        """);

        JSONArray updated = table.update("UPDATE profiles SET info.email = 'new@example.com' WHERE id = 1");

        assertEquals(1, updated.size());
        assertEquals("new@example.com", table.get(0).getJSONObject("info").getString("email"));
    }

    @Test
    public void testUpdateArrayElement() {
        JSONTable table = new JSONTable("skills");
        table.insertRaw("""
            {
              "name": "Jane",
              "skills": ["Java", "C++", "Python"]
            }
        """);

        JSONArray updated = table.update("UPDATE skills SET skills.1 = 'Go' WHERE name = 'Jane'");

        assertEquals(1, updated.size());
        JSONArray skills = table.get(0).getJSONArray("skills");
        assertEquals("Go", skills.getString(1));
    }

    @Test
    public void testUpdateWithJsonObject() {
        JSONTable table = new JSONTable("accounts");
        table.insertRaw("{\"id\": 1, \"meta\": {\"verified\": false}}");

        JSONArray updated = table.update("""
            UPDATE accounts
            SET meta = { "verified": true, "role": "admin" }
            WHERE id = 1
        """);

        assertEquals(1, updated.size());
        JSONObject meta = table.get(0).getJSONObject("meta");
        assertTrue(meta.getBoolean("verified"));
        assertEquals("admin", meta.getString("role"));
    }

    @Test
    public void testUpdateMultipleRows() {
        JSONTable table = new JSONTable("students");
        table.insertRaw("{\"name\": \"Ava\", \"grade\": 90}");
        table.insertRaw("{\"name\": \"Ben\", \"grade\": 90}");
        table.insertRaw("{\"name\": \"Cara\", \"grade\": 80}");

        JSONArray updated = table.update("UPDATE students SET grade = 95 WHERE grade = 90");

        assertEquals(2, updated.size());
        assertEquals(95, table.get(0).getIntValue("grade"));
        assertEquals(95, table.get(1).getIntValue("grade"));
        assertEquals(80, table.get(2).getIntValue("grade"));
    }

    @Test
    public void testUpdateNoMatch() {
        JSONTable table = new JSONTable("log");
        table.insertRaw("{\"event\": \"start\"}");
        table.insertRaw("{\"event\": \"stop\"}");

        JSONArray updated = table.update("UPDATE log SET event = 'restart' WHERE event = 'pause'");

        assertEquals(0, updated.size());
        assertEquals("start", table.get(0).getString("event"));
        assertEquals("stop", table.get(1).getString("event"));
    }

    @Test
    public void testUpdateWithWildcardCondition() {
        JSONTable table = new JSONTable("notes");
        table.insertRaw("{\"note\": \"remember to sleep\"}");
        table.insertRaw("{\"note\": \"remember to eat\"}");

        JSONArray updated = table.update("UPDATE notes SET note = 'remember to hydrate' WHERE * = *");

        assertEquals(2, updated.size());
        assertEquals("remember to hydrate", table.get(0).getString("note"));
        assertEquals("remember to hydrate", table.get(1).getString("note"));
    }

    @Test
    public void testUpdateWithConstantConditionV2() {
        JSONTable table = new JSONTable("notes");
        table.insertRaw("{\"note\": \"remember to sleep\"}");
        table.insertRaw("{\"note\": \"remember to eat\"}");

        JSONArray updated = table.update("UPDATE notes SET note = 'remember to hydrate' WHERE 1 = 1");

        assertEquals(2, updated.size());
        assertEquals("remember to hydrate", table.get(0).getString("note"));
        assertEquals("remember to hydrate", table.get(1).getString("note"));
    }




    @Test
    public void testDeleteWithNestedObjectCondition() {
        JSONTable table = new JSONTable("users");
        table.insertRaw("""
            {
              "id": 1,
              "profile": {
                "contact": {
                  "email": "alice@example.com"
                }
              }
            }
        """);
        table.insertRaw("""
            {
              "id": 2,
              "profile": {
                "contact": {
                  "email": "bob@example.com"
                }
              }
            }
        """);

        JSONArray deleted = table.delete("DELETE FROM users WHERE profile.contact.email = 'bob@example.com'");
        assertEquals(1, deleted.size());
        assertEquals("bob@example.com", deleted.getJSONObject(0)
                .getJSONObject("profile")
                .getJSONObject("contact")
                .getString("email"));
        assertEquals(1, table.size());
    }

    @Test
    public void testDeleteWithArrayElementCondition() {
        JSONTable table = new JSONTable("logs");
        table.insertRaw("""
            {
              "id": 1,
              "events": ["start", "pause", "resume"]
            }
        """);
        table.insertRaw("""
            {
              "id": 2,
              "events": ["start", "stop"]
            }
        """);

        JSONArray deleted = table.delete("DELETE FROM logs WHERE events.1 = 'pause'");
        assertEquals(1, deleted.size());
        assertEquals(1, deleted.getJSONObject(0).getIntValue("id"));
        assertEquals(1, table.size());
    }

    @Test
    public void testDeleteWithDeeplyNestedArrayAndObject() {
        JSONTable table = new JSONTable("projects");
        table.insertRaw("""
            {
              "name": "Gamma",
              "metadata": {
                "milestones": [
                  { "status": "complete" },
                  { "status": "pending" }
                ]
              }
            }
        """);
        table.insertRaw("""
            {
              "name": "Omega",
              "metadata": {
                "milestones": [
                  { "status": "complete" },
                  { "status": "complete" }
                ]
              }
            }
        """);

        JSONArray deleted = table.delete("DELETE FROM projects WHERE metadata.milestones.1.status = 'pending'");
        assertEquals(1, deleted.size());
        assertEquals("Gamma", deleted.getJSONObject(0).getString("name"));
        assertEquals(1, table.size());
    }

    @Test
    public void testDeleteWithNonMatchingDeepPath() {
        JSONTable table = new JSONTable("systems");
        table.insertRaw("""
            {
              "hostname": "alpha",
              "config": {
                "network": {
                  "ip": "192.168.1.1"
                }
              }
            }
        """);
        table.insertRaw("""
            {
              "hostname": "beta",
              "config": {
                "network": {
                  "ip": "192.168.1.2"
                }
              }
            }
        """);

        JSONArray deleted = table.delete("DELETE FROM systems WHERE config.network.ip = '192.168.1.99'");
        assertEquals(0, deleted.size());
        assertEquals(2, table.size());
    }

    @Test
    public void testDeleteMultipleMatchingNested() {
        JSONTable table = new JSONTable("inventory");
        table.insertRaw("""
            {
              "item": "Widget",
              "tags": ["fragile", "blue"]
            }
        """);
        table.insertRaw("""
            {
              "item": "Gadget",
              "tags": ["fragile", "red"]
            }
        """);
        table.insertRaw("""
            {
              "item": "Device",
              "tags": ["durable", "blue"]
            }
        """);

        JSONArray deleted = table.delete("DELETE FROM inventory WHERE tags.0 = 'fragile'");
        assertEquals(2, deleted.size());
        assertEquals("Widget", deleted.getJSONObject(0).getString("item"));
        assertEquals("Gadget", deleted.getJSONObject(1).getString("item"));
        assertEquals(1, table.size());
    }



    @Test
    public void testSingleInsert() {
        JSONTable table = new JSONTable("users");
        JSONArray result = table.insert("""
            INSERT INTO users VALUES ({"name": "Alice", "age": 30})
        """);

        assertEquals(1, result.size());
        JSONObject row = result.getJSONObject(0);
        assertEquals("Alice", row.getString("name"));
        assertEquals(30, row.getIntValue("age"));
    }

    @Test
    public void testMultipleInserts() {
        JSONTable table = new JSONTable("products");
        JSONArray result = table.insert("""
            INSERT INTO products VALUES 
            ({"name": "Laptop", "price": 999.99}),
            ({"name": "Mouse", "price": 25.5})
        """);

        assertEquals(2, result.size());
        assertEquals("Laptop", result.getJSONObject(0).getString("name"));
        assertEquals(25.5, result.getJSONObject(1).getDoubleValue("price"));
    }

    @Test
    public void testInsertWithNestedObject() {
        JSONTable table = new JSONTable("accounts");
        JSONArray result = table.insert("""
            INSERT INTO accounts VALUES 
            ({"user": "Charlie", "details": {"email": "charlie@example.com", "active": true}})
        """);

        JSONObject row = result.getJSONObject(0);
        JSONObject details = row.getJSONObject("details");

        assertEquals("Charlie", row.getString("user"));
        assertTrue(details.getBoolean("active"));
        assertEquals("charlie@example.com", details.getString("email"));
    }

    @Test
    public void testInsertWithNestedArray() {
        JSONTable table = new JSONTable("teams");
        JSONArray result = table.insert("""
            INSERT INTO teams VALUES 
            ({"name": "Alpha", "members": ["John", "Jane", "Jake"]})
        """);

        JSONObject row = result.getJSONObject(0);
        JSONArray members = row.getJSONArray("members");

        assertEquals("Alpha", row.getString("name"));
        assertEquals(3, members.size());
        assertEquals("Jake", members.getString(2));
    }

    @Test
    public void testInsertWithNestedParenthesesInString() {
        JSONTable table = new JSONTable("notes");
        JSONArray result = table.insert("""
            INSERT INTO notes VALUES 
            ({"title": "Meeting Notes", "content": "Discussion on (Q1 Roadmap)"})
        """);

        JSONObject row = result.getJSONObject(0);
        assertEquals("Meeting Notes", row.getString("title"));
        assertEquals("Discussion on (Q1 Roadmap)", row.getString("content"));
    }

    @Test
    public void testInsertInvalidJsonThrows() {
        JSONTable table = new JSONTable("bad");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            table.insert("""
                INSERT INTO bad VALUES 
                ({"broken": "true",,, })  // Trailing comma makes it invalid
            """);
        });

        assertTrue(exception.getMessage().contains("Invalid JSON object"));
    }

    @Test
    public void testInsertWrongTableNameThrows() {
        JSONTable table = new JSONTable("animals");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            table.insert("""
                INSERT INTO wrong_table VALUES 
                ({"name": "Tiger"})
            """);
        });
    }

    @Test
    public void testInsertEmptyValues() {
        JSONTable table = new JSONTable("empty");
        assertTrue(table.insert("INSERT INTO empty VALUES").isEmpty());
    }





    @Test
    public void testInsertWithBooleanAndNullValues() {
        JSONTable table = new JSONTable("settings");
        JSONArray result = table.insert("""
            INSERT INTO settings VALUES 
            ({"darkMode": true, "notifications": false, "nickname": null})
        """);

        JSONObject row = result.getJSONObject(0);
        assertTrue(row.getBoolean("darkMode"));
        assertFalse(row.getBoolean("notifications"));
        assertTrue(row.containsKey("nickname"));
        assertNull(row.get("nickname"));
    }

    @Test
    public void testInsertWithMixedTypes() {
        JSONTable table = new JSONTable("inventory");
        JSONArray result = table.insert("""
            INSERT INTO inventory VALUES 
            ({"id": 1, "name": "Widget", "tags": ["tools", "utility"], "metadata": {"weight": 2.5, "fragile": false}})
        """);

        JSONObject row = result.getJSONObject(0);
        assertEquals(1, row.getIntValue("id"));
        assertEquals("Widget", row.getString("name"));

        JSONArray tags = row.getJSONArray("tags");
        assertEquals("tools", tags.getString(0));

        JSONObject metadata = row.getJSONObject("metadata");
        assertEquals(2.5, metadata.getDoubleValue("weight"));
        assertFalse(metadata.getBoolean("fragile"));
    }

    @Test
    public void testInsertMultipleComplexObjects() {
        JSONTable table = new JSONTable("logs");
        JSONArray result = table.insert("""
            INSERT INTO logs VALUES 
            ({"timestamp": "2024-01-01T12:00:00Z", "events": [{"type": "login", "user": "admin"}]}),
            ({"timestamp": "2024-01-01T13:00:00Z", "events": [{"type": "logout", "user": "guest"}]})
        """);

        assertEquals(2, result.size());

        JSONObject first = result.getJSONObject(0);
        JSONArray firstEvents = first.getJSONArray("events");
        assertEquals("login", firstEvents.getJSONObject(0).getString("type"));

        JSONObject second = result.getJSONObject(1);
        JSONArray secondEvents = second.getJSONArray("events");
        assertEquals("logout", secondEvents.getJSONObject(0).getString("type"));
    }

    @Test
    public void testInsertStringContainingBraces() {
        JSONTable table = new JSONTable("messages");
        JSONArray result = table.insert("""
            INSERT INTO messages VALUES 
            ({"text": "Hello {user}, welcome to the system."})
        """);

        JSONObject row = result.getJSONObject(0);
        assertEquals("Hello {user}, welcome to the system.", row.getString("text"));
    }

    @Test
    public void testInsertWithArrayOfObjects() {
        JSONTable table = new JSONTable("projects");
        JSONArray result = table.insert("""
            INSERT INTO projects VALUES 
            ({"name": "Apollo", "tasks": [{"id": 1, "done": false}, {"id": 2, "done": true}]})
        """);

        JSONObject row = result.getJSONObject(0);
        JSONArray tasks = row.getJSONArray("tasks");
        assertEquals(2, tasks.size());
        assertFalse(tasks.getJSONObject(0).getBoolean("done"));
        assertTrue(tasks.getJSONObject(1).getBoolean("done"));
    }

    @Test
    public void testInsertDeeplyNestedStructure() {
        JSONTable table = new JSONTable("config");
        JSONArray result = table.insert("""
            INSERT INTO config VALUES 
            ({
              "env": {
                "staging": {
                  "url": "https://staging.example.com",
                  "settings": {
                    "retries": 3,
                    "timeout": 5000
                  }
                }
              }
            })
        """);

        JSONObject row = result.getJSONObject(0);
        JSONObject settings = row.getJSONObject("env")
                .getJSONObject("staging")
                .getJSONObject("settings");

        assertEquals(3, settings.getIntValue("retries"));
        assertEquals(5000, settings.getIntValue("timeout"));
    }



    @Test
    public void testInsertMultipleFlatObjects() {
        JSONTable table = new JSONTable("users");

        JSONArray result = table.insert("""
            INSERT INTO users VALUES 
            ({"name": "Alice", "age": 30}),
            ({"name": "Bob", "age": 25}),
            ({"name": "Charlie", "age": 28})
        """);

        assertEquals(3, result.size());
        assertEquals("Alice", result.getJSONObject(0).getString("name"));
        assertEquals(25, result.getJSONObject(1).getIntValue("age"));
        assertEquals("Charlie", result.getJSONObject(2).getString("name"));
    }

    @Test
    public void testInsertWithVaryingStructure() {
        JSONTable table = new JSONTable("accounts");

        JSONArray result = table.insert("""
            INSERT INTO accounts VALUES 
            ({"username": "user1", "status": "active"}),
            ({"username": "user2", "status": "inactive", "roles": ["admin", "editor"]}),
            ({"username": "user3"})
        """);

        assertEquals(3, result.size());
        assertTrue(result.getJSONObject(1).getJSONArray("roles").contains("admin"));
        assertTrue(result.getJSONObject(2).containsKey("username"));
    }

    @Test
    public void testInsertWithNestedObjectsMixed() {
        JSONTable table = new JSONTable("profiles");

        JSONArray result = table.insert("""
            INSERT INTO profiles VALUES 
            ({"name": "Dana", "contact": {"email": "dana@example.com", "phone": "123-456"}}),
            ({"name": "Eli", "contact": {"email": "eli@example.com"}}),
            ({"name": "Faythe", "contact": {}})
        """);

        assertEquals("dana@example.com", result.getJSONObject(0).getJSONObject("contact").getString("email"));
        assertNull(result.getJSONObject(2).getJSONObject("contact").get("email"));
    }

    @Test
    public void testInsertWithArrayFieldAndNestedArray() {
        JSONTable table = new JSONTable("projects");

        JSONArray result = table.insert("""
            INSERT INTO projects VALUES 
            ({"name": "Apollo", "tasks": [{"id": 1}, {"id": 2}]} ),
            ({"name":  "Gemini", "tasks": []}),
            ( {"name": "Orion"})
        """);

        assertEquals("Apollo", result.getJSONObject(0).getString("name"));
        assertEquals(2, result.getJSONObject(0).getJSONArray("tasks").size());
        assertTrue(result.getJSONObject(1).getJSONArray("tasks").isEmpty());
        assertFalse(result.getJSONObject(2).containsKey("tasks"));
    }

    @Test
    public void testInsertMultipleValuesWithSpecialCharacters() {
        JSONTable table = new JSONTable("messages");

        JSONArray result = table.insert("""
            INSERT INTO messages VALUES 
            ( {"text": "Hello, world!"}),
            ({"text": "Goodbye (for now)."} ),
            ({"text": "Special chars: {}, [] and \\"quotes\\""} )
        """);

        assertEquals(3, result.size());
        assertTrue(result.getJSONObject(2).getString("text").contains("Special chars"));
    }





    @Test
    public void testInsertNestedProfileStructure() {
        JSONTable table = new JSONTable("users");

        JSONArray result = table.insert("""
            INSERT INTO users VALUES
            (
                {
                    "name": "Alice",
                    "profile": {
                        "contacts": {
                            "email": "alice@example.com",
                            "social": {
                                "twitter": "@alice",
                                "github": "alicehub"
                            }
                        },
                        "history": [
                            {"year": 2020, "action": "joined"},
                            {"year": 2021, "action": "promoted"}
                        ]
                    }
                }
            ),
            (
                {
                    "name": "Bob",
                    "profile": {
                        "contacts": {
                            "email": "bob@example.com",
                            "social": {
                                "twitter": "@bob"
                            }
                        },
                        "history": []
                    }
                }
            )
        """);

        assertEquals(2, result.size());

        JSONObject aliceProfile = result.getJSONObject(0).getJSONObject("profile");
        assertEquals("alice@example.com", aliceProfile.getJSONObject("contacts").getString("email"));
        assertEquals("alicehub", aliceProfile.getJSONObject("contacts").getJSONObject("social").getString("github"));
        assertEquals(2, aliceProfile.getJSONArray("history").size());

        JSONObject bobProfile = result.getJSONObject(1).getJSONObject("profile");
        assertEquals("@bob", bobProfile.getJSONObject("contacts").getJSONObject("social").getString("twitter"));
    }

    @Test
    public void testInsertWithNestedMatrix() {
        JSONTable table = new JSONTable("matrices");

        JSONArray result = table.insert("""
            INSERT INTO matrices VALUES
            (
                {
                    "id": 1,
                    "data": [
                        [[1, 2], [3, 4]],
                        [[5, 6], [7, 8]]
                    ]
                }
            ),
            (
                {
                    "id": 2,
                    "data": [
                        [[10], [20]],
                        [[30], [40]]
                    ]
                }
            )
        """);

        assertEquals(2, result.size());

        JSONArray matrix1 = result.getJSONObject(0).getJSONArray("data");
        assertEquals(2, matrix1.size());
        assertEquals(2, matrix1.getJSONArray(0).size());
        assertEquals(3, matrix1.getJSONArray(0).getJSONArray(1).getIntValue(0));

        JSONArray matrix2 = result.getJSONObject(1).getJSONArray("data");
        assertEquals(10, matrix2.getJSONArray(0).getJSONArray(0).getIntValue(0));
    }

    @Test
    public void testInsertDeepConfigurationTree() {
        JSONTable table = new JSONTable("configs");

        JSONArray result = table.insert("""
            INSERT INTO configs VALUES
            (
                {
                    "env": "prod",
                    "settings": {
                        "network": {
                            "timeout": {
                                "connect": 30,
                                "read": 60
                            },
                            "retry": {
                                "count": 5,
                                "strategy": "exponential"
                            }
                        },
                        "features": {
                            "logging": true,
                            "analytics": {
                                "enabled": true,
                                "providers": ["segment", "mixpanel"]
                            }
                        }
                    }
                }
            )
        """);

        JSONObject settings = result.getJSONObject(0).getJSONObject("settings");
        assertEquals(30, settings.getJSONObject("network").getJSONObject("timeout").getIntValue("connect"));
        assertEquals("exponential", settings.getJSONObject("network").getJSONObject("retry").getString("strategy"));
        assertTrue(settings.getJSONObject("features").getJSONObject("analytics").getJSONArray("providers").contains("mixpanel"));
    }

    @Test
    public void testInsertDeeplyNestedWithEmptyBranches() {
        JSONTable table = new JSONTable("trees");

        JSONArray result = table.insert("""
            INSERT INTO trees VALUES
            (
                {
                    "label": "root",
                    "children": [
                        {
                            "label": "branch1",
                            "children": [
                                {
                                    "label": "leaf1",
                                    "children": []
                                }
                            ]
                        },
                        {
                            "label": "branch2",
                            "children": []
                        }
                    ]
                }
            )
        """);

        JSONObject root = result.getJSONObject(0);
        JSONArray branches = root.getJSONArray("children");

        assertEquals("branch1", branches.getJSONObject(0).getString("label"));
        assertEquals("leaf1", branches.getJSONObject(0).getJSONArray("children").getJSONObject(0).getString("label"));
        assertEquals("branch2", branches.getJSONObject(1).getString("label"));
        assertTrue(branches.getJSONObject(1).getJSONArray("children").isEmpty());
    }












    @Test
    public void testFlatRowsSchema() {
        JSONTable table = new JSONTable("people");

        table.insertRaw("""
            {"id": 1, "name": "Alice", "age": 30}
        """);
        table.insertRaw("""
            {"id": 2, "email": "bob@example.com", "active": true}
        """);

        JSONObject schema = table.getSchema();
        assertEquals(5, schema.size());
        assertTrue(schema.containsKey("id"));
        assertTrue(schema.containsKey("name"));
        assertTrue(schema.containsKey("age"));
        assertTrue(schema.containsKey("email"));
        assertTrue(schema.containsKey("active"));
    }

    @Test
    public void testNestedObjectsSchema() {
        JSONTable table = new JSONTable("employees");

        table.insertRaw("""
            {
              "id": 1,
              "name": "Alice",
              "profile": {
                "title": "Engineer",
                "department": {
                  "name": "Engineering",
                  "floor": 5
                }
              }
            }
        """);

        table.insertRaw("""
            {
              "id": 2,
              "name": "Bob",
              "profile": {
                "title": "Manager",
                "department": {
                  "name": "HR",
                  "location": "Building B"
                },
                "extension": 42
              }
            }
        """);

        JSONObject schema = table.getSchema();
        assertTrue(schema.containsKey("id"));
        assertTrue(schema.containsKey("name"));
        assertTrue(schema.containsKey("profile"));

        JSONObject profile = schema.getJSONObject("profile");
        assertTrue(profile.containsKey("title"));
        assertTrue(profile.containsKey("extension"));
        assertTrue(profile.containsKey("department"));

        JSONObject dept = profile.getJSONObject("department");
        assertTrue(dept.containsKey("name"));
        assertTrue(dept.containsKey("floor"));
        assertTrue(dept.containsKey("location"));
    }

    @Test
    public void testSchemaWithArraysOfObjects() {
        JSONTable table = new JSONTable("projects");

        table.insertRaw("""
            {
              "id": 100,
              "team": [
                {"name": "Alice", "role": "dev"},
                {"name": "Bob", "role": "qa"}
              ]
            }
        """);

        table.insertRaw("""
            {
              "id": 101,
              "team": [
                {"name": "Carol", "role": "manager", "remote": true}
              ]
            }
        """);

        JSONObject schema = table.getSchema();
        assertTrue(schema.containsKey("id"));
        assertTrue(schema.containsKey("team"));

        JSONArray teamArray = schema.getJSONArray("team");
        assertFalse(teamArray.isEmpty());

        JSONObject teamSchema = teamArray.getJSONObject(0);
        assertTrue(teamSchema.containsKey("name"));
        assertTrue(teamSchema.containsKey("role"));
        assertTrue(teamSchema.containsKey("remote"));
    }

    @Test
    public void testMixedTypesAndPartialFields() {
        JSONTable table = new JSONTable("data");

        table.insertRaw("""
            {
              "id": 1,
              "metrics": [1, 2, 3],
              "info": {"type": "A"}
            }
        """);

        table.insertRaw("""
            {
              "id": 2,
              "metrics": [4, 5],
              "info": {"type": "B", "flag": true}
            }
        """);

        table.insertRaw("""
            {
              "id": 3,
              "metrics": [],
              "tags": ["alpha", "beta"]
            }
        """);

        JSONObject schema = table.getSchema();
        assertTrue(schema.containsKey("id"));
        assertTrue(schema.containsKey("metrics"));
        assertTrue(schema.containsKey("info"));
        assertTrue(schema.containsKey("tags"));

        JSONObject info = schema.getJSONObject("info");
        assertTrue(info.containsKey("type"));
        assertTrue(info.containsKey("flag"));

        JSONArray tags = schema.getJSONArray("tags");
        assertNotNull(tags);
    }



    @Test
    public void testQualifyMissingRootKeys() {
        JSONTable table = new JSONTable("people");
        table.insertRaw("{\"name\": \"Alice\"}");
        table.insertRaw("{\"name\": \"Bob\", \"age\": 30}");

        table.normalize();

        JSONObject row = table.get(0);
        assertTrue(row.containsKey("age"));
        assertNull(row.get("age"));
    }

    @Test
    public void testQualifyNestedObjectKeys() {
        JSONTable table = new JSONTable("users");
        table.insertRaw("{\"profile\": {\"name\": \"Alice\"}}");
        table.insertRaw("{\"profile\": {\"name\": \"Bob\", \"email\": \"bob@example.com\"}}");

        table.normalize();

        JSONObject row = table.get(0);
        JSONObject profile = row.getJSONObject("profile");
        assertTrue(profile.containsKey("email"));
        assertNull(profile.get("email"));
    }

    @Test
    public void testQualifyArrayOfObjects() {
        JSONTable table = new JSONTable("teams");
        table.insertRaw("{\"members\": [{\"name\": \"Alice\"}]}");
        table.insertRaw("{\"members\": [{\"name\": \"Bob\", \"role\": \"QA\"}]}");

        table.normalize();

        JSONArray members = table.get(0).getJSONArray("members");
        JSONObject first = members.getJSONObject(0);
        assertTrue(first.containsKey("role"));
        assertNull(first.get("role"));
    }

    @Test
    public void testAlreadyQualifiedRow() {
        JSONTable table = new JSONTable("people");
        table.insertRaw("{\"name\": \"Alice\", \"age\": 25}");
        table.insertRaw("{\"name\": \"Bob\", \"age\": 30}");

        table.normalize();

        JSONObject row = table.get(0);
        assertEquals("Alice", row.getString("name"));
        assertEquals(25, row.getIntValue("age"));
    }

    @Test
    public void testEmptyRowQualify() {
        JSONTable table = new JSONTable("empty");
        table.insertRaw("{}");
        table.insertRaw("{\"a\": 1, \"b\": 2}");

        table.normalize();

        JSONObject row = table.get(0);
        assertTrue(row.containsKey("a"));
        assertTrue(row.containsKey("b"));
        assertNull(row.get("a"));
        assertNull(row.get("b"));
    }






    @Test
    public void testInsertWrongTableThrows() {
        JSONTable table = new JSONTable("heroes");
        String query = "INSERT INTO villains VALUES ({\"name\": \"Joker\"})";
        assertThrows(IllegalArgumentException.class, () -> table.insert(query));
    }

    @Test
    public void testSelectWrongTableThrows() {
        JSONTable table = new JSONTable("heroes");
        String query = "SELECT * FROM villains WHERE name = 'Joker'";
        assertThrows(IllegalArgumentException.class, () -> table.select(query));
    }

    @Test
    public void testUpdateWrongTableThrows() {
        JSONTable table = new JSONTable("heroes");
        String query = "UPDATE villains SET evilness = 100 WHERE name = 'Joker'";
        assertThrows(IllegalArgumentException.class, () -> table.update(query));
    }

    @Test
    public void testDeleteWrongTableThrows() {
        JSONTable table = new JSONTable("heroes");
        String query = "DELETE FROM villains WHERE name = 'Joker'";
        assertThrows(IllegalArgumentException.class, () -> table.delete(query));
    }

    @Test
    public void testCorrectTableNameDoesNotThrow() {
        JSONTable table = new JSONTable("heroes");
        String query = "INSERT INTO heroes VALUES ({\"name\": \"Batman\"})";
        assertDoesNotThrow(() -> table.insert(query));
    }

}
