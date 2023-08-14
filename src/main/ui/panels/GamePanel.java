package main.ui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import main.constants.ColorPalette;
import main.constants.Constants;
import main.ui.GameState;
import main.game.camera.Camera;
import main.game.components.tile.Gem;
import main.game.components.ActionManager;
import main.game.components.Animation;
import main.game.components.MovementManager;
import main.game.components.OverlayAnimation;
import main.game.components.Statistics;
import main.game.components.Tile;
import main.game.components.Vector;
import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.main.GameModel;
import main.game.stats.node.ResourceNode;
import main.game.stores.pools.AssetPool;
import main.game.stores.pools.FontPool;
import main.graphics.JScene;
import main.utils.MathUtils;

public class GamePanel extends JScene {

    private final Comparator<Entity> ordering = new Comparator<Entity>() {
        @Override
        public int compare(Entity o1, Entity o2) {
            Entity en1 = o1.get(Tile.class).unit;
            Entity en2 = o2.get(Tile.class).unit;
            if (en1 == null || en2 == null) { return 0; }
            Vector v1 = en1.get(Animation.class).position;
            Vector v2 = en2.get(Animation.class).position;
            return (int) (v2.y - v1.y);
        }
	};

    private final PriorityQueue<Entity> tilesWithEntitiesWithNameplates = new PriorityQueue<>(ordering);
    private final PriorityQueue<Entity> tilesWithUnits = new PriorityQueue<>(ordering);

    private final Queue<Entity> tilesWithStructures = new LinkedList<>();
    private final Queue<Entity> tilesWithGems = new LinkedList<>();
    private final Queue<Entity> tilesWithExits = new LinkedList<>();
    private final Queue<Entity> tilesWithOverlayAnimations = new LinkedList<>();

    private final GameController gc;

    public GamePanel(GameController controller, int width, int height) {
        super(width, height, GamePanel.class.getSimpleName());
        gc = controller;

        setPreferredSize(new Dimension(width, height));
        setLayout(new GridBagLayout());
        setBackground(ColorPalette.TRANSPARENT);
        setOpaque(false);
    }

    @Override
    public void jSceneUpdate(GameModel model) {
        revalidate();
        repaint();
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(gc.getModel(), g);
        g.dispose();
    }

    public void render(GameModel model, Graphics g) {

        collectAndQueueTileData(g, model);
    
        renderGems(g, model, tilesWithGems);
        renderUnits(g, model, tilesWithUnits);
        renderStructures(g, model, tilesWithStructures);
        renderOverlayAnimations(g, model, tilesWithOverlayAnimations);
        renderNamePlates(g, tilesWithEntitiesWithNameplates);
        renderExits(g, model, tilesWithExits);

        model.system.floatingText.render(g);
    }

    private void renderExits(Graphics g, GameModel model, Queue<Entity> queue) {
                
        while (queue.size() > 0) {
            Entity entity = queue.poll();
            Tile tile = entity.get(Tile.class);
            int tileX = Camera.getInstance().globalX(entity);
            int tileY = Camera.getInstance().globalY(entity);
            g.setColor(ColorPalette.TRANSLUCENT_BLACK_V1);
            g.fillRect(tileX, tileY, Constants.CURRENT_SPRITE_SIZE, Constants.CURRENT_SPRITE_SIZE);
        }    
    }

    private void renderNamePlates(Graphics g, PriorityQueue<Entity> queue) {
        while (queue.size() > 0) {
            Entity tileEntity = queue.poll();
            Entity entity = tileEntity.get(Tile.class).unit;
            if (entity == null) { continue; }
            drawHealthBar(g, entity);
        }      
    }

    private void renderOverlayAnimations(Graphics g, GameModel model, Queue<Entity> queue) {
        while(queue.size() > 0) {
            Entity entity = queue.poll();
            int tileX = Camera.getInstance().globalX(entity);
            int tileY = Camera.getInstance().globalY(entity);
            OverlayAnimation ca = entity.get(OverlayAnimation.class);
            if (ca.hasOverlay()) {
                BufferedImage image = ca.getAnimation().toImage();
                g.drawImage(image, tileX, tileY,  null);
            }
        }
    }

