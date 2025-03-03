package main.ui.game;

public class JavaFxStyleBuilder {
    private StringBuilder mBuilder = new StringBuilder();

    public JavaFxStyleBuilder setFontSize(int size) {
        mBuilder.append("-fx-font-size: ").append(size).append("px;");
        return this;
    }
    public JavaFxStyleBuilder setBackgroundColor(String color) {
        mBuilder.append("-fx-background-color: ").append(color).append(";");
        return this;
    }

    public String build() { return mBuilder.toString(); }
}
