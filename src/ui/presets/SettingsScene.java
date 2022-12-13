package ui.presets;

import engine.EngineModel;
import graphics.JScene;

import java.awt.*;
import java.util.HashMap;

public class SettingsScene extends JScene {

    private CardLayout cardLayout = new CardLayout(40, 50);

    private HashMap<String, Component> settings = new HashMap<>();

    public SettingsScene(EngineModel model, int width, int height) {
        super(width, height, "");

//        SceneManager sceneManager = new SceneManager();

//        JPanel panel = new JPanel();
//        panel.setBackground(Color.GREEN);
//        panel.add(new JKeyValue("Resolution", "???"));

//        sceneManager.addScene("test", new JButton("Tototot"));
//        add(sceneManager);
//        add(new JButton("Testing"));
        setBackground(Color.RED);

    }


}
