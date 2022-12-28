package utils;

import game.map.TileMap;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static java.lang.String.format;

public final class TileMapEncoder {

    public static TileMap decode(String path) {
        return null;
    }





    public static String encode(TileMap tileMap) {
        return encode(tileMap, false);
    }

    public static String encode(TileMap tileMap, boolean headers) {
        // Get the longest columns for correct spacing
        String[][] tileMapEncoding = get2DEncoding(tileMap);

        StringBuilder sb = new StringBuilder();
        int longestColumnLength = getLongestColumnLength(tileMapEncoding);
        String[] rowLabels = getRowLabels(tileMapEncoding);
        if (headers) {
            // Recalculate the longest column by rechecking the additional columns
            String[] columnLabels = getColumnLabels(tileMapEncoding, 0);
            int longestLengthOfAdditionalColumns = getLongestColumnLength(columnLabels);
            if (longestLengthOfAdditionalColumns > longestColumnLength) {
                longestColumnLength = longestLengthOfAdditionalColumns;
            }
            // potentially append header row
            sb.append(rowLabels[0].replaceAll("[0-9]", " "))
                    .append(getRowElongatedRow(columnLabels, longestColumnLength))
                    .append(System.lineSeparator());
        }
        // Construct table
        for (int row = 0; row < tileMapEncoding.length; row++) {
            String rowLabel = rowLabels[row];
            String representation = getRowElongatedRow(tileMapEncoding[row], longestColumnLength, true);
            if (headers) { sb.append(rowLabel); }
            sb.append(representation).append(System.lineSeparator());
        }
        return sb.toString();
    }

    private static String[][] get2DEncoding(TileMap tileMap) {
        String[][] encoding = new String[tileMap.data.length][tileMap.data[0].length];
        for (int row = 0; row < tileMap.data.length; row++) {
            for (int column = 0; column < tileMap.data[row].length; column++) {
                encoding[row][column] = tileMap.data[row][column].getEncoding();
            }
        }
        return encoding;
    }

    private static String[][] get2DFloorMapEncoding(TileMap tileMap) {
        String[][] encoding = new String[tileMap.data.length][tileMap.data[0].length];
        for (int row = 0; row < tileMap.data.length; row++) {
            for (int column = 0; column < tileMap.data[row].length; column++) {
                encoding[row][column] = tileMap.data[row][column].isWall() ? "X" : " ";
            }
        }
        return encoding;
    }

    private static String[] getRowLabels(String[][] table) {
        int mostAmountOfDigits = String.valueOf(table.length).length();
        String[] rowLabels = new String[table.length];
        for (int row = 0; row < table.length; row++) {
            String digitsAsString = String.valueOf(row);
            rowLabels[row] = " ".repeat(mostAmountOfDigits - digitsAsString.length()) + digitsAsString + " ";
        }
        return rowLabels;
    }

    private static String[] getColumnLabels(String[][] tileMapEncoding, int row) {
        String[] columnLabels = new String[tileMapEncoding[row].length];
        for (int column = 0; column < tileMapEncoding[row].length; column++) {
            columnLabels[column] = String.valueOf(column);
        }
        return columnLabels;
    }

    private static String getRowElongatedRow(String[] row, int longestColumnLength) {
        return getRowElongatedRow(row, longestColumnLength, false);
    }

    private static String getRowElongatedRow(String[] row, int longestColumnLength, boolean withSquareBrackets) {
        StringBuilder sb = new StringBuilder();
        for (String str : row) {

            if (withSquareBrackets) {
                sb.append("[");
            } else {
                sb.append(" ");
            }

            sb.append(" ".repeat(longestColumnLength - str.length())).append(str);

            if (withSquareBrackets) {
                sb.append("]");
            } else {
                sb.append(" ");
            }

        }
        return sb.toString();
    }

    private static int getLongestColumnLength(String[][] rows) {
        int max = 0;
        for (String[] row : rows) {
            int longest = getLongestColumnLength(row);
            if (longest <= max) { continue; }
            max = longest;
        }
        return max;
    }
    private static int getLongestColumnLength(String[] row) {
        int max = 0;
        for (String str : row) {
            if (str == null || str.length() <= max) { continue; }
            max = str.length();
        }
        return max;
    }

}