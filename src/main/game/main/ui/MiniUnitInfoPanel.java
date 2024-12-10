package main.game.main.ui;

import main.constants.StateLock;
import main.game.main.GameController;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.GameUI;
import main.ui.outline.OutlineButton;
import main.ui.outline.production.OutlineLabelToLabelRow;
import main.ui.outline.OutlineLabelToLabelRowsWithoutHeader;
import main.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

public class MiniUnitInfoPanel extends GameUI {

    private static final int DEFAULT_ROW_HEIGHT = 10;
    private int mHeaderPanelWidth = 0;
    private int mHeaderPanelHeight = 0;
    private int mUnitImageButtonWidth = 0;
    private int mUnitImageButtonHeight = 0;
    private JButton mUnitImageButton = null;

    private OutlineButton mFooterButton = new OutlineButton();
    private OutlineLabelToLabelRowsWithoutHeader mBodyContents = null;
    private OutlineButton mHeaderLabel = null;
    private StateLock mStateLock = new StateLock();
    private HorizontalResourcePanel mHorizontalResourcePanel = null;
    private JSONObject mRequestData = new JSONObject();
    private OutlineLabelToLabelRow mLevelAndNameRow = null;
    public MiniUnitInfoPanel(int width, int height, Color color) {
        super(width, height);
//        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


        mHeaderPanelWidth = width;
        mHeaderPanelHeight = (int) (height * .35);
        JPanel headerRowPanel = new GameUI();
        headerRowPanel.setLayout(new BorderLayout());
        headerRowPanel.setPreferredSize(new Dimension(mHeaderPanelWidth, mHeaderPanelHeight));
        headerRowPanel.setBackground(color);


        mUnitImageButtonWidth = (int) (mHeaderPanelWidth * .25);
        mUnitImageButtonHeight = (mHeaderPanelHeight);
        mUnitImageButton = new OutlineButton();
        mUnitImageButton.setPreferredSize(new Dimension(mUnitImageButtonWidth, mUnitImageButtonHeight));
        mUnitImageButton.setBackground(color);

        headerRowPanel.add(mUnitImageButton, BorderLayout.WEST);


        int mHeaderLabelWidth = width - mUnitImageButtonWidth;
        int mHeaderLabelHeight = mUnitImageButtonHeight;
        int fontSize = (int) (mHeaderLabelHeight * .4);


//        OutlineLabelToLabelRowsWithoutHeader headerDataRowsPanel = new OutlineLabelToLabelRowsWithoutHeader(
//                mHeaderLabelWidth, mHeaderLabelHeight, color, 4
//        );
//        OutlineLabelToLabelRow row1 = headerDataRowsPanel.createRow("Row 1");
//        row1.setLeftLabel("White Dragon");
//        row1.setRightLabel("Lvl 5");
//        row1.setFont(FontPool.getInstance().getFontForHeight(fontSize));
//
//        OutlineLabelToLabelRow row2 = headerDataRowsPanel.createRow("Row 1");
//        row2.setLeftLabel("HP");
//        row2.setRightLabel("200/1500");
//        row2.setFont(FontPool.getInstance().getFontForHeight(fontSize));
//
//        headerRowPanel.add(headerDataRowsPanel, BorderLayout.CENTER);


        JPanel headerDataPanel = new GameUI();
        headerDataPanel.setPreferredSize(new Dimension(mHeaderLabelWidth, mHeaderLabelHeight));
        headerDataPanel.setBackground(color);
//
        int rowHeight = mHeaderLabelHeight / 2;
        mLevelAndNameRow = new OutlineLabelToLabelRow(mHeaderLabelWidth, rowHeight);
        mLevelAndNameRow.setFont(FontPool.getInstance().getFontForHeight((int) (rowHeight * .8)));
        mLevelAndNameRow.setLeftLabel("Lvl1");
        mLevelAndNameRow.setRightLabel("UNIT NAME");
        mLevelAndNameRow.setBackground(color);
        headerDataPanel.add(mLevelAndNameRow);
//
//        JPanel healthRow = new GameUI();
//        healthRow.setBackground(color);
//        healthRow.setLayout(new BorderLayout());
//        healthRow.setPreferredSize(new Dimension(mHeaderLabelWidth, mHeaderLabelHeight / 2));
//        OutlineLabel ol = new OutlineLabel("ttt");
//        ol.setPreferredSize(new Dimension(mHeaderLabelWidth / 2, mHeaderLabelHeight / 2));
//        ol.setMinimumSize(new Dimension(mHeaderLabelWidth / 2, mHeaderLabelHeight / 2));
//        ol.setMaximumSize(new Dimension(mHeaderLabelWidth / 2, mHeaderLabelHeight / 2));
//        healthRow.add(ol, BorderLayout.CENTER);
//
//        HealthBar hb = new HealthBar(100);
//        healthRow.add(hb, BorderLayout.EAST);
//
//        headerDataPanel.add(healthRow);
//        headerRowPanel.add(headerDataPanel, BorderLayout.CENTER);
        mHorizontalResourcePanel = new HorizontalResourcePanel(
                mHeaderLabelWidth,
                mHeaderLabelHeight / 2,
                color,
                ColorPalette.TRANSLUCENT_RED_LEVEL_2
        );
        mHorizontalResourcePanel.setLabel("HP");
//        hbr.setLabel("HP");
//        hbr.setMaxHealth(200);
        headerDataPanel.add(mHorizontalResourcePanel);

        headerRowPanel.add(headerDataPanel, BorderLayout.CENTER);

        int bodyPanelWidth = width;
        int bodyPanelHeight = height - mHeaderPanelHeight;
        mBodyContents = new OutlineLabelToLabelRowsWithoutHeader(bodyPanelWidth, bodyPanelHeight, color, 4);


        int footerButtonWidth = width;
        int footerButtonHeight = (int) (height * .0);
        mFooterButton = new OutlineButton();
//        mFooterButton.setText("tewf");
        mFooterButton.setFont(FontPool.getInstance().getFontForHeight(footerButtonHeight));
        mFooterButton.setPreferredSize(new Dimension(footerButtonWidth, footerButtonHeight));
        mFooterButton.setMinimumSize(new Dimension(footerButtonWidth, footerButtonHeight));
        mFooterButton.setMaximumSize(new Dimension(footerButtonWidth, footerButtonHeight));
        mFooterButton.setBackground(color);


        add(headerRowPanel);
        add(mBodyContents);
        add(mFooterButton);
    }

