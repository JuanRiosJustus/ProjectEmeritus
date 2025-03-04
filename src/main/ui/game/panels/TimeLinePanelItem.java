package main.ui.game.panels;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.ui.game.BeveledButton;
import main.ui.game.GamePanel;
import main.ui.game.GraphicButton;

public class TimeLinePanelItem extends GamePanel {
    public final VBox mContainer;
    public final BeveledButton label;
    public final GraphicButton display;
    public final int displayWidth;
    public final int displayHeight;
    private Color baseColor;

    public TimeLinePanelItem(int width, int height, Color color) {
        super(width, height);
        this.baseColor = color;

        displayWidth = width;
        displayHeight = (int) (height * .75);
        display = new GraphicButton(displayWidth, displayHeight, color);
        display.setFocusTraversable(false);

        int labelWidth = width;
        int labelHeight = height - displayHeight;
        label = new BeveledButton(labelWidth, labelHeight, "", color);
        label.setFocusTraversable(false);
        label.setFont(getFontForHeight(labelHeight));

        // 🔹 **Container Setup**
        mContainer = new VBox(display, label);
        mContainer.setPrefSize(width, height);
        mContainer.setMinSize(width, height);
        mContainer.setMaxSize(width, height);

        // 🔹 **Set Initial Background Color and Bevels**
        setBackgroundColor(color);

        getChildren().add(mContainer);
        setFocusTraversable(false);
    }

    // 🔹 **Create Beveled Border Colors Based on Given Color**
    private Border createBevelBorder(Color color) {
        // ** Darker and more balanced bevels **
        Color highlightOuter = color.deriveColor(0, 1, 1.3, 1); // Less bright top-left
        Color highlightInner = color.deriveColor(0, 1, 1.1, 1); // Subtle inner highlight
        Color shadowInner = color.deriveColor(0, 1, 0.5, 1); // Darker inner shadow
        Color shadowOuter = color.deriveColor(0, 1, 0.3, 1); // Darkest outer shadow for more contrast

        Border outerBevel = new Border(new BorderStroke(
                highlightOuter, shadowOuter, shadowOuter, highlightOuter,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(5), // Slightly thicker outer bevel
                Insets.EMPTY
        ));

        Border innerBevel = new Border(new BorderStroke(
                highlightInner, shadowInner, shadowInner, highlightInner,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(3), // Thicker inner bevel
                Insets.EMPTY
        ));

        return new Border(outerBevel.getStrokes().get(0), innerBevel.getStrokes().get(0));
    }

    // 🔹 **Set Background Color and Update Bevels**
    public void setBackgroundColor(Color color) {
        baseColor = color;

        // ** Adjust background to complement bevels**
        Color adjustedBaseColor = color.deriveColor(0, 1, 0.85, 1); // Slightly muted base color

        // **Update the entire panel background**
        setBackground(new Background(new BackgroundFill(adjustedBaseColor, CornerRadii.EMPTY, Insets.EMPTY)));

        // **Update container background**
        mContainer.setBackground(new Background(new BackgroundFill(adjustedBaseColor, CornerRadii.EMPTY, Insets.EMPTY)));

        // **Update Beveled Borders dynamically based on the new color**
        mContainer.setBorder(createBevelBorder(color));

        // **Ensure buttons match the background to prevent grey areas**
        display.setBackgroundColor(adjustedBaseColor);
//        label.setBackgroundColor(adjustedBaseColor);
    }
}