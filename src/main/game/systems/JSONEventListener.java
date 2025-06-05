package main.game.systems;

import com.alibaba.fastjson2.JSONObject;

// Listener interface for JSON events.
public interface JSONEventListener {
    void onEvent(JSONObject event);
}
