package jsonsql.main;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

public class JSONDBDiskOperations {

    private static final String READ_ONLY = "r";
    private static final String READ_WRITE_ONLY = "rw";
    private static final String NEW_LINE_CHARACTER = "\n";

    /**
     * Reads the footer of a jsondb file from ignoring whitespace, until it
     * reaches digits, in which it terminates at the first non-digit character.
     * @param dbFile input to read footer for
     * @return footer offset value
     */
    public long readFooterOffset(File dbFile) {
        long footerOffset = -1;
        try {
            RandomAccessFile raf = new RandomAccessFile(dbFile, READ_ONLY);
            long pointer = raf.length() - 1;
            StringBuilder digitsReversed = new StringBuilder();

            // 1. Skip any trailing whitespace
            while (pointer >= 0) {
                raf.seek(pointer);
                char ch = (char) raf.read();
                if (!Character.isWhitespace(ch)) {
                    break;
                }
                pointer--;
            }

            // 2. Read digits in reverse
            while (pointer >= 0) {
                raf.seek(pointer);
                char ch = (char) raf.read();

                if (Character.isDigit(ch)) {
                    digitsReversed.append(ch);
                } else {
                    break; // Stop at first non-digit
                }
                pointer--;
            }

            if (digitsReversed.isEmpty()) {
                throw new IOException("No numeric footer offset found.");
            }

            String digits = digitsReversed.reverse().toString();
            raf.close();
            footerOffset = Long.parseLong(digits);
        } catch (Exception ex) {
            ex.printStackTrace();
            footerOffset = -1;
        }
        return footerOffset;
    }

    /**
     * Given a file input, reads the entire footer, excluding the trailing offset value
     * @param dbFile file representing the database
     * @return footer representing the dbFile
     */
    public JSONObject readFooter(File dbFile) {
        JSONObject result = null;
        try {
            RandomAccessFile raf = new RandomAccessFile(dbFile, READ_ONLY);
            long footerOffset = readFooterOffset(dbFile); // accurate byte offset
            raf.seek(footerOffset);
            // Read the footer line (just the JSON)
            String jsonFooterLine = raf.readLine();  // only reads the first line
            result =  JSON.parseObject(jsonFooterLine);
            raf.close();;
        } catch (Exception ex) {
            result = new JSONObject();
        }
        return result;
    }


