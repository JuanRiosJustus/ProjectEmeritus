package main.ui.game.panels;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import main.constants.SimpleCheckSum;
import main.game.components.AssetComponent;
import main.game.components.IdentityComponent;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.stores.factories.EntityStore;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.Animation;
import main.logging.EmeritusLogger;
import main.ui.game.GamePanel;
import org.json.JSONArray;

import java.util.*;


public class TimeLinePanel extends GamePanel {
    private static final EmeritusLogger logger = EmeritusLogger.create(TimeLinePanel.class);
    private HBox mContainer = new HBox();
    private SimpleCheckSum mSimpleCheckSum = new SimpleCheckSum();
    private List<TimeLinePanelItem> mTimeLinePanelItems = null;
    private int mTimeLineItemWidth = 0;
    private int mTimeLineItemHeight = 0;
    public TimeLinePanel(int x, int y, int width, int height, Color color, int visibleColumns) {
        super(x, y, width, height);

        mSimpleCheckSum = new SimpleCheckSum();

        mContainer = new HBox();
        mContainer.setPrefSize(width, height);
        mContainer.setMinSize(width, height);
        mContainer.setMaxSize(width, height);
        mContainer.setFillHeight(true);
//        mContainer.setPickOnBounds(false);

        mTimeLineItemWidth = width / visibleColumns;
        mTimeLineItemHeight = height;
        mTimeLinePanelItems = new ArrayList<>();

        for (int i = 0; i < visibleColumns; i++) {
            TimeLinePanelItem timeLinePanelItem = new TimeLinePanelItem(mTimeLineItemWidth, mTimeLineItemHeight, color);
            mContainer.getChildren().add(timeLinePanelItem);
            mTimeLinePanelItems.add(timeLinePanelItem);
        }

        getChildren().add(mContainer);
    }

    public void gameUpdate(GameController gc) {
//        GameModel model = gc.getModel();
//        Queue<Entity> toPlace = prepareTimelineQueue(model);
        Queue<String> toPlace = prepareTimelineQueue(gc);
        if (!mSimpleCheckSum.isUpdated("TEST", toPlace)) return;
//
        updateTimelineItems(toPlace);
        logger.info("Updating timeline HUD");
    }

    private Queue<String> prepareTimelineQueue(GameController gc) {
        JSONArray allUnits = gc.getAllUnitsInTurnQueue();
        JSONArray pendingTurn = gc.getAllUnitsInTurnQueuePendingTurn();

        // Add the units that have yet to go
        Queue<String> toPlace = new LinkedList<>();
        for (int i = 0; i < pendingTurn.length(); i++) {
            String unitID = pendingTurn.getString(i);
            toPlace.add(unitID);
        }
        // Add units that can eventually go again
        while (toPlace.size() < mTimeLinePanelItems.size()) {
            toPlace.add(null);
            for (int i = 0; i < allUnits.length(); i++) {
                String unitID = allUnits.getString(i);
                toPlace.add(unitID);
            }
        }

        return toPlace;
    }

    private void updateTimelineItems(Queue<String> toPlace) {
        for (int index = 0; index < mTimeLinePanelItems.size(); index++) {
            TimeLinePanelItem timeLinePanelItem = mTimeLinePanelItems.get(index);
            String entityID = toPlace.poll();

            if (entityID != null) {
                Entity unitEntity = EntityStore.getInstance().get(entityID);
                IdentityComponent identityComponent = unitEntity.get(IdentityComponent.class);
                String name = identityComponent.getNickname();

                timeLinePanelItem.label.setText(name);

                ImageView imageView = createAndCacheEntityIcon(entityID);
                if (imageView != null) {
                    imageView.setFitWidth(timeLinePanelItem.displayWidth * .9);
                    imageView.setFitHeight(timeLinePanelItem.displayHeight * .9);
                    imageView.setPreserveRatio(true);
                    timeLinePanelItem.display.setImageView(imageView);
                }

                // 🔹 **Set Color Based on Position**
                if (index == 0) {
                    timeLinePanelItem.setBackgroundColor(Color.FORESTGREEN);  // Current unit's turn
                } else if (index < toPlace.size()) {
                    timeLinePanelItem.setBackgroundColor(Color.YELLOW);  // Pending turn
                } else {
                    timeLinePanelItem.setBackgroundColor(Color.DARKRED);  // Not going this turn
                }

            } else {
                // 🔹 **Set Placeholder Color (Red)**
                timeLinePanelItem.label.setText("—");
                timeLinePanelItem.display.setImageView(null);
                timeLinePanelItem.setBackgroundColor(Color.DIMGRAY);
            }
        }
    }



    private ImageView createAndCacheEntityIcon(String entityID) {
        Entity entity = EntityStore.getInstance().get(entityID);
        AssetComponent assetComponent = entity.get(AssetComponent.class);
        String id = assetComponent.getMainID();
        Asset asset = AssetPool.getInstance().getAsset(id);
        if (asset == null) return null;

        Animation animation = asset.getAnimation();
        Image image = SwingFXUtils.toFXImage(animation.toImage(), null);
        ImageView view = new ImageView(image);

        return view;
    }
}
