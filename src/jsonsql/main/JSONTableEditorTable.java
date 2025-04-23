// Updated JsonTableEditorTab to simplify operations: Add Row, Add Column, Delete Row, Delete Column
package jsonsql.main;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.converter.DefaultStringConverter;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import main.constants.JavaFXUtils;
import main.game.stores.FontPool;
import main.game.stores.FontPoolV2;
import main.ui.foundation.BeveledButton;

import java.util.*;

public class JSONTableEditorTable extends Tab {

    private boolean mEditMode = false;
    private boolean mEditFields = false;
    private TableView<Map<String, Object>> mBaseTable = null;
    private List<Map<String, Object>> mFlattenedObjects = null;
    private Set<String> mColumns = new HashSet<>();
    private Button mAddRowButton = new Button("+Row");
    private Button mAddColButton = new Button("+Col");
    private Button mDelRowButton = new Button("-Row");
    private Button mDelColButton = new Button("-Col");
    private Button saveButton = new Button("Save");
    private ToggleButton mToggleEditSchema = new ToggleButton("Edit Schema");
    private ToggleButton mToggleEditHeaders = new ToggleButton("Edit Headers");
    private Font mTableEditorFont = null;

    public JSONTableEditorTable(String title, JSONArray results) {
        mColumns = new LinkedHashSet<>();
        mFlattenedObjects = new ArrayList<>();

        mAddRowButton = new Button("+Row");
        mAddColButton = new Button("+Col");
        mDelRowButton = new Button("-Row");
        mDelColButton = new Button("-Col");
        saveButton = new Button("Save");

        mToggleEditSchema = new ToggleButton("Edit Schema");
        mToggleEditHeaders = new ToggleButton("Edit Headers");

        mBaseTable = new TableView<>();
        mBaseTable.setEditable(true);

        mBaseTable.getSelectionModel().setCellSelectionEnabled(true);
        mBaseTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        mTableEditorFont = new Font("System", 13);

        JSONArray flattenedRows = results;
        for (int i = 0; i < flattenedRows.size(); i++) {
            JSONObject obj = flattenedRows.getJSONObject(i);
            mFlattenedObjects.add(obj);
            mColumns.addAll(obj.keySet());
        }

        JSONTableColumn rowNumberColumn = createRowNumberColumn(mBaseTable);
        mBaseTable.getColumns().add(rowNumberColumn);

        for (String columnName : mColumns) {
            JSONTableColumn baseTableColumn = setupTableColumn(columnName);
            mBaseTable.getColumns().add(baseTableColumn);
        }

        mBaseTable.getItems().addAll(mFlattenedObjects);

        mAddRowButton.setDisable(true);
        mAddColButton.setDisable(true);
        mDelRowButton.setDisable(true);
        mDelColButton.setDisable(true);
        saveButton.setDisable(false);

        mToggleEditHeaders.setDisable(true);

        mToggleEditSchema.setOnAction(e -> {
            mEditMode = mToggleEditSchema.isSelected();
            mToggleEditHeaders.setDisable(!mEditMode);
            mAddRowButton.setDisable(!mEditMode);
            mAddColButton.setDisable(!mEditMode);
            mDelRowButton.setDisable(!mEditMode);
            mDelColButton.setDisable(!mEditMode);
            mBaseTable.setEditable(!mEditMode);
            mBaseTable.getSelectionModel().clearSelection();
        });

        mToggleEditHeaders.setOnAction(e -> mEditFields = mToggleEditHeaders.isSelected());

        mAddRowButton.setOnAction(e -> {
            if (!mEditMode || mEditFields) return;
            TablePosition<Map<String, Object>, ?> pos = mBaseTable.getSelectionModel().getSelectedCells().stream().findFirst().orElse(null);
            int row = (pos != null) ? pos.getRow() : mBaseTable.getItems().size() - 1;
            row = Math.max(0, row);

            Map<String, Object> newRow = new LinkedHashMap<>();
            for (String column : mColumns) {
                newRow.put(column, "");
            }
            mBaseTable.getItems().add(row + 1, newRow);
        });

        mDelRowButton.setOnAction(e -> {
            if (!mEditMode || mEditFields) return;
            TablePosition<Map<String, Object>, ?> pos = mBaseTable.getSelectionModel().getSelectedCells().stream().findFirst().orElse(null);
            if (pos != null) {
                int row = pos.getRow();
                if (row >= 0 && row < mBaseTable.getItems().size()) {
                    mBaseTable.getItems().remove(row);
                }
            }
        });

        mAddColButton.setOnAction(e -> {
            if (!mEditMode || mEditFields) return;

            TablePosition<?, ?> pos = mBaseTable.getSelectionModel().getSelectedCells().stream().findFirst().orElse(null);

            // Clamp the insert index safely to within bounds of the column list
            int col = pos != null ? pos.getColumn() : mBaseTable.getColumns().size() - 1;
            int insertIndex = Math.max(1, Math.min(col + 1, mBaseTable.getColumns().size()));

            String newColName = "new_col_" + mBaseTable.getColumns().size();
            JSONTableColumn newCol = setupTableColumn(newColName);

            // Add the column and ensure rows get the field
            mBaseTable.getColumns().add(insertIndex, newCol);

            for (Map<String, Object> row : mBaseTable.getItems()) {
                row.put(newColName, "");
            }

            mColumns.add(newColName);
        });


        mDelColButton.setOnAction(e -> {
            if (!mEditMode || mEditFields) return;
            TablePosition<Map<String, Object>, ?> pos = mBaseTable.getSelectionModel().getSelectedCells().stream().findFirst().orElse(null);
            if (pos != null) {
                int col = pos.getColumn();
                if (col > 0 && col < mBaseTable.getColumns().size()) {
                    mBaseTable.getColumns().remove(col);
                }
            }
        });

        saveButton.setOnAction(e -> {
            System.out.println("Saving current table state:");
            mBaseTable.getItems().forEach(System.out::println);
        });

        // Let buttons grow and fill evenly
        for (ButtonBase btn : List.of(
                mToggleEditSchema, mToggleEditHeaders,
                mAddRowButton, mDelRowButton,
                mAddColButton, mDelColButton,
                saveButton)) {

            HBox.setHgrow(btn, Priority.ALWAYS);   // Make them grow with parent
            btn.setMaxWidth(Double.MAX_VALUE);     // Allow full width
            btn.setMaxHeight(Double.MAX_VALUE);    // Allow full height
        }



        VBox baseSection = new VBox(mBaseTable);
        VBox.setVgrow(mBaseTable, Priority.ALWAYS);

        setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Close Tab");
            alert.setHeaderText("Are you sure you want to close this tab?");
            alert.setContentText("Unsaved changes will be lost.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                event.consume();
            }
        });

        setText(title);
