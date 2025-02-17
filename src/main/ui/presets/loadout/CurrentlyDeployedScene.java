package main.ui.presets.loadout;

import main.engine.EngineScene;
import main.game.components.IdentityComponent;
import main.game.components.MovementComponent;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.stores.pools.ColorPalette;
import main.graphics.GameUI;
import main.ui.outline.production.core.OutlineButton;
import main.ui.outline.OutlineLabel;
import main.ui.custom.SwingUiUtils;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class CurrentlyDeployedScene extends EngineScene {

    private int mSpriteWidth;
    private int mSpriteHeight;
    private Entity mSelectedEntity;
    private JPanel mRowContentPanel;
    private Rectangle mBounds = null;
    private JButton mClearButton = null;
    private JLabel mTitleLabel = null;
    private final Map<Entity, String> mDeployedUnits = new LinkedHashMap<>();
    private float rowHeight = 0;
    public void clear() {
        mDeployedUnits.clear();
    }

    public void addUnitToDeploymentList(Entity entity, SummaryCardsPanel unitList, String data) {
        if (entity == null) { return; }
        // check to see if we already have this unit as deployed
        if (mDeployedUnits.containsKey(entity)) {
            // Go through and find the row related to this unit
            for (Component component : mRowContentPanel.getComponents()) {
                CurrentlyDeployRowContent row = (CurrentlyDeployRowContent) component;
                if (row.mEntity != entity) { continue; }
                row.setup(entity, data);
                break;
            }
            return;
        }

        // Check if there is an availkable to use slot
        boolean hasReusedComponent = false;
        CurrentlyDeployRowContent rowContent = null;
        // Check to see if there is a summary card available (Mo Entity). Then add
        for (int index = 0; index < mRowContentPanel.getComponentCount(); index++) {
            Component component = mRowContentPanel.getComponent(index);
            if (!(component instanceof CurrentlyDeployRowContent)) { continue; }
            rowContent = (CurrentlyDeployRowContent) component;
            // if this row is not being used, placed entity
            if (rowContent.mEntity != null) { continue; }
            // this row is not being used, use it
            hasReusedComponent = true;
            break;
        }
        // Didn't find any available slots, create new one
        if (!hasReusedComponent) {
            rowContent = new CurrentlyDeployRowContent(entity, mBounds.width, (int) rowHeight);
            mRowContentPanel.add(rowContent);
        }
        // Cache
        rowContent.setup(entity, data);
        mDeployedUnits.put(entity, data);
        SummaryCard summaryCard1 = unitList.getSummaryCard(mSelectedEntity);
        if (summaryCard1 == null) { return; }
        summaryCard1.setColors(ColorPalette.RED);

//        SwingUiUtils.removeAllActionListeners(rowContent.mFocusButton);
        SwingUiUtils.removeAllListeners(rowContent.mRemoveButton);
        //remove all references with etc
        CurrentlyDeployRowContent finalRowContent = rowContent;

        // When we call remove button, remove the entity from unit list and shift everything upwards
        summaryCard1.getImage().getImageContainer().addActionListener(e -> {
            removeUnitAndUpdateRows(entity, unitList, finalRowContent);
        });

        rowContent.mRemoveButton.addActionListener(e -> {
            removeUnitAndUpdateRows(entity, unitList, finalRowContent);
        });
    }
//    public void addUnitToDeploymentList(Entity entity, UnitSelectionListScene unitList) {
//        if (entity == null) { return; }
//        // check to see if we already have this unit as deployed
//        if (mDeployedUnits.containsKey(entity)) {
//            // Go through and find the row related to this unit
//            for (Component component : mRowContentPanel.getComponents()) {
//                CurrentlyDeployRowContent row = (CurrentlyDeployRowContent) component;
//                if (row.mEntity != entity) { continue; }
//                row.setup(entity);
//                break;
//            }
//            return;
//        }
//
//        // Check if there is an availkable to use slot
//        boolean hasReusedComponent = false;
//        CurrentlyDeployRowContent rowContent = null;
//        // Check to see if there is a summary card available (Mo Entity). Then add
//        for (int index = 0; index < mRowContentPanel.getComponentCount(); index++) {
//            Component component = mRowContentPanel.getComponent(index);
//            if (!(component instanceof CurrentlyDeployRowContent)) { continue; }
//            rowContent = (CurrentlyDeployRowContent) component;
//            // if this row is not being used, placed entity
//            if (rowContent.mEntity != null) { continue; }
//            // this row is not being used, use it
//            hasReusedComponent = true;
//            break;
//        }
//        // Didn't find any available slots, create new one
//        if (!hasReusedComponent) {
//            rowContent = new CurrentlyDeployRowContent(entity, mBounds.width, (int) rowHeight);
//            mRowContentPanel.add(rowContent);
//        }
//        // Cache
//        rowContent.setup(entity);
//        mDeployedUnits.put(entity, null);
//        SummaryCard summaryCard1 = unitList.getSummaryCard(mSelectedEntity);
//        if (summaryCard1 == null) { return; }
//        summaryCard1.setColors(ColorPalette.RED);
//
////        SwingUiUtils.removeAllActionListeners(rowContent.mFocusButton);
//        SwingUiUtils.removeAllActionListeners(rowContent.mRemoveButton);
//        //remove all references with etc
//        CurrentlyDeployRowContent finalRowContent = rowContent;
//
//        // When we call remove button, remove the entity from unit list and shift everything upwards
//        summaryCard1.getImage().getImageContainer().addActionListener(e -> {
//            removeUnitAndUpdateRows(entity, unitList, finalRowContent);
//        });
//
//        rowContent.mRemoveButton.addActionListener(e -> {
//            removeUnitAndUpdateRows(entity, unitList, finalRowContent);
//        });
//    }

//    public void addUnitToDeploymentList(Entity entity, EditorTile tile, SummaryCardsPanel unitList) {
//        if (entity == null) { return; }
//        // check to see if we already have this unit as deployed
//        if (mDeployedUnits.containsKey(entity)) {
//            // Go through and find the row related to this unit
//            for (Component component : mRowContentPanel.getComponents()) {
//                CurrentlyDeployRowContent row = (CurrentlyDeployRowContent) component;
//                if (row.mEntity != entity) { continue; }
//                row.setup(entity, "tile");
//                break;
//            }
//            return;
//        }
//
//        // Check if there is an availkable to use slot
//        boolean hasReusedComponent = false;
//        CurrentlyDeployRowContent rowContent = null;
//        // Check to see if there is a summary card available (Mo Entity). Then add
//        for (int index = 0; index < mRowContentPanel.getComponentCount(); index++) {
//            Component component = mRowContentPanel.getComponent(index);
//            if (!(component instanceof CurrentlyDeployRowContent)) { continue; }
//            rowContent = (CurrentlyDeployRowContent) component;
//            // if this row is not being used, placed entity
//            if (rowContent.mEntity != null) { continue; }
//            // this row is not being used, use it
//            hasReusedComponent = true;
//            break;
//        }
//        // Didn't find any available slots, create new one
//        if (!hasReusedComponent) {
//            rowContent = new CurrentlyDeployRowContent(entity, mBounds.width, (int) rowHeight);
//            mRowContentPanel.add(rowContent);
//        }
//        // Cache
//        rowContent.setup(entity, "tile");
//        mDeployedUnits.put(entity, "entity");
//        SummaryCard summaryCard1 = unitList.getSummaryCard(mSelectedEntity);
//        if (summaryCard1 == null) { return; }
//        summaryCard1.setColors(ColorPalette.RED);
//
////        SwingUiUtils.removeAllActionListeners(rowContent.mFocusButton);
//        SwingUiUtils.removeAllListeners(rowContent.mRemoveButton);
//        //remove all references with etc
//        CurrentlyDeployRowContent finalRowContent = rowContent;
//
//        // When we call remove button, remove the entity from unit list and shift everything upwards
//        summaryCard1.getImage().getImageContainer().addActionListener(e -> {
//            removeUnitAndUpdateRows(entity, unitList, finalRowContent);
//        });
//
//        rowContent.mRemoveButton.addActionListener(e -> {
//            removeUnitAndUpdateRows(entity, unitList, finalRowContent);
//        });
//    }

    private void removeUnitAndUpdateRows(Entity entity, SummaryCardsPanel unitList,
                                         CurrentlyDeployRowContent finalRowContent) {
        // remove current buttons reference
        MovementComponent movementComponent = entity.get(MovementComponent.class);
//        if (movementComponent.mCurrentTile != null) {
////            movementComponent.mCurrentTile.get(Tile.class).removeUnit();
//        }
        finalRowContent.setup(null, null);
        mDeployedUnits.remove(entity);
        mRowContentPanel.remove(finalRowContent);
        // Set the colors back to normal
        SummaryCard summaryCard = unitList.getSummaryCard(entity);
        if (summaryCard != null) {
            summaryCard.setColors(Color.DARK_GRAY);
        }

        // shift everything up
        int iteration = 0;
        for (Map.Entry<Entity, String> entry : mDeployedUnits.entrySet()) {
            CurrentlyDeployRowContent currentlyDeployRowContent =
                    (CurrentlyDeployRowContent) mRowContentPanel.getComponent(iteration);
            currentlyDeployRowContent.setup(entry.getKey(),  entry.getValue());
            iteration++;
        }
        while (iteration < mRowContentPanel.getComponentCount()) {
            CurrentlyDeployRowContent currentlyDeployRowContent =
                    (CurrentlyDeployRowContent) mRowContentPanel.getComponent(iteration);
            currentlyDeployRowContent.setup(null, null);
            iteration++;
        }
    }

    public static class CurrentlyDeployRowContent extends JPanel {
        public final OutlineLabel mNumberLabel = new OutlineLabel();
        public final OutlineLabel mNameLabel = new OutlineLabel();
        public final OutlineLabel mLevelLabel = new OutlineLabel();
        public final OutlineLabel mLocationLabel = new OutlineLabel();
        public final JButton mRemoveButton = new JButton();
        public final JButton mFocusButton = new JButton();
        public Entity mEntity = null;

        void CurrentlyDeployedRowContent(int index, Entity entity, int width, int height) {
            setLayout(new GridBagLayout());
            setBackground(Color.DARK_GRAY);
            setOpaque(true);
            setPreferredSize(new Dimension(width, height));

            mEntity = entity;

            mNameLabel.setBackground(ColorPalette.TRANSPARENT);
            mNameLabel.setPreferredSize(new Dimension((int) (width * .4), height));
            if (mEntity != null) {
                IdentityComponent identityComponent = entity.get(IdentityComponent.class);
                mNameLabel.setText(identityComponent.getNickname());
            }

            mLevelLabel.setText("Lv");
            mLevelLabel.setPreferredSize(new Dimension((int) (width * .2), height));
            mLevelLabel.setBackground(ColorPalette.TRANSPARENT);
            mLevelLabel.setHorizontalAlignment(SwingConstants.CENTER);

            mRemoveButton.setText("Remove");
            mRemoveButton.setPreferredSize(new Dimension((int) (width * .15), height));

            mFocusButton.setText("Focus");
            mFocusButton.setPreferredSize(new Dimension((int) (width * .2), height));

            mLocationLabel.setText("STUFF");
            mLocationLabel.setPreferredSize(new Dimension((int) (width * .2), height));
            mLocationLabel.setBackground(ColorPalette.TRANSPARENT);

//            mLocationLabel.setPreferredSize(new Dimension((int) (width * .15), height));
//            mLocationLabel.setBackground(ColorPalette.TRANSPARENT);
//            mLocationLabel.setText("yo");
//            mLocationLabel.setHorizontalAlignment(SwingConstants.CENTER);

            mNumberLabel.setText(index + ".)");
            mNumberLabel.setPreferredSize(new Dimension((int) (width * .1), height));

            mLevelLabel.setVisible(entity != null);
            mRemoveButton.setVisible(entity != null);
            mFocusButton.setVisible(entity != null);
            mNameLabel.setVisible(entity != null);
            mLocationLabel.setVisible(entity != null);

            add(mLevelLabel);
            add(mNameLabel);
//            add(mFocusButton);
            add(mLocationLabel);
            add(mRemoveButton);
        }
        CurrentlyDeployRowContent(Entity entity, int width, int height) {
            setLayout(new GridBagLayout());
            setBackground(Color.DARK_GRAY);
            setOpaque(true);
            setPreferredSize(new Dimension(width, height));

            mEntity = entity;

            mNameLabel.setBackground(ColorPalette.TRANSPARENT);
            mNameLabel.setPreferredSize(new Dimension((int) (width * .4), height));
            if (mEntity != null) {
                IdentityComponent identityComponent = entity.get(IdentityComponent.class);
                mNameLabel.setText(identityComponent.getNickname());
            }

            mLevelLabel.setText("Lv ");
            mLevelLabel.setPreferredSize(new Dimension((int) (width * .2), height));
            mLevelLabel.setBackground(ColorPalette.TRANSPARENT);
            mLevelLabel.setHorizontalAlignment(SwingConstants.CENTER);

            mRemoveButton.setText("Remove");
            mRemoveButton.setPreferredSize(new Dimension((int) (width * .2), height));

            mFocusButton.setText("Focus");
            mFocusButton.setPreferredSize(new Dimension((int) (width * .2), height));

            mLocationLabel.setText("Focus2");
            mLocationLabel.setPreferredSize(new Dimension((int) (width * .2), height));
            mLocationLabel.setBackground(ColorPalette.TRANSPARENT);

//            mLocationLabel.setPreferredSize(new Dimension((int) (width * .15), height));
//            mLocationLabel.setBackground(ColorPalette.TRANSPARENT);
//            mLocationLabel.setHorizontalAlignment(SwingConstants.CENTER);

            mLevelLabel.setVisible(entity != null);
//            mRemoveButton.setVisible(entity != null);
//            mFocusButton.setVisible(entity != null);
            mNameLabel.setVisible(entity != null);
            mLocationLabel.setVisible(entity != null);
            mRemoveButton.setVisible(entity != null);

            add(mLevelLabel);
            add(mNameLabel);
//            add(mRemoveButton);
            add(mLocationLabel);
            add(mRemoveButton);
//            add(mFocusButton);
        }

        CurrentlyDeployRowContent(int width, int height) {
            this(null, width, height);
        }

        public void setup(Entity entity, String data) {
            mEntity = entity;

            mLevelLabel.setVisible(entity != null);
            mRemoveButton.setVisible(entity != null);
            mFocusButton.setVisible(entity != null);
            mNameLabel.setVisible(entity != null);
            mNumberLabel.setVisible(entity != null);
            mLocationLabel.setVisible(entity != null);

            if (entity != null) {
                StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);
                mLevelLabel.setText("Lv " + statisticsComponent.getLevel());
                mNameLabel.setText(entity.toString());


                if (data == null) {
                    mLocationLabel.setText("");
                } else {
                    mLocationLabel.setText(data);
                }

                Tile currentTile = entity.get(Tile.class);
                if (currentTile != null) {
//                    mFocusButton.setText("@" + currentTile.row + ", " + currentTile.column);
                    mLocationLabel.setText("@" +currentTile.row + ", " + currentTile.column);
                }
            }
        }

//        public void setup(Entity entity) {
//            mEntity = entity;
//
//            mLevelLabel.setVisible(entity != null);
//            mRemoveButton.setVisible(entity != null);
//            mFocusButton.setVisible(entity != null);
//            mNameLabel.setVisible(entity != null);
//            mNumberLabel.setVisible(entity != null);
//            mLocationLabel.setVisible(entity != null);
//
//            if (entity != null) {
//                Statistics statistics = entity.get(Statistics.class);
//                mLevelLabel.setText("Lv " + statistics.getLevel());
//                mNameLabel.setText(entity.toString());
//
//
//                Tile currentTile = entity.get(Tile.class);
//                if (currentTile != null) {
////                    mFocusButton.setText("@" + currentTile.row + ", " + currentTile.column);
//                    mLocationLabel.setText("@" +currentTile.row + ", " + currentTile.column);
//                }
//            }
//        }

//        public void setup(Entity entity, EditorTile tile) {
//            mEntity = entity;
//
//            mLevelLabel.setVisible(entity != null);
//            mRemoveButton.setVisible(entity != null);
//            mFocusButton.setVisible(entity != null);
//            mNameLabel.setVisible(entity != null);
//            mNumberLabel.setVisible(entity != null);
//            mLocationLabel.setVisible(entity != null);
////            mEditorTile = tile;
//
//            if (entity != null) {
//                Statistics statistics = entity.get(Statistics.class);
//                mLevelLabel.setText("Lv " + statistics.getLevel());
//                mNameLabel.setText(entity.toString());
//
//
//                Tile currentTile = entity.get(Tile.class);
//                if (currentTile != null) {
////                    mFocusButton.setText(currentTile.row + ", " + currentTile.column);
//                    mLocationLabel.setText("@" +currentTile.row + ", " + currentTile.column);
//                }
//            }
//
////            if (mEditorTile != null) {
//////                mFocusButton.setText(mEditorTile.getTile().row + ", " + mEditorTile.getTile().column);
////                mLocationLabel.setText("@" +mEditorTile.getTile().row + ", " + mEditorTile.getTile().column);
////            }
//        }
    }

    public void setup(int rows, int columns, int width, int height) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(getPreferredSize());
        setMaximumSize(getPreferredSize());
