package jsonsql.test;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jsonsql.main.JSONDBDiskUtils;
import jsonsql.main.JSONFunctions;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class JSONDBDiskUtilsTest {
    @Test
    public void testJSONArrayOfJSONObjects() throws Exception {
        String json = """
            [
              { "name": "Alice", "age": 30 },
              { "name": "Bob", "age": 25 }
            ]
        """;

        File file = File.createTempFile("testJSONArrayOfJSONObjects", ".json");
        file.deleteOnExit();
        Files.writeString(file.toPath(), json);

        List<JSONObject> results = new ArrayList<>();
        JSONDBDiskUtils.streamJSONObjectElements(file, results::add);

        assertEquals(2, results.size());
        assertEquals("Alice", results.get(0).getString("name"));
        assertEquals(25, results.get(1).getIntValue("age"));
        assertNull(results.get(0).get("_parent_key"));
    }

    @Test
    public void testJSONObjectOfJSONObjects() throws Exception {
        String json = """
            {
              "alice": { "name": "Alice", "age": 30 },
              "bob": { "name": "Bob", "age": 25 }
            }
        """;

        File file = File.createTempFile("testJSONObjectOfJSONObjects", ".json");
        file.deleteOnExit();
        Files.writeString(file.toPath(), json);

        List<JSONObject> results = new ArrayList<>();
        JSONDBDiskUtils.streamJSONObjectElements(file, results::add);

        assertEquals(2, results.size());
        List<String> names = results.stream().map(o -> o.getString("name")).toList();
        assertTrue(names.contains("Alice"));
        assertTrue(names.contains("Bob"));

        for (JSONObject obj : results) {
            assertTrue(obj.containsKey("_parent_key"));
            assertTrue(List.of("alice", "bob").contains(obj.getString("_parent_key")));
        }
    }



    @Test
    public void testEmptyFile() throws Exception {
        File file = File.createTempFile("empty", ".json");
        file.deleteOnExit();
        Files.writeString(file.toPath(), "");

        List<JSONObject> results = new ArrayList<>();
        JSONDBDiskUtils.streamJSONObjectElements(file, results::add);
        assertEquals(0, results.size());
    }

    @Test
    public void testInvalidJSON() throws Exception {
        File file = File.createTempFile("invalid", ".json");
        file.deleteOnExit();
        Files.writeString(file.toPath(), "this is not json");

        List<JSONObject> results = new ArrayList<>();
        JSONDBDiskUtils.streamJSONObjectElements(file, results::add);
        assertEquals(0, results.size());
    }

    @Test
    public void testUnsupportedTopLevelPrimitive() throws Exception {
        File file = File.createTempFile("primitive", ".json");
        file.deleteOnExit();
        Files.writeString(file.toPath(), "123");

        List<JSONObject> results = new ArrayList<>();
        JSONDBDiskUtils.streamJSONObjectElements(file, results::add);
        assertEquals(0, results.size());
    }

    @Test
    public void testEmptyJSONArray() throws Exception {
        File file = File.createTempFile("emptyArray", ".json");
        file.deleteOnExit();
        Files.writeString(file.toPath(), "[]");

        List<JSONObject> results = new ArrayList<>();
        JSONDBDiskUtils.streamJSONObjectElements(file, results::add);

        assertEquals(0, results.size());
    }

    @Test
    public void testEmptyJSONObject() throws Exception {
        File file = File.createTempFile("emptyObject", ".json");
        file.deleteOnExit();
        Files.writeString(file.toPath(), "{}");

        List<JSONObject> results = new ArrayList<>();
        JSONDBDiskUtils.streamJSONObjectElements(file, results::add);

        assertEquals(0, results.size());
    }

    @Test
    public void testJSONObjectWithNonObjectValues() throws Exception {
        String json = """
            {
              "group1": 123,
              "group2": "hello",
              "group3": [ { "name": "Valid" } ]
            }
        """;

        File file = File.createTempFile("nonObjects", ".json");
        file.deleteOnExit();
        Files.writeString(file.toPath(), json);

        List<JSONObject> results = new ArrayList<>();
        JSONDBDiskUtils.streamJSONObjectElements(file, results::add);

        assertEquals(0, results.size());
    }
    @Test
    public void testJSONArrayWithOnlyPrimitives() throws Exception {
        String json = "[1, \"text\", true, null]";
        File file = File.createTempFile("arrayWithPrimitives", ".json");
        file.deleteOnExit();
        Files.writeString(file.toPath(), json);

        List<JSONObject> results = new ArrayList<>();
        JSONDBDiskUtils.streamJSONObjectElements(file, results::add);

        assertEquals(0, results.size());
    }






    private File createTempFile(String content) throws Exception {
        File temp = File.createTempFile("checksum_test_", ".txt");
        temp.deleteOnExit();
        try (FileWriter writer = new FileWriter(temp, StandardCharsets.UTF_8)) {
            writer.write(content);
        }
        return temp;
    }

    @Test
    public void testChecksumConsistency() throws Exception {
        File file1 = createTempFile("hello world");
        File file2 = createTempFile("hello world");

        String checksum1 = JSONDBDiskUtils.getChecksum(file1);
        String checksum2 = JSONDBDiskUtils.getChecksum(file2);

        assertEquals(checksum1, checksum2, "Checksums should match for identical files");
    }

    @Test
    public void testChecksumDifference() throws Exception {
        File file1 = createTempFile("hello world");
        File file2 = createTempFile("goodbye world");

        String checksum1 = JSONDBDiskUtils.getChecksum(file1);
        String checksum2 = JSONDBDiskUtils.getChecksum(file2);

        assertNotEquals(checksum1, checksum2, "Checksums should differ for different contents");
    }

    @Test
    public void testEmptyFileChecksum() throws Exception {
        File emptyFile = createTempFile("");
        String checksum = JSONDBDiskUtils.getChecksum(emptyFile);

        assertNotNull(checksum, "Checksum should not be null");
        assertEquals(64, checksum.length(), "SHA-256 checksum should be 64 hex characters");
    }

    @Test
    public void testChecksumRepeatability() throws Exception {
        File file = createTempFile("repeatable content");
        String checksum1 = JSONDBDiskUtils.getChecksum(file);
        String checksum2 = JSONDBDiskUtils.getChecksum(file);

        assertEquals(checksum1, checksum2, "Checksum should be consistent across multiple calls");
    }



    @Test
    public void testConvertArrayOfObjects() throws Exception {
        String json = """
            [
              { "name": "Alice", "age": 30 },
              { "name": "Bob", "age": 25 }
            ]
        """;

        File inputFile = File.createTempFile("arrayObjects", ".json");
        File outputFile = File.createTempFile("arrayObjects", ".ndjson");
        inputFile.deleteOnExit();
        outputFile.deleteOnExit();

        Files.writeString(inputFile.toPath(), json);
        JSONDBDiskUtils.convertToNDJSON(inputFile, outputFile);

        List<String> lines = Files.readAllLines(outputFile.toPath());
        assertEquals(2, lines.size());

        JSONObject obj0 = JSON.parseObject(lines.get(0));
        assertEquals("Alice", obj0.getString("name"));
        assertEquals(30, obj0.getIntValue("age"));

        JSONObject obj1 = JSON.parseObject(lines.get(1));
        assertEquals("Bob", obj1.getString("name"));
        assertEquals(25, obj1.getIntValue("age"));
    }

    @Test
    public void testConvertObjectOfObjects() throws Exception {
        String json = """
            {
              "id1": { "name": "Carol" },
              "id2": { "name": "Dan" }
            }
        """;

        File inputFile = File.createTempFile("objectObjects", ".json");
        File outputFile = File.createTempFile("objectObjects", ".ndjson");
        inputFile.deleteOnExit();
        outputFile.deleteOnExit();

        Files.writeString(inputFile.toPath(), json);
        JSONDBDiskUtils.convertToNDJSON(inputFile, outputFile);

        List<String> lines = Files.readAllLines(outputFile.toPath());
        assertEquals(2, lines.size());

        for (String line : lines) {
            JSONObject obj = JSON.parseObject(line);
            assertTrue(obj.containsKey("name"));
            assertTrue(obj.containsKey("_parent_key"));
        }
    }

    @Test
    public void testInvalidStructureProducesNoLines() throws Exception {
        String json = """
            "This is not valid"
        """;

        File inputFile = File.createTempFile("invalid", ".json");
        File outputFile = File.createTempFile("invalid", ".ndjson");
        inputFile.deleteOnExit();
        outputFile.deleteOnExit();

        Files.writeString(inputFile.toPath(), json);
        JSONDBDiskUtils.convertToNDJSON(inputFile, outputFile);

        List<String> lines = Files.readAllLines(outputFile.toPath());
        assertEquals(0, lines.size());
    }





    @Test
    public void testLastLineInNDJSONFile() throws Exception {
        File file = File.createTempFile("ndjson_last", ".ndjson");
        file.deleteOnExit();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("{\"id\":1,\"name\":\"Alice\"}\n");
            writer.write("{\"id\":2,\"name\":\"Bob\"}\n");
            writer.write("{\"id\":3,\"name\":\"Charlie\"}\n");
        }

        JSONObject last = JSONDBDiskUtils.readLastNDJSONLine(file);

        assertNotNull(last);
        assertEquals(3, last.getIntValue("id"));
        assertEquals("Charlie", last.getString("name"));
    }

    @Test
    public void testEmptyFileReturnsNull() throws Exception {
        File file = File.createTempFile("ndjson_empty", ".ndjson");
        file.deleteOnExit();

        JSONObject last = JSONDBDiskUtils.readLastNDJSONLine(file);
        assertNull(last);
    }

    @Test
    public void testFileWithTrailingNewline() throws Exception {
        File file = File.createTempFile("ndjson_trailing", ".ndjson");
        file.deleteOnExit();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("{\"id\":1}\n");
            writer.write("{\"id\":2}\n\n");
        }

        JSONObject last = JSONDBDiskUtils.readLastNDJSONLine(file);
        assertNotNull(last);
        assertEquals(2, last.getIntValue("id"));
    }




    @Test
    public void testConvertJsonArrayToNDJSONWithFooter() throws Exception {
        File input = File.createTempFile("tmp_array", ".json");
        File output = File.createTempFile("testArray", ".ndjson");
        input.deleteOnExit();
        output.deleteOnExit();

        String jsonArray = """
        [
          { "id": 1 },
          { "id": 2 },
          { "id": 3 }
        ]
        """;

        Files.writeString(input.toPath(), jsonArray);

        JSONDBDiskUtils.convertToNDJSONWithFooter(input, output);

        List<String> lines = Files.readAllLines(output.toPath());
        assertEquals(4, lines.size()); // 3 rows + 1 footer

        for (int i = 0; i < 3; i++) {
            JSONObject obj = JSONObject.parseObject(lines.get(i));
            assertEquals(i + 1, obj.getIntValue("id"));
        }

        JSONObject footer = JSONObject.parseObject(lines.getLast());
        assertTrue(footer.containsKey("_footer"));
        assertEquals(3, footer.getJSONObject("_footer").getJSONArray("offsets").size());
    }



    @Test
    public void testConvertJsonObjectToNDJSONWithFooter() throws Exception {
        File input = File.createTempFile("tmp_object", ".json");
        File output = File.createTempFile("testObject", ".ndjson");
        input.deleteOnExit();
        output.deleteOnExit();

        String jsonObject = """
        {
          "user1": { "id": 101 },
          "user2": { "id": 102 }
        }
        """;

        Files.writeString(input.toPath(), jsonObject);

        JSONDBDiskUtils.convertToNDJSONWithFooter(input, output);

        List<String> lines = Files.readAllLines(output.toPath());
        assertEquals(3, lines.size()); // 2 rows + 1 footer

        List<JSONObject> objects = lines.subList(0, 2).stream()
                .map(JSONObject::parseObject)
                .toList();

        assertTrue(objects.stream().anyMatch(o -> o.getIntValue("id") == 101 && "user1".equals(o.getString("_parent_key"))));
        assertTrue(objects.stream().anyMatch(o -> o.getIntValue("id") == 102 && "user2".equals(o.getString("_parent_key"))));

        JSONObject footer = JSONObject.parseObject(lines.getLast());
        assertTrue(footer.containsKey("_footer"));
        assertEquals(2, footer.getJSONObject("_footer").getJSONArray("offsets").size());
    }






    @Test
    public void testReadRowsUsingFooterOffsets() throws Exception {
        // Prepare input JSON array
        String jsonArray = """
        [
          { "id": 1, "name": "Alice" },
          { "id": 2, "name": "Bob" },
          { "id": 3, "name": "Charlie" }
        ]
        """;

        File input = File.createTempFile("test_array", ".json");
        Files.writeString(input.toPath(), jsonArray, StandardCharsets.UTF_8);
        File output = File.createTempFile("test_array_output", ".ndjson");

        // Convert to NDJSON with footer
        JSONDBDiskUtils.convertToNDJSONWithFooter(input, output);

        // Read all lines to grab the footer
        List<String> lines = Files.readAllLines(output.toPath());
        JSONObject footer = JSONObject.parseObject(lines.getLast());
        JSONArray offsets = footer.getJSONArray("offsets");

        // Re-read rows using footer offsets
        try (RandomAccessFile raf = new RandomAccessFile(output, "r")) {
            for (int i = 0; i < offsets.size(); i++) {
                long offset = offsets.getLongValue(i);
                raf.seek(offset);
                String line = raf.readLine();
                JSONObject obj = JSONObject.parseObject(line);
                assertEquals(i + 1, obj.getIntValue("id"));
            }
        }
    }
}
