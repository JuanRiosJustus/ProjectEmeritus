package main.game.map.builders.utils;

import java.util.Arrays;

public class TileMapLayerV2 {

    private final String[][] data;

    public TileMapLayerV2(int rows, int columns) { data = new String[rows][columns]; fill(null); }
    
    public void fill(String toFillAs) { for (String[] row : data) { Arrays.fill(row, toFillAs); } }
    
    public boolean isUsed(int row, int column) { return data[row][column] != null; }
    
    public boolean isNotUsed(int row, int column) { return !isUsed(row, column); }
    
    public void set(int row, int column, String value) { data[row][column] = value; }
    public void clear(int row, int column) { set(row, column, null); }
    
    public String get(int row, int column) { return data[row][column]; }
    
    public boolean isOutOfBounds(int row, int column) {
        return row < 0 || column < 0 || row >= data.length || column >= data[row].length;
    }
    
    public int getRows() { return data.length; }
    
    public int getColumns() { return getColumns(0); }
    
    public int getColumns(int row) { return data[row].length; }
    
    public TileMapLayerV2 getCopy() {
        TileMapLayerV2 map = new TileMapLayerV2(getRows(), getColumns());
        for (int row = 0; row < getRows(); row++) {
            for (int column = 0; column < getColumns(); column++) {
                map.set(row, column, get(row, column));
            }
        }
        return map;
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        for (int row = 0; row < data.length; row++) {
            for (int column = 0; column < data[row].length; column++) {
                b.append("[").append(isUsed(row, column) ? " " : "X").append("]");
            }
            b.append(System.lineSeparator());
        }
        return b.toString();
    }

    public String debug() { return debug(false); }
  
    public String debug(boolean showRegions) {
        StringBuilder b = new StringBuilder();

        // Print the header
        b.append(" ".repeat(String.valueOf(data.length).length()))
            .append(" ");
        for (int i = 0, j = 0; i < data[0].length; i++, j++) {
            if (j > 9) { j = 0; }
            b.append(" ")
                .append(j)
                .append(" ");
        }

        b.append("(")
            .append(data[0].length)
            .append(")");

        // Print the contents of the map
        b.append(System.lineSeparator());
        for (int row = 0; row < data.length; row++) {
            // Show the row numbers, aligning them uniformly
            b.append(row)        
                .append(" ".repeat(String.valueOf(data.length).length() - String.valueOf(row).length()))
                .append(" ");
            // Print out the value of each tile within that row
            for (int column = 0; column < data[row].length; column++) {
                if (showRegions) {
                    String lastDigit = data[row][column] + "";
                    b.append("[")
                        .append(isUsed(row, column) ? lastDigit.charAt(lastDigit.length() - 1) : " ")
                        .append("]");
                } else {
                    b.append("[")
                        .append(isUsed(row, column) ? " " : "X")
                        .append("]");
                }
            }
            b.append(System.lineSeparator());
        }
        return b.toString();
    }
}