    public void gameUpdate(GameController gameController) {
        String unitAtSelectedTiles = gameController.getUnitAtSelectedTiles();
        if (unitAtSelectedTiles == null) {
            return;
        }
        if (!mStateLock.isUpdated("STATE_LOCK", unitAtSelectedTiles)) {
            setVisible(true);
            return;
        }

        setVisible(true);
        String assetName = gameController.getUnitName(unitAtSelectedTiles);
        String id = AssetPool.getInstance().getOrCreateAsset(
                (int) (mUnitImageButtonWidth * .9),
                (int) (mUnitImageButtonHeight * .9),
                assetName,
                AssetPool.STATIC_ANIMATION,
                0,
                assetName + "_mini_unit_panel"
        );
        Asset asset = AssetPool.getInstance().getAsset(id);
        mUnitImageButton.setIcon(new ImageIcon(asset.getAnimation().toImage()));

//        mHeaderLabel.setText(nickName);

        mRequestData.clear();
        mRequestData.put("id", unitAtSelectedTiles);
        mRequestData.put("resource", "health");
        JSONObject objectResponse = gameController.getUnitResourceStats(mRequestData);
        mHorizontalResourcePanel.setMaxHealth(objectResponse.getInt("total"));
        mHorizontalResourcePanel.setCurrentHealthNoAnimation(objectResponse.getInt("current"));


        mRequestData.clear();
        mRequestData.put("id", unitAtSelectedTiles);
        mRequestData.put("resource", "level");
        objectResponse = gameController.getUnitResourceStats(mRequestData);
        int unitLevel = objectResponse.getInt("total");
        mLevelAndNameRow.setLeftLabel("Lvl. " + unitLevel);


        objectResponse = gameController.getUnitIdentifiers(mRequestData);
        String nickname = objectResponse.getString("nickname");
        String unit = objectResponse.getString("unit");
        unit = StringUtils.convertSnakeCaseToCapitalized(unit);
        mLevelAndNameRow.setRightLabel(nickname);
        JSONArray arrayResponse = gameController.getUnitStatsForMiniUnitInfoPanel(mRequestData);

        mBodyContents.clear();
        for (int index = 0; index < arrayResponse.length(); index++) {
            JSONArray stat = arrayResponse.getJSONArray(index);

            String key = stat.getString(0);
            int base = stat.getInt(1);
            int modified = stat.getInt(2);

            OutlineLabelToLabelRow olr = mBodyContents.createRow(key);

            olr.setLeftLabel(key);
            olr.setRightLabel(base + " (" + (modified < 0 ? "-" : "+") + modified + ")");
        }
    }
}
