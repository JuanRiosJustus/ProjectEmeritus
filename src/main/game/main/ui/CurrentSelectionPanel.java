package main.game.main.ui;

import main.constants.StateLock;
import main.game.components.AssetComponent;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.stores.factories.EntityStore;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.Animation;
import main.graphics.GameUI;
import main.ui.outline.production.core.OutlineButton;
import org.json.JSONObject;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class CurrentSelectionPanel extends GameUI {
    private int mBottomRowImageHeight = 0;
    private int mBottomRowLabelWidth = 0;
    private int mBottomRowLabelHeight = 0;
    private JButton mBottomRowLabel = null;
    private StateLock mStateLock = new StateLock();

    private int mEntityPortraitWidth = 0;
    private int mEntityPortraitHeight = 0;
    private JButton mEntityPortrait = null;
//    protected AnimationPanel mEntityPortrait;

    private int mBottomRowWidth = 0;
    private int mBottomRowHeight = 0;
    private JPanel mBottomRow = null;

    private String tileLayerAsset = null;
    private Timer timer = null;
    protected String mMonitoredUnitEntityID = null;

    public CurrentSelectionPanel(int x, int y, int width, int height, Color color) {
        super(x, y, width, height);

        setLayout(new BorderLayout());
        setBackground(color);

        mEntityPortraitWidth = width;
        mEntityPortraitHeight = (int) (height * .8);


//        mEntityPortrait = new AnimationPanel();
        mEntityPortrait = new OutlineButton();
        mEntityPortrait.setPreferredSize(new Dimension(mEntityPortraitWidth, mEntityPortraitHeight));
        mEntityPortrait.setMinimumSize(new Dimension(mEntityPortraitWidth, mEntityPortraitHeight));
        mEntityPortrait.setMaximumSize(new Dimension(mEntityPortraitWidth, mEntityPortraitHeight));
        mEntityPortrait.setContentAreaFilled(false);
//        mEntityPortrait.setVerticalAlignment(SwingConstants.BOTTOM);
        mEntityPortrait.setBackground(color);
//        SwingUiUtils.setHoverEffect(mEntityPortrait);

        add(mEntityPortrait, BorderLayout.CENTER);


        mBottomRowWidth = mEntityPortraitWidth;
        mBottomRowHeight = height - mEntityPortraitHeight;

        mBottomRow = new JPanel();
        mBottomRow.setLayout(new BorderLayout());
        mBottomRow.setBackground(Color.cyan);
        mBottomRow.setPreferredSize(new Dimension(mBottomRowWidth, mBottomRowHeight));
        mBottomRow.setMinimumSize(new Dimension(mBottomRowWidth, mBottomRowHeight));
        mBottomRow.setMaximumSize(new Dimension(mBottomRowWidth, mBottomRowHeight));

        mBottomRowLabelWidth = width;
        mBottomRowLabelHeight = mBottomRowHeight;

        mBottomRowLabel = new OutlineButton();
        mBottomRowLabel.setPreferredSize(new Dimension(mBottomRowLabelWidth, mBottomRowLabelHeight));
        mBottomRowLabel.setMinimumSize(new Dimension(mBottomRowLabelWidth, mBottomRowLabelHeight));
        mBottomRowLabel.setMaximumSize(new Dimension(mBottomRowLabelWidth, mBottomRowLabelHeight));
        int fontSize = (int) (mBottomRowLabelHeight);
        mBottomRowLabel.setFont(FontPool.getInstance().getFontForHeight(fontSize));
        mBottomRowLabel.setBackground(color);

        mBottomRow.add(mBottomRowLabel, BorderLayout.CENTER);
        add(mBottomRow, BorderLayout.SOUTH);

        setBounds(x, y, width, height);
    }

    public void gameUpdate(GameController gameController) {
        int selectedTilesHash = gameController.getSelectedTilesHash();
        if (!mStateLock.isUpdated("SELECTED_TILES_HASH", selectedTilesHash)) {
            return;
        }

        JSONObject selectedTileData = gameController.getSelectedTilesInfoForMiniSelectionInfoPanel();
        if (selectedTileData.isEmpty()) {
            setVisible(false);
            return;
        }

        setVisible(true);

        tileLayerAsset = selectedTileData.getString("top_layer_asset");
        String label = selectedTileData.getString("label");
        String assetID = selectedTileData.getString("asset_id");
        String entityOnTile = selectedTileData.getString("entity_on_tile");

        mMonitoredUnitEntityID = (entityOnTile == null || entityOnTile.isBlank() ? null : entityOnTile);
        mBottomRowLabel.setText(label);

        setupLargerPortraitImage(assetID, entityOnTile);
    }

    private void setupLargerPortraitImage(String selectedTileAssetID, String entityOnTile) {
        String id = null;
        float scaleFactor = .75f;
        int assetWidth = (int) (mEntityPortraitWidth * scaleFactor);
        int assetHeight = (int) (mEntityPortraitHeight * scaleFactor);
        if (entityOnTile != null && !entityOnTile.isBlank()) {
            Entity entity = EntityStore.getInstance().get(entityOnTile);

            AssetComponent assetComponent = entity.get(AssetComponent.class);
            id = assetComponent.getMainID();
            String sprite = AssetPool.getInstance().getSprite(id);

            if (EntityStore.getInstance().isUnitEntity(entityOnTile)) {
                id = AssetPool.getInstance().getOrCreateVerticalStretchAsset(
                        assetWidth,
                        assetHeight,
                        sprite,
                        -1,
                        entityOnTile + "_larger_tile_panel_unit"
                );
            } else if (EntityStore.getInstance().isStructureEntity(entityOnTile)) {
                id = AssetPool.getInstance().getOrCreateTopSwayingAsset(
                        assetWidth,
                        assetHeight,
                        sprite,
                        -1,
                        entityOnTile + "_larger_tile_panel_structure"
                );
            }
        }

        String largerTilePortraitID = AssetPool.getInstance().getOrCreateCopyOfAsset(
                mEntityPortraitWidth,
                mEntityPortraitHeight,
                selectedTileAssetID,
                selectedTileAssetID + "_larger_tile_panel_tile_"
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


//    /**
//     * Adds an animation to a JButton by looping through an array of BufferedImage.
//     *
//     * @param button           The JButton to animate.
//     * @param animationFrames  The array of BufferedImage for the animation frames.
//     * @param frameDelayMillis The delay between frames in milliseconds.
//     */
//    private void addAnimationToButton(AnimationPanel button, BufferedImage[] animationFrames, int frameDelayMillis) {
//
//        if (timer != null) { timer.stop(); }
//
//        button.stopAnimation();
//        if (animationFrames.length > 1) {
//            button.setup(animationFrames, frameDelayMillis, false);
//            button.startAnimation();
////            timer = new Timer(frameDelayMillis, new ActionListener() {
////                private int currentFrameIndex = 0;
////                @Override
////                public void actionPerformed(ActionEvent e) {
////                    // Set the icon for the current frame
////                    button.setIcon(new ImageIcon(animationFrames[currentFrameIndex]));
////
////                    // Move to the next frame, looping back to the start if necessary
////                    currentFrameIndex = (currentFrameIndex + 1) % animationFrames.length;
////                }
////            });
//
//            // Start the animation
////            timer.start();
//        } else {
//            button.setup(new BufferedImage[]{ animationFrames[0] }, frameDelayMillis, true);
//            button.startAnimation();
////            button.setIcon(new ImageIcon(animationFrames[0]));
//        }
//    }


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

    public JButton getLabelButton() { return mBottomRowLabel; }
    public String getMonitoredUnitID() { return mMonitoredUnitEntityID; }
}
