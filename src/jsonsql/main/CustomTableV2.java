package jsonsql.main;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.*;

public class CustomTableV2 extends VBox {

    private ScrollPane mRowsScrollPane = null;
    private HBox mHeaders = new HBox();
    private VBox mRows = new VBox();

    private final Map<String, Integer> mColumns = new LinkedHashMap<>();
    private final List<DoubleProperty> mColumnWidths = new ArrayList<>();
    private final Text mTextMetricsHelper = new Text();

    private Font mFont = Font.getDefault();
    private final double HEADER_ROW_HEIGHTS_MULTIPLIER = 1.5;
    private final DoubleProperty mRowHeights = new SimpleDoubleProperty(10);
    private static final String ROW_NUMBER_COLUMN = "#";

    private final List<Node> selectedCells = new ArrayList<>();
    private final Background mSelectedColor = new Background(new BackgroundFill(Color.LIGHTYELLOW, CornerRadii.EMPTY, Insets.EMPTY));
    private final Background mTransparentBackground = new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY));
    private final Background mPrimaryColor = new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY));
    private final Background mSecondaryColor = new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY));

    public CustomTableV2() {
        double idealHeight = computeTextHeight(mFont, 10);
        mRowHeights.set(idealHeight);

        setSpacing(2);
        setPadding(new Insets(5));



        mHeaders.minWidthProperty().bind(mRows.widthProperty());
        mHeaders.prefWidthProperty().bind(mRows.widthProperty());
        mHeaders.maxWidthProperty().bind(mRows.widthProperty());

        HBox mHeaderContent = new HBox(mHeaders);
        mHeaderContent.setFillHeight(false);

        // ✨ Bind height of mHeaderContent exactly
        mHeaderContent.prefHeightProperty().bind(mRowHeights.multiply(HEADER_ROW_HEIGHTS_MULTIPLIER - 1));
        mHeaderContent.minHeightProperty().bind(mRowHeights.multiply(HEADER_ROW_HEIGHTS_MULTIPLIER - 1));
        mHeaderContent.maxHeightProperty().bind(mRowHeights.multiply(HEADER_ROW_HEIGHTS_MULTIPLIER - 1));
        VBox.setVgrow(mHeaderContent, Priority.NEVER);


        ScrollPane mHeadersScrollPane = new ScrollPane(mHeaderContent);
        mHeadersScrollPane.setFitToHeight(false);
        mHeadersScrollPane.setFitToWidth(false);
        mHeadersScrollPane.setPannable(false);
        mHeadersScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mHeadersScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);


        VBox mRowsContent = new VBox(mRows);

        mRowsScrollPane = new ScrollPane(mRowsContent);
        mRowsScrollPane.setFitToHeight(false);
        mRowsScrollPane.setFitToWidth(false);
        mRowsScrollPane.setPannable(true);
        mRowsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mRowsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(mRowsScrollPane, Priority.ALWAYS);

        mHeaderContent.minWidthProperty().bind(mRows.widthProperty());
        mHeadersScrollPane.hvalueProperty().bindBidirectional(mRowsScrollPane.hvalueProperty());


        getChildren().addAll(mHeadersScrollPane, mRowsScrollPane);
    }

    public void setColumns(List<String> columns) {
        columns.addFirst(ROW_NUMBER_COLUMN);

        mColumns.clear();
        mColumnWidths.clear();
        mHeaders.getChildren().clear();

        for (int index = 0; index < columns.size(); index++) {
            String name = columns.get(index);

            mColumns.put(name, index);
            mColumnWidths.add(new SimpleDoubleProperty());

            TextField columnLabel = createHeaderCell(index);
            columnLabel.setEditable(false);
            columnLabel.setBackground(mPrimaryColor);
            columnLabel.setText(name);

            mHeaders.getChildren().add(columnLabel);

            for (Node node : mRows.getChildren()) {
                if (!(node instanceof HBox row)) continue;
                TextField cell = createEditableCell(index);
                row.getChildren().add(cell);
            }
        }
    }

    public void addRow(Map<String, Object> rowData) {
        HBox row = new HBox(1);
        row.setAlignment(Pos.CENTER_LEFT);

        int index = mRows.getChildren().size();
        rowData.put(ROW_NUMBER_COLUMN, index + "");

        for (Map.Entry<String, Integer> column : mColumns.entrySet()) {
            String columnName = column.getKey();
            int columnIndex = column.getValue();
            String inputData = (String) rowData.get(columnName);

            TextField cell = createEditableCell(columnIndex);
            cell.setText(inputData);

            if (columnName.equalsIgnoreCase(ROW_NUMBER_COLUMN)) {
                cell.setEditable(false);
                cell.setBackground(mPrimaryColor);
                cell.setOnMouseClicked(e -> selectRow(index));
            }

            DoubleProperty width = mColumnWidths.get(columnIndex);
            double cellTextWidth = computeTextWidth(cell.getFont(), columnName, 20);
            double headerTextWidth = computeTextWidth(cell.getFont(), inputData, 20);
            double neededWidth = Math.max(cellTextWidth, headerTextWidth);

            if (neededWidth > width.get()) width.set(neededWidth);

            row.getChildren().add(cell);
        }

        row.setBackground((index % 2 == 1) ? mPrimaryColor : mSecondaryColor);
        mRows.getChildren().add(row);
    }

    private TextField createHeaderCell(int index) {
        DoubleProperty width = mColumnWidths.get(index);
        DoubleProperty height = mRowHeights;

        TextField field = new TextField();
        field.setFont(Font.font(mFont.getFamily(), FontWeight.BOLD, mFont.getSize())); // always bold

//        double textHeight = computeTextHeight(mFont, 40);
//        label.setPadding(new Insets(5));
//        label.setPrefHeight(textHeight);
        field.setAlignment(Pos.CENTER);

        field.setBorder(getCellBordering());
        field.prefWidthProperty().bind(width);
        field.minWidthProperty().bind(width);
        field.maxWidthProperty().bind(width);

        field.prefHeightProperty().bind(height.multiply(HEADER_ROW_HEIGHTS_MULTIPLIER));
        field.minHeightProperty().bind(height.multiply(HEADER_ROW_HEIGHTS_MULTIPLIER));
        field.maxHeightProperty().bind(height.multiply(HEADER_ROW_HEIGHTS_MULTIPLIER));

//        label.setBorder(getTableNonEditableFieldStyle());

//        label.setOnMousePressed(event -> startX = event.getSceneX());
//        label.setOnMouseDragged(event -> {
//            double newWidth = width.get() + (event.getSceneX() - startX);
//            if (newWidth > MIN_WIDTH) width.set(newWidth);
//            startX = event.getSceneX();
//        });

//        label.setOnMouseDragged((MouseEvent event) -> {
//            double newWidth = event.getX();
//            if (newWidth > 30) {
//                width.set(newWidth);
//            }
//        });

        field.setOnMouseClicked(e -> { selectColumn(index); });

        return field;
    }

    private TextField createEditableCell(int column) {
        DoubleProperty width = mColumnWidths.get(column);
        DoubleProperty height = mRowHeights;

        TextField field = new TextField();
        field.setFont(mFont);
        field.setBackground(mTransparentBackground);
        field.setBorder(getCellBordering());

        field.prefWidthProperty().bind(width);
        field.minWidthProperty().bind(width);
        field.maxWidthProperty().bind(width);

        field.prefHeightProperty().bind(height);
        field.minHeightProperty().bind(height);
        field.maxHeightProperty().bind(height);

        field.setOnMouseClicked(e -> {
            clearSelected();
            field.setBackground(mSelectedColor);
            selectedCells.add(field);
        });

        field.textProperty().addListener((obs, oldText, newText) -> {
            double contentWidth = computeTextWidth(field.getFont(), newText, 0);
            double headerWidth = computeTextWidth(field.getFont(), ((TextField) mHeaders.getChildren().get(column)).getText(), 0);
            double maxWidth = Math.max(contentWidth, headerWidth);

            DoubleProperty dp = mColumnWidths.get(column);
            if (dp != null && maxWidth > dp.get()) { dp.set(maxWidth); }
        });

        return field;
    }

    private double computeTextWidth(Font font, String text, double padding) {
        mTextMetricsHelper.setText(text);
        mTextMetricsHelper.setFont(font);
        return mTextMetricsHelper.getLayoutBounds().getWidth() + padding;
    }

    private double computeTextHeight(Font font, double padding) {
        mTextMetricsHelper.setText("Ag");
        mTextMetricsHelper.setFont(font);
        return mTextMetricsHelper.getLayoutBounds().getHeight() + padding;
    }

    private Border getCellBordering() {
        return new Border(new BorderStroke(
                Color.GREY, BorderStrokeStyle.NONE, CornerRadii.EMPTY, new BorderWidths(0)
        ));
    }

    private void clearSelected() {
        for (Node node : selectedCells) {
            if (node instanceof TextField field) {
                field.setBackground(mTransparentBackground);
            }
        }
        selectedCells.clear();
    }

    private void selectRow(int index) {
        clearSelected();
        if (index < 0 || index >= mRows.getChildren().size()) return;
        HBox row = (HBox) mRows.getChildren().get(index);
        for (Node cell : row.getChildren()) {
            if (cell instanceof TextField tf) {
                tf.setBackground(mSelectedColor);
            }
            selectedCells.add(cell);
        }
    }

    private void selectColumn(int index) {
        clearSelected();
        if (index < 0 || index >= mHeaders.getChildren().size()) return;
        TextField header = (TextField) mHeaders.getChildren().get(index);
        header.setBackground(mSelectedColor);
        selectedCells.add(header);

        for (Node node : mRows.getChildren()) {
            if (node instanceof HBox row && index < row.getChildren().size()) {
                TextField cell = (TextField) row.getChildren().get(index);
                cell.setBackground(mSelectedColor);
                selectedCells.add(cell);
            }
        }
    }
}