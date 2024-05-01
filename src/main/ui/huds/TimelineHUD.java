package main.ui.huds;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
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

    private final List<JButton> mTimelineItems = new ArrayList<>();
    private final Map<Entity, ImageIcon> entityToIcon = new HashMap<>();
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private Entity first = null;
    private Entity selected = null;
    private int mUnitPortraitSize;
    private BufferedImage mBlankImage = null;

    public TimelineHUD(int hudWidth, int hudHeight) {
        super(hudWidth, hudHeight, TimelineHUD.class.getSimpleName());

        setupPaneContent(this, hudWidth, hudHeight);

        ComponentUtils.disable(this);
//        setOpaque(false);
        setOpaque(true);
        setBackground(ColorPalette.BLUE);
    }

    private void setupPaneContent(JPanel container, int width, int height) {
        container.setLayout(new GridBagLayout());
        container.setPreferredSize(new Dimension(width, height));
        container.setMaximumSize(new Dimension(width, height));
        container.setMinimumSize(new Dimension(width, height));

        int columns = 10;
        int containerItemWidth = width / columns;
        int containerItemHeight = height;
        mUnitPortraitSize = Math.min(containerItemWidth, containerItemHeight) / 2;
        mBlankImage = new BufferedImage(mUnitPortraitSize, mUnitPortraitSize, BufferedImage.TYPE_INT_ARGB);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.fill = GridBagConstraints.BOTH;

        for (int i = 0; i < columns; i++) {
            JButton timelineItem = new JButton();
            timelineItem.setBackground(ColorPalette.TRANSLUCENT_BLACK_V1);
            timelineItem.setForeground(ColorPalette.WHITE);
            timelineItem.setFont(FontPool.getInstance().getFont(timelineItem.getFont().getSize()).deriveFont(Font.BOLD));
            timelineItem.setFont(timelineItem.getFont().deriveFont(Font.BOLD));
            timelineItem.setOpaque(true);
            timelineItem.setBorderPainted(false);
            timelineItem.setFocusPainted(false);
//            timelineItem.setBackground(ColorPalette.getRandomColor());
            timelineItem.setPreferredSize(new Dimension(containerItemWidth, containerItemHeight));
            timelineItem.setMaximumSize(new Dimension(containerItemWidth, containerItemHeight));
            timelineItem.setMinimumSize(new Dimension(containerItemWidth, containerItemHeight));

            container.add(timelineItem, gbc);
            mTimelineItems.add(timelineItem);

            gbc.gridx = gbc.gridx + 1;
        }

        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.TRANSLUCENT_BLACK_V3),
                BorderFactory.createLineBorder(ColorPalette.TRANSLUCENT_BLACK_V3)
        ));
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
        if (model.speedQueue.peek() == null) { return; }
        first = model.speedQueue.peek();
        selected = userSelected == null ? null : userSelected.get(Tile.class).mUnit;
        // // Get all units to place
        List<Entity> all = model.speedQueue.getAll();
        List<Entity> unfinished = model.speedQueue.getUnfinished();
        Queue<Entity> toPlace = new LinkedList<>(unfinished);

        while (toPlace.size() < mTimelineItems.size()) {
            toPlace.add(null);
            toPlace.addAll(all);
        }

        Color current = ColorPalette.GREEN;
        Color upcoming = ColorPalette.RED;
        Color iterator = current;
        int nullCounts = model.speedQueue.getCycleCount();

        for (int iteration = 0; iteration < mTimelineItems.size(); iteration++) {
            JButton image = mTimelineItems.get(iteration);
            Entity entity = toPlace.poll();
            Animation animation = null;
            ImageIcon icon = null;
            // The null value tells us we have reached the end of the current turn
            if (entity != null) {
                animation = entity.get(Animation.class);
                icon = entityToIcon.get(entity);
                if (icon == null) {
                    Image newImage = animation.toImage().getScaledInstance(mUnitPortraitSize, mUnitPortraitSize, Image.SCALE_SMOOTH);
                    ImageIcon newIcon = new ImageIcon(newImage);
                    entityToIcon.put(entity, newIcon);
                    icon = newIcon;
                }
            }

            // Change color based on the position in the queue
            if (iteration == 0) {
                image.setBackground(ColorPalette.GREY);
            } else if (entity == null) {
                image.setBackground(ColorPalette.BLACK);
                iterator = upcoming;
                nullCounts = nullCounts + 1;
            } else {
                image.setBackground(iterator);
            }

            // Setup button configs
            if (entity != null) {
                image.setIcon(icon);
                image.setVerticalTextPosition(SwingConstants.BOTTOM);
                image.setHorizontalTextPosition(SwingConstants.CENTER);
                image.setText(entity.get(Identity.class).getName());
                image.setToolTipText(entity.get(Identity.class).getName());
                removeAllListeners(image);
                image.addActionListener(e -> {
                    model.gameState.set(GameState.CURRENTLY_SELECTED, entity.get(MovementManager.class).currentTile);
                    model.gameState.set(GameState.GLIDE_TO_SELECTED, true);
                });
                image.setVisible(true);
            } else {
                image.setIcon(new ImageIcon(mBlankImage));
                image.setVerticalTextPosition(SwingConstants.BOTTOM);
                image.setHorizontalTextPosition(SwingConstants.CENTER);
                image.setText("TURN " + nullCounts);
                image.setToolTipText("TURN " + nullCounts);
                removeAllListeners(image);
                image.addActionListener(e ->{
                    model.gameState.set(GameState.CURRENTLY_SELECTED, null);
                    model.gameState.set(GameState.GLIDE_TO_SELECTED, true);
                });
                image.setVisible(true);
            }
        }

//        logger.info("Updated Panel turn order panel ");
    }
}
