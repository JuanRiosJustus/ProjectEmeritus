package ui.presets;

import constants.ColorPalette;
import constants.Constants;
import engine.Engine;
import game.GameController;
import ui.panels.ControlPanel;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {

    private final Map<String, JPanel> scenes = new HashMap<>();

    public JPanel sceneSelectionPanel;
    private JComboBox<String> sceneOptions = null;

    private static SceneManager instance = null;
    public static SceneManager instance() { if (instance == null) { instance = new SceneManager(); } return instance; }

    public static final String MAIN_MENU_SCENE = "MainMenuScene";
    public static final String EDITOR_SCENE = "EditorScene";
    public static final String MAIN_CONTROLS_SCENE = "MainControlsScene";
    public static final String GAME_SCENE = "GameScene";

    public SceneManager() {

//        int width = Constants.APPLICATION_WIDTH, height = Constants.APPLICATION_HEIGHT;

        // Non-game/Pre-game scenes
//        scenes.put(MAIN_MENU_SCENE, new MenuScene(width, height));
//        scenes.put(EDITOR_SCENE, new EditorScene(width, height));
//        scenes.put(MAIN_CONTROLS_SCENE, new ControlPanel(width, height));

//        scenes.put(GAME_SCENE, GameController.instance().scene);

        sceneSelectionPanel = getSceneSelectionPanel();
    }

    public void install(String name, JPanel scene) {
        scenes.put(name, scene);
        System.out.println(name + " scene installed");
    }

    public JPanel get(String sceneName) {
        return scenes.get(sceneName);
    }

    public void set(String sceneName) {
        JPanel scene = scenes.get(sceneName);
        if (scene == null) { return; }
        Engine.instance().controller.view.setScene(scene);

        sceneOptions.setSelectedItem(sceneName);
    }

    public JPanel getSceneSelectionPanel() {
        if (sceneSelectionPanel != null) { return sceneSelectionPanel; }
        sceneSelectionPanel = new JPanel();
//        sceneSelectionPanel.setLayout(null);

        sceneOptions = new JComboBox<>();
        for (String key : scenes.keySet()) {
            sceneOptions.addItem(key);
        }

        sceneOptions.addActionListener(e -> set((String) sceneOptions.getSelectedItem()));

        JPanel containerPanel = new JPanel();
        containerPanel.add(new JLabel("Scene Select: "));
        containerPanel.add(sceneOptions);
        containerPanel.setBackground(ColorPalette.WHITE);
//        containerPanel.setBounds(50, 50, containerPanel.getWidth(), containerPanel.getHeight());

        sceneSelectionPanel.add(containerPanel);
        sceneSelectionPanel.setOpaque(false);

        return sceneSelectionPanel;
    }
}
