package main.ui.swing;

import javax.swing.*;
import java.awt.*;

public class NoScrollBarPane extends JScrollPane {

    public NoScrollBarPane(JComponent component, int width, int height, boolean disableHorizontalScroll) {
        this(component, width, height, disableHorizontalScroll, 1);
    }
    public NoScrollBarPane(JComponent component, int width, int height, boolean disableHorizontalScroll, int scrollSpeed) {
        super(component);

        if (width > 0 && height > 0) {
            getViewport().setPreferredSize(new Dimension(width, height));
            setPreferredSize(new Dimension(width, height));
        }

        if (disableHorizontalScroll) {
            setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            getVerticalScrollBar().setUnitIncrement(scrollSpeed);
            getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
            setComponentZOrder(getVerticalScrollBar(), 0);
        } else {
            setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            getHorizontalScrollBar().setUnitIncrement(scrollSpeed);
            getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
            setComponentZOrder(getHorizontalScrollBar(), 0);
        }

        setWheelScrollingEnabled(true);
        setComponentZOrder(getViewport(), 1);
        setBorder(BorderFactory.createEmptyBorder());
    }

    @Override
    public Dimension getPreferredSize() {
        return super.getPreferredSize(); // Ensure preferred size respects child components
    }
}