    private void collectAndQueueTileData(Graphics g, GameModel model) {
        int startColumn = (int) Math.max(0, model.getVisibleStartOfColumns());
        int startRow = (int) Math.max(0, model.getVisibleStartOfRows());
        int endColumn = (int) Math.min(model.getColumns(), model.getVisibleEndOfColumns() + 2);
        int endRow = (int) Math.min(model.getRows(), model.getVisibleEndOfRows() + 2);
                
        for (int row = startRow; row < endRow; row++) {
            for (int column = startColumn; column < endColumn; column++) {
                Entity entity = model.tryFetchingTileAt(row, column);
                Tile tile = entity.get(Tile.class);
                                
                int tileX = Camera.getInstance().globalX(entity);
                int tileY = Camera.getInstance().globalY(entity);

                if (tile.getLiquid() > 0) {
                    Animation animation = AssetPool.getInstance().getAnimation(tile.getLiquidId());
                    g.drawImage(animation.toImage(), tileX, tileY, null);
                    animation.update();
                } else {
                    Animation animation = AssetPool.getInstance().getAnimation(tile.getTerrainId());
                    g.drawImage(animation.toImage(), tileX, tileY, null);
                }

                for (BufferedImage heightShadow : tile.shadows) {
                    g.drawImage(heightShadow, tileX, tileY, null);
                }

                if (tile.unit != null) { tilesWithUnits.add(entity); }
                if (tile.unit != null) { tilesWithEntitiesWithNameplates.add(entity); }
                // if (tile.unit != null) { entitiesWithNameplates.add(tile.unit); }
                if (tile.getStructure() > 0) { tilesWithStructures.add(entity); }
                if (tile.getGem() != null) { tilesWithGems.add(entity); }
                if (tile.getExit() > 0) { tilesWithExits.add(entity); }

                OverlayAnimation ca = entity.get(OverlayAnimation.class);
                if (ca.hasOverlay()) { tilesWithOverlayAnimations.add(entity); }
            }
        }
    }

    private void renderPerforatedTile2(Graphics graphics, Entity tile, Color outline, Color main) {
        int globalX = Camera.getInstance().globalX(tile);
        int globalY = Camera.getInstance().globalY(tile);
        int size = 5;
        int newSize = Constants.CURRENT_SPRITE_SIZE - (Constants.CURRENT_SPRITE_SIZE - size);
        graphics.setColor(outline);
        graphics.fillRect(globalX, globalY, Constants.CURRENT_SPRITE_SIZE, newSize);
        graphics.fillRect(globalX, globalY, newSize, Constants.CURRENT_SPRITE_SIZE);
        graphics.fillRect(globalX + Constants.CURRENT_SPRITE_SIZE - newSize, globalY, newSize, Constants.CURRENT_SPRITE_SIZE);
        graphics.fillRect(globalX, globalY + Constants.CURRENT_SPRITE_SIZE - newSize, Constants.CURRENT_SPRITE_SIZE, newSize);
        graphics.setColor(main);
        graphics.fillRect(globalX + size, globalY + size,
                Constants.CURRENT_SPRITE_SIZE - (size * 2), Constants.CURRENT_SPRITE_SIZE - (size * 2));
    }

