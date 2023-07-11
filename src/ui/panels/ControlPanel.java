package ui.panels;

import constants.*;
import game.components.Tile;
import game.components.statistics.Summary;
import game.entity.Entity;
import game.main.GameModel;
import graphics.JScene;
import logging.ELogger;
import logging.ELoggerFactory;
import ui.presets.SceneManager;
import utils.ComponentUtils;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ControlPanel extends JScene {

    private MovementPanel movementPanel = null;
    private EndTurnPanel endTurnPanel = null;
    private SummaryPanel summaryPanel = null;
    private ActionPanel actionPanel = null;
    private JPanel outerContentPanel = new JPanel();
    private JPanel buttonPanel = new JPanel();
    private final JPanel innerContainer = new JPanel();
    private final JPanel outerContainer = new JPanel();
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private Entity lastSelected = null;
    private Entity currentSelected = null;

    public ControlPanel(int width, int height) {
        super(width, height, ControlPanel.class.getSimpleName());

        add(createContentPane(width, height, 3));
        setDoubleBuffered(true);
    }

    /**
     * Creates the UI for the main pane for the control panel
     * @param buttonWidth
     * @param buttonHeight
     * @return
     */
    private JPanel createButtonPanel(int buttonWidth, int buttonHeight) {

        int width = buttonWidth;
        int height = buttonHeight;
        
        JPanel buttonPanel = new JPanel();
        // buttonPanel.setBackground(ColorPalette.TRANSPARENT);
        // buttonPanel.setOpaque(true);
        buttonPanel.setLayout(new GridBagLayout());
        ComponentUtils.setMinMaxThenPreferredSize(buttonPanel, width, height);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;

        movementPanel = new MovementPanel(width, height);
        movementPanel.getEnterButton().setFont(movementPanel.getEnterButton().getFont().deriveFont(30f));
        ComponentUtils.setMinMaxThenPreferredSize(movementPanel.getEnterButton(), width / 2, height / 2);
        buttonPanel.add(movementPanel.getEnterButton(), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;

        actionPanel = new ActionPanel(width, height);
        actionPanel.getEnterButton().setFont(actionPanel.getEnterButton().getFont().deriveFont(30f));
        ComponentUtils.setMinMaxThenPreferredSize(actionPanel.getEnterButton(), width / 2, height / 2);
        buttonPanel.add(actionPanel.getEnterButton(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;

        summaryPanel = new SummaryPanel(width, height);
        summaryPanel.getEnterButton().setFont(summaryPanel.getEnterButton().getFont().deriveFont(30f));
        ComponentUtils.setMinMaxThenPreferredSize(summaryPanel.getEnterButton(), width / 2, height / 2);
        buttonPanel.add(summaryPanel.getEnterButton(), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;

        endTurnPanel = new EndTurnPanel(width, height);
        endTurnPanel.getEnterButton().setFont(endTurnPanel.getEnterButton().getFont().deriveFont(30f));

        int conjoinedHeight = (int)((height / 2) * .7);
        ComponentUtils.setMinMaxThenPreferredSize(endTurnPanel.getEnterButton(), width / 2, conjoinedHeight);

        JPanel conjoinedPanel = new JPanel();
        conjoinedPanel.add(endTurnPanel.getEnterButton());
        conjoinedPanel.add(endTurnPanel.endTurnButton);
        ComponentUtils.setMinMaxThenPreferredSize(conjoinedPanel,  width / 2,  height / 2);

        // Adding the "End the Turn" button here so it takes less clicks to end the turn
        buttonPanel.add(conjoinedPanel, gbc);

        return buttonPanel;
    }

    private JPanel createContentPane(int panelWidth, int panelHeight, int shrink) {

        int width = panelWidth / shrink;
        int height = panelHeight / shrink;

        outerContentPanel = new JPanel();
        // outerContentPanel.setBackground(ColorPalette.TRANSPARENT);
        // outerContentPanel.setOpaque(true);
        outerContentPanel.setLayout(new CardLayout());
        outerContentPanel.setName("outerContentPanelPane");
        ComponentUtils.setMinMaxThenPreferredSize(outerContentPanel, width, height);

        buttonPanel = createButtonPanel(width, height);
        buttonPanel.setName("ButtonPanel");

        // Put the scene on bottom right corner
        innerContainer.setBackground(ColorPalette.TRANSPARENT);
        innerContainer.setLayout(new BorderLayout());
        innerContainer.setPreferredSize(new Dimension(panelWidth, height));
        innerContainer.add(outerContentPanel, BorderLayout.LINE_END);
        ComponentUtils.setTransparent(outerContentPanel);
        innerContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        ComponentUtils.setTransparent(outerContainer);
        // outerContainer.setOpaque(true);
        outerContainer.setPreferredSize(new Dimension(panelWidth, panelHeight));
        outerContainer.setLayout(new BorderLayout());
        outerContainer.add(innerContainer, BorderLayout.PAGE_END);
        // ComponentUtils.setTransparent(outerContainer);
        outerContainer.setBorder(new EmptyBorder(10, 10, 10, 10));


        // Create cards for each available option
        JPanel innerContentPanel = new JPanel();
        innerContentPanel.setPreferredSize(new Dimension(width, height));
        innerContentPanel.setLayout(new CardLayout());
        innerContentPanel.setBackground(ColorPalette.TRANSPARENT);
        innerContentPanel.setName("innerContentPanelPane");
        ComponentUtils.setTransparent(innerContentPanel);
        // Install the scenes
        outerContentPanel.add(innerContentPanel, innerContentPanel.getName());
        outerContentPanel.add(buttonPanel, buttonPanel.getName());
        ComponentUtils.setTransparent(outerContentPanel);

        SceneManager.instance().install(this.getName(), buttonPanel);    
        SceneManager.instance().install(movementPanel.getName(), movementPanel);
        SceneManager.instance().install(actionPanel.getName(), actionPanel);
        SceneManager.instance().install(summaryPanel.getName(), summaryPanel);
        SceneManager.instance().install(endTurnPanel.getName(), endTurnPanel);

        innerContentPanel.add(movementPanel, movementPanel.getName());
        movementPanel.getEnterButton().addActionListener(e -> {
            CardLayout cl = (CardLayout)(innerContentPanel.getLayout());
            cl.show(innerContentPanel, movementPanel.getName());
            CardLayout cl2 = (CardLayout)(outerContentPanel.getLayout());
            cl2.show(outerContentPanel, innerContentPanel.getName());
            logger.info("Entering {} attached to {}", movementPanel.getName(), innerContentPanel.getName());
        });
        movementPanel.getExitButton().addActionListener(e -> {
            CardLayout cl2 = (CardLayout)(outerContentPanel.getLayout());
            cl2.show(outerContentPanel, buttonPanel.getName());
            logger.info("Exiting {} attached to {}", buttonPanel.getName(), outerContentPanel.getName());
        });

        innerContentPanel.add(actionPanel, actionPanel.getName());
        actionPanel.getEnterButton().addActionListener(e -> {
            CardLayout cl = (CardLayout)(innerContentPanel.getLayout());
            cl.show(innerContentPanel, actionPanel.getName());
            CardLayout cl2 = (CardLayout)(outerContentPanel.getLayout());
            cl2.show(outerContentPanel, innerContentPanel.getName());
            logger.info("Entering {} attached to {}", actionPanel.getName(), innerContentPanel.getName());
        });
        actionPanel.getExitButton().addActionListener(e -> {
            CardLayout cl2 = (CardLayout)(outerContentPanel.getLayout());
            cl2.show(outerContentPanel, buttonPanel.getName());
            logger.info("Exiting {} attached to {}", buttonPanel.getName(), outerContentPanel.getName());
        });

        innerContentPanel.add(summaryPanel, summaryPanel.getName());
        summaryPanel.getEnterButton().addActionListener(e -> {
            CardLayout cl = (CardLayout)(innerContentPanel.getLayout());
            cl.show(innerContentPanel, summaryPanel.getName());
            CardLayout cl2 = (CardLayout)(outerContentPanel.getLayout());
            cl2.show(outerContentPanel, innerContentPanel.getName());
            logger.info("Entering {} attached to {}", summaryPanel.getName(), innerContentPanel.getName());
        });    
        summaryPanel.getExitButton().addActionListener(e -> {
            CardLayout cl2 = (CardLayout)(outerContentPanel.getLayout());
            cl2.show(outerContentPanel, buttonPanel.getName());
            logger.info("Exiting {} attached to {}", buttonPanel.getName(), outerContentPanel.getName());
        });

        innerContentPanel.add(endTurnPanel, endTurnPanel.getName());
        endTurnPanel.getEnterButton().addActionListener(e -> {
            CardLayout cl = (CardLayout)(innerContentPanel.getLayout());
            cl.show(innerContentPanel, endTurnPanel.getName());
            CardLayout cl2 = (CardLayout)(outerContentPanel.getLayout());
            cl2.show(outerContentPanel, innerContentPanel.getName());
            logger.info("Entering {} attached to {}", endTurnPanel.getName(), innerContentPanel.getName());
        });   
        endTurnPanel.getExitButton().addActionListener(e -> {
            CardLayout cl2 = (CardLayout)(outerContentPanel.getLayout());
            cl2.show(outerContentPanel, buttonPanel.getName());
            logger.info("Exiting {} attached to {}", buttonPanel.getName(), outerContentPanel.getName());
        });
        CardLayout cl2 = (CardLayout)(outerContentPanel.getLayout());
        cl2.show(outerContentPanel, buttonPanel.getName());

        return outerContainer;
    }

    public void reset() {
        CardLayout cl2 = (CardLayout)(outerContentPanel.getLayout());
        cl2.show(outerContentPanel, buttonPanel.getName());
    }

    public void update(GameModel model) {

        // Entity unit = model.speedQueue.peek();

        lastSelected = (currentSelected == null ? lastSelected : currentSelected);
        currentSelected = (Entity) model.state.getObject(GameStateKey.CURRENTLY_SELECTED);

        // if (currentSelected == null) { return; }


        // Tile tile = currentSelected.get(Tile.class);
        // if (tile == null || tile.unit == null) { return; }
        boolean isDirty = false;//tile.unit.get(Summary.class).isDirty();

        if (isDirty) {
            summaryPanel.set(model, currentSelected);
            movementPanel.set(model, currentSelected);
            actionPanel.set(model, currentSelected);
            endTurnPanel.set(model, currentSelected);
        } else {
            if (summaryPanel.isShowing()) {
                summaryPanel.set(model, currentSelected);
            } else if (movementPanel.isShowing()) {
                movementPanel.set(model, currentSelected);
            } else if (actionPanel.isShowing()) {
                actionPanel.set(model, currentSelected);
            } else if (endTurnPanel.isShowing()) {
                endTurnPanel.set(model, currentSelected);
            }    
            endTurnPanel.set(model, currentSelected);
        }

        if (model.state.getBoolean(GameStateKey.UI_GO_TO_CONTROL_HOME)) {
            reset();
            model.state.set(GameStateKey.UI_GO_TO_CONTROL_HOME, false);
        }

        model.state.set(GameStateKey.UI_SUMMARY_PANEL_SHOWING, summaryPanel.isShowing());
        model.state.set(GameStateKey.UI_MOVEMENT_PANEL_SHOWING, movementPanel.isShowing());
        model.state.set(GameStateKey.UI_ACTION_PANEL_SHOWING, actionPanel.isShowing());
        model.state.set(GameStateKey.UI_END_TURN_PANEL_SHOWING, endTurnPanel.isShowing());
        
        // if (summaryPanel.isShowing()) {
        //     System.out.println("Summary panel is showing");
        // } else if (movementPanel.isShowing()) {
        //     System.out.println("Movement panel is showing");
        // } else if (actionPanel.isShowing()) {
        //     System.out.println("Action panel is showing");
        // }
    }
}
