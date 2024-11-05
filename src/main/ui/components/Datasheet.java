package main.ui.components;

import main.game.stores.pools.ColorPalette;
import main.ui.components.elements.*;

import javax.swing.JComboBox;
import java.awt.Color;
import java.awt.Font;

public class Datasheet extends JComboBox<String> {
    private static final String DATASHEET_SEPARATOR = ":";

    private DualOutlineLabelRenderer mDualOutlineLabelRenderer = new DualOutlineLabelRenderer(DATASHEET_SEPARATOR);
    private DualOutlineLabelComboBoxEditor mDualOutlineLabelComboBoxEditor = new DualOutlineLabelComboBoxEditor(DATASHEET_SEPARATOR);

    public Datasheet() {
        this(20);
    }

    public Datasheet(int columns) {
        setPrototypeDisplayValue("X".repeat(columns));
        setEditable(false);
        setUI(new GenericComboBoxUI());

        mDualOutlineLabelRenderer = new DualOutlineLabelRenderer(DATASHEET_SEPARATOR);
        setRenderer(mDualOutlineLabelRenderer);

        mDualOutlineLabelComboBoxEditor = new DualOutlineLabelComboBoxEditor(DATASHEET_SEPARATOR);
        setEditor(mDualOutlineLabelComboBoxEditor);
    }

    public void setCustomizeDatasheet(Font font, Color color) {
        mDualOutlineLabelRenderer.setPanelColors(color);
        mDualOutlineLabelRenderer.setLabelFonts(font);
        setBackground(color);
    }

    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        if (mDualOutlineLabelRenderer == null) { return; }
        mDualOutlineLabelRenderer.setPanelColors(color);
    }

    public void addDatasheetItem(String item) {
        addItem(item);
    }


    public void setReadOnly() {
        addActionListener(e -> setSelectedIndex(0));
    }
}
