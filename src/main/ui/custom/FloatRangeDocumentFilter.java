package main.ui.custom;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class FloatRangeDocumentFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
        String newText = currentText.substring(0, offset) + string + currentText.substring(offset);
        if (isValidFloatInRange(newText)) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException {
        String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
        String newText = currentText.substring(0, offset) + string + currentText.substring(offset + length);
        if (isValidFloatInRange(newText)) {
            super.replace(fb, offset, length, string, attr);
        }
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
        String newText = currentText.substring(0, offset) + currentText.substring(offset + length);
        if (isValidFloatInRange(newText)) {
            super.remove(fb, offset, length);
        }
    }

    private boolean isValidFloatInRange(String text) {
        if (text.isEmpty()) {
            return true; // Allow empty input to let user type
        }

        try {
            // Parse the text as a floating-point number
            float value = Float.parseFloat(text);
            // Check if the value is within the range [0, 1]
            return value >= 0.0f && value <= 1.0f;
        } catch (NumberFormatException e) {
            return false; // Not a valid float
        }
    }
}