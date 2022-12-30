package ui.presets;

import constants.ColorPalette;
import constants.Constants;
import engine.Engine;
import game.GameController;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {

    private final Map<String, JPanel> scenes = new HashMap<>();

    public JPanel sceneSelectionPanel;
    private JComboBox<String> sceneOptions = null;

    private static final SceneManager instance = new SceneManager();
    public static SceneManager instance() { return instance; }

    public SceneManager() {

        int width = Constants.APPLICATION_WIDTH, height = Constants.APPLICATION_HEIGHT;

        scenes.put(Constants.MAIN_MENU_SCENE, new MenuScene(width, height));
        scenes.put(Constants.EDIT_SCENE, new EditorScene(width, height));
        scenes.put(Constants.GAME_SCENE, GameController.instance().scene);

        sceneSelectionPanel = getSceneSelectionPanel();
    }

    public JPanel getScene(String sceneName) {
        return scenes.get(sceneName);
    }

    public void setScene(String sceneName) {
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

        sceneOptions.addActionListener(e -> setScene((String) sceneOptions.getSelectedItem()));

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
