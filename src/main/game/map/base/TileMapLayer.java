package main.game.map.base;

import java.util.Arrays;

public class TileMapLayer {

    private final String mName;
    private final String[][] mData;

    public TileMapLayer(String name, int rows, int columns) {
        mData = new String[rows][columns];
        fill(null);
        mName = name;
    }

    public void fill(String toFillAs) { for (String[] row : mData) { Arrays.fill(row, toFillAs); } }

    public boolean isUsed(int row, int column) { return mData[row][column] != null; }

    public boolean isNotUsed(int row, int column) { return !isUsed(row, column); }

    public void set(int row, int column, String value) { mData[row][column] = value; }
    public void set(int row, int column, int value) { mData[row][column] = String.valueOf(value); }
    public void clear(int row, int column) { set(row, column, null); }

    public String get(int row, int column) { return mData[row][column]; }

    public boolean isOutOfBounds(int row, int column) {
        return row < 0 || column < 0 || row >= mData.length || column >= mData[row].length;
    }

    public int getRows() { return mData.length; }

    public int getColumns() { return getColumns(0); }

    public int getColumns(int row) { return mData[row].length; }

    public String toString() {
        StringBuilder b = new StringBuilder();
        for (int row = 0; row < mData.length; row++) {
            for (int column = 0; column < mData[row].length; column++) {
                b.append("[").append(isUsed(row, column) ? " " : "X").append("]");
            }
            b.append(System.lineSeparator());
        }
        return b.toString();
    }

    public String debug(boolean showRegions) {
        StringBuilder b = new StringBuilder();

        // Print the header
        b.append(" ".repeat(String.valueOf(mData.length).length()))
                .append(" ");
        for (int i = 0, j = 0; i < mData[0].length; i++, j++) {
            if (j > 9) { j = 0; }
            b.append(" ")
                    .append(j)
                    .append(" ");
        }

        b.append("(")
                .append(mData[0].length)
                .append(")");

        // Print the contents of the map
        b.append(System.lineSeparator());
        for (int row = 0; row < mData.length; row++) {
            // Show the row numbers, aligning them uniformly
            b.append(row)
                    .append(" ".repeat(String.valueOf(mData.length).length() - String.valueOf(row).length()))
                    .append(" ");
            // Print out the value of each tile within that row
            for (int column = 0; column < mData[row].length; column++) {
                if (showRegions) {
                    String lastDigit = mData[row][column] + "";
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