package main.game.main;

import com.github.cliftonlabs.json_simple.JsonObject;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class GameState extends JsonObject {

    public static final String CURRENTLY_SELECTED_TILE = "currently.selected.tile.entity";
    public static final String CURRENTLY_SELECTED_UNIT = "currently.selected.unit.entity";
    public static final String LATEST_NON_NULL_SELECTED_TILE = "get_the_latest_non_null_selected_tile";
    public static final String LATEST_NON_NULL_SELECTED_UNIT = "get_the_latest_non_null_selected_UNIT";
    public static final String PREVIOUSLY_SELECTED = "previously.selected.from.entity";
    public static final String SHOW_SELECTED_UNIT_MOVEMENT_PATHING = "show_selected_unit_movement_pathing";
    public static final String SHOW_SELECTED_UNIT_ACTION_PATHING = "show_selected_unit_action_pathing";
    public static final String ACTION_HUD_IS_SHOWING = "skills.hud.showing";
    public static final String UI_SETTINGS_FAST_FORWARD_TURNS = "settings.ui.fastForwardTurns";
    public static final String UNDO_MOVEMENT_BUTTON_PRESSED = "ui.settings.undo.movement";
    public static final String ACTIONS_END_TURN = "actions.ui.endTurn";
    public static final String END_CURRENT_UNITS_TURN = "end_turn_for_current_unit";
    public static final String UI_GO_TO_CONTROL_HOME = "action.close.movement.ui";
    public static final String GLIDE_TO_SELECTED = "glide.to.selected";
    public static final String TILE_TO_GLIDE_TO = "tile_to_glide_to";
    public static final String CHANGE_BATTLE_UI_TO_HOME_SCREEN = "change_battle_ui_to_home_screen";
    private static final String ACTION_PANEL_IS_OPEN = "is_action_panel_being_used";
    private static final String MOVEMENT_PANEL_BEING_USED = "is_movement_panel_being_used";
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());

    public void log(String key, Object obj) {
        Object currentState = get(key);
        logger.info("Setting game state from {0} to {1}", currentState.toString(), obj);
        put(key, obj);
    }
    public boolean getBoolean(String key) {
        Object value = get(key);
        if (value == null) {
            return false;
        } else {
            return (boolean) value;
        }
    }

    public Object getObject(String key) { return get(key); }


    public Entity getCurrentlySelectedTileEntity() { return (Entity) get(GameState.CURRENTLY_SELECTED_TILE); }
    public Entity getLastNonNullSelectedUnitEntity() { return (Entity) get(GameState.LATEST_NON_NULL_SELECTED_UNIT); }
    public void setupEntitySelections(Entity tileEntity) {
        put(GameState.CURRENTLY_SELECTED_TILE, tileEntity);
        put(GameState.CURRENTLY_SELECTED_UNIT, null);
        if (tileEntity == null) { return; }
        put(GameState.LATEST_NON_NULL_SELECTED_TILE, tileEntity);

        Tile tile = tileEntity.get(Tile.class);
        Entity unitEntity = tile.getUnit();

        put(GameState.CURRENTLY_SELECTED_UNIT, unitEntity);
        if (unitEntity == null) { return; }
        put(GameState.LATEST_NON_NULL_SELECTED_UNIT, unitEntity);
    }
    public boolean isActionPanelOpen() {
        return (boolean) getOrDefault(GameState.ACTION_PANEL_IS_OPEN, false);
    }
    public void setActionPanelIsOpen(boolean value) {
        put(GameState.ACTION_PANEL_IS_OPEN, value);
    }

    public boolean isMovementPanelOpen() {
        return (boolean) getOrDefault(GameState.MOVEMENT_PANEL_BEING_USED, false);
    }
    public void setMovementPanelIsOpen(boolean value) {
        put(GameState.MOVEMENT_PANEL_BEING_USED, value);
    }

    public boolean shouldChangeControllerToHomeScreen() {
        return (boolean) getOrDefault(GameState.CHANGE_BATTLE_UI_TO_HOME_SCREEN, false);
    }

    public void setControllerToHomeScreen(boolean value) {
        put(GameState.CHANGE_BATTLE_UI_TO_HOME_SCREEN, value);
    }
    public void setEndCurrentUnitsTurn(boolean value) { put(GameState.END_CURRENT_UNITS_TURN, value); }
    public boolean shoutEndCurrentUnitsTurn() {
        return (boolean) getOrDefault(GameState.END_CURRENT_UNITS_TURN, false);
    }
    public boolean shouldGlideToSelected() {
        return getOrDefault(GameState.TILE_TO_GLIDE_TO, null) != null;
    }

    public void setTileToGlideTo(Entity tileEntity) { put(GameState.TILE_TO_GLIDE_TO, tileEntity); }
    public Entity getTileToGlideTo() { return (Entity) getOrDefault(GameState.TILE_TO_GLIDE_TO, null); }
}
