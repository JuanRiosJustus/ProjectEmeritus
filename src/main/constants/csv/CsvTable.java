package main.constants.csv;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CsvTable extends LinkedHashMap<String, CsvRow> {
    private final String mPrimaryKey;
    private List<String> mHeader;
    private static final String IGNORE_COLUMN_SPLIT_DELIMITER = "\"";

    public CsvTable(String path, String primaryKey) {
        this(path, primaryKey, ',');
    }

    public CsvTable(String path, String primaryKey, char delimiter) {
        mPrimaryKey = primaryKey;
        List<CsvRow> csvRows = parseCsv(path, String.valueOf(delimiter));
        for (CsvRow csvRow : csvRows) {
            put(csvRow.get(primaryKey), csvRow);
        }
    }

    private List<CsvRow> parseCsv(String path, String delimiter) {
        List<CsvRow> csvRows = new ArrayList<>();

        try {
            List<String> rawCsvRows = Files.readAllLines(Path.of(path));
            List<List<String>> splitCsvRows = new ArrayList<>();
            for (String rawCsvRow : rawCsvRows) {
                List<String> splitCsvRow = splitCsvRow(rawCsvRow, delimiter);
                splitCsvRows.add(splitCsvRow);
            }

            List<String> header = splitCsvRows.get(0);
            header.replaceAll(String::trim);

            mHeader = header;
            for (int index = 0; index < splitCsvRows.size(); index++) {
                if (index == 0) { continue; }
                List<String> splitCsvRow = splitCsvRows.get(index);
                CsvRow csvRow = new CsvRow();
                for (int field = 0; field < header.size(); field++) {
                    String fieldName = header.get(field);
                    String fieldValue = splitCsvRow.get(field);
                    csvRow.put(fieldName, fieldValue);
                }
                csvRows.add(csvRow);
            }
        } catch (Exception e) {
            System.out.println("Unable to parse CSV " + e);
        }
        return csvRows;
    }

    private static ArrayList<String> splitCsvRow(String row, String delimiter) {
        ArrayList<String> tokens = new ArrayList<>();
        int startPosition = 0;
        boolean isInQuotes = false;
        // Iterate through the row on each character
        for (int index = 0; index < row.length(); index++) {
            char currentChar = row.charAt(index);
            // Check if were within quotes or not
            if (currentChar == IGNORE_COLUMN_SPLIT_DELIMITER.charAt(0)) {
                isInQuotes = !isInQuotes;
                // Handle when we are out of quotes
            } else if (currentChar == delimiter.charAt(0) && !isInQuotes) {
                String token = row.substring(startPosition, index);
                // If the token starts or ends with a comma, remove it
                if (token.startsWith(IGNORE_COLUMN_SPLIT_DELIMITER) && token.endsWith(IGNORE_COLUMN_SPLIT_DELIMITER)) {
                    token = token.substring(1, token.length() - 1);
                }
                tokens.add(token);
                startPosition = index + 1;
            }
        }
        // Handle the case for when the last token is delimiter
        String token = row.substring(startPosition);
        if (token.equals(delimiter)) {
            tokens.add("");
        } else {
            tokens.add(token);
        }
        return tokens;
    }

    public List<String> getHeader() { return new ArrayList<>(mHeader); }
}
