package main.game.main.ui;

import main.constants.SimpleCheckSum;
import main.game.main.GameControllerV1;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.GameUI;
import main.ui.outline.production.core.OutlineButton;
import main.ui.outline.production.OutlineLabelToLabelRow;
import main.ui.outline.production.OutlineLabelToLabelRows;
import main.ui.swing.NoScrollBarPane;
import main.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

public class MiniUnitInfoPanelV1 extends GameUI {

    private static final int DEFAULT_ROW_HEIGHT = 10;
    private int mHeaderPanelWidth = 0;
    private int mHeaderPanelHeight = 0;
    private int mUnitImageButtonWidth = 0;
    private int mUnitImageButtonHeight = 0;
    private JButton mUnitImageButton = null;

    private OutlineButton mFooterButton = new OutlineButton();
    private OutlineLabelToLabelRows mBodyContents = null;
    private OutlineButton mHeaderLabel = null;
    private SimpleCheckSum mSimpleCheckSum = new SimpleCheckSum();
    private HorizontalResourcePanel mHealthBarRow = null;
    private HorizontalResourcePanel mStaminaBarRow = null;
    private HorizontalResourcePanel mManaBarRow = null;
    private JSONObject mRequestData = new JSONObject();
    private OutlineLabelToLabelRow mLevelAndNameRow = null;
//    public MiniUnitInfoPanel(int width, int height, Color color) {
//        super(width, height);
////        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//
//
//        mHeaderPanelWidth = width;
//        mHeaderPanelHeight = (int) (height * .35);
//        JPanel headerRowPanel = new GameUI();
//        headerRowPanel.setLayout(new BorderLayout());
//        headerRowPanel.setPreferredSize(new Dimension(mHeaderPanelWidth, mHeaderPanelHeight));
//        headerRowPanel.setBackground(color);
//
//
//        mUnitImageButtonWidth = (int) (mHeaderPanelWidth * .25);
//        mUnitImageButtonHeight = (mHeaderPanelHeight);
//        mUnitImageButton = new OutlineButton();
//        mUnitImageButton.setPreferredSize(new Dimension(mUnitImageButtonWidth, mUnitImageButtonHeight));
//        mUnitImageButton.setBackground(color);
//        headerRowPanel.add(mUnitImageButton, BorderLayout.WEST);
//
//        int headerDataPanelWidth = mHeaderPanelWidth - mUnitImageButtonWidth;
//        int headerDataPanelHeight = mHeaderPanelHeight;
//        JPanel headerDataPanel = new GameUI();
//        headerDataPanel.setLayout(new BoxLayout(headerDataPanel, BoxLayout.Y_AXIS));
//        headerDataPanel.setPreferredSize(null);
////        headerDataPanel.setPreferredSize(new Dimension(headerDataPanelWidth, headerDataPanelHeight * 4));
//        headerDataPanel.setBackground(color);
//
//
//        int headerRowWidth = headerDataPanelWidth;
//        int headerRowHeight = headerDataPanelHeight / 2;
//        mLevelAndNameRow = new OutlineLabelToLabelRow(headerRowWidth, headerRowHeight);
//        mLevelAndNameRow.setFont(FontPool.getInstance().getFontForHeight((int) (headerRowHeight * .8)));
//        mLevelAndNameRow.setLeftLabel("Lvl1");
//        mLevelAndNameRow.setRightLabel("UNIT NAME");
//        mLevelAndNameRow.setBackground(color);
//        headerDataPanel.add(mLevelAndNameRow);
////
////        headerDataPanel.add(new JButton("kokojoijo"));
////        headerDataPanel.add(new JButton("kokojoijo"));
////        headerDataPanel.add(new JButton("kokojoijo"));
////        headerDataPanel.add(new JButton("kokojoijo"));
////        headerDataPanel.add(new JButton("kokojoijo"));
////        headerDataPanel.add(new JButton("kokojoijo"));
//
//
//        mHealthBarRow = new HorizontalResourcePanel(
//                headerRowWidth,
//                headerRowHeight,
//                color,
//                ColorPalette.TRANSLUCENT_RED_LEVEL_2
//        );
//        mHealthBarRow.setLabel("HP");
//        mHealthBarRow.setLabelVisible(false);
//        headerDataPanel.add(mHealthBarRow);
////
////
////
//        mStaminaBarRow = new HorizontalResourcePanel(
//                headerRowWidth,
//                headerRowHeight,
//                color,
//                ColorPalette.TRANSLUCENT_FOREST_GREEN_LEVEL_4
//        );
//        mStaminaBarRow.setLabel("SP");
//        mStaminaBarRow.setLabelVisible(false);
//        headerDataPanel.add(mStaminaBarRow);
//
////
////
//        mManaBarRow = new HorizontalResourcePanel(
//                headerRowWidth,
//                headerRowHeight,
//                color,
//                ColorPalette.TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_3
//        );
//        mManaBarRow.setLabel("MP");
//        mManaBarRow.setLabelVisible(false);
//        headerDataPanel.add(mManaBarRow);
////
////        headerRowPanel.add(new JScrollPane(headerDataPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
//
//        headerRowPanel.add(new NoScrollBarPane(
//                headerDataPanel,
//                headerDataPanelWidth,
//                headerDataPanelHeight,
//                true,
//                1
//                ), BorderLayout.CENTER
//        );
//
//        headerRowPanel.revalidate();
//        headerRowPanel.repaint();
//        //        headerRowPanel.add(headerDataPanel, BorderLayout.CENTER);
//
//        int bodyPanelWidth = width;
//        int bodyPanelHeight = height - mHeaderPanelHeight;
//        mBodyContents = new OutlineLabelToLabelRows(bodyPanelWidth, bodyPanelHeight, color, 4);
//
//
//        int footerButtonWidth = width;
//        int footerButtonHeight = (int) (height * .0);
//        mFooterButton = new OutlineButton();
////        mFooterButton.setText("tewf");
//        mFooterButton.setFont(FontPool.getInstance().getFontForHeight(footerButtonHeight));
//        mFooterButton.setPreferredSize(new Dimension(footerButtonWidth, footerButtonHeight));
//        mFooterButton.setMinimumSize(new Dimension(footerButtonWidth, footerButtonHeight));
//        mFooterButton.setMaximumSize(new Dimension(footerButtonWidth, footerButtonHeight));
//        mFooterButton.setBackground(color);
//
//
//        add(headerRowPanel);
//        add(mBodyContents);
//        add(mFooterButton);
//    }

