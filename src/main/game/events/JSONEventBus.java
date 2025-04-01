package main.game.events;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONEventBus {
    private final Map<String, List<JSONEventListener>> listenersMap = new HashMap<>();

    /**
     * Registers a listener for a specific event type.
     *
     * @param eventType The event type (key) to listen for.
     * @param listener  The listener that will handle the event.
     */
    public void subscribe(String eventType, JSONEventListener listener) {
        listenersMap.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    /**
     * Unregisters a listener for a specific event type.
     *
     * @param eventType The event type.
     * @param listener  The listener to remove.
     */
    public void unsubscribe(String eventType, JSONEventListener listener) {
        List<JSONEventListener> listeners = listenersMap.get(eventType);
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
        List<JSONEventListener> listeners = listenersMap.get(eventType);
        if (listeners == null) { return; }
        // Create a copy to avoid concurrent modification if a listener unsubscribes during handling.
        List<JSONEventListener> listenersCopy = new ArrayList<>(listeners);
        for (JSONEventListener listener : listenersCopy) {
            listener.onEvent(eventData);
        }
    }
}