package main.ui.custom;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UpwardGrowingUI extends Application {

    public static class Pane extends VBox {
        private double mItemHeight;
        private double mItemWidth;
        private final double mBaseY;
        private final double mBaseX;

        public Pane(double baseX, double baseY, double itemWidth, double itemHeight) {
            mBaseY = baseY;
            mBaseX = baseX;

            mItemWidth = itemWidth;
            mItemHeight = itemHeight;

            setLayoutX(baseX);
            setLayoutY(baseY);
            setPrefHeight(0);
        }

        public double getItemWidth() { return mItemWidth; }
        public double getItemHeight() { return mItemHeight; }

        public void addRow(Node row) {
            getChildren().addFirst(row);

            double newHeight = getChildren().size() * mItemHeight;
            setPrefHeight(newHeight);
            setLayoutY(mBaseY - newHeight);
        }

        public void clearRows() {
            getChildren().clear();
            setPrefHeight(0);
            setLayoutY(mBaseY);
        }

        public void setItemSize(int width, int height) {
            mItemWidth = width;
            mItemHeight = height;
            clearRows();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        javafx.scene.layout.Pane root = new javafx.scene.layout.Pane();
        root.setPrefSize(400, 400);

        Pane growingPane = new Pane(100, 300, 200, 30);
        growingPane.setStyle("-fx-border-color: black; -fx-background-color: #eeeeee;");
        root.getChildren().add(growingPane);

        Button addButton = new Button("Add HBox");
        addButton.setLayoutX(100);
        addButton.setLayoutY(20);
        addButton.setOnAction(e -> {
            HBox row = new HBox(10);
            row.getChildren().addAll(
                    new Label("Label " + (growingPane.getChildren().size() + 1)),
                    new Button("Action")
            );
            growingPane.addRow(row);
        });

        root.getChildren().add(addButton);

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("UpwardGrowingPane Demo");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}