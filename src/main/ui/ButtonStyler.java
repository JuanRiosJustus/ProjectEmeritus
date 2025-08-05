package main.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class ButtonStyler {


    public static void applyHoverStyle(Button button, float bevelWidth, float bevelHeight, Color baseColor) {
        Color hoverColor = baseColor.brighter();
        Color pressColor = baseColor.darker();

        Background normalBg = new Background(new BackgroundFill(baseColor, CornerRadii.EMPTY, Insets.EMPTY));
        Background hoverBg = new Background(new BackgroundFill(hoverColor, CornerRadii.EMPTY, Insets.EMPTY));
        Background pressBg = new Background(new BackgroundFill(pressColor, CornerRadii.EMPTY, Insets.EMPTY));

        Border bevelBorder = createBevelBorder(bevelWidth, bevelHeight, baseColor);

        // ✅ Clear ALL JavaFX default styles to take full control
//        button.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-background-insets: 0; -fx-background-radius: 0;");

        // ✅ Set initial background and border
        button.setBackground(normalBg);
        button.setBorder(bevelBorder);

        // ✅ Handle mouse events
        button.setOnMouseEntered(e -> button.setBackground(hoverBg));
        button.setOnMouseExited(e -> button.setBackground(normalBg));
        button.setOnMousePressed(e -> button.setBackground(pressBg));
        button.setOnMouseReleased(e -> button.setBackground(hoverBg));
    }
//    public static void applyHoverStyle(Button button, float bevelWidth, float bevelHeight, Color color) {
//        Color hoverColor = color.brighter();
//        Color pressColor = color.darker();
//
//        Background normalBg = new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY));
//        Background hoverBg = new Background(new BackgroundFill(hoverColor, CornerRadii.EMPTY, Insets.EMPTY));
//        Background pressBg = new Background(new BackgroundFill(pressColor, CornerRadii.EMPTY, Insets.EMPTY));
//
//
////        button.setStyle("-fx-background-color: transparent;");
//
//        Border bevelBorder = createBevelBorder(bevelWidth, bevelHeight, color);
//
//        button.setOnMouseEntered(e -> {
//            button.setBackground(hoverBg);
//            button.setBorder(bevelBorder);
//        });
//
//        button.setOnMouseExited(e -> {
//            button.setBackground(normalBg);
//            button.setBorder(bevelBorder);
//        });
//
//        button.setOnMousePressed(e -> {
//            button.setBackground(pressBg);
//            button.setBorder(bevelBorder);
//        });
//
//        button.setOnMouseReleased(e -> {
//            button.setBackground(hoverBg);
//            button.setBorder(bevelBorder);
//        });
//
//        button.setBackground(normalBg); // not pressBg
//        button.setBorder(bevelBorder);
//    }

    public static Border createBevelBorder(float topBottomWidth, float rightLeftHeight, Color baseColor) {
        // Calculate lighter and darker variations of the base color
        Color lightColor = baseColor.brighter();
        Color darkColor = baseColor.darker();

        // Define the width of the bevel borders (3px here, you can customize)
//        double bevelWidth = Math.max(2, Math.min(topBottomWidth, rightLeftHeight) * 0.05);

        BorderStroke topLeftStroke = new BorderStroke(
                lightColor,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(topBottomWidth, 0, 0, rightLeftHeight) // top and left
        );

        BorderStroke bottomRightStroke = new BorderStroke(
                darkColor,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(0, rightLeftHeight, topBottomWidth, 0) // bottom and right
        );

        return new Border(topLeftStroke, bottomRightStroke);
    }
}