//        mBounds = new Rectangle(0, 0, width, height);

        columns += 1;
        rows += 1;

        mSpriteWidth = (width / columns);
        mSpriteHeight = (height / rows);
        removeAll();

        mRowContentPanel = new JPanel();
        mRowContentPanel.removeAll();
        mRowContentPanel.setLayout(new BoxLayout(mRowContentPanel, BoxLayout.Y_AXIS));
//        mRowContentPanel.setBackground(ColorPalette.YELLOW);
//        mRowContentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        BufferedImage blank = new BufferedImage(mSpriteWidth, mSpriteHeight, BufferedImage.TYPE_INT_ARGB);

        OutlineLabel outlineLabel = new OutlineLabel();
        outlineLabel.setText("EXAMPLE WIDTH AND HEIGHT");
        outlineLabel.setPreferredSize(new Dimension(width, outlineLabel.getHeight()));
        rowHeight = (float) (height * .1);

        for (int i = mRowContentPanel.getComponentCount(); i < 8; i++) {
            CurrentlyDeployRowContent content
                    = new CurrentlyDeployRowContent(width, (int) rowHeight);
            mRowContentPanel.add(content);
        }

        mRowContentPanel.setOpaque(true);
        mRowContentPanel.setBackground(Color.RED);

        JPanel controlsPanel = new GameUI();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.X_AXIS));
        int controlsPanelWidth = width;
        int controlsPanelHeight = (int) (height * .1);
        SwingUiUtils.setSize(controlsPanel, controlsPanelWidth, controlsPanelHeight);

        int titleLabelWidth = (int) (controlsPanelWidth * .6);
        int titleLabelHeight = controlsPanelHeight;
        mTitleLabel = new OutlineLabel(1);
        mTitleLabel.setText("---Deployed Units---");
        mTitleLabel.setOpaque(false);
        SwingUiUtils.setSize(mTitleLabel, titleLabelWidth, titleLabelHeight);

        int clearAllWidth = (int) (controlsPanelWidth * .4);
        int clearAllHeight = controlsPanelHeight;
        mClearButton = new OutlineButton("");
        mClearButton.setText("Clear All");
        mClearButton.setOpaque(false);
        mClearButton.setBackground(Color.DARK_GRAY);
        SwingUiUtils.setHoverEffect(mClearButton);
        SwingUiUtils.setSize(mClearButton, clearAllWidth, clearAllHeight);
        SwingUiUtils.setStylizedRaisedBevelBorder(mClearButton, 1);

        controlsPanel.add(mTitleLabel);
        controlsPanel.add(mClearButton);
        add(controlsPanel);



        add(SwingUiUtils.createTranslucentScrollbar(width, (int) (height * .9), mRowContentPanel));
    }

    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        if (mClearButton != null) {

//            mClearButton.setBackground(color);
        }
    }

