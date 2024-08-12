package main.ui.custom;

import main.game.stores.pools.FontPool;
import main.ui.components.OutlineLabel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;

public class SwingUiUtils {
    private SwingUiUtils() { }

    private static final Insets WEST_INSETS = new Insets(5, 0, 5, 5);
    private static final Insets EAST_INSETS = new Insets(5, 5, 5, 0);
    public static void stylizeComponent(JComponent button, Color color) {
        button.setFont(FontPool.getInstance().getFontForHeight((int) button.getPreferredSize().getHeight()));
//        button.setFont(FontPool.getInstance().getFont(button.getFont().getSize()).deriveFont(Font.BOLD));
        button.setForeground(color);
    }
    public static void stylizeButtons(JButton button, Color color) {
        button.setFont(FontPool.getInstance().getFont(button.getFont().getSize()).deriveFont(Font.BOLD));
        button.setForeground(color);
    }
    public static void automaticallyStyleButton(JButton button) {
        int fontSize = (int) ((int) button.getPreferredSize().getHeight() * .4);
        button.setFont(FontPool.getInstance().getFont(fontSize).deriveFont(Font.BOLD));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            private final Color mDefaultMouseColor = button.getBackground();
            private final Color mMouseEnteredColor = mDefaultMouseColor.darker();
            private final Color mMousedExitedColor = mDefaultMouseColor;
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(mMouseEnteredColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(mMousedExitedColor);
            }
        });
    }

    public static void automaticallyStyleComponent(JComponent component) {
        automaticallyStyleComponent(component, 0);
    }

    public static void automaticallyStyleComponent(JComponent component, int fontHeight) {
        if (fontHeight != 0) {
            component.setFont(FontPool.getInstance().getFont(fontHeight).deriveFont(Font.BOLD));
        }

        component.addMouseListener(new java.awt.event.MouseAdapter() {
            private final Color mDefaultMouseColor = component.getBackground();
            private final Color mMouseEnteredColor = mDefaultMouseColor.darker();
            private final Color mMousedExitedColor = mDefaultMouseColor;
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                component.setBackground(mMouseEnteredColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                component.setBackground(mMousedExitedColor);
            }
        });
    }



    public static void stylizeButtons(JLabel label, Color color, int size) {
        label.setFont(FontPool.getInstance().getFont(size).deriveFont(Font.BOLD));
        label.setForeground(color);
    }


    public static JProgressBar getProgressBar0to100() {
        JProgressBar progressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        progressBar.setOpaque(false);
        progressBar.setValue(0);
        return progressBar;
    }

    public static JComboBox<String> getRightAlignedComboBox() {
        return getComboBox(20, true);
    }

    public static JComboBox<String> getLeftAlignedComboBox() {
        return getComboBox(20, false);
    }

    public static JComboBox<String> getComboBoxLeftAligned() {
        return getComboBox(20, false);
    }

    private static JComboBox<String> getComboBox(int columns, boolean rightAligned) {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setPrototypeDisplayValue("X".repeat(columns));
        comboBox.setEditable(false);
        comboBox.setUI(createBonelessComboBoxUI());
        if (rightAligned) {
            comboBox.setRenderer(createReadonlyRenderer(false));
        } else {
            comboBox.setRenderer(createReadonlyRenderer(true));
        }
        return comboBox;
    }

    private static DefaultListCellRenderer createReadonlyRenderer(boolean leftToRight) {
        return new DefaultListCellRenderer() {
            private OutlineLabel mOutlineLabel = new OutlineLabel();
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel component = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);


                if (leftToRight) {
                    component.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                } else {
                    component.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                }

                component.setBackground(index % 2 == 0 ? Color.WHITE.darker() : Color.WHITE);

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
    public static JScrollPane createBonelessScrollingPaneNoVertical(int width, int height, JPanel panel) {

        JScrollPane scrollPane = new JScrollPane(panel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.getViewport().setPreferredSize(new Dimension(width, height));
//        scrollPane.getViewport().setMaximumSize(new Dimension(width, height));
//        scrollPane.getViewport().setMinimumSize(new Dimension(width, height));
        scrollPane.setPreferredSize(new Dimension(width, height));

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }
    public static JScrollPane createBonelessScrollingPaneNoHorizontal(int width, int height, JPanel panel) {

        JScrollPane scrollPane = new JScrollPane(panel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.getViewport().setPreferredSize(new Dimension(width, height));
        scrollPane.setPreferredSize(new Dimension(width, height));

        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
//        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }

    public static GridBagConstraints createGbc(int row, int column) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = column;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;

        gbc.anchor = (column == 0) ? GridBagConstraints.WEST : GridBagConstraints.EAST;
        gbc.fill = (column == 0) ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;

        gbc.insets = (column == 0) ? WEST_INSETS : EAST_INSETS;
        gbc.weightx = (column == 0) ? 0.1 : 1.0;
        gbc.weighty = 1.0;
        gbc.ipadx = 5;
        gbc.ipady = 5;
        return gbc;
    }

    public static void removeAllListeners(AbstractButton button) {
        if (button == null) { return; }
        for (ActionListener listener : button.getActionListeners()) {
            button.removeActionListener(listener);
        }
        for (MouseListener listener : button.getMouseListeners()) {
            button.removeMouseListener(listener);
        }
    }

    public static void removeAllActionListeners(AbstractButton button) {
        if (button == null) { return; }
        for (ActionListener listener : button.getActionListeners()) {
            button.removeActionListener(listener);
        }
    }


    public static JComponent createTranslucentScrollbar(JComponent component) {
        return createTranslucentScrollbar(-1, -1, component);
    }
    public static JComponent createTranslucentScrollbar(int width, int height, JComponent component) {
        JScrollPane scrollPane = new JScrollPane(component) {
            @Override public boolean isOptimizedDrawingEnabled() {
                return false; // JScrollBar is overlap
            }
        };

        if (width > 0 && height > 0) {
            scrollPane.getViewport().setPreferredSize(new Dimension(width, height));
            scrollPane.setPreferredSize(new Dimension(width, height));
        }

        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        scrollPane.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.setComponentZOrder(scrollPane.getVerticalScrollBar(), 0);
        scrollPane.setComponentZOrder(scrollPane.getViewport(), 1);
        scrollPane.getVerticalScrollBar().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        scrollPane.setLayout(new ScrollPaneLayout() {
            @Override public void layoutContainer(Container parent) {
                JScrollPane scrollPane = (JScrollPane) parent;

                Rectangle availR = scrollPane.getBounds();
                availR.x = availR.y = 0;

                Insets insets = parent.getInsets();
                availR.x = insets.left;
                availR.y = insets.top;
                availR.width  -= insets.left + insets.right;
                availR.height -= insets.top  + insets.bottom;

                Rectangle vsbR = new Rectangle();
                vsbR.width  = 12;
                vsbR.height = availR.height;
                vsbR.x = availR.x + availR.width - vsbR.width;
                vsbR.y = availR.y;

                if (viewport != null) {
                    viewport.setBounds(availR);
                }
                if (vsb != null) {
                    vsb.setVisible(true);
                    vsb.setBounds(vsbR);
                }
            }
        });
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            private final Color defaultColor  = new Color(220, 100, 100, 100);
            private final Color draggingColor = new Color(200, 100, 100, 100);
            private final Color rolloverColor = new Color(255, 120, 100, 100);
            private final Dimension d = new Dimension();
            @Override protected JButton createDecreaseButton(int orientation) {
                return new JButton() {
                    @Override public Dimension getPreferredSize() {
                        return d;
                    }
                };
            }
            @Override protected JButton createIncreaseButton(int orientation) {
                return new JButton() {
                    @Override public Dimension getPreferredSize() {
                        return d;
                    }
                };
            }
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {}
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                Color color;
                JScrollBar sb = (JScrollBar) c;
                if (!sb.isEnabled() || r.width > r.height) {
                    return;
                } else if (isDragging) {
                    color = draggingColor;
                } else if (isThumbRollover()) {
                    color = rolloverColor;
                } else {
                    color = defaultColor;
                }
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(color);
                g2.fillRect(r.x, r.y, r.width - 1, r.height - 1);
                g2.setPaint(Color.WHITE);
                g2.drawRect(r.x, r.y, r.width - 1, r.height - 1);
                g2.dispose();
            }
            @Override protected void setThumbBounds(int x, int y, int width, int height) {
                super.setThumbBounds(x, y, width, height);
                //scrollbar.repaint(x, 0, width, scrollbar.getHeight());
                scrollbar.repaint();
            }
        });
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
//                        JScrollPane scroller = createBonelessScrollingPane(list);
//                        JScrollPane scroller = (JScrollPane) createTranslucentScrollbar(list);
                        JScrollPane scroller = new JScrollPane(list,
                                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

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

    public static void setStylizedRaisedBevelBorder(JComponent component, int thickness) {
        Border border = BorderFactory.createRaisedBevelBorder();
        Border margin = new EmptyBorder(thickness, thickness + 3,
                thickness, thickness + 3);
        component.setBorder(new CompoundBorder(border, margin));
    }
    public static void setStylizedRaisedBevelBorder(JComponent component) {
        setStylizedRaisedBevelBorder(component, 2);
    }

    public static void disable(JComponent component) {
        if (component == null || component.getComponents() == null)  { return; }
        if (component.getComponents().length == 0) { return; }

        component.setEnabled(false);
        component.setFocusable(false);
        component.setFocusCycleRoot(false);
        component.setRequestFocusEnabled(false);
        component.setFocusTraversalPolicyProvider(false);
        component.setFocusTraversalKeysEnabled(false);

        for (Component child : component.getComponents()) {
            JComponent jComponent = (JComponent) child;
            disable(jComponent);
        }
    }
}