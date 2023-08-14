package main.ui.huds;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import javax.swing.*;

import main.constants.ColorPalette;
import main.ui.GameState;
import main.game.components.Animation;
import main.game.components.MovementManager;
import main.game.components.Identity;
import main.game.components.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.FontPool;
import main.graphics.JScene;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class TurnOrderTimelineHUD extends JScene {

    private final List<JButton> timelineItems = new ArrayList<>();
    private final Map<Entity, ImageIcon> entityToIcon = new HashMap<>();
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private Entity first = null;
    private Entity selected = null;
    private int portraitSize;
    private int portraitWidths;
    private int portraitHeights;

    private final Color availableNextRound = ColorPalette.TRANSPARENT_RED;
    private final Color availableThisRound = ColorPalette.TRANSPARENT_GREEN;

    public TurnOrderTimelineHUD(int width, int height) {
        super(width, height, TurnOrderTimelineHUD.class.getSimpleName());

        JPanel contentPane = contentPane(width, height);
        add(contentPane);

        setOpaque(false);
    }

    private JPanel contentPane(int width, int height) {

        portraitWidths = (int) (width * .3);
        portraitHeights = (int) (height * .2);
        portraitSize = (int) (Math.min(portraitHeights, portraitWidths) * 2);

        JPanel container = new JPanel();
        container.setLayout(new GridBagLayout());
        container.setPreferredSize(new Dimension(width, height));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel label = new JLabel("Turn");
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setHorizontalAlignment(SwingConstants.HORIZONTAL);
        label.setForeground(ColorPalette.WHITE);
        label.setFont(FontPool.getInstance().getFont(label.getFont().getSize()));
        label.setPreferredSize(new Dimension(portraitWidths, portraitHeights / 2));
        container.add(label, gbc);

        gbc.gridx = 1;
        label = new JLabel("Next");
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setHorizontalAlignment(SwingConstants.HORIZONTAL);
        label.setForeground(ColorPalette.WHITE);
        label.setFont(FontPool.getInstance().getFont(label.getFont().getSize()));
        label.setPreferredSize(new Dimension(portraitWidths, portraitHeights / 2));
        container.add(label, gbc);

        int columns = 8;

        for (int i = 2; i < columns; i++) {
            gbc.gridx = i;
            label = new JLabel("" + i);
            label.setFont(FontPool.getInstance().getFont(label.getFont().getSize()));
            label.setHorizontalAlignment(SwingConstants.HORIZONTAL);
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            label.setForeground(ColorPalette.WHITE);
            label.setPreferredSize(new Dimension(portraitWidths, portraitHeights / 2));
            container.add(label, gbc);
        }

        gbc.gridy = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;

        for (int i = 0; i < columns; i++) {
            JButton timelineItem = new JButton();
            timelineItem.setBackground(ColorPalette.TRANSPARENT);
            timelineItem.setForeground(ColorPalette.WHITE);
            timelineItem.setFont(FontPool.getInstance().getFont(timelineItem.getFont().getSize()));
//            timelineItem.setFont(timelineItem.getFont().deriveFont(Font.BOLD));
            timelineItem.setOpaque(true);
            timelineItem.setBorderPainted(false);
            timelineItem.setFocusPainted(false);
//            timelineItem.setBackground(ColorPalette.getRandomColor());
            timelineItem.setPreferredSize(new Dimension(portraitWidths, portraitHeights / 2));

            container.add(timelineItem, gbc);
            gbc.gridx = gbc.gridx + 1;
            timelineItems.add(timelineItem);
        }

        container.setPreferredSize(new Dimension(width,
                (int) (label.getPreferredSize().getHeight() )));
        container.setBackground(ColorPalette.TRANSLUCENT_BLACK_V1);
        container.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createLineBorder(Color.black),
                BorderFactory.createLineBorder(Color.black)
//                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)
        ));
        return container;
    }

    private void removeAllListeners(JButton toRemove) {
        for (ActionListener listener : toRemove.getActionListeners()) {
            toRemove.removeActionListener(listener);
        }
    }

    @Override
    public void jSceneUpdate(GameModel model) {
        // Check if the queue has changed since last time
        Entity userSelected = (Entity) model.gameState.getObject(GameState.CURRENTLY_SELECTED);
        boolean isNonNullAndAlreadySelecting = userSelected != null && userSelected.get(Tile.class).unit == selected;
        if (first == model.speedQueue.peek() && isNonNullAndAlreadySelecting) { return; }
        first = model.speedQueue.peek();
        selected = userSelected == null ? null : userSelected.get(Tile.class).unit;
        // if the inner queue is empty, initialize it and create the
        List<Entity> available = model.speedQueue.getAvailable();
        Queue<Entity> toPlace = new LinkedList<>(available);
        int index = 0;
        Color thisRoundsColor = availableThisRound;
        Color nextRoundsColor = availableNextRound;

        List<Entity> availableNextTurn = model.speedQueue.getFinished();
        if (toPlace.size() < timelineItems.size()) {
            toPlace.addAll(availableNextTurn);
        }
        toPlace.addAll(availableNextTurn);
        while (!toPlace.isEmpty()) {
            Entity entity = toPlace.poll();
            Animation animation = entity.get(Animation.class);
            ImageIcon icon = entityToIcon.get(entity);
            if (icon == null) {
                Image newImage = animation.toImage().getScaledInstance(portraitSize, portraitSize, Image.SCALE_SMOOTH);
                ImageIcon newIcon = new ImageIcon(newImage);
                entityToIcon.put(entity, newIcon);
                icon = newIcon;
            }
            // if the index is larger than the amount of spaces, exit/return
            if (index > timelineItems.size() - 1) { continue; }
            // if index is more than than, return
            if (index > available.size() + availableNextTurn.size() - 1) { continue; }

            JButton image = timelineItems.get(index);
            boolean isSelected = false;
            if (userSelected != null && userSelected.get(Tile.class).unit == entity) {
                image.setBackground(ColorPalette.TRANSPARENT_WHITE);
                isSelected = true;
            }
            if (index < available.size()) {
                if (!isSelected) { image.setBackground(thisRoundsColor); }
                thisRoundsColor = thisRoundsColor.darker();
            } else if (index < available.size() + availableNextTurn.size()) {
                if (!isSelected) { image.setBackground(nextRoundsColor); }
                nextRoundsColor = nextRoundsColor.darker();
            } else {
                image.setBackground(ColorPalette.TRANSPARENT);
                continue;
            }
            image.setIcon(icon);
            image.setText(entity.get(Identity.class).getName());
            removeAllListeners(image);
            image.addActionListener(e ->{
                model.gameState.set(GameState.CURRENTLY_SELECTED, entity.get(MovementManager.class).currentTile);
                model.gameState.set(GameState.GLIDE_TO_SELECTED, true);
            });
            image.setVisible(true);
            index++;
        }

        while (index < timelineItems.size()) {
            JButton image = timelineItems.get(index);
            image.setBackground(ColorPalette.TRANSPARENT);
            image.setVisible(false);
            index++;
        }

        logger.info("Updated Panel turn order panel ");
    }
}
