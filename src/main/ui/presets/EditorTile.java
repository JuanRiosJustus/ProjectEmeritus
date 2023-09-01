package main.ui.presets;

import javax.swing.JButton;

public class EditorTile extends JButton {

    public int row;
    public int column;

    public EditorTile(int tileRow, int tileColumn) {
        row = tileRow;
        column = tileColumn;
    }
}
