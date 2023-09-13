package main.ui.huds.controls.v1;


import main.constants.Constants;
import main.constants.GameState;
import main.game.components.Identity;
import main.game.components.Statistics;
import main.game.components.Types;
import main.game.main.GameModel;
import main.game.stats.Resource;
import main.game.stats.Stat;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.graphics.temporary.JKeyLabelOld;
import main.ui.panels.ControlPanelPane;
import main.utils.ComponentUtils;
import main.utils.MathUtils;

import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class MiniMovementHUD extends ControlPanelPane {

    private JKeyLabelOld nameFieldLabel;
    private JKeyLabelOld statusFieldLabel;
    private JKeyLabelOld typeFieldLabel;
    private JKeyLabelOld healthFieldLabel;
    private JProgressBar healthProgressBar;
    private JKeyLabelOld energyFieldLabel;
    private JProgressBar energyProgressBar;
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private final Map<String, JKeyLabelOld> nameToJKeyLabelnMap = new HashMap<>();

    private final JButton undoButton = new JButton("Undo Movement");

    public MiniMovementHUD(int width, int height) {
        super(width, height, "Mini Movement");

        JScrollPane topRightScroller = createTopRightPanel(topRight);
        topRight.add(topRightScroller);

        JScrollPane middleScroller = createMiddlePanel(middle);
        middle.add(middleScroller);
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
            JKeyLabelOld kl = new JKeyLabelOld(nameAndToolTip[0] + ":", "");
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

        nameFieldLabel = new JKeyLabelOld("Name: ", "");//ComponentUtils.createFieldLabel("","[Name Field]");
        ComponentUtils.setTransparent(nameFieldLabel);

        typeFieldLabel = new JKeyLabelOld("Type: ", "");//ComponentUtils.createFieldLabel("", "[Types Field]");
        ComponentUtils.setTransparent(typeFieldLabel);

        Dimension dimension = reference.getPreferredSize();
        int width = (int) dimension.getWidth();
        int height = (int) dimension.getHeight();
        int rowHeight = (int) (height * .2);

        JPanel row0 = new JPanel();
        row0.add(nameFieldLabel);
        row0.add(typeFieldLabel);

        JPanel row1 = new JPanel();//ComponentUtils.createTransparentPanel(new FlowLayout());
        healthFieldLabel = new JKeyLabelOld(": ", ""); //ComponentUtils.createFieldLabel("Health", "");
        ComponentUtils.setTransparent(healthFieldLabel);
        healthFieldLabel.setValue("100%");
        healthFieldLabel.key.setFont(healthFieldLabel.key.getFont().deriveFont(Font.BOLD));
        row1.add(healthFieldLabel);
        healthProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        healthProgressBar.setValue(0);
        row1.add(healthProgressBar);
        row1.setPreferredSize(new Dimension(width, rowHeight));
        // ComponentUtils.setTransparent(row1);

        JPanel row2 = ComponentUtils.createTransparentPanel(new FlowLayout());
        energyFieldLabel = new JKeyLabelOld("Energy: ", "");// ComponentUtils.createFieldLabel("Energy", "");
        ComponentUtils.setTransparent(energyFieldLabel);
        energyFieldLabel.setValue("100%");
        energyFieldLabel.key.setFont(energyFieldLabel.key.getFont().deriveFont(Font.BOLD));
        row2.add(energyFieldLabel);
        energyProgressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
        // ComponentUtils.setTransparent(energyProgressBar);
        energyProgressBar.setValue(0);
        row2.add(energyProgressBar);
        row2.setPreferredSize(new Dimension(width, rowHeight));
        // ComponentUtils.setTransparent(row2);

        JPanel row3 = ComponentUtils.createTransparentPanel(new FlowLayout());
        statusFieldLabel = new JKeyLabelOld("Status: ", "");
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
    }

    @Override
    public void jSceneUpdate(GameModel model) {
        if (currentUnit == null) { return; }
        Statistics statistics = currentUnit.get(Statistics.class);

        ComponentUtils.removeActionListeners(undoButton);
        undoButton.addActionListener(e -> {
            model.gameState.set(GameState.UNDO_MOVEMENT_BUTTON_PRESSED, true);

        });

        topLeft.set(currentUnit);
        nameFieldLabel.value.setText(currentUnit.get(Identity.class).toString());
        typeFieldLabel.value.setText(currentUnit.get(Types.class).getTypes().toString());

        Resource health = statistics.getResourceNode(Constants.HEALTH);
        int percentage = (int) MathUtils.map(health.getPercentage(), 0, 1, 0, 100);
        if (healthProgressBar.getValue() != percentage) {
            healthProgressBar.setValue(percentage);
            healthFieldLabel.setValue(String.valueOf(health.getCurrent()));
        }

        Resource energy = statistics.getResourceNode(Constants.ENERGY);
        percentage = (int) MathUtils.map(energy.getPercentage(), 0, 1, 0, 100);
        if (energyProgressBar.getValue() != percentage) {
            energyProgressBar.setValue(percentage);
            energyFieldLabel.setValue(String.valueOf(energy.getCurrent()));
        }

        Stat node = statistics.getStatsNode(Constants.MOVE);
        nameToJKeyLabelnMap.get("MOVE").value.setText(node.getTotal() + "");
        node = statistics.getStatsNode(Constants.CLIMB);
        nameToJKeyLabelnMap.get("CLIMB").value.setText(node.getTotal() + "");
        node = statistics.getStatsNode(Constants.SPEED);
        nameToJKeyLabelnMap.get("SPEED").value.setText(node.getTotal() + "");
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

    private static String show(Stat node) {
        int total = node.getTotal();
        int base = node.getBase();
        int mods = node.getModified();

        return MessageFormat.format("{0}=({1}+{2})", total, base, mods);
    }
}
