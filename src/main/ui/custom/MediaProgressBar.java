package main.ui.custom;

import main.game.stores.pools.ColorPalette;

import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ProgressBarUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class MediaProgressBar extends JProgressBar {

    public static class MediaProgressBarUI extends BasicProgressBarUI {

        private Handler handler;
        private double renderProgress = 0;
        private double targetProgress = 0;
        private double progressDelta = 0.04;
        private Timer repaintTimer;
        private Timer paintTimer;
        private Color mBackground = Color.GREEN;

        public void setBackground(Color color) {
            mBackground = color;
        }

        public MediaProgressBarUI() {
            repaintTimer = new Timer(25, e -> progressBar.repaint());
            repaintTimer.setRepeats(false);
            repaintTimer.setCoalesce(true);

            paintTimer = new Timer(40, e -> {
                if (progressDelta < 0) {

                    if (renderProgress + progressDelta < targetProgress) {
                        ((Timer) e.getSource()).stop();
                        renderProgress = targetProgress + progressDelta;
                    }

                } else {

                    if (renderProgress + progressDelta > targetProgress) {
                        ((Timer) e.getSource()).stop();
                        renderProgress = targetProgress - progressDelta;
                    }

                }
                renderProgress += progressDelta;
                requestRepaint();
            });
        }

        public MediaProgressBarUI(Color color) {
            setBackground(color);
            repaintTimer = new Timer(25, e -> progressBar.repaint());
            repaintTimer.setRepeats(false);
            repaintTimer.setCoalesce(true);

            paintTimer = new Timer(40, e -> {
                if (progressDelta < 0) {

                    if (renderProgress + progressDelta < targetProgress) {
                        ((Timer) e.getSource()).stop();
                        renderProgress = targetProgress + progressDelta;
                    }

                } else {

                    if (renderProgress + progressDelta > targetProgress) {
                        ((Timer) e.getSource()).stop();
                        renderProgress = targetProgress - progressDelta;
                    }

                }
                renderProgress += progressDelta;
                requestRepaint();
            });
        }

        protected void requestRepaint() {
            repaintTimer.restart();
        }

        @Override
        protected void installDefaults() {
            super.installDefaults();
            progressBar.setOpaque(false);
            progressBar.setBorder(null);
        }

        public void setRenderProgress(double value) {
            if (value != targetProgress) {
                paintTimer.stop();

                targetProgress = value;
                if (targetProgress < renderProgress && progressDelta > 0) {
                    progressDelta *= -1;
                } else if (targetProgress > renderProgress && progressDelta < 0) {
                    progressDelta *= -1;
                }

                paintTimer.start();
            }
        }

        public double getRenderProgress() {
            return renderProgress;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            super.paint(g, c);

            int width = c.getWidth();
            int height = c.getHeight();
            Graphics2D g2d = (Graphics2D) g;

            g2d.setPaint(ColorPalette.BLACK);
            g2d.fillRect(0, 0, width, height);

            Color borderColor = ColorPalette.BLACK;
            int strokeWidth = 2;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
            g2d.setColor(borderColor);
            g2d.setBackground(borderColor);

            Color barColor = mBackground;
            Color topBarColor = barColor.brighter();
            Color bottomColor = topBarColor.brighter();

            Rectangle2D outline = new Rectangle2D.Double(((double) strokeWidth / 2), ((double) strokeWidth / 2),
                    width - strokeWidth, height - strokeWidth);

            g2d.draw(outline);

            int iInnerHeight = height - (strokeWidth * 4);
            int iInnerWidth = width - (strokeWidth * 4);

            iInnerWidth = (int) Math.round(iInnerWidth * renderProgress);

            int x = strokeWidth * 2;
            int y = strokeWidth * 2;

            Point2D start = new Point2D.Double(x, y);
            Point2D end = new Point2D.Double(x, y + iInnerHeight);

            float[] dist = {0.0f, 0.5f, 1.0f};
            Color[] colors = {barColor, topBarColor, bottomColor};
            LinearGradientPaint p = new LinearGradientPaint(start, end, dist, colors);

            g2d.setPaint(p);

            Rectangle2D fill = new Rectangle2D.Double(strokeWidth * 2, strokeWidth * 2,
                    iInnerWidth, iInnerHeight);

            g2d.fill(fill);
        }

//        @Override
//        public void paint(Graphics g, JComponent c) {
////            super.paint(g, c);
//            Graphics2D g2d = (Graphics2D) g.create();
//
////            Color borderColor = ColorPalette.RED;
////            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
////            g2d.setStroke(new BasicStroke(iStrokWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
////            g2d.setColor(borderColor);
////            g2d.setBackground(borderColor);
//
//            int strokeWidth = 3;
//            int width = c.getWidth();
//            int height = c.getHeight();
//            Color barColor = ColorPalette.RED;
//
////            RoundRectangle2D outline = new RoundRectangle2D.Double(((double) strokeWidth / 2), ((double) strokeWidth / 2),
////                    width - strokeWidth, height - strokeWidth,
////                    height, height);
////
////            g2d.draw(outline);
//
//            int iInnerHeight = height - (strokeWidth * 4);
//            int iInnerWidth = width - (strokeWidth * 4);
//
//            iInnerWidth = (int) Math.round(iInnerWidth * renderProgress);
//
//            int x = strokeWidth * 2;
//            int y = strokeWidth * 2;
//
//            Point2D start = new Point2D.Double(x, y);
//            Point2D end = new Point2D.Double(x, y + iInnerHeight);
//
//            float[] dist = {0.0f, 0.25f, 1.0f};
//            Color[] colors = {barColor, barColor.brighter(), barColor.darker()};
//            LinearGradientPaint p = new LinearGradientPaint(start, end, dist, colors);
//
//            g2d.setPaint(p);
//
//            RoundRectangle2D fill = new RoundRectangle2D.Double(strokeWidth * 2, strokeWidth * 2,
//                    iInnerWidth, iInnerHeight, iInnerHeight, iInnerHeight);
//
//            g2d.fill(fill);
//
//            g2d.dispose();
//        }


        @Override
        protected void installListeners() {
            super.installListeners();
            progressBar.addChangeListener(getChangeHandler());
        }

        protected ChangeListener getChangeHandler() {
            return getHandler();
        }

        protected Handler getHandler() {
            if (handler == null) {
                handler = new Handler();
            }
            return handler;
        }

        protected class Handler implements ChangeListener {

            @Override
            public void stateChanged(ChangeEvent e) {

                BoundedRangeModel model = progressBar.getModel();
                int newRange = model.getMaximum() - model.getMinimum();
                double dProgress = model.getValue() / (double) newRange;

                if (dProgress < 0) {
                    dProgress = 0;
                } else if (dProgress > 1) {
                    dProgress = 1;
                }

                setRenderProgress(dProgress);
            }

        }

    }

    public MediaProgressBar() {
        setUI(new MediaProgressBarUI(){
            protected Color getSelectionBackground() { return Color.black; }
            protected Color getSelectionForeground() { return Color.white; }
        });
    }

    public MediaProgressBar(Color color) {
        setUI(new MediaProgressBarUI(color){
            protected Color getSelectionBackground() { return Color.black; }
            protected Color getSelectionForeground() { return Color.white; }
        });
    }

    @Override
    public void update(Graphics g) {
        paintComponent(g);
    }
}