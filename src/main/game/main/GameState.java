package main.game.main;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import main.constants.StateLock;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.map.base.TileMap;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.List;
import java.util.Objects;

public class GameState extends JsonObject {

    public static final String CURRENTLY_SELECTED_TILES = "currently.selected.tile.entity";
    public static final String CURRENTLY_SELECTED_UNITS = "currently.selected.unit.entity";
    public static final String LAST_NON_NULL_SELECTED_TILES = "get_the_latest_non_null_selected_tile";
    public static final String LAST_NON_NULL_SELECTED_UNITS = "get_the_latest_non_null_selected_UNIT";
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
    private static final JsonArray EMPTY_JSON_ARRAY = new JsonArray();
    private static final JsonObject EMPTY_JSON_OBJECT = new JsonObject();
    private final StateLock mStateLock = new StateLock();
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());

    public GameState() {

    }

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

    public List<Entity> getLastNonNullSelectedUnitEntityV2() { return mLastNonNullSelectedUnits.stream().map(object -> (Entity)object).toList(); }
    public void setSelectedEntity(TileMap tileMap, Entity tileEntity) {


        put(GameState.CURRENTLY_SELECTED_TILES, tileEntity);
        put(GameState.CURRENTLY_SELECTED_UNITS, null);
        if (tileEntity == null) { return; }
        put(GameState.LAST_NON_NULL_SELECTED_TILES, tileEntity);

        Tile tile = tileEntity.get(Tile.class);
        Entity unitEntity = tile.getUnit();

        put(GameState.CURRENTLY_SELECTED_UNITS, unitEntity);
        if (unitEntity == null) { return; }
        put(GameState.LAST_NON_NULL_SELECTED_UNITS, unitEntity);
    }

    public void setSelectedEntityV1(Entity tileEntity) {
        put(GameState.CURRENTLY_SELECTED_TILES, tileEntity);
        put(GameState.CURRENTLY_SELECTED_UNITS, null);
        if (tileEntity == null) { return; }
        put(GameState.LAST_NON_NULL_SELECTED_TILES, tileEntity);

        Tile tile = tileEntity.get(Tile.class);
        Entity unitEntity = tile.getUnit();

        put(GameState.CURRENTLY_SELECTED_UNITS, unitEntity);
        if (unitEntity == null) { return; }
        put(GameState.LAST_NON_NULL_SELECTED_UNITS, unitEntity);
    }

    private final JsonArray mCurrentSelectedTiles = new JsonArray();
    private final JsonArray mLastNonNullSelectedUnits = new JsonArray();
//    public void setSelectedEntityV2(Entity[] tileEntities) {
//        if (tileEntities == null) { return; }
//        List<Entity> entityList = Arrays.asList(tileEntities);
//
//        mCurrentSelectedTiles.clear();
//        mCurrentSelectedTiles.addAll(entityList);
//        put(GameState.CURRENTLY_SELECTED_TILES, mCurrentSelectedTiles);
//        put(GameState.CURRENTLY_SELECTED_UNITS, null);
//        if (mCurrentSelectedTiles.isEmpty()) { return; }
//
//        mLastNonNullSelectedTiles.clear();
//        mLastNonNullSelectedTiles.addAll(entityList.stream().filter(Objects::nonNull).toList());
//        put(GameState.LAST_NON_NULL_SELECTED_TILES, mLastNonNullSelectedTiles);
//
//        mCurrentlySelectedUnits.clear();
//        mCurrentlySelectedUnits.addAll(entityList.stream().filter(Objects::nonNull).map(tileEntity -> tileEntity.get(Tile.class).getUnit()).toList());
//        put(GameState.CURRENTLY_SELECTED_UNITS, mCurrentlySelectedUnits);
//        if (mCurrentlySelectedUnits.isEmpty()) { return; }
//
//        mLastNonNullSelectedUnits.clear();
//        mLastNonNullSelectedUnits.addAll(entityList.stream().filter(Objects::nonNull).map(tileEntity -> tileEntity.get(Tile.class).getUnit()).filter(Objects::nonNull).toList());
//        put(GameState.LAST_NON_NULL_SELECTED_UNITS, mLastNonNullSelectedUnits);
//    }

    public Entity getSelectedTile(TileMap tileMap) {
        List<Entity> selectedTiles = getSelectedTiles(tileMap);
        Entity selectedTile = null;
        if (!selectedTiles.isEmpty()) {
            selectedTile = selectedTiles.get(0);
        }
        return selectedTile;
    }
    public List<Entity> getSelectedTiles(TileMap tileMap) {
        JsonArray selectedTiles = (JsonArray) getOrDefault(CURRENTLY_SELECTED_TILES, EMPTY_JSON_ARRAY);
        return selectedTiles.stream()
                .map(selectedTileObject -> {
                    JsonObject tileObject = (JsonObject) selectedTileObject;
                    int row = (int) tileObject.get(Tile.ROW);
                    int column = (int) tileObject.get(Tile.COLUMN);
                    return tileMap.tryFetchingEntityAt(row, column);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public boolean setSelectedTiles(JsonArray selectedTiles) {
        if (!mStateLock.isUpdated(GameState.CURRENTLY_SELECTED_TILES, selectedTiles.toString())) {
            return false;
        }
        put(GameState.CURRENTLY_SELECTED_TILES, selectedTiles);
        return true;

//        boolean success = true;
//        for (Object object: selectedTiles) {
//            if (!(object instanceof JsonObject selectedTile)) {
//                return false; }
//            if (selectedTile.size() < 2) { continue; }
//            if (selectedTile.containsKey(Tile.ROW) && selectedTile.containsKey(Tile.COLUMN)) { continue; }
//            success = false;
//        }
//        if (success) {
//            put(GameState.CURRENTLY_SELECTED_TILES, selectedTiles);
//        }
//
//        return success;
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
