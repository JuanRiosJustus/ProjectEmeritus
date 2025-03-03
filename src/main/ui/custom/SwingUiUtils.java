package main.ui.custom;

import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.input.Keyboard;
import main.input.MouseV1;
import main.ui.outline.OutlineLabel;
import main.ui.custom.mouse.MouseHoverEffect;

import main.ui.outline.OutlineComboBoxEditor;
import main.ui.outline.production.core.OutlineButton;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class SwingUiUtils {

    private SwingUiUtils() { }

    private static final Insets WEST_INSETS = new Insets(5, 0, 5, 5);
    private static final Insets EAST_INSETS = new Insets(5, 5, 5, 0);
    public static void stylizeButtons(JButton button, Color color) {
        button.setFont(FontPool.getInstance().getFont(button.getFont().getSize()).deriveFont(Font.BOLD));
        button.setForeground(color);
    }
    public static void automaticallyStyleButton(JButton button) {
        int fontSize = (int) (button.getPreferredSize().getHeight() * .4);
        button.setFont(FontPool.getInstance().getFont(fontSize).deriveFont(Font.BOLD));
        button.addMouseListener(new MouseAdapter() {
            private final Color mDefaultMouseColor = button.getBackground();
            private final Color mMouseEnteredColor = mDefaultMouseColor.darker();
            private final Color mMousedExitedColor = mDefaultMouseColor.brighter();
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(mMouseEnteredColor);
            }
            public void mouseExited(MouseEvent evt) {
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

        component.addMouseListener(new MouseAdapter() {
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

//    public static void addHoverEffect(JComponent component) {
//        addHoverEffect(component, false);
//    }
//
    public static void setHoverEffect(JComponent component) {
        addHoverEffects(new JComponent[] { component }, false);
    }

    public static void setHoverEffect(JComponent component, boolean reset) {
        if (reset) { removeAllMouseListeners(component); }
        component.addMouseListener(new MouseHoverEffect(
                component,
                component.getBackground().darker(),
                component.getBackground()
        ));
    }

    public static void addHoverEffects(JComponent[] jComponents) {
        addHoverEffects(jComponents, false);
    }

    public static void addHoverEffects(JComponent[] jComponents, boolean reset) {
        if (jComponents == null) { return; }
        for (JComponent jcomponent : jComponents) {
            if (reset) {
                removeAllMouseListeners(jcomponent);
            }
            jcomponent.addMouseListener(new MouseHoverEffect(
                    jcomponent,
                    jcomponent.getBackground().darker(),
                    jcomponent.getBackground()
            ));
        }
    }

//    public static JComboBox<String> createJComboBox(String str, int width, int height) {
//        return createJComboBox(str, null, width, height);
//    }
//    public static JComboBox<String> createJComboBox(Color mainColor, int width, int height) {
//        return createJComboBox(null, mainColor, width, height);
//    }
//    public static JComboBox<String> createJComboBox(int width, int height) {
//        return createJComboBox(null, null, width, height);
//    }
//    public static JComboBox<String> createJComboBox(String txt, Color mainColor, int width, int height) {
//        JComboBox<String> jComboBox = new JComboBox<>();
//        if (txt != null) { jComboBox.addItem(txt); }
//        jComboBox.setMinimumSize(new Dimension(width, height));
//        jComboBox.setMaximumSize(new Dimension(width, height));
//        jComboBox.setPreferredSize(new Dimension(width, height));
//        jComboBox.setUI(new BasicComboBoxUI() {
//            @Override public Dimension getMinimumSize(JComponent c) { return new Dimension(width, height); }
//            @Override public Dimension getMaximumSize(JComponent c) { return new Dimension(width, height); }
//            @Override public Dimension getPreferredSize(JComponent c) { return new Dimension(width, height); }
//        });
//
//        // Set a custom renderer to change the background color of the selected item
//        DefaultListCellRenderer renderer = new DefaultListCellRenderer() {
////            @Override
////            public Color getBackground() {
////                return mainColor;
////            }
//
//            @Override
//            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//                // Center-align the text
//                label.setHorizontalAlignment(SwingConstants.CENTER);
//
//                // Set background color for selected item
//                if (index == -1) {
//                    // Displayed item in the JComboBox (after selection)
//                    label.setBackground(mainColor); // Background for selected item shown in combo box
//                    label.setForeground(Color.BLACK); // Text color for contrast
//                } else if (isSelected) {
//                    // Item selected in the dropdown list
//                    label.setBackground(mainColor); // Dropdown selection color
//                    label.setForeground(Color.BLACK); // Text color for contrast in dropdown
//                } else {
//                    // Default dropdown item appearance
//                    label.setBackground(Color.WHITE);
//                    label.setForeground(Color.BLACK);
//                }
//
//                label.setOpaque(true); // Make background color visible
//                label.setEnabled(true);
//                return label;
//            }
//        };
//        if (mainColor != null) {
//            jComboBox.setRenderer(renderer);
//        }
//
//
//        return jComboBox;
//    }

    public static JPanel createWrapperJPanel(int width, int height, JComponent component) {
        JPanel wrapper = new GameUI(width, height);

        wrapper.setOpaque(false);
//        wrapper.setOpaque(true);
//        wrapper.setBackground(ColorPalette.getRandomColor());
        wrapper.setPreferredSize(new Dimension(width, height));
        wrapper.setMinimumSize(new Dimension(width, height));
        wrapper.setMaximumSize(new Dimension(width, height));

        int verticalPadding = (int) (height * .05);
        wrapper.setBorder(BorderFactory.createEmptyBorder(verticalPadding, 5, verticalPadding, 5));
        if (component != null) {
            wrapper.add(component);
        }
        return wrapper;
    }

    public static JPanel createWrapperJPanelLeftAlign(int width, int height, JComponent component) {

        JPanel wrapperPanel = SwingUiUtils.createWrapperJPanel(width, height, null);

        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.X_AXIS));
//        wrapperPanel.setOpaque(false);
//        wrapperPanel.setOpaque(true);
//        wrapperPanel.setBackground(ColorPalette.getRandomColor());

        wrapperPanel.add(component);




        int fillerComponentWidth = (int) (width - component.getPreferredSize().getWidth());
        int fillerComponentHeight = height;
        JPanel fillerComponent = new GameUI(fillerComponentWidth, fillerComponentHeight);
        fillerComponent.setOpaque(false);
//        fillerComponent.setOpaque(true);
//        fillerComponent.setBackground(ColorPalette.getRandomColor());

        wrapperPanel.add(fillerComponent);

        return wrapperPanel;
    }

    public static StringComboBox createJComboBox(int width, int height) {
        return createJComboBox(null, null, width, height);
    }
    public static StringComboBox createJComboBox(String txt, Color mainColor, int width, int height) {
        StringComboBox jComboBox = new StringComboBox();
        SwingUiUtils.setupPrettyStringComboBox(jComboBox, mainColor, width, height);
        return jComboBox;
    }
    public static void setupPrettyStringComboBox(StringComboBox comboBox, int width, int height) {
        setupPrettyStringComboBox(comboBox,null, width, height);
    }

    public static void setupPrettyStringComboBox(StringComboBox comboBox, Color mainColor, int width, int height) {
        if (comboBox == null) { return; }
        comboBox.setMinimumSize(new Dimension(width, height));
        comboBox.setMaximumSize(new Dimension(width, height));
        comboBox.setPreferredSize(new Dimension(width, height));
        comboBox.setUI(new BasicComboBoxUI() {
            @Override public Dimension getMinimumSize(JComponent c) { return new Dimension(width, height); }
            @Override public Dimension getMaximumSize(JComponent c) { return new Dimension(width, height); }
            @Override public Dimension getPreferredSize(JComponent c) { return new Dimension(width, height); }
        });

        // Set a custom renderer to change the background color of the selected item
        DefaultListCellRenderer renderer = new DefaultListCellRenderer() {
//            @Override
//            public Color getBackground() {
//                return mainColor;
//            }

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                // Center-align the text
                label.setHorizontalAlignment(SwingConstants.CENTER);
                setHorizontalAlignment(SwingConstants.CENTER);


                // Set background color for selected item
                if (index == -1) {
                    // Displayed item in the JComboBox (after selection)
                    label.setBackground(mainColor); // Background for selected item shown in combo box
                    label.setForeground(Color.BLACK); // Text color for contrast
                } else if (isSelected) {
                    // Item selected in the dropdown list
                    label.setBackground(mainColor); // Dropdown selection color
                    label.setForeground(Color.BLACK); // Text color for contrast in dropdown
                } else {
                    // Default dropdown item appearance
                    label.setBackground(Color.WHITE);
                    label.setForeground(Color.BLACK);
                }

                label.setOpaque(true); // Make background color visible
                label.setEnabled(true);
                return label;
            }
        };
        if (mainColor != null) {
            comboBox.setRenderer(renderer);
        }
    }

    public static JPanel createSpacingContainer(int width, int height, Color color) {
        JPanel spacerPanel = new JPanel();
        spacerPanel.setMinimumSize(new Dimension(width, height));
        spacerPanel.setMaximumSize(new Dimension(width, height));
        spacerPanel.setPreferredSize(new Dimension(width, height));
        spacerPanel.setBackground(color);
        spacerPanel.setOpaque(true);
        return spacerPanel;
    }
    public static JPanel createSpacingContainer(int width, int height) {
        return createSpacingContainer(width, height, ColorPalette.getRandomColor());
    }

    public static JPanel horizontalSpacerPanel(int amount) {
        JPanel spacerPanel = new JPanel();
        spacerPanel.setMinimumSize(new Dimension(amount, 1));
        spacerPanel.setMaximumSize(new Dimension(amount, 1));
        spacerPanel.setPreferredSize(new Dimension(amount, 1));
        return spacerPanel;
    }
    public static JPanel verticalSpacePanel(int amount) {
        JPanel spacerPanel = new JPanel();
        spacerPanel.setOpaque(false);
        spacerPanel.setMinimumSize(new Dimension(1, amount));
        spacerPanel.setMaximumSize(new Dimension(1, amount));
        spacerPanel.setPreferredSize(new Dimension(1, amount));
        return spacerPanel;
    }
    public static void setupPrettyStringComboBoxV2(StringComboBox comboBox, Color mainColor, int width, int height) {
        if (comboBox == null) { return; }

        // Set the size of the combo box
        comboBox.setMinimumSize(new Dimension(width, height));
        comboBox.setMaximumSize(new Dimension(width, height));
        comboBox.setPreferredSize(new Dimension(width, height));
        comboBox.setEditable(true);

        // Customize the UI to set preferred sizes
        comboBox.setUI(createBonelessComboBoxUI());
        comboBox.setEditor(new OutlineComboBoxEditor(mainColor));
        // Set a custom renderer to change the background color of the selected item
        DefaultListCellRenderer renderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                // Center-align the text
                label.setHorizontalAlignment(SwingConstants.CENTER);

                // Set background color for the selected item
                if (isSelected) {
                    label.setBackground(mainColor); // Dropdown selection color
                    label.setForeground(Color.BLACK); // Text color for contrast
                } else {
                    label.setBackground(Color.WHITE); // Default background color
                    label.setForeground(Color.BLACK); // Default text color
                }

                list.setSelectionForeground(Color.BLACK);
                list.setSelectionBackground(mainColor);
                // Ensure the background color is visible
                label.setOpaque(true);
                return label;
            }
        };
        comboBox.setRenderer(renderer);

    }
    public static JProgressBar getProgressBar0to100() {
        JProgressBar progressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        progressBar.setOpaque(false);
        progressBar.setValue(0);
        return progressBar;
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
            private final OutlineLabel mOutlineLabel = new OutlineLabel();
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

    public static JPanel createVerticalCenteredHoldingPanel(int width, int height) {
        JPanel verticalCenteredHoldingPanel = new GameUI(width, height);
        verticalCenteredHoldingPanel.setLayout(new BoxLayout(verticalCenteredHoldingPanel, BoxLayout.Y_AXIS));
        verticalCenteredHoldingPanel.setPreferredSize(new Dimension(width, height));
        verticalCenteredHoldingPanel.setMinimumSize(new Dimension(width, height));
        verticalCenteredHoldingPanel.setMaximumSize(new Dimension(width, height));
        return verticalCenteredHoldingPanel;
    }
    public static JScrollPane createBonelessScrollingPaneNoVertical(int width, int height, JPanel panel) {

        JScrollPane scrollPane = new JScrollPane(panel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.getViewport().setPreferredSize(new Dimension(width, height));
        scrollPane.getViewport().setMaximumSize(new Dimension(width, height));
        scrollPane.getViewport().setMinimumSize(new Dimension(width, height));
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
        scrollPane.getViewport().setMaximumSize(new Dimension(width, height));
        scrollPane.getViewport().setMinimumSize(new Dimension(width, height));
        scrollPane.setPreferredSize(new Dimension(width, height));

        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
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

    public static void removeAllMouseListeners(JComponent jComponent) {
        if (jComponent == null) { return; }
        for (MouseListener listener : jComponent.getMouseListeners()) {
            jComponent.removeMouseListener(listener);
        }
    }

    public static int getHeightOfAllChildren(JComponent jComponent) {
        int combinedHeight = 0;
        for (Component child : jComponent.getComponents()) {
            combinedHeight += child.getHeight();
        }
        return combinedHeight;
    }



    public static JComponent createTranslucentScrollbar(JComponent component) {
        return createTranslucentScrollbar(-1, -1, component);
    }

//    public static JComponent createTranslucentScrollbar(int width, int height, JComponent component) {
//        JScrollPane scrollPane = new JScrollPane(component) {
//            @Override public boolean isOptimizedDrawingEnabled() {
//                return false; // JScrollBar is overlap
//            }
//        };
//
//        if (width > 0 && height > 0) {
//            scrollPane.getViewport().setPreferredSize(new Dimension(width, height));
//            scrollPane.setPreferredSize(new Dimension(width, height));
//        }
//
//        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
//        scrollPane.setBorder(BorderFactory.createEmptyBorder());
//
//        scrollPane.setHorizontalScrollBarPolicy(
//                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
//        scrollPane.setVerticalScrollBarPolicy(
//                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
//
//        scrollPane.setComponentZOrder(scrollPane.getHorizontalScrollBar(), 0);
//        scrollPane.setComponentZOrder(scrollPane.getViewport(), 1);
//        scrollPane.getHorizontalScrollBar().setOpaque(false);
//        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
//
//        scrollPane.setLayout(new ScrollPaneLayout() {
//            @Override public void layoutContainer(Container parent) {
//                JScrollPane scrollPane = (JScrollPane) parent;
//
//                Rectangle availR = scrollPane.getBounds();
//                availR.x = availR.y = 0;
//
//                Insets insets = parent.getInsets();
//                availR.x = insets.left;
//                availR.y = insets.top;
//                availR.width  -= insets.left + insets.right;
//                availR.height -= insets.top  + insets.bottom;
//
//                Rectangle vsbR = new Rectangle();
//                vsbR.width  = 12;
//                vsbR.height = availR.height;
//                vsbR.x = availR.x + availR.width - vsbR.width;
//                vsbR.y = availR.y;
//
//                if (viewport != null) {
//                    viewport.setBounds(availR);
//                }
//                if (vsb != null) {
//                    vsb.setVisible(true);
//                    vsb.setBounds(vsbR);
//                }
//            }
//        });
//        scrollPane.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
//            private final Color defaultColor  = new Color(220, 100, 100, 100);
//            private final Color draggingColor = new Color(200, 100, 100, 100);
//            private final Color rolloverColor = new Color(255, 120, 100, 100);
//            private final Dimension d = new Dimension();
//            @Override protected JButton createDecreaseButton(int orientation) {
//                return new JButton() {
//                    @Override public Dimension getPreferredSize() {
//                        return d;
//                    }
//                };
//            }
//            @Override protected JButton createIncreaseButton(int orientation) {
//                return new JButton() {
//                    @Override public Dimension getPreferredSize() {
//                        return d;
//                    }
//                };
//            }
//            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {}
//            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
//                Color color;
//                JScrollBar sb = (JScrollBar) c;
//                if (!sb.isEnabled() || r.width > r.height) {
//                    return;
//                } else if (isDragging) {
//                    color = draggingColor;
//                } else if (isThumbRollover()) {
//                    color = rolloverColor;
//                } else {
//                    color = defaultColor;
//                }
//                Graphics2D g2 = (Graphics2D) g.create();
//                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                        RenderingHints.VALUE_ANTIALIAS_ON);
//                g2.setPaint(color);
//                g2.fillRect(r.x, r.y, r.width - 1, r.height - 1);
//                g2.setPaint(Color.WHITE);
//                g2.drawRect(r.x, r.y, r.width - 1, r.height - 1);
//                g2.dispose();
//            }
//            @Override protected void setThumbBounds(int x, int y, int width, int height) {
//                super.setThumbBounds(x, y, width, height);
//                //scrollbar.repaint(x, 0, width, scrollbar.getHeight());
//                scrollbar.repaint();
//            }
//        });
//        return scrollPane;
//    }

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

    public static JScrollPane createTranslucentScrollbarV2(int width, int height, JComponent component) {
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
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

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
            @Override
            protected ComboBoxEditor createEditor() {
                ComboBoxEditor editor1 = super.createEditor();
                editor1.getEditorComponent().setBackground(Color.BLACK); // Set background color to black
                editor1.getEditorComponent().setForeground(Color.WHITE); // Set text color to white for contrast
//                editor1.getEditorComponent().setCaretColor(Color.WHITE); // Set caret color to white for visibility
                return editor1;
            }

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
                                return new Dimension(10, (int) super.getPreferredSize(c).getHeight());
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

    public static void fitTextToLabel(JLabel label) {
        String text = label.getText();
        if (text == null || text.isEmpty()) {
            return; // No text to fit
        }

        int labelWidth = label.getWidth();
        int labelHeight = label.getHeight();

        if (labelWidth <= 0 || labelHeight <= 0) {
            return; // Cannot calculate font size if dimensions are not set
        }

        Font labelFont = label.getFont();
        int fontSize = labelFont.getSize();

        Graphics g = label.getGraphics();
        if (g == null) {
            return; // Ensure graphics context is available
        }

        FontMetrics metrics = g.getFontMetrics(labelFont);

        while (true) {
            // Measure text dimensions
            metrics = g.getFontMetrics(new Font(labelFont.getName(), labelFont.getStyle(), fontSize));
            int textWidth = metrics.stringWidth(text);
            int textHeight = metrics.getHeight();

            // Check if the text fits within the label
            if (textWidth <= labelWidth && textHeight <= labelHeight) {
                break;
            }

            // Reduce the font size to fit
            fontSize--;

            if (fontSize <= 1) {
                break; // Stop if font size is too small
            }
        }
    }

    public static void setStylizedRaisedBevelBorder(JComponent component, int thickness) {
        Border border = BorderFactory.createRaisedBevelBorder();
        Border margin = new EmptyBorder(thickness, thickness,
                thickness, thickness);
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

    public static void register(JComponent component, Keyboard keyboard, MouseV1 mouseV1) {
        if (component == null || component.getComponents() == null)  { return; }
        if (component.getComponents().length == 0) { return; }

        component.addMouseMotionListener(mouseV1);
        component.addMouseListener(mouseV1);
        component.addMouseWheelListener(mouseV1);

        for (Component child : component.getComponents()) {
            JComponent jComponent = (JComponent) child;
            register(jComponent, keyboard, mouseV1);
        }
    }

    public static void recursivelySetBackgroundV2(Color color, JComponent jComponent) {

        Set<JComponent> visited = new HashSet<>();
        Queue<JComponent> toVisit = new LinkedList<>();
        toVisit.add(jComponent);

        while (!toVisit.isEmpty()) {
            JComponent parent = toVisit.poll();

            if (visited.contains(parent)) { continue; }
            visited.add(parent);

            // Actual work
            parent.setOpaque(true);
            parent.setBackground(color);

            for (Component child : parent.getComponents()) {
                if (!(child instanceof JComponent toEnqueue)) { continue; }
                toVisit.add(toEnqueue);
            }
        }
    }

    private static List<JComponent> getChildrenComponents(JComponent jComponent) {
        Set<JComponent> visited = new HashSet<>();
        Queue<JComponent> toVisit = new LinkedList<>();
        toVisit.add(jComponent);

        while (!toVisit.isEmpty()) {
            JComponent parent = toVisit.poll();

            if (visited.contains(parent)) { continue; }
            visited.add(parent);

            for (Component child : parent.getComponents()) {
                if (!(child instanceof JComponent toEnqueue)) { continue; }
                toVisit.add(toEnqueue);
            }
        }

        return new ArrayList<>(visited);
    }

    public static void setBackgroundsFor(Color color, JComponent... jComponents) {

        for (JComponent jComponent : jComponents) {
            if (jComponent == null) { continue; }
            jComponent.setOpaque(true);
            jComponent.setBackground(color);
            recursivelySetBackgroundV2(color, jComponent);
        }
    }

    public static void setBackgroundFor(Color color, JComponent jComponent) {
        jComponent.setOpaque(true);
        jComponent.setBackground(color);
    }

    public static void recursivelySetBackground(Color color, JComponent jComponent) {
        if (jComponent == null) { return; }

        jComponent.setBackground(color);
        List<JComponent> components = getChildrenComponents(jComponent);
        for (JComponent component : components) {
            component.setBackground(color);
        }
    }

    public static void recursivelySetFont(Font font, JComponent jComponent) {
        if (jComponent == null) { return; }

        List<JComponent> components = getChildrenComponents(jComponent);

        for (JComponent component : components) {
            component.setFont(font);
        }

//        jComponent.setFont(font);
//
//        for (Component iteratedComponent : jComponent.getComponents()) {
//            if (iteratedComponent instanceof JComponent iteratedJcomponent) {
//                recursivelySetFont(font, iteratedJcomponent);
//            }
//        }
    }

    public static void setSignatureEtchedBordering(JComponent component) {
        int thickness = 0;
        Border border = component.getBorder();
        Border margin = new EmptyBorder(thickness, thickness + 3,
                thickness, thickness + 3);
        component.setBorder(new CompoundBorder(border, margin));
    }

    public static void setUiBorder(JComponent jComponent) {
        jComponent.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.DARK_GRAY, Color.GRAY));
    }

    public static void setSize(JComponent component, int width, int height) {
        component.setSize(new Dimension(width, height));
        component.setPreferredSize(component.getSize());
        component.setMinimumSize(component.getSize());
        component.setMaximumSize(component.getSize());
    }

    public static JPanel createVerticalPanel(int width, int height, JComponent[] children) {
        // Container Panel
        JPanel parent = new GameUI();
        parent.setBackground(ColorPalette.getRandomColor());
        parent.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));
        parent.setPreferredSize(new Dimension(width, height));
        parent.setMinimumSize(parent.getPreferredSize());
        parent.setMaximumSize(parent.getPreferredSize());

        int childrenHeight = height / children.length;
        int childrenWidth = width;
        for (JComponent child : children) {
            child.setPreferredSize(new Dimension(childrenWidth, childrenHeight));
            child.setAlignmentX(Component.LEFT_ALIGNMENT);
            parent.add(child);
        }
        return parent;
    }

    public static JTextField setHelpText(JTextField textField, String helpText) {
        textField.setToolTipText(helpText + " ");
        textField.setText(helpText);
        return textField;
    }

    public static Border createStylizedCompoundBorder(int mOutlineThickness) {
        return new CompoundBorder(BorderFactory.createCompoundBorder(), new EmptyBorder(mOutlineThickness, mOutlineThickness,
                mOutlineThickness, mOutlineThickness));
    }

    public static void setBoxLayoutSize(JComponent component, int width, int height) {
        component.setMinimumSize(new Dimension(width, height));
        component.setMaximumSize(new Dimension(width, height));
    }

    public static void setupOutlineButton(OutlineButton button) { setupOutlineButton(button, 2); }

    public static void setupOutlineButton(OutlineButton button, int outlineThickness) {
        button.setFocusPainted(false);
        button.setTextOutlineColor(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setOpaque(true);
        button.setTextOutlineThickness(outlineThickness);
        button.setBackground(ColorPalette.CONTROLLER_BUTTON_HIGHLIGHT);
        SwingUiUtils.setStylizedRaisedBevelBorder(button, button.getTextOutlineThickness());
    }

    public static void setupOutlineLabel(OutlineLabel label, int outlineThickness) {
        label.setOpaque(false);
        label.setOutlineColor(Color.BLACK);
        label.setInlineColor(Color.WHITE);

        label.setOutlineColor(Color.BLACK);

        label.setTextOutlineThickness(outlineThickness);
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.LEFT);

        label.setDoubleBuffered(true);
    }


    public static class MyIntFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string,
                                 AttributeSet attr) throws BadLocationException {

            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.insert(offset, string);

            if (test(sb.toString())) {
                super.insertString(fb, offset, string, attr);
            } else {
                // warn the user and don't allow the insert
            }
        }

        private boolean test(String text) {
            try {
                Integer.parseInt(text);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text,
                            AttributeSet attrs) throws BadLocationException {

            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.replace(offset, offset + length, text);

            if (test(sb.toString())) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                // warn the user and don't allow the insert
            }

        }

        @Override
        public void remove(FilterBypass fb, int offset, int length)
                throws BadLocationException {
            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.delete(offset, offset + length);

            if (test(sb.toString())) {
                super.remove(fb, offset, length);
            } else {
                // warn the user and don't allow the insert
            }

        }
    }


    public static class CustomComboBox<T> extends JLabel implements ListCellRenderer {
        private Dimension customDimension = new Dimension(200, 100);
        public void setSize(int width, int height) {
            customDimension = new Dimension(width, height);
        }
        @Override
        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            JLabel label = new JLabel(){
                public Dimension getPreferredSize(){
                    return new Dimension(200, 100);
                }
            };
            label.setText(String.valueOf(value));

            return label;
        }
    }

    public static class MyComboBoxEditor implements ComboBoxEditor {
        private final OutlineLabel field;

        public MyComboBoxEditor(int width, int height) {
            this(null, width, height);
        }
        public MyComboBoxEditor(Color color, int width, int height) {
            field = new OutlineLabel();
            field.setPreferredSize(new Dimension(width, height));
            field.setHorizontalAlignment(JLabel.RIGHT);
            field.setOpaque(true);
            field.setBackground(color);
        }

        @Override
        public Component getEditorComponent() {
            return field;
        }

        @Override
        public void setItem(Object anObject) {
            field.setText(anObject == null ? null : anObject.toString());
        }

        @Override
        public Object getItem() {
            return field.getText();
        }

        @Override
        public void selectAll() {
        }

        @Override
        public void addActionListener(ActionListener l) {

        }

        @Override
        public void removeActionListener(ActionListener l) {}
    }

    public static class EditorPane extends GameUI {

        private JTextField field;
        private JButton button;

//        public EditorPane() {
//            field = new JTextField(10);
//            button = new JButton("X");
//            setLayout(new GridBagLayout());
//            GridBagConstraints gbc = new GridBagConstraints();
//            gbc.weightx = 1;
//            gbc.fill = GridBagConstraints.HORIZONTAL;
//            gbc.gridx = 0;
//            add(field, gbc);
//            gbc.weightx = 0;
//            gbc.fill = GridBagConstraints.NONE;
//            gbc.gridx++;
//            add(button, gbc);
//        }

        public EditorPane(int width, int height) {
            super(width, height);
            field = new JTextField("tooooo");
            field.setPreferredSize(new Dimension(width / 2, height));
            button = new JButton("X");
            button.setPreferredSize(new Dimension(width / 2, height));
//            add(field);
//            add(button);
        }

        @Override
        public void addNotify() {
            super.addNotify();
            field.requestFocusInWindow();
        }

        public void selectAll() {
            field.selectAll();
        }

        public void setText(String text) {
            field.setText(text);
        }

        public String getText() {
            return field.getText();
        }

        public void addActionListener(ActionListener listener) {
            field.addActionListener(listener);
        }

        public void removeActionListener(ActionListener listener) {
            field.removeActionListener(listener);
        }
    }
}