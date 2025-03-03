package main.ui.game;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.main.rendering.*;

import java.util.PriorityQueue;
import java.util.Queue;

public class GameCanvas extends Pane {

    private final Canvas mCanvas;
    private final GameModel mGameModel;

    private final Queue<Entity> tilesWithUnits = new PriorityQueue<>();

    private final RendererV2 tileRenderer = new TileRendererV2();
    private final RendererV2 unitRenderer = new UnitRendererV2();
    private final RendererV2 structureRenderer = new StructureRendererV2();
    private final RendererV2 selectedAndHoveredTileRenderer = new SelectedAndHoveredTileRendererV2();
    private final RendererV2 actionAndMovementPathingRenderer = new ActionAndMovementPathingRendererV2();
//    private final RendererV2 floatingTextRenderer = new FloatingTextRendererV2();
//    private final RendererV2 healthBarRenderer = new HealthBarRendererV2();

    public GameCanvas(GameModel gm, int width, int height) {
        mGameModel = gm;
        mCanvas = new Canvas(width, height);

        // Ensure the canvas resizes with the GameCanvas (Pane)
        mCanvas.widthProperty().bind(widthProperty());
        mCanvas.heightProperty().bind(heightProperty());

        getChildren().add(mCanvas);
    }

    public void update() {
        if (!mGameModel.isRunning()) return;
        render();
    }

    public void render() {
        GraphicsContext gc = mCanvas.getGraphicsContext2D();
//        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Clear the screen
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, mCanvas.getWidth(), mCanvas.getHeight()); // Fill background

        RenderContext renderContext = RenderContext.create(mGameModel);

        tileRenderer.render(gc, mGameModel, renderContext);
        actionAndMovementPathingRenderer.render(gc, mGameModel, renderContext);
        unitRenderer.render(gc, mGameModel, renderContext);
        structureRenderer.render(gc, mGameModel, renderContext);
        selectedAndHoveredTileRenderer.render(gc, mGameModel, renderContext);
//        floatingTextRenderer.render(gc, gameModel, renderContext);
//        healthBarRenderer.render(gc, gameModel, renderContext);
    }

    public void setCanvasSize(double width, double height) {
        mCanvas.setWidth(width);
        mCanvas.setHeight(height);
    }
}