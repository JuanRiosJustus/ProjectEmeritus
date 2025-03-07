package main.ui.presets.editor;

import main.engine.EngineScene;
import main.game.main.GameControllerV1;
import main.game.main.ui.GamePanelHud;

import javax.swing.*;
import java.awt.*;

public class GameScene extends EngineScene {

    private GameControllerV1 mGameControllerV1;
    private JPanel mGamePanel;
    private GamePanelHud gamePanelHud;

    public GameScene(GameControllerV1 controller, int width, int height) {
        super(width, height);

        // Set layout manager to null for manual positioning
        setLayout(null);

        // Initialize the game controller and game panel
        mGameControllerV1 = controller;
        mGameControllerV1.run();

        // Create the layered pane
        JLayeredPane container = new JLayeredPane();
        container.setBounds(0, 0, width, height); // Correct bounds
        container.setLayout(null); // No layout for manual positioning
        container.setPreferredSize(new Dimension(width, height));

        // Add the game panel to the default layer
        mGamePanel = controller.getGamePanel(width, height);
        mGamePanel.setBounds(0, 0, width, height); // Correct bounds
        container.add(mGamePanel, JLayeredPane.DEFAULT_LAYER); // Lowest layer

        // Add the HUD to a higher layer
        gamePanelHud = new GamePanelHud(mGameControllerV1, width, height);
        gamePanelHud.setBounds(0, 0, width, height); // Correct bounds
        gamePanelHud.setOpaque(false); // Ensure transparency for overlap
        container.add(gamePanelHud, JLayeredPane.PALETTE_LAYER); // Next highest layer

        // Add the layered pane to the GameScene
        container.setOpaque(true);
        add(container);

        setVisible(true);
    }

    @Override
    public void update() {
        mGameControllerV1.update();
        gamePanelHud.gameUpdate(mGameControllerV1);
    }

    @Override
    public void input() {
        mGameControllerV1.input();
    }

    @Override
    public JPanel render() {
        return this;
    }
}