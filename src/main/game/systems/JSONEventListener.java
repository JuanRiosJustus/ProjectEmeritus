package main.game.systems;

import org.json.JSONObject;

// Listener interface for JSON events.
public interface JSONEventListener {
    void onEvent(JSONObject event);
}
