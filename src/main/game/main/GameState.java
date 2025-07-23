package main.game.main;

import main.constants.HashSlingingSlasher;
import main.constants.JSONCamera;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

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
    private static final String HOVERED_TILE = "hovered.tile";
    private static final String MAP_EDITOR_HOVERED_TILES_STORE = "hovered.tiles";
    private static final String FLOATING_TEXT_MAP = "floating.text.map";
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

        gameState.getOrCreateCamera(MAIN_CAMERA);
        gameState.getOrCreateCamera(SECONDARY_CAMERA);

        gameState.getViewport();
        gameState.getSecondaryCamera();

        gameState.setViewportZoom(1);
        gameState.setOriginalSpriteWidth(64);
        gameState.setOriginalSpriteHeight(64);
        gameState.setSpriteWidth(64);
        gameState.setSpriteHeight(64);

        gameState.put(FLOATING_TEXT_MAP, new JSONObject());
        gameState.setFloatingTextFontSize(20);

        gameState.setIsDebugMode(false);
        gameState.setGameMode(GAMEPLAY_MODE_REGULAR);

        gameState.setOptionShouldHideGameplayTileHeights(true);
        gameState.setConfigurableStateGameplayHudIsVisible(true);

        gameState.setSelectedTileIDs(new JSONArray());
        gameState.setHoveredTile(null);
        gameState.setMapEditorHoveredTiles(new JSONArray());
        gameState.addTileToGlideTo(null, null);
        gameState.setDeltaTime(0);

        gameState.setHoveredTilesCursorSize(1);
        gameState.setMapEditorCursorSize(1);
        gameState.setUnitWaitTimeBetweenActivity(1);

        gameState.setAnchorCameraToEntity("");
        gameState.setFreeFormCamera();

        gameState.setShouldForcefullyEndTurn(false);
        gameState.setShouldAutomaticallyEndUserTurn(true);
        gameState.setShouldAutomaticallyEndCpusTurn(true);

        return gameState;
    }

    public GameState(JSONObject input) {
        GameState defaults = getDefaults();
        for (String key : defaults.keySet()) { put(key, defaults.get(key)); }
        for (String key : input.keySet()) { put(key, input.get(key)); }
    }




    public String getMainCameraID() { return MAIN_CAMERA; }
    private JSONCamera getViewport() { return getOrCreateCamera(MAIN_CAMERA); }
    public String getSecondaryCameraID() { return SECONDARY_CAMERA; }
    public JSONCamera getSecondaryCamera() { return getOrCreateCamera(SECONDARY_CAMERA); }

    private JSONCamera getOrCreateCamera(String camera) {
        JSONObject cameraMap = getJSONObject(CAMERA_MAP);
        if (cameraMap == null) {
            cameraMap = new JSONObject();
            put(CAMERA_MAP, cameraMap);
        }

        JSONCamera cameraData = (JSONCamera) cameraMap.get(camera);
        if (cameraData == null) {
            cameraData = new JSONCamera();
            cameraMap.put(camera, cameraData);
        }
        return cameraData;
    }


    private static final String VIEWPORT_ZOOM = "viewport.zoom";
    public float getViewportZoom() { return getFloatValue(VIEWPORT_ZOOM); }
    public void setViewportZoom(float zoom) { put(VIEWPORT_ZOOM, zoom); }

    public GameState setViewportX(float x) { getViewport().setX(x); return this; }
    public GameState setViewportY(float y) { getViewport().setY(y); return this; }
    public int getViewportX() { return (int) getViewport().getX(); }
    public int getViewportY() { return (int) getViewport().getY(); }
    public GameState setViewportWidth(int width) { getViewport().setWidth(width); return this; }
    public GameState setViewportHeight(int height) { getViewport().setHeight(height); return this; }
    public int getMainCameraWidth() { return (int) getViewport().getWidth(); }
    public int getMainCameraHeight() { return (int) getViewport().getHeight(); }
    public int getGlobalX(int x) { return x - getViewportX(); }
    public int getGlobalY(int y) { return y - getViewportY(); }









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






    public int getSelectedTilesChecksum() { return getIntValue(SELECTED_TILES_CHECK_SUM, -1); }
    public JSONArray getSelectedTileIDs() { return getJSONArray(SELECTED_TILES); }
    public void setSelectedTileIDs(JSONArray tiles) {
        put(SELECTED_TILES, tiles);
        put(SELECTED_TILES_CHECK_SUM, HashSlingingSlasher.fastHash(tiles.toString()));
    }
    public void setSelectedTileIDs(String tileID) {
        if (tileID == null) {
            setSelectedTileIDs(EMPTY_JSON_ARRAY);
        } else {
            JSONArray array = new JSONArray();
            array.add(tileID);
            setSelectedTileIDs(array);
        }
    }

    public int getHoveredTilesHash() {
        JSONArray hoveredTiles = getHoveredTileIDs();
        int hash = -1;
        if (!hoveredTiles.isEmpty()) {
            String firstElement = hoveredTiles.getString(0);
            String lastElement = hoveredTiles.getString(hoveredTiles.size() - 1);
            int size = hoveredTiles.size();
            hash = Objects.hash(firstElement, lastElement, size);
        }
        return hash;
    }
    public JSONArray getHoveredTileIDs() { return getJSONArray(HOVERED_TILES_STORE); }
    public void setHoveredTiles(JSONArray tiles) { put(HOVERED_TILES_STORE, tiles); }
    public void setHoveredTiles(String tileID) {
        JSONArray currentHoverTiles = getJSONArray(HOVERED_TILES_STORE);
        currentHoverTiles.clear();
        currentHoverTiles.add(tileID);
    }

    public String getHoveredTileID() { return getString(HOVERED_TILE); }
    public void setHoveredTile(String hoveredTileID) { put(HOVERED_TILE, hoveredTileID); }




    public JSONArray getMapEditorHoveredTileIDs() { return getJSONArray(MAP_EDITOR_HOVERED_TILES_STORE); }
    public void setMapEditorHoveredTiles(JSONArray tiles) { put(MAP_EDITOR_HOVERED_TILES_STORE, tiles); }
    public void setMapEditorHoveredTiles(String tileID) {
        JSONArray currentHoverTiles = getJSONArray(MAP_EDITOR_HOVERED_TILES_STORE);
        currentHoverTiles.clear();
        currentHoverTiles.add(tileID);
    }




    private static final String TEAM_TO_UNITS_MAP = "TEAMS_TO_UNITS";
    private static final String UNIT_TO_TEAM_MAP = "ENTITIES_TO_TEAMS";
    public void addUnitToTeam(String unitID, String teamID) {
        JSONObject teams = getOrPutDefault(this, TEAM_TO_UNITS_MAP);
        JSONObject team = getOrPutDefault(teams, teamID);
        team.put(unitID, unitID);

        JSONObject entityMap = getOrPutDefault(this, UNIT_TO_TEAM_MAP);
        entityMap.put(unitID, teamID);
    }

    public String getTeam(String entityID) {
        JSONObject entityMap = getOrPutDefault(this, UNIT_TO_TEAM_MAP);
        String team = entityMap.getString(entityID);
        return team;
    }
    public JSONArray getAllUnits() {
        JSONObject entityMap = getOrPutDefault(this, UNIT_TO_TEAM_MAP);
        JSONArray entityList = new JSONArray();
        entityList.addAll(entityMap.keySet());
        return entityList;
    }

    private JSONObject getOrPutDefault(JSONObject root, String key) {
        JSONObject object = root.getJSONObject(key);
        if (object == null) {
            object = new JSONObject();
            root.put(key, object);
        }
        return object;
    }

