package main.ui.foundation;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Border;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import main.game.stores.ColorPalette;
import main.constants.JavaFXUtils;

public class BeveledTextField extends BevelStyle {
    private final TextField textField;
    protected Button mButton = null;
    protected boolean mEditing = false;

    /**
     * Creates a beveled text field with the given width, height, and base color.
     *
     * @param width     the width of the text field
     * @param height    the height of the text field
     * @param baseColor the base color used for the bevel and background
     */
    public BeveledTextField(int width, int height, Color baseColor) {
        super(width, height, baseColor);
//
//
//        // ** Background of Progress Bar (Beveled) **
////        setBorder(new Border(mOuterBevel.getStrokes().get(0), mInnerBevel.getStrokes().get(0)));
////        setBackground(new Background(new BackgroundFill(baseColor, CornerRadii.EMPTY, Insets.EMPTY)));
//
//        // Create the TextField
        textField = new TextField();
        textField.setPrefSize(width, height);
        textField.setMinSize(width, height);
        textField.setMaxSize(width, height);
//        // Make the TextField background transparent so the bevel shows
//        textField.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
//        // Set text properties – adjust font size relative to height as needed
//        textField.setStyle("-fx-text-fill: white; -fx-font-size: " + (height * 0.4) + "px; -fx-background-color: transparent;");
//        // Optionally, remove the default focus border (if desired)
//        textField.setFocusTraversable(false);
//
//        // Apply the bevel borders computed in the parent class
//        textField.setBorder(new Border(
//                mOuterBevel.getStrokes().get(0),
//                mInnerBevel.getStrokes().get(0)
//        ));
//        textField.setFont(getFontForHeight(height * 3));
//
//        // Add the TextField to this StackPane so the bevel remains visible
//        getChildren().add(textField);


//        super(width, height, baseColor);

        // ** Create Background Button **
        mButton = new Button();
        mButton.setPrefSize(width, height);
        mButton.setMinSize(width, height);
        mButton.setMaxSize(width, height);
        mButton.setPadding(new Insets(height * 0.1));
//        mButton.setFocusTraversable(false);

        // ** Apply Borders & Background **
        mButton.setBorder(new Border(
                mOuterBevel.getStrokes().get(0),
                mInnerBevel.getStrokes().get(0)
        ));

        mButton.setStyle(ColorPalette.getJavaFxColorStyle(baseColor));

        // ** Text Node ** with left alignment
//        mTextNode.setText("Test");
//        mTextNode.setTextAlignment(TextAlignment.LEFT);

        mTextNodeContainer.setAlignment(Pos.CENTER_LEFT); // Align text to the left
        mButton.setGraphic(mTextNodeContainer);

        // ** Add Elements to StackPane **
        getChildren().addAll(mButton);

        // 🔹 **Hover Effects**
        JavaFXUtils.addMouseEnteredEvent(mButton, e -> mButton.setStyle(ColorPalette.getJavaFxColorStyle(baseColor.brighter())));
        JavaFXUtils.addMouseExitedEvent(mButton, e -> mButton.setStyle(ColorPalette.getJavaFxColorStyle(baseColor)));

        mButton.setOnMouseClicked(e -> {
            if (mEditing) { return; }
            startEditing();
        });

        mButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
//                mButton.setGraphic(mTextNodeContainer);
//                mTextNode.setText(textField.getText());
//                mEditing = false;
//                    commitEdit();
                stopEditing();
            } else if (event.getCode() == KeyCode.ESCAPE) {
//                    cancelEdit();
            }
        });
//        StringBuilder sb = new StringBuilder();
//        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
////            System.out.println(" e " + event.getCode());
//            int allowedHorizontalChars = JavaFxUtils.getMaxCharactersThatFitWithinWidth(mTextNode.getFont(), width);
//            boolean wasUpdated = false;
//            if (event.getCode() == KeyCode.ENTER) {
////                    commitEdit();
//            } else if (event.getCode() == KeyCode.ESCAPE) {
////                    cancelEdit();
//            } else if (event.getCode() == KeyCode.BACK_SPACE && !sb.isEmpty()) {
//                sb.delete(sb.length() - 1, sb.length());
//                wasUpdated = true;
//            } else  {
//                sb.append(event.getCode().getChar());
//                wasUpdated = true;
//            }
//
//            if (!wasUpdated) { return; }
//            if (sb.length() + 3 < allowedHorizontalChars) {
//                mTextNode.setText(sb.toString());
//            } else if (sb.length() + 3>= allowedHorizontalChars) {
//                mTextNode.setText(sb.substring(0, allowedHorizontalChars - 3) + "...");
//            }
//        });

        // When focus is lost, commit the edit.
        mButton.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused && mEditing) {
                stopEditing();
            }
            if (wasFocused && mEditing) {
                stopEditing();
            }
        });
    }

    private void stopEditing() {
        mTextNodeContainer.setVisible(true);
        textField.setVisible(false);
        mButton.setGraphic(mTextNodeContainer);

        mTextNode.setText(textField.getText());
        mTextNodeContainer.requestFocus();
        mEditing = false;
    }
    private void startEditing() {
        mTextNodeContainer.setVisible(false);
        textField.setVisible(true);
        mButton.setGraphic(textField);

        textField.requestFocus();
        mEditing = true;
    }

    // ------------------ Test Application ------------------

    public static class TestApp extends Application {
        @Override
        public void start(Stage primaryStage) {
            // Create an instance of BeveledTextField with desired dimensions and color
            BeveledTextFieldOG beveledTextField = new BeveledTextFieldOG(300, 50, Color.DARKSLATEGRAY);
            beveledTextField.getTextField().setText("Enter text here...");

            // Create a root pane and add the BeveledTextField
            StackPane root = new StackPane(beveledTextField);
            root.setPadding(new Insets(20));
//            root.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

            Scene scene = new Scene(root, 400, 200);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Beveled TextField Test");
            primaryStage.show();
        }
    }

    // For quick testing, you can run this main method.
    public static void main(String[] args) {
        Application.launch(TestApp.class, args);
    }
}