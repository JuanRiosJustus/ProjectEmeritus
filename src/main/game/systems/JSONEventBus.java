package main.game.systems;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONEventBus {
    private final Map<String, List<JSONEventListener>> mListenerMap = new HashMap<>();

    /**
     * Registers a listener for a specific event type.
     *
     * @param eventType The event type (key) to listen for.
     * @param listener  The listener that will handle the event.
     */
    public void subscribe(String eventType, JSONEventListener listener) {
        mListenerMap.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    /**
     * Unregisters a listener for a specific event type.
     *
     * @param eventType The event type.
     * @param listener  The listener to remove.
     */
    public void unsubscribe(String eventType, JSONEventListener listener) {
        List<JSONEventListener> listeners = mListenerMap.get(eventType);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    /**
     * Publishes an event to all listeners subscribed to the event type.
     *
     * @param eventType The event type.
     * @param eventData The JSONObject representing the event.
     */
    public void publish(String eventType, JSONObject eventData) {
        List<JSONEventListener> listeners = mListenerMap.get(eventType);
        if (listeners == null) { return; }
        // Create a copy to avoid concurrent modification if a listener unsubscribes during handling.
        List<JSONEventListener> listenersCopy = new ArrayList<>(listeners);
        for (JSONEventListener listener : listenersCopy) {
            listener.onEvent(eventData);
        }
    }

    /**
     * Publishes an event to all listeners subscribed to the event type.
     *
     * @param eventType The event type.
     * @param payload The JSONObject representing the event.
     */
    public void publish(JSONObject payload) {
        String event = payload.getString("event");
        List<JSONEventListener> listeners = mListenerMap.get(event);
        if (listeners == null) { return; }
        // Create a copy to avoid concurrent modification if a listener unsubscribes during handling.
        List<JSONEventListener> listenersCopy = new ArrayList<>(listeners);
        for (JSONEventListener listener : listenersCopy) {
            listener.onEvent(payload);
        }
    }
}