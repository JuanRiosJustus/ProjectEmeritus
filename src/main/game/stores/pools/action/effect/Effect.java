package main.game.stores.pools.action.effect;

import main.constants.Tuple;
import main.constants.Vector3f;
import main.game.components.MovementComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.factories.EntityStore;
import main.game.systems.texts.RandomizedFloatingText;
import main.utils.MathUtils;
import main.utils.StringUtils;
import org.json.JSONObject;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SplittableRandom;

public abstract class Effect {
    protected final SplittableRandom mRandom = new SplittableRandom();
    protected final List<Runnable> mOnCompleteListeners = new ArrayList<>();
    protected final JSONObject mEffect;
    public Effect(JSONObject effect) { mEffect = effect; }

    public Entity getEntityFromID(String id) { return EntityStore.getInstance().get(id); }

    public boolean validateV2(GameModel model, String userID, Set<String> targetTileID) {
        return true; // Default implementation (always valid)
    }

    /**
     * Apply the effect.
     *
     * @param model   The game model.
     * @param userID    The user id applying the effect.
     * @param targetTileIDs The target tile ids of the effect.
     * @return True if the effect is asynchronous and will complete later; false if it is immediate.
     */
    public abstract boolean apply(GameModel model, String userID, Set<String> targetTileIDs);

    /**
     * Add a listener that will be called when the effect is complete.
     *
     * @param listener The listener to call upon completion.
     */
    public void addOnCompleteListener(Runnable listener) {
        mOnCompleteListeners.add(listener);
    }

    /**
     * Notify all listeners that the effect is complete.
     */
    protected void notifyComplete() {
        for (Runnable listener : mOnCompleteListeners) {
            listener.run();
        }
        mOnCompleteListeners.clear(); // Clear listeners to avoid duplicate notifications
    }

    protected List<Tuple<String, String, Float>> getAttributeScalars(JSONObject containingScalars) {
        List<Tuple<String, String, Float>> result = new ArrayList<>();
        for (String key : containingScalars.keySet()) {
            // Keys for scaling must end in "_scaling"
            if (!key.endsWith("_scaling")) { continue; }

            String type = key.substring(0, key.indexOf("_"));
            String attribute = key.substring(key.indexOf("_") + 1, key.lastIndexOf("_"));
            String keyword = key.substring(key.lastIndexOf("_") + 1);
            float value = containingScalars.getFloat(key);

            result.add(new Tuple<>(type, attribute, value));
        }
        return result;
    }
    public boolean passesChanceOutOf100(float successChance){
        boolean success = MathUtils.passesChanceOutOf100(successChance);
        return success;
    }
//    protected void announceWithFloatingTextCentered(GameModel model, String str, Entity unitEntity, Color color) {
//        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
//        Entity tileEntity = movementComponent.getCurrentTileV1();
//        if (tileEntity == null) { return; }
//        Tile tile = tileEntity.get(Tile.class);
//        Vector3f vector3f = tile.getLocalVector(model);
//
//
//        int spriteWidths = model.getGameState().getSpriteWidth();
//        int spriteHeights = model.getGameState().getSpriteHeight();
//        int x = (int) vector3f.x;
//        int y = (int) vector3f.y - (spriteHeights / 2);
//
//        str = StringUtils.convertSnakeCaseToCapitalized(str);
//
//        float fontSize = model.getGameState().getFloatingTextFontSize();
//        float variedFontSize = fontSize + mRandom.nextInt((int)(fontSize * 0.25f));
//        int lifeTime = mRandom.nextInt(2, 4);
//
//        String capitalizedString = StringUtils.convertSnakeCaseToCapitalized(str);
//        model.getGameState().addFloatingText(new RandomizedFloatingText(capitalizedString, variedFontSize, x, y, color, lifeTime));
//    }

    protected void announceWithFloatingTextCentered(GameModel model, String str, String unitID, Color color) {
        Entity unitEntity = getEntityFromID(unitID);
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        String currentTileID = movementComponent.getCurrentTileID();
        Entity tileEntity = getEntityFromID(currentTileID);
        if (tileEntity == null) { return; }
        Tile tile = tileEntity.get(Tile.class);
        Vector3f vector3f = tile.getLocalVector(model);


        int spriteWidths = model.getGameState().getSpriteWidth();
        int spriteHeights = model.getGameState().getSpriteHeight();
        int x = (int) vector3f.x;
        int y = (int) vector3f.y - (spriteHeights / 2);

        str = StringUtils.convertSnakeCaseToCapitalized(str);

        float fontSize = model.getGameState().getFloatingTextFontSize();
        float variedFontSize = fontSize + mRandom.nextInt((int)(fontSize * 0.25f));
        int lifeTime = mRandom.nextInt(2, 4);

        String capitalizedString = StringUtils.convertSnakeCaseToCapitalized(str);
        model.getGameState().addFloatingText(new RandomizedFloatingText(capitalizedString, variedFontSize, x, y, color, lifeTime));
    }
}
