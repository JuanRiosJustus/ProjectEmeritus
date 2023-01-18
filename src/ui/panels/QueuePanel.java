package ui.panels;

import constants.ColorPalette;
import game.components.Animation;
import game.entity.Entity;
import game.queue.SpeedQueue;
import graphics.JScene;
import graphics.temporary.JImage;
import utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class QueuePanel extends JScene {

    private final JPanel queueViewPanel = new JPanel();
    private final Deque<Entity> queueViewPanelList = new ConcurrentLinkedDeque<>();
    private final Deque<Entity> queueViewPanelListLeftovers = new ConcurrentLinkedDeque<>();
    private final Map<Entity, ImageIcon> entityToIcon = new HashMap<>();
    private final BufferedImage empty = ImageUtils.empty(30, 30);
    private final int ENTITIES_TO_SHOW = 5;

    public QueuePanel(int width, int height) {
        super(width, height, "TurnOrder");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(queueViewPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        for (int i = 0; i < ENTITIES_TO_SHOW; i++) {
            queueViewPanel.add(new JImage(null));
        }

        add(scrollPane);
    }

    public void dequeue() {
        if (queueViewPanelList.size() > 0) {
            queueViewPanel.remove(0);
            queueViewPanelList.removeFirst();

            JImage image;

            if (queueViewPanelListLeftovers.size() > 0) {
                Entity entity = queueViewPanelListLeftovers.remove();
                ImageIcon icon = entityToIcon.get(entity);
                image = new JImage(icon);
            } else {
                image = new JImage(new ImageIcon(empty));
            }
            queueViewPanel.add(image);
        }
        setImageToCurrent();
    }

    public void set(SpeedQueue queue) {

//        if (queueViewPanelList.size() > 1) { return; }

        queueViewPanelListLeftovers.clear();
        queueViewPanel.removeAll();
        queueViewPanelList.clear();

        List<Entity> copyOfQueue = queue.getOrdering();

        for (Entity entity : copyOfQueue) {
            Animation animation = entity.get(Animation.class);
            ImageIcon icon = entityToIcon.get(entity);
            if (icon == null) {
                Image newImage = animation.toImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
                ImageIcon newIcon = new ImageIcon(newImage);
                entityToIcon.put(entity, newIcon);
                icon = entityToIcon.get(entity);
            }

            if (queueViewPanelList.size() < ENTITIES_TO_SHOW) {
                queueViewPanel.add(new JImage(icon));
            } else {
                queueViewPanelListLeftovers.add(entity);
            }
            queueViewPanelList.add(entity);
        }

        setImageToCurrent();
    }

    private void setImageToCurrent() {
        if (queueViewPanel.getComponents().length == 0) { return; }
        if (queueViewPanelList.isEmpty()) { return; }
        Entity entity = queueViewPanelList.peek();
        JImage image = (JImage) queueViewPanel.getComponent(0);
        ImageIcon icon = entityToIcon.get(entity);
        image.setImage(icon);
        image.setBackground(ColorPalette.TRANSPARENT_RED);
        queueViewPanel.revalidate();
        queueViewPanel.repaint();
    }
}
