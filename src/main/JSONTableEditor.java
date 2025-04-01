package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class JSONTableEditor extends Application {

    @Override
    public void start(Stage primaryStage) {
        TableView<Map<String, Object>> tableView = new TableView<>();

//        // Sample JSON Data
//        String jsonString = """
//            [
//              { "name": "Alice", "age": 30, "city": "New York" },
//              { "name": "Bob", "age": 25, "city": "Los Angeles" },
//              { "name": "Charlie", "age": 35, "city": "Chicago" }
//            ]
//        """;
//
//        JSONArray jsonArray = new JSONArray(jsonString);
//
//        if (jsonArray.length() > 0) {
//            JSONObject first = jsonArray.getJSONObject(0);
//
//            // Dynamically create columns from the keys
//            for (String key : first.keySet()) {
//                TableColumn<Map<String, Object>, Object> col = new TableColumn<>(key);
////                col.setCellValueFactory(new MapValueFactory<>(key));
//                tableView.getColumns().add(col);
//            }
//
//            // Add data rows
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                Map<String, Object> row = new HashMap<>();
//                for (String key : jsonObject.keySet()) {
//                    row.put(key, jsonObject.get(key));
//                }
//                tableView.getItems().add(row);
//            }
//        }

        VBox root = new VBox(tableView);
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JSON Table Viewer");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}