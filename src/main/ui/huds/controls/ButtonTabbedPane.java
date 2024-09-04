package main.ui.huds.controls;

import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.graphics.GameUI;
import main.ui.custom.SwingUiUtils;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ButtonTabbedPane extends GameUI {
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

        if (horizontal) {
            mButtonContainer.setLayout(new BoxLayout(mButtonContainer, BoxLayout.X_AXIS));
        } else {
            mButtonContainer.setLayout(new BoxLayout(mButtonContainer, BoxLayout.Y_AXIS));
        }
        mButtonContainer.setOpaque(true);
        mButtonContainer.setBackground(ColorPalette.TRANSPARENT);

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

        enter.setText(panelName);
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
            CardLayout cardLayout = (CardLayout) mCardLayoutContainer.getLayout();
            cardLayout.show(mCardLayoutContainer, panelName);
            mCurrentlyShownComponent = panelName;
        });

        // Map of the buttons
        mButtonContainer.add(enter);
        mButtonMap.put(panelName, enter);

        int backButtonHeight = (int) (mDisplayHeight * .1);
        back.setPreferredSize(new Dimension(mButtonWidth, backButtonHeight));
        back.setMaximumSize(new Dimension(mButtonWidth, backButtonHeight));
        back.setMinimumSize(new Dimension(mButtonWidth, backButtonHeight));

        SwingUiUtils.automaticallyStyleButton(enter);
        SwingUiUtils.automaticallyStyleComponent(back, (int) (backButtonHeight * .75));
    }

    @Override
    public void gameUpdate(GameModel model) {
        for (Component component : mButtonToComponent.values()) {
            if (component instanceof GameUI scene) {
                scene.gameUpdate(model);
            }
        }
    }
}
