package main.ui.huds.controls.v2;

import main.constants.ColorPalette;
import main.game.components.SecondTimer;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.graphics.JScene;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.constants.GameState;
import main.ui.custom.UIVerticalArray;
import main.ui.huds.controls.v1.*;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ControllerHUD extends JScene {

    private SummaryHUD summary;
    private ActionHUD action;
    private InspectionHUD inspection;
    private MovementHUD movement;
    private SettingsHUD settings;
    private JButton endTurn = new JButton("End the Turn");
    private UIVerticalArray buttonArray;
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private Entity lastSelected = null;
    private Entity currentSelected = null;
    private SecondTimer timer = new SecondTimer();

    public ControllerHUD(int width, int height) {
        super(width, height, ControlHUD.class.getSimpleName());
        int buttonItemWidth = width / 3;
        int buttonItemHeight = height / 3;
        int yBuffer = 50;
        int xBuffer = 30;
        setLayout(null);

        buttonArray = new UIVerticalArray(width / 5, height / 4);
        buttonArray.setPreferredLocation(
                width - buttonArray.getWidth() - xBuffer,
                height - buttonArray.getHeight() - 50
        );

//        miniActionHUD = GameViewSetup.createActionHUD(buttonItemWidth, buttonItemHeight);
//        miniActionHUD.getEnterButton().addActionListener(e -> {
//            buttonArray.setVisible(false);
//            miniActionHUD.setVisible(true);
//        });
//        miniActionHUD.getExitButton().addActionListener(e -> { miniActionHUD.setVisible(false); buttonArray.setVisible(true); });
//        miniActionHUD.setPreferredLocation(
//                width - miniActionHUD.getWidth() - xBuffer,
//                height - miniActionHUD.getHeight() - yBuffer
//        );
//        add(miniActionHUD);
//
//        miniSummaryHUD = GameViewSetup.createMiniSummaryHUD(buttonItemWidth, buttonItemHeight);
//        miniSummaryHUD.getEnterButton().addActionListener(e -> { buttonArray.setVisible(false); miniSummaryHUD.setVisible(true); });
//        miniSummaryHUD.getExitButton().addActionListener(e -> { miniSummaryHUD.setVisible(false); buttonArray.setVisible(true); });
//        miniSummaryHUD.setPreferredLocation(
//                width - miniSummaryHUD.getWidth() - xBuffer,
//                height - miniSummaryHUD.getHeight() - yBuffer
//        );
//        add(miniSummaryHUD);

        int exp = (int) (height * .94);
        summary = new SummaryHUD(width / 5, exp);
        summary.getEnterButton().setFont(summary.getEnterButton().getFont().deriveFont(30f));
        summary.setVisible(false);
        summary.getEnterButton().addActionListener(e -> { buttonArray.setVisible(false); summary.setVisible(true); });
        summary.getExitButton().addActionListener(e -> { summary.setVisible(false); buttonArray.setVisible(true); });
        summary.setPreferredLocation(width - summary.getWidth() - 10, 10);
        add(summary);

        exp = (int) (height * .94);
        action = new ActionHUD(width / 5, exp);
        action.getEnterButton().setFont(action.getEnterButton().getFont().deriveFont(30f));
        action.setVisible(false);
        action.getEnterButton().addActionListener(e -> { buttonArray.setVisible(false); action.setVisible(true); });
        action.getExitButton().addActionListener(e -> { action.setVisible(false); buttonArray.setVisible(true); });
        action.setPreferredLocation(width - action.getWidth() - 10, 10);
        add(action);

        movement = new MovementHUD(width / 5, exp);
        movement.getEnterButton().setFont(summary.getEnterButton().getFont().deriveFont(30f));
        movement.setVisible(false);
        movement.getEnterButton().addActionListener(e -> { buttonArray.setVisible(false); movement.setVisible(true); });
        movement.getExitButton().addActionListener(e -> { movement.setVisible(false); buttonArray.setVisible(true); });
        movement.setPreferredLocation(width - movement.getWidth() - 10, 10);
        add(movement);

        inspection = new InspectionHUD(width / 5, exp);
        inspection.getEnterButton().setFont(summary.getEnterButton().getFont().deriveFont(30f));
        inspection.setVisible(false);
        inspection.getEnterButton().addActionListener(e -> { buttonArray.setVisible(false); inspection.setVisible(true); });
        inspection.getExitButton().addActionListener(e -> { inspection.setVisible(false); buttonArray.setVisible(true); });
        inspection.setPreferredLocation(width - inspection.getWidth() - 10, 10);
        add(inspection);

        settings = new SettingsHUD(width / 5, exp);
        settings.getEnterButton().setFont(summary.getEnterButton().getFont().deriveFont(30f));
        settings.setVisible(false);
        settings.getEnterButton().addActionListener(e -> { buttonArray.setVisible(false); settings.setVisible(true); });
        settings.getExitButton().addActionListener(e -> { settings.setVisible(false); buttonArray.setVisible(true); });
        settings.setPreferredLocation(width - settings.getWidth() - 10, 10);
        add(settings);

        buttonArray.addUIVerticalButton(action.getEnterButton());
        buttonArray.addUIVerticalButton(movement.getEnterButton());
        buttonArray.addUIVerticalButton(summary.getEnterButton());
        buttonArray.addUIVerticalButton(inspection.getEnterButton());
        buttonArray.addUIVerticalButton(endTurn);


        add(buttonArray);
//        setBackground(ColorPalette.BLUE);
        setBackground(ColorPalette.TRANSPARENT);
        setOpaque(false);
    }

    private void reset() {
        buttonArray.setVisible(true);
        action.setVisible(false);
        movement.setVisible(false);
        summary.setVisible(false);
        inspection.setVisible(false);
    }


    private void initialize(GameModel model) {
        if (endTurn.getActionListeners().length == 0) {
            endTurn.addActionListener(e -> model.gameState.set(GameState.ACTIONS_END_TURN, true));
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
        model.gameState.set(GameState.MOVEMENT_HUD_IS_SHOWING, movement.isShowing());
        model.gameState.set(GameState.ACTION_HUD_IS_SHOWING, action.isShowing());
        model.gameState.set(GameState.INSPECTION_HUD_IS_SHOWING, inspection.isShowing());
//        model.gameState.set(GameState.UI_SUMMARY_PANEL_SHOWING, miniSummaryHUD.isShowing());
//        model.gameState.set(GameState.UI_MOVEMENT_PANEL_SHOWING, miniMovementHUD.isShowing());
//        model.gameState.set(GameState.UI_ACTION_PANEL_SHOWING, miniActionHUD.isShowing());
    }
}
