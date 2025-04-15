package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JsonGridEditorFX extends Application {

    private TableView<JSONObject> table = new TableView<>();
    private JSONArray jsonArray = new JSONArray();
    private final Map<String, Boolean> expansionState = new HashMap<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        jsonArray.put(new JSONObject("{" +
                "\"name\": \"Alice\", \"age\": 25, " +
                "\"address\": {\"city\": \"Springfield\", \"zip\": \"12345\", " +
                "\"location\": {\"lat\": 40.7128, \"lng\": -74.0060}}, " +
                "\"phones\": [\"123-4567\", \"987-6543\"], " +
                "\"projects\": [" +
                "  {\"title\": \"Project A\", \"tasks\": [\"Design\", \"Implement\"]}," +
                "  {\"title\": \"Project B\", \"tasks\": [\"Plan\", \"Test\"]}" +
                "]}"));

        jsonArray.put(new JSONObject("{" +
                "\"name\": \"Bob\", \"age\": 30, " +
                "\"address\": {\"city\": \"Shelbyville\", \"zip\": \"67890\", " +
                "\"location\": {\"lat\": 41.0000, \"lng\": -75.0000}}, " +
                "\"phones\": [\"555-1212\"], " +
                "\"projects\": [" +
                "  {\"title\": \"Project C\", \"tasks\": [\"Research\", \"Deploy\"]}" +
                "]}"));

        loadTable(jsonArray, table);

        VBox root = new VBox(table);
        VBox.setVgrow(table, Priority.ALWAYS);
        root.setPrefSize(1000, 800);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("JSON Grid Editor - Recursive Nested Table");
        primaryStage.show();
    }

    private void loadTable(JSONArray array, TableView<JSONObject> targetTable) {
        targetTable.getColumns().clear();
        targetTable.getItems().clear();

        if (array.length() == 0) return;

        JSONObject sample = array.getJSONObject(0);

        for (String key : sample.keySet()) {
            TableColumn<JSONObject, Object> column = new TableColumn<>(key);
            column.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().opt(key)));
            column.setCellFactory(col -> new TableCell<>() {
                private final VBox container = new VBox();
                private final HBox header = new HBox();
                private final Label label = new Label();
                private final Button toggleBtn = new Button();
                private Node nestedNode;

                {
                    container.setSpacing(5);
                    container.setPadding(new Insets(5));
                    header.setSpacing(5);
                    header.getChildren().addAll(toggleBtn, label);
                    container.getChildren().add(header);

                    toggleBtn.setOnAction(e -> {

                        String mapKey = getIndex() + ":" + getTableColumn().getText();
                        boolean isExpanded = expansionState.getOrDefault(mapKey, false);
                        if (isExpanded) {
                            container.getChildren().remove(nestedNode);
                            toggleBtn.setText("+");
                        } else {
                            if (!container.getChildren().contains(nestedNode)) {
                                container.getChildren().add(nestedNode);
                            }
                            toggleBtn.setText("−");

                            if (nestedNode instanceof Region region) {
                                region.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
                                    TableRow<?> row = getTableRow();
                                    if (row != null) {
                                        double nestedHeight = region.getLayoutBounds().getHeight();
                                        double padding = container.getInsets().getTop() + container.getInsets().getBottom();
                                        row.setPrefHeight(nestedHeight + padding + 50);
                                        row.requestLayout();
                                    }

                                    if (region instanceof TableView<?> nestedTable) {
                                        double totalPrefWidth = nestedTable.getColumns().stream()
                                                .mapToDouble(col -> {
                                                    double w = col.getWidth();
                                                    return w > 0 ? w : 100;
                                                })
                                                .sum();
                                        getTableColumn().setPrefWidth(Math.max(getTableColumn().getPrefWidth(), totalPrefWidth + 60));
                                    }

                                    propagateLayoutUp(container);
                                });
                            }
                        }
                        expansionState.put(mapKey, !isExpanded);

                        Platform.runLater(() -> {
                            container.applyCss();
                            container.layout();
                            propagateLayoutUp(container);
                        });
//                        propagateLayoutUp(container);
                    });
                }

                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setGraphic(null);
                        return;
                    }

                    container.getChildren().clear();
                    header.getChildren().setAll(toggleBtn, label);
                    container.getChildren().add(header);

                    if (item instanceof JSONObject || item instanceof JSONArray) {
                        label.setText("[Nested " + item.getClass().getSimpleName() + "]");
                        String mapKey = getIndex() + ":" + getTableColumn().getText();
                        boolean isExpanded = expansionState.getOrDefault(mapKey, false);
                        toggleBtn.setText(isExpanded ? "−" : "+");

                        if (nestedNode == null) {
                            nestedNode = createNestedNode(item);
                        }
                        if (isExpanded && !container.getChildren().contains(nestedNode)) {
                            container.getChildren().add(nestedNode);
                        }

                        setGraphic(container);
                    } else {
                        label.setText(item.toString());
                        setGraphic(label);
                    }
                }
            });
            targetTable.getColumns().add(column);
        }

        for (int i = 0; i < array.length(); i++) {
            targetTable.getItems().add(array.getJSONObject(i));
        }

        targetTable.setFixedCellSize(-1);
        targetTable.setStyle("-fx-cell-size: -1;");
    }

    private Node createNestedNode(Object item) {
        if (item instanceof JSONObject obj) {
            TableView<JSONObject> nestedTable = new TableView<>();
            JSONArray arr = new JSONArray();
            arr.put(obj);
            loadTable(arr, nestedTable);

            // Resize and disable scrollbars
            nestedTable.setPrefWidth(Region.USE_COMPUTED_SIZE);
            nestedTable.setMinWidth(Region.USE_COMPUTED_SIZE);
            nestedTable.setMaxWidth(Double.MAX_VALUE);
            nestedTable.setPrefHeight(Region.USE_COMPUTED_SIZE);
            nestedTable.setMinHeight(Region.USE_COMPUTED_SIZE);
            nestedTable.setMaxHeight(Double.MAX_VALUE);
            nestedTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

            disableScrollBars(nestedTable);
            VBox.setVgrow(nestedTable, Priority.ALWAYS);
            return nestedTable;

        } else if (item instanceof JSONArray arr) {
            TableView<JSONObject> nestedTable = new TableView<>();
            JSONArray wrapped = new JSONArray();

            if (arr.length() > 0 && arr.get(0) instanceof JSONObject) {
                loadTable(arr, nestedTable);
            } else {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject wrapper = new JSONObject();
                    wrapper.put("value", arr.get(i));
                    wrapped.put(wrapper);
                }
                loadTable(wrapped, nestedTable);
            }

            nestedTable.setPrefWidth(Region.USE_COMPUTED_SIZE);
            nestedTable.setMinWidth(Region.USE_COMPUTED_SIZE);
            nestedTable.setMaxWidth(Double.MAX_VALUE);
            nestedTable.setPrefHeight(Region.USE_COMPUTED_SIZE);
            nestedTable.setMinHeight(Region.USE_COMPUTED_SIZE);
            nestedTable.setMaxHeight(Double.MAX_VALUE);
            nestedTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

            disableScrollBars(nestedTable);
            VBox.setVgrow(nestedTable, Priority.ALWAYS);
            return nestedTable;
        }

        return new Label("Unsupported nested type");
    }

    private void propagateLayoutUp(Node node) {
        while (node != null) {
            if (node instanceof TableRow<?> row) {
                row.setPrefHeight(Region.USE_COMPUTED_SIZE);
                row.requestLayout();
            }

            if (node instanceof TableCell<?, ?> cell) {
                Region region = (Region) cell.getGraphic();
                if (region != null) {
                    double width = region.prefWidth(-1);
                    double height = region.prefHeight(-1);

                    TableColumn<?, ?> col = cell.getTableColumn();
                    if (col != null && width > col.getPrefWidth()) {
                        col.setPrefWidth(width + 20);
                    }

                    TableRow<?> row = cell.getTableRow();
                    if (row != null && height > row.getPrefHeight()) {
                        row.setPrefHeight(height + 20);
                    }
                }
            }

            if (node instanceof TableView<?> tv) {
                tv.applyCss();
                tv.layout();
                tv.refresh();
            }

            node = node.getParent();
        }
    }

    private void disableScrollBars(TableView<?> tableView) {
        tableView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            Platform.runLater(() -> {
                tableView.lookupAll(".scroll-bar").forEach(sb -> sb.setVisible(false));
            });
        });
    }
}