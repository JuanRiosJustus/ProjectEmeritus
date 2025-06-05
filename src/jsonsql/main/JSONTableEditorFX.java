package jsonsql.main;

import com.alibaba.fastjson2.JSON;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JSONTableEditorFX extends Application {

    private final TabPane tabPane = new TabPane();
    private final JSONDatabase jsonDatabase = new JSONDatabase();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.show();
        tabPane.setTabMinHeight(30);
        tabPane.setTabMaxHeight(30);

        tabPane.getTabs().add(createLandingTab(primaryStage));
        tabPane.getTabs().add(createAddTab());

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null && "➕".equals(newTab.getText())) {
                JSONArray emptyTable = new JSONArray();
                emptyTable.add(new JSONObject()); // One empty row
                Tab newTabInstance = new JSONTableEditorTable("Untitled Table", jsonDatabase);
                tabPane.getTabs().add(tabPane.getTabs().size() - 1, newTabInstance);
                tabPane.getSelectionModel().select(newTabInstance);
            }
        });

        BorderPane root = new BorderPane();

        // Query Bar
        double queryFieldMultiplier = 0.02;
        TextField queryField = new TextField();
        queryField.setPromptText("Enter query...");
        HBox.setHgrow(queryField, Priority.ALWAYS);

        // Bind font size and height proportionally to scene height
        queryField.prefHeightProperty().bind(primaryStage.heightProperty().multiply(queryFieldMultiplier));
        queryField.styleProperty().bind(
                primaryStage.heightProperty().multiply(queryFieldMultiplier).asString("-fx-font-size: %.0fpx;")
        );

        Button submitButton = new Button("Submit");
        submitButton.setMaxHeight(Double.MAX_VALUE);

        HBox queryBar = new HBox(10, queryField, submitButton);
        queryBar.setPadding(new Insets(10));
        queryBar.setAlignment(Pos.CENTER_LEFT);

        // Query Results placeholder
        VBox queryResultsBox = new VBox();
        queryResultsBox.setPadding(new Insets(10));
        queryResultsBox.setSpacing(10);
        queryResultsBox.setAlignment(Pos.TOP_LEFT);

        // SplitPane for query results and table tabs
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.getItems().addAll(queryResultsBox, tabPane);
        splitPane.setDividerPositions(0.25);

        // Submit button populates the top pane with query results
        submitButton.setOnAction(e -> {
            String query = queryField.getText();
            if (query == null || query.isEmpty()) { return; }
            query = query.trim();
            try {
                JSONArray result = jsonDatabase.execute(query);
                if (result == null || result.isEmpty()) { return; }
                JSONObject schema = result.getJSONObject(0);

                CustomTable table = new CustomTable();
                table.setColumns(new ArrayList<>(schema.keySet()));
                table.bindVisibleRowCountToParent(8);

                for (int i = 0; i < result.size(); i++) {
                    JSONObject item = result.getJSONObject(i);
                    table.addRow(item);
                }

                VBox tableContainer = new VBox(table);
                tableContainer.setAlignment(Pos.CENTER_LEFT);
                VBox.setVgrow(table, Priority.ALWAYS); // ✨ this tells the table to expand
                queryResultsBox.getChildren().setAll(tableContainer);
                VBox.setVgrow(tableContainer, Priority.ALWAYS); // ✨ also let the container expand
            } catch (Exception ex) {
                queryResultsBox.getChildren().setAll(new Label("❌ Query Error: " + ex.getMessage()));
            }
        });
        submitButton.prefHeightProperty().bind(primaryStage.heightProperty().multiply(queryFieldMultiplier));
        submitButton.styleProperty().bind(
                primaryStage.heightProperty().multiply(queryFieldMultiplier).asString("-fx-font-size: %.0fpx;")
        );




        VBox centerLayout = new VBox(queryBar, splitPane);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        root.setCenter(centerLayout);

        Scene scene = new Scene(root, 1000, 740);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JSON Table Editor");
        primaryStage.centerOnScreen();
        primaryStage.show();
    }


    private Tab createLandingTab(Stage stage) {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(40));

        Label welcomeLabel = new Label("Welcome to JSON Table Editor");

//        JavaFXUtils.bindFontToSize(welcomeLabel, 12, true);

        welcomeLabel.setFont(new Font("System", 20));

        Button loadButton = new Button("📂 Load JSON");
        Button createButton = new Button("✨ Create Table From Scratch");

        loadButton.setOnAction(e -> openNewJsonFile(stage));


        createButton.setOnAction(e -> {

            String tableName = "untitled_table";
            jsonDatabase.addTable(tableName);

            Tab newTab = new JSONTableEditorTable(tableName, jsonDatabase);

            tabPane.getTabs().add(tabPane.getTabs().size() - 1, newTab);
            tabPane.getSelectionModel().select(newTab);
        });

        container.getChildren().addAll(welcomeLabel, loadButton, createButton);
        Tab landingTab = new Tab("🏠 Home", container);
        landingTab.setClosable(false);
        return landingTab;
    }

    private Tab createAddTab() {
        Tab tab = new Tab("➕");
        tab.setClosable(false);
        return tab;
    }

    private void openNewJsonFile(Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open JSON Directory");
        File selected = directoryChooser.showDialog(stage);

        if (selected == null) { return; }

        File[] jsonFiles = selected.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
        if (jsonFiles != null) {
            for (File file : jsonFiles) {
                loadJsonFileAsTab(file);
            }
        }
    }




    private void loadJsonFileAsTab(File file) {
        try {
            String content = Files.readString(file.toPath());

            // Register table with JSONDatabase
            String tableName = file.getName().replace(".json", "");
            jsonDatabase.addTable(tableName, content);

            Tab newTab = new JSONTableEditorTable(tableName, jsonDatabase);
            newTab.setText(tableName);

            tabPane.getTabs().add(tabPane.getTabs().size() - 1, newTab);
            tabPane.getSelectionModel().select(newTab);

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Loading JSON");
            alert.setHeaderText("Could not load file: " + file.getName());
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
