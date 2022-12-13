package game.map;

import utils.StringUtils;

import java.util.Arrays;

public class SchemaMap {

    private final int[][] map;
    public SchemaMap(int rows, int columns) { map = new int[rows][columns]; }

    public void fill(int toFillAs) { for (int[] row : map) { Arrays.fill(row, toFillAs); } }
    public boolean isOccupied(int row, int column) { return map[row][column] != 0; }
    public boolean isNotOccupied(int row, int column) { return !isOccupied(row, column); }
    public void setOccupied(int row, int column, int value) { map[row][column] = value; }
    public int getOccupied(int row, int column) { return map[row][column]; }
    public boolean isOutOfBounds(int row, int column) {
        return row < 0 || column < 0 || row >= map.length || column >= map[row].length;
    }
    public int getRows() { return map.length; }
    public int getColumns() { return map[0].length; }

    public String toString() {
        StringBuilder b = new StringBuilder();
        for (int row = 0; row < map.length; row++) {
            for (int column = 0; column < map[row].length; column++) {
                b.append("[").append(isOccupied(row, column) ? " " : "X").append("]");
            }
            b.append(System.lineSeparator());
        }
        return b.toString();
    }

    public String debug() { return debug(false); }

    public String debug(boolean showRegions) {
        StringBuilder b = new StringBuilder();

        // Print the header
        b.append(" ".repeat(String.valueOf(map.length).length())).append(" ");
        for (int i = 0, j = 0; i < map[0].length; i++, j++) {
            if (j > 9) { j = 0; }
            b.append(" ").append(j).append(" ");
        }

        b.append("(").append(map[0].length).append(")");

        // Print the contents of the map
        b.append(System.lineSeparator());
        for (int row = 0; row < map.length; row++) {
            // Show the row numbers, aligning them uniformly
            b.append(row)
                    .append(" ".repeat(String.valueOf(map.length).length() - String.valueOf(row).length()))
                    .append(" ");
            // Print out the value of each tile within that row
            for (int column = 0; column < map[row].length; column++) {
                if (showRegions) {
                    String lastDigit = map[row][column] + "";
                    b.append("[").append(isOccupied(row, column) ? lastDigit.charAt(lastDigit.length() - 1) : " ").append("]");
                } else {
                    b.append("[").append(isOccupied(row, column) ? " " : "X").append("]");
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
        b.append(" ".repeat(String.valueOf(map.length).length())).append(" ");
        for (int i = 0, j = 0; i < map[0].length; i++, j++) {
            b.append(" ").append(StringUtils.spaceFillers(j, fillSpace)).append(" ");
        }
        b.append("(").append(map[0].length).append(")");

        // Print the contents of the map
        b.append(System.lineSeparator());
        for (int row = 0; row < map.length; row++) {
            // Show the row numbers, aligning them uniformly
            b.append(row)
                    .append(" ".repeat(String.valueOf(map.length).length() - String.valueOf(row).length()))
                    .append(" ");
            // Print out the value of each tile within that row
            for (int column = 0; column < map[row].length; column++) {
                if (showRegions) {
                    b.append("[").append(isOccupied(row, column) ? StringUtils.spaceFillers(map[row][column], fillSpace) : "   ").append("]");
                } else {
                    b.append("[").append(isOccupied(row, column) ? "   " : " X ").append("]");
                }
            }
            b.append(System.lineSeparator());
        }
        return b.toString();
    }
}