//        setText(null);


//        BeveledButton b = new BeveledButton(200, 200);
//        b.setFitText(title);
//        setGraphic(title);

        setContent(baseSection);
        setClosable(true);
    }

    public JSONTableEditorTable(String title, JSONDatabase database) {
        mColumns = new LinkedHashSet<>();
        mFlattenedObjects = new ArrayList<>();

        mAddRowButton = new Button("+Row");
        mAddColButton = new Button("+Col");
        mDelRowButton = new Button("-Row");
        mDelColButton = new Button("-Col");
        saveButton = new Button("Save");

        mToggleEditSchema = new ToggleButton("Edit Schema");
        mToggleEditHeaders = new ToggleButton("Edit Headers");

        mBaseTable = new TableView<>();
        mBaseTable.setEditable(true);

        mBaseTable.getSelectionModel().setCellSelectionEnabled(true);
        mBaseTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        mTableEditorFont = new Font("System", 13);

        JSONArray rows = database.execute("SELECT * FROM " + title );
        JSONArray flattenedRows = JsonTableUtilities.flattenJSONArray(rows);
        for (int i = 0; i < flattenedRows.size(); i++) {
            JSONObject obj = flattenedRows.getJSONObject(i);
            mFlattenedObjects.add(obj);
            mColumns.addAll(obj.keySet());
        }

        JSONTableColumn rowNumberColumn = createRowNumberColumn(mBaseTable);
        mBaseTable.getColumns().add(rowNumberColumn);

        for (String columnName : mColumns) {
            JSONTableColumn baseTableColumn = setupTableColumn(columnName);
            mBaseTable.getColumns().add(baseTableColumn);
        }

        mBaseTable.getItems().addAll(mFlattenedObjects);

        mAddRowButton.setDisable(true);
        mAddColButton.setDisable(true);
        mDelRowButton.setDisable(true);
        mDelColButton.setDisable(true);
        saveButton.setDisable(false);

        mToggleEditHeaders.setDisable(true);

        mToggleEditSchema.setOnAction(e -> {
            mEditMode = mToggleEditSchema.isSelected();
            mToggleEditHeaders.setDisable(!mEditMode);
            mAddRowButton.setDisable(!mEditMode);
            mAddColButton.setDisable(!mEditMode);
            mDelRowButton.setDisable(!mEditMode);
            mDelColButton.setDisable(!mEditMode);
            mBaseTable.setEditable(!mEditMode);
            mBaseTable.getSelectionModel().clearSelection();
        });

        mToggleEditHeaders.setOnAction(e -> mEditFields = mToggleEditHeaders.isSelected());

        mAddRowButton.setOnAction(e -> {
            if (!mEditMode || mEditFields) return;
            TablePosition<Map<String, Object>, ?> pos = mBaseTable.getSelectionModel().getSelectedCells().stream().findFirst().orElse(null);
            int row = (pos != null) ? pos.getRow() : mBaseTable.getItems().size() - 1;
            row = Math.max(0, row);

            Map<String, Object> newRow = new LinkedHashMap<>();
            for (String column : mColumns) {
                newRow.put(column, "");
            }
            mBaseTable.getItems().add(row + 1, newRow);
        });

        mDelRowButton.setOnAction(e -> {
            if (!mEditMode || mEditFields) return;
            TablePosition<Map<String, Object>, ?> pos = mBaseTable.getSelectionModel().getSelectedCells().stream().findFirst().orElse(null);
            if (pos != null) {
                int row = pos.getRow();
                if (row >= 0 && row < mBaseTable.getItems().size()) {
                    mBaseTable.getItems().remove(row);
                }
            }
        });

        mAddColButton.setOnAction(e -> {
            if (!mEditMode || mEditFields) return;

            TablePosition<?, ?> pos = mBaseTable.getSelectionModel().getSelectedCells().stream().findFirst().orElse(null);

            // Clamp the insert index safely to within bounds of the column list
            int col = pos != null ? pos.getColumn() : mBaseTable.getColumns().size() - 1;
            int insertIndex = Math.max(1, Math.min(col + 1, mBaseTable.getColumns().size()));

            String newColName = "new_col_" + mBaseTable.getColumns().size();
            JSONTableColumn newCol = setupTableColumn(newColName);

            // Add the column and ensure rows get the field
            mBaseTable.getColumns().add(insertIndex, newCol);

            for (Map<String, Object> row : mBaseTable.getItems()) {
                row.put(newColName, "");
            }

            mColumns.add(newColName);
        });


        mDelColButton.setOnAction(e -> {
            if (!mEditMode || mEditFields) return;
            TablePosition<Map<String, Object>, ?> pos = mBaseTable.getSelectionModel().getSelectedCells().stream().findFirst().orElse(null);
            if (pos != null) {
                int col = pos.getColumn();
                if (col > 0 && col < mBaseTable.getColumns().size()) {
                    mBaseTable.getColumns().remove(col);
                }
            }
        });

        saveButton.setOnAction(e -> {
            System.out.println("Saving current table state:");
            mBaseTable.getItems().forEach(System.out::println);
        });

        HBox operations = new HBox(6,
                mToggleEditSchema,
                mToggleEditHeaders,
                mAddRowButton,
                mDelRowButton,
                mAddColButton,
                mDelColButton,
                saveButton
        );
        operations.setPadding(new Insets(6));

        // Let buttons grow and fill evenly
        for (ButtonBase btn : List.of(
                mToggleEditSchema, mToggleEditHeaders,
                mAddRowButton, mDelRowButton,
                mAddColButton, mDelColButton,
                saveButton)) {

            HBox.setHgrow(btn, Priority.ALWAYS);   // Make them grow with parent
            btn.setMaxWidth(Double.MAX_VALUE);     // Allow full width
            btn.setMaxHeight(Double.MAX_VALUE);    // Allow full height
        }

        // Dynamically bind font size based on height of the button container
        operations.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            double fontSize = newHeight.doubleValue() * 0.35; // Tune the multiplier as needed
            Font font = FontPoolV2.getInstance().getFont(fontSize);

            for (ButtonBase button : List.of(
                    mToggleEditSchema,
                    mToggleEditHeaders,
                    mAddRowButton,
                    mDelRowButton,
                    mAddColButton,
                    mDelColButton,
                    saveButton
            )) {
                button.setFont(font);
            }
        });



        VBox baseSection = new VBox(operations, mBaseTable);
        VBox.setVgrow(mBaseTable, Priority.ALWAYS);

        setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Close Tab");
            alert.setHeaderText("Are you sure you want to close this tab?");
            alert.setContentText("Unsaved changes will be lost.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                event.consume();
            }
        });

        setText(title);
