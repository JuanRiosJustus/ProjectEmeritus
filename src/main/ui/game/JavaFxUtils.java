package main.ui.game;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class JavaFxUtils {
    public static final String TRANSPARENT_STYLING = "-fx-background: transparent; -fx-background-color: transparent;";

    public static Pane createWrapperPane(int width, int height) {
        Pane containerPane = new Pane();
        containerPane.setPrefSize(width, height);
        containerPane.setMinSize(width, height);
        containerPane.setMaxSize(width, height);
//        containerPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        containerPane.setPickOnBounds(false);
        return containerPane;
    }


    // ** Convert Color to CSS-Compatible RGB String **
    public static String toRgbString(Color color) {
        return String.format("rgb(%d, %d, %d)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}
