package main.game.main;

import main.constants.HashSlingingSlasher;
import main.constants.JSONShape;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class GameState extends JSONObject {
    private static final String SHOW_ACTION_RANGES = "Show.action.Ranges";
    private static final String SHOW_MOVEMENT_RANGES = "Show.movement.ranges";
    private static final String OPTION_HIDE_TILE_HEIGHTS = "show.heights";
    public static final String CONFIGURABLE_STATE_SET_GAMEPLAY_HUD_IS_VISIBLE = "hide.gameplay.ui";
    public static final String VIEW_SPRITE_WIDTH = "view.sprite.width";
    public static final String VIEW_SPRITE_HEIGHT = "view.sprite.height";
    public static final String VIEW_ORIGINAL_SPRITE_WIDTH = "view.original.sprite.width";
    public static final String VIEW_ORIGINAL_SPRITE_HEIGHT = "view.original.sprite.height";
    private static final String TILE_TO_GLIDE_TO_LIST = "tile.to.glide.to";
    private static final String TILE_TO_GLIDE_TO_ID = "id";
    private static final String TILE_TO_GLIDE_TO_CAMERA = "camera";
    public static final String GAMEPLAY_MODE = "gameplay.mode";
    public static final String GAMEPLAY_MODE_MAP_EDITOR_MODE = "gameplay.map.editor.mode";
    public static final String GAMEPLAY_MODE_UNIT_DEPLOYMENT = "gameplay.mode.load.out";
    public static final String GAMEPLAY_MODE_REGULAR = "gameplay.mode.regular";
    public static final String GAMEPLAY_DEBUG_MODE = "debug_mode_for_the_game_state";

    public static final String MODEL_MAP_DESCRIPTION = "model.map.description";

    private static final JSONArray EMPTY_JSON_ARRAY = new JSONArray();
    private static final JSONObject EMPTY_JSON_OBJECT = new JSONObject();

    private static final String SELECTED_TILES = "selected.tiles";
    private static final String SELECTED_TILES_CHECK_SUM = "selected.tiles.checksum";
    private static final String HOVERED_TILES_STORE = "hovered.tiles";
    private static final String FLOATING_TEXT_MAP = "floating_text_map";
    private static final String FLOATING_TEXT_FONT_SIZE = "floating_text_font_size";
    private static final String ABILITY_SELECTED_FROM_UI = "selected_ability_from_ui";
    private static final String DELTA_TIME = "delta_time";
    private static final String MAIN_CAMERA = "0";
    private static final String SECONDARY_CAMERA = "1";
    private static final String CAMERA_MAP = "camera.map";;
    private static final String EMPTY_STRING = "";

    private static final String HUD_IS_VISIBLE = "hud.is.visible";
    private static final String HOVERED_TILES_CURSOR_SIZE = "hovered.tiles.cursor.size";

    private static final String EVENT_QUEUE = "event.system.queue";


    private GameState() {}

    public static GameState getDefaults() {
        GameState gameState = new GameState();

        gameState.put(EVENT_QUEUE, new JSONObject());

        gameState.getMainCamera();
        gameState.getSecondaryCamera();

        gameState.setSpriteWidth(64);
        gameState.setSpriteHeight(64);

        gameState.put(FLOATING_TEXT_MAP, new JSONObject());
        gameState.setFloatingTextFontSize(30);

        gameState.setIsDebugMode(false);
        gameState.setGameMode(GAMEPLAY_MODE_REGULAR);

        gameState.setOptionShouldHideGameplayTileHeights(true);
        gameState.setConfigurableStateGameplayHudIsVisible(true);

        gameState.setSelectedTileIDs(new JSONArray());
        gameState.setHoveredTiles(new JSONArray());
        gameState.addTileToGlideTo(null, null);
        gameState.setAbilitySelectedFromUI("");
        gameState.setDeltaTime(0);

        gameState.setHoveredTilesCursorSize(1);
        gameState.setUnitWaitTimeBetweenActivity(1);
        gameState.setAnchorCameraToEntity("");
        gameState.setFreeFormCamera();

        gameState.setShouldForcefullyEndTurn(false);
        gameState.setShouldAutomaticallyEndUserTurn(true);

        return gameState;
    }

    public GameState(JSONObject input) {
        GameState defaults = getDefaults();
        for (String key : defaults.keySet()) { put(key, defaults.get(key)); }
        for (String key : input.keySet()) { put(key, input.get(key)); }
    }


    public JSONObject consumeEventQueue() {
        JSONObject eventQueue = getJSONObject(EVENT_QUEUE);
        if (eventQueue.isEmpty()) { return eventQueue; }
        JSONObject newQueue = new JSONObject();
        for (String key : eventQueue.keySet()) {
            JSONObject object = eventQueue.getJSONObject(key);
            newQueue.put(key, object);
        }
        eventQueue.clear();
        return newQueue;
    }


    public String getMainCameraID() { return MAIN_CAMERA; }
    private JSONShape getMainCamera() { return getOrCreateCamera(MAIN_CAMERA); }
    public String getSecondaryCameraID() { return SECONDARY_CAMERA; }
    public JSONShape getSecondaryCamera() { return getOrCreateCamera(SECONDARY_CAMERA); }

    private JSONShape getOrCreateCamera(String camera) {
        JSONObject cameraMap = optJSONObject(CAMERA_MAP, new JSONObject());
        put(CAMERA_MAP, cameraMap);

        JSONShape cameraRep = (JSONShape) cameraMap.optJSONObject(camera, new JSONShape());
        cameraMap.put(camera, cameraRep);

        return cameraRep;
    }

    public GameState setMainCameraX(float x) { getMainCamera().setX(x); return this; }
    public GameState setMainCameraY(float y) { getMainCamera().setY(y); return this; }
    public int getMainCameraX() { return (int) getMainCamera().getX(); }
    public int getMainCameraY() { return (int) getMainCamera().getY(); }
    public GameState setMainCameraWidth(int width) { getMainCamera().setWidth(width); return this; }
    public GameState setMainCameraHeight(int height) { getMainCamera().setHeight(height); return this; }
    public int getMainCameraWidth() { return (int) getMainCamera().getWidth(); }
    public int getMainCameraHeight() { return (int) getMainCamera().getHeight(); }
    public int getGlobalX(int x) { return x - getMainCameraX(); }
    public int getGlobalY(int y) { return y - getMainCameraY(); }









    public GameState setCameraX(String camera, float x) { getOrCreateCamera(camera).setX(x); return this; }
    public GameState setCameraY(String camera, float y) { getOrCreateCamera(camera).setY(y); return this; }
    public int getCameraX(String camera) { return (int) getOrCreateCamera(camera).getX(); }
    public int getCameraY(String camera) { return (int) getOrCreateCamera(camera).getY(); }
    public GameState setCameraWidth(String camera, int width) { getOrCreateCamera(camera).setWidth(width); return this; }
    public GameState setCameraHeight(String camera, int height) { getOrCreateCamera(camera).setHeight(height); return this; }
    public int getCameraWidth(String camera) { return (int) getOrCreateCamera(camera).getWidth(); }
    public int getCameraHeight(String camera) { return (int) getOrCreateCamera(camera).getHeight(); }


    public int getGlobalX(String camera, int x) { return x - getCameraX(camera); }
    public int getGlobalY(String camera, int y) { return y - getCameraY(camera); }








    private final List<JSONObject> mEphemeralList = new ArrayList<>();
    private final Map<String, JSONObject> mEpemeralMap = new HashMap<>();


    public int getSelectedTilesChecksum() { return optInt(SELECTED_TILES_CHECK_SUM); }
    public JSONArray getSelectedTileIDs() { return getJSONArray(SELECTED_TILES); }
    public void setSelectedTileIDs(JSONArray tiles) {
        put(SELECTED_TILES, tiles);
        put(SELECTED_TILES_CHECK_SUM, HashSlingingSlasher.fastHash(tiles.toString()));
    }
    public void setSelectedTileIDs(String tileID) { setSelectedTileIDs(tileID == null ? EMPTY_JSON_ARRAY : new JSONArray().put(tileID)); }

    public int getHoveredTilesHash() {
        JSONArray hoveredTiles = getHoveredTileIDs();
        int hash = -1;
        if (!hoveredTiles.isEmpty()) {
            String firstElement = hoveredTiles.getString(0);
            String lastElement = hoveredTiles.getString(hoveredTiles.length() - 1);
            int size = hoveredTiles.length();
            hash = Objects.hash(firstElement, lastElement, size);
        }
        return hash;
    }
    public JSONArray getHoveredTileIDs() { return getJSONArray(HOVERED_TILES_STORE); }
    public void setHoveredTiles(JSONArray tiles) { put(HOVERED_TILES_STORE, tiles); }
    public void setHoveredTiles(String tileID) {
        JSONArray currentHoverTiles = getJSONArray(HOVERED_TILES_STORE);
        currentHoverTiles.clear();
        currentHoverTiles.put(tileID);
    }






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



    public static final String ABILITY_PANEL_IS_OPEN = "ability.panel.is.open";
    public void setAbilityPanelIsOpen(boolean isOpen) { put(ABILITY_PANEL_IS_OPEN, isOpen); }
    public boolean isAbilityPanelOpen() { return optBoolean(ABILITY_PANEL_IS_OPEN, false); }


    public static final String MOVEMENT_PANEL_IS_OPEN = "movement.panel.is.open";
    public void setMovementPanelIsOpen(boolean isOpen) { put(MOVEMENT_PANEL_IS_OPEN, isOpen); }
    public boolean isMovementPanelOpen() { return optBoolean(MOVEMENT_PANEL_IS_OPEN, false); }


    public static final String STATISTICS_PANEL_IS_OPEN = "statistics.panel.is.open";
    public void setStatisticsInformationPanelOpen(boolean isOpen) { put(STATISTICS_PANEL_IS_OPEN, isOpen); }
    public boolean isStatisticsPanelOpen() { return optBoolean(STATISTICS_PANEL_IS_OPEN, false); }

    private static final String GREATER_STATISTICS_PANEL_IS_OPEN = "greater.statistics.panel.is.open";
    public void setGreaterStatisticsPanelOpen(boolean b) { put(GREATER_STATISTICS_PANEL_IS_OPEN, b); }
    public boolean isGreaterStatisticsPanelOpen() { return optBoolean(GREATER_STATISTICS_PANEL_IS_OPEN); }

    private static final String GREATER_ABILITY_PANEL_IS_OPEN = "greater.ability.panel.is.open";
    public void setGreaterAbilityInformationPanelOpen(boolean b) { put(GREATER_ABILITY_PANEL_IS_OPEN, b); }
    public boolean isGreaterAbilityInformationPanelOpen() { return optBoolean(GREATER_ABILITY_PANEL_IS_OPEN); }


    private static final String AUTOMATICALLY_END_CONTROLLED_TURNS = "automatically.end.controlled.turns";
    public void setAutomaticallyEndControlledTurns(boolean value) { put(AUTOMATICALLY_END_CONTROLLED_TURNS, value); }
    public boolean shouldAutomaticallyEndControlledTurns() { return optBoolean(AUTOMATICALLY_END_CONTROLLED_TURNS, false); }

    private static final String AUTOMATICALLY_GO_TO_HOME_CONTROLS = "automatically.go.to.home.controls";
    public void setAutomaticallyGoToHomeControls(boolean value) { put(AUTOMATICALLY_GO_TO_HOME_CONTROLS, value); }
    public boolean shouldAutomaticallyGoToHomeControls() { return optBoolean(AUTOMATICALLY_GO_TO_HOME_CONTROLS, false); }


    private static final String FORCEFULLY_END_TURN = "forcefully.end.turn";
    public void setShouldForcefullyEndTurn(boolean b) { put(FORCEFULLY_END_TURN, b); }
    public boolean shouldForcefullyEndTurn() { return optBoolean(FORCEFULLY_END_TURN, false); }

    private static final String USER_SELECTED_STANDBY = "user.selected.standby";
    public void setUserSelectedStandby(boolean b) { put(USER_SELECTED_STANDBY, b); }
    public boolean isUserSelectedStandby() { return optBoolean(USER_SELECTED_STANDBY, false); }

    private static final String AUTOMATICALLY_END_USER_TURN = "automatically.end.user.turn";
    public void setShouldAutomaticallyEndUserTurn(boolean b) { put(AUTOMATICALLY_END_USER_TURN, b); }
    public boolean shouldAutomaticallyEndUserTurn() { return optBoolean(AUTOMATICALLY_END_USER_TURN, false); }

    private static final String UNIT_WAIT_TIME_BETWEEN_ACTIVITY = "unit.wait.time.between.activities";
    public void setUnitWaitTimeBetweenActivity(int t) { put(UNIT_WAIT_TIME_BETWEEN_ACTIVITY, t); }
    public float getUnitWaitTimeBetweenActivities() { return getFloat(UNIT_WAIT_TIME_BETWEEN_ACTIVITY); }

    private static final String ANCHOR_CAMERA_TO_ENTITY = "anchor.camera.to.entity";
    public void setAnchorCameraToEntity(String id) { put(ANCHOR_CAMERA_TO_ENTITY, id); }
    public String getAnchorCameraToEntity() { return getString(ANCHOR_CAMERA_TO_ENTITY); }
    public boolean shouldAnchorCameraToEntity() { return !getAnchorCameraToEntity().isEmpty(); }




    private static final String CAMERA_MODE = "camera.mode";
    private static final String FREE_FORM_CAMERA = "camera.mode.free.form";
    private static final String LOCK_ON_ACTIVITY_CAMERA = "camera.mode.locked.on.activity";
    private static final String FIXED_ON_ACTIVE_CAMERA = "camera.mode.fixed.on.active";
    public void setCameraMode(String id) { put(CAMERA_MODE, id); }
    public void setFixedOnActiveCamera() { setCameraMode(FIXED_ON_ACTIVE_CAMERA); }
    public void setFreeFormCamera() { setCameraMode(FREE_FORM_CAMERA); }
    public void setLockOnActivityCamera() { setCameraMode(LOCK_ON_ACTIVITY_CAMERA); }
    public String getCameraMode() { return getString(CAMERA_MODE); }
    public boolean isFixedOnActiveCamera() { return getCameraMode().equals(FIXED_ON_ACTIVE_CAMERA); }
    public boolean isFreeFormCamera() { return getCameraMode().equals(FREE_FORM_CAMERA); }
    public boolean isLockOnActivityCamera() { return getCameraMode().equals(LOCK_ON_ACTIVITY_CAMERA); }
    public JSONArray getCameraModes() { return new JSONArray(List.of(FREE_FORM_CAMERA, LOCK_ON_ACTIVITY_CAMERA, FIXED_ON_ACTIVE_CAMERA)); }
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

    public int getSpriteWidth() { return getInt(VIEW_SPRITE_WIDTH); }
    public GameState setSpriteWidth(int spriteWidth) { put(VIEW_SPRITE_WIDTH, spriteWidth); return this; }

    public int getSpriteHeight() { return getInt(VIEW_SPRITE_HEIGHT); }
    public GameState setSpriteHeight(int spriteHeight) { put(VIEW_SPRITE_HEIGHT, spriteHeight); return this; }


    public int getOriginalSpriteWidth() { return getInt(VIEW_ORIGINAL_SPRITE_WIDTH); }
    public GameState setOriginalSpriteWidth(int spriteWidth) { put(VIEW_ORIGINAL_SPRITE_WIDTH, spriteWidth); return this; }

    public int getOriginalSpriteHeight() { return getInt(VIEW_ORIGINAL_SPRITE_HEIGHT); }
    public GameState setOriginalSpriteHeight(int spriteHeight) { put(VIEW_ORIGINAL_SPRITE_HEIGHT, spriteHeight); return this; }





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

    public boolean getConfigurableStateGameplayHudIsVisible() {
        return getBoolean(CONFIGURABLE_STATE_SET_GAMEPLAY_HUD_IS_VISIBLE);
    }
    public GameState setConfigurableStateGameplayHudIsVisible(boolean show) {
        put(CONFIGURABLE_STATE_SET_GAMEPLAY_HUD_IS_VISIBLE, show);
        return this;
    }


    public boolean addTileToGlideTo(String tileID, String camera) {
        JSONObject tileToGlideToDataList = optJSONObject(TILE_TO_GLIDE_TO_LIST, new JSONObject());
        put(TILE_TO_GLIDE_TO_LIST, tileToGlideToDataList);

        if (tileID == null || camera == null) { return false; }
        JSONObject newTileToGlideTo = new JSONObject();
        newTileToGlideTo.put(TILE_TO_GLIDE_TO_ID, tileID);
        newTileToGlideTo.put(TILE_TO_GLIDE_TO_CAMERA, camera);
        tileToGlideToDataList.put(camera, newTileToGlideTo);
        return true;
    }

    public JSONObject consumeTilesToGlideTo() {
        JSONObject tileToGlideToList = getJSONObject(TILE_TO_GLIDE_TO_LIST);
        JSONObject result = null;
        if (!tileToGlideToList.isEmpty()) {
            result = new JSONObject();
            for (String key : tileToGlideToList.keySet()) {
                JSONObject value = tileToGlideToList.getJSONObject(key);
                result.put(key, value);
            }
            tileToGlideToList.clear();
        }
        return result;
    }



    public void setHoveredTilesCursorSize(int size) {
        if (size <= 0) { return; }
        put(HOVERED_TILES_CURSOR_SIZE, size);
    }

    public int getHoveredTilesCursorSize() {
        return getInt(HOVERED_TILES_CURSOR_SIZE);
    }


//    public boolean hasTileToGlideTo() { return !getTileToGlideTo().equalsIgnoreCase(EMPTY_STRING); }
//    public void setTileToGlideTo(String tileID) { put(TILE_TO_GLIDE_TO, tileID == null ? EMPTY_STRING : tileID); }
//    public void setTileToGlideTo(String tileID, String camera) {
//        JSONObject toGlideToData = new JSONObject();
//        toGlideToData.put(TILE_TO_GLIDE_TO_ID, tileID);
//        toGlideToData.put(TILE_TO_GLIDE_TO_CAMERA, camera);
//        put(TILE_TO_GLIDE_TO, toGlideToData);
////        put(TILE_TO_GLIDE_TO, tileID == null ? EMPTY_STRING : tileID); }
//    }
//    public String getTileToGlideTo() { return optString(TILE_TO_GLIDE_TO, EMPTY_STRING); }






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


    public void setAbilitySelectedFromUI(String ability) { put(ABILITY_SELECTED_FROM_UI, ability == null ? EMPTY_STRING : ability); }
    public String getAbilitySelectedFromUI() { return getString(ABILITY_SELECTED_FROM_UI); }

    public void setDeltaTime(double deltaTime) { put(DELTA_TIME, deltaTime); }
    public double getDeltaTime() { return getDouble(DELTA_TIME); }
}
