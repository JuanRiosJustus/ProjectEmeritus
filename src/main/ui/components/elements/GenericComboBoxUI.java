package main.ui.components.elements;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.Dimension;

import static main.ui.custom.SwingUiUtils.createTranslucentScrollbar;

public class GenericComboBoxUI extends BasicComboBoxUI {
    protected JButton createArrowButton() {
        return new JButton() {
            @Override
            public int getWidth() { return 0; }
        };
    }

    @Override
    protected ComboPopup createPopup() {
        return new BasicComboPopup(comboBox) {
            @Override
            protected JScrollPane createScroller() {
//                        JScrollPane scroller = createBonelessScrollingPane(list);
//                        JScrollPane scroller = (JScrollPane) createTranslucentScrollbar(list);
                JScrollPane scrollPane = new JScrollPane(list,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

                scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
                    @Override
                    protected JButton createDecreaseButton(int orientation) {
                        return createZeroButton();
                    }

                    @Override
                    protected JButton createIncreaseButton(int orientation) {
                        return createZeroButton();
                    }

                    @Override
                    public Dimension getPreferredSize(JComponent c) {
                        return new Dimension(10, super.getPreferredSize(c).height);
                    }

                    private JButton createZeroButton() {
                        Dimension ZERO_DIMENSION = new Dimension(0, 0);

                        return new JButton() {

                            @Override
                            public Dimension getMinimumSize() {
                                return ZERO_DIMENSION;
                            }

                            @Override
                            public Dimension getPreferredSize() { return ZERO_DIMENSION; }

                            @Override
                            public Dimension getMaximumSize() {
                                return ZERO_DIMENSION;
                            }
                        };
                    }
                });
                return scrollPane;
            }
        };
    }
}