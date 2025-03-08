package main.ui.outline.production;

import main.graphics.GameUI;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

public class OutlineButtonToCheckBoxRowsWithHeader extends GameUI {

    protected JPanel mRowsPanel = new GameUI();
    protected OutlineButtonAndOutlineField mHeader = null;
    protected OutlineButtonToCheckBoxRows mRows = null;
    protected final Map<String, OutlineButtonToCheckBoxRow> mButtonsMap = new LinkedHashMap<>();
    protected Color mColor = null;
    protected int mVisibleRows = 0;
    protected static final int DEFAULT_VISIBLE_ROWS = 4;

    public OutlineButtonToCheckBoxRowsWithHeader(int width, int height, Color color) {
        this(width, height, color, DEFAULT_VISIBLE_ROWS);
    }

    public OutlineButtonToCheckBoxRowsWithHeader(int width, int height, Color color, int visibleRows) {
        super(width, height);

        mColor = color;
        mVisibleRows = visibleRows;

        int headersWidth = width;
        int headersHeight = (int) (height * .2);
        mHeader = new OutlineButtonAndOutlineField(headersWidth, headersHeight, color);

        int rowsWidth = width;
        int rowsHeight = height - headersHeight;
        mRows = new OutlineButtonToCheckBoxRows(rowsWidth, rowsHeight, color);

        add(mHeader);
        add(mRows);
    }

    public OutlineButtonToCheckBoxRow createRow(String id) { return mRows.createRow(id); }
    public OutlineButtonToCheckBoxRow createRow(String id, boolean showCheckBox) {
        return mRows.createRow(id, showCheckBox);
    }

    public JButton getButton(String value) {
        return mButtonsMap.get(value).getButton();
    }
    public JButton getReturnButton() { return mHeader.getButton(); }
    public void clear() { mRows.clear(); }
}
