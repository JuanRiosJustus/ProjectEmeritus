package main.game.systems.texts;

import main.constants.Vector3f;
import main.game.components.MovementComponent;
import main.game.components.TileComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.ColorPalette;
import main.game.systems.GameSystem;
import main.game.systems.SystemContext;
import main.utils.StringUtils;
import org.json.JSONObject;

import java.util.*;

public class FloatingTextSystem extends GameSystem {
    private final Queue<String> mGarbageCalculator = new LinkedList<>();

    public FloatingTextSystem(GameModel gameModel) {
        super(gameModel);
        mEventBus.subscribe(FLOATING_TEXT_EVENT, this::handleFloatingTextEvent);
    }


    public static final String FLOATING_TEXT_EVENT = "floating.text.event";
    private static final String FLOAT_TEXT_EVENT_TEXT = "float.text.event_text";
    private static final String FLOAT_TEXT_EVENT_UNIT_ID = "float.text.event.unit_id";
    private static final String FLOAT_TEXT_EVENT_TYPE = "floating.text.event.type";
    public static JSONObject createFloatingTextEvent(String txt, String unitID) {
        JSONObject event = new JSONObject();
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
        if (tileEntity == null) { return; }
        TileComponent tile = tileEntity.get(TileComponent.class);
        Vector3f vector3f = tile.getLocalVector(mGameModel);


        int spriteWidths = mGameModel.getGameState().getSpriteWidth();
        int spriteHeights = mGameModel.getGameState().getSpriteHeight();
        int x = (int) vector3f.x;
        int y = (int) vector3f.y - (spriteHeights / 2);

        text = StringUtils.convertSnakeCaseToCapitalized(text);

        float fontSize = mGameModel.getGameState().getFloatingTextFontSize();
        float variedFontSize = fontSize + new Random().nextInt((int)(fontSize * 0.25f));
        int lifeTime = new Random().nextInt(2, 4);

        String capitalizedString = StringUtils.convertSnakeCaseToCapitalized(text);
//        mGameModel.getGameState().addFloatingText(new RandomizedFloatingText(
//                capitalizedString,
//                variedFontSize,
//                x,
//                y,
//                ColorPalette.WHITE_LEVEL_4,
//                lifeTime
//        ));

        mGameModel.getGameState().addFloatingText(new ShrinkingFloatingText(
                capitalizedString,
                variedFontSize,
                x,
                y,
                ColorPalette.WHITE_LEVEL_4,
                lifeTime
        ));
    }


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