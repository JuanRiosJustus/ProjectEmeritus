package main.game.main;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class GameState extends JSONObject {
    private static final String SHOW_ACTION_RANGES = "Show.action.Ranges";
    private static final String SHOW_MOVEMENT_RANGES = "Show_movement_ranges";
    private static final String OPTION_HIDE_TILE_HEIGHTS = "show.heights";

    public static final String GAMEPLAY_AUTO_END_TURNS = "auto.end.turns";
    public static final String GAMEPLAY_FAST_FORWARD_TURNS = "speed.turns";
    public static final String OPTION_HIDE_GAMEPLAY_HUD = "hide.gameplay.ui";

    public static final String VIEW_VIEWPORT_WIDTH = "view.width";
    public static final String VIEW_VIEWPORT_HEIGHT = "view.height";
    public static final String VIEW_VIEWPORT_CAMERA_X = "view.camera.x";
    public static final String VIEW_VIEWPORT_CAMERA_Y = "view.camera.y";
    public static final String VIEW_VIEWPORT_CAMERA = "view.camera";
    public static final String VIEW_SPRITE_WIDTH = "view_sprite_width";
    public static final String VIEW_SPRITE_HEIGHT = "view_sprite_height";

    private static final String MODEL_GAME_STATE_TILE_TO_GLIDE_TO = "tile.to.glide.to";


    public static final String GAMEPLAY_MODE = "gameplay.mode";
    public static final String GAMEPLAY_MODE_MAP_EDITOR_MODE = "gameplay.map.editor.mode";
    public static final String GAMEPLAY_MODE_UNIT_DEPLOYMENT = "gameplay.mode.load.out";
    public static final String GAMEPLAY_MODE_REGULAR = "gameplay.mode.regular";
    public static final String GAMEPLAY_DEBUG_MODE = "debug_mode_for_the_game_state";

    public static final String MODEL_MAP_DESCRIPTION = "model.map.description";

    private static final JSONArray EMPTY_JSON_ARRAY = new JSONArray();
    private static final JSONObject EMPTY_JSON_OBJECT = new JSONObject();

    private static final String SELECTED_TILES_STORE = "selected.tiles";
    private static final String HOVERED_TILES_STORE = "hovered.tiles";
    private static final String FLOATING_TEXT_MAP = "floating_text_map";
    private static final String FLOATING_TEXT_FONT_SIZE = "floating_text_font_size";

    private GameState() {}

    public GameState getDefaults() {
        GameState gameState = new GameState();
        gameState.setViewportHeight(1280);
        gameState.setViewportHeight(720);
        gameState.setCameraX(0);
        gameState.setCameraY(0);
        gameState.setSpriteWidth(64);
        gameState.setSpriteHeight(64);

        gameState.put(FLOATING_TEXT_MAP, new JSONObject());
        gameState.setFloatingTextFontSize(20);

        gameState.setIsDebugMode(false);
        gameState.setGameMode(GAMEPLAY_MODE_REGULAR);

        gameState.setOptionShouldHideGameplayTileHeights(true);
        gameState.setOptionHideGameplayHUD(true);

        gameState.setSelectedTiles(new JSONArray());
        gameState.setHoveredTiles(new JSONArray());

        return gameState;
    }
    public GameState(JSONObject input) {
        JSONObject defaults = getDefaults();
        for (String key : defaults.keySet()) { put(key, defaults.get(key)); }
        for (String key : input.keySet()) { put(key, input.get(key)); }
    }

    public GameState setCameraX(float x) { put(VIEW_VIEWPORT_CAMERA_X, x); return this; }
    public GameState setCameraY(float y) { put(VIEW_VIEWPORT_CAMERA_Y, y); return this; }
    public int getCameraX() { return getInt(VIEW_VIEWPORT_CAMERA_X); }
    public int getCameraY() { return getInt(VIEW_VIEWPORT_CAMERA_Y); }
    public int getGlobalX(int x) { return x - getCameraX(); }
    public int getGlobalY(int y) { return y - getCameraY(); }

    private final List<JSONObject> mEphemeralList = new ArrayList<>();
    private final Map<String, JSONObject> mEpemeralMap = new HashMap<>();


    public List<JSONObject> getSelectedTilesV1() {
        List<JSONObject> result = new ArrayList<>();
        JSONArray selectedTiles = optJSONArray(SELECTED_TILES_STORE, EMPTY_JSON_ARRAY);

        for (int index = 0; index < selectedTiles.length(); index++) {
            JSONObject jsonObject = selectedTiles.getJSONObject(index);
            result.add(jsonObject);
        }
        return result;
    }
    public void setSelectedTilesV1(JSONArray tiles) { put(SELECTED_TILES_STORE, tiles); }
    public void setSelectedTilesV1(JSONObject tile) {
        setSelectedTilesV1(tile == null ? EMPTY_JSON_ARRAY : new JSONArray().put(tile));
    }

    public JSONArray getSelectedTiles() { return getJSONArray(SELECTED_TILES_STORE); }
    public void setSelectedTiles(JSONArray tiles) { put(SELECTED_TILES_STORE, tiles); }
    public void setSelectedTiles(String tileID) { setSelectedTiles(tileID == null ? EMPTY_JSON_ARRAY : new JSONArray().put(tileID)); }

    public JSONArray getHoveredTiles() { return getJSONArray(HOVERED_TILES_STORE); }
    public void setHoveredTiles(JSONArray tiles) { put(HOVERED_TILES_STORE, tiles); }
    public void setHoveredTiles(String tileID) { setHoveredTiles(tileID == null ? EMPTY_JSON_ARRAY : new JSONArray().put(tileID)); }






    private boolean locked = false;
    public void setLockForHoveredTiles(boolean lockState) {
        locked = lockState;
    }
//    public List<JSONObject> getHoveredTiles() {
//        JSONArray hoveredTiles = optJSONArray(HOVERED_TILES_STORE, EMPTY_JSON_ARRAY);
//        List<JSONObject> result = new ArrayList<>();
//        for (int index = 0; index < hoveredTiles.length(); index++) {
//            JSONObject jsonObject = hoveredTiles.getJSONObject(index);
//            result.add(jsonObject);
//        }
//        return result;
//    }
//    public void setHoveredTiles(JSONArray tiles) {
////        if (locked) { return; }
//        put(HOVERED_TILES_STORE, tiles);
//    }
//    public void setHoveredTiles(JSONObject tile) {
//        setHoveredTiles(tile == null ? EMPTY_JSON_ARRAY : new JSONArray().put(tile));
//    }


    public void addFloatingText(JSONObject text) {
        JSONObject floatingTextMap = getJSONObject(FLOATING_TEXT_MAP);
        floatingTextMap.put(UUID.randomUUID().toString(), text);
    }

    public void removeFloatingText(String key) {
        JSONObject floatingTextStore = getJSONObject(FLOATING_TEXT_MAP);
        floatingTextStore.remove(key);
    }

    public Map<String, JSONObject> getFloatingTexts() {
        JSONObject floatingTextMap = getJSONObject(FLOATING_TEXT_MAP);

        Map<String, JSONObject> result = new LinkedHashMap<>();
        for (String key : floatingTextMap.keySet()) {
            JSONObject floatingText = floatingTextMap.getJSONObject(key);
            result.put(key, floatingText);
        }

        return result;
    }



    public static final String ACTION_PANEL_IS_OPEN = "action.panel.is.open";
    public void setActionPanelIsOpen(boolean isOpen) { put(ACTION_PANEL_IS_OPEN, isOpen); }
    public boolean isActionPanelOpen() { return optBoolean(ACTION_PANEL_IS_OPEN, false); }

    private static final String SHOULD_CLOSE_ACTION_PANEL = "should.close.action.panel";
    public boolean shouldCloseActionPanel() { return optBoolean(SHOULD_CLOSE_ACTION_PANEL, false); }
    public void setShouldCloseActionPanel(boolean b) { put(SHOULD_CLOSE_ACTION_PANEL, b); }


    public static final String MOVEMENT_PANEL_IS_OPEN = "movement.panel.is.open";
    public void setMovementPanelIsOpen(boolean isOpen) { put(MOVEMENT_PANEL_IS_OPEN, isOpen); }
    public boolean isMovementPanelOpen() { return optBoolean(MOVEMENT_PANEL_IS_OPEN, false); }

    private static final String SHOULD_CLOSE_MOVEMENT_PANEL = "should.close.movement.panel";
    public boolean shouldCloseMovementPanel() { return optBoolean(SHOULD_CLOSE_MOVEMENT_PANEL, false); }
    public void setShouldCloseMovementPanel(boolean b) { put(SHOULD_CLOSE_MOVEMENT_PANEL, b); }



    private static final String AUTOMATICALLY_END_CONTROLLED_TURNS = "automatically.end.controlled.turns";
    public void setAutomaticallyEndControlledTurns(boolean value) { put(AUTOMATICALLY_END_CONTROLLED_TURNS, value); }
    public boolean shouldAutomaticallyEndControlledTurns() { return optBoolean(AUTOMATICALLY_END_CONTROLLED_TURNS, false); }

    private static final String AUTOMATICALLY_GO_TO_HOME_CONTROLS = "automatically.go.to.home.controls";
    public void setAutomaticallyGoToHomeControls(boolean value) { put(AUTOMATICALLY_GO_TO_HOME_CONTROLS, value); }
    public boolean shouldAutomaticallyGoToHomeControls() { return optBoolean(AUTOMATICALLY_GO_TO_HOME_CONTROLS, false); }


    private static final String SHOULD_END_THE_TURN = "should.end.the.turn";
    public void setShouldEndTheTurn(boolean b) { put(SHOULD_END_THE_TURN, b); }
    public boolean shouldEndTheTurn() { return optBoolean(SHOULD_END_THE_TURN, false); }
    /**
     *
     *  __   __  ___   _______  _     _
     * |  | |  ||   | |       || | _ | |
     * |  |_|  ||   | |    ___|| || || |
     * |       ||   | |   |___ |       |
     * |       ||   | |    ___||       |
     *  |     | |   | |   |___ |   _   |
     *   |___|  |___| |_______||__| |__|
     *  _______  _______  _______  _______  _______
     * |       ||       ||   _   ||       ||       |
     * |  _____||_     _||  |_|  ||_     _||    ___|
     * | |_____   |   |  |       |  |   |  |   |___
     * |_____  |  |   |  |       |  |   |  |    ___|
     *  _____| |  |   |  |   _   |  |   |  |   |___
     * |_______|  |___|  |__| |__|  |___|  |_______|
     *
     * View State, Viewstate
     */

    public int getViewportWidth() { return getInt(VIEW_VIEWPORT_WIDTH); }
    public GameState setViewportWidth(int width) {
        put(VIEW_VIEWPORT_WIDTH, width);
        return this;
    }

    public int getViewportHeight() { return getInt(VIEW_VIEWPORT_HEIGHT); }
    public GameState setViewportHeight(int height) {
        put(VIEW_VIEWPORT_HEIGHT, height);
        return this;
    }

    public int getSpriteWidth() { return getInt(VIEW_SPRITE_WIDTH); }
    public GameState setSpriteWidth(int spriteWidth) {
        put(VIEW_SPRITE_WIDTH, spriteWidth);
        return this;
    }

    public int getSpriteHeight() { return getInt(VIEW_SPRITE_HEIGHT); }
    public GameState setSpriteHeight(int spriteHeight) {
        put(VIEW_SPRITE_HEIGHT, spriteHeight);
        return this;
    }





    public void setGameMode(String mode) { put(GAMEPLAY_MODE, mode); }
    public void setModeAsUnitDeploymentMode() { put(GAMEPLAY_MODE, GAMEPLAY_MODE_UNIT_DEPLOYMENT); }
    public boolean isUnitDeploymentMode() { return get(GAMEPLAY_MODE).equals(GAMEPLAY_MODE_UNIT_DEPLOYMENT); }
    public void setModeAsMapEditorMode() { put(GAMEPLAY_MODE, GAMEPLAY_MODE_MAP_EDITOR_MODE); }
    public boolean isMapEditorMode() { return get(GAMEPLAY_MODE).equals(GAMEPLAY_MODE_MAP_EDITOR_MODE); }

    private static final String SHOULD_FAST_FORWARD_TURNS = "should.fast.forward.turns";
    public void setShouldFastForwardTurns(boolean value) { put(SHOULD_FAST_FORWARD_TURNS, value); }
    public boolean shouldFastForwardTurns() { return getBoolean(SHOULD_FAST_FORWARD_TURNS); }

//    public boolean isDebugMode() { return getBoolean(GAMEPLAY_DEBUG_MODE); }
    public void setShouldShowActionRanges(boolean value) { put(SHOW_ACTION_RANGES, value); }
    public boolean shouldShowActionRanges() { return optBoolean(SHOW_ACTION_RANGES, true); }
    public void setShouldShowMovementRanges(boolean value) { put(SHOW_MOVEMENT_RANGES, value); }
    public boolean shouldShowMovementRanges() { return optBoolean(SHOW_MOVEMENT_RANGES, true); }
    public void setOptionShouldHideGameplayTileHeights(boolean value) { put(OPTION_HIDE_TILE_HEIGHTS, value); }
    public boolean shouldHideGameplayTileHeights() { return optBoolean(OPTION_HIDE_TILE_HEIGHTS, true); }

    public boolean setOptionHideGameplayHUD() { return getBoolean(OPTION_HIDE_GAMEPLAY_HUD); }
    public GameState setOptionHideGameplayHUD(boolean show) {
        put(OPTION_HIDE_GAMEPLAY_HUD, show);
        return this;
    }



    public void setTileToGlideTo(JSONObject tile) { put(MODEL_GAME_STATE_TILE_TO_GLIDE_TO, tile == null ? EMPTY_JSON_OBJECT : tile); }
    public JSONObject getTileToGlideTo() {
        return optJSONObject(MODEL_GAME_STATE_TILE_TO_GLIDE_TO, EMPTY_JSON_OBJECT);
    }



    public boolean isDebugMode() { return getBoolean(GAMEPLAY_DEBUG_MODE); }
    public GameState setIsDebugMode(boolean isDebugMode) {
        put(GAMEPLAY_DEBUG_MODE, isDebugMode);
        return this;
    }

    public float getFloatingTextFontSize() { return getFloat(FLOATING_TEXT_FONT_SIZE); }
    public GameState setFloatingTextFontSize(float size) {
        put(FLOATING_TEXT_FONT_SIZE, size);
        return this;
    }
}
