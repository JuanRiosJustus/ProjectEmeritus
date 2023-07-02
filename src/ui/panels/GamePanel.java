package ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.PriorityQueue;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import constants.ColorPalette;
import constants.Constants;
import constants.GameStateKey;
import game.camera.Camera;
import game.collectibles.Gem;
import game.components.ActionManager;
import game.components.Animation;
import game.components.Inventory;
import game.components.MovementManager;
import game.components.OverlayAnimation;
import game.components.Tile;
import game.components.Vector;
import game.components.behaviors.UserBehavior;
import game.components.statistics.Energy;
import game.components.statistics.Health;
import game.components.statistics.Resource;
import game.entity.Entity;
import game.main.GameController;
import game.main.GameModel;
import game.stores.pools.AssetPool;
import game.stores.pools.FontPool;
import graphics.JScene;
import logging.Logger;
import logging.LoggerFactory;
import utils.MathUtils;

public class GamePanel extends JScene {
    
    private Logger logger = LoggerFactory.instance().logger(getClass());
    private String last = "";

    private final PriorityQueue<Entity> unitsToDraw = new PriorityQueue<>(10, getZOrdering());

    private final PriorityQueue<Entity> nameplatesToDraw = new PriorityQueue<>(10, getZOrdering());

    private Comparator<Entity> getZOrdering() {
        return (o1, o2) -> {
            Vector v1 = o1.get(Animation.class).position;
            Vector v2 = o2.get(Animation.class).position;
            return (int) (v2.y - v1.y);
        };
    }

    private final GameController gc;

    public GamePanel(GameController controller, int width, int height) {
        super(width, height, "GamePanel");
        gc = controller;

        setPreferredSize(new Dimension(width, height));
        setLayout(new GridBagLayout());
        setBackground(ColorPalette.TRANSPARENT);
        setOpaque(false);
    }

    public void update() {
        revalidate();
        repaint();
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(gc.getModel(), g);
        g.dispose();
    }

    public void render(GameModel model, Graphics g) {
        renderTileMapAndCollectUnits(g, model, unitsToDraw);
        renderUnits(g, model, unitsToDraw);
        renderNamePlates(g, nameplatesToDraw);
        renderCombatAnimationOverlays(g, model);

//        BufferedImage tiles = renderTileMapAndCollectUnits(model, unitsToDraw);
//        BufferedImage units = renderUnits(model, unitsToDraw);
//        BufferedImage nameplates = renderNamePlates(nameplatesToDraw);
        model.system.floatingText.render(g);
        // g.dispose();
    }

    private void renderNamePlates(Graphics g, PriorityQueue<Entity> nameplatesToDraw) {

        while (nameplatesToDraw.size() > 0) {
            drawHealthBar(g, nameplatesToDraw.poll());
        }      
    }

    public void renderCombatAnimationOverlays(Graphics g, GameModel model) {
        int startColumn = (int) Math.max(0, model.getVisibleStartOfColumns());
        int startRow = (int) Math.max(0, model.getVisibleStartOfRows());
        int endColumn = (int) Math.min(model.getColumns(), model.getVisibleEndOfColumns() + 2);
        int endRow = (int) Math.min(model.getRows(), model.getVisibleEndOfRows() + 2);

        for (int row = startRow; row < endRow; row++) {
            for (int column = startColumn; column < endColumn; column++) {
                Entity entity = model.tryFetchingTileAt(row, column);

                if (entity == null) { continue; }

                int tileX = Camera.instance().globalX(entity);
                int tileY = Camera.instance().globalY(entity);

                OverlayAnimation ca = entity.get(OverlayAnimation.class);
                if (ca.hasOverlay()) {
                    BufferedImage image = ca.getAnimation().toImage();
                    g.drawImage(image, tileX, tileY,  null);
                }
            }
        }
    }

