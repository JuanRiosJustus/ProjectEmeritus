package main.constants;

import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GameState {

    public static final String CURRENTLY_SELECTED = "currently.selected.from.entity";
    public static final String PREVIOUSLY_SELECTED = "previously.selected.from.entity";
    public static final String MOVEMENT_HUD_IS_SHOWING = "movement.ui.showing";
    public static final String ACTION_HUD_IS_SHOWING = "action.ui.showing";
    public static final String ACTION_PANEL_SELECTED_ACTION = "action.ui.selected.ability";
    public static final String SUMMARY_HUD_IS_SHOWING = "condition.ui.showing";
    public static final String UI_SETTINGS_FAST_FORWARD_TURNS = "settings.ui.fastForwardTurns";
    public static final String UNDO_MOVEMENT_BUTTON_PRESSED = "ui.settings.undo.movement";
    public static final String ACTIONS_END_TURN = "actions.ui.endTurn";
    public static final String UI_GO_TO_CONTROL_HOME = "action.close.movement.ui";
    public static final String UI_END_TURN_PANEL_SHOWING = "end.ui.showing";
    public static final String GLIDE_TO_SELECTED = "glide.to.selected";
    public static final String INSPECTION_HUD_IS_SHOWING = "inspection.hud.is.showing";

    private final Map<String, Object> state = new HashMap<>();
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());

//    private static GameState instance = null;
//    private GameState() { }
//    public static GameState getInstance() {
//        if (instance == null) {
//            instance = new GameState();
//        }
//        return instance;
//    }

    public void set(String key, Object obj) { state.put(key, obj); }
    public void log(String key, Object obj) {
        logger.info("Storing key {0} with value {1}", key, obj);
        set(key, obj);
    }
    public boolean getBoolean(String key) {
        Object value = state.get(key);
        if (value == null) {
            return false;
        } else {
            return (boolean) value;
        }
    }

    public Object getObject(String key) { return state.get(key); }
}
