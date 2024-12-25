package main.ui.outline.production;

import main.graphics.GameUI;
import main.ui.outline.OutlineTextField;

import javax.swing.*;
import java.awt.Color;

public class OutlineLabelToTextAreaRowsWithHeader extends GameUI {

    protected OutlineTextAreaToTextAreaRows mRows = null;
    protected Color mColor = null;
    protected int mVisibleRows = 0;
    protected static final int DEFAULT_VISIBLE_ROWS = 4;
    protected OutlineButtonAndOutlineField mHeader = null;

    public OutlineLabelToTextAreaRowsWithHeader(int width, int height, Color color, int visibleRows) {
        super(width, height);

        mColor = color;
        mVisibleRows = visibleRows;

        // Controls header setup
        int headerWidth = width;
        int headerHeight = (int) (height * 0.15);
        mHeader = new OutlineButtonAndOutlineField(headerWidth, headerHeight, color);

        int rowsWidth = width;
        int rowsHeight = height - headerHeight;
        mRows = new OutlineTextAreaToTextAreaRows(rowsWidth, rowsHeight, color, visibleRows);

        add(mHeader);
        add(mRows);
    }


    public JButton getReturnButton() { return mHeader.getButton(); }
    public OutlineTextField getHeaderLabel() { return mHeader.getTextField(); }
    public OutlineTextAreaToTextAreaRow createRow(String id) { return mRows.createRow(id); }
    public OutlineTextAreaToTextAreaRow createRow(String id, int outlineThickness) {
        OutlineTextAreaToTextAreaRow row = mRows.createRow(id);
        row.setOutlineThickness(outlineThickness);
//        return mRows.createRow(id);
        return row;
    }
    public OutlineTextAreaToTextAreaRow getRow(String id) {
        return mRows.getRow(id);
    }
    public void clear() { mRows.clear(); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().setBackground(Color.GRAY); // Set a distinct background


            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 200);

            OutlineTextAreaToTextAreaRow row = new OutlineTextAreaToTextAreaRow("Left", "Right",
                    Color.YELLOW, 400, 50);
            frame.add(row);

            frame.setVisible(true);
        });
    }
}