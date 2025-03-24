package main.ui.game;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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

    public static Pane createWrapperPane(int x, int y, int width, int height) {
        Pane containerPane = new Pane();
        containerPane.setPrefSize(width, height);
        containerPane.setMinSize(width, height);
        containerPane.setMaxSize(width, height);
        containerPane.setPickOnBounds(false);
        containerPane.setLayoutX(x);
        containerPane.setLayoutX(y);
        return containerPane;
    }

    public static Pane createHorizontallyCenteringPane(HBox container, int width, int height, float usedWidth) {

        HBox newPane = new HBox();
        newPane.setPrefSize(width, height);
        newPane.setMinSize(width, height);
        newPane.setMaxSize(width, height);

        int centeringSpacerWidth = (int) ((width - usedWidth) / 2);
        Region spacer1 = new Region();
        spacer1.setPrefSize(centeringSpacerWidth, height);
        spacer1.setMinSize(centeringSpacerWidth, height);
        spacer1.setMaxSize(centeringSpacerWidth, height);

        Region spacer2 = new Region();
        spacer2.setPrefSize(centeringSpacerWidth, height);
        spacer2.setMinSize(centeringSpacerWidth, height);
        spacer2.setMaxSize(centeringSpacerWidth, height);

        newPane.getChildren().addAll(spacer1, container, spacer2);
        return newPane;
    }

    public static void setCachingHints(Node node) {
        node.setCache(true);
        node.setCacheHint(CacheHint.SPEED);
        node.setManaged(true);
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


    /**
     * Returns true if the given text fits within the specified width and height.
     */
    public static boolean doesTextFit(String text, Font font, double maxWidth, double maxHeight) {
        Text helper = new Text(text);
        helper.setFont(font);
        // Optionally, set wrapping width if you want to simulate multi-line layout:
        // helper.setWrappingWidth(maxWidth);
        double textWidth = helper.getLayoutBounds().getWidth();
        double textHeight = helper.getLayoutBounds().getHeight();
        return textWidth <= maxWidth && textHeight <= maxHeight;
    }

    /**
     * Returns the maximum number of characters that can fit in a single line within the given width.
     * For proportional fonts, this is an approximation using the width of a sample character.
     *
     * @param font the Font to use for measurement
     * @param maxWidth the available width
     * @return the number of characters that fit in one line
     */
    public static int getMaxCharactersThatFitWithinWidth(Font font, double maxWidth) {
        // Use a sample character; for monospaced fonts, any character is fine.
        // For proportional fonts, you might use an average or the widest character.
        Text sample = new Text("W");
        sample.setFont(font);
        double charWidth = sample.getLayoutBounds().getWidth();
        if (charWidth <= 0) {
            return 0;
        }
        return (int) Math.floor(maxWidth / charWidth);
    }

    /**
     * Returns the maximum number of lines that can fit within the given height.
     *
     * @param font the Font to use for measurement
     * @param maxHeight the available height
     * @return the number of lines that fit
     */
    public static int getMaxCharactersThatFitWithinHeight(Font font, double maxHeight) {
        // Create a sample text to estimate the line height.
        Text sample = new Text("Ay"); // "Ay" is often used to account for ascenders/descenders
        sample.setFont(font);
        double lineHeight = sample.getLayoutBounds().getHeight();
        if (lineHeight <= 0) {
            return 0;
        }
        return (int) Math.floor(maxHeight / lineHeight);
    }
}
