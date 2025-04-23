package jsonsql.main;

import com.alibaba.fastjson2.JSON;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.io.File;
import java.nio.file.Files;
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
                Tab newTabInstance = new JSONTableEditorTable("Untitled Table", result);
                queryResultsBox.getChildren().setAll(newTabInstance.getContent());
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

//        welcomeLabel.setFont(new Font("System", 20));

        Button loadButton = new Button("📂 Load JSON");
        Button createButton = new Button("✨ Create Table From Scratch");

        loadButton.setOnAction(e -> openNewJsonFile(stage));

        createButton.setOnAction(e -> {
            JSONArray emptyTable = new JSONArray();
            emptyTable.add(new JSONObject());
            Tab newTab = new JSONTableEditorTable("Untitled Table", jsonDatabase);
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
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open JSON File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON Files", "*.json"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open JSON Directory");

        Alert choiceAlert = new Alert(Alert.AlertType.CONFIRMATION);
        choiceAlert.setTitle("Open JSON Source");
        choiceAlert.setHeaderText("Would you like to open a single file or an entire directory?");
        ButtonType fileButton = new ButtonType("Single File");
        ButtonType dirButton = new ButtonType("Directory");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        choiceAlert.getButtonTypes().setAll(fileButton, dirButton, cancelButton);

        Optional<ButtonType> result = choiceAlert.showAndWait();
        if (result.isEmpty() || result.get() == cancelButton) return;

        if (result.get() == fileButton) {
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                loadJsonFileAsTab(selectedFile);
            }
        } else if (result.get() == dirButton) {
            File selectedDir = directoryChooser.showDialog(stage);
            if (selectedDir != null && selectedDir.isDirectory()) {
                File[] jsonFiles = selectedDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
                if (jsonFiles != null) {
                    for (File file : jsonFiles) {
                        loadJsonFileAsTab(file);
                    }
                }
            }
        }
    }

    private void loadJsonFileAsTab(File file) {
        try {
            String content = Files.readString(file.toPath());
            JSONArray jsonArray = JSON.parseArray(content);

            // Register table with JSONDatabase
            String tableName = file.getName().replace(".json", "");
            jsonDatabase.addTable(tableName, content);

            Tab newTab = new JSONTableEditorTable(tableName, jsonDatabase);
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