//        setText(null);


//        BeveledButton b = new BeveledButton(200, 200);
//        b.setFitText(title);
//        setGraphic(title);

        setContent(baseSection);
        setClosable(true);
    }

    private JSONTableColumn setupTableColumn(String columnName) {
        JSONTableColumn baseTableColumn = new JSONTableColumn(columnName, (int) mTableEditorFont.getSize());
        JSONTableColumn.Header baseTableHeader = baseTableColumn.getHeader();

        baseTableHeader.setOnMouseClicked(e -> {
            boolean shouldEditHeader = mEditMode && mEditFields && e.getButton() == MouseButton.PRIMARY;

            if (shouldEditHeader) {
                handleEditingHeader(baseTableColumn, mFlattenedObjects);
            } else if (e.getButton() == MouseButton.PRIMARY) {
                mBaseTable.getSelectionModel().clearSelection();
                for (int row = 0; row < mBaseTable.getItems().size(); row++) {
                    mBaseTable.getSelectionModel().select(row, baseTableColumn);
                }
            }
        });

        baseTableColumn.setCellValueFactory(data -> {
            Object val = data.getValue().get(columnName);
            return new ReadOnlyObjectWrapper<>(val == null ? "" : val.toString());
        });


        baseTableColumn.setCellFactory(column -> {
            TableCell<Map<String, Object>, String> cell = new TextFieldTableCell<>(new DefaultStringConverter()) {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty && item != null && JsonTableUtilities.isNumber(item)) {
                        setAlignment(Pos.CENTER_RIGHT);
                    } else {
                        setAlignment(Pos.CENTER);
                    }
                }
            };

            cell.setOnMouseClicked(e -> {
                boolean shouldEditHeader = mEditMode && e.getButton() == MouseButton.PRIMARY;
                if (!shouldEditHeader) return;
                handleEditingHeader(baseTableColumn, mFlattenedObjects);
            });
            return cell;
        });

        double maxCellWidth = getMaxColumnWidth(mFlattenedObjects, columnName, baseTableHeader.getLabel().getFont());
        baseTableColumn.setPrefWidth(maxCellWidth);
        baseTableColumn.setMinWidth(maxCellWidth);
        return baseTableColumn;
    }

    private static void handleEditingHeader(JSONTableColumn column, List<Map<String, Object>> flattenedObjects) {
        JSONTableColumn.Header header = column.getHeader();
        Label label = header.getLabel();
        TextInputDialog dialog = new TextInputDialog(label.getText());
        dialog.setTitle("Rename Column");
        dialog.setHeaderText("Rename Column");
        dialog.setContentText("New name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(e -> {
            label.setText(e);
            double maxCellWidth = getMaxColumnWidth(flattenedObjects, label.getText(), label.getFont());
            column.setPrefWidth(maxCellWidth);
            column.setMinWidth(maxCellWidth);
        });
    }

    private JSONTableColumn createRowNumberColumn(TableView<Map<String, Object>> table) {
        JSONTableColumn indexColumn = new JSONTableColumn("", (int) mTableEditorFont.getSize());
        indexColumn.setPrefWidth(40);
        indexColumn.setSortable(false);
        indexColumn.setReorderable(false);
        indexColumn.setEditable(false);

        JSONTableColumn.Header customHeader = indexColumn.getHeader();
        indexColumn.setText(null);
        indexColumn.setGraphic(customHeader);

        indexColumn.setCellFactory(col -> {
            TableCell<Map<String, Object>, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : String.valueOf(getIndex() + 1));
                }
            };

            cell.setAlignment(Pos.CENTER);

            cell.setOnMouseClicked(e -> {
                if (!cell.isEmpty() && e.getButton() == MouseButton.PRIMARY) {
                    table.getSelectionModel().clearSelection();
                    table.getSelectionModel().select(cell.getIndex());
                }
            });

            return cell;
        });

        return indexColumn;
    }

    private static double getMaxColumnWidth(List<Map<String, Object>> flattenedObjects, String columnName, Font headerFont) {
        double padding = 20;

        double headerTextWidth = JsonTableUtilities.computeTextWidth(headerFont, columnName) + padding;
        double maxCellWidth = headerTextWidth;

        for (Map<String, Object> row : flattenedObjects) {
            Object val = row.get(columnName);
            if (val != null) {
                double cellWidth = JsonTableUtilities.computeTextWidth(headerFont, val.toString()) + padding;
                maxCellWidth = Math.max(maxCellWidth, cellWidth);
            }
        }

        return maxCellWidth;
    }
}
