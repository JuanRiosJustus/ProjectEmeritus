package main.ui.huds.controls;

import main.game.main.GameState;
import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.graphics.GameUI;
import main.ui.components.OutlineButton;
import main.ui.custom.SwingUiUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ControllerPanel extends GameUI {
    private final OutlineMapPanel mButtonPanel;
    private final JPanel mComponentContents;
    private final Map<String, JComponent> mAddedComponents;
    private final JPanel mCardLayoutContainer;
    private final String CONTROLLER_SCREEN = "home_Screen";
    private final String CONTENT_SCREEN = "content_Screen";
    private String mCurrentShownPanel = "";
    private int mContentContainerHeight = 0;
    private int mContentContainerWidth = 0;
    private int mContentHeight = 0;
    private int mContentWidth = 0;
    private int mContentPanelBackButtonHeight;
    private final int mVisibleControls = 5;
    private final ArrayList<GameUI> mAddedJscene = new ArrayList<>();

    public ControllerPanel(int width, int height, int x, int y) {
        super(width, height, x, y, ControllerPanel.class.getSimpleName());

        mButtonPanel = new OutlineMapPanel(width, height, mVisibleControls);
        mAddedComponents = new LinkedHashMap<>();

        mContentContainerHeight = height;
        mContentContainerWidth = width;

        mContentWidth = width;
        mContentHeight = (int) (height * .8);

        mContentPanelBackButtonHeight = height - mContentHeight;

        mComponentContents = new JGamePanel();
        mComponentContents.setBackground(ColorPalette.getRandomColor());
        mComponentContents.setLayout(new CardLayout());
        mComponentContents.setPreferredSize(new Dimension(mContentContainerWidth, mContentContainerHeight));
        mComponentContents.setMinimumSize(new Dimension(mContentContainerWidth, mContentContainerHeight));
        mComponentContents.setMaximumSize(new Dimension(mContentContainerWidth, mContentContainerHeight));

        mCardLayoutContainer = new JGamePanel();
        mCardLayoutContainer.setLayout(new CardLayout());
        mCardLayoutContainer.setPreferredSize(new Dimension(width, height));
        mCardLayoutContainer.setMaximumSize(new Dimension(width, height));
        mCardLayoutContainer.setMinimumSize(new Dimension(width, height));

        setLayout(new GridBagLayout());

        GridBagConstraints mConstraints = new GridBagConstraints();
        mConstraints.weightx = 1;
        mConstraints.weighty = 1;
        mConstraints.gridx = 0;
        mConstraints.gridy = 0;

        add(mCardLayoutContainer, mConstraints);
        mCardLayoutContainer.add(mButtonPanel, CONTROLLER_SCREEN);
        mCardLayoutContainer.add(mComponentContents, CONTENT_SCREEN);
        CardLayout cardLayout = (CardLayout) mCardLayoutContainer.getLayout();
        cardLayout.show(mCardLayoutContainer, CONTROLLER_SCREEN);
        mCurrentShownPanel = CONTROLLER_SCREEN;
    }


//    public void addControllerPane(String name, JComponent component) {
//        addControllerPane(name, component, false);
//    }
//
//    public void addControllerPane(String name, JComponent component, boolean overwriteButtonPanel) {
//
//        if (overwriteButtonPanel) {
//            mButtonPanel.createButton(name, component);
//
//            component.setPreferredSize(new Dimension(
//                    mButtonPanel.getComponentWidth(),
//                    mButtonPanel.getComponentHeight()
//            ));
//            component.setFont(FontPool.getInstance().getFontForHeight(mButtonPanel.getComponentHeight()));
//            mAddedComponents.put("___COMPONENT___", component);
//            return;
//        }
//
//        OutlineButton enter = mButtonPanel.createButton(name);
//        OutlineButton exit = new OutlineButton("Back");
//
////        mComponentEntrances.addScrollableArrayItem(name, enter);
//
//        // Add given component as card
//        JPanel content = new JGamePanel();
//        content.setLayout(new GridBagLayout());
//        content.setPreferredSize(new Dimension(mContentWidth, mContentHeight));
//
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        gbc.weightx = 1;
//        gbc.weighty = 1;
//        gbc.gridx = 0;
//        gbc.gridy = content.getComponentCount();
//
//        enter.setFont(FontPool.getInstance().getFontForHeight(mButtonPanel.getComponentHeight()));
//        // Only update jscenes
//        if (component instanceof GameUI gameUI) { mAddedJscene.add(gameUI); }
//        component.setPreferredSize(new Dimension(mContentContainerWidth, mContentHeight));
//        content.add(component, gbc);
//
//        gbc.gridy = content.getComponentCount();
//
//        exit.setPreferredSize(new Dimension(mContentContainerWidth, mContentPanelBackButtonHeight));
//        exit.setFont(FontPool.getInstance().getFontForHeight(mContentPanelBackButtonHeight));
//        content.add(exit, gbc);
//
//        mAddedComponents.put(name + "___ENTER___", enter);
//        mAddedComponents.put(name + "___EXIT___", exit);
//
//        // Setup default behavior when selecting button
//        SwingUiUtils.removeAllActionListeners(enter);
//        enter.addActionListener(e -> {
//            CardLayout cardLayout = (CardLayout) mCardLayoutContainer.getLayout();
//            cardLayout.show(mCardLayoutContainer, CONTENT_SCREEN);
//
//            cardLayout = (CardLayout) mComponentContents.getLayout();
//            cardLayout.show(mComponentContents, name);
//
//            mCurrentShownPanel = name;
//        });
//
//        // Setup default behavior when selecting button
//        SwingUiUtils.removeAllActionListeners(exit);
//        exit.addActionListener(e -> {
//            CardLayout cardLayout = (CardLayout) mCardLayoutContainer.getLayout();
//            cardLayout.show(mCardLayoutContainer, CONTROLLER_SCREEN);
//            mCurrentShownPanel = CONTROLLER_SCREEN;
//        });
//
//        mComponentContents.add(content, name);
//    }

    public int getContentWidth() { return mContentWidth; }
    public int getContentHeight() { return mContentHeight; }
    public boolean isShowingHomeScreen() {
        return mCurrentShownPanel.equalsIgnoreCase(CONTROLLER_SCREEN);
    }

    @Override
    public void setBackground(Color color) {
        if (mButtonPanel == null) { return; }
        SwingUiUtils.recursivelySetBackgroundV2(color, mButtonPanel.getContainer());

        for (Map.Entry<String, JComponent> component : mAddedComponents.entrySet()) {
            JComponent toUpdate = component.getValue();
            toUpdate.setBackground(color);
            SwingUiUtils.setHoverEffect(toUpdate);
        }
    }

    public void setScreenToHome() {
        CardLayout cardLayout = (CardLayout) mCardLayoutContainer.getLayout();
        cardLayout.show(mCardLayoutContainer, CONTROLLER_SCREEN);
    }

    @Override
    public void gameUpdate(GameModel model) {

        // By default, we can show the movement pathing of the current unit
        boolean isVisible = isShowingHomeScreen();
        model.setGameState(GameState.SHOW_SELECTED_UNIT_MOVEMENT_PATHING, isVisible);

        boolean shouldChangeBattleUiToHome = model.getGameState().shouldChangeControllerToHomeScreen();
        if (shouldChangeBattleUiToHome) {
            setScreenToHome();
//            mButtonTabbedPane.setScreenToHome();
//            model.setGameState(GameState.CHANGE_BATTLE_UI_TO_HOME_SCREEN, false);
            model.getGameState().setControllerToHomeScreen(false);
        }

        for (GameUI scene : mAddedJscene) {
            scene.gameUpdate(model);
        }
    }
}
