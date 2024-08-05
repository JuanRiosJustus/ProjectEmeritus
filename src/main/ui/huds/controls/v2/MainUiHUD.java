package main.ui.huds.controls.v2;

import main.game.stores.pools.ColorPalette;
import main.constants.GameState;
import main.engine.Engine;
import main.game.components.SecondTimer;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.graphics.JScene;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.ui.custom.JButtonGrid;
import main.ui.huds.GameLogHUD;
import main.ui.panels.Accordion;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.CardLayout;

public class MainUiHUD extends JScene {

    private SummaryHUD summary;
    private ActionHUD action;
    private ViewHUD inspection;
    private MovementHUD movement;
    private SettingsHUD settings;
    private GameLogHUD gameLog;
    private JButton endTurn = new JButton("End the Turn");
    private WinOrLoseConditionHUD condition;
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private Entity lastSelected = null;
    private Entity currentSelected = null;
    private SecondTimer timer = new SecondTimer();

    private final Accordion mAccordion = new Accordion();

    private final JButtonGrid mButtonGrid;
    private final JPanel mContextPanel;

    public MainUiHUD(int width, int height) {
        super(width, height, MainUiHUD.class.getSimpleName());

        setLayout(null);
        setBackground(ColorPalette.TRANSPARENT);
        setOpaque(false);

        mButtonGrid = new JButtonGrid((int) (width * .25), (int) (height * .2));
        mButtonGrid.add(new String[]{ "Actions", "Movement", "Inventory", "View", "Summary", "End of Turn"},
                3, 3);
        mButtonGrid.setPreferredLocation(width - mButtonGrid.getJSceneWidth() - 10,
                height - mButtonGrid.getJSceneHeight() - 10 - Engine.getInstance().getHeaderSize());
        add(mButtonGrid);


        int contextWidth = mButtonGrid.getJSceneWidth();
        int contextHeight = height - 10 - Engine.getInstance().getHeaderSize() - mButtonGrid.getJSceneHeight() - 20;
        int contextX = width - contextWidth - 10;
        int contextY = 10;

        mContextPanel = new JPanel(new CardLayout());
        mContextPanel.setBounds(contextX, contextY, contextWidth , contextHeight);
        mContextPanel.setBackground(ColorPalette.getRandomColor());
        mContextPanel.setVisible(false);
        add(mContextPanel);

        condition = new WinOrLoseConditionHUD(width, height);
        condition.setBounds(0, 0, width, height);
        add(condition);

        summary = new SummaryHUD(contextWidth, contextHeight);
        summary.setPreferredLocation(contextX, contextY);
        linkToButtonAndContextPanelToButton(summary, "Summary");

        action = new ActionHUD(contextWidth, contextHeight);
        action.setPreferredLocation(contextX, contextY);
        linkToButtonAndContextPanelToButton(action, "Actions");

        movement = new MovementHUD(contextWidth, contextHeight);
        movement.setPreferredLocation(contextX, contextY);
        linkToButtonAndContextPanelToButton(movement, "Movement");

        inspection = new ViewHUD(contextWidth, contextHeight);
        inspection.setPreferredLocation(contextX, contextY);
        linkToButtonAndContextPanelToButton(inspection, "View");

        settings = new SettingsHUD(contextWidth, contextHeight);
        settings.setPreferredLocation(contextX, contextY);

        gameLog = new GameLogHUD(contextWidth, mButtonGrid.getJSceneHeight());
//        gameLog.setPreferredLocation(
//                width - mButtonGrid.getWidth() - gameLog.getWidth() - 20,
//                height - gameLog.getHeight() - Engine.getInstance().getHeaderSize() - 10
//        );
        add(gameLog);
    }

    private void linkToButtonAndContextPanelToButton(JScene panel, String buttonAssigned) {
        mContextPanel.add(panel, buttonAssigned);
        mButtonGrid.getButton(buttonAssigned).addActionListener(e -> {
            mContextPanel.setVisible(!panel.isVisible() || !mContextPanel.isVisible());
            CardLayout cl = (CardLayout)(mContextPanel.getLayout());
            cl.show(mContextPanel, buttonAssigned);
        });
    }

    private void reset() {
        action.setVisible(false);
        movement.setVisible(false);
        summary.setVisible(false);
        inspection.setVisible(false);
    }


    private void initialize(GameModel model) {
        if (mButtonGrid.getButton("End of Turn").getActionListeners().length == 0) {
            mButtonGrid.getButton("End of Turn").addActionListener(e ->
                    model.gameState.set(GameState.ACTIONS_END_TURN, true));
        }
    }
    @Override
    public void jSceneUpdate(GameModel model) {
        initialize(model);

        lastSelected = (currentSelected == null ? lastSelected : currentSelected);
        currentSelected = (Entity) model.gameState.getObject(GameState.CURRENTLY_SELECTED);


        if (summary.isShowing()) {
            summary.jSceneUpdate(model, currentSelected);
        } else if (action.isShowing()) {
            action.jSceneUpdate(model, currentSelected);
        } else if (inspection.isShowing()) {
            inspection.jSceneUpdate(model, currentSelected);
        } else if (movement.isShowing()) {
            movement.jSceneUpdate(model, currentSelected);
        }

        if (gameLog.isShowing()) {
            gameLog.jSceneUpdate(model);
        }

        condition.jSceneUpdate(model, currentSelected);


//        // if (currentSelected == null) { return; }
//        boolean somethingOpened = false;
//        if (miniSummaryHUD.isShowing()) {
//            miniSummaryHUD.contentPaneUpdate(model, currentSelected);
//            somethingOpened = true;
//        } else if (miniMovementHUD.isShowing()) {
//            miniMovementHUD.contentPaneUpdate(model, currentSelected);
//            somethingOpened = true;
//        } else if (miniActionHUD.isShowing()) {
//            miniActionHUD.contentPaneUpdate(model, currentSelected);
//            somethingOpened = true;
//        } else if (miniOtherHUD.isShowing()) {
//            miniOtherHUD.contentPaneUpdate(model, currentSelected);
//            somethingOpened = true;
//        }

//        if (timer.elapsed() > 0) {
//            summary.jSceneUpdate(model, currentSelected);
//            movement.jSceneUpdate(model, currentSelected);
//            action.jSceneUpdate(model, currentSelected);
//            miniOtherHUD.jSceneUpdate(model, currentSelected);
//        }

//        miniOtherHUD.contentPaneUpdate(model, currentSelected);

        if (model.gameState.getBoolean(GameState.UI_GO_TO_CONTROL_HOME)) {
            reset();
            model.gameState.set(GameState.UI_GO_TO_CONTROL_HOME, false);
        }

        model.gameState.set(GameState.SUMMARY_HUD_IS_SHOWING, summary.isShowing());
        model.gameState.set(GameState.SHOW_SELECTED_UNIT_MOVEMENT_PATHING, movement.isShowing());
        model.gameState.set(GameState.ACTION_HUD_IS_SHOWING, action.isShowing());
        model.gameState.set(GameState.INSPECTION_HUD_IS_SHOWING, inspection.isShowing());
//        model.gameState.set(GameState.UI_SUMMARY_PANEL_SHOWING, miniSummaryHUD.isShowing());
//        model.gameState.set(GameState.UI_MOVEMENT_PANEL_SHOWING, miniMovementHUD.isShowing());
//        model.gameState.set(GameState.UI_ACTION_PANEL_SHOWING, miniActionHUD.isShowing());
    }
}
