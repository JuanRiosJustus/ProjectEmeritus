package main.ui.outline;

import main.game.stores.pools.FontPoolV1;
import main.graphics.GameUI;
import main.ui.custom.SwingUiUtils;
import main.ui.outline.production.OutlineLabelToLabelRow;
import main.ui.swing.NoScrollBarPane;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class OutlineLabelRowsWithoutHeader extends GameUI {

    protected JPanel mRowPanel = new GameUI();
    protected final Map<String, OutlineLabelToLabelRow> mRowMap = new LinkedHashMap<>();
    protected Color mColor = null;
    protected int mVisibleRows = 0;
    protected static final int DEFAULT_VISIBLE_ROWS = 4;

    public OutlineLabelRowsWithoutHeader(int width, int height, Color color) {
        this(width, height, color, DEFAULT_VISIBLE_ROWS);
    }

    public OutlineLabelRowsWithoutHeader(int width, int height, Color color, int visibleRows) {
        super(width, height);

        mColor = color;
        mVisibleRows = visibleRows;

        mRowPanel = new GameUI();
        mRowPanel.setLayout(new BoxLayout(mRowPanel, BoxLayout.Y_AXIS));
        mRowPanel.setBackground(color);

        add(new NoScrollBarPane(mRowPanel, width, height, true, 10));
    }

    public OutlineLabelToLabelRow createRow(String id) {
        return createRow(id, false);
    }

    public OutlineLabelToLabelRow createRow(String id, boolean isCheckBox) {
        int outlineButtonPanelWidth = (int) getPreferredSize().getWidth();
        int outlineButtonPanelHeight = (int) (getPreferredSize().getHeight() / mVisibleRows);


        int rowWidth = outlineButtonPanelWidth ;
        int rowHeight = outlineButtonPanelHeight;
        OutlineLabelToLabelRow row = new OutlineLabelToLabelRow(rowWidth, rowHeight);

        row.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        int fontHeight = (int) (outlineButtonPanelHeight * .8);

        row.setPreferredSize(new Dimension(rowWidth, rowHeight));
        row.setFont(FontPoolV1.getInstance().getFontForHeight(fontHeight));

        row.setBackground(mColor);
        SwingUiUtils.setHoverEffect(row);

        row.setLeftLabel(id);

        mRowMap.put(id, row);
        mRowPanel.add(row);
        return row;
    }

    public OutlineLabelToLabelRow getRow(String value) {
        return mRowMap.get(value.toLowerCase(Locale.ROOT));
    }


    public void clear() {
        mRowMap.clear();
        mRowPanel.removeAll();
    }
}