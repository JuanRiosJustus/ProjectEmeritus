package main.constants;

import com.github.cliftonlabs.json_simple.JsonObject;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class GameState {

    public static final String CURRENTLY_SELECTED = "currently.selected.from.entity";
    public static final String PREVIOUSLY_SELECTED = "previously.selected.from.entity";
    public static final String SHOW_SELECTED_UNIT_MOVEMENT_PATHING = "show_selected_unit_movement_pathing";
    public static final String SHOW_SELECTED_UNIT_ACTION_PATHING = "show_selected_unit_action_pathing";
    public static final String ACTION_HUD_IS_SHOWING = "skills.hud.showing";
    public static final String ACTION_PANEL_SELECTED_ACTION = "action.ui.selected.ability";
    public static final String SUMMARY_HUD_IS_SHOWING = "condition.ui.showing";
    public static final String UI_SETTINGS_FAST_FORWARD_TURNS = "settings.ui.fastForwardTurns";
    public static final String UNDO_MOVEMENT_BUTTON_PRESSED = "ui.settings.undo.movement";
    public static final String ACTIONS_END_TURN = "actions.ui.endTurn";
    public static final String END_CURRENT_UNITS_TURN = "end_turn_for_current_unit";
    public static final String UI_GO_TO_CONTROL_HOME = "action.close.movement.ui";
    public static final String UI_END_TURN_PANEL_SHOWING = "end.ui.showing";
    public static final String GLIDE_TO_SELECTED = "glide.to.selected";
    public static final String INSPECTION_HUD_IS_SHOWING = "inspection.hud.is.showing";
    public static final String CHANGE_BATTLE_UI_TO_HOME_SCREEN = "change_battle_ui_to_home_screen";

    //    private final Map<String, Object> state = new HashMap<>();
    private final JsonObject state = new JsonObject();
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());

    public void set(String key, Object obj) { state.put(key, obj); }
    public void log(String key, Object obj) {
        Object currentState = state.get(key);
        logger.info("Setting game state from {0} to {1}", currentState.toString(), obj);
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
