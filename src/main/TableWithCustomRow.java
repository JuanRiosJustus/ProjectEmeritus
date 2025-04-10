package main;

import java.util.Random;
import java.util.function.Function;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TableWithCustomRow extends Application {

    @Override
    public void start(Stage primaryStage) {
        TableView<Item> table = new TableView<>();

        table.setRowFactory(tv -> new TableRow<Item>() {
            Node detailsPane ;
            {
                selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                    if (isNowSelected) {
                        getChildren().add(detailsPane);
                    } else {
                        getChildren().remove(detailsPane);
                    }
                    this.requestLayout();
                });
                detailsPane = createDetailsPane(itemProperty());
            }

            @Override
            protected double computePrefHeight(double width) {
                if (isSelected()) {
                    return super.computePrefHeight(width)+detailsPane.prefHeight(getWidth());
                } else {
                    return super.computePrefHeight(width);
                }
            }

            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                if (isSelected()) {
                    double width = getWidth();
                    double paneHeight = detailsPane.prefHeight(width);
                    detailsPane.resizeRelocate(0, getHeight()-paneHeight, width, paneHeight);
                }
            }
        });

        Random random = new Random();
        for (int i = 1 ; i <= 100 ; i++) {
            table.getItems().add(new Item("Item "+i, random.nextInt(100)));
        }
        table.getColumns().add(column("Item", Item::nameProperty));
        table.getColumns().add(column("Value", Item::valueProperty));

        Scene scene = new Scene(table, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private Node createDetailsPane(ObjectProperty<Item> item) {
        BorderPane detailsPane = new BorderPane();
        Label detailsLabel = new Label();
        VBox labels = new VBox(5, new Label("These are the"), detailsLabel);
        labels.setAlignment(Pos.CENTER_LEFT);
        labels.setPadding(new Insets(2, 2, 2, 16));
        detailsPane.setCenter(labels);

        Label icon = new Label("Icon");
        icon.setStyle("-fx-background-color: aqua; -fx-text-fill: darkgreen; -fx-font-size:18;");
        BorderPane.setMargin(icon, new Insets(6));
        icon.setMinSize(40, 40);
        detailsPane.setLeft(icon);

        detailsPane.setStyle("-fx-background-color: -fx-background; -fx-background: skyblue;");

        item.addListener((obs, oldItem, newItem) -> {
            if (newItem == null) {
                detailsLabel.setText("");
            } else {
                detailsLabel.setText("details for "+newItem.getName());
            }
        });


        return detailsPane ;
    }

    private static <S,T> TableColumn<S,T> column(String title, Function<S, ObservableValue<T>> property) {
        TableColumn<S,T> col = new TableColumn<>(title);
        col.setCellValueFactory(cellData -> property.apply(cellData.getValue()));
        col.setPrefWidth(150);
        return col ;
    }

    public static class Item {
        private final StringProperty name = new SimpleStringProperty() ;
        private final IntegerProperty value = new SimpleIntegerProperty() ;

        public Item(String name, int value) {
            setName(name);
            setValue(value);
        }

        public final StringProperty nameProperty() {
            return this.name;
        }


        public final java.lang.String getName() {
            return this.nameProperty().get();
        }


        public final void setName(final java.lang.String name) {
            this.nameProperty().set(name);
        }


        public final IntegerProperty valueProperty() {
            return this.value;
        }


        public final int getValue() {
            return this.valueProperty().get();
        }


        public final void setValue(final int value) {
            this.valueProperty().set(value);
        }



    }

    public static void main(String[] args) {
        launch(args);
    }
}