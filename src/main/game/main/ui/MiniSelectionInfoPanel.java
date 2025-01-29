package main.game.main.ui;

import main.constants.StateLock;
import main.game.components.AssetComponent;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.stores.factories.EntityFactory;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.Animation;
import main.graphics.GameUI;
import main.ui.outline.production.core.OutlineButton;
import org.json.JSONObject;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class MiniSelectionInfoPanel extends GameUI {
    private int mBottomRowImageWidth = 0;
    private int mBottomRowImageHeight = 0;
    private JButton mBottomRowImage = null;

    private int mBottomRowLabelWidth = 0;
    private int mBottomRowLabelHeight = 0;
    private JButton mBottomRowLabel = null;
    private StateLock mStateLock = new StateLock();

    private int mEntityPortraitWidth = 0;
    private int mEntityPortraitHeight = 0;
    private JButton mEntityPortrait = null;

    private int mBottomRowWidth = 0;
    private int mBottomRowHeight = 0;
    private JPanel mBottomRow = null;

    private String tileLayerAsset = null;
    private Timer timer = null;

    public MiniSelectionInfoPanel(int width, int height, Color color) {
        super(width, height);

        setLayout(new BorderLayout());
        setBackground(color);

        mEntityPortraitWidth = width;
        mEntityPortraitHeight = (int) (height * .8);

        mEntityPortrait = new OutlineButton();
        mEntityPortrait.setPreferredSize(new Dimension(mEntityPortraitWidth, mEntityPortraitHeight));
        mEntityPortrait.setMinimumSize(new Dimension(mEntityPortraitWidth, mEntityPortraitHeight));
        mEntityPortrait.setMaximumSize(new Dimension(mEntityPortraitWidth, mEntityPortraitHeight));
        mEntityPortrait.setContentAreaFilled(false);
        mEntityPortrait.setVerticalAlignment(SwingConstants.BOTTOM);
        mEntityPortrait.setBackground(color);

        add(mEntityPortrait, BorderLayout.CENTER);


        mBottomRowWidth = mEntityPortraitWidth;
        mBottomRowHeight = height - mEntityPortraitHeight;

        mBottomRow = new JPanel();
        mBottomRow.setLayout(new BorderLayout());
        mBottomRow.setBackground(Color.cyan);
        mBottomRow.setPreferredSize(new Dimension(mBottomRowWidth, mBottomRowHeight));
        mBottomRow.setMinimumSize(new Dimension(mBottomRowWidth, mBottomRowHeight));
        mBottomRow.setMaximumSize(new Dimension(mBottomRowWidth, mBottomRowHeight));



        int imageSize = Math.min(mBottomRowWidth, mBottomRowHeight);
        mBottomRowImageWidth = imageSize;
        mBottomRowImageHeight = imageSize;
        mBottomRowImage = new OutlineButton();
        mBottomRowImage.setPreferredSize(new Dimension(mBottomRowImageWidth, mBottomRowImageHeight));
        mBottomRowImage.setMinimumSize(new Dimension(mBottomRowImageWidth, mBottomRowImageHeight));
        mBottomRowImage.setMaximumSize(new Dimension(mBottomRowImageWidth, mBottomRowImageHeight));
        mBottomRowImage.setBackground(color);


        mBottomRowLabelWidth = width - mBottomRowImageWidth;
        mBottomRowLabelHeight = mBottomRowImageHeight;

        mBottomRowLabel = new OutlineButton();
        mBottomRowLabel.setPreferredSize(new Dimension(mBottomRowLabelWidth, mBottomRowLabelHeight));
        mBottomRowLabel.setMinimumSize(new Dimension(mBottomRowLabelWidth, mBottomRowLabelHeight));
        mBottomRowLabel.setMaximumSize(new Dimension(mBottomRowLabelWidth, mBottomRowLabelHeight));
        int fontSize = (int) (mBottomRowLabelHeight * .8);
        mBottomRowLabel.setFont(FontPool.getInstance().getFontForHeight(fontSize));
        mBottomRowLabel.setBackground(color);

        mBottomRow.add(mBottomRowImage, BorderLayout.WEST);
        mBottomRow.add(mBottomRowLabel, BorderLayout.CENTER);
        add(mBottomRow, BorderLayout.SOUTH);
    }

//    public void gameUpdate(GameController gameController) {
//        List<JSONObject> selectedTiles = gameController.getSelectedTiles();
//        if (selectedTiles.isEmpty()) { setVisible(false); return; }
//        Tile selectedTile = (Tile) selectedTiles.get(0);
//
//        if (!mStateLock.isUpdated("IS_NEW_STATE_SELECTED", selectedTile)) {
//            setVisible(true); return;
//        }
//
//        setVisible(true);
//
//
//        mTopTileLayerAsset = selectedTile.getTopLayerAsset();
//        String id = AssetPool.getInstance().getOrCreateAsset(
//                mBottomRowImageWidth,
//                mBottomRowImageHeight,
//                mTopTileLayerAsset,
//                AssetPool.STATIC_ANIMATION,
//                -1,
//                selectedTile.hashCode() + "_mini_tile_panel_tile_"
////                selectedTile.getOriginFrame(),
////                selectedTile.hashCode() + "_mini_tile_panel_tile_" + selectedTile.getOriginFrame()
//        );
//        Asset asset = AssetPool.getInstance().getAsset(id);
//        mBottomRowImage.setIcon(new ImageIcon(asset.getAnimation().toImage()));
//        mBottomRowLabel.setText(selectedTile.getBasicIdentityString() + " (" + selectedTile.getHeight() + ")");
//
//
//        setupLargerPortraitImage(selectedTile);
//    }

    public void gameUpdate(GameController gameController) {
        JSONObject selectedTileData = gameController.getSelectedTilesInfoForMiniSelectionInfoPanel();
        if (selectedTileData.isEmpty()) { setVisible(false); return; }
        String selectedTileID = selectedTileData.getString("id");

        if (!mStateLock.isUpdated("IS_NEW_STATE_SELECTED", selectedTileID)) {
            setVisible(true); return;
        }

        setVisible(true);

        tileLayerAsset = selectedTileData.getString("top_layer_asset");
        selectedTileID = selectedTileData.getString("id");
        String label = selectedTileData.getString("label");
        String assetID = selectedTileData.getString("asset_id");
        String entityOnTile = selectedTileData.getString("entity_on_tile");


        int originFrame = AssetPool.getInstance().getOriginFrame(assetID);
        String miniTilePortraitAssetID = AssetPool.getInstance().getOrCreateAsset(
                mBottomRowImageWidth,
                mBottomRowImageHeight,
                tileLayerAsset,
                AssetPool.STATIC_ANIMATION,
                originFrame,
                selectedTileID + "_mini_tile_panel_tile_"
        );
        Asset asset = AssetPool.getInstance().getAsset(miniTilePortraitAssetID);
        mBottomRowImage.setIcon(new ImageIcon(asset.getAnimation().toImage()));
        mBottomRowLabel.setText(label);

        setupLargerPortraitImage(miniTilePortraitAssetID, entityOnTile);
    }

//    public void gameUpdate(GameController gameController) {
//        JSONArray selectedTiles = gameController.getSelectedTiles();
//        if (selectedTiles.isEmpty()) { setVisible(false); return; }
//        String selectedTile = selectedTiles.getString(0);
//
//        if (!mStateLock.isUpdated("IS_NEW_STATE_SELECTED", selectedTile)) {
//            setVisible(true); return;
//        }
//
//        setVisible(true);
//
//        JSONObject request = new JSONObject();
//        request.put("id", selectedTile);
//        JSONObject assetID = gameController.getSelectedTilesInfoForMiniSelectionInfoPanel(request);
//        tileLayerAsset = assetID.getString(selectedTile);
//
//
//        String id = AssetPool.getInstance().getOrCreateAsset(
//                mBottomRowImageWidth,
//                mBottomRowImageHeight,
//                tileLayerAsset,
//                AssetPool.STATIC_ANIMATION,
//                -1,
//                selectedTile + "_mini_tile_panel_tile_"
////                selectedTile.getOriginFrame(),
////                selectedTile.hashCode() + "_mini_tile_panel_tile_" + selectedTile.getOriginFrame()
//        );
//        Asset asset = AssetPool.getInstance().getAsset(id);
//        mBottomRowImage.setIcon(new ImageIcon(asset.getAnimation().toImage()));
////        mBottomRowLabel.setText(selectedTile.getBasicIdentityString() + " (" + selectedTile.getHeight() + ")");
//
//
//        setupLargerPortraitImage(selectedTile);
//    }

    private void setupLargerPortraitImage(String miniTilePortraitId, String entityOnTile) {
        String id = null;
        float scaleFactor = .75f;
        int assetWidth = (int) (mEntityPortraitWidth * scaleFactor);
        int assetHeight = (int) (mEntityPortraitHeight * scaleFactor);
        if (entityOnTile != null && !entityOnTile.isBlank()) {
            Entity entity = EntityFactory.getInstance().get(entityOnTile);

            AssetComponent assetComponent = entity.get(AssetComponent.class);
            id = assetComponent.getMainID();
            String sprite = AssetPool.getInstance().getSprite(id);

            if (EntityFactory.getInstance().isUnitEntity(entityOnTile)) {
                id = AssetPool.getInstance().getOrCreateAsset(
                        assetWidth,
                        assetHeight,
                        sprite,
                        AssetPool.STRETCH_Y_ANIMATION,
                        -1,
                        entityOnTile + "_larger_tile_panel_unit"
                );
            } else if (EntityFactory.getInstance().isStructureEntity(entityOnTile)) {
                id = AssetPool.getInstance().getOrCreateAsset(
                        assetWidth,
                        assetHeight,
                        sprite,
                        AssetPool.TOP_SWAYING_ANIMATION,
                        -1,
                        entityOnTile + "_larger_tile_panel_structure"
                );
            }
        }

        String sprite = AssetPool.getInstance().getSprite(miniTilePortraitId);
        int originFrame = AssetPool.getInstance().getOriginFrame(miniTilePortraitId);
        String largerTilePortraitID = AssetPool.getInstance().getOrCreateAsset(
                mEntityPortraitWidth,
                mEntityPortraitHeight,
                sprite,
                AssetPool.STATIC_ANIMATION,
                originFrame,
                miniTilePortraitId + "_larger_tile_panel_tile_"
        );

        if (id != null) {
            String mergedId = AssetPool.getInstance().createPortrait(id, largerTilePortraitID);
            Animation mergedAnimation = AssetPool.getInstance().getAnimation(mergedId);
            addAnimationToButton(mEntityPortrait, mergedAnimation.getContent(), 60);
        } else {
            Animation animation = AssetPool.getInstance().getAnimation(largerTilePortraitID);
            addAnimationToButton(mEntityPortrait, new BufferedImage[]{ animation.getFrame(animation.getCurrentFrame())},30);
        }
    }


    /**
     * Adds an animation to a JButton by looping through an array of BufferedImage.
     *
     * @param button           The JButton to animate.
     * @param animationFrames  The array of BufferedImage for the animation frames.
     * @param frameDelayMillis The delay between frames in milliseconds.
     */
    private void addAnimationToButton(JButton button, BufferedImage[] animationFrames, int frameDelayMillis) {

        if (timer != null) { timer.stop(); }

        if (animationFrames.length > 1) {
            timer = new Timer(frameDelayMillis, new ActionListener() {
                private int currentFrameIndex = 0;
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Set the icon for the current frame
                    button.setIcon(new ImageIcon(animationFrames[currentFrameIndex]));

                    // Move to the next frame, looping back to the start if necessary
                    currentFrameIndex = (currentFrameIndex + 1) % animationFrames.length;
                }
            });

            // Start the animation
            timer.start();
        } else {
            button.setIcon(new ImageIcon(animationFrames[0]));
        }
    }

    public JButton getValueButton() { return mBottomRowLabel; }
}
