package jsonsql.main;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.*;

public class CustomTable extends VBox {

    private ScrollPane mIndexesScrollPane = null;
    private ScrollPane mRowsScrollPane = null;
    private ScrollPane mHeadersScrollPane = null;
    private final GridPane mHeaders = new GridPane();
    private final GridPane mIndexes = new GridPane();
    private final GridPane mContent = new GridPane();
    private final List<String> mColumns = new ArrayList<>();
    private int mRowCount = 0;
    private final Map<Integer, DoubleProperty> mColumnWidths = new HashMap<>();
    private final DoubleProperty mIndexColumnWidth = new SimpleDoubleProperty(40); // start with some default
    private final List<DoubleProperty> mHeights = new ArrayList<>();

    private final Text mTextMetricsHelper = new Text();
    private final double HEADER_ROW_HEIGHTS_MULTIPLIER = 1.5;
    private final DoubleProperty mRowHeights = new SimpleDoubleProperty(10);
    private final List<Node> selectedCells = new ArrayList<>();
    private final Background mSelectedColor = new Background(new BackgroundFill(Color.LIGHTYELLOW, CornerRadii.EMPTY, Insets.EMPTY));
    private final Background mPrimaryColor = new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY));
    private final Background mSecondaryColor = new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY));
    private static final String INDEX_COLUMN = "#";
    private static final double PADDING = 8d;
    private Font mFont = Font.getDefault();
    private final Map<TextField, Background> mOriginalBackgrounds = new HashMap<>();
    private boolean mIsEditMode = false;
    public CustomTable() {
        double idealHeight = computeTextHeight(mFont);
        mRowHeights.set(idealHeight);

        setPadding(new Insets(5));
        bindVisibleRowCountToParent(15);

        mHeadersScrollPane = new ScrollPane(mHeaders);
        mHeadersScrollPane.setFitToHeight(false);
        mHeadersScrollPane.setFitToWidth(false);
        mHeadersScrollPane.setPannable(false);
        mHeadersScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mHeadersScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mHeaders.setBackground(mPrimaryColor);
        mHeadersScrollPane.setBackground(mPrimaryColor);

        mIndexesScrollPane = new ScrollPane(mIndexes);
        mIndexesScrollPane.setFitToHeight(false);
        mIndexesScrollPane.setFitToWidth(false);
        mIndexesScrollPane.setPannable(false);
        mIndexesScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mIndexesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mIndexes.setBackground(mPrimaryColor);
        mIndexesScrollPane.setBackground(mPrimaryColor);

        mRowsScrollPane = new ScrollPane(mContent);
        mRowsScrollPane.setFitToHeight(false);
        mRowsScrollPane.setFitToWidth(false);
        mRowsScrollPane.setPannable(true);
        mRowsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mRowsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mHeadersScrollPane.setBackground(mPrimaryColor);
        mContent.setBackground(mPrimaryColor);

        mHeadersScrollPane.setPadding(Insets.EMPTY);
        mIndexesScrollPane.setPadding(Insets.EMPTY);
        mRowsScrollPane.setPadding(Insets.EMPTY);

        mRowsScrollPane.hvalueProperty().addListener((obs, oldVal, newVal) -> {
            double contentWidth = mRowsScrollPane.getContent().getBoundsInLocal().getWidth();
            double max = contentWidth - mRowsScrollPane.getViewportBounds().getWidth();
            double scrollPixels = newVal.doubleValue() * max;
            mHeaders.setTranslateX(-scrollPixels);
        });

        mRowsScrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            double contentHeight = mRowsScrollPane.getContent().getBoundsInLocal().getHeight();
            double max = contentHeight - mRowsScrollPane.getViewportBounds().getHeight();
            double scrollPixels = newVal.doubleValue() * max;
            mIndexes.setTranslateY(-scrollPixels);
        });


        // 🛡️ Forward scrolls on header pane to the rows pane
        mHeadersScrollPane.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (e.getDeltaX() != 0) {
                double contentWidth = e.getDeltaX() / mRowsScrollPane.getContent().getBoundsInLocal().getWidth();
                double horizontalDelta = mRowsScrollPane.getHvalue();
                mRowsScrollPane.setHvalue(horizontalDelta - contentWidth);
                e.consume(); // Block original event
            }
        });

        // 🛡️ Forward scrolls on index pane to the rows pane
        mIndexesScrollPane.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (e.getDeltaY() != 0) {
                double contentHeight = e.getDeltaY() / mRowsScrollPane.getContent().getBoundsInLocal().getHeight();
                double verticalDelta = mRowsScrollPane.getVvalue();
                mRowsScrollPane.setVvalue(verticalDelta - contentHeight);
                e.consume(); // Block original event
            }
        });


        HBox indexAndRowPane = new HBox();
        indexAndRowPane.getChildren().addAll(mIndexesScrollPane, mRowsScrollPane);


