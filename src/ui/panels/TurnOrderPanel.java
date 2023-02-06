package ui.panels;

import constants.ColorPalette;
import constants.GameStateKey;
import game.GameModel;
import game.components.MovementManager;
import game.components.Name;
import game.components.Animation;
import game.entity.Entity;
import graphics.JScene;
import graphics.temporary.JImage;
import logging.Logger;
import logging.LoggerFactory;
import utils.ComponentUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TurnOrderPanel extends JScene {
    private final JPanel queueViewPanel = new JPanel();
    private final Map<Entity, ImageIcon> entityToIcon = new HashMap<>();
    private final int ENTITIES_TO_SHOW = 6;
    private final Logger logger = LoggerFactory.instance().logger(getClass());
    private Entity first = null;

    public TurnOrderPanel(int width, int height) {
        super(width, height, "End Turn");
        add(contentPane(width, height));
        setBackground(ColorPalette.TRANSPARENT);
        setOpaque(false);
    }

    private JPanel contentPane(int width, int height) {

        ComponentUtils.setTransparent(queueViewPanel);
        JScrollPane scrollPane = new JScrollPane(queueViewPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        ComponentUtils.setTransparent(scrollPane);

        for (int i = 0; i < ENTITIES_TO_SHOW; i++) {
            JImage jImage = new JImage(new ImageIcon());
//            ComponentUtils.setTransparent(jImage);
            jImage.setBorder(new EtchedBorder(ColorPalette.RED, ColorPalette.BEIGE));
            jImage.setPreferredSize(new Dimension(150, 80));
            queueViewPanel.add(jImage);
        }

        // Put the scene on bottom right corner
        JPanel b1 = ComponentUtils.createTransparentPanel(new BorderLayout(10, 10));
        b1.add(queueViewPanel, BorderLayout.LINE_START);
        b1.setBackground(ColorPalette.TRANSPARENT);
        b1.setOpaque(false);
        b1.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel b2 = ComponentUtils.createTransparentPanel(new BorderLayout(10, 10));
        b2.add(b1, BorderLayout.PAGE_END);
        b2.setBackground(ColorPalette.TRANSPARENT);
        b2.setOpaque(false);
        b2.setBorder(new EmptyBorder(10, 10, 10, 10));

        return b2;
    }

    public void update(GameModel model) {
        // Check if the queue has changed since last time
        if (first == model.unitTurnQueue.peek()) { return; }
        first = model.unitTurnQueue.peek();

        List<Entity> copyOfQueue = model.unitTurnQueue.getOrdering();

        // Get a copy of each sprite to show
        int index = 0;

        for (Entity entity : copyOfQueue) {
            Animation animation = entity.get(Animation.class);
            ImageIcon icon = entityToIcon.get(entity);
            if (icon == null) {
                Image newImage = animation.toImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                ImageIcon newIcon = new ImageIcon(newImage);
                entityToIcon.put(entity, newIcon);
                icon = newIcon;
            }

            if (index < queueViewPanel.getComponents().length) {
                JImage image = (JImage) queueViewPanel.getComponent(index);
                image.setVisible(true);
                image.setImage(icon);
                image.setText(entity.get(Name.class).value);
                image.removeAllListeners();
                image.setAction(e -> {
                    model.state.set(GameStateKey.CURRENTLY_SELECTED, entity.get(MovementManager.class).currentTile);
                    model.state.set(GameStateKey.ZOOM_TOO_SELECTED, true);
                });
            }
            index++;
        }

        while (index < queueViewPanel.getComponents().length) {
            JImage image = (JImage) queueViewPanel.getComponent(index);
            image.setVisible(false);
            index++;
        }

        JImage image = (JImage) queueViewPanel.getComponent(0);
        image.setBackground(ColorPalette.TRANSPARENT_BLACK);

        revalidate();
        repaint();
        logger.log("Updated Panel turn order panel " + copyOfQueue);

    }
}
