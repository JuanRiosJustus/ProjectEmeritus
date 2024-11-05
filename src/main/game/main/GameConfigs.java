package main.game.main;

import java.util.HashMap;

public class GameConfigs extends HashMap<String, Integer> {
    private int mRows = 0;
    private int mColumns = 0;
    private int mViewWidth = 0;
    private int mViewHeight = 0;
    public GameConfigs(int viewWidth, int viewHeight, int rows, int columns) {
        mRows = rows;
        mColumns = columns;
        mViewHeight = viewHeight;
        mViewWidth = viewWidth;
    }
    public int getViewHeight() { return mViewHeight; }
    public int getViewWidth() { return mViewWidth; }
    public int getColumns() { return mColumns; }
    public int getRows() { return mRows; }
}