    private void renderUiHelpers(Graphics graphics, GameModel model, Entity unit) {
        ActionManager manager = unit.get(ActionManager.class);
        MovementManager movement = unit.get(MovementManager.class);

        boolean movementUiOpen = model.gameState.getBoolean(GameState.UI_MOVEMENT_PANEL_SHOWING);
        boolean actionUiOpen = model.gameState.getBoolean(GameState.UI_ACTION_PANEL_SHOWING);

        Entity entity = (Entity) model.gameState.getObject(GameState.CURRENTLY_SELECTED);
        Tile t = entity.get(Tile.class);
        boolean isCurrentTurnAndSelected = t.unit == model.speedQueue.peek();
        if (actionUiOpen == false && (movementUiOpen || isCurrentTurnAndSelected)) { 
            for (Entity tile : movement.movementRange) {
                if (manager.withinRange.contains(tile)) { continue; }
                renderPerforatedTile2(graphics, tile, ColorPalette.TRANSPARENT_BLUE, ColorPalette.TRANSPARENT_BLUE);
                
            }
            if (unit.get(UserBehavior.class) != null) {
                for (Entity tile : movement.movementPath) {
                    renderPerforatedTile2(graphics, tile, ColorPalette.TRANSPARENT_BLUE, ColorPalette.TRANSPARENT_BLUE);
                }
            }
        } else if (actionUiOpen) {
            for (Entity tile : manager.withinRange) {
                if (manager.lineOfSight.contains(tile)) { continue; }
                renderPerforatedTile2(graphics, tile, ColorPalette.TRANSPARENT_GREEN, ColorPalette.TRANSPARENT_GREEN);
            }

            for (Entity tile : manager.lineOfSight) {
                renderPerforatedTile2(graphics, tile, ColorPalette.GREEN, ColorPalette.TRANSPARENT_GREEN);
            }

            for (Entity tile : manager.areaOfEffect) {
                if (manager.lineOfSight.contains(tile)) { continue; }
                renderPerforatedTile2(graphics, tile, ColorPalette.RED, ColorPalette.TRANSPARENT_RED);
            }
        }

        if (manager.targeting != null) {
            renderPerforatedTile2(graphics, manager.targeting, ColorPalette.BLACK, ColorPalette.BLACK);
        }
    }

    private void renderStructures(Graphics graphics, GameModel model, Queue<Entity> queue) {
        while(queue.size() > 0) {
            Entity entity = queue.poll();
            Tile tile = entity.get(Tile.class);
            int tileX = Camera.getInstance().globalX(entity);
            int tileY = Camera.getInstance().globalY(entity);
            Animation structure = AssetPool.getInstance().getAnimation(tile.getStructureId());
            graphics.drawImage(structure.toImage(), tileX - 8, tileY - 8, null);
            structure.update();   
        }
    }

    private void renderGems(Graphics graphics, GameModel model, Queue<Entity> queue) {
        while(queue.size() > 0) {
            Entity entity = queue.poll();
            Tile tile = entity.get(Tile.class);
            int tileX = Camera.getInstance().globalX(entity);
            int tileY = Camera.getInstance().globalY(entity);
            Gem gem = tile.getGem();
            if (gem == null) { continue; }
            graphics.setColor(ColorPalette.TRANSPARENT_WHITE);
            graphics.fillRect(tileX, tileY, Constants.CURRENT_SPRITE_SIZE, Constants.CURRENT_SPRITE_SIZE);
            graphics.setFont(FontPool.getInstance().getFont(10));
            graphics.setColor(ColorPalette.BLACK);
            graphics.drawString(gem.name().substring(0, Math.min(gem.name().length(), 8)), tileX, tileY + Constants.CURRENT_SPRITE_SIZE / 2);
//            Animation animation = AssetPool.getInstance().getAnimation(gem.animationId);
//            graphics.drawImage(animation.toImage(), tileX, tileY, null);
//            animation.update();
        }
    }