//        VBox headerAndBottomPane = new VBox();
//        headerAndBottomPane.getChildren().addAll(mHeadersScrollPane, indexAndRowPane);
//        getChildren().addAll(headerAndBottomPane);
//
        getChildren().addAll(mHeadersScrollPane, indexAndRowPane);
        setBackground(mPrimaryColor);


        Platform.runLater(() -> {
            Region headerViewport = (Region) mHeadersScrollPane.lookup(".viewport");
            if (headerViewport != null) { headerViewport.setBackground(mPrimaryColor); }

            Region rowViewport = (Region) mRowsScrollPane.lookup(".viewport");
            if (rowViewport != null) { rowViewport.setBackground(mPrimaryColor); }
        });


        mIndexes.prefWidthProperty().bind(mIndexColumnWidth);
        mIndexes.minWidthProperty().bind(mIndexColumnWidth);
        mIndexes.maxWidthProperty().bind(mIndexColumnWidth);

        mIndexesScrollPane.prefWidthProperty().bind(mIndexColumnWidth);
        mIndexesScrollPane.minWidthProperty().bind(mIndexColumnWidth);
        mIndexesScrollPane.maxWidthProperty().bind(mIndexColumnWidth);
    }

    public int getColumns() { return mHeaders.getColumnCount(); }
    public void setColumns(List<String> columns) {
        columns = new ArrayList<>(columns); // Defensive copy
        columns.addFirst(INDEX_COLUMN);

        mColumns.clear();
        mColumnWidths.clear();

        mHeaders.getChildren().clear();
        mIndexes.getChildren().clear();
        TextField cell = null;

        for (int index = 0; index < columns.size(); index++) {
            String key = columns.get(index);
            cell = createCell(0, index);
            if (key.equalsIgnoreCase(INDEX_COLUMN)) {
                cell.setText("");
            } else {
                cell.setText(key);
            }

            add(cell, 0, index);
            mColumns.add(key);
        }

        clearSelected();
        setContentVisible(true);
    }

    public void addColumn(String column, int insertAtIndex) {
        if (insertAtIndex < 0 || insertAtIndex > mColumns.size()) {
            insertAtIndex = mColumns.size();
        }

        mColumns.add(insertAtIndex, column);
        mColumnWidths.put(insertAtIndex, new SimpleDoubleProperty());

        // Shift existing headers to the right
        for (Node node : new ArrayList<>(mHeaders.getChildren())) {
            Integer col = GridPane.getColumnIndex(node);
            Integer row = GridPane.getRowIndex(node);
            if (col != null && col >= insertAtIndex && row != null && row == 0) {
                GridPane.setColumnIndex(node, col + 1);
            }
        }

        // Add new header cell
//        TextField header = createCell(-1, insertAtIndex);
//        header.setText(column.equalsIgnoreCase(INDEX_COLUMN) ? "" : column);
//        mHeaders.add(header, insertAtIndex, 0);

        int rowCount = mContent.getRowCount();

        for (Node node : new ArrayList<>(mContent.getChildren())) {
            Integer col = GridPane.getColumnIndex(node);
            Integer row = GridPane.getRowIndex(node);
            if (col != null && row != null && col >= insertAtIndex) {
                GridPane.setColumnIndex(node, col + 1);
            }
        }

        // Add empty cells for the new column
        for (int row = 0; row < rowCount; row++) {
//            TextField cell = createCell(row, insertAtIndex);
//            mRows.add(cell, insertAtIndex, row);
//            add()
        }

//        selectColumn(header);
        updateColumnWidth(insertAtIndex);
    }


    private void add(Node node, int row, int column) {
        if (row == 0) {
            mHeaders.add(node, column, row);
        } else if (column == 0) {
            mIndexes.add(node, column, row);
        } else {
            mContent.add(node, column, row);
        }
    }

    public void addRow(Map<String, Object> rowData) {
        // Add index cell, row == 0 is always the header
        int row = mRowCount + 1;
        rowData.put(INDEX_COLUMN, row);

        for (int column = 0; column < mColumns.size(); column++) {
            String name = mColumns.get(column);
            String value = String.valueOf(rowData.getOrDefault(name, ""));

            TextField cell = createCell(row, column);
            cell.setText(value);

            add(cell, row, column);
        }
        mRowCount = mRowCount + 1;
    }

    public void setFont(Font font) {
        mFont = font;

        // Recalculate row height
        double newRowHeight = computeTextHeight(mFont);
        mRowHeights.set(newRowHeight);

        // Update header fields
        for (Node node : mHeaders.getChildren()) {
            if (node instanceof TextField tf) {
                tf.setFont(Font.font(font.getFamily(), FontWeight.BOLD, font.getSize()));
            }
        }

        // Update row fields
        for (Node node : mContent.getChildren()) {
            if (node instanceof TextField tf) {
                tf.setFont(font);
            }
        }

        // Recalculate column widths
        for (int i = 0; i < mColumnWidths.size(); i++) {
            updateColumnWidth(i);
        }
    }

    private double computeTextWidth(Font font, String text) {
        return computeTextWidth(font, text, PADDING);
    }

    private double computeTextWidth(Font font, String text, double padding) {
        mTextMetricsHelper.setText(text);
        mTextMetricsHelper.setFont(font);
        return mTextMetricsHelper.getLayoutBounds().getWidth() + (2 * padding) + 2; // ✨ +2 for safety
    }

    private double computeTextHeight(Font font) {
        mTextMetricsHelper.setText("Ag");
        mTextMetricsHelper.setFont(font);
        return mTextMetricsHelper.getLayoutBounds().getHeight();
    }

    private Border getCellBordering() {
        return new Border(new BorderStroke(
                Color.GREY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1)
        ));
    }

    private void clearSelected() {
        for (Node node : selectedCells) {
            if (!(node instanceof TextField field)) { continue; }
            Background originalBackground = mOriginalBackgrounds.get(field);
            field.setBackground(originalBackground);
        }
        selectedCells.clear();
    }

    private void selectRow(Node selected) {
        clearSelected();

        // Determine the index of the selected node. This node is presumably within index pane
        Integer index = GridPane.getRowIndex(selected);
        if (index == null || !(selected instanceof TextField field)) { return; }

        field.setBackground(mSelectedColor);
        selectedCells.add(field);

        for (Node node : mContent.getChildren()) {
            Integer row = GridPane.getRowIndex(node);

            if (row == null || row != index) { continue; }
            if (!(node instanceof TextField tf)) { continue; }

            tf.setBackground(mSelectedColor);
            selectedCells.add(tf);
        }
    }

    private void selectColumn(Node selected) {
        clearSelected();

        Integer index = GridPane.getColumnIndex(selected);
        if (index == null || !(selected instanceof TextField field)) { return; }

        field.setBackground(mSelectedColor);
        selectedCells.add(field);


        for (Node node : mContent.getChildren()) {
            Integer column = GridPane.getColumnIndex(node);

            if (column == null || column != index) { continue; }
            if (!(node instanceof TextField tf)) { continue; }

            tf.setBackground(mSelectedColor);
            selectedCells.add(tf);
        }
    }

    private void updateColumnWidth(int index) {
        double maxWidth = Double.MIN_VALUE;

        if (mHeaders.getChildren().isEmpty()) { return; }

        // We need to check the header sizes
        if (index == 0) {
            // Check header of index Column
            Node headerNode = mHeaders.getChildren().getFirst();
            if (headerNode instanceof TextField tf) {
                double headerWidth = computeTextWidth(tf.getFont(), tf.getText());
                maxWidth = Math.max(maxWidth, headerWidth);
            }
        } else if (index < mHeaders.getChildren().size()) {
            Node headerNode = mHeaders.getChildren().get(index);
            if (headerNode instanceof TextField tf) {
                double headerWidth = computeTextWidth(tf.getFont(), tf.getText());
                maxWidth = Math.max(maxWidth, headerWidth);
            }
        }


        // Check every cell in that column
        if (index == 0) {
            for (Node node : mIndexes.getChildren()) {
                Integer col = GridPane.getColumnIndex(node);
                if (col == null || col != index) { continue; }
                if (!(node instanceof TextField tf)) { continue; }
                double cellWidth = computeTextWidth(tf.getFont(), tf.getText());
                maxWidth = Math.max(maxWidth, cellWidth);
            }
        } else {
            for (Node node : mContent.getChildren()) {
                Integer col = GridPane.getColumnIndex(node);
                if (col == null || col != index) { continue; }
                if (!(node instanceof TextField tf)) { continue; }
                double cellWidth = computeTextWidth(tf.getFont(), tf.getText());
                maxWidth = Math.max(maxWidth, cellWidth);
            }
        }

        // Update column width
        DoubleProperty widthProperty = getOrCreateNewWidthProperty(index);
        widthProperty.set(maxWidth);
    }


    private void bindWidthProperties(TextField field, DoubleProperty width) {
        field.prefWidthProperty().bind(width);
        field.minWidthProperty().bind(width);
        field.maxWidthProperty().bind(width);
    }

    private void bindHeightProperties(TextField field, DoubleProperty height) {
        field.prefHeightProperty().bind(height);
        field.minHeightProperty().bind(height);
        field.maxHeightProperty().bind(height);
    }

    private void bindHeightAsHeaderProperties(TextField field, DoubleProperty height) {
        field.prefHeightProperty().bind(height.multiply(HEADER_ROW_HEIGHTS_MULTIPLIER));
        field.minHeightProperty().bind(height.multiply(HEADER_ROW_HEIGHTS_MULTIPLIER));
        field.maxHeightProperty().bind(height.multiply(HEADER_ROW_HEIGHTS_MULTIPLIER));
    }

