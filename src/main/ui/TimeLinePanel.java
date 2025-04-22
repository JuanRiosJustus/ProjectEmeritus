package main.ui;

import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import main.constants.HashSlingingSlasher;
import main.game.components.AIComponent;
import main.game.components.IdentityComponent;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.main.GameModel;
import main.game.stores.EntityStore;
import main.logging.EmeritusLogger;
import main.ui.foundation.BevelStyle;
import main.ui.game.GamePanel;
import main.constants.JavaFXUtils;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.*;


public class TimeLinePanel extends BevelStyle {
    private static final EmeritusLogger logger = EmeritusLogger.create(TimeLinePanel.class);
    private HBox mContainer = new HBox();
    private HashSlingingSlasher mHashSlingingSlasher = new HashSlingingSlasher();
    private List<TimeLinePanelItem> mTimeLinePanelItems = null;
    private int mTimeLineItemWidth = 0;
    private int mTimeLineItemHeight = 0;
    private Color mColor;
    private JSONObject mResponseObject = new JSONObject();
    public TimeLinePanel(int x, int y, int width, int height, Color color, int visibleColumns) {
        super(x, y, width, height, color);

        mHashSlingingSlasher = new HashSlingingSlasher();
        mColor = color;

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

//        mInnerShadow.setRadius(0.002);
//        mDropShadow.setInput(mInnerShadow);
//        mDropShadow.setRadius(width * .002);
//        mDropShadow.setSpread(300);
//        setEffect(mDropShadow);
    }

    public void gameUpdate(GameModel gameModel) {
        int speedQueueCheckSum = gameModel.getSpeedQueueChecksum();
        if (!mHashSlingingSlasher.setOnDifference(speedQueueCheckSum)) { return; }

        logger.info("Started updating the timeline panel after turn order has changed.");
        Queue<String> toPlace = prepareTimelineQueue(gameModel);
        updateTimelineItems(gameModel, toPlace);
        logger.info("Finished updating the timeline panel after turn order has changed.");
    }

    private Queue<String> prepareTimelineQueue(GameModel gameModel) {
        JSONArray allUnits = gameModel.getAllEntitiesInTurnQueue();
        JSONArray pendingTurn = gameModel.getAllEntityIDsPendingTurnInTurnQueue();

        // Add the units that have yet to go
        Queue<String> toPlace = new LinkedList<>();
        for (int i = 0; i < pendingTurn.size(); i++) {
            String unitID = pendingTurn.getString(i);
            toPlace.add(unitID);
        }
        // Add units that can eventually go again
        while (toPlace.size() < mTimeLinePanelItems.size()) {
            toPlace.add(null);
            for (int i = 0; i < allUnits.size(); i++) {
                String unitID = allUnits.getString(i);
                toPlace.add(unitID);
            }
        }

        return toPlace;
    }
    private void updateTimelineItems(GameModel gameModel, Queue<String> toPlace) {
        boolean hasSeenNull = false;
        for (int index = 0; index < mTimeLinePanelItems.size(); index++) {
            TimeLinePanelItem timeLinePanelItem = mTimeLinePanelItems.get(index);
            String entityID = toPlace.poll();

            if (entityID != null) {
                Entity unitEntity = EntityStore.getInstance().get(entityID);
                IdentityComponent identityComponent = unitEntity.get(IdentityComponent.class);
                String name = identityComponent.getNickname();

                timeLinePanelItem.label.setText(name);
                AIComponent aiComponent = unitEntity.get(AIComponent.class);
                boolean isUserControlled = aiComponent.isUserControlled();

                timeLinePanelItem.label.setText((isUserControlled ? "* " : "  ") + name);

                JavaFXUtils.setOnMousePressedEvent(timeLinePanelItem.display.getUnderlyingButton(), e -> {
                    JSONObject request = new JSONObject();
                    request.put("id", entityID);
                    JSONArray response = gameModel.getEntityTileID(request);

                    String currentTileID = response.getString(0);
                    gameModel.setSelectedTileIDs(currentTileID);

                    JSONObject tileToGlideToRequest = new JSONObject();
                    tileToGlideToRequest.put("id", currentTileID);
                    tileToGlideToRequest.put("camera", "0");
                    gameModel.setTileToGlideTo(tileToGlideToRequest);

                    tileToGlideToRequest = new JSONObject();
                    tileToGlideToRequest.put("id", currentTileID);
                    tileToGlideToRequest.put("camera", "1");
                    gameModel.setTileToGlideTo(tileToGlideToRequest);
                });

                ImageView imageView = createAndCacheEntityIcon(entityID);
                if (imageView != null) {
                    imageView.setFitWidth(timeLinePanelItem.displayWidth * .9);
                    imageView.setFitHeight(timeLinePanelItem.displayHeight * .9);
                    imageView.setPreserveRatio(true);
                    imageView.setFocusTraversable(false);
                    timeLinePanelItem.display.setImageView(imageView);
                    timeLinePanelItem.display.setPickOnBounds(false);
                }

                // 🔹 **Set Color Based on Position**
                if (hasSeenNull) {
                    timeLinePanelItem.setBackgroundColor(Color.CRIMSON);  // Not going this turn
                } else {
                    if (index == 0) {
                        timeLinePanelItem.setBackgroundColor(Color.SEAGREEN);  // Current unit's turn
                    } else if (index < toPlace.size()) {
                        timeLinePanelItem.setBackgroundColor(Color.YELLOW);  // Pending turn
                    }
                }
            } else {
                // 🔹 **Set Placeholder Color (Red)**
                timeLinePanelItem.label.setText("—");
                timeLinePanelItem.display.setImageView(null);
                timeLinePanelItem.setBackgroundColor(mColor);
                hasSeenNull = true;
            }
        }
    }




