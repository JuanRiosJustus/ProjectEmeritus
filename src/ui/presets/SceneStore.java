package ui.presets;

import graphics.JScene;

import java.util.HashMap;

public class SceneStore {

    private HashMap<String, JScene> sceneHashMap = new HashMap<>();

    private SceneStore(){}
    private final static SceneStore instance = new SceneStore();
    public static SceneStore get() { return instance; }
}
