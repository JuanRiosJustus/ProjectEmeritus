package ui;

import constants.Constants;

import java.util.HashMap;
import java.util.Map;

public class GameUiModel {

    private final Map<String, Object> settings = new HashMap<>();

    public GameUiModel() {
        settings.put(Constants.SETTINGS_UI_AUTOENDTURNS, true);
    }

    public void set(String key, Object obj) { settings.put(key, obj); }
    public boolean getBoolean(String key) {
        if (!settings.containsKey(key)) { return false; }
        return (boolean) settings.get(key); }
    public String getString(String key) { return (String) settings.get(key); }
}
