package main.jsontable;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;

import java.util.Map;

public class JSONTableColumn extends TableColumn<Map<String, Object>, String> {

    public static class Header extends BorderPane {
        private final Label mLabel;
        public Header(String name) {
            String headerStyle = "-fx-background-color: linear-gradient(to bottom, #f0f0f0, #e2e2e2); " +
                    "-fx-border-color: #ccc; " +
                    "-fx-border-width: 0 0 1px 0; " +
                    "-fx-padding: 6 10 6 10; " +
                    "-fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.05), 2, 0, 0, 1); " +
                    "-fx-alignment: center-left;";

            mLabel = new Label();
            mLabel.setText(name);
            setCenter(mLabel);
        }
        public Label getLabel() { return mLabel; }
    }

    private JSONTableColumn.Header mHeader = null;

    public JSONTableColumn(String name, int fontSize) {
        mHeader = new JSONTableColumn.Header(name);

        Font headerFont = new Font("System", fontSize + 14);
        mHeader.getLabel().setFont(headerFont);
        mHeader.getLabel().setAlignment(Pos.CENTER);

        setText(null);
        setGraphic(mHeader);
        setSortable(false);
    }

    public JSONTableColumn.Header getHeader() { return mHeader; }
}
