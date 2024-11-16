package main.ui.custom;

import main.game.stores.pools.ColorPalette;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class TranslucentScrollPane extends JScrollPane {
    public TranslucentScrollPane(JComponent component, int width, int height) {
        this(component, ColorPalette.getRandomColor(), width, height);
    }
    public TranslucentScrollPane(JComponent component, Color mainColor, int width, int height) {
        super(component);

        if (width > 0 && height > 0) {
            getViewport().setPreferredSize(new Dimension(width, height));
            setPreferredSize(new Dimension(width, height));
        }

        // Hide both scrollbars
        getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        setBorder(BorderFactory.createEmptyBorder());

        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        setComponentZOrder(getVerticalScrollBar(), 0);
        setComponentZOrder(getViewport(), 1);
        getVerticalScrollBar().setOpaque(false);
        getVerticalScrollBar().setUnitIncrement(16);
        getViewport().setOpaque(false);
        setOpaque(false);

        // Remove the paint methods to keep them completely transparent
        getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {}
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {}
            @Override protected JButton createDecreaseButton(int orientation) {
                return createInvisibleButton();
            }
            @Override protected JButton createIncreaseButton(int orientation) {
                return createInvisibleButton();
            }

            private JButton createInvisibleButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });

        getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {}
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {}
            @Override protected JButton createDecreaseButton(int orientation) {
                return createInvisibleButton();
            }
            @Override protected JButton createIncreaseButton(int orientation) {
                return createInvisibleButton();
            }

            private JButton createInvisibleButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
    }
//    public TranslucentScrollPane(JComponent component, Color mainColor,  int width, int height) {
//        super(component);
//
//        if (width > 0 && height > 0) {
//            getViewport().setPreferredSize(new Dimension(width, height));
//            setPreferredSize(new Dimension(width, height));
//        }
//
//        getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
//        setBorder(BorderFactory.createEmptyBorder());
//
//        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//
//        setComponentZOrder(getVerticalScrollBar(), 0);
//        setComponentZOrder(getViewport(), 1);
//        getVerticalScrollBar().setOpaque(false);
//        getVerticalScrollBar().setUnitIncrement(1);
//        getViewport().setOpaque(false);
//        setOpaque(false);
//
//        setLayout(new ScrollPaneLayout() {
//            @Override public void layoutContainer(Container parent) {
//                JScrollPane scrollPane = (JScrollPane) parent;
//
//                Rectangle availR = getBounds();
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
//
//        final Color color =  new Color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), 100);
//        getVerticalScrollBar().setUI(new BasicScrollBarUI() {
////            private final Color defaultColor  = new Color(220, 100, 100, 100);
////            private final Color draggingColor = new Color(200, 100, 100, 100);
////            private final Color rolloverColor = new Color(255, 120, 100, 100);
//            private final Color defaultColor = color;
//            private final Color draggingColor = color.darker();
//            private final Color rolloverColor = color.brighter();
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
//                scrollbar.repaint(x, 0, width, scrollbar.getHeight());
////                scrollbar.repaint();
//            }
//        });
//    }
    @Override
    public boolean isOptimizedDrawingEnabled() {
        return false; // JScrollBar is overlap
    }
}
