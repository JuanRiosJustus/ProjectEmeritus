package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

public class JSONTableViewer extends BorderPane {

    /**
     * Constructs a JSONTableViewerVertical for the given JSONObject.
     * This viewer creates one row for the JSON object and one column per key.
     */
    public JSONTableViewer(JSONObject json) {
        setPadding(new Insets(10));
        ObservableList<JSONObject> data = FXCollections.observableArrayList();
        data.add(json);
        TableView<JSONObject> tableView = new TableView<>();
        tableView.setItems(data);

        // Allow variable row heights.
        tableView.setFixedCellSize(-1);
        // Use unconstrained column resize policy.
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Create one column per key.
        for (String key : json.keySet()) {
            TableColumn<JSONObject, Object> col = new TableColumn<>();
            Label headerLabel = new Label(key);
            headerLabel.setFont(Font.font("System", 12));
            headerLabel.setAlignment(Pos.CENTER);
            col.setGraphic(headerLabel);

            // The cell value is taken from the JSON object.
            col.setCellValueFactory(cellData -> {
                JSONObject obj = cellData.getValue();
                Object value = obj.opt(key);
                return new SimpleObjectProperty<>(value);
            });

            // Use our custom expandable cell.
            col.setCellFactory(column -> new ExpandableJSONTableCell(key));
            tableView.getColumns().add(col);
        }

        setCenter(tableView);
    }

    private static class ExpandableJSONTableCell extends TableCell<JSONObject, Object> {
        private boolean expanded = false;
        private final VBox container = new VBox();
        private final Button toggleButton = new Button();
        private Node nestedViewer = null;
        private final String key;

        public ExpandableJSONTableCell(String key) {
            this.key = key;
            container.setSpacing(5);
            container.setAlignment(Pos.TOP_LEFT);
            container.setFillWidth(true);

            toggleButton.setOnAction(e -> toggleExpansion());


        }

        private void toggleExpansion() {
            expanded = !expanded;
            Object item = getItem();

            // Determine toggle button label (JSON object or array)
            String prettyValue = item instanceof JSONObject ? " {}" :
                    item instanceof JSONArray ? " []" : "";

            if (expanded) {
                JSONObject nested = null;
                if (item instanceof JSONObject jsonObject) {
                    nested = jsonObject;
                } else if (item instanceof JSONArray jsonArray) {
                    nested = convertJSONArrayToJSONObject(jsonArray);
                }

                if (nested != null) {
                    nestedViewer = new JSONTableViewer(nested);
                    nestedViewer.setStyle("-fx-border-color: gray; -fx-background-color: #f9f9f9;");
//                    nestedViewer.setPadding(new Insets(5));
//                    nestedViewer.setMaxWidth(Double.MAX_VALUE);
//                    nestedViewer.setMaxHeight(Double.MAX_VALUE);
                    VBox.setMargin(nestedViewer, new Insets(5));
                }
            } else {
                if (nestedViewer != null) {
                    container.getChildren().remove(nestedViewer);
                    nestedViewer = null;
                }
            }

            // Update button label
            toggleButton.setFont(Font.font("System", FontWeight.BOLD, 14));
            toggleButton.setText((expanded ? "- " : "+ ") + this.key + prettyValue);

            // Rebuild cell contents
            container.getChildren().clear();
            container.getChildren().add(toggleButton);
            if (expanded && nestedViewer != null) {
                container.getChildren().add(nestedViewer);
            }
            setGraphic(container);

            // Force resize and layout pass
            Platform.runLater(() -> {
                container.applyCss();
                container.layout();

                double desiredWidth = container.prefWidth(-1);
                TableColumn<?, ?> col = getTableColumn();
                if (desiredWidth > col.getWidth()) {
                    col.setPrefWidth(desiredWidth + 20);
                }

                updateParentColumns(this, desiredWidth + 20);
                updateParentRows(this);
                getTableRow().requestLayout();
                getTableView().requestLayout();
                getTableView().scrollTo(getIndex());
            });
        }

        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            String prettyValue = item instanceof JSONObject ? " {}" :
                    item instanceof JSONArray ? " []" : null;

            if (prettyValue != null) {
                // JSON object/array – show toggle button
                toggleButton.setFont(Font.font("System", FontWeight.BOLD, 14));
                toggleButton.setText((expanded ? "- " : "+ ") + this.key + prettyValue);
                container.getChildren().clear();
                container.getChildren().add(toggleButton);
                if (expanded && nestedViewer != null) {
                    container.getChildren().add(nestedViewer);
                }
                setGraphic(container);
                setText(null);
            } else {
                // Plain value – show in wrapped label
                Label label = new Label(item.toString());
                label.setWrapText(true);
                label.setMaxWidth(Double.MAX_VALUE);
                label.setMaxHeight(Double.MAX_VALUE);
                label.setStyle("-fx-padding: 5;");
                setGraphic(label);
                setText(null);
            }
        }

        private static JSONObject convertJSONArrayToJSONObject(JSONArray array) {
            JSONObject obj = new JSONObject();
            for (int i = 0; i < array.length(); i++) {
                obj.put(String.valueOf(i), array.get(i));
            }
            return obj;
        }

        private static void updateParentColumns(Node node, double newWidth) {
            Node current = node.getParent();
            while (current != null) {
                if (current instanceof TableCell<?, ?> cell) {
                    TableColumn<?, ?> col = cell.getTableColumn();
                    if (col != null && newWidth > col.getPrefWidth()) {
                        col.setPrefWidth(newWidth);
                    }
                }
                current = current.getParent();
            }
        }

        private static void updateParentRows(Node node) {
            Node current = node.getParent();
            while (current != null) {
                if (current instanceof TableRow<?> row) {
                    row.requestLayout();
                }
                current = current.getParent();
            }
        }
    }

    // Test application for the JSONTableViewerVertical.
    public static class TestApp extends Application {
        @Override
        public void start(Stage primaryStage) {
            String jsonString = """
            {
              "id": 123,
              "name": "Alice",
              "attributes": {
                "health": 100,
                "mana": 50,
                "skills": ["Java", "Python", "JavaScript"]
              },
              "inventory": [
                {"item": "Sword", "damage": 10},
                {"item": "Shield", "defense": 5}
              ],
              "active": true
            }
            """;
            JSONObject json = new JSONObject(jsonString);
            JSONTableViewer viewer = new JSONTableViewer(json);
            Scene scene = new Scene(viewer, 1280, 730);
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.setTitle("JSON Table Viewer Vertical");
            primaryStage.show();
        }

        public static void main(String[] args) {
            launch(args);
        }
    }
}