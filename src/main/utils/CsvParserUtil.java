package main.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class CsvParserUtil {

    public CsvParserUtil(String file) {
        try {
            List<String> rows = Files.readAllLines(Path.of(file));
            StringBuilder builder = new StringBuilder();
            List<String> sanitized;
            // For each line in csv, check each column
            for(String row : rows) {
                sanitized = new ArrayList<>();
                builder.append(row.trim());
                while (builder.length() > 0) {
                    String value = null;
                    // if the column starts with quotes, get next quote
                    // and store everything in between as column value
                    if (builder.charAt(0) == '\"') {
                        builder.deleteCharAt(0);
                        int nextIndex = builder.indexOf("\"");
                        value = builder.substring(0, nextIndex);
                        builder.delete(0, nextIndex);
                    } else {
                        int nextIndex = builder.indexOf(",");
                        if (nextIndex >= 0) {
                            value = builder.substring(0, nextIndex);
                            builder.delete(0, nextIndex + 1);
                        } else {
                            value = builder.toString();
                            builder.delete(0, builder.length());
                        }
                    }
                    sanitized.add(value);
                }
                String[] columns = row.split(",");
//                for (String column : columns) {
//                    // clean builder
//                    builder.delete(0, builder.length());
//                    // add column
//                    builder.append(column.trim());
//                    // if column starts with quotes, get last quote, if not, add column to row
//                    if (builder.charAt(0) == '"') {
//                        sanitized.add(builder)
//                    } else {
//                        sanitized.add(builder.toString());
//                    }
//                }
                System.out.println(row);
            }
        } catch (IOException ex) {
            System.out.format("I/O error: %s%n", ex);
        }
    }
}
