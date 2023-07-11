package ui.panels;


import constants.Constants;
import constants.GameStateKey;
import game.components.NameTag;
import game.components.Tile;
import game.components.Type;
import game.components.Types;
import game.components.statistics.Summary;
import game.entity.Entity;
import game.main.GameModel;
import game.stats.node.ResourceNode;
import game.stats.node.StatsNode;
import game.stats.node.StatsNode;
import graphics.JScene;
import logging.ELogger;
import logging.ELoggerFactory;
import graphics.temporary.JKeyLabel;
import utils.ComponentUtils;
import utils.MathUtils;

import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class MovementPanel extends ControlPanelInnerTemplate {

    private JKeyLabel nameFieldLabel;
    private JKeyLabel statusFieldLabel;
    private JKeyLabel typeFieldLabel;
    private JKeyLabel healthFieldLabel;
    private JProgressBar healthProgressBar;
    private JKeyLabel energyFieldLabel;
    private JProgressBar energyProgressBar;
    private Entity observing;
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private final Map<String, JKeyLabel> nameToJKeyLabelnMap = new HashMap<>();

    private final JButton undoButton = new JButton("Undo Movement");

    public MovementPanel(int width, int height) {
        super(width, (int) (height * .9), MovementPanel.class.getSimpleName());

        JScrollPane topRightScroller = createTopRightPanel(topRight);
        topRight.add(topRightScroller);

        JScrollPane middleScroller = createMiddlePanel(middleThird);
        middleThird.add(middleScroller);
    }

    protected JScrollPane createMiddlePanel(JComponent reference) {
        JPanel result = new JPanel();
        result.setPreferredSize(new Dimension(
            (int)reference.getPreferredSize().getWidth(), 
            (int)(reference.getPreferredSize().getHeight() * 1)
        ));
        
        int rows = 3;
        int columns = 1;
        result.setLayout(new GridLayout(rows, columns));

        String[][] nameAndToolTips = new String[][]{
            new String[]{ "MOVE", "What the unit is referenced as" },
            new String[]{ "CLIMB", "The elements associated by the unit"},
            new String[]{ "SPEED", "Chance the ability will land successfully" },
        };

        double height = reference.getPreferredSize().getHeight();
        double width = reference.getPreferredSize().getWidth();
        Dimension buttonDimension = new Dimension((int) (width / columns), (int) (height / rows));

        for (String[] nameAndToolTip : nameAndToolTips) {
            JKeyLabel kl = new JKeyLabel(nameAndToolTip[0] + ":", "");
            kl.key.setFont(kl.key.getFont().deriveFont(Font.BOLD));
            kl.setPreferredSize(buttonDimension);
            kl.setToolTipText(nameAndToolTip[1]);
            nameToJKeyLabelnMap.put(nameAndToolTip[0], kl);
            result.add(kl);
        }

        JScrollPane scrollPane = new JScrollPane(
            result,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        scrollPane.getViewport().setPreferredSize(reference.getPreferredSize());
        scrollPane.setPreferredSize(reference.getPreferredSize());

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }

    protected JScrollPane createTopRightPanel(JComponent reference) {

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        nameFieldLabel = ComponentUtils.createFieldLabel("","[Name Field]");
        ComponentUtils.setTransparent(nameFieldLabel);

        typeFieldLabel = ComponentUtils.createFieldLabel("", "[Types Field]");
        ComponentUtils.setTransparent(typeFieldLabel);

        Dimension dimension = reference.getPreferredSize();
        int width = (int) dimension.getWidth();
        int height = (int) dimension.getHeight();
        int rowHeight = height / 4;

        JPanel row0 = new JPanel();
        row0.add(nameFieldLabel);
        row0.add(typeFieldLabel);

        JPanel row1 = new JPanel();//ComponentUtils.createTransparentPanel(new FlowLayout());
        healthFieldLabel = ComponentUtils.createFieldLabel("Health", "");
        ComponentUtils.setTransparent(healthFieldLabel);
        healthFieldLabel.setLabel("100%");
        healthFieldLabel.key.setFont(healthFieldLabel.key.getFont().deriveFont(Font.BOLD));
        row1.add(healthFieldLabel);
        healthProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        healthProgressBar.setValue(0);
        row1.add(healthProgressBar);
        row1.setPreferredSize(new Dimension(width, rowHeight));
        // ComponentUtils.setTransparent(row1);

        JPanel row2 = ComponentUtils.createTransparentPanel(new FlowLayout());
        energyFieldLabel = ComponentUtils.createFieldLabel("Energy", "");
        ComponentUtils.setTransparent(energyFieldLabel);
        energyFieldLabel.setLabel("100%");
        energyFieldLabel.key.setFont(energyFieldLabel.key.getFont().deriveFont(Font.BOLD));
        row2.add(energyFieldLabel);
        energyProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        // ComponentUtils.setTransparent(energyProgressBar);
        energyProgressBar.setValue(0);
        row2.add(energyProgressBar);
        row2.setPreferredSize(new Dimension(width, rowHeight));
        // ComponentUtils.setTransparent(row2);

        JPanel row3 = ComponentUtils.createTransparentPanel(new FlowLayout());
        statusFieldLabel = ComponentUtils.createFieldLabel("", "[Status Field]");
        ComponentUtils.setTransparent(statusFieldLabel);
        row3.add(statusFieldLabel);
        row3.setPreferredSize(new Dimension(width, rowHeight));
        // ComponentUtils.setTransparent(row3);

        content.add(row1);
        content.add(row2);
        content.add(row3);
        content.add(undoButton);
        content.setOpaque(true);
        // content.setBackground(ColorPalette);
        // ComponentUtils.setTransparent(content);

        JScrollPane scrollPane = new JScrollPane(
            content,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        ComponentUtils.setTransparent(scrollPane);
        ComponentUtils.setTransparent(scrollPane.getViewport());
        scrollPane.getViewport().setPreferredSize(reference.getPreferredSize());
        scrollPane.setPreferredSize(reference.getPreferredSize());

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
        // JPanel result = ComponentUtils.createTransparentPanel(new GridBagLayout());
        // ComponentUtils.setTransparent(result);

        // nameFieldLabel = ComponentUtils.createFieldLabel("","[Name Field]");
        // // ComponentUtils.setTransparent(nameFieldLabel);

        // typeFieldLabel = ComponentUtils.createFieldLabel("", "[Types Field]");
        // // ComponentUtils.setTransparent(typeFieldLabel);

        // statusFieldLabel = ComponentUtils.createFieldLabel("", "[Status Field]");
        // // ComponentUtils.setTransparent(statusFieldLabel);

        // Dimension dimension = reference.getPreferredSize();
        // int rowHeights = (int) (dimension.getHeight() / 3);
        // int width = (int) dimension.getWidth();

        // JPanel row0 = ComponentUtils.createTransparentPanel(new FlowLayout());
        // row0.add(nameFieldLabel);
        // row0.add(statusFieldLabel);
        // ComponentUtils.setSize(row0, width, rowHeights);
        // ComponentUtils.setTransparent(row0);

        // JPanel row1 = ComponentUtils.createTransparentPanel(new FlowLayout());
        // healthFieldLabel = ComponentUtils.createFieldLabel("Health", "");
        // row1.add(undoButton);
        // ComponentUtils.setSize(row1, (int) dimension.getWidth(), rowHeights);
        // // ComponentUtils.setTransparent(row1);

        // JPanel row2 = ComponentUtils.createTransparentPanel(new FlowLayout());
        // energyFieldLabel = ComponentUtils.createFieldLabel("Energy", "");
        // ComponentUtils.setTransparent(energyFieldLabel);
        // energyFieldLabel.setLabel("100%");
        // energyFieldLabel.key.setFont(energyFieldLabel.key.getFont().deriveFont(Font.BOLD));
        // row2.add(energyFieldLabel);
        // energyProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        // ComponentUtils.setTransparent(energyProgressBar);
        // energyProgressBar.setValue(0);
        // row2.add(energyProgressBar);
        // ComponentUtils.setSize(row2, width, rowHeights);
        // // ComponentUtils.setTransparent(row2);

        // JPanel row3 = ComponentUtils.createTransparentPanel(new FlowLayout());
        // statusFieldLabel = ComponentUtils.createFieldLabel("", "[Status Field]");
        // ComponentUtils.setTransparent(statusFieldLabel);
        // row3.add(statusFieldLabel);
        // ComponentUtils.setSize(row3, width, rowHeights);
        // // ComponentUtils.setTransparent(row3);

        // GridBagConstraints gbc = new GridBagConstraints();
        // gbc.gridx = 0;
        // gbc.gridy = 0;
        // gbc.weightx = 0;
        // gbc.weighty = 0;

        // result.add(row0, gbc);
        // gbc.gridy = 1;
        // result.add(undoButton, gbc);

        // JScrollPane scrollPane = new JScrollPane(result,
        //         ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
        //         ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        // scrollPane.getViewport().setPreferredSize(reference.getPreferredSize());
        // scrollPane.setPreferredSize(reference.getPreferredSize());

        // scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        // scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        // scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // return scrollPane;
    }

    public void set(GameModel model, Entity tileEntity) {
        if (tileEntity == null || observing == tileEntity) { return; }
        Tile tile = tileEntity.get(Tile.class);
        if (tile == null || tile.unit == null) { return; }
        
        observing = tile.unit;
        Summary summary = observing.get(Summary.class);

        ComponentUtils.removeActionListeners(undoButton);
        undoButton.addActionListener(e -> {
            model.state.set(GameStateKey.UI_UNDO_MOVEMENT_PRESSED, true);
        });

        topLeft.set(observing);
        nameFieldLabel.label.setText(observing.get(Summary.class).getName());
        typeFieldLabel.label.setText(observing.get(Type.class).getTypes().toString());
            
        ResourceNode health = summary.getResourceNode(Constants.HEALTH);
        int percentage = (int) MathUtils.mapToRange(health.percentage(), 0, 1, 0, 100);
        if (healthProgressBar.getValue() != percentage) {
            healthProgressBar.setValue(percentage);
            healthFieldLabel.setLabel(String.valueOf(health.current));
        }

        ResourceNode energy = summary.getResourceNode(Constants.ENERGY);
        percentage = (int) MathUtils.mapToRange(energy.percentage(), 0, 1, 0, 100);
        if (energyProgressBar.getValue() != percentage) {
            energyProgressBar.setValue(percentage);
            energyFieldLabel.setLabel(String.valueOf(energy.current));
        }

        StatsNode node = summary.getStatsNode(Constants.MOVE);
        nameToJKeyLabelnMap.get("MOVE").label.setText(node.getTotal() + "");
        node = summary.getStatsNode(Constants.CLIMB);
        nameToJKeyLabelnMap.get("CLIMB").label.setText(node.getTotal() + "");
        node = summary.getStatsNode(Constants.SPEED);
        nameToJKeyLabelnMap.get("SPEED").label.setText(node.getTotal() + "");
        

        // for (String key : stats.getKeySet()) {
        //     StatsNode stat = stats.getNode(key);
        //     if (key == null || stat == null) { continue; }
        //     String capitalized = handle(key);
        //     StatsNode scalar = (StatsNode) stat;
        //     if (nameToJKeyLabelnMap.get(capitalized) != null) {
        //         nameToJKeyLabelnMap.get(capitalized).key.setText(capitalized + ": ");
        //         nameToJKeyLabelnMap.get(capitalized).label.setText(scalar.getBase() + " ( " + scalar.getMods() + " )");
        //     }
        // }

        // revalidate();
        // repaint();
        logger.info("Polling Movement panel for " + observing);
    }

    private String handle(String key) {
        StringBuilder sb = new StringBuilder();
        boolean finishedFirstCharacter = false;
        for (char c : key.toCharArray()) {
            if (Character.isUpperCase(c) && finishedFirstCharacter) {
                sb.append(' ');
            }
            sb.append(c);
            finishedFirstCharacter = true;
        }
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    private static String show(StatsNode node) {
        int total = node.getTotal();
        int base = node.getBase();
        int mods = node.getMods();

        return MessageFormat.format("{0}=({1}+{2})", total, base, mods);
    }
}
