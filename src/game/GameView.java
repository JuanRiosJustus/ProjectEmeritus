package game;

import constants.ColorPalette;
import constants.Constants;
import game.camera.Camera;
import game.components.*;
import game.components.Dimension;
import game.components.SpriteAnimation;
import game.components.statistics.Energy;
import game.components.statistics.Health;
import game.components.statistics.Resource;
import game.components.statistics.Statistics;
import game.components.Tile;
import game.entity.Entity;
import game.stores.pools.FontPool;
import input.InputController;
import ui.screen.Ui;
import utils.MathUtils;
//import core.Camera;
//import core.Entity;
//import core.components.*;
//import creature.Creature;
//import creature.stats.Statistics;
//import engine.GameEngine;
//import io.stores.FontStore;
//import ui.ColorPalette;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.PriorityQueue;


public class GameView extends JPanel {
    private GameModel model = null;
    private GameController controller;
    public final Ui ui = new Ui();

    public void initialize(GameController gc) {
        controller = gc;
        removeAll();
        setOpaque(true);
        setDoubleBuffered(true);
        setVisible(true);
        listen(controller.input);
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        if (model == null) { return; }
        render(model, g);
    }

    public void listen(InputController controls) {
        setFocusable(true);
        addMouseListener(controls.mouse());
        addMouseMotionListener(controls.mouse());
        addMouseWheelListener(controls.mouse());
        requestFocusInWindow();
    }

    private final PriorityQueue<Entity> unitsToDraw = new PriorityQueue<>(10, getZOrdering());
    private final PriorityQueue<Entity> nameplatesToDraw = new PriorityQueue<>(10, getZOrdering());

    private Comparator<Entity> getZOrdering() {
        return (o1, o2) -> {
            Vector v1 = o1.get(SpriteAnimation.class).position;
            Vector v2 = o2.get(SpriteAnimation.class).position;
            return (int) (v2.y - v1.y);
        };
    }

//    public void render(EngineController engine, Graphics g) {
//        renderTileMapAndCollectUnits(g, engine, unitsToDraw);
//        renderUnits(g, engine, unitsToDraw);
//        renderNamePlates(g, engine, nameplatesToDraw);
//        FloatingTextSystem.render(g);
//    }

    public void update() {
        model = controller.model;
        ui.update(controller.model);
    }

    public void render(GameModel model, Graphics g) {
        renderTileMapAndCollectUnits(g, model, unitsToDraw);
        renderUnits(g, model, unitsToDraw);
        renderNamePlates(g, nameplatesToDraw);
        model.system.floatingText.render(g);
    }

    private void renderNamePlates(Graphics g, PriorityQueue<Entity> nameplatesToDraw) {
        while (nameplatesToDraw.size() > 0) {
            drawHealthBar(g, nameplatesToDraw.poll());
        }
    }

//    private void renderHovered(Graphics graphics, EngineController e, Color color1, Color color2) {
//        Entity tile = e.model.game.model.tryFetchingMousedTile();
//        graphics.setColor(ColorPalette.TRANSPARENT_GREY);
//        if (tile == null) { return; }
//        int x = Camera.get().globalX(tile);
//        int y = Camera.get().globalY(tile);
//        Dimension d = tile.get(Dimension.class);
//        int intervalSize = 3;
//        boolean offColor = false;
//        for (int interval = 0; interval < Constants.CURRENT_SPRITE_SIZE; interval += intervalSize) {
//            graphics.setColor((offColor ? color1 : color2));
//            graphics.fillOval(x + interval, y + interval,
//                    (int)d.width - (interval * 2), (int)d.height - (interval * 2));
////            graphics.fillRoundRect(x + interval, y + interval,
////                    (int)d.width - (interval * 2), (int)d.height - (interval * 2),
////                    30, 30);
//            offColor = !offColor;
//        }
//    }

    //
//    private void renderScreenData(Graphics g, GameModel model) {
//        g.setFont(FontStore.getInstance().getFont(35));
//        g.setColor(Color.WHITE);
//        Vector pv = Camera.get().getComponent(Vector.class);
//        int x = (int) pv.x;
//        int y = (int) pv.y;
//        g.drawString( x + ", " + y, 20, 20);
//        x = 20;
//        y = 20 + g.getFont().getSize();
//        g.drawString(String.valueOf(GameEngine.get().getFPS()), x, y);
//    }
//
//    private void renderGameLogs(Graphics g) {
//
//    }
//
//

