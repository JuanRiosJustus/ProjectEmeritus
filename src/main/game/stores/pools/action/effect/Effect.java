package main.game.stores.pools.action.effect;

import main.constants.Vector3f;
import main.game.components.MovementComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.systems.texts.FloatingText;
import main.utils.StringUtils;
import org.json.JSONObject;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class Effect {
    protected final String UNDERSCORE_DELIMITER = "_";
    protected final List<Runnable> mOnCompleteListeners = new ArrayList<>();

    public Effect(JSONObject effect) { }

    public boolean validate(GameModel model, Entity user, Set<Entity> targets) {
        return true; // Default implementation (always valid)
    }
    /**
     * Apply the effect.
     *
     * @param model   The game model.
     * @param user    The entity applying the effect.
     * @param targets The targets of the effect.
     * @return True if the effect is asynchronous and will complete later; false if it is immediate.
     */
    public abstract boolean apply(GameModel model, Entity user, Set<Entity> targets);

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

    protected void announceWithFloatingTextCentered(GameModel model, String str, Entity unitEntity, Color color) {
        MovementComponent movementComponent = unitEntity.get(MovementComponent.class);
        Entity tileEntity = movementComponent.getCurrentTile();
        if (tileEntity == null) { return; }
        Tile tile = tileEntity.get(Tile.class);
        Vector3f vector3f = tile.getLocalVector(model);


        int spriteWidths = model.getGameState().getSpriteWidth();
        int spriteHeights = model.getGameState().getSpriteHeight();
        int x = (int) vector3f.x;
        int y = (int) vector3f.y - (spriteHeights / 2);

        str = StringUtils.convertSnakeCaseToCapitalized(str);

        model.getGameState().addFloatingText(new FloatingText(str, x, y, color, false));
    }
}
