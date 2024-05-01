package main.ui.huds.controls.v2;

import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.graphics.JScene;
import main.ui.custom.SwingUiUtils;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ButtonTabbedPane extends JScene {
    private final JPanel mButtonContainer = new JPanel();
    private final JPanel mDisplayContainer = new JPanel();
    private final Map<String, JButton> mButtonMap = new HashMap<>();
    private int mButtonWidth = 0;
    private int mButtonHeight = 0;
    private int mDisplayWidth = 0;
    private int mDisplayHeight = 0;
    public ButtonTabbedPane(int width, int height) {
        super(width, height, "");
        width = (int) (width * .9f);
        height = (int) (height * .9f);

        setLayout(new GridBagLayout());

        setPreferredSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));

        GridBagConstraints mConstraints = new GridBagConstraints();
        mConstraints.weightx = 1;
        mConstraints.weighty = 1;
        mConstraints.gridx = 0;
        mConstraints.gridy = 0;
        float contentRatio = .7f;

        mDisplayWidth = width;
        mDisplayHeight = (int) (height * contentRatio);
        mDisplayContainer.setLayout(new CardLayout());
        mDisplayContainer.setPreferredSize(new Dimension(mDisplayWidth, mDisplayHeight));
        mDisplayContainer.setMaximumSize(new Dimension(mDisplayWidth, mDisplayHeight));
        mDisplayContainer.setMinimumSize(new Dimension(mDisplayWidth, mDisplayHeight));
        mDisplayContainer.setOpaque(true);
        mDisplayContainer.setBackground(ColorPalette.TRANSPARENT);
        add(mDisplayContainer, mConstraints);

        float selectorRatio = (1 - contentRatio);
        int containerPanelHeight = (int) ((height * selectorRatio) * .8);
        mButtonContainer.setLayout(new BoxLayout(mButtonContainer, BoxLayout.X_AXIS));
        mButtonContainer.setOpaque(true);
        mButtonContainer.setBackground(ColorPalette.TRANSPARENT);
//        mButtonContainer.setBackground(ColorPalette.GREEN);
        mButtonWidth = width / 4;
        mButtonHeight = containerPanelHeight;
        JScrollPane pane = SwingUiUtils.createBonelessScrollingPane(width, containerPanelHeight, mButtonContainer);

        mConstraints.gridy = 1;
        add(pane, mConstraints);
        setBackground(ColorPalette.GREEN);
    }

    public void addPanel(String panelName, JComponent component) {

        JButton newButton = new JButton(panelName);
//        newButton.setBackground(ColorPalette.RED);
        newButton.setForeground(ColorPalette.WHITE);
        newButton.setFont(FontPool.getInstance().getFont(newButton.getFont().getSize()).deriveFont(Font.BOLD));
//        newButton.setFont(newButton.getFont().deriveFont(Font.BOLD));
//        newButton.setBorderPainted(false);
        newButton.setFocusPainted(false);

        newButton.setPreferredSize(new Dimension(mButtonWidth, mButtonHeight));
        newButton.setMaximumSize(new Dimension(mButtonWidth, mButtonHeight));
        newButton.setMinimumSize(new Dimension(mButtonWidth, mButtonHeight));
        newButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) mDisplayContainer.getLayout();
            cardLayout.show(mDisplayContainer, panelName);
        });

        // If nothing is set, set something
        mDisplayContainer.add(component, panelName);
        if (mDisplayContainer.getComponentCount() == 0) {
            CardLayout cardLayout = (CardLayout) mDisplayContainer.getLayout();
            cardLayout.show(mDisplayContainer, panelName);
        }

        mButtonContainer.add(newButton);
        mButtonMap.put(panelName, newButton);
    }

    public int getButtonWidth() { return mButtonWidth; }
    public int geButtonHeight() { return mButtonHeight; }
    public int getDisplayWidth() { return mDisplayWidth; }
    public int geDisplayHeight() { return mDisplayHeight; }
    public JButton getButton(String name) { return mButtonMap.get(name); }

    @Override
    public void jSceneUpdate(GameModel model) {
        for (Component component : mDisplayContainer.getComponents()) {
            if (component instanceof JScene scene) {
                scene.jSceneUpdate(model);
            }
        }
    }
}
