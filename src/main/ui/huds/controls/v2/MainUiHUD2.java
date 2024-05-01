package main.ui.huds.controls.v2;

import main.constants.Constants;
import main.constants.Settings;
import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.graphics.JScene;
import main.ui.custom.SwingUiUtils;
import main.ui.panels.AccordionV2;

import javax.swing.*;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

public class MainUiHUD2 extends JScene {
    private ButtonTabbedPane mButtonTabbedPane = null;
    public MainUiHUD2(int width, int height) {
        super(width, height, "test");

//        setLayout(null);
        setBackground(ColorPalette.TRANSPARENT);
        setOpaque(false);

        mButtonTabbedPane = new ButtonTabbedPane(width, height);
        add(mButtonTabbedPane);
    }

    public void addPanel(String componentName, JComponent component) {
        mButtonTabbedPane.addPanel(componentName, component);
    }

    @Override
    public void jSceneUpdate(GameModel model) {
        mButtonTabbedPane.jSceneUpdate(model);
    }

    public int getButtonWidth() { return mButtonTabbedPane.getButtonWidth(); }
    public int geButtonHeight() { return mButtonTabbedPane.geButtonHeight(); }
    public int getDisplayWidth() { return mButtonTabbedPane.getDisplayWidth(); }
    public int geDisplayHeight() { return mButtonTabbedPane.geDisplayHeight(); }
    public JButton getButton(String key) { return mButtonTabbedPane.getButton(key); }
}
