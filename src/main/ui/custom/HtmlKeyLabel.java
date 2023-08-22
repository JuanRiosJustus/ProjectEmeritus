package main.ui.custom;

import main.utils.StringFormatter;

import javax.swing.JLabel;

public class HtmlKeyLabel extends JLabel {

    private String currentKey;
    private String currentLabel;

    public HtmlKeyLabel() { this("", ""); }
    public HtmlKeyLabel(String key) { this(key, ""); }
    public HtmlKeyLabel(String key, String label) { setKeyAndLabel(key, label); }

    public void setKeyAndLabel(String key, String label) {
        currentKey = key;
        currentLabel = label;
//        setKeyAndLabel(StringFormatter.format("<html><b>{}</b>{}</html>", key, label));
    }

    public void setText(String label) {
        if (currentKey == null) { currentKey = label; }
        setKeyAndLabel(currentKey, label);
    }

    public String getCurrentKey() { return currentKey; }
    public String getCurrentLabel() { return currentLabel; }
}
