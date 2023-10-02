//package main.ui.huds.controls.v1;
//
//import main.constants.*;
//import main.game.components.SecondTimer;
//import main.game.entity.Entity;
//import main.game.main.GameModel;
//import main.graphics.JScene;
//import main.logging.ELogger;
//import main.logging.ELoggerFactory;
//import main.constants.GameState;
//import main.utils.ComponentUtils;
//
//import javax.swing.JPanel;
//import java.awt.*;
//
//public class ControlHUD extends JScene {
//
//    private MiniMovementHUD movementPanel = null;
//    private MiniOtherHUD endTurnPanel = null;
//    private MiniSummaryHUD miniSummaryHUD = null;
//    private MiniActionHUD miniActionHUD = null;
//    private JPanel outerContentPanel = new JPanel();
//    private JPanel buttonPanel = new JPanel();
//    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
//    private Entity lastSelected = null;
//    private Entity currentSelected = null;
//    private SecondTimer timer = new SecondTimer();
//
//    public ControlHUD(int width, int height) {
//        super(width, height, ControlHUD.class.getSimpleName());
//
//        buttonPanel = createButtonPanel(width, height);
//        JPanel content = createContentPane(width, height);
//
//        add(content);
//        setDoubleBuffered(true);
//    }
//
////    public ControlPanel(int width, int height) {
////        super(width, height, ControlPanel.class.getSimpleName());
////
////        buttonPanel = createButtonPanel(width, height);
////        JPanel content = createContentPane(width, height);
////
////        add(content);
////        setDoubleBuffered(true);
////    }
//
////    private JPanel createButtonPanel(int buttonWidth, int buttonHeight) {
////
////        int width = buttonWidth;
////        int height = buttonHeight;
////
////        JPanel panel = new JPanel();
////        panel.setName("buttonPanel");
////        // buttonPanel.setBackground(ColorPalette.TRANSPARENT);
////        // buttonPanel.setOpaque(true);
////        panel.setLayout(new GridBagLayout());
////        ComponentUtils.setMinMaxThenPreferredSize(panel, width, height);
////        GridBagConstraints gbc = new GridBagConstraints();
////
////        gbc.weightx = 1;
////        gbc.weighty = 1;
////        gbc.gridx = 0;
////        gbc.gridy = 0;
////
////        movementPanel = new MovementPanel(width, height);
////        movementPanel.getEnterButton().setFont(movementPanel.getEnterButton().getFont().deriveFont(30f));
////        ComponentUtils.setMinMaxThenPreferredSize(movementPanel.getEnterButton(), width / 1, height / 2);
////        panel.add(movementPanel.getEnterButton(), gbc);
////
////        gbc.gridy = 1;
////
////        actionPanel = new ActionPanel(width, height);
////        actionPanel.getEnterButton().setFont(actionPanel.getEnterButton().getFont().deriveFont(30f));
////        ComponentUtils.setMinMaxThenPreferredSize(actionPanel.getEnterButton(), width / 1, height / 2);
////        panel.add(actionPanel.getEnterButton(), gbc);
////
////        gbc.gridy = 2;
////
////        summaryPanel = new SummaryPanel(width, height);
////        summaryPanel.getEnterButton().setFont(summaryPanel.getEnterButton().getFont().deriveFont(30f));
////        ComponentUtils.setMinMaxThenPreferredSize(summaryPanel.getEnterButton(), width / 1, height / 2);
////        panel.add(summaryPanel.getEnterButton(), gbc);
////
////        gbc.gridy = 3;
////
////        endTurnPanel = new OtherPanel(width, height);
////        endTurnPanel.getEnterButton().setFont(endTurnPanel.getEnterButton().getFont().deriveFont(30f));
////        ComponentUtils.setMinMaxThenPreferredSize(endTurnPanel.getEnterButton(), width / 1, height / 2);
////        panel.add(endTurnPanel.getEnterButton(), gbc);
////
////        panel.add(endTurnPanel.endTurnButton, gbc);
////
//////        int conjoinedHeight = (int)((height / 2) * .7);
//////        ComponentUtils.setMinMaxThenPreferredSize(endTurnPanel.getEnterButton(), width / 1, conjoinedHeight);
//////
//////        JPanel conjoinedPanel = new JPanel();
//////        conjoinedPanel.add(endTurnPanel.getEnterButton());
//////        // TODO?
//////        conjoinedPanel.add(endTurnPanel.endTurnButton);
//////        ComponentUtils.setMinMaxThenPreferredSize(conjoinedPanel,  width / 2,  height / 2);
////
////        // Adding the "End the Turn" button here so it takes less clicks to end the turn
//////        panel.add(conjoinedPanel, gbc);
////
////        return panel;
////    }
//
//
//    private JPanel createButtonPanel(int buttonWidth, int buttonHeight) {
//
//        int width = buttonWidth;
//        int height = buttonHeight;
//
//        JPanel panel = new JPanel();
//        panel.setName("buttonPanel");
//        // buttonPanel.setBackground(ColorPalette.TRANSPARENT);
//        // buttonPanel.setOpaque(true);
//        panel.setLayout(new GridBagLayout());
//        ComponentUtils.setMinMaxThenPreferredSize(panel, width, height);
//        GridBagConstraints gbc = new GridBagConstraints();
//
//        gbc.weightx = 1;
//        gbc.weighty = 1;
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//
//        movementPanel = new MiniMovementHUD(width, height);
//        movementPanel.getEnterButton().setFont(movementPanel.getEnterButton().getFont().deriveFont(30f));
//        ComponentUtils.setMinMaxThenPreferredSize(movementPanel.getEnterButton(), width / 2, height / 2);
//        panel.add(movementPanel.getEnterButton(), gbc);
//
//        gbc.gridx = 1;
//        gbc.gridy = 0;
//
//        miniActionHUD = new MiniActionHUD(width, height);
//        miniActionHUD.getEnterButton().setFont(miniActionHUD.getEnterButton().getFont().deriveFont(30f));
//        ComponentUtils.setMinMaxThenPreferredSize(miniActionHUD.getEnterButton(), width / 2, height / 2);
//        panel.add(miniActionHUD.getEnterButton(), gbc);
//
//        gbc.gridx = 0;
//        gbc.gridy = 1;
//
//        miniSummaryHUD = new MiniSummaryHUD(width, height);
//        miniSummaryHUD.getEnterButton().setFont(miniSummaryHUD.getEnterButton().getFont().deriveFont(30f));
//        ComponentUtils.setMinMaxThenPreferredSize(miniSummaryHUD.getEnterButton(), width / 2, height / 2);
//        panel.add(miniSummaryHUD.getEnterButton(), gbc);
//
//        gbc.gridx = 1;
//        gbc.gridy = 1;
//
//        endTurnPanel = new MiniOtherHUD(width, height);
//        endTurnPanel.getEnterButton().setFont(endTurnPanel.getEnterButton().getFont().deriveFont(30f));
//
//        int conjoinedHeight = (int)((height / 2) * .7);
//        ComponentUtils.setMinMaxThenPreferredSize(endTurnPanel.getEnterButton(), width / 2, conjoinedHeight);
//
//        JPanel conjoinedPanel = new JPanel();
//        conjoinedPanel.add(endTurnPanel.getEnterButton());
//        // TODO?
//        conjoinedPanel.add(endTurnPanel.endTurnButton);
//        ComponentUtils.setMinMaxThenPreferredSize(conjoinedPanel,  width / 2,  height / 2);
//
//        // Adding the "End the Turn" button here so it takes less clicks to end the turn
//        panel.add(conjoinedPanel, gbc);
//
//        return panel;
//    }
//
//    private JPanel createContentPane(int panelWidth, int panelHeight) {
//
//        outerContentPanel = new JPanel();
//        outerContentPanel.setBackground(ColorPalette.TRANSPARENT);
//        outerContentPanel.setOpaque(true);
//        outerContentPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
//        outerContentPanel.setLayout(new CardLayout());
//        outerContentPanel.setName("outerContentPanelPane");
//
//        // Create cards for each available option
//        JPanel innerContentPanel = new JPanel();
//        innerContentPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
//        innerContentPanel.setLayout(new CardLayout());
//        innerContentPanel.setBackground(ColorPalette.TRANSPARENT);
//        innerContentPanel.setName("innerContentPanelPane");
//        // ComponentUtils.setTransparent(innerContentPanel);
//        // Install the scenes
//        outerContentPanel.add(innerContentPanel, innerContentPanel.getName());
//        outerContentPanel.add(buttonPanel, buttonPanel.getName());
//        ComponentUtils.setTransparent(outerContentPanel);
//
//        int prefWidth = getPreferredSize().width;
//        int prefHeight = getPreferredSize().height;
//
//        innerContentPanel.add(movementPanel, movementPanel.getName());
//        movementPanel.getEnterButton().addActionListener(e -> {
//            CardLayout cl = (CardLayout)(innerContentPanel.getLayout());
//            cl.show(innerContentPanel, movementPanel.getName());
//            CardLayout cl2 = (CardLayout)(outerContentPanel.getLayout());
//            cl2.show(outerContentPanel, innerContentPanel.getName());
//            logger.info("Entering {} attached to {}", movementPanel.getName(), innerContentPanel.getName());
//        });
//        movementPanel.getExitButton().addActionListener(e -> {
//            CardLayout cl2 = (CardLayout)(outerContentPanel.getLayout());
//            cl2.show(outerContentPanel, buttonPanel.getName());
//            logger.info("Exiting {} attached to {}", buttonPanel.getName(), outerContentPanel.getName());
//        });
//
//        innerContentPanel.add(miniActionHUD, miniActionHUD.getName());
//        miniActionHUD.getEnterButton().addActionListener(e -> {
//            CardLayout cl = (CardLayout)(innerContentPanel.getLayout());
//            cl.show(innerContentPanel, miniActionHUD.getName());
//            CardLayout cl2 = (CardLayout)(outerContentPanel.getLayout());
//            cl2.show(outerContentPanel, innerContentPanel.getName());
//            logger.info("Entering {} attached to {}", miniActionHUD.getName(), innerContentPanel.getName());
//        });
//        miniActionHUD.getExitButton().addActionListener(e -> {
//            CardLayout cl2 = (CardLayout)(outerContentPanel.getLayout());
//            cl2.show(outerContentPanel, buttonPanel.getName());
//            logger.info("Exiting {} attached to {}", buttonPanel.getName(), outerContentPanel.getName());
//        });
//
//        innerContentPanel.add(miniSummaryHUD, miniSummaryHUD.getName());
//        miniSummaryHUD.getEnterButton().addActionListener(e -> {
//            CardLayout cl = (CardLayout)(innerContentPanel.getLayout());
//            cl.show(innerContentPanel, miniSummaryHUD.getName());
//            CardLayout cl2 = (CardLayout)(outerContentPanel.getLayout());
//            cl2.show(outerContentPanel, innerContentPanel.getName());
//            logger.info("Entering {} attached to {}", miniSummaryHUD.getName(), innerContentPanel.getName());
////            setPreferredSize(new Dimension(panelWidth * 2, panelHeight * 2));
//        });
//        miniSummaryHUD.getExitButton().addActionListener(e -> {
//            CardLayout cl2 = (CardLayout)(outerContentPanel.getLayout());
//            cl2.show(outerContentPanel, buttonPanel.getName());
//            logger.info("Exiting {} attached to {}", buttonPanel.getName(), outerContentPanel.getName());
//        });
//
//        innerContentPanel.add(endTurnPanel, endTurnPanel.getName());
//        endTurnPanel.getEnterButton().addActionListener(e -> {
//            CardLayout cl = (CardLayout)(innerContentPanel.getLayout());
//            cl.show(innerContentPanel, endTurnPanel.getName());
//            CardLayout cl2 = (CardLayout)(outerContentPanel.getLayout());
//            cl2.show(outerContentPanel, innerContentPanel.getName());
//            logger.info("Entering {} attached to {}", endTurnPanel.getName(), innerContentPanel.getName());
//        });
//        endTurnPanel.getExitButton().addActionListener(e -> {
//            CardLayout cl2 = (CardLayout)(outerContentPanel.getLayout());
//            cl2.show(outerContentPanel, buttonPanel.getName());
//            logger.info("Exiting {} attached to {}", buttonPanel.getName(), outerContentPanel.getName());
//        });
//        CardLayout cl2 = (CardLayout)(outerContentPanel.getLayout());
//        cl2.show(outerContentPanel, buttonPanel.getName());
//
//        return outerContentPanel;
//    }
//
//    public void reset() {
//        CardLayout cl2 = (CardLayout)(outerContentPanel.getLayout());
//        cl2.show(outerContentPanel, buttonPanel.getName());
//    }
//
//    @Override
//    public void jSceneUpdate(GameModel model) {
//
//        lastSelected = (currentSelected == null ? lastSelected : currentSelected);
//        currentSelected = (Entity) model.gameState.getObject(GameState.CURRENTLY_SELECTED);
//
//        // if (currentSelected == null) { return; }
//        boolean somethingOpened = false;
//        if (miniSummaryHUD.isShowing()) {
//            miniSummaryHUD.contentPaneUpdate(model, currentSelected);
//            somethingOpened = true;
//        } else if (movementPanel.isShowing()) {
//            movementPanel.contentPaneUpdate(model, currentSelected);
//            somethingOpened = true;
//        } else if (miniActionHUD.isShowing()) {
//            miniActionHUD.contentPaneUpdate(model, currentSelected);
//            somethingOpened = true;
//        } else if (endTurnPanel.isShowing()) {
//            endTurnPanel.contentPaneUpdate(model, currentSelected);
//            somethingOpened = true;
//        }
//
//        if (timer.elapsed() > 0) {
//            miniSummaryHUD.contentPaneUpdate(model, currentSelected);
//            movementPanel.contentPaneUpdate(model, currentSelected);
//            miniActionHUD.contentPaneUpdate(model, currentSelected);
//            endTurnPanel.contentPaneUpdate(model, currentSelected);
//        }
//
//        endTurnPanel.contentPaneUpdate(model, currentSelected);
//
//        if (model.gameState.getBoolean(GameState.UI_GO_TO_CONTROL_HOME)) {
//            reset();
//            model.gameState.set(GameState.UI_GO_TO_CONTROL_HOME, false);
//        }
//
//        model.gameState.set(GameState.SUMMARY_HUD_IS_SHOWING, miniSummaryHUD.isShowing());
//        model.gameState.set(GameState.MOVEMENT_HUD_IS_SHOWING, movementPanel.isShowing());
//        model.gameState.set(GameState.ACTION_HUD_IS_SHOWING, miniActionHUD.isShowing());
//        model.gameState.set(GameState.UI_END_TURN_PANEL_SHOWING, endTurnPanel.isShowing());
//    }
//}