    private void renderTileMapAndCollectUnits(Graphics g, GameModel model, PriorityQueue<Entity> queue) {
        int startColumn = (int) Math.max(0, model.getVisibleStartOfColumns());
        int startRow = (int) Math.max(0, model.getVisibleStartOfRows());
        int endColumn = (int) Math.min(model.getColumns(), model.getVisibleEndOfColumns() + 2);
        int endRow = (int) Math.min(model.getRows(), model.getVisibleEndOfRows() + 2);

        boolean showCoordinates = false; //engine.model.ui.settings.showCoordinates.isSelected();

        for (int row = startRow; row < endRow; row++) {
            for (int column = startColumn; column < endColumn; column++) {
                Entity entity = model.tryFetchingTileAt(row, column);
                Dimension d = entity.get(Dimension.class);

                Tile tile = entity.get(Tile.class);
                Inventory inventory = entity.get(Inventory.class);

                if (tile.unit != null) { queue.add(tile.unit); }
                int tileX = Camera.instance().globalX(entity);
                int tileY = Camera.instance().globalY(entity);
;
                if (tile.getLiquid() > 0) {
                    Animation animation = AssetPool.instance().getAnimation(tile.getLiquidId());
                    g.drawImage(animation.toImage(), tileX, tileY, null);
                    animation.update();
                } else {
                    Animation animation = AssetPool.instance().getAnimation(tile.getTerrainId());
                    g.drawImage(animation.toImage(), tileX, tileY, null);
                }

                for (BufferedImage heightShadow : tile.shadows) {
                    g.drawImage(heightShadow, tileX, tileY, null);
                }

            //    g.setColor(Color.WHITE);
            //    g.setFont(FontPool.instance().getFont(10));
            //    g.drawString(tile.row + "," + tile.column, tileX + 16, tileY + 26);


                if (tile.getStructure() > 0) {
                    Animation structure2 = AssetPool.instance().getAnimation(tile.getStructureId());
                    g.drawImage(structure2.toImage(), tileX, tileY, null);
                    structure2.update();

                }

                if (inventory != null) {
//                    g.setColor(Color.WHITE);
//                    g.fillRoundRect(tileX, tileY, 64, 64, 33, 33);
                }

                if (tile.getGem() != null) {
                    Gem buff = tile.getGem();
                    Animation animation = AssetPool.instance().getAnimation(buff.animationId);
                    g.drawImage(animation.toImage(), tileX, tileY, null);
                    animation.update();
                }

                if (showCoordinates) {
                    renderCoordinates(g, tileX, tileY, d, entity);
                }

//                Entity current = engine.model.game.model.queue.peek();
//                if (details.occupyingUnit == current) {
////                    renderUiHelpers(g, engine, current);
//                }
            }
        }
    }

    private void renderPerforatedTile2(Graphics graphics, Entity tile, Color outline, Color main) {
        int globalX = Camera.instance().globalX(tile);
        int globalY = Camera.instance().globalY(tile);
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

        for (Entity tile : movement.tilesWithinMovementRange) {
            if (manager.tilesWithinActionRange.contains(tile)) { continue; }
            renderPerforatedTile2(graphics, tile, ColorPalette.TRANSPARENT_BLUE, ColorPalette.TRANSPARENT_BLUE);
        }

        if (model.state.getBoolean(GameStateKey.UI_MOVEMENT_PANEL_SHOWING)) {
            if (unit.get(UserBehavior.class) != null) {
                for (Entity tile : movement.tilesWithinMovementPath) {
                    renderPerforatedTile2(graphics, tile, ColorPalette.TRANSPARENT_BLUE, ColorPalette.TRANSPARENT_BLUE);
                }
            }
        } else if (model.state.getBoolean(GameStateKey.UI_ACTION_PANEL_SHOWING) || true) {
            for (Entity tile : manager.tilesWithinActionRange) {
                if (manager.tilesWithinActionLOS.contains(tile)) { continue; }
                renderPerforatedTile2(graphics, tile, ColorPalette.TRANSPARENT_GREEN, ColorPalette.TRANSPARENT_GREEN);
            }

            for (Entity tile : manager.tilesWithinActionLOS) {
                renderPerforatedTile2(graphics, tile, ColorPalette.TRANSPARENT_BLACK, ColorPalette.TRANSPARENT_GREY);
            }

            for (Entity tile : manager.tilesWithinActionAOE) {
                if (manager.tilesWithinActionLOS.contains(tile)) { continue; }
                renderPerforatedTile2(graphics, tile, ColorPalette.TRANSPARENT_GREY, ColorPalette.TRANSPARENT_GREY);
            }
        }

        if (manager.targeting != null) {
            renderPerforatedTile2(graphics, manager.targeting, ColorPalette.BLACK, ColorPalette.BLACK);
        }
    }

