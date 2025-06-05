import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

// Represents a single vertical stack of terrain (e.g. tile elevation)
class TileColumn {
    public final int xIndex;
    public final int tileHeight;
    public final int unitHeight;

    public TileColumn(int xIndex, int tileHeight, int unitHeight) {
        this.xIndex = xIndex;
        this.tileHeight = tileHeight;
        this.unitHeight = unitHeight;
    }

    public float totalHeight() {
        return tileHeight + unitHeight;
    }
}

// Responsible for rendering the tiles, units, and LoS line
class LineOfSightRenderer {
    private final Canvas canvas;
    private final GraphicsContext gc;

    private final int tileWidth = 50;
    private final int heightUnit = 20;
    private final int unitBodyWidth = 30;

    public LineOfSightRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    public void drawScene(List<TileColumn> columns) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (TileColumn col : columns) {
            int x = col.xIndex * tileWidth;
            int y = (int) canvas.getHeight() - col.tileHeight * heightUnit;

            // Draw tile base
            gc.setFill(Color.DARKGRAY);
            gc.fillRect(x, y, tileWidth, col.tileHeight * heightUnit);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(x, y, tileWidth, col.tileHeight * heightUnit);

            // Draw unit as a colored rectangle aligned to top of tile
            if (col.unitHeight > 0) {
                int unitHeightPx = col.unitHeight * heightUnit;
                int unitY = y - unitHeightPx;
                int unitX = x + (tileWidth - unitBodyWidth) / 2;

                gc.setFill(col.xIndex == 0 ? Color.GREEN : Color.RED);
                gc.fillRect(unitX, unitY, unitBodyWidth, unitHeightPx);
                gc.setStroke(Color.BLACK);
                gc.strokeRect(unitX, unitY, unitBodyWidth, unitHeightPx);
            }
        }

        // Draw dashed line of sight from unit top-center to unit top-center
        TileColumn left = columns.get(0);
        TileColumn right = columns.get(columns.size() - 1);

        double x1 = left.xIndex * tileWidth + tileWidth / 2.0;
        double y1 = canvas.getHeight() - (left.tileHeight + left.unitHeight) * heightUnit;
        double x2 = right.xIndex * tileWidth + tileWidth / 2.0;
        double y2 = canvas.getHeight() - (right.tileHeight + right.unitHeight) * heightUnit;

        gc.setStroke(Color.RED);
        gc.setLineDashes(5);
        gc.strokeLine(x1, y1, x2, y2);
    }
}

// Standalone JavaFX app
public class LineOfSightVisualizer extends Application {

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(400, 200);
        LineOfSightRenderer renderer = new LineOfSightRenderer(canvas);

        List<TileColumn> columns = List.of(
                new TileColumn(0, 1, 2), // green unit
                new TileColumn(1, 2, 0), // middle tile
                new TileColumn(2, 3, 0), // obstruction
                new TileColumn(3, 2, 0),
                new TileColumn(4, 2, 2)  // red unit
        );

        renderer.drawScene(columns);

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, 400, 200);

        primaryStage.setTitle("Line of Sight (Side View)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}