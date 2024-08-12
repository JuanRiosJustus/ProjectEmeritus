package main.ui.huds.controls;

import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.graphics.JScene;
import main.ui.custom.SwingUiUtils;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ButtonTabbedPane extends JScene {
    private final String HOME_SCREEN = "HOME_SCREEN";
    private final JPanel mButtonContainer = new JPanel();
    private final JPanel mCardLayoutContainer = new JPanel();
    private final Map<String, Component> mButtonToComponent = new HashMap<>();
    private final Map<String, JButton> mButtonMap = new HashMap<>();
    private int mButtonWidth = 0;
    private int mButtonHeight = 0;
    private int mDisplayWidth = 0;
    private int mDisplayHeight = 0;
    private String mCurrentlyShownComponent = null;
    public ButtonTabbedPane(int width, int height, boolean horizontal) {
        super(width, height, "");

        setLayout(new GridBagLayout());

        setPreferredSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));

        GridBagConstraints mConstraints = new GridBagConstraints();
        mConstraints.weightx = 1;
        mConstraints.weighty = 1;
        mConstraints.gridx = 0;
        mConstraints.gridy = 0;

        mDisplayWidth = width;
        mDisplayHeight = height;
        mCardLayoutContainer.setLayout(new CardLayout());
        mCardLayoutContainer.setPreferredSize(new Dimension(mDisplayWidth, mDisplayHeight));
        mCardLayoutContainer.setMaximumSize(new Dimension(mDisplayWidth, mDisplayHeight));
        mCardLayoutContainer.setMinimumSize(new Dimension(mDisplayWidth, mDisplayHeight));
//        mCardLayoutContainer.setOpaque(true);
//        mCardLayoutContainer.setBackground(ColorPalette.getRandomColor());

        if (horizontal) {
            mButtonContainer.setLayout(new BoxLayout(mButtonContainer, BoxLayout.X_AXIS));
        } else {
            mButtonContainer.setLayout(new BoxLayout(mButtonContainer, BoxLayout.Y_AXIS));
        }
        mButtonContainer.setOpaque(true);
        mButtonContainer.setBackground(ColorPalette.GREEN);

        JScrollPane pane;
        if (horizontal) {
            mButtonWidth = width / 5;
            mButtonHeight = height;
        } else {
            mButtonHeight = height / 5;
            mButtonWidth = width;
        }
        pane = SwingUiUtils.createBonelessScrollingPane(width, height, mButtonContainer);

        // Show the button panel
        add(mCardLayoutContainer, mConstraints);
        mCardLayoutContainer.add(pane, HOME_SCREEN);
        CardLayout cardLayout = (CardLayout) mCardLayoutContainer.getLayout();
        cardLayout.show(mCardLayoutContainer, HOME_SCREEN);
        mCurrentlyShownComponent = HOME_SCREEN;

        setBackground(ColorPalette.GREEN);
    }

    public boolean isShowingHomeScreen() {
        return mCurrentlyShownComponent.equalsIgnoreCase(HOME_SCREEN);
    }
    public void setScreenToHome() {
        CardLayout cardLayout = (CardLayout) mCardLayoutContainer.getLayout();
        cardLayout.show(mCardLayoutContainer, HOME_SCREEN);
    }

    public void addPanel(String panelName, JComponent component, JButton enter, JButton back, boolean buttonOnly) {

        mButtonToComponent.put(panelName, component);
        if (buttonOnly) {
            enter = back;
            mButtonToComponent.put(panelName, back);
        }
//        SwingUiUtils.removeAllListeners(enter);
//        SwingUiUtils.removeAllListeners(back);
        enter.setText(panelName);
        enter.setFocusPainted(false);

        enter.setPreferredSize(new Dimension(mButtonWidth, mButtonHeight));
        enter.setMaximumSize(new Dimension(mButtonWidth, mButtonHeight));
        enter.setMinimumSize(new Dimension(mButtonWidth, mButtonHeight));


        if (buttonOnly) {
            mButtonContainer.add(enter);
            mButtonMap.put(panelName, enter);
            SwingUiUtils.automaticallyStyleButton(enter);
            return;
        }


        // setup going back to default controls panel
        back.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) mCardLayoutContainer.getLayout();
            cardLayout.show(mCardLayoutContainer, HOME_SCREEN);
            mCurrentlyShownComponent = HOME_SCREEN;
        });

        mCardLayoutContainer.add(component, panelName);
        mCardLayoutContainer.setBackground(ColorPalette.getRandomColor());

        // Setup default behavior when selecting button
        enter.addActionListener(e -> {
//            pane.getVerticalScrollBar().setValue(pane.getVerticalScrollBar().getMinimum());
            CardLayout cardLayout = (CardLayout) mCardLayoutContainer.getLayout();
            cardLayout.show(mCardLayoutContainer, panelName);
            mCurrentlyShownComponent = panelName;
        });

        // Map of the buttons
        mButtonContainer.add(enter);
        mButtonMap.put(panelName, enter);

        int backButtonHeight = (int) (mDisplayHeight * .2);
        back.setPreferredSize(new Dimension(mButtonWidth, backButtonHeight));
        back.setMaximumSize(new Dimension(mButtonWidth, backButtonHeight));
        back.setMinimumSize(new Dimension(mButtonWidth, backButtonHeight));

        SwingUiUtils.automaticallyStyleButton(enter);
        SwingUiUtils.automaticallyStyleButton(back);
    }

    public int getButtonWidth() { return mButtonWidth; }
    public int geButtonHeight() { return mButtonHeight; }
    public int getDisplayWidth() { return mDisplayWidth; }
    public int geDisplayHeight() { return mDisplayHeight; }
    public JButton getButton(String name) { return mButtonMap.get(name); }

    @Override
    public void jSceneUpdate(GameModel model) {
        for (Component component : mButtonToComponent.values()) {
            if (component instanceof JScene scene) {
                scene.jSceneUpdate(model);
            }
        }
    }
}


