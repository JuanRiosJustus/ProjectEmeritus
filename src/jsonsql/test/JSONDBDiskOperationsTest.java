package jsonsql.test;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jsonsql.main.JSONDBDiskOperations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class JSONDBDiskOperationsTest {

    private File tempFile;
    private JSONDBDiskOperations diskOps;

    @Before
    public void setUp() throws Exception {
        tempFile = File.createTempFile("test_footer", ".jsondb");
        diskOps = new JSONDBDiskOperations();
    }

    @After
    public void tearDown() {
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }
    @Test
    public void testValidFooterOffset() throws Exception {
        try (RandomAccessFile raf = new RandomAccessFile(tempFile, "rw")) {
            raf.write("some JSON footer\n".getBytes());
            long offset = raf.getFilePointer();
            raf.write((offset + "\n").getBytes());
        }

        long result = diskOps.readFooterOffset(tempFile);
        assertTrue(result > 0);
    }

    @Test
    public void testTrailingWhitespaceFooter() throws Exception {
        try (RandomAccessFile raf = new RandomAccessFile(tempFile, "rw")) {
            raf.write("footer JSON here\n".getBytes());
            long offset = raf.getFilePointer();
            raf.write((offset + "\n   \n\t\n").getBytes()); // Add trailing whitespace
        }

        long result = diskOps.readFooterOffset(tempFile);
        assertTrue(result > 0);
    }

    @Test
    public void testMissingFooterNumber() throws Exception {
        try (RandomAccessFile raf = new RandomAccessFile(tempFile, "rw")) {
            raf.write("no footer offset\n".getBytes());
        }

        long result = diskOps.readFooterOffset(tempFile);
        assertEquals(-1, result);
    }

    @Test
    public void testFooterAtStartOfFile2() throws Exception {
        try (RandomAccessFile raf = new RandomAccessFile(tempFile, "rw")) {
            raf.write("0\n".getBytes()); // footer is at byte 0
        }

        long result = diskOps.readFooterOffset(tempFile);
        assertEquals(0, result);
    }





    @Test
    public void testValidFooterRead() throws Exception {
        JSONObject jsonObject = new JSONObject(Map.of(
                "offsets", new JSONArray(List.of(0, 12)),
                "index", new JSONObject(Map.of("id:1", 0)),
                "nextRowId", 3
        ));
        try (RandomAccessFile raf = new RandomAccessFile(tempFile, "rw")) {
            raf.write("row1\n".getBytes());
            raf.write("row2\n".getBytes());

            long footerOffset = raf.getFilePointer();
            raf.write((jsonObject + "\n").getBytes());
            raf.write((footerOffset + "\n").getBytes());
        }

        JSONObject footer = diskOps.readFooter(tempFile);
        assertNotNull(footer);
        assertEquals(3, footer.getIntValue("nextRowId"));
        assertEquals(2, footer.getJSONArray("offsets").size());
        assertEquals(0, footer.getJSONArray("offsets").getIntValue(0));
        assertEquals(12, footer.getJSONArray("offsets").getIntValue(1));
    }

    @Test
    public void testFooterWithTrailingWhitespace() throws Exception {
        JSONObject jsonObject = new JSONObject(Map.of(
                "offsets", new JSONArray(List.of(0)),
                "index", new JSONObject(),
                "nextRowId", 2
        ));
        try (RandomAccessFile raf = new RandomAccessFile(tempFile, "rw")) {
            raf.write("data\n".getBytes());
            long footerOffset = raf.getFilePointer();
            raf.write((jsonObject + "\n").getBytes());
            raf.write((footerOffset + "\n   \n\n\t").getBytes());
        }

        JSONObject footer = diskOps.readFooter(tempFile);
        assertEquals(2, footer.getIntValue("nextRowId"));
    }

    @Test
    public void testInvalidFooterReturnsEmptyObject() throws Exception {
        try (RandomAccessFile raf = new RandomAccessFile(tempFile, "rw")) {
            raf.write("corrupt\n".getBytes());
        }

        JSONObject footer = diskOps.readFooter(tempFile);
        assertTrue(footer.isEmpty());
    }

    @Test
    public void testFooterAtStartOfFile() throws Exception {
        JSONObject jsonObject = new JSONObject(Map.of(
                "offsets", new JSONArray(),
                "index", new JSONObject(),
                "nextRowId", 1
        ));
        try (RandomAccessFile raf = new RandomAccessFile(tempFile, "rw")) {
            raf.write((jsonObject + "\n").getBytes());
            raf.write("0".getBytes());
        }

        JSONObject footer = diskOps.readFooter(tempFile);
        assertEquals(1, footer.getIntValue("nextRowId"));
        assertTrue(footer.getJSONArray("offsets").isEmpty());
    }





    @Test
    public void testTryCreatingNewDbFile_createsValidFooter() throws Exception {
        String dbPath = tempFile.getAbsolutePath();
        diskOps.tryCreatingNewDbFile(dbPath);

        File created = new File(dbPath);
        assertTrue(created.exists());

        JSONObject footer = diskOps.readFooter(created);
        assertNotNull(footer);
        assertTrue(footer.containsKey("offsets"));
        assertTrue(footer.containsKey("index"));
        assertTrue(footer.containsKey("nextRowId"));

        assertEquals(1, footer.getIntValue("nextRowId"));
        assertTrue(footer.getJSONArray("offsets").isEmpty());
        assertEquals(new JSONObject(), footer.getJSONObject("index"));
    }

    @Test
    public void testTryCreatingNewDbFile_doesNotOverwriteExistingFile() throws Exception {
        String dbPath = tempFile.getAbsolutePath();

        // Manually write to file
        Files.write(tempFile.toPath(), "PREEXISTING".getBytes());

        diskOps.tryCreatingNewDbFile(dbPath); // should not overwrite

        String contents = new String(Files.readAllBytes(tempFile.toPath()));
        assertTrue(contents.contains("PREEXISTING"));
    }




    @Test
    public void testInsertSingleRow() {
        diskOps.tryCreatingNewDbFile(tempFile.getAbsolutePath());
        JSONObject row = new JSONObject();
        row.put("name", "Alice");
        row.put("level", 10);

        JSONObject inserted = diskOps.insert(tempFile.getAbsolutePath(), row);
        assertNotNull(inserted);
        assertTrue(inserted.containsKey("id"));

        JSONObject footer = diskOps.readFooter(tempFile);
        assertNotNull(footer);

        JSONArray offsets = footer.getJSONArray("offsets");
        assertEquals(1, offsets.size());
        assertTrue(footer.getJSONObject("index").containsKey("id:" + inserted.getString("id")));
    }

    @Test
    public void testInsertMultipleRows() {
        diskOps.tryCreatingNewDbFile(tempFile.getAbsolutePath());
        for (int i = 0; i < 5; i++) {
            JSONObject row = new JSONObject();
            row.put("name", "Unit" + i);
            row.put("power", i * 5);
            diskOps.insert(tempFile.getAbsolutePath(), row);
        }

        JSONObject footer = diskOps.readFooter(tempFile);
        assertEquals(5, footer.getJSONArray("offsets").size());
        assertEquals(6, footer.getLongValue("nextRowId")); // should be 6 after 5 inserts
    }

    @Test
    public void testManualIdInsertUpdatesNextRowId() {
        diskOps.tryCreatingNewDbFile(tempFile.getAbsolutePath());
        JSONObject row = new JSONObject();
        row.put("id", 999);
        row.put("name", "Boss");
        diskOps.insert(tempFile.getAbsolutePath(), row);

        JSONObject footer = diskOps.readFooter(tempFile);
        assertEquals(1000, footer.getLongValue("nextRowId"));
        assertTrue(footer.getJSONObject("index").containsKey("id:999"));
    }






}
