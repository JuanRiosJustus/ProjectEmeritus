package main.game.main.ui;

import main.constants.Quadruple;
import main.constants.StateLock;
import main.game.main.GameController;
import main.game.stores.pools.ColorPalette;
import main.graphics.GameUI;
import main.ui.custom.ResourceBar;
import main.ui.outline.OutlineLabel;
import main.utils.RandomUtils;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Map;

public class StandardUnitInfoPanel extends GameUI {
    protected StateLock mStateLock = new StateLock();
    protected JPanel mContentPanel = null;
    protected UnitLevelTypeNameRowPanel levelTypeAndNameRow = null;
    protected UnitPortraitAndResourcesRowPanel portraitAndResourcesRow = null;
    protected UnitStatusEffectRowPanel statusEffectRow = null;
    public StandardUnitInfoPanel(int width, int height, Color color) {
        super(width, height);

//        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(color);



        mContentPanel = new JPanel();
//        mContentPanel.setLayout(new BoxLayout(mContentPanel, BoxLayout.Y_AXIS));
        mContentPanel.setBackground(color);
        mContentPanel.setPreferredSize(new Dimension(width, height));
        mContentPanel.setMinimumSize(new Dimension(width, height));
        mContentPanel.setMaximumSize(new Dimension(width, height));


        // First row
        int levelTypeNameWidth = (int) (width * 1);
        int levelTypeNameHeight = (int) (height * .04);
        levelTypeAndNameRow = new UnitLevelTypeNameRowPanel(levelTypeNameWidth, levelTypeNameHeight, ColorPalette.getRandomColor());


        int portraitAndResourcesWidth = width;
        int portraitAndResourcesHeight = (int) (height * .125);
        portraitAndResourcesRow = new UnitPortraitAndResourcesRowPanel(
                portraitAndResourcesWidth,
                portraitAndResourcesHeight,
                levelTypeAndNameRow.getLevelButtonWidth() + levelTypeAndNameRow.getTypeButtonWidth(),
                ColorPalette.getRandomColor()
        );

        // STATUS ICONS GO HERE
        int statusEffectWidth = width;
        int statusEffectHeight = (int) (height * .04);
        statusEffectRow = new UnitStatusEffectRowPanel(statusEffectWidth, statusEffectHeight, ColorPalette.getRandomColor());
        for (int i = 0; i < 15; i++) {
            statusEffectRow.putStatusEffect(RandomUtils.createRandomName(3, 6));
        }


        int statsPanelWidth = width;
        int statsPanelHeight = (int) (height * .25);
        UnitKeyAndValuePairPairs stats = new UnitKeyAndValuePairPairs(statsPanelWidth, statsPanelHeight, ColorPalette.getRandomColor());



        UnitKeyAndValuePair abilities = new UnitKeyAndValuePair(statsPanelWidth, statsPanelHeight,  ColorPalette.getRandomColor());




//        add(levelTypeNameRow);
////        add(SwingUiUtils.createWrapperJPanel(headerNameLabelWidth, headerNameLabelHeight, topMostHeader));
////        add(resourceAndImagePanel);
//        add(portraitAndResourcesRow);
////        add(unitResourcesRowPanel);
////        add(new NoScrollBarPane(mResourceRows, mResourceRowWidth, mResourceRowHeight + resourceRowSpacingHeight, true, 1));
////        add(SwingUiUtils.createWrapperJPanel(statusBarWidth, statusBarHeight, statusBarPanel));
////        add(new NoScrollBarPane(statusBarPanel, statusBarWidth, statusBarHeight, false, 1));
////        add(new NoScrollBarPane(statusEffectRow, statusEffectRow.getWidth(), statusEffectRow.getHeight(), false, 1));
//        add(statusEffectRow);
//
////        add(statsKeyAndValue);
//        add(statsKeyAndValuePair);
//        add(abilities);


        mContentPanel.add(levelTypeAndNameRow);
        mContentPanel.add(portraitAndResourcesRow);
        mContentPanel.add(statusEffectRow);
        mContentPanel.add(stats);
        mContentPanel.add(abilities);



        add(mContentPanel);





//        add(mHeader);


    }

    public void gameUpdate(GameController gameController) {
        JSONObject response = gameController.getUnitAtSelectedTilesForStandardUnitInfoPanel();
        if (response.length() == 0) {
            return;
        }
//        mRequestData.clear();
//        mRequestData.put("id", unitAtSelectedTiles);
//        mRequestData.put("resource", "health");
//        JSONObject objectResponse = gameController.getUnitResourceStats(mRequestData);
//        int tempCurrent = objectResponse.getInt("current");
//        int tempTotal = objectResponse.getInt("total");
        if (!mStateLock.isUpdated("STATE_LOCK", response.getString("id"))) {
            setVisible(true);
            return;
        }

        int level = response.getInt("level");
        String type = response.getString("type");
        String nickname = response.getString("nickname");

        levelTypeAndNameRow.setLevelText(level + "");
        levelTypeAndNameRow.setTypeButton(type);
        levelTypeAndNameRow.setNameButton(nickname);

        System.out.println("ttt");
//        JSONObject response = gameController.getCu
//        levelTypeAndNameRow.setLevelText();

    }
}
