package main.ui.huds;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import javax.swing.*;

import main.constants.StateLock;
import main.game.components.AssetComponent;
import main.game.components.IdentityComponent;
import main.game.components.MovementComponent;
import main.game.components.behaviors.Behavior;
import main.game.stores.pools.ColorPalette;
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
import main.ui.outline.OutlineButton;
import main.ui.custom.SwingUiUtils;

public class TimeLinePanel extends GameUI {

        private static class TimeLineItem extends GameUI {
        private JButton label = null;
        private JButton display = null;
        public TimeLineItem(int width, int height) {
            super(width, height);
            int displayWidth = width;
            int displayHeight = (int) (height * .75);
            display = new OutlineButton();
            display.setPreferredSize(new Dimension(displayWidth, displayHeight));
            display.setHorizontalAlignment(SwingConstants.CENTER);
            display.setBorder(BorderFactory.createRaisedBevelBorder());
            display.setFocusPainted(false);


            int labelWidth = width;
            int labelHeight = height - displayHeight;
            label = new OutlineButton("?????", SwingConstants.LEFT, 1);
            label.setPreferredSize(new Dimension(labelWidth, labelHeight));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(FontPool.getInstance().getFontForHeight(labelHeight));

            add(display);
            add(label);
            setOpaque(true);
        }
    }
    private final Map<Entity, ImageIcon> mEntityToImageCache = new HashMap<>();
    private final List<TimeLineItem> mTimeLineItems = new ArrayList<>();
    private final int mMaxTimeLineItems = 15;
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private Color mTimeLineDivierColor = ColorPalette.TRANSLUCENT_BLACK_V4;
    private final Color mFirstInTimeLineColor = ColorPalette.YELLOW;
    private final Color mSoonToGoTimeLineColor = ColorPalette.GREEN;
    private final Color mInUpcomingTurnColor = ColorPalette.RED;
    private final StateLock mStateLock = new StateLock();
    private boolean turnDividerHit = false;

    public TimeLinePanel(int width, int height) {
        super(width, height);
        setupPaneContent(width, height);
    }