//    public void setup(int rows, int columns, int width, int height) {
//
//        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//        setPreferredSize(new Dimension(width, height));
//        setMinimumSize(getPreferredSize());
//        setMaximumSize(getPreferredSize());
////        mBounds = new Rectangle(0, 0, width, height);
//
//        columns += 1;
//        rows += 1;
//
//        mSpriteWidth = (width / columns);
//        mSpriteHeight = (height / rows);
//        removeAll();
//
//        mRowContentPanel = new JPanel();
//        mRowContentPanel.removeAll();
//        mRowContentPanel.setLayout(new BoxLayout(mRowContentPanel, BoxLayout.Y_AXIS));
//        mRowContentPanel.setBackground(ColorPalette.YELLOW);
////        mRowContentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
//
//        BufferedImage blank = new BufferedImage(mSpriteWidth, mSpriteHeight, BufferedImage.TYPE_INT_ARGB);
//
//        OutlineLabel outlineLabel = new OutlineLabel();
//        outlineLabel.setText("EXAMPLE WIDTH AND HEIGHT");
//        outlineLabel.setPreferredSize(new Dimension(width, outlineLabel.getHeight()));
//        rowHeight = (float) (height * .1);
//
//        for (int i = mRowContentPanel.getComponentCount(); i < 8; i++) {
//            CurrentlyDeployRowContent content
//                    = new CurrentlyDeployRowContent(width, (int) rowHeight);
//            mRowContentPanel.add(content);
//        }
//
//        mRowContentPanel.setOpaque(true);
//        mRowContentPanel.setBackground(Color.RED);
//
//        JPanel controlsPanel = new GameUI();
//        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.X_AXIS));
//        int controlsPanelWidth = width;
//        int controlsPanelHeight = (int) (height * .1);
//        SwingUiUtils.setPreferredSize(controlsPanel, controlsPanelWidth, controlsPanelHeight);
//
//        int titleLabelWidth = width;
//        int titleLabelHeight = (int) (height * .1);
//        mTitleLabel.setText("Deployed Units");
//        SwingUiUtils.setPreferredSize(mTitleLabel, titleLabelWidth, titleLabelHeight);
//        mTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        add(mTitleLabel);
//
//        int clearAllWidth = width;
//        int clearAllHeight = titleLabelHeight;
//        mClearButton = new JButton("Clear All");
//        SwingUiUtils.setPreferredSize(mClearButton, clearAllWidth, clearAllHeight);
//        mClearButton.setHorizontalAlignment(SwingConstants.CENTER);
//        add(mClearButton);
//
//
//        add(SwingUiUtils.createTranslucentScrollbar(getWidth(),
//                (int) (getHeight() -
//                        (mClearButton.getPreferredSize().getHeight() + mTitleLabel.getPreferredSize().getHeight()) * 1.5
//                ),
//                mRowContentPanel));
//
//
//        add(mTitleLabel);
//        add(mClearButton);
//
//
//        add(SwingUiUtils.createTranslucentScrollbar(getWidth(),
//                (int) (getHeight() -
//                        (mClearButton.getPreferredSize().getHeight() + mTitleLabel.getPreferredSize().getHeight()) * 1.5
//                ),
//                mRowContentPanel));
//    }

