package main.ui.presets.internet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class TestMediaProgressBar {
    // https://stackoverflow.com/questions/20136505/java-making-a-pretty-jprogressbar

    public static void main(String[] args) {
        new TestMediaProgressBar();
    }

    public TestMediaProgressBar() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

                JFrame frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new TestPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public class TestPane extends JPanel {

        private MediaProgressBar pb;
        private int value = 0;
        private int delta = 25;

        public TestPane() {
            setBackground(Color.BLACK);
            setLayout(new GridBagLayout());
            pb = new MediaProgressBar();
            add(pb);

            Timer timer = new Timer(500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (value + delta > 100) {
                        delta *= -1;
                        value = 100;
                    } else if (value + delta < 0) {
                        delta *= -1;
                        value = 0;
                    }
                    value += delta;
                    pb.setValue(value);
                }
            });
            timer.start();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(200, 200);
        }
    }

    public class MediaProgressBar extends JProgressBar {

        public MediaProgressBar() {
            setUI(new MediaProgressBarUI());
        }

        @Override
        public Dimension getPreferredSize() {

            return new Dimension(128, 24);

        }

    }

    public class MediaProgressBarUI extends BasicProgressBarUI {

        private Handler handler;
        private double renderProgress = 0;
        private double targetProgress = 0;
        private double progressDelta = 0.04;
        private Timer repaintTimer;
        private Timer paintTimer;

        public MediaProgressBarUI() {
            repaintTimer = new Timer(25, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    progressBar.repaint();
                }
            });
            repaintTimer.setRepeats(false);
            repaintTimer.setCoalesce(true);

            paintTimer = new Timer(40, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
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
                }
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
                System.out.println(progressDelta);

                paintTimer.start();
            }
        }

        public double getRenderProgress() {
            return renderProgress;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int iStrokWidth = 3;
            g2d.setStroke(new BasicStroke(iStrokWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.setColor(c.getBackground());
            g2d.setBackground(c.getBackground());

            int width = c.getWidth();
            int height = c.getHeight();

            RoundRectangle2D outline = new RoundRectangle2D.Double((iStrokWidth / 2), (iStrokWidth / 2),
                    width - iStrokWidth, height - iStrokWidth,
                    height, height);

            g2d.draw(outline);

            int iInnerHeight = height - (iStrokWidth * 4);
            int iInnerWidth = width - (iStrokWidth * 4);

            iInnerWidth = (int) Math.round(iInnerWidth * renderProgress);

            int x = iStrokWidth * 2;
            int y = iStrokWidth * 2;

            Point2D start = new Point2D.Double(x, y);
            Point2D end = new Point2D.Double(x, y + iInnerHeight);

            float[] dist = {0.0f, 0.25f, 1.0f};
            Color[] colors = {c.getBackground(), c.getBackground().brighter(), c.getBackground().darker()};
            LinearGradientPaint p = new LinearGradientPaint(start, end, dist, colors);

            g2d.setPaint(p);

            RoundRectangle2D fill = new RoundRectangle2D.Double(iStrokWidth * 2, iStrokWidth * 2,
                    iInnerWidth, iInnerHeight, iInnerHeight, iInnerHeight);

            g2d.fill(fill);

            g2d.dispose();
        }

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
                double dProgress = (double) (model.getValue() / (double) newRange);

                if (dProgress < 0) {
                    dProgress = 0;
                } else if (dProgress > 1) {
                    dProgress = 1;
                }

                setRenderProgress(dProgress);

            }

        }

    }

}