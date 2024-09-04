package main.ui.custom;

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

    public static void main(String[] args) {
        new TestMediaProgressBar();
    }

    public TestMediaProgressBar() {
        EventQueue.invokeLater(() -> {
//                try {
//                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
//                }

            JFrame frame = new JFrame("Testing");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new TestPane());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
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

            Timer timer = new Timer(500, e -> {
                if (value + delta > 100) {
                    delta *= -1;
                    value = 100;
                } else if (value + delta < 0) {
                    delta *= -1;
                    value = 0;
                }
                value += delta;
                pb.setValue(value);
            });
            timer.start();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(200, 200);
        }
    }

}