    public void renderTileMapAndCollectUnits(Graphics g, GameModel model, PriorityQueue<Entity> queue) {

        int startColumn = (int) Math.max(0, model.getVisibleStartOfColumns());
        int startRow = (int) Math.max(0, model.getVisibleStartOfRows());
        int endColumn = (int) Math.min(model.getColumns(), model.getVisibleEndOfColumns() + 2);
        int endRow = (int) Math.min(model.getRows(), model.getVisibleEndOfRows() + 2);

        boolean showCoordinates = false; //engine.model.ui.settings.showCoordinates.isSelected();

        for (int row = startRow; row < endRow; row++) {
            for (int column = startColumn; column < endColumn; column++) {
                Entity tile = model.tryFetchingTileAt(row, column);
                Dimension d = tile.get(Dimension.class);
//                Selectable selection = tile.getComponent(Selectable.class);
                Tile details = tile.get(Tile.class);
                Inventory inventory = tile.get(Inventory.class);
////                if (meta.occupyingUnit != null) { System.out.println("Yooo");}
                if (details.unit != null) { queue.add(details.unit); }
                int tileX = Camera.get().globalX(tile);
                int tileY = Camera.get().globalY(tile);



                if (details.getSpecialAnimation() != null) {
                    g.drawImage(details.getSpecialAnimation().toImage(), tileX, tileY, null);
                    details.getSpecialAnimation().update();
                } else {
                    g.drawImage(details.getTerrainImage(), tileX, tileY, null);
                }
//                g.drawImage(details.getTerrainImage(), tileX, tileY, null);

                if (details.getShadowImage() != null) {
                    g.drawImage(details.getShadowImage(), tileX, tileY, null);
                }

                for (BufferedImage heightShadow : details.shadows) {
                    g.drawImage(heightShadow, tileX, tileY, null);
                }

                g.setColor(Color.WHITE);
                g.setFont(FontPool.instance().getFont(8));
                g.drawString(details.getHeight() + " ", tileX + 16, tileY + 26);


//                if (details.getLiquidImage() != null) {
//                    g.drawImage(details.getLiquidImage(), tileX, tileY, null);
//                } else {
//                    g.drawImage(details.getTerrainImage(), tileX, tileY, null);
//                }

                if (details.getStructureImage() != null) {
                    g.drawImage(details.getStructureImage(), tileX, tileY, null);
                }

                if (inventory != null) {
                    g.setColor(Color.WHITE);
                    g.fillRoundRect(tileX, tileY, 64, 64, 33, 33);
                }

                if (showCoordinates) {
                    renderCoordinates(g, tileX, tileY, d, tile);
                }
//                Entity current = engine.model.game.model.queue.peek();
//                if (details.occupyingUnit == current) {
////                    renderUiHelpers(g, engine, current);
//                }
            }
        }
    }

//    public void renderTileMapAndCollectUnits(Graphics g, EngineController engine, PriorityQueue<Entity> queue) {
//
//        GameModel model = engine.model.game.model;
//        int startColumn = (int) Math.max(0, model.getVisibleStartOfColumns());
//        int startRow = (int) Math.max(0, model.getVisibleStartOfRows());
//        int endColumn = (int) Math.min(model.getColumns(), model.getVisibleEndOfColumns() + 2);
//        int endRow = (int) Math.min(model.getRows(), model.getVisibleEndOfRows() + 2);
//
//        boolean showCoordinates = engine.model.ui.settings.showCoordinates.isSelected();
//
//        for (int row = startRow; row < endRow; row++) {
//            for (int column = startColumn; column < endColumn; column++) {
//                Entity tile = model.tryFetchingTileAt(row, column);
//                Dimension d = tile.get(Dimension.class);
////                Selectable selection = tile.getComponent(Selectable.class);
//                Tile details = tile.get(Tile.class);
//                Inventory inventory = tile.get(Inventory.class);
//////                if (meta.occupyingUnit != null) { System.out.println("Yooo");}
//                if (details.unit != null) { queue.add(details.unit); }
//                int tileX = Camera.get().globalX(tile);
//                int tileY = Camera.get().globalY(tile);
//
//
//                if (details.getLiquidImage() != null) {
//                    g.drawImage(details.getLiquidImage(), tileX, tileY, null);
//                } else {
//                    g.drawImage(details.getBaseImage(), tileX, tileY, null);
//                }
//
//                if (details.getStructureImage() != null) {
//                    g.drawImage(details.getStructureImage(), tileX, tileY, null);
//                }
//
//                if (inventory != null) {
//                    g.setColor(Color.WHITE);
//                    g.fillRoundRect(tileX, tileY, 64, 64, 33, 33);
//                }
//
//                if (showCoordinates) {
//                    renderCoordinates(g, tileX, tileY, d, tile);
//                }
////                Entity current = engine.model.game.model.queue.peek();
////                if (details.occupyingUnit == current) {
//////                    renderUiHelpers(g, engine, current);
////                }
//            }
//        }
//    }
//
    private void renderTile(Graphics g, Entity t, Color c, boolean fill) {
        int globalX = Camera.get().globalX(t);
        int globalY = Camera.get().globalY(t);
        g.setColor(c);
        if (fill) {
            g.fillRoundRect(
                    globalX,
                    globalY,
                    Constants.CURRENT_SPRITE_SIZE,
                    Constants.CURRENT_SPRITE_SIZE,
                    25,
                    25
            );
        } else {
            g.drawRoundRect(
                    globalX,
                    globalY,
                    Constants.CURRENT_SPRITE_SIZE,
                    Constants.CURRENT_SPRITE_SIZE,
                    25,
                    25
            );
        }
    }
    private void renderPerforatedTile(Graphics graphics, Entity tile, Color outline, Color main) {
        int globalX = Camera.get().globalX(tile);
        int globalY = Camera.get().globalY(tile);
        graphics.setColor(main);
        graphics.fillRect(globalX, globalY, Constants.CURRENT_SPRITE_SIZE, Constants.CURRENT_SPRITE_SIZE);
        graphics.setColor(outline);
        graphics.drawRect(globalX, globalY, Constants.CURRENT_SPRITE_SIZE, Constants.CURRENT_SPRITE_SIZE);
    }

