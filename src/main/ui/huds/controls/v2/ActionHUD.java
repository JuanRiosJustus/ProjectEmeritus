package main.ui.huds.controls.v2;


import main.constants.ColorPalette;
import main.constants.Constants;
import main.game.components.AbilityManager;
import main.game.components.Summary;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.action.Ability;
import main.game.stores.pools.action.AbilityPool;
import main.ui.custom.ImagePanel;
import main.ui.custom.JKeyValueMap;
import main.ui.custom.SwingUiUtils;
import main.ui.huds.controls.HUD;
import main.utils.StringUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.Set;

public class ActionHUD extends HUD {
    private JPanel mAbilitiesPanel;
    private JPanel mSkillsPanel;
    private JTextArea description;
    private JButton lastToggledButton = null;
    private JButton currentlyToggledButton = null;
    private int hashState = 0;
    private final JKeyValueMap mStatsKeyValueMap;
    private boolean initialized = false;

    public ActionHUD(int width, int height) {
        super(width, height, "Actions");

        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.weightx = 1;
//        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;

        // Image
        mImagePanel = new ImagePanel(width, (int) (height * .25));
        add(mImagePanel, constraints);

        constraints.gridy = 1;
        mStatsKeyValueMap =  new JKeyValueMap(
                width,
                (int) (height * .4),
                new Object[][]{
                        new Object[] { Constants.NAME, new JLabel() },
                        new Object[] { Constants.TYPE, new JLabel() },
                        new Object[] { Constants.ACC, new JLabel() },
                        new Object[] { Constants.DAMAGE, SwingUiUtils.getComboBox() },
                        new Object[] { Constants.COST, SwingUiUtils.getComboBox() },
                        new Object[] { Constants.AREA, new JLabel() },
                        new Object[] { Constants.RANGE, new JLabel() },
                        new Object[] { Constants.IMPACT, new JLabel() },
                        new Object[] { Constants.TRAVEL, new JLabel() },
                }
        );
        add(mStatsKeyValueMap, constraints);


        constraints.gridy = 2;
        description = new JTextArea();
        description.setPreferredSize(new Dimension(width, (int) (height * .1)));
        description.setEditable(false);
        description.setOpaque(false);
        description.setBorder(new EmptyBorder(5, 5, 5, 5));
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        add(description, constraints);
//
        constraints.gridy = 3;
        JScrollPane pane = createButtonPane(width, (int) (height * .25));
        add(pane, constraints);
    }

    protected JScrollPane createButtonPane(int width, int height) {
        mAbilitiesPanel = new JPanel();
        mAbilitiesPanel.setLayout(new GridLayout(0, 2));
        mAbilitiesPanel.setBackground(ColorPalette.getRandomColor());

        mSkillsPanel = new JPanel();
        mSkillsPanel.setLayout(new GridLayout(0, 2));
        mSkillsPanel.setBackground(ColorPalette.getRandomColor());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add(mAbilitiesPanel, "Abilities");
        tabbedPane.add(SwingUiUtils.createBonelessScrollingPane(width, height, mSkillsPanel), "Skills");

        tabbedPane.setPreferredSize(new Dimension(width, height));
        tabbedPane.setMinimumSize(tabbedPane.getPreferredSize());
        tabbedPane.setMaximumSize(tabbedPane.getPreferredSize());


        JPanel panel = new JPanel();
        panel.add(tabbedPane);

        return createScalingPane(width, height, panel);
    }

    private void updateUi(Entity entity, boolean forceUpdate) {

        Summary summary = entity.get(Summary.class);
        Set<String> abilitiesAndSkills = summary.getAbilities();
        abilitiesAndSkills.addAll(summary.getSkills());

        int newHashState = abilitiesAndSkills.hashCode();
        if (abilitiesAndSkills.isEmpty() || newHashState == hashState) { return; }
        hashState = newHashState;


        mAbilitiesPanel.removeAll();
        mSkillsPanel.removeAll();
        AbilityManager abilityManager = entity.get(AbilityManager.class);

        for (String key : abilitiesAndSkills) {
            JButton button = new JButton(key);
            button.setFocusPainted(false);
            if (summary.setContains(Summary.ABILITIES, key)) {
                mAbilitiesPanel.add(button);
            } else if (summary.setContains(Summary.SKILLS, key)) {
                mSkillsPanel.add(button);
            }

            button.addActionListener(e -> {

                Ability ability = AbilityPool.getInstance().get(key);
                if (ability == null) { return; }

                JLabel label = (JLabel) mStatsKeyValueMap.get(Constants.NAME).getValueComponent();
                label.setText(ability.name);

                label = (JLabel) mStatsKeyValueMap.get(Constants.TYPE).getValueComponent();
                label.setText(ability.getTypes().toString());

                JComboBox comboBox = (JComboBox) mStatsKeyValueMap.get(Constants.DAMAGE).getValueComponent();
                comboBox.removeAllItems();
                for (String damageType : ability.getDamageKeys()) {
                    int damage = (int) ability.getDamage(entity, damageType);
                    comboBox.addItem("(" + damageType + ") " + damage);
                }

                comboBox = (JComboBox) mStatsKeyValueMap.get(Constants.COST).getValueComponent();
                comboBox.removeAllItems();
                for (String costType : ability.getCostKeys()) {
                    int cost = (int) ability.getCost(entity, costType);
                    comboBox.addItem("(" + costType + ") " + cost);
                }

                label = (JLabel) mStatsKeyValueMap.get(Constants.IMPACT).getValueComponent();
                label.setText(ability.impact);

                label = (JLabel) mStatsKeyValueMap.get(Constants.ACC).getValueComponent();
                label.setText(StringUtils.floatToPercent(ability.accuracy));

                label = (JLabel) mStatsKeyValueMap.get(Constants.AREA).getValueComponent();
                label.setText(String.valueOf(ability.area));

                label = (JLabel) mStatsKeyValueMap.get(Constants.RANGE).getValueComponent();
                label.setText(String.valueOf(ability.range));

                label = (JLabel) mStatsKeyValueMap.get(Constants.TRAVEL).getValueComponent();
                label.setText(ability.travel);

                description.setText(ability.description);
                abilityManager.preparing = ability;
            });
        }
    }

    @Override
    public void jSceneUpdate(GameModel model) {
        if (mCurrentUnit == null) { return; }

        mImagePanel.set(mCurrentUnit);

        if (!initialized) {
            updateUi(mCurrentUnit, true);
            initialized = true;
        } else {
            updateUi(mCurrentUnit, false);
        }
    }
}
