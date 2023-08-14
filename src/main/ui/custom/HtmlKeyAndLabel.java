package main.ui.custom;

import main.constants.ColorPalette;
import main.graphics.temporary.JKeyLabel;
import main.utils.StringFormatter;

import javax.swing.JLabel;

public class HtmlKeyAndLabel extends JLabel {

    private String currentKey;
    private String currentLabel;

    public HtmlKeyAndLabel() { this("", ""); }
    public HtmlKeyAndLabel(String key) { this(key, ""); }
    public HtmlKeyAndLabel(String key, String label) { setKeyAndLabel(key, label); }

    public void setKeyAndLabel(String key, String label) {
        currentKey = key;
        currentLabel = label;
        setText(StringFormatter.format("<html><b>{}</b>{}</html>", key, label));
    }

    public void setLabel(String label) {
        if (currentKey == null) { currentKey = label; }
        setKeyAndLabel(currentKey, label);
    }

    public String getCurrentKey() { return currentKey; }
    public String getCurrentLabel() { return currentLabel; }
}
