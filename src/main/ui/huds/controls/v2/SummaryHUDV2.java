package main.ui.huds.controls.v2;

import main.constants.ColorPalette;
import main.constants.Constants;
import main.game.components.*;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.ui.custom.*;
import main.ui.huds.controls.HUD;
import main.utils.MathUtils;
import main.utils.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class SummaryHUDV2 extends HUD {
    private SecondTimer timer = new SecondTimer();
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private Entity lastViewedUnit = null;
    private boolean initialized = false;
    private Map<String, Integer> mHashStateMap = new HashMap<>();
//    private final JKeyValueMap combatStatPane;
    private final JKeyValueMapV2 mStatsKeyValueMap;
    public SummaryHUDV2(int width, int height) {
        super(width, height, "Summary");

        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;

        // Image
        selection = new ImagePanel(width, (int) (height * .2));
        add(selection, constraints);

        // raw stats
        constraints.gridy = 1;
        mStatsKeyValueMap = new JKeyValueMapV2(
                width,
                (int) (height * .8),
                new Object[][]{
                        new Object[]{ Constants.NAME, new JLabel() },
                        new Object[]{ Summary.LEVEL, new JLabel() },
                        new Object[]{ Constants.TYPE, new JLabel() },
                        new Object[]{ Constants.TAGS, SwingUiUtils.getComboBox() },

                        new Object[]{ Summary.EXPERIENCE, SwingUiUtils.getComboBox() },
                        new Object[]{ Summary.EXPERIENCE + " Bar", SwingUiUtils.getProgressBar0to100() },
                        new Object[]{ Summary.HEALTH, SwingUiUtils.getComboBox() },
                        new Object[]{ Summary.HEALTH + " Bar",SwingUiUtils.getProgressBar0to100() },
                        new Object[]{ Summary.MANA, SwingUiUtils.getComboBox() },
                        new Object[]{ Summary.MANA + " Bar", SwingUiUtils.getProgressBar0to100() },
                        new Object[]{ Summary.STAMINA, SwingUiUtils.getComboBox() },
                        new Object[]{ Summary.STAMINA + " Bar", SwingUiUtils.getProgressBar0to100() },

                        new Object[]{ Summary.MOVE, SwingUiUtils.getComboBox() },
                        new Object[]{ Summary.CLIMB, SwingUiUtils.getComboBox() },
                        new Object[]{ Summary.SPEED, SwingUiUtils.getComboBox() },

                        new Object[]{ Summary.STRENGTH, SwingUiUtils.getComboBox() },
                        new Object[]{ Summary.INTELLIGENCE, SwingUiUtils.getComboBox() },
                        new Object[]{ Summary.DEXTERITY, SwingUiUtils.getComboBox() },
                        new Object[]{ Summary.WISDOM, SwingUiUtils.getComboBox() },
                        new Object[]{ Summary.CONSTITUTION, SwingUiUtils.getComboBox() },
                        new Object[]{ Summary.CHARISMA, SwingUiUtils.getComboBox() },

                        new Object[]{ Summary.RESISTANCE, SwingUiUtils.getComboBox() },
                        new Object[]{ Summary.LUCK, SwingUiUtils.getComboBox() },
                }
        );
        // Hude the name of the health bars
        for (String key : mStatsKeyValueMap.getKeySet()) {
            if (!key.contains(" Bar")) { continue; }
            mStatsKeyValueMap.get(key).fill();
        }

        add(mStatsKeyValueMap, constraints);
    }
    @Override
    public void jSceneUpdate(GameModel gameModel) {
//        // TODO cacheing on this object or mak observable... so many ui calls
        if (gameModel == null) { return; }
        if (mCurrentUnit == null) { return; }

        selection.set(mCurrentUnit);

        if (!initialized) {
            updateUi(mCurrentUnit, true);
            initialized = true;
        } else {
            updateUi(mCurrentUnit, false);
        }
    }

    private void updateUi(Entity entity, boolean forceUpdate) {

        Summary statistics = entity.get(Summary.class);

        for (String statKey : statistics.getKeySet()) {
            int base = statistics.getStatBase(statKey);
            int current = statistics.getStatCurrent(statKey);
            int total = statistics.getStatTotal(statKey);
            int hashState = statistics.getHashState(statKey);

            Set<String> mapKeys = mStatsKeyValueMap.getKeys(statKey);
            for (String mapKey : mapKeys) {
                JComponent component = mStatsKeyValueMap.get(mapKey).getValueComponent();

                if (shouldNotUpdate(forceUpdate, mapKey, hashState)) { continue; }

//                System.out.println("Updating " + mapKey + " to " + current + " with total " + total);

                if (component instanceof JProgressBar progressBar) {
                    float currentPercent = (float) ((current * 1.0) / (total * 1.0));
                    int percentage = (int) MathUtils.map(currentPercent, 0, 1, 0, 100);
                    progressBar.setValue(percentage);

                } else if (component instanceof JLabel label) {

                    label.setText(String.valueOf(current));
                } else if (component instanceof JComboBox comboBox) {

                    comboBox.removeAllItems();
                    comboBox.addItem(String.valueOf(current));
                    comboBox.addItem(base + "(Base) " + (total - base) + "(Mods)");
                    Map<String, Float> summary = statistics.getStatModSummary(statKey);
                    for (Map.Entry<String, Float> entry : summary.entrySet()) {
                        float value = entry.getValue();
                        String valueAsText = String.valueOf(value);;
                        if (value >= 0 && value <= 1) {
                            valueAsText = MathUtils.floatToPercent(value);
                        }
                        comboBox.addItem("(@" + entry.getKey() + ") " + (value  > 0 ? "+" : "") + valueAsText);
                    }
                }
            }
        }

        JLabel adhocLabel = (JLabel) mStatsKeyValueMap.get(Constants.TYPE).getValueComponent();
        if (shouldUpdate(forceUpdate, Constants.TYPE, Objects.hash(statistics.getType().toString()))) {
            adhocLabel.setText(statistics.getType().toString());
        }

        adhocLabel = (JLabel) mStatsKeyValueMap.get(Constants.NAME).getValueComponent();
        if (shouldUpdate(forceUpdate, Constants.NAME, Objects.hash(entity.toString()))) {
            adhocLabel.setText(entity + " (" + statistics.getSpecies() + ")");
        }

        JComboBox comboBox = (JComboBox) mStatsKeyValueMap.get(Constants.TAGS).getValueComponent();
        Tags unitTags = entity.get(Tags.class);
        if (shouldUpdate(forceUpdate, Constants.TAGS, Objects.hash(unitTags.getTagMap()))) {
            comboBox.removeAllItems();
            for (Map.Entry<String, Object> entry : unitTags.getTagMap().entrySet()) {
                comboBox.addItem("(@" + entry.getValue() + ") " + StringUtils.spaceByCapitalization(entry.getKey()));
            }
        }
    }

    private boolean shouldNotUpdate(boolean forceUpdate, String mapKey, int hashState) {
        // Use hashStateMap to determine what needs to be updated
        int currentState = mHashStateMap.getOrDefault(mapKey, 0);
        if (currentState == hashState && !forceUpdate) { return true; }
        mHashStateMap.put(mapKey, hashState);
        return false;
    }

    private boolean shouldUpdate(boolean forceUpdate, String mapKey, int hashState) {
        return !shouldNotUpdate(forceUpdate, mapKey, hashState);
    }
}
