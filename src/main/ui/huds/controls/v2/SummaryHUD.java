package main.ui.huds.controls.v2;

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


public class SummaryHUD extends HUD {
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private boolean initialized = false;
    private final Map<String, Integer> mHashStateMap = new HashMap<>();
    private final DatasheetPanel mStatsKeyValueMap;
    public SummaryHUD(int width, int height) {
        super(width, height, 0, 0, SummaryHUD.class.getSimpleName());

        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTHWEST;

        // Image
        mImagePanel = new ImagePanel(width, (int) (height * .25));
        add(mImagePanel, constraints);

        // raw stats
        constraints.gridy = 1;
        mStatsKeyValueMap = new DatasheetPanel(
                width,
                (int) (height * .75),
                new Object[][]{
                        new Object[]{ Constants.NAME, new JLabel() },
                        new Object[]{ Statistics.LEVEL, new JLabel() },
                        new Object[]{ Constants.TYPE, new JLabel() },
                        new Object[]{ Constants.TAGS, SwingUiUtils.getRightAlignedComboBox() },


                        new Object[]{ Statistics.EXPERIENCE, SwingUiUtils.getRightAlignedComboBox() },
                        new Object[]{ Statistics.EXPERIENCE + " Bar", SwingUiUtils.getProgressBar0to100() },
                        new Object[]{ Statistics.HEALTH, SwingUiUtils.getRightAlignedComboBox() },
                        new Object[]{ Statistics.HEALTH + " Bar",SwingUiUtils.getProgressBar0to100() },
                        new Object[]{ Statistics.MANA, SwingUiUtils.getRightAlignedComboBox() },
                        new Object[]{ Statistics.MANA + " Bar", SwingUiUtils.getProgressBar0to100() },
                        new Object[]{ Statistics.STAMINA, SwingUiUtils.getRightAlignedComboBox() },
                        new Object[]{ Statistics.STAMINA + " Bar", SwingUiUtils.getProgressBar0to100() },

                        new Object[]{ Statistics.MOVE, SwingUiUtils.getRightAlignedComboBox() },
                        new Object[]{ Statistics.CLIMB, SwingUiUtils.getRightAlignedComboBox() },
                        new Object[]{ Statistics.SPEED, SwingUiUtils.getRightAlignedComboBox() },

                        new Object[]{ Statistics.PHYSICAL_ATTACK, SwingUiUtils.getRightAlignedComboBox() },
                        new Object[]{ Statistics.MAGICAL_ATTACK, SwingUiUtils.getRightAlignedComboBox() },
//                        new Object[]{ Statistics.DEXTERITY, SwingUiUtils.getRightAlignedComboBox() },
                        new Object[]{ Statistics.MAGICAL_DEFENSE, SwingUiUtils.getRightAlignedComboBox() },
                        new Object[]{ Statistics.PHYSICAL_DEFENSE, SwingUiUtils.getRightAlignedComboBox() },
                        new Object[]{ Statistics.CHARISMA, SwingUiUtils.getRightAlignedComboBox() },

                        new Object[]{ Statistics.RESISTANCE, SwingUiUtils.getRightAlignedComboBox() },
                        new Object[]{ Statistics.LUCK, SwingUiUtils.getRightAlignedComboBox() },
                }
        );
        // Hude the name of the health bars
        for (String key : mStatsKeyValueMap.getKeySet()) {
            if (!key.contains(" Bar")) { continue; }
//            mStatsKeyValueMap.get(key).setEnabled(false);
//            mStatsKeyValueMap.get(key).setKey("");
        }

        add(mStatsKeyValueMap, constraints);
    }

    @Override
    public void jSceneUpdate(GameModel gameModel) {
//        // TODO cacheing on this object or mak observable... so many ui calls
        if (gameModel == null) { return; }
        if (mCurrentUnit == null) { return; }

        mImagePanel.set(mCurrentUnit);

        if (!initialized) {
            updateUi(mCurrentUnit, true);
            initialized = true;
        } else {
            updateUi(mCurrentUnit, false);
        }
    }

    private void updateUi(Entity entity, boolean forceUpdate) {

        Statistics statistics = entity.get(Statistics.class);

        for (String statKey : statistics.getKeySet()) {
            int base = statistics.getStatBase(statKey);
            int mods = statistics.getStatModified(statKey);
            int current = statistics.getStatCurrent(statKey);
            int total = statistics.getStatTotal(statKey);
            int hashState = statistics.getHashState(statKey);

            Set<String> mapKeys = mStatsKeyValueMap.getKeys(statKey);
            for (String mapKey : mapKeys) {
                JComponent component = mStatsKeyValueMap.get(mapKey).getValueComponent();

                if (shouldNotUpdate(forceUpdate, mapKey, hashState)) { continue; }

                if (component instanceof JProgressBar progressBar) {
                    float currentPercent = (float) ((current * 1.0) / (total * 1.0));
                    int percentage = (int) MathUtils.map(currentPercent, 0, 1, 0, 100);
                    progressBar.setValue(percentage);
                    mStatsKeyValueMap.get(mapKey).setKey(percentage + "%");
//                    mStatsKeyValueMap.get(mapKey).setBackground(ColorPalette.TRANSLUCENT_GREY_V1);

                } else if (component instanceof JLabel label) {

                    label.setText(String.valueOf(current));
                } else if (component instanceof JComboBox comboBox) {

                    comboBox.removeAllItems();
                    comboBox.addItem(String.valueOf(current));
                    comboBox.addItem("----------------");
                    comboBox.addItem("(Base) " + base);
                    comboBox.addItem("(Mods) " + mods);
                    comboBox.addItem("(Total) " + total);
                    comboBox.addItem("----------------");
                    Map<String, Float> summary = statistics.getStatModSummary(statKey);
                    for (Map.Entry<String, Float> entry : summary.entrySet()) {
                        float value = entry.getValue();
                        String valueAsText = String.valueOf(value);;
                        if (value >= -1 && value <= 1) {
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
            adhocLabel.setText(entity + " (" + statistics.getUnit() + ")");
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
//        if (mapKey.contains("Health"))
//            System.out.println("Current state for " + entity + " " + currentState +
//            " vs " + entity.get(Summary.class).getStatCurrent("Health"));
//        }
        if (currentState == hashState && !forceUpdate) { return true; }
        mHashStateMap.put(mapKey, hashState);
        return false;
    }

    private boolean shouldUpdate(boolean forceUpdate, String mapKey, int hashState) {
        return !shouldNotUpdate(forceUpdate, mapKey, hashState);
    }
}
