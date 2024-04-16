package main.game.stores.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvReader {

    private final List<Map<String, String>> mRows = new ArrayList<>();
    private final Map<Integer, String> mHeader = new HashMap<>();
    private static final String DEFAULT_DELIMITER = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

    public CsvReader(String file) throws Exception { this(file, DEFAULT_DELIMITER); }
    public CsvReader(String file, String delimiter) throws Exception {
        String raw;
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while ((raw = reader.readLine()) != null) {
            String[] row = raw.split(delimiter);
            // Always assume starting row is header
            if (mHeader.isEmpty()) {
                for (int column = 0; column < row.length; column++) {
                    mHeader.put(column, row[column]);
                }
            } else {
                Map<String, String> rowMap = new HashMap<>();
                for (int column = 0; column < row.length; column++) {
                    rowMap.put(mHeader.get(column), row[column].replaceAll("\"", "").trim());
                }
                mRows.add(rowMap);
            }
        }
        reader.close();
    }

    public Map<Integer, String> getHeader() {
        return mHeader;
    }

    public List<Map<String, String>> getRows() {
        return mRows;
    }

    public Map<String, String> getRow(int row) {
        return mRows.get(row);
    }

    private static ArrayList<String> customSplitSpecific(String s) {
        ArrayList<String> words = new ArrayList<>();
        boolean notInsideComma = true;
        int start =0, end=0;
        for(int i=0; i<s.length()-1; i++) {
            if(s.charAt(i)==',' && notInsideComma) {
                words.add(s.substring(start,i));
                start = i+1;
            } else if (s.charAt(i)=='"') {
                notInsideComma = !notInsideComma;
            }
        }
        words.add(s.substring(start));
        return words;
    }
}