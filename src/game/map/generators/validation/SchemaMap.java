package game.map;

import utils.StringUtils;

import java.util.Arrays;

public class SchemaMap {

    private final int[][] data;
    public SchemaMap(int rows, int columns) { data = new int[rows][columns]; }
    public void fill(int toFillAs) { for (int[] row : data) { Arrays.fill(row, toFillAs); } }
    public boolean isUsed(int row, int column) { return data[row][column] != 0; }
    public boolean isNotUsed(int row, int column) { return !isUsed(row, column); }
    public void set(int row, int column, int value) { data[row][column] = value; }
    public int get(int row, int column) { return data[row][column]; }
    public boolean isOutOfBounds(int row, int column) {
        return row < 0 || column < 0 || row >= data.length || column >= data[row].length;
    }
    public int getRows() { return data.length; }
    public int getColumns() { return getColumns(0); }
    public int getColumns(int row) { return data[row].length; }

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
        b.append(" ".repeat(String.valueOf(data.length).length())).append(" ");
        for (int i = 0, j = 0; i < data[0].length; i++, j++) {
            if (j > 9) { j = 0; }
            b.append(" ").append(j).append(" ");
        }

        b.append("(").append(data[0].length).append(")");

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
                    b.append("[").append(isUsed(row, column) ? lastDigit.charAt(lastDigit.length() - 1) : " ").append("]");
                } else {
                    b.append("[").append(isUsed(row, column) ? " " : "X").append("]");
                }
            }
            b.append(System.lineSeparator());
        }
        return b.toString();
    }

    public String debug2(boolean showRegions) {
        StringBuilder b = new StringBuilder();

        int fillSpace = 3;
        // Print the header
        b.append(" ".repeat(String.valueOf(data.length).length())).append(" ");
        for (int i = 0, j = 0; i < data[0].length; i++, j++) {
            b.append(" ").append(StringUtils.spaceFillers(j, fillSpace)).append(" ");
        }
        b.append("(").append(data[0].length).append(")");

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
                    b.append("[").append(isUsed(row, column) ? StringUtils.spaceFillers(data[row][column], fillSpace) : "   ").append("]");
                } else {
                    b.append("[").append(isUsed(row, column) ? "   " : " X ").append("]");
                }
            }
            b.append(System.lineSeparator());
        }
        return b.toString();
    }
}