//    public void setup(int rows, int columns, Rectangle bounds) {
//
////        setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
//        setPreferredSize(new Dimension(bounds.width, bounds.height));
//        mBounds = bounds;
//
//        columns += 1;
//        rows += 1;
//
//        mSpriteWidth = (bounds.width / columns);
//        mSpriteHeight = (bounds.height / rows);
//        removeAll();
//
//        mRowContentPanel = new JPanel();
//        mRowContentPanel.removeAll();
//        mRowContentPanel.setLayout(new BoxLayout(mRowContentPanel, BoxLayout.Y_AXIS));
//        mRowContentPanel.setBackground(ColorPalette.TRANSPARENT);
//        mRowContentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
//
//        BufferedImage blank = new BufferedImage(mSpriteWidth, mSpriteHeight, BufferedImage.TYPE_INT_ARGB);
//
//        OutlineLabel outlineLabel = new OutlineLabel();
//        outlineLabel.setText("EXAMPLE WIDTH AND HEIGHT");
//        outlineLabel.setPreferredSize(new Dimension(bounds.width, outlineLabel.getHeight()));
//        rowHeight = (float) (mBounds.height * .1);
//
//        for (int i = mRowContentPanel.getComponentCount(); i < 8; i++) {
//            CurrentlyDeployRowContent content
//                    = new CurrentlyDeployRowContent(mBounds.width, (int) rowHeight);
//            mRowContentPanel.add(content);
//        }
//
//        mRowContentPanel.setOpaque(true);
//        mRowContentPanel.setBackground(Color.DARK_GRAY);
//
//        mTitleLabel.setText("Deployed Units");
//        mTitleLabel.setPreferredSize(new Dimension(bounds.width, (int) mTitleLabel.getPreferredSize().getHeight()));
//        mTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        mTitleLabel.setBackground(Color.DARK_GRAY);
//        add(mTitleLabel);
//
//        mClearButton = new JButton("Clear All");
//        mClearButton.setPreferredSize(new Dimension( bounds.width, (int) (mClearButton.getPreferredSize().getHeight())));
//        add(mClearButton);
//
//
//        add(SwingUiUtils.createTranslucentScrollbar(getWidth(),
//                (int) (getHeight() -
//                        (mClearButton.getPreferredSize().getHeight() + mTitleLabel.getPreferredSize().getHeight()) * 1.5
//                ),
//                mRowContentPanel));
//    }

    @Override
    public void update() {
//        for (Map.Entry<String, Entity> entry : mEntityMap.entrySet()) {
//            Animation animation = entry.getValue().get(Animation.class);
//            animation.update();
//            JButton button = mButtonMap.get(entry.getKey());
//            button.setIcon(new ImageIcon(ImageUtils.getResizedImage(animation.toImage(), mSpriteWidth, mSpriteHeight)));
//        }
    }

    @Override
    public void input() {

    }

    @Override
    public JPanel render() {
        return null;
    }

    public void setSelected(Entity selectedEntity) {
        mSelectedEntity = selectedEntity;
    }
}
