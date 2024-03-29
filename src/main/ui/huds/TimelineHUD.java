package main.ui.huds;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import javax.swing.*;

import main.game.stores.pools.ColorPalette;
import main.constants.GameState;
import main.game.components.Animation;
import main.game.components.MovementManager;
import main.game.components.Identity;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.FontPool;
import main.graphics.JScene;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.ComponentUtils;

public class TimelineHUD extends JScene {

    private final List<JButton> timelineItems = new ArrayList<>();
    private final Map<Entity, ImageIcon> entityToIcon = new HashMap<>();
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private Entity first = null;
    private Entity selected = null;
    private int portraitSize;
    private int portraitWidths;
    private int portraitHeights;

    private final Color availableNextRound = ColorPalette.TRANSPARENT_RED;
    private final Color availableThisRound = ColorPalette.TRANSLUCENT_GREEN_V2;
    private final Color availableAndCurrent = ColorPalette.TRANSLUCENT_WHITE_V1;

    public TimelineHUD(int width, int height) {
        super(width, height, TimelineHUD.class.getSimpleName());

        JPanel contentPane = contentPane(width, height);
        add(contentPane);

        ComponentUtils.disable(this);
        setOpaque(false);
//        setOpaque(true);
//        setBackground(ColorPalette.BLUE);
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

        int columns = 8;

        gbc.gridy = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.fill = GridBagConstraints.BOTH;

        for (int i = 0; i < columns; i++) {
            gbc.gridy = 0;
            gbc.gridx = i;
            String str;
            switch (i) {
                case 0 -> str = "Current";
                case 1 -> str = "Next";
                default -> str = i + "";
            }
            JLabel label = new JLabel(str);
            label.setFont(FontPool.getInstance().getFont(label.getFont().getSize()));
            label.setHorizontalAlignment(SwingConstants.HORIZONTAL);
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            label.setForeground(ColorPalette.WHITE);
            label.setBackground(ColorPalette.TRANSLUCENT_BLACK_V1);
            label.setPreferredSize(new Dimension(portraitWidths, portraitHeights / 2));
            container.add(label, gbc);


            gbc.gridy = 1;
            JButton timelineItem = new JButton();
            timelineItem.setBackground(ColorPalette.TRANSLUCENT_BLACK_V1);
            timelineItem.setForeground(ColorPalette.WHITE);
            timelineItem.setFont(FontPool.getInstance().getFont(timelineItem.getFont().getSize()).deriveFont(Font.BOLD));
            timelineItem.setFont(timelineItem.getFont().deriveFont(Font.BOLD));
            timelineItem.setOpaque(true);
            timelineItem.setBorderPainted(false);
            timelineItem.setFocusPainted(false);
//            timelineItem.setBackground(ColorPalette.getRandomColor());
            timelineItem.setPreferredSize(new Dimension(portraitWidths, portraitHeights / 2));

            container.add(timelineItem, gbc);
            gbc.gridx = gbc.gridx + 1;
            timelineItems.add(timelineItem);

        }

        container.setPreferredSize(new Dimension(width, height));
        container.setBackground(ColorPalette.TRANSLUCENT_BLACK_V2);
        container.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createLineBorder(ColorPalette.TRANSLUCENT_BLACK_V3),
                BorderFactory.createLineBorder(ColorPalette.TRANSLUCENT_BLACK_V3)
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
        boolean isNonNullAndAlreadySelecting = userSelected != null && userSelected.get(Tile.class).mUnit == selected;
        if (first == model.speedQueue.peek() && isNonNullAndAlreadySelecting) { return; }
        first = model.speedQueue.peek();
        selected = userSelected == null ? null : userSelected.get(Tile.class).mUnit;
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
            if (userSelected != null && userSelected.get(Tile.class).mUnit == entity) {
                image.setBackground(availableAndCurrent);
                isSelected = true;
            }
            if (index < available.size()) {
                if (!isSelected) { image.setBackground(thisRoundsColor); }
                thisRoundsColor = thisRoundsColor.darker();
            } else if (index < available.size() + availableNextTurn.size()) {
                if (!isSelected) { image.setBackground(nextRoundsColor); }
//                nextRoundsColor = nextRoundsColor.darker();
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

//        logger.info("Updated Panel turn order panel ");
    }
}