    /**
     * Attempts to create a new dbFile if no such file exists!
     * @param path
     */
    public void tryCreatingNewDbFile(String path) {
        try (RandomAccessFile raf = new RandomAccessFile(path, READ_WRITE_ONLY)) {
            if (raf.length() > 0) {
                return; // File already initialized
            }

            // Create initial footer JSON
            JSONObject header = new JSONObject();
            header.put("offsets", new JSONArray());
            header.put("index", new JSONObject());
            header.put("nextRowId", 1);

            // Serialize footer
            byte[] footerBytes = header.toJSONString().getBytes(StandardCharsets.UTF_8);
            long headerOffset = 0; // Will be the start of header in empty file

            // Write footer
            raf.write(footerBytes);
            raf.write("\n".getBytes(StandardCharsets.UTF_8));

            // Write footer offset
            String marker = headerOffset + NEW_LINE_CHARACTER;
            raf.write(marker.getBytes(StandardCharsets.UTF_8));

            System.out.println(">>> [DEBUG] Created file with header at offset " + headerOffset);

        } catch (IOException e) {
            System.err.println(">>> [ERROR] Failed to create DB file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public JSONObject insert(String tablePath, JSONObject row) {
        File file = new File(tablePath);

        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            // 1. Read footer offset from end of file
            long footerOffset = readFooterOffset(file);

            // 2. Load existing footer JSON
            raf.seek(footerOffset);
            String footerJsonLine = raf.readLine();
            if (footerJsonLine == null || footerJsonLine.trim().isEmpty()) {
                throw new IOException("Missing or empty footer JSON.");
            }
            JSONObject footer = JSON.parseObject(footerJsonLine);
            JSONArray offsets = footer.getJSONArray("offsets");
            JSONObject index = footer.getJSONObject("index");
            long mNextRowId = footer.getLongValue("nextRowId");

            if (offsets == null) offsets = new JSONArray();
            if (index == null) index = new JSONObject();

            // 3. Move to end of file (append-only mode!)
            raf.seek(file.length());

            // 4. Prepare new row
            if (!row.containsKey("id")) {
                row.put("id", mNextRowId++);
            } else {
                long manualId = row.getLongValue("id");
                mNextRowId = Math.max(mNextRowId, manualId + 1);
            }

            long rowOffset = raf.getFilePointer();
            String rowStr = row.toJSONString();
            raf.write(rowStr.getBytes(StandardCharsets.UTF_8));
            raf.write("\n".getBytes(StandardCharsets.UTF_8));

            // 5. Update footer (in memory)
            offsets.add(rowOffset);
            index.put("id:" + row.getString("id"), rowOffset);

            JSONObject updatedFooter = new JSONObject();
            updatedFooter.put("offsets", offsets);
            updatedFooter.put("index", index);
            updatedFooter.put("nextRowId", mNextRowId);
            byte[] updatedFooterBytes = updatedFooter.toJSONString().getBytes(StandardCharsets.UTF_8);

            // 6. Write updated footer and new offset (at the very end)
            long newFooterOffset = raf.getFilePointer();
            raf.write(updatedFooterBytes);
            raf.write("\n".getBytes(StandardCharsets.UTF_8));
            raf.write(String.valueOf(newFooterOffset).getBytes(StandardCharsets.UTF_8));
            raf.write("\n".getBytes(StandardCharsets.UTF_8));

            System.out.println(">>> [DEBUG] Appended row at offset " + rowOffset + ", new footer at " + newFooterOffset);

        } catch (Exception e) {
            System.err.println(">>> [ERROR] Failed to insert row: " + e.getMessage());
            e.printStackTrace();
        }

        return row;
    }


//    public JSONObject insert(String tablePath, JSONObject row) {
//        File file = new File(tablePath);
//
//        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
//            // 1. Read footer offset from end of file
//            long footerOffset = readFooterOffset(file);
//
//            // 2. Load footer JSON (only read one line)
//            raf.seek(footerOffset);
//            String footerJsonLine = raf.readLine();
//            if (footerJsonLine == null || footerJsonLine.trim().isEmpty()) {
//                throw new IOException("Missing or empty footer JSON.");
//            }
//            JSONObject footer = JSON.parseObject(footerJsonLine);
//            JSONArray offsets = footer.getJSONArray("offsets");
//            JSONObject index = footer.getJSONObject("index");
//            long mNextRowId = footer.getLongValue("nextRowId");
//
//            if (offsets == null) offsets = new JSONArray();
//            if (index == null) index = new JSONObject();
//
//            // 3. Truncate file before footer
//            raf.setLength(footerOffset);
//            raf.seek(footerOffset);
//
//            // 4. Prepare new row
//            if (!row.containsKey("id")) {
//                row.put("id", mNextRowId++);
//            } else {
//                long manualId = row.getLongValue("id");
//                mNextRowId = Math.max(mNextRowId, manualId + 1);
//            }
//
//            long rowOffset = raf.getFilePointer();
//            String rowStr = row.toJSONString();
//            raf.write(rowStr.getBytes(StandardCharsets.UTF_8));
//            raf.write("\n".getBytes(StandardCharsets.UTF_8));
//
//            // 5. Update footer
//            offsets.add(rowOffset);
//            index.put("id:" + row.getString("id"), rowOffset);
//
//            JSONObject updatedFooter = new JSONObject();
//            updatedFooter.put("offsets", offsets);
//            updatedFooter.put("index", index);
//            updatedFooter.put("nextRowId", mNextRowId);
//            byte[] updatedFooterBytes = updatedFooter.toJSONString().getBytes(StandardCharsets.UTF_8);
//
//            // 6. Write updated footer and its new offset
//            long newFooterOffset = raf.getFilePointer();
//            raf.write(updatedFooterBytes);
//            raf.write("\n".getBytes(StandardCharsets.UTF_8));
//            raf.write(String.valueOf(newFooterOffset).getBytes(StandardCharsets.UTF_8));
//            raf.write("\n".getBytes(StandardCharsets.UTF_8));
//
//            System.out.println(">>> [DEBUG] Wrote row at offset " + rowOffset + " and footer at " + newFooterOffset);
//            System.out.println(">>> [DEBUG] New footer: " + updatedFooter.toJSONString());
//
//        } catch (Exception e) {
//            System.err.println(">>> [ERROR] Failed to insert row: " + e.getMessage());
//            e.printStackTrace();
//        }
//
//        return row;
//    }
}
