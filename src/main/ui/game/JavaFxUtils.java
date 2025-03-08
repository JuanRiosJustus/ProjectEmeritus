package main.ui.game;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import main.game.stores.pools.ColorPalette;

public class JavaFxUtils {
    public static final String TRANSPARENT_STYLING = "-fx-background: transparent; -fx-background-color: transparent;";

    public static Pane createWrapperPane(int width, int height) {
        Pane containerPane = new Pane();
        containerPane.setPrefSize(width, height);
        containerPane.setMinSize(width, height);
        containerPane.setMaxSize(width, height);
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

    public static void setOnMousePressedEvent(Node node,  EventHandler<? super Event> handler) {
        node.setOnMousePressed(handler);
    }
    public static void addMousePressedEvent(Node node,  EventHandler<? super Event> handler) {
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, handler);
    }

    public static void setOnMouseReleasedEvent(Node node,  EventHandler<? super Event> handler) {
        node.setOnMouseReleased(handler);
    }

    public static void addMouseReleasedEvent(Node node,  EventHandler<? super Event> handler) {
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, handler);
    }

    public static void setOnMouseEnteredEvent(Node node,  EventHandler<? super Event> handler) {
        node.setOnMouseEntered(handler);
    }
    public static void addMouseEnteredEvent(Node node,  EventHandler<? super Event> handler) {
        node.addEventHandler(MouseEvent.MOUSE_ENTERED, handler);
    }
    public static void setOnMouseExitedEvent(Node node,  EventHandler<? super Event> handler) {
        node.setOnMouseExited(handler);
    }
    public static void addMouseExitedEvent(Node node,  EventHandler<? super Event> handler) {
        node.addEventHandler(MouseEvent.MOUSE_EXITED, handler);
    }

    public static void setBackgroundWithHoverEffect(Node node, Color color) {
        // 🔹 **Hover Effects**
        node.setStyle(ColorPalette.getJavaFxColorStyle(color));
        JavaFxUtils.setOnMouseEnteredEvent(node, e -> node.setStyle(ColorPalette.getJavaFxColorStyle(color.brighter())));
        JavaFxUtils.setOnMouseExitedEvent(node, e -> node.setStyle(ColorPalette.getJavaFxColorStyle(color)));
    }
    public static Effect createLighting(int width, int height) {
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(Color.color(1, 1, 1, 0.6));
        int radius = (int) (((width + height) / 2) * 0.01);
        innerShadow.setRadius(radius);
//        innerShadow.setRadius(6);
        return innerShadow;
    }
    public static Effect createBasicDropShadow(int width, int height) {
        // 🔹 **Drop Shadow for Depth Effect**
        int radius = (int) (((width + height) / 2) * 0.01);
        return createBasicDropShadow(width, height, radius);
    }

    public static Effect createBasicDropShadow(int width, int height, int radius) {
        // 🔹 **Drop Shadow for Depth Effect**
        DropShadow shadowEffect = new DropShadow();
        shadowEffect.setColor(Color.color(0, 0, 0, 0.5)); // Semi-transparent black
        shadowEffect.setRadius(radius);
        shadowEffect.setOffsetX(width * 0.025);
        shadowEffect.setOffsetY(height * 0.025);

        return shadowEffect;
    }
}
