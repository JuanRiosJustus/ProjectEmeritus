package main.ui.custom;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class IntegerOnlyDocumentFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (isInteger(fb.getDocument().getText(0, fb.getDocument().getLength()) + string)) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException {
        if (isInteger(fb.getDocument().getText(0, fb.getDocument().getLength()).substring(0, offset) + string)) {
            super.replace(fb, offset, length, string, attr);
        }
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        super.remove(fb, offset, length);
    }

    private boolean isInteger(String text) {
        if (text.isEmpty() || text.equals("-")) {  // Allow empty or "-" to permit typing negative numbers
            return true;
        }
        try {
            Integer.parseInt(text); // Acceptable if it's a valid integer
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}