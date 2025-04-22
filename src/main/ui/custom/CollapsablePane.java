package main.ui.custom;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import main.constants.JavaFXUtils;

public class CollapsablePane extends VBox {
    private int mWidth = 0;
    private int mHeight = 0;
    public CollapsablePane(int width, int height) {
        mWidth = width;
        mHeight = height;

        setPrefSize(mWidth, mHeight);
        setMinSize(mWidth, mHeight);
        setMaxSize(mWidth, mHeight);
        setBackground(new Background(new BackgroundFill(Color.LEMONCHIFFON, CornerRadii.EMPTY, Insets.EMPTY)));

        int buttonWidth = mWidth;
        int buttonHeight = (int) (mHeight * .1);
        Button closer = new Button("-");
        closer.setPrefSize(buttonWidth, buttonHeight);
        closer.setMinSize(buttonWidth, buttonHeight);
        closer.setMaxSize(buttonWidth, buttonHeight);

        int containerWidth = mWidth;
        int containerHeight = mHeight - buttonHeight;
        var container = JavaFXUtils.createWrapperPane(containerWidth, containerHeight);
        container.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));

        getChildren().addAll(closer, container);

        closer.setOnMousePressed(e -> {
            container.setVisible(!container.isVisible());
            if (container.isVisible()) {
                setPrefSize(containerWidth, containerHeight);
                setMinSize(containerWidth, containerHeight);
                setMaxSize(containerWidth, containerHeight);
            } else {
                setPrefSize(buttonWidth, buttonHeight);
                setMinSize(buttonWidth, buttonHeight);
                setMaxSize(buttonWidth, buttonHeight);
            }
        });
    }

}
