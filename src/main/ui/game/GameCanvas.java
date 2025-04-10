package main.ui.game;


import javafx.scene.CacheHint;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.main.GameModel;
import main.game.main.rendering.*;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class GameCanvas extends GamePanel {

    private final Canvas mCanvas;

    private List<Renderer> mRenderers = null;

    public GameCanvas(int width, int height) {
        super(width, height);
        mCanvas = new Canvas(width, height);

        // Ensure the canvas resizes with the GameCanvas (Pane)
        mCanvas.widthProperty().bind(widthProperty());
        mCanvas.heightProperty().bind(heightProperty());

        mCanvas.setCache(true);
        mCanvas.setCacheHint(CacheHint.SPEED);

        mRenderers = new ArrayList<>();
        mRenderers.add(new BackgroundRenderer());
        mRenderers.add(new TileRenderer());
        mRenderers.add(new ActionAndMovementPathingRenderer());
        mRenderers.add(new SelectedAndHoveredTileRenderer());
        mRenderers.add(new UnitRenderer());
        mRenderers.add(new StructureRenderer());
        mRenderers.add(new FloatingTextRenderer());
        mRenderers.add(new HealthBarRenderer());

        getChildren().add(mCanvas);
    }

    public GameCanvas(GameController gc, int width, int height) {
        super(width, height);
        mCanvas = new Canvas(width, height);

        // Ensure the canvas resizes with the GameCanvas (Pane)
        mCanvas.widthProperty().bind(widthProperty());
        mCanvas.heightProperty().bind(heightProperty());

        mCanvas.setCache(true);
        mCanvas.setCacheHint(CacheHint.SPEED);

        mRenderers = new ArrayList<>();
        mRenderers.add(new BackgroundRenderer());
        mRenderers.add(new TileRenderer());
        mRenderers.add(new ActionAndMovementPathingRenderer());
        mRenderers.add(new SelectedAndHoveredTileRenderer());
        mRenderers.add(new UnitRenderer());
        mRenderers.add(new StructureRenderer());
        mRenderers.add(new FloatingTextRenderer());
        mRenderers.add(new HealthBarRenderer());

        getChildren().add(mCanvas);
    }

    public void gameUpdate(GameController gc) {
        if (!gc.isRunning()) { return; }
        render(gc, gc.getGameModel().getGameState().getMainCameraID());
    }

    public void gameUpdate(GameController gc, String camera) {
        if (!gc.isRunning()) { return; }
        render(gc, camera);
    }

    private void render(GameController gameController, String camera) {
        RenderContext renderContext = RenderContext.create(gameController, camera);
        GraphicsContext graphicsContext = mCanvas.getGraphicsContext2D();

        for (Renderer renderer : mRenderers) {
            renderer.render(graphicsContext, renderContext);
        }
    }

    public void gameUpdate(GameModel gameModel, String camera) {
        if (!gameModel.isRunning()) { return; }
        render(gameModel, camera);
    }

    private void render(GameModel gameModel, String camera) {
        RenderContext renderContext = RenderContext.create(gameModel, camera);
        GraphicsContext graphicsContext = mCanvas.getGraphicsContext2D();

        for (Renderer renderer : mRenderers) {
            renderer.render(graphicsContext, renderContext);
        }
    }
}