    public void gameUpdate(GameController gc) {
        gc.getGameModel().getTurnQueueChecksums(mResponseObject);
        int pendingTurnChecksum = mResponseObject.getIntValue("pending");
        int finishedTurnCheckSum = mResponseObject.getIntValue("finished");
        if (!mHashSlingingSlasher.setOnDifference(pendingTurnChecksum, finishedTurnCheckSum)) { return; }

        logger.info("Started updating the timeline panel after turn order has changed.");
        Queue<String> toPlace = prepareTimelineQueue(gc);
        updateTimelineItems(gc, toPlace);
        logger.info("Finished updating the timeline panel after turn order has changed.");
    }

    private Queue<String> prepareTimelineQueue(GameController gc) {
        JSONArray allUnits = gc.getGameModel().getAllEntitiesInTurnQueue();
        JSONArray pendingTurn = gc.getGameModel().getAllEntitiesInTurnQueuePendingTurn();

        // Add the units that have yet to go
        Queue<String> toPlace = new LinkedList<>();
        for (int i = 0; i < pendingTurn.size(); i++) {
            String unitID = pendingTurn.getString(i);
            toPlace.add(unitID);
        }
        // Add units that can eventually go again
        while (toPlace.size() < mTimeLinePanelItems.size()) {
            toPlace.add(null);
            for (int i = 0; i < allUnits.size(); i++) {
                String unitID = allUnits.getString(i);
                toPlace.add(unitID);
            }
        }

        return toPlace;
    }


    private void updateTimelineItems(GameController gc, Queue<String> toPlace) {
        boolean hasSeenNull = false;
        for (int index = 0; index < mTimeLinePanelItems.size(); index++) {
            TimeLinePanelItem timeLinePanelItem = mTimeLinePanelItems.get(index);
            String entityID = toPlace.poll();

            if (entityID != null) {
                Entity unitEntity = EntityStore.getInstance().get(entityID);
                IdentityComponent identityComponent = unitEntity.get(IdentityComponent.class);
                String name = identityComponent.getNickname();

                timeLinePanelItem.label.setText(name);
                AIComponent aiComponent = unitEntity.get(AIComponent.class);
                boolean isUserControlled = aiComponent.isUserControlled();

                timeLinePanelItem.label.setText((isUserControlled ? "* " : "  ") + name);

                JavaFXUtils.setOnMousePressedEvent(timeLinePanelItem.display.getUnderlyingButton(), e -> {
                    JSONObject request = new JSONObject();
                    request.put("id", entityID);
                    JSONArray response = gc.getGameModel().getEntityTileID(request);

                    String currentTileID = response.getString(0);
                    gc.getGameModel().setSelectedTileIDs(currentTileID);

                    JSONObject secondaryCameraInfo = gc.getGameModel().getSecondaryCameraInfo(gc.getGameModel());
                    String camera = secondaryCameraInfo.getString("camera");
                    JSONObject tileToGlideToRequest = new JSONObject();
                    tileToGlideToRequest.put("id", currentTileID);
                    tileToGlideToRequest.put("camera", camera);
                    gc.getGameModel().setTileToGlideTo(tileToGlideToRequest);

                    JSONObject mainCameraInfo = gc.getMainCameraInfoAPI();
                    camera = mainCameraInfo.getString("camera");
                    tileToGlideToRequest = new JSONObject();
                    tileToGlideToRequest.put("id", currentTileID);
                    tileToGlideToRequest.put("camera", camera);
                    gc.getGameModel().setTileToGlideTo(tileToGlideToRequest);
                });

                ImageView imageView = createAndCacheEntityIcon(entityID);
                if (imageView != null) {
                    imageView.setFitWidth(timeLinePanelItem.displayWidth * .9);
                    imageView.setFitHeight(timeLinePanelItem.displayHeight * .9);
                    imageView.setPreserveRatio(true);
                    imageView.setFocusTraversable(false);
                    timeLinePanelItem.display.setImageView(imageView);
                    timeLinePanelItem.display.setPickOnBounds(false);
                }

                // 🔹 **Set Color Based on Position**
                if (hasSeenNull) {
                    timeLinePanelItem.setBackgroundColor(Color.CRIMSON);  // Not going this turn
                } else {
                    if (index == 0) {
                        timeLinePanelItem.setBackgroundColor(Color.SEAGREEN);  // Current unit's turn
                    } else if (index < toPlace.size()) {
                        timeLinePanelItem.setBackgroundColor(Color.YELLOW);  // Pending turn
                    }
                }
            } else {
                // 🔹 **Set Placeholder Color (Red)**
                timeLinePanelItem.label.setText("—");
                timeLinePanelItem.display.setImageView(null);
                timeLinePanelItem.setBackgroundColor(mColor);
                hasSeenNull = true;
            }
        }
    }
}