//public class ButtonTabbedPane extends JScene {
//    private final JPanel mButtonContainer = new JPanel();
//    private final JPanel mDisplayContainer = new JPanel();
//    private final Map<String, JButton> mButtonMap = new HashMap<>();
//    private int mButtonWidth = 0;
//    private int mButtonHeight = 0;
//    private int mDisplayWidth = 0;
//    private int mDisplayHeight = 0;
//    public ButtonTabbedPane(int width, int height) {
//        this(width, height, true);
//    }
//    public ButtonTabbedPane(int width, int height, boolean horizontal) {
//        super(width, height, "");
//        width = (int) (width * .9f);
//        height = (int) (height * .9f);
//
//        setLayout(new GridBagLayout());
//
//        setPreferredSize(new Dimension(width, height));
//        setMaximumSize(new Dimension(width, height));
//        setMinimumSize(new Dimension(width, height));
//
//        GridBagConstraints mConstraints = new GridBagConstraints();
//        mConstraints.weightx = 1;
//        mConstraints.weighty = 1;
//        mConstraints.gridx = 0;
//        mConstraints.gridy = 0;
//        float contentRatio = .7f;
//
//        mDisplayWidth = width;
//        mDisplayHeight = (int) (height * contentRatio);
//        mDisplayContainer.setLayout(new CardLayout());
//        mDisplayContainer.setPreferredSize(new Dimension(mDisplayWidth, mDisplayHeight));
//        mDisplayContainer.setMaximumSize(new Dimension(mDisplayWidth, mDisplayHeight));
//        mDisplayContainer.setMinimumSize(new Dimension(mDisplayWidth, mDisplayHeight));
//        mDisplayContainer.setOpaque(true);
//        mDisplayContainer.setBackground(ColorPalette.TRANSPARENT);
//        add(mDisplayContainer, mConstraints);
//
//        float selectorRatio = (1 - contentRatio);
//        int containerPanelHeight = (int) ((height * selectorRatio) * .8);
//        int containerPanelWidth = (int) ((width * selectorRatio) * .8);
//        if (horizontal) {
//            mButtonContainer.setLayout(new BoxLayout(mButtonContainer, BoxLayout.X_AXIS));
//        } else {
//            mButtonContainer.setLayout(new BoxLayout(mButtonContainer, BoxLayout.Y_AXIS));
//        }
//        mButtonContainer.setOpaque(true);
//        mButtonContainer.setBackground(ColorPalette.TRANSPARENT);
////        mButtonContainer.setBackground(ColorPalette.GREEN);
//        JScrollPane pane = null;
//        if (horizontal) {
//            mButtonWidth = width / 4;
//            mButtonHeight = containerPanelHeight;
//            pane = SwingUiUtils.createBonelessScrollingPane(width, containerPanelHeight, mButtonContainer);
//        } else {
//            mButtonHeight = height / 4;
//            mButtonWidth = containerPanelWidth;
//            pane = SwingUiUtils.createBonelessScrollingPane(containerPanelWidth, height, mButtonContainer);
//        }
//        pane = SwingUiUtils.createBonelessScrollingPane(width, containerPanelHeight, mButtonContainer);
//
//        mConstraints.gridy = 1;
//        add(pane, mConstraints);
//        setBackground(ColorPalette.GREEN);
//    }
//
//    public void addPanel(String panelName, JComponent component) {
//
//        JButton newButton = new JButton(panelName);
////        newButton.setBackground(ColorPalette.RED);
//        newButton.setForeground(ColorPalette.WHITE);
//        newButton.setFont(FontPool.getInstance().getFont(newButton.getFont().getSize()).deriveFont(Font.BOLD));
////        newButton.setFont(newButton.getFont().deriveFont(Font.BOLD));
////        newButton.setBorderPainted(false);
//        newButton.setFocusPainted(false);
//
//        newButton.setPreferredSize(new Dimension(mButtonWidth, mButtonHeight));
//        newButton.setMaximumSize(new Dimension(mButtonWidth, mButtonHeight));
//        newButton.setMinimumSize(new Dimension(mButtonWidth, mButtonHeight));
//        newButton.addActionListener(e -> {
//            CardLayout cardLayout = (CardLayout) mDisplayContainer.getLayout();
//            cardLayout.show(mDisplayContainer, panelName);
//        });
//
//        // If nothing is set, set something
//        mDisplayContainer.add(component, panelName);
//        if (mDisplayContainer.getComponentCount() == 0) {
//            CardLayout cardLayout = (CardLayout) mDisplayContainer.getLayout();
//            cardLayout.show(mDisplayContainer, panelName);
//        }
//
//        mButtonContainer.add(newButton);
//        mButtonMap.put(panelName, newButton);
//    }
//
//    public int getButtonWidth() { return mButtonWidth; }
//    public int geButtonHeight() { return mButtonHeight; }
//    public int getDisplayWidth() { return mDisplayWidth; }
//    public int geDisplayHeight() { return mDisplayHeight; }
//    public JButton getButton(String name) { return mButtonMap.get(name); }
//
//    @Override
//    public void jSceneUpdate(GameModel model) {
//        for (Component component : mDisplayContainer.getComponents()) {
//            if (component instanceof JScene scene) {
//                scene.jSceneUpdate(model);
//            }
//        }
//    }
//}
