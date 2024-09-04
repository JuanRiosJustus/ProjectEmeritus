package main.ui.huds;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import javax.swing.*;

import main.game.components.AssetComponent;
import main.game.components.IdentityComponent;
import main.game.components.MovementComponent;
import main.game.components.StatisticsComponent;
import main.game.components.behaviors.Behavior;
import main.game.stores.pools.ColorPalette;
import main.game.main.GameState;
import main.graphics.Animation;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.GameUI;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.ui.components.OutlineButton;
import main.ui.custom.SwingUiUtils;

public class TimeLinePanel extends GameUI {

    private static class TimeLineItem extends JPanel {
        private OutlineButton label = null;
        private OutlineButton image = null;
        public TimeLineItem(int width, int height) {
            setLayout(new GridBagLayout());
            setPreferredSize(new Dimension(width, height));
            setMaximumSize(new Dimension(width, height));
            setMinimumSize(new Dimension(width, height));

            int displayWidth = width;
            int displayHeight = (int) (height * .75);

            image = new OutlineButton("", SwingConstants.CENTER, 0);
            image.setPreferredSize(new Dimension(displayWidth, displayHeight));
            image.setMinimumSize(new Dimension(displayWidth, displayHeight));
            image.setMaximumSize(new Dimension(displayWidth, displayHeight));
            image.setHorizontalAlignment(SwingConstants.CENTER);
            image.setVerticalAlignment(SwingConstants.CENTER);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(image, gbc);

            int labelWidth = width;
            int labelHeight = height - displayHeight;

            label = new OutlineButton("", SwingConstants.CENTER, 1);
            label.setPreferredSize(new Dimension(labelWidth, labelHeight));
            label.setMinimumSize(new Dimension(labelWidth, labelHeight));
            label.setMaximumSize(new Dimension(labelWidth, labelHeight));
            SwingUiUtils.automaticallyStyleComponent(label, (int) (labelHeight * .75));
            gbc.gridy = 1;
            add(label, gbc);
        }
    }

    private final Map<Entity, ImageIcon> mEntityToImageCache = new HashMap<>();
    private final List<TimeLineItem> mTimeLineItems = new ArrayList<>();
    private int mCurrentTimelineState = -1;
    private final int mMaxTimeLineItems = 15;
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private Entity mFirstInTimeLineEntity = null;
    private Entity mSelectedUnitEntity = null;

    private Color mTimeLineDivierColor = ColorPalette.TRANSPARENT_DARK_GREY;
    private final Color mFirstInTimeLineColor = ColorPalette.YELLOW;
    private final Color mSoonToGoTimeLineColor = ColorPalette.GREEN;
    private final Color mInUpcomingTurnColor = ColorPalette.RED;
    public TimeLinePanel(int width, int height, int x, int y) {
        super(width, height, x, y, TimeLinePanel.class.getSimpleName());
        setupPaneContent(this, width, height);
    }

