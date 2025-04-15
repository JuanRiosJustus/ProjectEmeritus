package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CollapsiblePaneTest extends Application {

    public static class CollapsiblePane extends VBox {
        private final Button toggleButton;
        private final Region content;
        private boolean expanded = true;

        public CollapsiblePane(Region content) {
            this.content = content;

            // Toggle button
            toggleButton = new Button("-");
            toggleButton.setMaxWidth(Double.MAX_VALUE);

            toggleButton.setOnAction(e -> toggleContent());

            // Initial layout
            getChildren().addAll(toggleButton, content);

            // 25% height binding
            toggleButton.prefHeightProperty().bind(heightProperty().multiply(0.15));

            // Hide from layout when invisible
            content.managedProperty().bind(content.visibleProperty());
        }

        private void toggleContent() {
            expanded = !expanded;
            content.setVisible(expanded);
            toggleButton.setText(expanded ? "-" : "+");
        }
    }

    @Override
    public void start(Stage primaryStage) {
        // Sample content
        VBox contentBox = new VBox(new Label("Hello!"), new Button("Click Me"));
        contentBox.setStyle("-fx-background-color: lightgray; -fx-padding: 10; -fx-border-color: gray;");
        contentBox.setPrefHeight(150); // So it's visible when expanded

        // Create custom pane
        CollapsiblePane collapsiblePane = new CollapsiblePane(contentBox);
        collapsiblePane.setPrefSize(300, 200);

        StackPane root = new StackPane(collapsiblePane);
        Scene scene = new Scene(root, 300, 200);

        primaryStage.setScene(scene);
        primaryStage.setTitle("CollapsiblePane Test");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}