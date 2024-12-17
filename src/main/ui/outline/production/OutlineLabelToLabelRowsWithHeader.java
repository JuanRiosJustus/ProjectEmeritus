package main.ui.outline.production;

import main.graphics.GameUI;

import javax.swing.*;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class OutlineLabelToLabelRowsWithHeader extends GameUI {

    protected JPanel mRowPanel = new GameUI();
    protected final Map<String, OutlineLabelToLabelRow> mRowMap = new HashMap<>();
    protected Color mColor = null;
    protected int mVisibleRows = 0;
    protected static final int DEFAULT_VISIBLE_ROWS = 4;
    protected OutlineLabelToLabelRows mRows = null;
    protected OutlineButtonAndOutlineField mHeader = null;
    public OutlineLabelToLabelRowsWithHeader(int width, int height, Color color, int visibleRows) {
        super(width, height);

        mColor = color;
        mVisibleRows = visibleRows;

        // Controls header setup
        int headerWidth = width;
        int headerHeight = (int) (height * 0.2);
        mHeader = new OutlineButtonAndOutlineField(headerWidth, headerHeight, color);

        int rowsWidth = width;
        int rowsHeight = height - headerHeight;
        mRows = new OutlineLabelToLabelRows(rowsWidth, rowsHeight, color, visibleRows);

        add(mHeader);
        add(mRows);
    }

    public OutlineLabelToLabelRow createRow(String id) { return mRows.createRow(id); }
    public OutlineLabelToLabelRow getRow(String id) {
        return mRows.getRow(id);
    }
    public JButton getReturnButton() {
        return mHeader.getButton();
    }
    public JTextField getTextField() {
        return mHeader.getTextField();
    }
    public void clear() { mRows.clear(); }
}