package main.constants;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import main.engine.EngineController;
import main.game.stores.pools.ColorPalette;
import main.game.stores.pools.FontPool;
import main.ui.foundation.SwitchButton;

import java.io.File;
import java.util.Arrays;

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

    public static Quadruple<HBox, Button, TextField, DirectoryChooser> getFolderToFieldRow(String init, int width, int height, float ratio) {
        int labelWidth = (int) (width * ratio);
        int fieldWidth = width - labelWidth;
        Button label = new Button();
        label.setAlignment(Pos.CENTER);
        label.setPrefSize(labelWidth, height);
        label.setMinSize(labelWidth, height);
        label.setMaxSize(labelWidth, height);

        TextField field = new TextField();
        field.setPrefSize(fieldWidth, height);
        field.setMinSize(fieldWidth, height);
        field.setMaxSize(fieldWidth, height);
        field.setEditable(false);

        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (init != null && !init.isEmpty()) { directoryChooser.setInitialDirectory(new File(init)); }

        label.setOnMousePressed(e -> {
            File selected = directoryChooser.showDialog(EngineController.getInstance().getStage());

            if (selected == null) { return; }
            File[] filesInDirectory = selected.listFiles();

            System.out.println(Arrays.toString(filesInDirectory));
        });

        HBox container = new HBox();
        container.setPrefSize(width, height);
        container.setMinSize(width, height);
        container.setMaxSize(width, height);
        container.getChildren().addAll(label, field);

        Quadruple<HBox, Button, TextField, DirectoryChooser> row = new Quadruple<>(container, label, field, directoryChooser);

        return row;
    }

    public static Tuple<HBox, Label, CheckBox> getLabelToSwitchButton(String text, int width, int height) {
        int labelWidth = (int) (width * .5);
        int fieldWidth = width - labelWidth;
        Label label = new Label();
        label.setText(text);
        label.setAlignment(Pos.CENTER);
        label.setPrefSize(labelWidth, height);
        label.setMinSize(labelWidth, height);
        label.setMaxSize(labelWidth, height);

        CheckBox button = new CheckBox();
        button.setPrefSize(fieldWidth, height);
        button.setMinSize(fieldWidth, height);
        button.setMaxSize(fieldWidth, height);
        button.setOnAction(e -> {
//            if (button.isSelected()) {
//                button.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
//            } else {
//                button.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
//            }
        });

        HBox container = new HBox();
        container.setPrefSize(width, height);
        container.setMinSize(width, height);
        container.setMaxSize(width, height);
        container.getChildren().addAll(label, button);

        Tuple<HBox, Label, CheckBox> row = new Tuple<>(container, label, button);

        return row;
    }

    private Tuple<HBox, Label, Button> getLabelToFieldRow(String text, int width, int height, float ratio) {
        int labelWidth = (int) (width * ratio);
        int fieldWidth = width - labelWidth;
        Label label = new Label();
        label.setText(text);
        label.setAlignment(Pos.CENTER);
        label.setPrefSize(labelWidth, height);
        label.setMinSize(labelWidth, height);
        label.setMaxSize(labelWidth, height);

        Button button = new Button();
        button.setPrefSize(fieldWidth, height);
        button.setMinSize(fieldWidth, height);
        button.setMaxSize(fieldWidth, height);

        HBox container = new HBox();
        container.setPrefSize(width, height);
        container.setMinSize(width, height);
        container.setMaxSize(width, height);
        container.getChildren().addAll(label, button);

        Tuple<HBox, Label, Button> row = new Tuple<>(container, label, button);

        return row;
    }


    public static Tuple<HBox, ImageView, TextField> getImageAndStringField(int width, int height, float ratio) {
        int labelWidth = (int) (width * ratio);
        int fieldWidth = width - labelWidth;
        ImageView label = new ImageView();
        label.setFitWidth(labelWidth);
        label.setFitHeight(height);

        TextField textField = new TextField();
        textField.setPrefSize(fieldWidth, height);
        textField.setMinSize(fieldWidth, height);
        textField.setMaxSize(fieldWidth, height);

        HBox container = new HBox();
        container.setPrefSize(width, height);
        container.setMinSize(width, height);
        container.setMaxSize(width, height);
        container.getChildren().addAll(label, textField);

        Tuple<HBox, ImageView, TextField> row = new Tuple<>(container, label, textField);

        return row;
    }


    public static Tuple<HBox, Button, ComboBox<Object>> getSyncedRatioImageAndComboBox(int width, int height) {
        int labelWidth = (int) (height);
        int fieldWidth = width - labelWidth;
        Button button = new Button();
        button.setPrefSize(labelWidth, height);
        button.setMinSize(labelWidth, height);
        button.setMaxSize(labelWidth, height);

        ComboBox<Object> comboBox = new ComboBox<>();
        comboBox.setPrefSize(fieldWidth, height);
        comboBox.setMinSize(fieldWidth, height);
        comboBox.setMaxSize(fieldWidth, height);

        HBox container = new HBox();
        container.setPrefSize(width, height);
        container.setMinSize(width, height);
        container.setMaxSize(width, height);
        container.getChildren().addAll(button, comboBox);

        Tuple<HBox, Button, ComboBox<Object>> row = new Tuple<>(container, button, comboBox);

        return row;
    }

    public static Tuple<HBox, Button, ComboBox<Object>> getImageAndComboBox(int width, int height, float ratio) {
        int labelWidth = (int) (width * ratio);
        int fieldWidth = width - labelWidth;
        Button label = new Button();
        label.setPrefSize(labelWidth, height);
        label.setMinSize(labelWidth, height);
        label.setMaxSize(labelWidth, height);

        ComboBox<Object> comboBox = new ComboBox<>();
        comboBox.setPrefSize(fieldWidth, height);
        comboBox.setMinSize(fieldWidth, height);
        comboBox.setMaxSize(fieldWidth, height);

        HBox container = new HBox();
        container.setPrefSize(width, height);
        container.setMinSize(width, height);
        container.setMaxSize(width, height);
        container.getChildren().addAll(label, comboBox);

        Tuple<HBox, Button, ComboBox<Object>> row = new Tuple<>(container, label, comboBox);

        return row;
    }

    public static Tuple<HBox, Label, TextField> getLabelAndIntegerField(int width, int height, float ratio) {
        int labelWidth = (int) (width * ratio);
        int fieldWidth = width - labelWidth;
        Label label = new Label();
        label.setAlignment(Pos.CENTER);
        label.setPrefSize(labelWidth, height);
        label.setMinSize(labelWidth, height);
        label.setMaxSize(labelWidth, height);

        TextField textField = new TextField();
        textField.setPrefSize(fieldWidth, height);
        textField.setMinSize(fieldWidth, height);
        textField.setMaxSize(fieldWidth, height);


//        textField.textProperty().addListener((observable, oldValue, newValue) -> {
//            try {
//                textField.setText(Integer.parseInt(newValue) + "");
//            } catch (Exception ex) {
//                textField.setText(oldValue);
//            }
//        });

        HBox container = new HBox();
        container.setPrefSize(width, height);
        container.setMinSize(width, height);
        container.setMaxSize(width, height);
        container.getChildren().addAll(label, textField);

        Tuple<HBox, Label, TextField> row = new Tuple<>(container, label, textField);

        return row;
    }


    public static Tuple<HBox, Label, ComboBox<String>> getLabelAndComboBox(int width, int height, float ratio) {
        int labelWidth = (int) (width * ratio);
        int fieldWidth = width - labelWidth;
        Label label = new Label();
        label.setAlignment(Pos.CENTER);
        label.setPrefSize(labelWidth, height);
        label.setMinSize(labelWidth, height);
        label.setMaxSize(labelWidth, height);
//        label.setFont(Font.font("System", FontWeight.BOLD, getFon));
        label.setFont(FontPool.getInstance().getBoldFontForHeight(height));

        ComboBox<String> comboBox = new ComboBox<String>();
        comboBox.setPrefSize(fieldWidth, height);
        comboBox.setMinSize(fieldWidth, height);
        comboBox.setMaxSize(fieldWidth, height);

        HBox container = new HBox();
        container.setPrefSize(width, height);
        container.setMinSize(width, height);
        container.setMaxSize(width, height);
        container.getChildren().addAll(label, comboBox);

        Tuple<HBox, Label, ComboBox<String>> row = new Tuple<>(container, label, comboBox);

        return row;
    }




    public static Tuple<HBox, Label, Slider> getButtonAndSliderField(int width, int height, float ratio) {
        int labelWidth = (int) (width * ratio);
        int fieldWidth = width - labelWidth;
        Label label = new Label();
        label.setAlignment(Pos.CENTER);
        label.setPrefSize(labelWidth, height);
        label.setMinSize(labelWidth, height);
        label.setMaxSize(labelWidth, height);

        Slider field = new Slider();
        field.setPrefSize(fieldWidth, height);
        field.setMinSize(fieldWidth, height);
        field.setMaxSize(fieldWidth, height);

        HBox container = new HBox();
        container.setPrefSize(width, height);
        container.setMinSize(width, height);
        container.setMaxSize(width, height);
        container.getChildren().addAll(label, field);

        Tuple<HBox, Label, Slider> row = new Tuple<>(container, label, field);

        return row;
    }

    public static Tuple<HBox, Label, TextField> getLabelAndFloatField(int width, int height, float ratio) {
        int labelWidth = (int) (width * ratio);
        int fieldWidth = width - labelWidth;
        Label label = new Label();
        label.setAlignment(Pos.CENTER);
        label.setPrefSize(labelWidth, height);
        label.setMinSize(labelWidth, height);
        label.setMaxSize(labelWidth, height);

        TextField field = new TextField();
        field.setPrefSize(fieldWidth, height);
        field.setMinSize(fieldWidth, height);
        field.setMaxSize(fieldWidth, height);


        field.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                field.setText(Float.parseFloat(newValue) + "");
            } catch (Exception ex) {
                field.setText(oldValue);
            }
        });

        HBox container = new HBox();
        container.setPrefSize(width, height);
        container.setMinSize(width, height);
        container.setMaxSize(width, height);
        container.getChildren().addAll(label, field);

        Tuple<HBox, Label, TextField> row = new Tuple<>(container, label, field);

        return row;
    }


    public static Tuple<HBox, Label, TextField> getLabelToFieldRow(int width, int height, float ratio) {
        int labelWidth = (int) (width * ratio);
        int fieldWidth = width - labelWidth;
        Label label = new Label();
        label.setAlignment(Pos.CENTER);
        label.setPrefSize(labelWidth, height);
        label.setMinSize(labelWidth, height);
        label.setMaxSize(labelWidth, height);

        TextField field = new TextField();
        field.setPrefSize(fieldWidth, height);
        field.setMinSize(fieldWidth, height);
        field.setMaxSize(fieldWidth, height);

        HBox container = new HBox();
        container.setPrefSize(width, height);
        container.setMinSize(width, height);
        container.setMaxSize(width, height);
        container.getChildren().addAll(label, field);

        Tuple<HBox, Label, TextField> row = new Tuple<>(container, label, field);

        return row;
    }

    public static Tuple<HBox, Button, TextField> getButtonToFieldRow(int width, int height, float ratio) {
        int labelWidth = (int) (width * ratio);
        int fieldWidth = width - labelWidth;
        Button label = new Button();
        label.setAlignment(Pos.CENTER);
        label.setPrefSize(labelWidth, height);
        label.setMinSize(labelWidth, height);
        label.setMaxSize(labelWidth, height);

        TextField field = new TextField();
        field.setPrefSize(fieldWidth, height);
        field.setMinSize(fieldWidth, height);
        field.setMaxSize(fieldWidth, height);

        HBox container = new HBox();
        container.setPrefSize(width, height);
        container.setMinSize(width, height);
        container.setMaxSize(width, height);
        container.getChildren().addAll(label, field);

        Tuple<HBox, Button, TextField> row = new Tuple<>(container, label, field);

        return row;
    }


}
