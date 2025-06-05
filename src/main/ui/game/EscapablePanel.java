package main.ui.game;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.JavaFXUtils;
import main.constants.Pair;
import main.ui.foundation.BeveledButton;

import java.util.Map;

public class EscapablePanel extends GamePanel {

    protected final ScrollPane mContentPanelScroller;
    protected final VBox mContentPanel;
    protected final Color mColor;
    protected final VBox mContainer;
    protected final BeveledButton mBannerBackButton;
    protected final BeveledButton mBannerTextField;
    protected final int mContentWidth;
    protected final int mContentHeight;

    public EscapablePanel(int x, int y, int width, int height, Color color) {
        super(x, y, width, height);

        setEffect(JavaFXUtils.createBasicDropShadowFixed(width, height));
        setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));

        mColor = color;

        int bannerWidth = width;
        int bannerHeight = (int) (height * 0.2); // Banner takes 20% of total height

        // ✅ **Main Layout Container**
        mContainer = new VBox();
        mContainer.setPrefSize(width, height);
        mContainer.setMinSize(width, height);
        mContainer.setMaxSize(width, height);

        // ✅ **Fixed Banner Row**
        HBox bannerRow = new HBox();
        bannerRow.setPrefSize(bannerWidth, bannerHeight);
        bannerRow.setMinSize(bannerWidth, bannerHeight);
        bannerRow.setMaxSize(bannerWidth, bannerHeight);
        bannerRow.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));

        int bannerBackButtonWidth = (int) (bannerWidth * 0.2);
        int bannerBackButtonHeight = bannerHeight;
        mBannerBackButton = new BeveledButton(bannerBackButtonWidth, bannerBackButtonHeight, "X", color);

        int bannerTextFieldWidth = bannerWidth - bannerBackButtonWidth;
        int bannerTextFieldHeight = bannerHeight;
        mBannerTextField = new BeveledButton(bannerTextFieldWidth, bannerTextFieldHeight, "Banner", color);

        bannerRow.getChildren().addAll(mBannerBackButton, mBannerTextField);

        // ✅ **Scrollable Content Panel**
        mContentPanel = new VBox();
        mContentPanel.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
        mContentPanel.setFillWidth(true);

        int contentPanelScrollingWidth = width;
        int contentPanelScrollingHeight = height - bannerHeight; // Make it fit remaining space

        mContentWidth = contentPanelScrollingWidth;
        mContentHeight = contentPanelScrollingHeight;

        mContentPanelScroller = new ScrollPane();
        mContentPanelScroller.setFitToWidth(true);
        mContentPanelScroller.setPrefSize(contentPanelScrollingWidth, contentPanelScrollingHeight);
        mContentPanelScroller.setMinSize(contentPanelScrollingWidth, contentPanelScrollingHeight);
        mContentPanelScroller.setMaxSize(contentPanelScrollingWidth, contentPanelScrollingHeight);
        mContentPanelScroller.setContent(mContentPanel);
        mContentPanelScroller.setStyle(JavaFXUtils.TRANSPARENT_STYLING);
        mContentPanelScroller.setPickOnBounds(false);
        mContentPanelScroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mContentPanelScroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // ✅ **Final Layout**
        mContainer.getChildren().addAll(bannerRow, mContentPanelScroller);
        getChildren().add(mContainer);
    }


    protected Pair<BeveledButton, BeveledButton> getOrCreateRow(
            Map<String, Pair<BeveledButton, BeveledButton>> mMap,
            String name,
            int mButtonWidth,
            int mButtonHeight) {
        Pair<BeveledButton, BeveledButton> newRow = mMap.get(name);
        if (newRow != null) { return newRow; }

        HBox hBox = new HBox();
        hBox.setPrefSize(mButtonWidth, mButtonHeight);
        hBox.setMinSize(mButtonWidth, mButtonHeight);
        hBox.setMaxSize(mButtonWidth, mButtonHeight);
        hBox.setFillHeight(true);

        Color color = mColor;

        int newButtonWidth = (int) (mButtonWidth * .2);
        BeveledButton leftButton = new BeveledButton(newButtonWidth, mButtonHeight, "", color);

        int rightButtonWidth = (int) (mButtonWidth - newButtonWidth);
        BeveledButton rightButton = new BeveledButton(rightButtonWidth, mButtonHeight, name, color);

        hBox.getChildren().addAll(leftButton, rightButton);

        mContentPanel.getChildren().add(hBox); // ✅ Add to the scrollable area

        Pair<BeveledButton, BeveledButton> pair = new Pair<>(leftButton, rightButton);
        mMap.put(name, pair);

        return pair;
    }

    public BeveledButton getBanner() { return mBannerTextField; }
    public int getContentWidth() { return mContentWidth; }
    public int getContentHeight() { return mContentHeight; }
    public BeveledButton getEscapeButton() { return mBannerBackButton; }
    public void setMainContent(Node node) { mContentPanel.getChildren().clear(); mContentPanel.getChildren().add(node); }
}