    private void setupPaneContent(int width, int height) {
        int containerItemWidth = width / mMaxTimeLineItems;
        int containerItemHeight = height;

        for (int i = 0; i < mMaxTimeLineItems; i++) {
            TimeLineItem tli = new TimeLineItem(containerItemWidth, containerItemHeight);
            add(tli);
            mTimeLineItems.add(tli);
        }
        setOpaque(false);
    }

    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        mTimeLineDivierColor = color;
    }

    @Override
    public void gameUpdate(GameModel model) {
        Queue<Entity> toPlace = prepareTimelineQueue(model);
        if (!mStateLock.isUpdated("TEST", toPlace)) return;

        updateTimelineItems(model, toPlace);
        logger.info("Updating timeline HUD");
    }

    private Queue<Entity> prepareTimelineQueue(GameModel model) {
        List<Entity> all = model.mSpeedQueue.getAll();
        List<Entity> unfinished = model.mSpeedQueue.getUnfinished();
        Queue<Entity> toPlace = new LinkedList<>(unfinished);

        while (toPlace.size() < mTimeLineItems.size()) {
            toPlace.add(null);
            toPlace.addAll(all);
        }
        return toPlace;
    }

    private void updateTimelineItems(GameModel model, Queue<Entity> toPlace) {
        int turnDividerHits = 0;
        int turnCounts = model.mSpeedQueue.getCycleCount();
        turnDividerHit = false;

        for (int index = 0; index < mTimeLineItems.size(); index++) {
            TimeLineItem item = mTimeLineItems.get(index);
            Entity entity = toPlace.poll();
            Color colorForComponent = getTimeLineitemColor(index, entity);

            resetItemStyle(item, colorForComponent);

            updateTimelineItem(model, item, entity, turnCounts, turnDividerHits, index);
        }
    }

    private Color getTimeLineitemColor(int index, Entity entity) {
        Color colorForComponent = ColorPalette.getRandomColor();
        if (index == 0) {
            colorForComponent = ColorPalette.YELLOW;
        } else if (entity == null) {
            colorForComponent = ColorPalette.TRANSLUCENT_BLACK_V2;
            turnDividerHit = true;
        } else if (!turnDividerHit) {
            colorForComponent = ColorPalette.GREEN;
        } else if (turnDividerHit) {
            colorForComponent = ColorPalette.RED;
        }
        return colorForComponent;
    }

    private void updateTimelineItem(GameModel model, TimeLineItem item, Entity entity, int turnCounts, int turnDividerHits, int iteration) {
        JButton display = item.display;
        JButton label = item.label;

        if (entity != null) {
            updateEntityItem(display, label, entity);
            setupEntityActionListener(display, label, entity, model);
        } else {
            updateTurnDividerItem(display, label, turnCounts, turnDividerHits);
        }

        SwingUiUtils.automaticallyStyleComponent(label);
        SwingUiUtils.automaticallyStyleComponent(display);
    }

    private void resetItemStyle(TimeLineItem item, Color color) {
        JButton display = item.display;
        JButton label = item.label;
        SwingUiUtils.removeAllActionListeners(label);
        SwingUiUtils.removeAllActionListeners(display);
        display.setVerticalTextPosition(SwingConstants.CENTER);
        display.setHorizontalTextPosition(SwingConstants.CENTER);
        display.setText("");
        display.setVisible(true);

        item.display.setBackground(color);
        item.label.setBackground(color);
        item.setBackground(color);
    }

    private void updateEntityItem(JButton display, JButton label, Entity entity) {
        ImageIcon icon = getEntityIcon(entity, display);

        display.setIcon(icon);
        display.setToolTipText(entity.get(IdentityComponent.class).getName());
        label.setText(entity.get(IdentityComponent.class).getName() + getUserControlledSuffix(entity));
    }

    private ImageIcon getEntityIcon(Entity entity, JButton display) {
        ImageIcon icon = mEntityToImageCache.get(entity);
        if (icon == null) {
            icon = createAndCacheEntityIcon(entity, display);
        }
        return icon;
    }

    private ImageIcon createAndCacheEntityIcon(Entity entity, JButton display) {
        AssetComponent assetComponent = entity.get(AssetComponent.class);
        String id = assetComponent.getId(AssetComponent.UNIT_ASSET);
        Asset asset = AssetPool.getInstance().getAsset(id);
        if (asset == null) return null;

        Animation animation = asset.getAnimation();
        Image newImage = animation.toImage().getScaledInstance(
                (int) (display.getPreferredSize().getWidth() * .5),
                (int) (display.getPreferredSize().getHeight() * .75),
                Image.SCALE_SMOOTH
        );
        ImageIcon newIcon = new ImageIcon(newImage);
        mEntityToImageCache.put(entity, newIcon);
        return newIcon;
    }

    private String getUserControlledSuffix(Entity entity) {
        return entity.get(Behavior.class).isUserControlled() ? "*" : "";
    }

    private void setupEntityActionListener(JButton display, JButton label, Entity entity, GameModel model) {
        ActionListener al = e -> {
            MovementComponent movementComponent = entity.get(MovementComponent.class);
            model.getGameState().setTileToGlideTo(movementComponent.getCurrentTile());
            model.setSelectedTile(movementComponent.getCurrentTile().get(Tile.class));


        };

        display.addActionListener(al);
        label.addActionListener(al);
    }

    private void setColorForItem(Entity entity, TimeLineItem tli, int iteration, int turnDividerHits) {
        Color color = null;
        JButton display = tli.display;
        JButton label = tli.label;
        if (iteration == 0) {
            color = mFirstInTimeLineColor;
        } else {
            if (entity == null) {
                color = mTimeLineDivierColor;
            } else if (turnDividerHits > 0) {
                color = ColorPalette.TRANSLUCENT_BLACK_V4;
            } else {
                color = mSoonToGoTimeLineColor;
            }
        }
//        Color color = (iteration == 0) ? mFirstInTimeLineColor :
//                (turnDividerHits > 0) ? mInUpcomingTurnColor : mSoonToGoTimeLineColor;
        display.setBackground(color);
        label.setBackground(color);
        tli.setBackground(color);
    }

    private void updateTurnDividerItem(JButton display, JButton label, int turnCounts, int turnDividerHits) {
        String turnText = "TURN " + (turnCounts + turnDividerHits);
        display.setBackground(mTimeLineDivierColor);
        display.setText("←");
        display.setToolTipText(turnText);
        display.setIcon(null);
        label.setText(turnText);
        label.setBackground(mTimeLineDivierColor);
    }
}