    private void setupPaneContent(JPanel container, int width, int height) {
        container.setLayout(new GridBagLayout());
        container.setPreferredSize(new Dimension(width, height));
        container.setMaximumSize(new Dimension(width, height));
        container.setMinimumSize(new Dimension(width, height));

        int containerItemWidth = width / mMaxTimeLineItems;
        int containerItemHeight = height;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.fill = GridBagConstraints.BOTH;

        for (int i = 0; i < mMaxTimeLineItems; i++) {
            TimeLineItem tli = new TimeLineItem(containerItemWidth, containerItemHeight);
            SwingUiUtils.setStylizedRaisedBevelBorder(tli);

            container.add(tli, gbc);
            mTimeLineItems.add(tli);

            gbc.gridx = gbc.gridx + 1;
        }
    }

    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        mTimeLineDivierColor = color;
    }

    @Override
    public void gameUpdate(GameModel model) {
        // Check if the queue has changed since last time
        Entity userSelected = model.getGameState().getCurrentlySelectedTileEntity();
        boolean isNonNullAndAlreadySelecting = userSelected != null && userSelected.get(Tile.class).getUnit() == mSelectedUnitEntity;
        if (mFirstInTimeLineEntity == model.mSpeedQueue.peek() && isNonNullAndAlreadySelecting) { return; }
        if (model.mSpeedQueue.peek() == null) { return; }
        mFirstInTimeLineEntity = model.mSpeedQueue.peek();
        mSelectedUnitEntity = userSelected == null ? null : userSelected.get(Tile.class).getUnit();

        // // Get all units to place, including the ones this turn and next turn
        List<Entity> all = model.mSpeedQueue.getAll();
        List<Entity> unfinished = model.mSpeedQueue.getUnfinished();
        Queue<Entity> toPlace = new LinkedList<>(unfinished);

        // Use all the timeline items,
        while (toPlace.size() < mTimeLineItems.size()) {
            toPlace.add(null);
            toPlace.addAll(all);
        }

        int currentState = Objects.hash(Arrays.toString(toPlace.toArray()));
        if (currentState == mCurrentTimelineState) { return; }
        mCurrentTimelineState = currentState;

        int turnDividerHits = 0;
        int turnCounts = model.mSpeedQueue.getCycleCount();
        int iteration = 0;
        for (TimeLineItem tli : mTimeLineItems) {
            OutlineButton imageContainer = tli.image;
            int portraitWidth = (int) (imageContainer.getPreferredSize().getWidth() * .5);
            int portraitHeight = (int) (imageContainer.getPreferredSize().getHeight() * .75);
            OutlineButton label = tli.label;

            Entity entity = toPlace.poll();
            Animation animation = null;
            ImageIcon icon = null;
            StatisticsComponent statisticsComponent = null;
            boolean isUserControlled = (entity != null && entity.get(Behavior.class).isUserControlled());

            // The null value tells us we have reached the end of the current turn
            if (entity != null) {
                AssetComponent assetComponent = entity.get(AssetComponent.class);
                statisticsComponent = entity.get(StatisticsComponent.class);
                String id = assetComponent.getId(AssetComponent.UNIT_ASSET);
                Asset asset = AssetPool.getInstance().getAsset(id);

                icon = mEntityToImageCache.get(entity);
                if (icon == null && asset != null) {
                    animation = asset.getAnimation();
                    Image newImage = animation.toImage().getScaledInstance(portraitWidth, portraitHeight, Image.SCALE_SMOOTH);
                    ImageIcon newIcon = new ImageIcon(newImage);
                    mEntityToImageCache.put(entity, newIcon);
                    icon = newIcon;
                }
            }

            // Remove previous actions and setup common configs
            SwingUiUtils.removeAllActionListeners(label);
            SwingUiUtils.removeAllActionListeners(imageContainer);

            imageContainer.setVerticalTextPosition(SwingConstants.CENTER);
            imageContainer.setHorizontalTextPosition(SwingConstants.CENTER);
            imageContainer.setFont(FontPool.getInstance().getFont(portraitHeight).deriveFont(Font.BOLD));
            imageContainer.setVisible(true);

            if (entity != null) {
                imageContainer.setText("");
                imageContainer.setIcon(icon);
                imageContainer.setToolTipText(entity.get(IdentityComponent.class).getName());

                ActionListener al = e -> {
                    MovementComponent movementComponent = entity.get(MovementComponent.class);
                    model.getGameState().setTileToGlideTo(movementComponent.getCurrentTile());
                    model.getGameState().setupEntitySelections(movementComponent.getCurrentTile());
                };

                imageContainer.addActionListener(al);
                label.addActionListener(al);

                // if first iteration set as yellow
                if (iteration == 0) {
                    imageContainer.setBackground(mFirstInTimeLineColor);
                    label.setBackground(mFirstInTimeLineColor);
                } else {
                    if (turnDividerHits > 0) {
                        imageContainer.setBackground(mInUpcomingTurnColor);
                        label.setBackground(mInUpcomingTurnColor);
                    } else {
                        imageContainer.setBackground(mSoonToGoTimeLineColor);
                        label.setBackground(mSoonToGoTimeLineColor);
                    }
                }
                label.setText(entity.get(IdentityComponent.class).getName() + (isUserControlled ? "*" : ""));
            } else  {
                String turnText = "TURN " + (turnCounts + turnDividerHits);

                imageContainer.setBackground(mTimeLineDivierColor);
                imageContainer.setText("←");
                imageContainer.setToolTipText(turnText);
                imageContainer.setIcon(null);

                label.setText(turnText);
                label.setBackground(mTimeLineDivierColor);

                turnDividerHits = turnDividerHits + 1;
            }

            SwingUiUtils.automaticallyStyleComponent(label);
            SwingUiUtils.automaticallyStyleComponent(imageContainer);
            iteration++;
        }
        logger.info("Updating timeline HUD");
    }
}