    private void renderUnits(Graphics graphics, GameModel model, Queue<Entity> queue) {

        if (model.gameState.getObject(GameState.CURRENTLY_SELECTED) != null) {
            Object object = model.gameState.getObject(GameState.CURRENTLY_SELECTED);
            if (object == null) { return; }
            Entity entity = (Entity) object;
            Tile tile = entity.get(Tile.class);
            // if (model.state.getBoolean(GameStateKey.UI_ACTION_PANEL_SHOWING)) {
            //     // Entity current = model.speedQueue.peek();
            //     // renderUiHelpers(graphics, model, tile.unit);
            // }
            if (tile.unit != null) { renderUiHelpers(graphics, model, tile.unit); }
        }

        while (queue.size() > 0) {
            Entity entity = queue.poll();
            Tile tile = entity.get(Tile.class);
            Entity unit = tile.unit;
            if (unit == null) { continue; } // TODO why is this null sometimes?
            Animation animation = unit.get(Animation.class);

            graphics.drawImage(
                    animation.toImage(),
                    Camera.getInstance().globalX(animation.animatedX()),
                    Camera.getInstance().globalY(animation.animatedY()),
                    null
            );

            OverlayAnimation ca = unit.get(OverlayAnimation.class);
            if (ca.hasOverlay()) {
                animation = ca.getAnimation();
                graphics.drawImage(
                        animation.toImage(),
                        Camera.getInstance().globalX(animation.animatedX()),
                        Camera.getInstance().globalY(animation.animatedY()),
                        null
                );
            }

            // nameplatesToDraw.add(unit);
        }
    }

    private void drawHealthBar(Graphics graphics, Entity unit) {
        // Check if we should render health or energy bar
        Statistics summary = unit.get(Statistics.class);
        ResourceNode energy = summary.getResourceNode(Constants.ENERGY);
        ResourceNode health = summary.getResourceNode(Constants.HEALTH);
        if (health.getPercentage() == 1 && energy.getPercentage() == 1) { return; }

        Animation animation = unit.get(Animation.class);

        int xPosition = (int) animation.position.x;
        int yPosition = (int) animation.position.y + Constants.CURRENT_SPRITE_SIZE;
        int newX = Camera.getInstance().globalX(xPosition);
        int newY = Camera.getInstance().globalY(yPosition);
        graphics.setColor(Color.WHITE);
        graphics.setFont(FontPool.getInstance().getFont(10));

        // Render the energy and health resource bars
        if (energy.getPercentage() != 1 && energy.getPercentage() != 0) {
            renderResourceBar(graphics, newX, newY, Constants.CURRENT_SPRITE_SIZE, energy.getPercentage(),
                    ColorPalette.BLACK, ColorPalette.BLUE, 8);
        }
        if (health.getPercentage() != 1 && energy.getPercentage() != 0) {
            renderResourceBar(graphics, newX, newY - 6, Constants.CURRENT_SPRITE_SIZE, health.getPercentage(),
                    ColorPalette.BLACK, ColorPalette.RED, 8);
        }
    }

    public static void renderResourceBar(Graphics graphics, int x, int y, int size, 
        float amt, Color bg, Color fg, int height) {
        graphics.setColor(bg);
        graphics.fillRoundRect(x, y, size, height, 2, 2);
        float barWidth = MathUtils.mapToRange(amt, 0, 1, 0, size - 4);
        graphics.setColor(fg);
        graphics.fillRoundRect(x + 2, y + 2, (int) barWidth, height / 2, 2, 2);
    }

    public void renderCoordinates(Graphics g, int tileX, int tileY, Dimension dimension, Entity tile) {
        Tile details = tile.get(Tile.class);
        g.setColor(ColorPalette.BLACK);
        g.drawRect(tileX, tileY, (int)dimension.width, (int)dimension.height);

        if (details.isOccupied()) {
            g.setColor(Color.RED);
        } else if (details.getStructure() > 0) {
            g.setColor(Color.GREEN);
        } else if (details.isWall()) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(Color.DARK_GRAY);
        }
        g.fillRect(tileX, tileY, (int)dimension.width, (int)dimension.height);
        g.setColor(Color.BLACK);
        g.drawString(
                details.row + ", " + details.column,
                tileX + (Constants.CURRENT_SPRITE_SIZE / 6),
                tileY + (Constants.CURRENT_SPRITE_SIZE / 2)
        );
    }
}
