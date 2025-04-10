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

public class JSONTableViewerVertical extends BorderPane {

    /**
     * Constructs a JSONTableViewerVertical for the given JSONObject.
     * This viewer creates one row for the JSON object and one column per key.
     */
    public JSONTableViewerVertical(JSONObject json) {
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

    /**
     * Custom TableCell that supports inline expansion of nested JSON.
     */
    private static class ExpandableJSONTableCell extends TableCell<JSONObject, Object> {
        private boolean expanded = false;
        private final VBox container = new VBox();
        private final Button toggleButton = new Button();
        private Node nestedViewer = null;
        private final String key;

        public ExpandableJSONTableCell(String key) {
            this.key = key;
            container.setSpacing(5);
            container.setAlignment(Pos.CENTER_LEFT);
            // Set the toggle action
            toggleButton.setOnAction(e -> toggleExpansion());
        }

        private void toggleExpansion() {
            expanded = !expanded;
            Object item = getItem();
            String prettyValue = "";
            if (item instanceof JSONObject) {
                prettyValue = " {}";
            } else if (item instanceof JSONArray) {
                prettyValue = " []";
            }
            if (expanded) {
                // If item is a JSONObject or JSONArray, convert accordingly.
                JSONObject nested = null;
                if (item instanceof JSONObject) {
                    nested = (JSONObject) item;
                } else if (item instanceof JSONArray) {
                    nested = convertJSONArrayToJSONObject((JSONArray) item);
                }
                if (nested != null) {
                    // Create a nested viewer for the nested JSON.
                    nestedViewer = new JSONTableViewerVertical(nested);
                    nestedViewer.setStyle("-fx-border-color: gray;");
                    // Add the nested viewer to the container.
                    container.getChildren().add(nestedViewer);
                    toggleButton.setFont(Font.font("System", FontWeight.BOLD, 14));
                    toggleButton.setText("- " + this.key + prettyValue);
                }
            } else {
                // Collapse: remove nested viewer.
                container.getChildren().remove(nestedViewer);
                container.layout();

                nestedViewer = null;
                toggleButton.setFont(Font.font("System", FontWeight.BOLD, 14));
                toggleButton.setText(" + " + this.key + prettyValue);
            }
            // Always clear and add the toggle button (and nested viewer if expanded).
            container.getChildren().clear();
            container.getChildren().add(toggleButton);
            if (expanded && nestedViewer != null) {
                container.getChildren().add(nestedViewer);
            }
            setGraphic(container);

            // Force a layout pass then update the column width if necessary.
            Platform.runLater(() -> {
                container.applyCss();
                container.layout();
                double desiredWidth = container.prefWidth(-1);
                TableColumn<?, ?> col = getTableColumn();
                if (desiredWidth > col.getPrefWidth()) {
                    col.setPrefWidth(desiredWidth);
                }
                // Recursively update parent columns.
                updateParentColumns(this, desiredWidth);
                getTableView().requestLayout();
            });
        }

        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                String prettyValue = null;
                if (item instanceof JSONObject) {
                    prettyValue = " {}";
                } else if (item instanceof JSONArray) {
                    prettyValue = " []";
                }

                if (prettyValue != null) {
                    // Show the toggle button with a snippet.
                    toggleButton.setFont(Font.font("System", FontWeight.BOLD, 14));
                    toggleButton.setText("+ " + this.key + prettyValue);
                    container.getChildren().clear();
                    container.getChildren().add(toggleButton);
                    setGraphic(container);
                    setText(null);
                } else {
                    setText(item.toString());
                    setGraphic(null);
                }
            }
        }

        private String getSnippet(String text) {
            text = text.trim();
            return text.length() > 15 ? text.substring(0, 15) + "..." : text;
        }

        private static JSONObject convertJSONArrayToJSONObject(JSONArray array) {
            JSONObject obj = new JSONObject();
            for (int i = 0; i < array.length(); i++) {
                obj.put(String.valueOf(i), array.get(i));
            }
            return obj;
        }

        /**
         * Recursively update any parent TableCell’s column widths.
         */
        private static void updateParentColumns(Node node, double newWidth) {
            Node current = node.getParent();
            while (current != null) {
                if (current instanceof TableCell) {
                    TableCell<?, ?> cell = (TableCell<?, ?>) current;
                    TableColumn<?, ?> col = cell.getTableColumn();
                    if (col != null && newWidth > col.getPrefWidth()) {
                        col.setPrefWidth(newWidth);
                    }
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
            JSONTableViewerVertical viewer = new JSONTableViewerVertical(json);
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