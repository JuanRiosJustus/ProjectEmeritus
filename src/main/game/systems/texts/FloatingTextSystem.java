package main.game.systems.texts;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import main.constants.Vector3f;
import main.game.components.MovementComponent;
import main.game.components.tile.TileComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.ColorPalette;
import main.game.systems.GameSystem;
import main.game.systems.SystemContext;
import main.utils.StringUtils;
import com.alibaba.fastjson2.JSONObject;

import java.util.*;

public class FloatingTextSystem extends GameSystem {
    private final Queue<String> mGarbageCalculator = new LinkedList<>();
    private final Queue<FloatingText> mFloatingTextQueue = new LinkedList<>();
    private final FloatingTextFactory mFloatingTextFactory = new FloatingTextFactory();

    private double mTimeSinceLastSpawn = 0;
    private static final double STAGGER_INTERVAL = 0.1; // in seconds (100ms)

    public FloatingTextSystem(GameModel gameModel) {
        super(gameModel);
        mEventBus.subscribe(FLOATING_TEXT_EVENT, this::handleFloatingTextEvent);
    }


    private static final String FLOATING_TEXT_EVENT = "floating.text.event";
    private static final String FLOAT_TEXT_EVENT_TEXT = "float.text.event_text";
    private static final String FLOAT_TEXT_EVENT_UNIT_ID = "float.text.event.unit_id";
    public static JSONObject createFloatingTextEvent(String txt, String unitID) {
        JSONObject event = new JSONObject();
        event.put("event", FLOATING_TEXT_EVENT);
        event.put(FLOAT_TEXT_EVENT_TEXT, txt);
        event.put(FLOAT_TEXT_EVENT_UNIT_ID, unitID);
        return event;
    }

    public void handleFloatingTextEvent(JSONObject event) {
        String text = event.getString(FLOAT_TEXT_EVENT_TEXT);
        String unitID = event.getString(FLOAT_TEXT_EVENT_UNIT_ID);

        Entity unitEntity = getEntityWithID(unitID);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        String currentTileID = movementComponent.getCurrentTileID();
        Entity tileEntity = getEntityWithID(currentTileID);
        if (tileEntity == null) return;

        TileComponent tile = tileEntity.get(TileComponent.class);
        Vector3f vector3f = tile.getLocalVector(mGameModel);

        int spriteWidths = mGameModel.getGameState().getSpriteWidth();
        int spriteHeights = mGameModel.getGameState().getSpriteHeight();
        int x = (int) vector3f.x;
        int y = (int) vector3f.y - (spriteHeights / 2);

        // Normalize and vary style
        text = StringUtils.convertSnakeCaseToCapitalized(text);
        float baseFontSize = mGameModel.getGameState().getFloatingTextFontSize();
        double lifetime = 2.5;

        // Choose the style dynamically or randomly if you like
        float generalFontSize = mGameModel.getGameState().getFloatingTextFontSize();
        Color color = Color.ANTIQUEWHITE;
//        FloatingText ft = mFloatingTextFactory.createShrinking(text, x, y,  color, generalFontSize);
        FloatingText ft = mFloatingTextFactory.createPopUp(text, x, y,  color, generalFontSize);

        mFloatingTextQueue.add(ft);
    }

    @Override
    public void update(GameModel model, SystemContext systemContext) {
        double deltaTime = model.getGameState().getDeltaTime();
        mTimeSinceLastSpawn += deltaTime;

        // Only spawn one text every STAGGER_INTERVAL seconds
        if (mTimeSinceLastSpawn >= STAGGER_INTERVAL && !mFloatingTextQueue.isEmpty()) {
            FloatingText next = mFloatingTextQueue.poll();
            model.getGameState().addFloatingText(next);
            mTimeSinceLastSpawn = 0;
        }

        // Update all floating texts
        Map<String, JSONObject> floatingTexts = model.getGameState().getFloatingTexts();
        for (String key : floatingTexts.keySet()) {
            FloatingText floatingText = (FloatingText) floatingTexts.get(key);
            floatingText.update();
            if (!floatingText.hasPassedLifeExpectancy()) { continue; }

            mGarbageCalculator.add(key);
        }

        // Cleanup expired texts
        while (!mGarbageCalculator.isEmpty()) {
            String keyToRemove = mGarbageCalculator.poll();
            model.getGameState().removeFloatingText(keyToRemove);
        }
    }
}