    public MiniUnitInfoPanelV1(int width, int height, Color color) {
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

        int headerDataPanelWidth = mHeaderPanelWidth - mUnitImageButtonWidth;
        int headerDataPanelHeight = mHeaderPanelHeight;
        JPanel headerDataPanel = new GameUI();
        headerDataPanel.setLayout(new BoxLayout(headerDataPanel, BoxLayout.Y_AXIS));
        headerDataPanel.setPreferredSize(null);
//        headerDataPanel.setPreferredSize(new Dimension(headerDataPanelWidth, headerDataPanelHeight * 4));
        headerDataPanel.setBackground(color);


        int headerRowWidth = headerDataPanelWidth;
        int headerRowHeight = headerDataPanelHeight / 2;
        mLevelAndNameRow = new OutlineLabelToLabelRow(headerRowWidth, headerRowHeight);
        mLevelAndNameRow.setFont(FontPool.getInstance().getFontForHeight((int) (headerRowHeight * .8)));
        mLevelAndNameRow.setLeftLabel("Lvl1");
        mLevelAndNameRow.setRightLabel("UNIT NAME");
        mLevelAndNameRow.setBackground(color);
        headerDataPanel.add(mLevelAndNameRow);
//
//        headerDataPanel.add(new JButton("kokojoijo"));
//        headerDataPanel.add(new JButton("kokojoijo"));
//        headerDataPanel.add(new JButton("kokojoijo"));
//        headerDataPanel.add(new JButton("kokojoijo"));
//        headerDataPanel.add(new JButton("kokojoijo"));
//        headerDataPanel.add(new JButton("kokojoijo"));


        mHealthBarRow = new HorizontalResourcePanel(
                headerRowWidth,
                headerRowHeight,
                color,
                ColorPalette.TRANSLUCENT_RED_LEVEL_2
        );
//        mHealthBarRow.getResourceBar()
        mHealthBarRow.setLabel("HP");
        mHealthBarRow.setLabelVisible(false);
        headerDataPanel.add(mHealthBarRow);
//
//
//
        mStaminaBarRow = new HorizontalResourcePanel(
                headerRowWidth,
                headerRowHeight,
                color,
                ColorPalette.TRANSLUCENT_FOREST_GREEN_LEVEL_4
        );
        mStaminaBarRow.setLabel("SP");
        mStaminaBarRow.setLabelVisible(false);
        headerDataPanel.add(mStaminaBarRow);

//
//
        mManaBarRow = new HorizontalResourcePanel(
                headerRowWidth,
                headerRowHeight,
                color,
                ColorPalette.TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_3
        );
        mManaBarRow.setLabel("MP");
        mManaBarRow.setLabelVisible(false);
        headerDataPanel.add(mManaBarRow);
//
//        headerRowPanel.add(new JScrollPane(headerDataPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

        headerRowPanel.add(new NoScrollBarPane(
                        headerDataPanel,
                        headerDataPanelWidth,
                        headerDataPanelHeight,
                        true,
                        1
                ), BorderLayout.CENTER
        );

        headerRowPanel.revalidate();
        headerRowPanel.repaint();
        //        headerRowPanel.add(headerDataPanel, BorderLayout.CENTER);

        int bodyPanelWidth = width;
        int bodyPanelHeight = height - mHeaderPanelHeight;
        mBodyContents = new OutlineLabelToLabelRows(bodyPanelWidth, bodyPanelHeight, color, 6);


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

//    public MiniUnitInfoPanel(int width, int height, Color color) {
//        super(width, height);
////        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//        setBackground(color);
//
//
//        mHeaderPanelWidth = width;
//        mHeaderPanelHeight = (int) (height * .3);
//        JPanel headerRowPanel = new GameUI();
//        headerRowPanel.setLayout(new BorderLayout());
//        headerRowPanel.setPreferredSize(new Dimension(mHeaderPanelWidth, mHeaderPanelHeight));
//        headerRowPanel.setBackground(color);
//
//
//        mUnitImageButtonWidth = (int) (mHeaderPanelWidth * .25);
//        mUnitImageButtonHeight = (mHeaderPanelHeight);
//        mUnitImageButton = new OutlineButton();
//        mUnitImageButton.setPreferredSize(new Dimension(mUnitImageButtonWidth, mUnitImageButtonHeight));
//        mUnitImageButton.setBackground(color);
//        headerRowPanel.add(mUnitImageButton, BorderLayout.WEST);
//
//        int headerDataPanelWidth = mHeaderPanelWidth - mUnitImageButtonWidth;
//        int headerDataPanelHeight = mHeaderPanelHeight;
//        JPanel headerDataPanel = new GameUI();
//        headerDataPanel.setLayout(new BoxLayout(headerDataPanel, BoxLayout.Y_AXIS));
//        headerDataPanel.setPreferredSize(null);
////        headerDataPanel.setPreferredSize(new Dimension(headerDataPanelWidth, headerDataPanelHeight * 4));
//        headerDataPanel.setBackground(color);
//
//
//        int headerRowWidth = headerDataPanelWidth;
//        int headerRowHeight = headerDataPanelHeight / 4;
//        mLevelAndNameRow = new OutlineLabelToLabelRow(headerRowWidth, headerRowHeight);
//        mLevelAndNameRow.setFont(FontPool.getInstance().getFontForHeight((int) (headerRowHeight * .8)));
//        mLevelAndNameRow.setLeftLabel("Lvl1");
//        mLevelAndNameRow.setRightLabel("UNIT NAME");
//        mLevelAndNameRow.setBackground(color);
//        headerDataPanel.add(mLevelAndNameRow);
////
////        headerDataPanel.add(new JButton("kokojoijo"));
////        headerDataPanel.add(new JButton("kokojoijo"));
////        headerDataPanel.add(new JButton("kokojoijo"));
////        headerDataPanel.add(new JButton("kokojoijo"));
////        headerDataPanel.add(new JButton("kokojoijo"));
////        headerDataPanel.add(new JButton("kokojoijo"));
//
////
////        headerRowPanel.add(new JScrollPane(headerDataPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
//
//        headerRowPanel.add(new NoScrollBarPane(
//                        headerDataPanel,
//                        headerDataPanelWidth,
//                        headerDataPanelHeight,
//                        true,
//                        1
//                ), BorderLayout.CENTER
//        );
//
//        headerRowPanel.revalidate();
//        headerRowPanel.repaint();
//        //        headerRowPanel.add(headerDataPanel, BorderLayout.CENTER);
//
//        int bodyPanelWidth = width;
//        int bodyPanelHeight = (int) (height * .6);
//        mBodyContents = new OutlineLabelToLabelRows(bodyPanelWidth, bodyPanelHeight, color, 4);
//
//
//        int footerButtonWidth = width;
//        int footerButtonHeight = (int) (height * .1);
//        mFooterButton = new OutlineButton();
////        mFooterButton.setText("tewf");
//        mFooterButton.setFont(FontPool.getInstance().getFontForHeight(footerButtonHeight));
//        mFooterButton.setPreferredSize(new Dimension(footerButtonWidth, footerButtonHeight));
//        mFooterButton.setMinimumSize(new Dimension(footerButtonWidth, footerButtonHeight));
//        mFooterButton.setMaximumSize(new Dimension(footerButtonWidth, footerButtonHeight));
//        mFooterButton.setBackground(color);
//
//
//
//        int footerPanelWidth = width;
//        int footerPanelHeight = (int) (height * .1);
//        int footerPanelItemWidth = footerPanelWidth / 3;
//        JPanel footerPanel = new GameUI();
//        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.X_AXIS));
//        footerPanel.setPreferredSize(null);
////        headerDataPanel.setPreferredSize(new Dimension(headerDataPanelWidth, headerDataPanelHeight * 4));
//        footerPanel.setBackground(color);
//
//        mHealthBarRow = new HorizontalResourcePanel(
//                footerPanelItemWidth,
//                footerPanelHeight,
//                color,
//                ColorPalette.TRANSLUCENT_RED_LEVEL_2
//        );
////        mHealthBarRow.setLabel("HP");
//        mHealthBarRow.setLabelVisible(false);
//        footerPanel.add(mHealthBarRow);
//
//        mStaminaBarRow = new HorizontalResourcePanel(
//                footerPanelItemWidth,
//                footerPanelHeight,
//                color,
//                ColorPalette.TRANSLUCENT_FOREST_GREEN_LEVEL_4
//        );
////        mStaminaBarRow.setLabel("SP");
//        mStaminaBarRow.setLabelVisible(false);
//        footerPanel.add(mStaminaBarRow);
//
//        mManaBarRow = new HorizontalResourcePanel(
//                footerPanelItemWidth,
//                footerPanelHeight,
//                color,
//                ColorPalette.TRANSLUCENT_DEEP_SKY_BLUE_LEVEL_3
//        );
////        mManaBarRow.setLabel("MP");
//        mManaBarRow.setLabelVisible(false);
//        footerPanel.add(mManaBarRow);
//
//
//
//
//
//
//        add(headerRowPanel);
//        add(mBodyContents);
//        add(footerPanel);
//    }


    public void gameUpdate(GameControllerV1 gameControllerV1) {
        String unitAtSelectedTiles = gameControllerV1.getUnitAtSelectedTiles();
        if (unitAtSelectedTiles == null) {
            return;
        }
        mRequestData.clear();
        mRequestData.put("id", unitAtSelectedTiles);
        mRequestData.put("resource", "health");
        JSONObject objectResponse = gameControllerV1.getUnitResourceStats(mRequestData);
        int tempCurrent = objectResponse.getInt("current");
        int tempTotal = objectResponse.getInt("total");
        if (!mSimpleCheckSum.isUpdated("STATE_LOCK", unitAtSelectedTiles, tempCurrent, tempCurrent)) {
            setVisible(true);
            return;
        }

        setVisible(true);
        String assetName = gameControllerV1.getUnitName(unitAtSelectedTiles);
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
        objectResponse = gameControllerV1.getUnitResourceStats(mRequestData);
        mHealthBarRow.setMax(objectResponse.getInt("total"));
        mHealthBarRow.setCurrentNoAnimation(objectResponse.getInt("current"));

        mRequestData.put("resource", "mana");
        objectResponse = gameControllerV1.getUnitResourceStats(mRequestData);
        mManaBarRow.setMax(objectResponse.getInt("total"));
        mManaBarRow.setCurrentNoAnimation(objectResponse.getInt("current"));

        mRequestData.put("resource", "stamina");
        objectResponse = gameControllerV1.getUnitResourceStats(mRequestData);
        mStaminaBarRow.setMax(objectResponse.getInt("total"));
        mStaminaBarRow.setCurrentNoAnimation(objectResponse.getInt("current"));



        mRequestData.clear();
        mRequestData.put("id", unitAtSelectedTiles);
        mRequestData.put("resource", "level");
        objectResponse = gameControllerV1.getUnitResourceStats(mRequestData);
        int unitLevel = objectResponse.getInt("total");
        mLevelAndNameRow.setLeftLabel("Lvl. " + unitLevel);


        objectResponse = gameControllerV1.getUnitIdentifiers(mRequestData);
        String nickname = objectResponse.getString("nickname");
        String unit = objectResponse.getString("unit");
        unit = StringUtils.convertSnakeCaseToCapitalized(unit);
        mLevelAndNameRow.setRightLabel(nickname);
        JSONArray arrayResponse = gameControllerV1.getUnitStatsForMiniUnitInfoPanel(mRequestData);

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
