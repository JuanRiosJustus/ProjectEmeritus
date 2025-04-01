package main.game.events;

import main.game.main.GameModel;
import org.json.JSONObject;

// Listener interface for JSON events.
public interface JSONEventListener {
    void onEvent(JSONObject event);
}
