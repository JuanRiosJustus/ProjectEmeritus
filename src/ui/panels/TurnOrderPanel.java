package ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import constants.ColorPalette;
import constants.GameStateKey;
import game.components.Animation;
import game.components.MovementManager;
import game.components.NameTag;
import game.components.Statistics;
import game.entity.Entity;
import game.main.GameModel;
import graphics.JScene;
import graphics.temporary.JImage;
import logging.ELogger;
import logging.ELoggerFactory;
import utils.ComponentUtils;

public class TurnOrderPanel extends JScene {

    private final JPanel queueViewPanel = new JPanel();
    private final Map<Entity, ImageIcon> entityToIcon = new HashMap<>();
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private Entity first = null;
    private final int portraitSize = 30;
    private final int panelWidth = 150;
    private final int panelHeight = 60;

    private final Color turnIsOver = ColorPalette.TRANSPARENT_BLACK;
    private final Color turnIsUpcoming = ColorPalette.TRANSPARENT_BLACK;
    private final Color turnIsNow = ColorPalette.TRANSPARENT_WHITE;

    public TurnOrderPanel(int width, int height) {
        super(width, height, TurnOrderPanel.class.getSimpleName());

        JPanel contentPane = contentPane(width, height);
        add(contentPane);

        setOpaque(false);
    }

    private JPanel contentPane(int width, int height) {

        ComponentUtils.setTransparent(queueViewPanel);
        JScrollPane scrollPane = new JScrollPane(queueViewPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        ComponentUtils.setTransparent(scrollPane);

        int imagesToShow = width / panelWidth;

        for (int i = 0; i < imagesToShow; i++) {
            JImage jImage = new JImage(new ImageIcon());
            jImage.setBorder(new EtchedBorder(ColorPalette.WHITE, ColorPalette.BEIGE));
            jImage.setPreferredSize(new Dimension(panelWidth, panelHeight));
            jImage.silenceButton();
            queueViewPanel.add(jImage);
        }

        // Put the scene on bottom left corner
        JPanel b1 = ComponentUtils.createTransparentPanel(new BorderLayout(10, 10));
        b1.add(queueViewPanel, BorderLayout.LINE_START);
        b1.setBackground(ColorPalette.TRANSPARENT);
        b1.setOpaque(true);
        b1.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel b2 = ComponentUtils.createTransparentPanel(new BorderLayout(10, 10));
        b2.add(b1, BorderLayout.PAGE_END);
        b2.setBackground(ColorPalette.TRANSPARENT);
        b2.setOpaque(true);
        b2.setBorder(new EmptyBorder(10, 10, 10, 10));

        return b2;
    }

    public void update(GameModel model) {
        // Check if the queue has changed since last time
        if (first == model.speedQueue.peek()) { return; }
        first = model.speedQueue.peek();
        // if the the inner queue is empty, initialize it and create the 

        List<Entity> queue = model.speedQueue.getAvailableTurnQueue();
        
        Queue<Entity> toPlace = new LinkedList<>(queue);
        int indexToPlace = 0;
        while (!toPlace.isEmpty()) {
            // ensure appropriately size icons are available
            Entity entity = toPlace.poll();
            Animation animation = entity.get(Animation.class);
            ImageIcon icon = entityToIcon.get(entity);
            if (icon == null) {
                Image newImage = animation.toImage().getScaledInstance(portraitSize, portraitSize, Image.SCALE_SMOOTH);
                ImageIcon newIcon = new ImageIcon(newImage);
                entityToIcon.put(entity, newIcon);
                icon = newIcon;
            }
                
            if (indexToPlace >= queueViewPanel.getComponentCount()) { continue; }
            JImage image = (JImage) queueViewPanel.getComponent(indexToPlace);
            image.setImage(icon);
            image.setText(entity.get(NameTag.class).toString());
            image.setBackground(turnIsUpcoming);
            image.setVisible(true);
            image.removeAllListeners();
            image.setAction(e -> {
                model.state.set(GameStateKey.CURRENTLY_SELECTED, entity.get(MovementManager.class).currentTile);
                model.state.set(GameStateKey.ZOOM_TOO_SELECTED, true);
            });
            indexToPlace++;
        }

        toPlace.clear();
        toPlace.addAll(model.speedQueue.getFinishedTurnQueue());
        while (!toPlace.isEmpty()) {
            // ensure appropriately size icons are available
            Entity entity = toPlace.poll();
            Animation animation = entity.get(Animation.class);
            ImageIcon icon = entityToIcon.get(entity);
            if (icon == null) {
                Image newImage = animation.toImage().getScaledInstance(portraitSize, portraitSize, Image.SCALE_SMOOTH);
                ImageIcon newIcon = new ImageIcon(newImage);
                entityToIcon.put(entity, newIcon);
                icon = newIcon;
            }
                
            if (indexToPlace >= queueViewPanel.getComponentCount()) { continue; }
            JImage image = (JImage) queueViewPanel.getComponent(indexToPlace);
            image.setVisible(false);
            image.setImage(icon);
            image.setText(entity.get(Statistics.class).toString());
            image.setBackground(turnIsOver);
            image.removeAllListeners();
            image.setAction(e -> {
                model.state.set(GameStateKey.CURRENTLY_SELECTED, entity.get(MovementManager.class).currentTile);
                model.state.set(GameStateKey.ZOOM_TOO_SELECTED, true);
            });
            indexToPlace++;
        }

        if (indexToPlace < queueViewPanel.getComponentCount()) {
            JImage image = (JImage) queueViewPanel.getComponent(indexToPlace);
            image.setVisible(false);
            indexToPlace++;
        }


        JImage image = (JImage) queueViewPanel.getComponent(0);
        image.setBackground(turnIsNow);

        logger.info("Updated Panel turn order panel ");
    }
}
