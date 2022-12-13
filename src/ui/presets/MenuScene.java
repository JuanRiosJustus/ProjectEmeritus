package ui.presets;

import constants.Constants;
import engine.EngineModel;
import graphics.JScene;
import utils.ComponentUtils;

import javax.swing.*;
import java.awt.*;

public class MenuScene extends JScene {

    public final JButton startButton = new JButton(Constants.START_BUTTON);
    public final JButton exitButton = new JButton(Constants.EXIT_BUTTON);
    public final JButton settingsButton = ComponentUtils.createJButton(Constants.SETTINGS_BUTTON); //new JButton(Constants.SETTINGS_BUTTON);

    private final JPanel scene = new JPanel(new BorderLayout());

    public MenuScene(EngineModel model, int width, int height) {
        super(width, height, "");
        setLayout(new BorderLayout());

//        gridBagConstraints.insets = new Insets(10, 5, 10, 5);
//        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

//        for (int i = 0; i < 30; i++) {
//            scene.add(new JLabel(""), gridBagConstraints);
//        }

        JPanel buttonContainer = new JPanel();
//        buttonContainer.add(startButton);
//        startButton.addActionListener(e -> model.sceneManager.setScene(Constants.GAME_SCENE));
//        buttonContainer.add(settingsButton);
//        settingsButton.addActionListener(e -> model.sceneManager.setScene(Constants.SETTINGS_SCENE));
//        buttonContainer.add(exitButton);
//        exitButton.addActionListener(e -> Engine.get().stop());

        add(buttonContainer, BorderLayout.SOUTH);
        setBackground(Color.ORANGE);
    }
}