//    public List<JSONObject> getHoveredTiles() {
//        JSONArray hoveredTiles = optJSONArray(HOVERED_TILES_STORE, EMPTY_JSON_ARRAY);
//        List<JSONObject> result = new ArrayList<>();
//        for (int index = 0; index < hoveredTiles.size(); index++) {
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



    private static final String IS_ABILITY_PANEL_OPEN = "is.ability.panel.open";
    public void updateIsAbilityPanelOpen(boolean isOpen) { put(IS_ABILITY_PANEL_OPEN, isOpen); }
    public boolean isAbilityPanelOpen() { return getBooleanValue(IS_ABILITY_PANEL_OPEN, false); }


    private static final String TRIGGER_OPEN_ABILITY_PANEL = "trigger.open.ability.panel";
    public boolean shouldOpenAbilityPanel() {
        boolean trigger = getBooleanValue(TRIGGER_OPEN_ABILITY_PANEL, false);
        put(TRIGGER_OPEN_ABILITY_PANEL, false);
        return trigger;
    }
    public void triggerOpenAbilityPanel() { put(TRIGGER_OPEN_ABILITY_PANEL, true); }



    private static final String IS_MOVEMENT_PANEL_OPEN = "is.movement.panel.open";
    public void updateIsMovementPanelOpen(boolean isOpen) { put(IS_MOVEMENT_PANEL_OPEN, isOpen); }
    public boolean isMovementPanelOpen() { return getBooleanValue(IS_MOVEMENT_PANEL_OPEN, false); }


    private static final String TRIGGER_OPEN_MOVEMENT_PANEL = "trigger.open.movement.panel";
    public boolean shouldOpenMovementPanel() {
        boolean trigger = getBooleanValue(TRIGGER_OPEN_MOVEMENT_PANEL, false);
        put(TRIGGER_OPEN_MOVEMENT_PANEL, false);
        return trigger;
    }
    public void triggerOpenMovementPanel() { put(TRIGGER_OPEN_MOVEMENT_PANEL, true); }


    private static final String IS_STATISTICS_PANEL_OPEN = "is.statistics.panel.open";
    public void updateIsStatisticsPanelOpen(boolean isOpen) { put(IS_STATISTICS_PANEL_OPEN, isOpen); }
    public boolean isStatisticsPanelOpen() { return getBooleanValue(IS_STATISTICS_PANEL_OPEN, false); }
    private static final String TRIGGER_OPEN_STATISTICS_PANEL = "trigger.open.statistics.panel";
    public boolean shouldOpenStatisticsPanel() {
        boolean trigger = getBooleanValue(TRIGGER_OPEN_STATISTICS_PANEL, false);
        put(TRIGGER_OPEN_STATISTICS_PANEL, false);
        return trigger;
    }
    public void triggerOpenStatisticsPanel() { put(TRIGGER_OPEN_STATISTICS_PANEL, true); }



    private static final String IS_GREATER_STATISTICS_PANEL_OPEN = "is.greater.statistics.panel.open";
    public void updateIsGreaterStatisticsPanelOpen(boolean b) { put(IS_GREATER_STATISTICS_PANEL_OPEN, b); }
    public boolean isGreaterStatisticsPanelOpen() { return getBooleanValue(IS_GREATER_STATISTICS_PANEL_OPEN); }

    private static final String TRIGGER_OPEN_GREATER_STATISTICS_PANEL = "trigger.open.greater.statistics.panel";
    public boolean shouldOpenGreaterStatisticsPanel() {
        boolean trigger = getBooleanValue(TRIGGER_OPEN_GREATER_STATISTICS_PANEL, false);
        put(TRIGGER_OPEN_GREATER_STATISTICS_PANEL, false);
        return trigger;
    }
    public void triggerOpenGreaterStatisticsPanel() { put(TRIGGER_OPEN_GREATER_STATISTICS_PANEL, true); }



    private static final String IS_GREATER_ABILITY_PANEL_OPEN = "is.greater.ability.panel.open";
    public void updateIsGreaterAbilityPanelOpen(boolean b) { put(IS_GREATER_ABILITY_PANEL_OPEN, b); }
    public boolean isGreaterAbilityPanelOpen() { return getBooleanValue(IS_GREATER_ABILITY_PANEL_OPEN); }
    private static final String TRIGGER_OPEN_GREATER_ABILITY_PANEL = "trigger.open.greater.ability.panel";
    public boolean shouldOpenGreaterAbilityPanel() {
        boolean trigger = getBooleanValue(TRIGGER_OPEN_GREATER_ABILITY_PANEL, false);
        put(TRIGGER_OPEN_GREATER_ABILITY_PANEL, false);
        return trigger;
    }
    public void triggerOpenGreaterAbilityPanel() { put(TRIGGER_OPEN_GREATER_ABILITY_PANEL, true); }




    private static final String IS_DAMAGE_PREVIEW_FROM_PANEL_OPEN = "is.damage.preview.from.panel.open";
    public void updateIsDamageFromPreviewPanelOpen(boolean b) { put(IS_DAMAGE_PREVIEW_FROM_PANEL_OPEN, b); }
    public boolean isDamageFromPreviewPanelOpen() { return getBooleanValue(IS_DAMAGE_PREVIEW_FROM_PANEL_OPEN, false); }


    private static final String IS_DAMAGE_PREVIEW_TO_PANEL_OPEN = "is.damage.to.from.panel.open";
    public void updateIsDamageToPreviewPanelOpen(boolean b) { put(IS_DAMAGE_PREVIEW_TO_PANEL_OPEN, b); }
    public boolean isDamageToPreviewPanelOpen() { return getBooleanValue(IS_DAMAGE_PREVIEW_TO_PANEL_OPEN, false); }



    private static final String AUTOMATICALLY_END_CONTROLLED_TURNS = "automatically.end.controlled.turns";
    public void setAutomaticallyEndControlledTurns(boolean value) { put(AUTOMATICALLY_END_CONTROLLED_TURNS, value); }
    public boolean shouldAutomaticallyEndControlledTurns() { return getBooleanValue(AUTOMATICALLY_END_CONTROLLED_TURNS, false); }

    private static final String AUTOMATICALLY_GO_TO_HOME_CONTROLS = "automatically.go.to.home.controls";
    public void setAutomaticallyGoToHomeControls(boolean value) { put(AUTOMATICALLY_GO_TO_HOME_CONTROLS, value); }
    public boolean shouldAutomaticallyGoToHomeControls() { return getBooleanValue(AUTOMATICALLY_GO_TO_HOME_CONTROLS, false); }


    private static final String FORCEFULLY_END_TURN = "forcefully.end.turn";
    public void setShouldForcefullyEndTurn(boolean b) { put(FORCEFULLY_END_TURN, b); }
    public boolean shouldForcefullyEndTurn() { return getBooleanValue(FORCEFULLY_END_TURN, false); }

    private static final String USER_SELECTED_STANDBY = "user.selected.standby";
    public void setUserSelectedStandby(boolean b) { put(USER_SELECTED_STANDBY, b); }
    public boolean isUserSelectedStandby() { return getBooleanValue(USER_SELECTED_STANDBY, false); }

    private static final String AUTOMATICALLY_END_USER_TURN = "automatically.end.user.turn";
    public void setShouldAutomaticallyEndUserTurn(boolean b) { put(AUTOMATICALLY_END_USER_TURN, b); }
    public boolean shouldAutomaticallyEndUserTurn() { return getBooleanValue(AUTOMATICALLY_END_USER_TURN, false); }

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

    public int getSpriteWidth() { return getIntValue(VIEW_SPRITE_WIDTH); }
    public GameState setSpriteWidth(int spriteWidth) { put(VIEW_SPRITE_WIDTH, spriteWidth); return this; }

    public int getSpriteHeight() { return getIntValue(VIEW_SPRITE_HEIGHT); }
    public GameState setSpriteHeight(int spriteHeight) { put(VIEW_SPRITE_HEIGHT, spriteHeight); return this; }





    public int getOriginalSpriteWidth() { return getIntValue(VIEW_ORIGINAL_SPRITE_WIDTH); }
    private GameState setOriginalSpriteWidth(int width) { put(VIEW_ORIGINAL_SPRITE_WIDTH, width); return this; }

    public int getOriginalSpriteHeight() { return getIntValue(VIEW_ORIGINAL_SPRITE_HEIGHT); }
    private GameState setOriginalSpriteHeight(int height) { put(VIEW_ORIGINAL_SPRITE_HEIGHT, height); return this; }




    private static final String MANUALLY_PASS_THROUGH_TURNS = "manually.pass.through.turns";
    public GameState setManuallyPassThroughTurns(boolean value) {
        return (GameState) fluentPut(MANUALLY_PASS_THROUGH_TURNS, value);
    }
    public boolean getManuallyPassThroughTurns() {
        return getBooleanValue(MANUALLY_PASS_THROUGH_TURNS);
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
    public boolean shouldShowActionRanges() { return getBooleanValue(SHOW_ACTION_RANGES, true); }
    public void setShouldShowMovementRanges(boolean value) { put(SHOW_MOVEMENT_RANGES, value); }
    public boolean shouldShowMovementRanges() { return getBooleanValue(SHOW_MOVEMENT_RANGES, true); }
    public void setOptionShouldHideGameplayTileHeights(boolean value) { put(OPTION_HIDE_TILE_HEIGHTS, value); }
    public boolean shouldHideGameplayTileHeights() { return getBooleanValue(OPTION_HIDE_TILE_HEIGHTS, true); }

    public boolean getConfigurableStateGameplayHudIsVisible() {
        return getBoolean(CONFIGURABLE_STATE_SET_GAMEPLAY_HUD_IS_VISIBLE);
    }
    public GameState setConfigurableStateGameplayHudIsVisible(boolean show) {
        put(CONFIGURABLE_STATE_SET_GAMEPLAY_HUD_IS_VISIBLE, show);
        return this;
    }


    public boolean addTileToGlideTo(String tileID, String camera) {
        JSONObject tileToGlideToDataList = getJSONObject(TILE_TO_GLIDE_TO_LIST);
        if (tileToGlideToDataList == null) { tileToGlideToDataList = new JSONObject(); }
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
        return getIntValue(HOVERED_TILES_CURSOR_SIZE);
    }


    private static final String MAP_EDITOR_CURSOR_SIZE = "map.editor.cursor.size";
    public int getMapEditorCursorSize() { return getIntValue(MAP_EDITOR_CURSOR_SIZE); }
    public void setMapEditorCursorSize(int size) { if (size <= 0) { return; } put(MAP_EDITOR_CURSOR_SIZE, size); }


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



    public void setDeltaTime(double deltaTime) { put(DELTA_TIME, deltaTime); }
    public double getDeltaTime() { return getDouble(DELTA_TIME); }
    private double mDeltaTime = 0;
    private double mLateUpdatedTime = 0;
    public void updateGameDeltaTime() {
        long currentTime = System.nanoTime();
        mDeltaTime = (currentTime - mLateUpdatedTime) / 1.0E9D;
        mLateUpdatedTime = currentTime;
        setDeltaTime(mDeltaTime);
//        System.out.println(mDeltaTime + " ?");
    }



    private static final String SHOULD_AUTOMATICALLY_END_CPUS_TURN = "should.automatically.end.cpus.turn";
    public void setShouldAutomaticallyEndCpusTurn(boolean value) {
        put(SHOULD_AUTOMATICALLY_END_CPUS_TURN, value);
    }
    public boolean shouldAutomaticallyEndCpusTurn() {
        return getBooleanValue(SHOULD_AUTOMATICALLY_END_CPUS_TURN, true);
    }



    private static final String CURRENT_ENTITIES_TURN = "current.entities.turn";
    public void setCurrentEntitiesTurn(String entityID) {
        put(CURRENT_ENTITIES_TURN, entityID);
    }
    public String getCurrentEntitiesTurn() {
        return (String) getOrDefault(CURRENT_ENTITIES_TURN, "");
    }

    private static final String TURN_START_DELAY_IN_SECONDS = "turn.start.delay.in.seconds";
    public void setCpuTurnStartDelayInSeconds(double seconds) {
        put(TURN_START_DELAY_IN_SECONDS, seconds);
    }
    public double getCpuTurnStartDelayInSeconds() {
        return getDoubleValue(TURN_START_DELAY_IN_SECONDS);
    }



    private static final String GAME_TIME_START_NS = "game.time.start.ns";
    public void updateGameStartTime() {
        if (containsKey(GAME_TIME_START_NS)) { return; }
        long startTime = System.nanoTime();
        put(GAME_TIME_START_NS, startTime);
    }
    public long getStartTime() { return getLongValue(GAME_TIME_START_NS, 0); }

    private static final String GAME_TIME_DURATION = "game.time.duration.ns";
    public long getGameDurationTime() {
        long duration = System.nanoTime() - getStartTime();
        return duration;
    }

    private static final String AUTO_BEHAVIOR_ENABLED = "auto.behavior";
    public GameState setAutoBehaviorEnabled(boolean value) {
        return (GameState) fluentPut(AUTO_BEHAVIOR_ENABLED, value);
    }

    public boolean isAutoBehaviorEnabled() {
        return (boolean) getOrDefault(AUTO_BEHAVIOR_ENABLED, true);
    }
}
