package main.ui.huds.controls.v2;

import main.constants.ColorPalette;
import main.constants.GameState;
import main.engine.Engine;
import main.game.components.SecondTimer;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.graphics.JScene;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.ui.custom.JButtonGrid;
import main.ui.panels.Accordion;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.CardLayout;

public class MainUiHUD extends JScene {

    private SummaryHUDV2 summary;
    private ActionHUD action;
    private InspectionHUD inspection;
    private MovementHUD movement;
    private SettingsHUD settings;
    private JButton endTurn = new JButton("End the Turn");
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private Entity lastSelected = null;
    private Entity currentSelected = null;
    private SecondTimer timer = new SecondTimer();

    private final Accordion mAccordion = new Accordion();

    private JButtonGrid mButtonGrid;
    private JPanel mContextPanel;

    public MainUiHUD(int width, int height) {
        super(width, height, MainUiHUD.class.getSimpleName());

        setLayout(null);
        setBackground(ColorPalette.TRANSPARENT);
        setOpaque(false);

        mButtonGrid = new JButtonGrid((int) (width * .2), (int) (height * .2), 3, 2);
        mButtonGrid.add(new String[]{ "Actions", "Movement", "Inventory", "View", "Summary", "End of Turn"});
        mButtonGrid.setPreferredLocation(width - mButtonGrid.getWidth() - 10,
                height - mButtonGrid.getHeight() - 10 - Engine.getInstance().getHeaderSize());
        add(mButtonGrid);


        int contextWidth = mButtonGrid.getWidth();
        int contextHeight = height - 10 - Engine.getInstance().getHeaderSize() - mButtonGrid.getHeight() - 20;
        int contextX = width - contextWidth - 10;
        int contextY = 10;

        mContextPanel = new JPanel(new CardLayout());
        mContextPanel.setBounds(contextX, contextY, contextWidth , contextHeight);
        mContextPanel.setBackground(ColorPalette.getRandomColor());
        add(mContextPanel);


        summary = new SummaryHUDV2(contextWidth, contextHeight);
        summary.setPreferredLocation(contextX, contextY);
        linkToButtonAndContextPanelToButton(summary, "Summary");

        action = new ActionHUD(contextWidth, contextHeight);
        action.setPreferredLocation(contextX, contextY);
        linkToButtonAndContextPanelToButton(action, "Actions");

//        contextPanel.add(action, "Actions");
//        mButtonGrid.getButton("Actions").addActionListener(e -> { action.setVisible(!action.isShowing()); });

        movement = new MovementHUD(contextWidth, contextHeight);
        movement.setPreferredLocation(contextX, contextY);
        linkToButtonAndContextPanelToButton(movement, "Movement");

        inspection = new InspectionHUD(contextWidth, contextHeight);
        inspection.setPreferredLocation(contextX, contextY);
        linkToButtonAndContextPanelToButton(inspection, "View");

        settings = new SettingsHUD(contextWidth, contextHeight);
        settings.setPreferredLocation(contextX, contextY);













//        int buttonItemWidth = width / 3;
//        int buttonItemHeight = height / 3;
//        int yBuffer = 50;
//        int xBuffer = 30;
//        setLayout(null);
//
//        int exp = (int) (height * .94);
//        summary = new SummaryHUD(width / 5, exp);
//
//        summary.getEnterButton().setFont(summary.getEnterButton().getFont().deriveFont(30f));
//        summary.setVisible(false);
//        summary.getEnterButton().addActionListener(e -> { buttonArray.setVisible(false); summary.setVisible(true); });
//        summary.getExitButton().addActionListener(e -> { summary.setVisible(false); buttonArray.setVisible(true); });
//        summary.setPreferredLocation(width - summary.getWidth() - 10, 10);
//        add(summary);
//
//        exp = (int) (height * .94);
//        action = new ActionHUD(width / 5, exp);
//        action.getEnterButton().setFont(action.getEnterButton().getFont().deriveFont(30f));
//        action.setVisible(false);
//        action.getEnterButton().addActionListener(e -> { buttonArray.setVisible(false); action.setVisible(true); });
//        action.getExitButton().addActionListener(e -> { action.setVisible(false); buttonArray.setVisible(true); });
//        action.setPreferredLocation(width - action.getWidth() - 10, 10);
//        add(action);
//
//        movement = new MovementHUD(width / 5, exp);
//        movement.getEnterButton().setFont(summary.getEnterButton().getFont().deriveFont(30f));
//        movement.setVisible(false);
//        movement.getEnterButton().addActionListener(e -> { buttonArray.setVisible(false); movement.setVisible(true); });
//        movement.getExitButton().addActionListener(e -> { movement.setVisible(false); buttonArray.setVisible(true); });
//        movement.setPreferredLocation(width - movement.getWidth() - 10, 10);
//        add(movement);
//
//        inspection = new InspectionHUD(width / 5, exp);
//        inspection.getEnterButton().setFont(summary.getEnterButton().getFont().deriveFont(30f));
//        inspection.setVisible(false);
//        inspection.getEnterButton().addActionListener(e -> { buttonArray.setVisible(false); inspection.setVisible(true); });
//        inspection.getExitButton().addActionListener(e -> { inspection.setVisible(false); buttonArray.setVisible(true); });
//        inspection.setPreferredLocation(width - inspection.getWidth() - 10, 10);
//        add(inspection);
//
//        settings = new SettingsHUD(width / 5, exp);
//        settings.getEnterButton().setFont(summary.getEnterButton().getFont().deriveFont(30f));
//        settings.setVisible(false);
//        settings.getEnterButton().addActionListener(e -> { buttonArray.setVisible(false); settings.setVisible(true); });
//        settings.getExitButton().addActionListener(e -> { settings.setVisible(false); buttonArray.setVisible(true); });
//        settings.setPreferredLocation(width - settings.getWidth() - 10, 10);
//        add(settings);
//
//        buttonArray.addUIVerticalButton(action.getEnterButton());
//        buttonArray.addUIVerticalButton(movement.getEnterButton());
//        buttonArray.addUIVerticalButton(summary.getEnterButton());
//        buttonArray.addUIVerticalButton(inspection.getEnterButton());
//        buttonArray.addUIVerticalButton(endTurn);
//
//
//        add(buttonArray);
////        setBackground(ColorPalette.BLUE);
//        setBackground(ColorPalette.TRANSPARENT);
//        setOpaque(false);
    }

    private void linkToButtonAndContextPanelToButton(JScene panel, String buttonAssigned) {
        mContextPanel.add(panel, buttonAssigned);
        mButtonGrid.getButton(buttonAssigned).addActionListener(e -> {
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
