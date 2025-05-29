package jsonsql.main;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class JSONDBDiskUtils {
    /**
     * Streams each JSONObject from a JSON file with one of these structures:
     * - A JSON array of objects:           [ {...}, {...}, ... ]
     * - A JSON object of objects:          { "id1": {...}, "id2": {...}, ... }
     *
     * @param inputFile The input file containing a valid JSON structure
     * @param consumer  The consumer callback to receive each JSONObject
     */
    public static void streamJSONObjectElements(File inputFile, Consumer<JSONObject> consumer) {
        InputStream in = null;
        JSONReader reader = null;

        try {
            in = new FileInputStream(inputFile);
            reader = JSONReader.of(in, StandardCharsets.UTF_8);

            // Case 1: JSON array of mixed values
            if (reader.nextIfMatch('[')) {
                while (!reader.nextIfMatch(']')) {
                    if (reader.isObject()) {
                        JSONObject obj = reader.read(JSONObject.class);
                        consumer.accept(obj);
                    } else {
                        reader.skipValue(); // skip non-JSONObject
                    }
                    reader.nextIfMatch(','); // tolerate commas
                }

                // Case 2: JSON object of mixed values
            } else if (reader.nextIfMatch('{')) {
                while (!reader.nextIfMatch('}')) {
                    String key = reader.readFieldName();
                    if (reader.isObject()) {
                        JSONObject obj = reader.read(JSONObject.class);
                        obj.put("_parent_key", key);
                        consumer.accept(obj);
                    } else {
                        reader.skipValue(); // skip non-JSONObject
                    }
                    reader.nextIfMatch(','); // tolerate commas
                }
            }

        } catch (Exception ignored) {
            // Swallow any errors (optional: log or count skips)
        } finally {
            try { if (reader != null) reader.close(); } catch (Exception ignored) {}
            try { if (in != null) in.close(); } catch (Exception ignored) {}
        }
    }

    public static String getChecksum(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] buffer = new byte[8192];
        int bytesRead;

        try (InputStream fis = new FileInputStream(file)) {
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        byte[] hashBytes = digest.digest();
        StringBuilder hex = new StringBuilder();
        for (byte b : hashBytes) {
            hex.append(String.format("%02x", b));
        }

        return hex.toString();
    }


    public static void convertToNDJSONWithFooter(File inputFile, File outputFile) throws IOException {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        BufferedWriter writer = null;

        List<Long> rowOffsets = new ArrayList<>();
        final long[] currentOffset = {0};

        try {
            fos = new FileOutputStream(outputFile);
            osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            writer = new BufferedWriter(osw);

            BufferedWriter finalWriter = writer;
            streamJSONObjectElements(inputFile, obj -> {
                try {
                    String line = obj.toJSONString();
                    byte[] lineBytes = (line + "\n").getBytes(StandardCharsets.UTF_8);
                    rowOffsets.add(currentOffset[0]);
                    finalWriter.write(line);
                    finalWriter.newLine();
                    currentOffset[0] += lineBytes.length;
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });

            writer.flush(); // flush before footer

            JSONObject footer = new JSONObject();
            footer.put("type", "_footer");
            footer.put("version", "1.0");
            footer.put("created_at", System.currentTimeMillis());
            footer.put("offsets", rowOffsets);

            String footerLine = footer.toJSONString();
            writer.write(footerLine);
            writer.newLine();

        } catch (UncheckedIOException e) {
            throw e.getCause();
        } finally {
            if (writer != null) try { writer.close(); } catch (IOException ignored) {}
            if (osw != null) try { osw.close(); } catch (IOException ignored) {}
            if (fos != null) try { fos.close(); } catch (IOException ignored) {}
        }
    }

    /**
     * Converts a JSON file (array or object of objects) into NDJSON format.
     *
     * Each object is written as a single line of JSON in the output file.
     * "_parent_key" is preserved if present from object-of-objects.
     *
     * @param inputFile  The source JSON file (array or object of objects)
     * @param outputFile The destination NDJSON file to write
     * @throws IOException if any I/O error occurs
     */
    public static void convertToNDJSON(File inputFile, File outputFile) throws IOException {
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8));

            BufferedWriter finalWriter = writer;
            streamJSONObjectElements(inputFile, obj -> {
                try {
                    finalWriter.write(obj.toJSONString());
                    finalWriter.newLine();
                } catch (IOException e) {
                    // Wrap and rethrow to propagate to caller
                    throw new UncheckedIOException(e);
                }
            });

        } catch (UncheckedIOException e) {
            throw e.getCause(); // unwrap wrapped IOExceptions from the consumer
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {}
            }
        }
    }

    public static JSONObject readLastNDJSONLine(File file) throws Exception {
        RandomAccessFile raf = null;
        JSONObject result = null;
        try {
            raf = new RandomAccessFile(file, "r");
            long length = raf.length();
            if (length == 0) return null;

            long pointer = length - 1;
            StringBuilder sb = new StringBuilder();

            // First: skip trailing newlines and whitespace
            while (pointer >= 0) {
                raf.seek(pointer);
                char c = (char) raf.read();
                if (c != '\n' && c != '\r' && !Character.isWhitespace(c)) break;
                pointer--;
            }

            // Then: collect the last non-empty line
            while (pointer >= 0) {
                raf.seek(pointer);
                char c = (char) raf.read();
                if (c == '\n') break;
                sb.insert(0, c);
                pointer--;
            }

            String lastLine = sb.toString().trim();
            result = lastLine.isEmpty() ? null : JSONObject.parseObject(lastLine);
        } finally {
            if (raf != null) {
                try { raf.close(); } catch (IOException ignored) {}
            }
        }
        return result;
    }
}
