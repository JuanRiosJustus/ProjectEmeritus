package main.game.systems.texts;

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
        if (movementComponent == null) { return; }

        String currentTileID = movementComponent.getCurrentTileID();
        Entity tileEntity = getEntityWithID(currentTileID);
        if (tileEntity == null) { return; }
        TileComponent tile = tileEntity.get(TileComponent.class);
        Vector3f vector3f = tile.getLocalVector(mGameModel);

        int spriteWidths = mGameModel.getGameState().getSpriteWidth();
        int spriteHeights = mGameModel.getGameState().getSpriteHeight();
        int baseX = (int) vector3f.x;
        int baseY = (int) vector3f.y - (spriteHeights / 2);

        // Capitalize text
        String capitalizedString = StringUtils.convertSnakeCaseToCapitalized(text);

        // Font size and lifetime randomization
        float fontSize = mGameModel.getGameState().getFloatingTextFontSize();
        float variedFontSize = fontSize + new Random().nextInt((int)(fontSize * 0.25f));
        int lifeTime = new Random().nextInt(2, 4);

        // --- Staggering Logic ---

        // 1. Count nearby floating texts
        int numFloatingTextsHere = (int) mGameModel.getGameState().getFloatingTexts().values().stream()
                .filter(ft -> {
                    FloatingText floatingText = (FloatingText) ft;
                    return Math.abs(floatingText.getX() - baseX) < 10 && Math.abs(floatingText.getY() - baseY) < 10;
                })
                .count();

        // 2. Apply vertical stagger
        int staggerAmount = 12; // pixels to move each additional floating text
        int finalY = baseY - (numFloatingTextsHere * staggerAmount);

        // 3. Apply horizontal wobble
        int horizontalWobble = new Random().nextInt(-24, 27); // between -6 and +6
        int finalX = baseX + horizontalWobble;

        // 4. (Optional) Time stagger - small delay before appearing
        // long delayInNanos = (numFloatingTextsHere * 50_000_000L); // 50 ms delay per text
        // long spawnTime = System.nanoTime() + delayInNanos;

        // Create and add floating text
        ShrinkingFloatingText floatingText = new ShrinkingFloatingText(
                capitalizedString,
                variedFontSize,
                finalX,
                finalY,
                ColorPalette.WHITE_LEVEL_4,
                lifeTime
        );

        // floatingText.setSpawnTime(spawnTime); // If you add spawnTime support to FloatingText base class

        mGameModel.getGameState().addFloatingText(floatingText);
    }

//    public void handleFloatingTextEvent(JSONObject event) {
//        String text = event.getString(FLOAT_TEXT_EVENT_TEXT);
//        String unitID = event.getString(FLOAT_TEXT_EVENT_UNIT_ID);
//
//        Entity unitEntity = getEntityWithID(unitID);
//        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
//        String currentTileID = movementComponent.getCurrentTileID();
//        Entity tileEntity = getEntityWithID(currentTileID);
//        if (tileEntity == null) { return; }
//        TileComponent tile = tileEntity.get(TileComponent.class);
//        Vector3f vector3f = tile.getLocalVector(mGameModel);
//
//
//        int spriteWidths = mGameModel.getGameState().getSpriteWidth();
//        int spriteHeights = mGameModel.getGameState().getSpriteHeight();
//        int x = (int) vector3f.x;
//        int y = (int) vector3f.y - (spriteHeights / 2);
//
//        text = StringUtils.convertSnakeCaseToCapitalized(text);
//
//        float fontSize = mGameModel.getGameState().getFloatingTextFontSize();
//        float variedFontSize = fontSize + new Random().nextInt((int)(fontSize * 0.25f));
//        int lifeTime = new Random().nextInt(2, 4);
//
//        String capitalizedString = StringUtils.convertSnakeCaseToCapitalized(text);
////        mGameModel.getGameState().addFloatingText(new RandomizedFloatingText(
////                capitalizedString,
////                variedFontSize,
////                x,
////                y,
////                ColorPalette.WHITE_LEVEL_4,
////                lifeTime
////        ));
//
//        mGameModel.getGameState().addFloatingText(new ShrinkingFloatingText(
//                capitalizedString,
//                variedFontSize,
//                x,
//                y,
//                ColorPalette.WHITE_LEVEL_4,
//                lifeTime
//        ));
//    }


    @Override
    public void update(GameModel model, SystemContext systemContext) {
        Map<String, JSONObject> floatingTexts = model.getGameState().getFloatingTexts();
        for (String key : floatingTexts.keySet()) {
            FloatingText floatingText = (FloatingText) floatingTexts.get(key);
            floatingText.update();
            if (!floatingText.hasPassedLifeExpectancy()) { continue; }
            mGarbageCalculator.add(key);
        }

        // remove the floating text that have been collected
        while (!mGarbageCalculator.isEmpty()) {
            String keyToRemove = mGarbageCalculator.poll();
            model.getGameState().removeFloatingText(keyToRemove);
        }
    }
}