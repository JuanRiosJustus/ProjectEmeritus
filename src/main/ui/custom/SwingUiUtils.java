package main.ui.custom;

import javax.swing.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

public class SwingUiUtils {
    private SwingUiUtils() { }

    public static JProgressBar getProgressBar0to100() {
        JProgressBar progressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        progressBar.setOpaque(false);
        progressBar.setValue(0);
        return progressBar;
    }

    public static JComboBox<String> getComboBox() {
        return getComboBox(20);
    }

    private static JComboBox<String> getComboBox(int columns) {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setPrototypeDisplayValue("X".repeat(columns));
        comboBox.setEditable(false);
        comboBox.setUI(createBonelessComboBoxUI());
        comboBox.setRenderer(createRightAlignedReadonlyRenderer());
        return comboBox;
    }

    private static DefaultListCellRenderer createRightAlignedReadonlyRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                component.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

                list.setSelectionForeground(Color.BLACK);
                list.setSelectionBackground(getBackground());
                list.setSelectedIndex(0);

                return this;
            }
        };
    }
    public static JScrollPane createBonelessScrollingPane(int width, int height, JPanel panel) {

        JScrollPane scrollPane = new JScrollPane(panel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.getViewport().setPreferredSize(new Dimension(width, height));
        scrollPane.setPreferredSize(new Dimension(width, height));

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }

    private static JScrollPane createBonelessScrollingPane(Component pane) {

        JScrollPane scrollPane = new JScrollPane(pane,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }

    private static BasicComboBoxUI createBonelessComboBoxUI() {
        return new BasicComboBoxUI() {
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
                        JScrollPane scroller = createBonelessScrollingPane(list);
                        scroller.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
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
                        return scroller;
                    }
                };
            }
        };
    }
}