//    public void bindVisibleRowCountToParent(int visibleRowsIncludingHeader) {
//        heightProperty().addListener((obs, oldHeight, newHeight) -> {
//            double effectiveRows = (HEADER_ROW_HEIGHTS_MULTIPLIER - 1) + visibleRowsIncludingHeader;
//            double newRowHeight = newHeight.doubleValue() / effectiveRows;
//            mRowHeights.set(newRowHeight);
//
//            // Update header heights
//            for (Node node : mHeaders.getChildren()) {
//                if (node instanceof TextField tf) {
////                    bindHeightAsHeaderProperties(tf, mRowHeights);
//                    bindHeightProperties(tf, mRowHeights);
//                }
//            }
//
//            // Update row heights
//            for (Node node : mContent.getChildren()) {
//                if (node instanceof TextField tf) {
//                    bindHeightProperties(tf, mRowHeights);
//                }
//            }
//
//            // 🔥 ADD THIS: Update index (row numbers) heights
//            for (Node node : mIndexes.getChildren()) {
//                if (node instanceof TextField tf) {
//                    bindHeightProperties(tf, mRowHeights);
//                }
//            }
//
//            mHeadersScrollPane.prefHeightProperty().bind(mRowHeights.multiply(HEADER_ROW_HEIGHTS_MULTIPLIER));
//            mHeadersScrollPane.minHeightProperty().bind(mRowHeights.multiply(HEADER_ROW_HEIGHTS_MULTIPLIER));
//            mHeadersScrollPane.maxHeightProperty().bind(mRowHeights.multiply(HEADER_ROW_HEIGHTS_MULTIPLIER));
//        });
//    }


    public void bindVisibleRowCountToParent(int visibleRowsIncludingHeader) {
        heightProperty().addListener((obs, oldHeight, newHeight) -> {
            if (mFont == null) { return; }

            // ⚡ 1. Compute *exact* pixel height of one font line
            double fontHeight = computeTextHeight(mFont);

            // ⚡ 2. Estimate header height (make it a little taller for bold headers)
            double headerHeight = fontHeight * 1.0; // or tweak if you want fancier headers

            // ⚡ 3. Total height needed for all rows
            double totalRowHeight = fontHeight * (visibleRowsIncludingHeader - 1) + headerHeight;

            // ⚡ 4. Scale rows proportionally based on available space
            double scale = newHeight.doubleValue() / totalRowHeight;

            // ⚡ 5. Adjusted row and header heights
            mRowHeights.set(fontHeight * scale);

            double adjustedHeaderHeight = headerHeight * scale;

            // ⚡ 6. Apply adjusted heights
            for (Node node : mHeaders.getChildren()) {
                if (node instanceof TextField tf) {
                    tf.prefHeightProperty().unbind();
                    tf.prefHeightProperty().bind(new SimpleDoubleProperty(adjustedHeaderHeight));
                }
            }

            for (Node node : mContent.getChildren()) {
                if (node instanceof TextField tf) {
                    tf.prefHeightProperty().unbind();
                    tf.prefHeightProperty().bind(mRowHeights);
                }
            }

            for (Node node : mIndexes.getChildren()) {
                if (node instanceof TextField tf) {
                    tf.prefHeightProperty().unbind();
                    tf.prefHeightProperty().bind(mRowHeights);
                }
            }

            // Also update header container height
            mHeadersScrollPane.prefHeightProperty().unbind();
            mHeadersScrollPane.prefHeightProperty().bind(new SimpleDoubleProperty(adjustedHeaderHeight));
        });
    }

    private void adjustHeaderFontSize(TextField header, double availableHeight, double availableWidth) {
        if (availableHeight <= 0 || availableWidth <= 0) return;

        final String text = header.getText();
        final String fontFamily = mFont.getFamily();
        final FontWeight weight = FontWeight.BOLD;

        double maxHeight = availableHeight * 0.9; // ✨ leave 10% breathing room
        double maxWidth = availableWidth * 0.9;   // ✨ leave 10% breathing room

        double fontSize = 40; // Start big

        mTextMetricsHelper.setText(text);

        while (fontSize > 1) {
            Font testFont = Font.font(fontFamily, weight, fontSize);
            mTextMetricsHelper.setFont(testFont);

            double textHeight = mTextMetricsHelper.getLayoutBounds().getHeight();
            double textWidth = mTextMetricsHelper.getLayoutBounds().getWidth();

            if (textHeight <= maxHeight && textWidth <= maxWidth) {
                header.setFont(testFont);
                return;
            }

            fontSize -= 0.5; // Shrink gradually
        }

        // Fallback if nothing fits
        header.setFont(Font.font(fontFamily, weight, 12));
    }

    public void setContentVisible(boolean visible) {
        if (mHeaders.isVisible() != visible) { mHeaders.setVisible(visible); }
        if (mHeadersScrollPane.isVisible() != visible) { mHeadersScrollPane.setVisible(visible); }
        if (mContent.isVisible() != visible) { mContent.setVisible(visible); }
        if (mRowsScrollPane.isVisible() != visible) { mRowsScrollPane.setVisible(visible); }
    }

    private DoubleProperty getOrCreateNewWidthProperty(int column) {
        DoubleProperty width = mColumnWidths.get(column);
        if (width == null) {
            if (column == 0) {
                width = mIndexColumnWidth;
            } else {
                width = new SimpleDoubleProperty();
            }
            mColumnWidths.put(column, width);
        }
        return width;
    }

    private TextField createCell(int row, int column) {
        DoubleProperty width = getOrCreateNewWidthProperty(column);
        DoubleProperty height = mRowHeights;

        boolean isHeader = row == 0;
        boolean isIndexer = column == 0;

        TextField field = new TextField();
        field.setFont(isHeader || isIndexer ? Font.font(mFont.getFamily(), FontWeight.BOLD, mFont.getSize() * 1.5) : mFont);
        field.setAlignment(Pos.CENTER);
        field.setBorder(getCellBordering());
        field.setPadding(Insets.EMPTY);
        field.setSnapToPixel(true);
        field.setCache(true);
        field.setCacheHint(CacheHint.SPEED);

        bindWidthProperties(field, width);

        if (isHeader || isIndexer) {
            field.setCursor(Cursor.HAND);
            field.setEditable(false);
            field.setBackground(mPrimaryColor);
            if (isHeader) {
                field.setOnMouseClicked(e -> selectColumn(field));
                bindHeightProperties(field, height);
//                bindHeightAsHeaderProperties(field, height);
                // ✨ Dynamically adjust font size
//                width.addListener((obs, oldWidth, newWidth) -> {
//                    double availableHeight = mRowHeights.get() * HEADER_ROW_HEIGHTS_MULTIPLIER;
//                    double availableWidth = newWidth.doubleValue();
//                    adjustHeaderFontSize(field, availableHeight, availableWidth);
//                });
                updateColumnWidth(column);
//                adjustHeaderFontSize(field, availableHeight, availableWidth);
            } else {
                field.setOnMouseClicked(e -> selectRow(field));
                bindHeightProperties(field, height);
                updateColumnWidth(column);
            }
            field.setBackground(mPrimaryColor);
        } else {
            field.setBackground(row % 2 == 1 ? mPrimaryColor : mSecondaryColor);
            field.setOnMouseClicked(e -> {
                clearSelected();
                field.setBackground(mSelectedColor);
                selectedCells.add(field);
            });
            bindHeightProperties(field, height);
        }


        field.textProperty().addListener((obs, oldText, newText) -> updateColumnWidth(column));

        mOriginalBackgrounds.put(field, field.getBackground()); // ✨ remember it

        return field;
    }

    public void setEditable(boolean edit) {
        mIsEditMode = edit;

        for (Node node : mHeaders.getChildren()) {
            if (!(node instanceof TextField tf)) { continue; }
            tf.setEditable(mIsEditMode);
        }
    }

    public int getSelectedRow() {
        if (selectedCells.isEmpty()) { return -1; }
        Node cell = selectedCells.getFirst(); // Assuming only one selected
        Integer row = GridPane.getRowIndex(cell);
        return row != null ? row : -1;
    }

    public int getSelectedColumn() {
        if (selectedCells.isEmpty()) { return -1; }
        Node cell = selectedCells.getFirst();
        Integer col = GridPane.getColumnIndex(cell);
        return col != null ? col : -1;
    }

}