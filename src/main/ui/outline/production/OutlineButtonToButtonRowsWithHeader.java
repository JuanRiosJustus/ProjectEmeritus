package main.ui.outline.production;

import main.graphics.GameUI;

import javax.swing.*;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class OutlineButtonToButtonRowsWithHeader extends GameUI {

    protected JPanel mRowPanel = null;
    protected final Map<String, OutlineButtonToButtonRow> mRowMap = new HashMap<>();
    protected Color mColor = null;
    protected int mVisibleRows = 0;
    protected static final int DEFAULT_VISIBLE_ROWS = 4;
    protected OutlineButtonToButtonRows mRows = null;
    protected OutlineButtonAndOutlineField mHeader = null;

    public OutlineButtonToButtonRowsWithHeader(int width, int height, Color color, int visibleRows) {
        super(width, height);

        mColor = color;
        mVisibleRows = visibleRows;

        // Controls header setup
        int headerWidth = width;
        int headerHeight = (int) (height * 0.2);
        mHeader = new OutlineButtonAndOutlineField(headerWidth, headerHeight, color);

        int rowsWidth = width;
        int rowsHeight = height - headerHeight;
        mRows = new OutlineButtonToButtonRows(rowsWidth, rowsHeight, color, visibleRows);

        add(mHeader);
        add(mRows);
    }

    public OutlineButtonToButtonRow createRow(String id) { return mRows.createRow(id); }
    public OutlineButtonToButtonRow createRow(String id, boolean isCheckbox) { return mRows.createRow(id, isCheckbox); }
    public OutlineButtonToButtonRow getRow(String id) {
        return mRowMap.get(id);
    }
    public JButton getReturnButton() {
        return mHeader.getButton();
    }
    public JTextField getTextField() {
        return mHeader.getTextField();
    }
    public void clear() { mRows.clear(); }

}