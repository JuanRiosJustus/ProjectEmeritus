package game.stores.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvReader {

    private final List<Map<String, String>> rows = new ArrayList<>();

    public CsvReader(String file, String delimiter) throws Exception {
        String raw;
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while ((raw = reader.readLine()) != null) {
            String[] row = raw.split(delimiter);
            // If there are no rows, assume header row, and construct
            Map<String, String> rowMap = new HashMap<>();
            if (rows.isEmpty()) {
                for (int column = 0; column < row.length; column++) {
                    String index = String.valueOf(column);
                    rowMap.put(index, row[column]);
                }
            } else {
                for (int column = 0; column < row.length; column++) {
                    String index = String.valueOf(column);
                    String columnName = rows.get(0).get(index);
                    rowMap.put(columnName, row[column].trim());
                }
            }
            rows.add(rowMap);
        }
        reader.close();
    }

    public Map<String, String> getHeader() {
        return rows.get(0);
    }

    public int getSize() {
        return rows.size();
    }

    public Map<String, String> getRow(int row) {
        return rows.get(row);
    }
}