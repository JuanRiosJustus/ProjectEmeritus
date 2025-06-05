package main.ui.custom;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.constants.JavaFXUtils;

public class CollapsableHeaderPane extends VBox {
    private int mWidth = 0;
    private int mHeight = 0;
    private int mHeaderWidth;
    private int mHeaderHeight;
    private HBox mHeader;
    private int mHeaderClosureButtonWidth;
    private int mHeaderClosureButtonHeight;
    private Button mHeaderClosureButton;
    private int mHeaderLabelWidth;
    private int mHeaderLabelHeight;
    private Button mHeaderLabel;
    private int mContainerWidth = 0;
    private int mContainerHeight = 0;
    private Pane mContainer = null;
    public CollapsableHeaderPane(int width, int height) {
        mWidth = width;
        mHeight = height;

        setPrefSize(mWidth, mHeight);
        setMinSize(mWidth, mHeight);
        setMaxSize(mWidth, mHeight);
        setBackground(new Background(new BackgroundFill(Color.LEMONCHIFFON, CornerRadii.EMPTY, Insets.EMPTY)));

        mHeaderWidth = mWidth;
        mHeaderHeight = (int) (mHeight * .1);
        mHeader = new HBox();
        mHeader.setPrefSize(mHeaderWidth, mHeaderHeight);
        mHeader.setMinSize(mHeaderWidth, mHeaderHeight);
        mHeader.setMaxSize(mHeaderWidth, mHeaderHeight);

        mHeaderClosureButtonWidth = (int) (mHeaderWidth * .2);
        mHeaderClosureButtonHeight = (int) (mHeaderHeight * 1);
        mHeaderClosureButton = new Button("-");
        mHeaderClosureButton.setPrefSize(mHeaderClosureButtonWidth, mHeaderClosureButtonHeight);
        mHeaderClosureButton.setMinSize(mHeaderClosureButtonWidth, mHeaderClosureButtonHeight);
        mHeaderClosureButton.setMaxSize(mHeaderClosureButtonWidth, mHeaderClosureButtonHeight);

        mHeaderLabelWidth = mHeaderWidth - mHeaderClosureButtonWidth;
        mHeaderLabelHeight = mHeaderHeight;
        mHeaderLabel = new Button("Header");
        mHeaderLabel.setPrefSize(mHeaderLabelWidth, mHeaderLabelHeight);
        mHeaderLabel.setMinSize(mHeaderLabelWidth, mHeaderLabelHeight);
        mHeaderLabel.setMaxSize(mHeaderLabelWidth, mHeaderLabelHeight);

        mHeader.getChildren().addAll(mHeaderClosureButton, mHeaderLabel);

        mContainerWidth = mWidth;
        mContainerHeight = mHeight - mHeaderClosureButtonHeight;

        mContainer = new StackPane();
        mContainer.setPrefSize(mContainerWidth, mContainerHeight);
        mContainer.setMinSize(mContainerWidth, mContainerHeight);
        mContainer.setMaxSize(mContainerWidth, mContainerHeight);
        mContainer.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));

        getChildren().addAll(mHeader, mContainer);

        mHeaderClosureButton.setOnMousePressed(e -> {
            mContainer.setVisible(!mContainer.isVisible());
            mHeaderLabel.setVisible(mContainer.isVisible());
            if (mContainer.isVisible()) {
                mHeaderClosureButton.setText("-");
                setPrefSize(mContainerWidth, mContainerHeight);
                setMinSize(mContainerWidth, mContainerHeight);
                setMaxSize(mContainerWidth, mContainerHeight);
            } else {
                mHeaderClosureButton.setText("+");
                setPrefSize(mHeaderClosureButtonWidth, mHeaderClosureButtonHeight);
                setMinSize(mHeaderClosureButtonWidth, mHeaderClosureButtonHeight);
                setMaxSize(mHeaderClosureButtonWidth, mHeaderClosureButtonHeight);
            }
        });
    }


    public int getContainerWidth() { return mContainerWidth; }
    public int getContainerHeight() { return mContainerHeight; }
    public void setContent(Node node) {
        mContainer.getChildren().clear();
        mContainer.getChildren().add(node);
        StackPane.setAlignment(node, javafx.geometry.Pos.CENTER);
    }
    public void setHeaderText(String txt) { mHeaderLabel.setText(txt); }

}
