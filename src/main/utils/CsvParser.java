package main.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class CsvParser {

    /** split on the comma only if that comma has zero, or an even number of quotes ahead of it.
     * 
     * Lookahead and see if the rest starts with a quote
     * (?:^|,)\s*
     * 
     * If it does, then match non-greedily till next quote.
     * (?:(?!")(.*?))
     * 
     * If it does not begin with a quote, then match non-greedily till next comma or end of string.
     * (?=,|$)
     */
    // private final String DELIMITER = "(?:^|,)\\s*(?:(?:(?=\")\"([^\"].*?)\")|(?:(?!\")(.*?)))(?=,|$)";
    
    private final char DELIMITER = ',';

    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(CsvParser.class);

    private Map<Integer, String> headers = new HashMap<>();
    private List<Map<String, String>> records = new ArrayList<>();


    public CsvParser(String path) {
        run(path);
    }

    private void run(String path) {
        try {
            logger.info("Starting processing {}", path);

            List<String> rows = Files.readAllLines(Path.of(path));

            for (int i = 0; i < rows.size(); i++) {
                String row = rows.get(i);
                
                StringBuilder builder = new StringBuilder(row);
                StringBuilder token = new StringBuilder();
                List<String> tokens = new ArrayList<>();
                boolean inQuotes = false;

                // Get all the tokens from the row
                for (int j = 0; j < builder.length(); j++) {
                    char c = builder.charAt(j);
                    // store the current state of quotations
                    if (c == '\"' ) { inQuotes = !inQuotes; }
                    // Do not store the delimiter
                    if (c != DELIMITER || (c == DELIMITER && inQuotes)) { token.append(c); }
                    // The token has completed because delimiter found outside of quotes
                    // or we have reached the end of the row
                    if ((c == DELIMITER && !inQuotes) || j == builder.length() - 1) {
                        if (!token.isEmpty()) {
                            boolean startsQuoted = token.charAt(0) == '\"';
                            boolean endsQuoted = token.charAt(token.length() - 1) == '\"';
                            if (startsQuoted && endsQuoted) {
                                token.deleteCharAt(0);
                                token.deleteCharAt(token.length() - 1);
                            }
                        }
                        tokens.add(token.toString().strip());
                        token.setLength(0);
                    }
                }

                // Store the tokenated row into something more manageable        
                Map<String, String> record = new LinkedHashMap<>();               
                for (int j = 0; j < tokens.size(); j++) { 
                    if (i == 0) {
                        headers.put(j, tokens.get(j)); 
                    } else {
                        record.put(headers.get(j), tokens.get(j));
                    }   
                }
                if (i != 0) { records.add(record); }
            }
            logger.info("Completed processing {}", path);
        } catch (Exception e) {
            logger.error("Failed processing {} because {}", path, e.getMessage());
        }
    }

    public Map<String, String> getRecord(int index) {
        return records.get(index);
    }

    public int getRecordCount() { return records.size(); }
}
