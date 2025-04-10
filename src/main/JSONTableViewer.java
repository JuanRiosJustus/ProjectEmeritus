package main;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

public class JSONTableViewer extends BorderPane {

    private final TableView<JSONObject> tableView = new TableView<>();

    /**
     * Constructs a JSONTableViewerVertical for the given JSONObject.
     * This viewer creates one row for the JSON object and creates one column per key,
     * with the header label rotated 90° (vertical).
     */
    public JSONTableViewer(JSONObject json) {
        // For a single JSONObject, we use a single-row table.
        ObservableList<JSONObject> data = FXCollections.observableArrayList();
        data.add(json);
        tableView.setItems(data);

        // Create one column for each key in the JSONObject.
        for (String key : json.keySet()) {
            TableColumn<JSONObject, Object> col = new TableColumn<>();
            // Create a rotated label for the header.
            Label headerLabel = new Label(key);
            headerLabel.setFont(Font.font("System", 12));
            headerLabel.setMinWidth(20);
            headerLabel.setAlignment(Pos.CENTER);
            col.setGraphic(headerLabel);

            // Extract the cell value from the JSONObject.
            col.setCellValueFactory(cellData -> {
                JSONObject obj = cellData.getValue();
                Object value = obj.opt(key);
                return new SimpleObjectProperty<>(value);
            });

            // Use a custom cell factory to display nested JSON as a "+" button.
            col.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        if (item instanceof JSONObject) {
                            Button expandButton = new Button("+ " + key + " {}");
                            expandButton.setOnAction(e -> showNestedJSON((JSONObject) item, key));
                            setText(null);
                            setGraphic(expandButton);
                        } else if (item instanceof JSONArray) {
                            Button expandButton = new Button("+ " + key + " []");
                            expandButton.setOnAction(e -> showNestedJSON(convertJSONArrayToJSONObject((JSONArray) item), key));
                            setText(null);
                            setGraphic(expandButton);
                        } else {
                            setText(item.toString());
                            setGraphic(null);
                        }
                    }
                }
            });
            tableView.getColumns().add(col);
        }

        setCenter(tableView);
    }

    /**
     * Returns a shortened snippet of text (up to 15 characters) for display on the button.
     */
    private String getSnippet(String text) {
        String trimmed = text.trim();
        return trimmed.length() > 15 ? trimmed.substring(0, 15) + "..." : trimmed;
    }

    /**
     * Converts a JSONArray into a JSONObject by using array indices as keys.
     */
    private JSONObject convertJSONArrayToJSONObject(JSONArray array) {
        JSONObject obj = new JSONObject();
        for (int i = 0; i < array.length(); i++) {
            obj.put(String.valueOf(i), array.get(i));
        }
        return obj;
    }

    /**
     * Opens a modal window displaying the nested JSON in another JSONTableViewerVertical.
     */
    private void showNestedJSON(JSONObject nested, String title) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Nested JSON: " + title);
        JSONTableViewer viewer = new JSONTableViewer(nested);
        Scene scene = new Scene(viewer, 500, 400);
        stage.setScene(scene);
        stage.show();
    }

    // ------------------ Test Application ------------------

    public static class TestApp extends Application {
        @Override
        public void start(Stage primaryStage) {
            // Sample JSON data
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
            Scene scene = new Scene(viewer, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("JSON Table Viewer Vertical");
            primaryStage.show();
        }

        public static void main(String[] args) {
            launch(args);
        }
    }
}