    private void renderUnits(Graphics graphics, GameModel model, PriorityQueue<Entity> queue) {

        if (model.state.getObject(GameStateKey.CURRENTLY_SELECTED) != null) {
            Object object = model.state.getObject(GameStateKey.CURRENTLY_SELECTED);
            if (object == null) { return; }
            Entity entity = (Entity) object;
            Tile tile = entity.get(Tile.class);
            if (tile.unit != null) { renderUiHelpers(graphics, model, tile.unit); }
        }

        Entity currentEntitiesTurn = model.unitTurnQueue.peek();
        if (currentEntitiesTurn != null) {
            // TODO
//            renderUiHelpers(graphics, model, currentEntitiesTurn);
        }

//        System.out.println(queue.size() + " ?");
        while (queue.size() > 0) {
            Entity unit = queue.poll();
            Animation animation = unit.get(Animation.class);
//            TileSelectionState selection = creature.getComponent(TileSelectionState.class);
            // ActionManager manager = unit.get(ActionManager.class);
        //    System.out.println(animation.toImage().getHeight() + " , " + animation.toImage().getWidth());
            graphics.drawImage(
                    animation.toImage(),
                    Camera.instance().globalX(animation.animatedX()),
                    Camera.instance().globalY(animation.animatedY()),
                    null
            );

            OverlayAnimation ca = unit.get(OverlayAnimation.class);
            if (ca.hasOverlay()) {
                animation = ca.getAnimation();
                graphics.drawImage(
                        animation.toImage(),
                        Camera.instance().globalX(animation.animatedX()),
                        Camera.instance().globalY(animation.animatedY()),
                        null
                );
            }

            nameplatesToDraw.add(unit);
        }
    }

    private void drawHealthBar(Graphics graphics, Entity unit) {
        // Check if we should render health or energy bar
        Resource energy = unit.get(Energy.class);
        Resource health = unit.get(Health.class);
        if (health.percentage() == 1 && energy.percentage() == 1) { return; }


        Animation animation = unit.get(Animation.class);
        // Statistics statistics = unit.get(Statistics.class);

        int xPosition = (int) animation.position.x;
        int yPosition = (int) animation.position.y + Constants.CURRENT_SPRITE_SIZE;
        int newX = Camera.instance().globalX(xPosition);
        int newY = Camera.instance().globalY(yPosition);
        graphics.setColor(Color.WHITE);
        graphics.setFont(FontPool.instance().getFont(10));

//        renderNamePlate(graphics, Constants.SPRITE_SIZE, newX, newY - 9, ColorPalette.BLACK, name);

        // Render the energy and health resource bars
        if (energy.percentage() != 1) {
            renderResourceBar(graphics, newX, newY, Constants.CURRENT_SPRITE_SIZE, energy.percentage(),
                    ColorPalette.BLACK, ColorPalette.BLUE);
        }
        if (health.percentage() != 1) {
            renderResourceBar(graphics, newX, newY - 5, Constants.CURRENT_SPRITE_SIZE, health.percentage(),
                    ColorPalette.BLACK, ColorPalette.GREEN);
        }
    }

    public static void renderResourceBar(Graphics graphics, int x, int y, int size, float amt, Color bg, Color fg) {
        graphics.setColor(bg);
        graphics.fillRoundRect(x, y, size, 5, 5, 5);
        float barWidth = MathUtils.mapToRange(amt, 0, 1, 0, size - 4);
        graphics.setColor(fg);
        graphics.fillRoundRect(x + 2, y + 1, (int) barWidth, 2, 5, 5);
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
