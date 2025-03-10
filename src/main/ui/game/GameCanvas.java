package main.ui.game;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.main.GameModel;
import main.game.main.rendering.*;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class GameCanvas extends GamePanel {

    private final Canvas mCanvas;
    private final GameController mGameController;

    private final Queue<Entity> tilesWithUnits = new PriorityQueue<>();
    private final Renderer tileRenderer = new TileRenderer();
    private final Renderer unitRenderer = new UnitRenderer();
    private final Renderer structureRenderer = new StructureRenderer();
    private final Renderer selectedAndHoveredTileRenderer = new SelectedAndHoveredTileRenderer();
    private final Renderer actionAndMovementPathingRenderer = new ActionAndMovementPathingRenderer();
    private final Renderer floatingTextRenderer = new FloatingTextRenderer();
    private final Renderer healthBarRenderer = new HealthBarRenderer();
    private final Renderer backgroundRenderer = new BackgroundRenderer();

    private List<Renderer> mRenderers = null;

    public GameCanvas(GameController gc, int width, int height) {
        super(width, height);
        mGameController = gc;
        mCanvas = new Canvas(width, height);

        // Ensure the canvas resizes with the GameCanvas (Pane)
        mCanvas.widthProperty().bind(widthProperty());
        mCanvas.heightProperty().bind(heightProperty());

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
        render(gc, gc.getGameModel().getGameState().getMainCameraName());
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
}