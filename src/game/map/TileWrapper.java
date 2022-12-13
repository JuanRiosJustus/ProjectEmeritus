package game.map;

import java.util.Objects;

public class TileWrapper {

    public final int row, column;
    public int data; // can be used for anything

    public TileWrapper(int _row, int _column) {
        row = _row;
        column = _column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        TileWrapper that = (TileWrapper) o;
        return row == that.row && column == that.column;
    }
    @Override
    public int hashCode() { return Objects.hash(row, column); }
    public String toString() { return "(row: " + row + ", col: " + column + ")"; }
}