    private void renderPerforatedTile2(Graphics graphics, Entity tile, Color outline, Color main) {
        int globalX = Camera.get().globalX(tile);
        int globalY = Camera.get().globalY(tile);
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

    private void renderPerf(Graphics g, Entity t, Color c) {
        int globalX = Camera.get().globalX(t);
        int globalY = Camera.get().globalY(t);
        g.setColor(c);
        g.fillRect(
                globalX,
                globalY,
                Constants.CURRENT_SPRITE_SIZE,
                Constants.CURRENT_SPRITE_SIZE
        );
        g.setColor(ColorPalette.TRANSPARENT_BLACK);
        g.fillRect(
                globalX + 5,
                globalY + 5,
                Constants.CURRENT_SPRITE_SIZE - 10,
                Constants.CURRENT_SPRITE_SIZE - 10
        );
    }
//
    private void renderUiHelpers(Graphics graphics, GameModel model, Entity unit) {
        ActionManager manager = unit.get(ActionManager.class);
//        if (engine.model.ui.movement.isShowing()) {
        if (model.ui.getBoolean(Constants.MOVEMENT_UI_SHOWING)) {
            // Render tile outline that can be moved to
            for (Entity tile : manager.tilesWithinMovementRange) {
                if (manager.tilesWithinMovementRangePath.contains(tile)) { continue; }
                if (manager.tilesWithinAbilityRange.contains(tile)) { continue; }
//                renderPerf(graphics, tile, Color.DARK_GRAY);
                renderPerforatedTile2(graphics, tile, ColorPalette.BEIGE, ColorPalette.TRANSPARENT_BEIGE);
            }
            // Render tile outline for tiles within ability range
            for (Entity tile : manager.tilesWithinAbilityRange) {
                if (manager.tilesWithinMovementRangePath.contains(tile)) { continue; }
                renderPerforatedTile2(graphics, tile, ColorPalette.RED, ColorPalette.TRANSPARENT_RED);
//                renderPerf(graphics, tile, Color.GRAY);
//                renderPerferatedTile(graphics, tile, ColorPalette.BEIGE);
            }
            // Render tiles outline for potential path
            for (Entity tile : manager.tilesWithinMovementRangePath) {
                renderPerforatedTile2(graphics, tile, ColorPalette.BLACK, ColorPalette.TRANSPARENT_WHITE);
            }
        }
//        if (engine.model.ui.ability.isShowing()) {
        if (model.ui.getBoolean(Constants.ABILITY_UI_SHOWING)) {
            for (Entity tile : manager.tilesWithinAbilityRange) {
                if (manager.tilesWithinAreaOfEffect.contains(tile)) { continue; }
                renderPerforatedTile2(graphics, tile, ColorPalette.BLUE, ColorPalette.TRANSPARENT_BLUE);
            }
            for (Entity tile : manager.tilesWithinAreaOfEffect) {
                renderPerforatedTile2(graphics, tile, ColorPalette.BLACK, ColorPalette.TRANSPARENT_WHITE);
            }
        }
    }

    private void renderUnits(Graphics graphics, GameModel model, PriorityQueue<Entity> queue) {

        Entity currentEntitiesTurn = model.queue.peek();
        if (currentEntitiesTurn != null) {
            // TODO
            renderUiHelpers(graphics, model, currentEntitiesTurn);
        }

//        System.out.println(queue.size() + " ?");
        while (queue.size() > 0) {
            Entity unit = queue.poll();
            SpriteAnimation spriteAnimation = unit.get(SpriteAnimation.class);
//            TileSelectionState selection = creature.getComponent(TileSelectionState.class);
            ActionManager manager = unit.get(ActionManager.class);
//            System.out.println(animation.toImage().getHeight() + " , " + animation.toImage().getWidth());
            graphics.drawImage(
                    spriteAnimation.toImage(),
                    Camera.get().globalX(spriteAnimation.animatedX()),
                    Camera.get().globalY(spriteAnimation.animatedY()),
                    null
            );

            CombatAnimations ca = unit.get(CombatAnimations.class);
            if (ca.count() > 0) {
                SpriteAnimation animation = ca.getCurrentAnimation();
                graphics.drawImage(
                        animation.toImage(),
                        Camera.get().globalX(spriteAnimation.animatedX()),
                        Camera.get().globalY(spriteAnimation.animatedY()),
                        null
                );
            }
            nameplatesToDraw.add(unit);
        }
    }

//    private void renderUnits(Graphics graphics, EngineController engine, PriorityQueue<Entity> queue) {
//
//        Entity currentEntitiesTurn = engine.model.game.model.queue.peek();
//        if (currentEntitiesTurn != null) {
//            renderUiHelpers(graphics, engine, currentEntitiesTurn);
//        }
//
////        System.out.println(queue.size() + " ?");
//        while (queue.size() > 0) {
//            Entity unit = queue.poll();
//            SpriteAnimation spriteAnimation = unit.get(SpriteAnimation.class);
////            TileSelectionState selection = creature.getComponent(TileSelectionState.class);
//            ActionManager manager = unit.get(ActionManager.class);
////            System.out.println(animation.toImage().getHeight() + " , " + animation.toImage().getWidth());
//            graphics.drawImage(
//                    spriteAnimation.toImage(),
//                    Camera.get().globalX(spriteAnimation.animatedX()),
//                    Camera.get().globalY(spriteAnimation.animatedY()),
//                    null
//            );
//
//            CombatAnimations ca = unit.get(CombatAnimations.class);
//            if (ca.count() > 0) {
//                SpriteAnimation animation = ca.getCurrentAnimation();
//                graphics.drawImage(
//                        animation.toImage(),
//                        Camera.get().globalX(spriteAnimation.animatedX()),
//                        Camera.get().globalY(spriteAnimation.animatedY()),
//                        null
//                );
//            }
//            nameplatesToDraw.add(unit);
//        }
//    }

    private void drawHealthBar(Graphics graphics, Entity unit) {
        SpriteAnimation spriteAnimation = unit.get(SpriteAnimation.class);
        Statistics statistics = unit.get(Statistics.class);
        int xPosition = (int) spriteAnimation.position.x;
        int yPosition = (int) spriteAnimation.position.y + Constants.CURRENT_SPRITE_SIZE;
        int newX = Camera.get().globalX(xPosition);
        int newY = Camera.get().globalY(yPosition);
        String name = unit.get(Name.class).value;
        graphics.setColor(Color.WHITE);
        graphics.setFont(FontPool.instance().getFont(10));


//        renderNamePlate(graphics, Constants.SPRITE_SIZE, newX, newY - 9, ColorPalette.BLACK, name);

        // Render the energy and health resource bars
        Resource resource = unit.get(Energy.class);
        renderResourceBar(graphics, newX, newY, Constants.CURRENT_SPRITE_SIZE, resource.percentage(),
                ColorPalette.BLACK, ColorPalette.BLUE);
        resource = unit.get(Health.class);
        renderResourceBar(graphics, newX, newY - 5, Constants.CURRENT_SPRITE_SIZE, resource.percentage(),
                ColorPalette.BLACK, ColorPalette.GREEN);
    }

    public static void renderNamePlate(Graphics graphics, int size, int x, int y, Color bg, String name) {
        int fontSize = graphics.getFont().getSize();
        int newY = y - (fontSize / 2);
        graphics.setColor(bg);
        graphics.fillRoundRect(x, newY, size, fontSize + 2, 5, 5);
        graphics.setColor(ColorPalette.WHITE);
        graphics.drawString((name.length() > 12 ? name.substring(0, Math.min(name.length(), 11)): name),
                x + 3, newY + (fontSize / 2) + 3);
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
        } else if (details.getStructureImage() != null) {
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
