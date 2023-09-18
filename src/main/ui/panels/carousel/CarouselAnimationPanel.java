package main.ui.panels.carousel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class CarouselAnimationPanel extends JPanel implements ActionListener {
    Image bg = new ImageIcon(Carousel.class.getResource("bg.png")).getImage();
    int x;

    public CarouselAnimationPanel() {
        Timer timer = new Timer(500, this);
        timer.setInitialDelay(2000);
        timer.start();
    }

    public void paint(Graphics g) {
        // g.drawImage(bg, 0, 0, this.getParent().getSize().width, this.getParent().getSize().height, null);
        // g.setColor(Color.red);
        // g.fillRect(x, x, 50, 50);
        Carousel carousel = (Carousel) this.getParent();
        for (int x = 1; x < carousel.getComponentCount(); x++) {
            System.out.println(carousel.getComponent(x));
            CarouselLayout layout = (CarouselLayout) carousel.getLayout();
//			layout.
            carousel.getComponent(x).setBounds(x * 400, x * 400, 400, 400);
            carousel.getComponent(x).paint(g);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // x = new Random().nextInt(200);
        repaint();
    }
}