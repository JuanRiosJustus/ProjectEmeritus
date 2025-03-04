package main.ui.huds;

import main.constants.SimpleCheckSum;
import main.game.components.AssetComponent;
import main.game.components.IdentityComponent;
import main.game.components.behaviors.Behavior;
import main.game.entity.Entity;
import main.game.main.GameControllerV1;
import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.Animation;
import main.graphics.GameUI;
import main.logging.EmeritusLogger;

import main.ui.custom.SwingUiUtils;
import main.ui.outline.production.core.OutlineButton;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.util.*;

public class TimeLinePanelV2 extends GameUI {

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
            label = new OutlineButton("?????", 1);
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
    private final EmeritusLogger logger = EmeritusLogger.create(getClass());
    private Color mTimeLineDivierColor = ColorPalette.TRANSLUCENT_BLACK_LEVEL_4;
    private final Color mFirstInTimeLineColor = ColorPalette.YELLOW;
    private final Color mSoonToGoTimeLineColor = ColorPalette.GREEN;
    private final Color mInUpcomingTurnColor = ColorPalette.RED;
    private final SimpleCheckSum mSimpleCheckSum = new SimpleCheckSum();
    private boolean turnDividerHit = false;

    public TimeLinePanelV2(int width, int height, Color color) {
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
    public void gameUpdate(GameControllerV1 gameControllerV1) {
        GameModel model = gameControllerV1.getModel();
        Queue<Entity> toPlace = prepareTimelineQueue(model);
        if (!mSimpleCheckSum.isUpdated("TEST", toPlace)) return;

        updateTimelineItems(gameControllerV1, toPlace);
        logger.info("Updating timeline HUD");
    }

//    @Override
//    public void gameUpdate(GameModel model) {
//        Queue<Entity> toPlace = prepareTimelineQueue(model);
//        if (!mStateLock.isUpdated("TEST", toPlace)) return;
//
//        updateTimelineItems(model, toPlace);
//        logger.info("Updating timeline HUD");
//    }

    private Queue<Entity> prepareTimelineQueue(GameModel model) {
        List<Entity> all = model.getSpeedQueue().getAllV1();
        List<Entity> unfinished = model.getSpeedQueue().getUnfinishedV1();
        Queue<Entity> toPlace = new LinkedList<>(unfinished);

        while (toPlace.size() < mTimeLineItems.size()) {
            toPlace.add(null);
            toPlace.addAll(all);
        }
        return toPlace;
    }

    private void updateTimelineItems(GameControllerV1 gameControllerV1, Queue<Entity> toPlace) {
        int turnDividerHits = 0;
        int turnCounts = gameControllerV1.getModel().getSpeedQueue().getCycleCount();
        turnDividerHit = false;

        for (int index = 0; index < mTimeLineItems.size(); index++) {
            TimeLineItem item = mTimeLineItems.get(index);
            Entity entity = toPlace.poll();
            Color colorForComponent = getTimeLineitemColor(index, entity);

            resetItemStyle(item, colorForComponent);

            updateTimelineItem(item, entity, turnCounts, turnDividerHits, gameControllerV1);
        }
    }

    private Color getTimeLineitemColor(int index, Entity entity) {
        Color colorForComponent = ColorPalette.getRandomColor();
        if (index == 0) {
            colorForComponent = ColorPalette.YELLOW;
        } else if (entity == null) {
            colorForComponent = ColorPalette.TRANSLUCENT_BLACK_LEVEL_2;
            turnDividerHit = true;
        } else if (!turnDividerHit) {
            colorForComponent = ColorPalette.GREEN;
        } else if (turnDividerHit) {
            colorForComponent = ColorPalette.RED;
        }
        return colorForComponent;
    }

    private void updateTimelineItem(TimeLineItem item, Entity entity, int turnCounts, int turnDividerHits, GameControllerV1 gameControllerV1) {
        JButton display = item.display;
        JButton label = item.label;

        if (entity != null) {
            updateEntityItem(display, label, entity);
            setupEntityActionListener(display, label, entity, gameControllerV1);
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
        display.setToolTipText(entity.get(IdentityComponent.class).getNickname());
        label.setText(entity.get(IdentityComponent.class).getNickname() + getUserControlledSuffix(entity));
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
//        String id = assetComponent.getId(AssetComponent.UNIT_ASSET);
        String id = assetComponent.getMainID();
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

    private void setupEntityActionListener(JButton display, JButton label, Entity entity, GameControllerV1 gameControllerV1) {

        ActionListener al = e -> {
//            MovementComponent movementComponent = entity.get(MovementComponent.class);
//            Entity tileEntity = movementComponent.getCurrentTile();
//            JSONObject currentTile = tileEntity.get(Tile.class);
//            gameController.setTileToGlideToV1(currentTile);
//            gameController.setSelectedTilesV1(currentTile);
        };

        display.addActionListener(al);
        label.addActionListener(al);
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

