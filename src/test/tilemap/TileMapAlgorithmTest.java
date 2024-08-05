package test.tilemap;

import main.constants.Constants;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.map.base.TileMap;
import main.game.map.base.TileMapAlgorithm;
import main.game.map.base.TileMapFactory;
import main.game.map.base.TileMapParameters;
import org.junit.Assert;
import org.junit.Test;

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TileMapAlgorithmTest {

    @Test
    public void getAllTilesWithinSmallestRoom() {
        int rows = 10, columns = 10;
        int row = 3, column = 3, width = 1, height = 1;
        int expectedTileCount = width * height;
        String spriteMap = Constants.TILES_SPRITEMAP_FILEPATH;
        TileMapParameters tileMapParameters = TileMapParameters.getDefaultParameters(rows, columns, spriteMap);
        TileMap tileMap = TileMapFactory.create(tileMapParameters);
        Rectangle room = new Rectangle(column, row, width, height);
        Set<Tile> tiles = TileMapAlgorithm.getAllTilesOfRoom(tileMap, room);

        Assert.assertNotNull(tiles);
        Assert.assertNotEquals(tiles.size(), 0);
        Assert.assertEquals(tiles.size(), expectedTileCount);
        for (Tile tile : tiles) {
            Assert.assertTrue(tile.getRow() >= row && tile.getRow() < row + height);
            Assert.assertTrue(tile.getColumn() >= column && tile.getColumn() < column + width);
        }
    }

    @Test
    public void getAllTilesWithinPersonSizeRoom() {
        int rows = 10, columns = 10;
        int row = 3, column = 3, width = 3, height = 3;
        int expectedTileCount = width * height;
        String spriteMap = Constants.TILES_SPRITEMAP_FILEPATH;
        TileMapParameters tileMapParameters = TileMapParameters.getDefaultParameters(rows, columns, spriteMap);
        TileMap tileMap = TileMapFactory.create(tileMapParameters);
        Rectangle room = new Rectangle(column, row, width, height);
        Set<Tile> tiles = TileMapAlgorithm.getAllTilesOfRoom(tileMap, room);

        Assert.assertNotNull(tiles);
        Assert.assertNotEquals(tiles.size(), 0);
        Assert.assertEquals(tiles.size(), expectedTileCount);
        for (Tile tile : tiles) {
            Assert.assertTrue(tile.getRow() >= row && tile.getRow() < row + height);
            Assert.assertTrue(tile.getColumn() >= column && tile.getColumn() < column + width);
        }
    }

    @Test
    public void getAllTilesWithinLargestRoom() {
        int rows = 10, columns = 10;
        int row = 0, column = 0, width = columns, height = rows;
        int expectedTileCount = width * height;
        String spriteMap = Constants.TILES_SPRITEMAP_FILEPATH;
        TileMapParameters tileMapParameters = TileMapParameters.getDefaultParameters(rows, columns, spriteMap);
        TileMap tileMap = TileMapFactory.create(tileMapParameters);
        
        Rectangle room = new Rectangle(column, row, width, height);
        Set<Tile> tiles = TileMapAlgorithm.getAllTilesOfRoom(tileMap, room);

        Assert.assertNotNull(tiles);
        Assert.assertNotEquals(tiles.size(), 0);
        Assert.assertEquals(tiles.size(), expectedTileCount);
        for (Tile tile : tiles) {
            Assert.assertTrue(tile.getRow() >= row && tile.getRow() < row + height);
            Assert.assertTrue(tile.getColumn() >= column && tile.getColumn() < column + width);
        }
    }

    @Test
    public void getAllWallTilesOfSmallestRoom() {
        int rows = 10, columns = 10;
        int row = 3, column = 3, width = 1, height = 1;
        String spriteMap = Constants.TILES_SPRITEMAP_FILEPATH;
        TileMapParameters tileMapParameters = TileMapParameters.getDefaultParameters(rows, columns, spriteMap);
        TileMap tileMap = TileMapFactory.create(tileMapParameters);
        
        Rectangle room = new Rectangle(column, row, width, height);
        Set<Tile> tiles = TileMapAlgorithm.getWallTilesOfRoom(tileMap, room);

        Assert.assertNotNull(tiles);
        Assert.assertEquals(tiles.size(), 1);
    }

    @Test
    public void getAllWallTilesOfModestRoom() {
        int rows = 10, columns = 10;
        int row = 3, column = 3, width = 4, height = 4;
        String spriteMap = Constants.TILES_SPRITEMAP_FILEPATH;
        TileMapParameters tileMapParameters = TileMapParameters.getDefaultParameters(rows, columns, spriteMap);
        TileMap tileMap = TileMapFactory.create(tileMapParameters);
        
        Rectangle room = new Rectangle(column, row, width, height);
        Set<Tile> tiles = TileMapAlgorithm.getAllTilesOfRoom(tileMap, room);
        Set<Tile> walls = TileMapAlgorithm.getWallTilesOfRoom(tileMap, room);

        Assert.assertNotNull(walls);
        Assert.assertNotNull(tiles);
        Assert.assertNotEquals(walls.size(), tiles.size());
        for (Tile tile : walls) {
            // Some part of the tile must hug the outer bounds
            boolean isVerticalBoundary = tile.getRow() == row || tile.getRow() == (row + height) - 1;
            boolean isHorizontalBoundary = tile.getColumn() == column || tile.getColumn() == (column + width) - 1;
            Assert.assertTrue(isHorizontalBoundary || isVerticalBoundary );
        }
    }

    @Test
    public void getAllWallTilesOfLargestRoom() {
        int rows = 10, columns = 10;
        int row = 0, column = 0, width = columns, height = rows;
        String spriteMap = Constants.TILES_SPRITEMAP_FILEPATH;
        TileMapParameters tileMapParameters = TileMapParameters.getDefaultParameters(rows, columns, spriteMap);
        TileMap tileMap = TileMapFactory.create(tileMapParameters);
        
        Rectangle room = new Rectangle(column, row, width, height);

        Set<Tile> tiles = TileMapAlgorithm.getAllTilesOfRoom(tileMap, room);
        Set<Tile> walls = TileMapAlgorithm.getWallTilesOfRoom(tileMap, room);

        Assert.assertNotNull(walls);
        Assert.assertNotNull(tiles);
        Assert.assertNotEquals(walls.size(), tiles.size());
        for (Tile tile : walls) {
            // Some part of the tile must hug the outer bounds
            boolean isVerticalBoundary = tile.getRow() == row || tile.getRow() == (row + height) - 1;
            boolean isHorizontalBoundary = tile.getColumn() == column || tile.getColumn() == (column + width) - 1;
            Assert.assertTrue(isHorizontalBoundary || isVerticalBoundary );
        }
    }

    @Test
    public void getAllCornerTilesOfSmallestRoom() {
        int rows = 10, columns = 10;
        int row = 3, column = 3, width = 1, height = 1;
        String spriteMap = Constants.TILES_SPRITEMAP_FILEPATH;
        TileMapParameters tileMapParameters = TileMapParameters.getDefaultParameters(rows, columns, spriteMap);
        TileMap tileMap = TileMapFactory.create(tileMapParameters);
        
        Rectangle room = new Rectangle(column, row, width, height);
        Set<Tile> tiles = TileMapAlgorithm.getCornerTilesOfRoom(tileMap, room);

        Assert.assertNotNull(tiles);
        Assert.assertEquals(tiles.size(), 1);
    }

    @Test
    public void getAllCornerTilesOfModestRoom() {
        int rows = 10, columns = 10;
        int row = 3, column = 3, width = 4, height = 4;
        String spriteMap = Constants.TILES_SPRITEMAP_FILEPATH;
        TileMapParameters tileMapParameters = TileMapParameters.getDefaultParameters(rows, columns, spriteMap);
        TileMap tileMap = TileMapFactory.create(tileMapParameters);
        
        Rectangle room = new Rectangle(column, row, width, height);
        Set<Tile> tiles = TileMapAlgorithm.getAllTilesOfRoom(tileMap, room);
        Set<Tile> corners = TileMapAlgorithm.getCornerTilesOfRoom(tileMap, room);

        Assert.assertNotNull(corners);
        Assert.assertNotNull(tiles);
        Assert.assertNotEquals(corners.size(), tiles.size());
        Assert.assertEquals(4, corners.size());
        for (Tile tile : corners) {
            // Some part of the tile must hug the outer bounds
            boolean isVerticalBoundary = tile.getRow() == row || tile.getRow() == (row + height) - 1;
            boolean isHorizontalBoundary = tile.getColumn() == column || tile.getColumn() == (column + width) - 1;
            Assert.assertTrue(isHorizontalBoundary || isVerticalBoundary );
        }
    }

    @Test
    public void getAllCornerTilesOfLargestRoom() {
        int rows = 10, columns = 10;
        int row = 0, column = 0, width = columns, height = rows;
        String spriteMap = Constants.TILES_SPRITEMAP_FILEPATH;
        TileMapParameters tileMapParameters = TileMapParameters.getDefaultParameters(rows, columns, spriteMap);
        TileMap tileMap = TileMapFactory.create(tileMapParameters);
        
        Rectangle room = new Rectangle(column, row, width, height);

        Set<Tile> tiles = TileMapAlgorithm.getAllTilesOfRoom(tileMap, room);
        Set<Tile> corners = TileMapAlgorithm.getCornerTilesOfRoom(tileMap, room);

        Assert.assertNotNull(corners);
        Assert.assertNotNull(tiles);
        Assert.assertNotEquals(corners.size(), tiles.size());
        Assert.assertEquals(4, corners.size());
        for (Tile tile : corners) {
            // Some part of the tile must hug the outer bounds
            boolean isVerticalBoundary = tile.getRow() == row || tile.getRow() == (row + height) - 1;
            boolean isHorizontalBoundary = tile.getColumn() == column || tile.getColumn() == (column + width) - 1;
            Assert.assertTrue(isHorizontalBoundary || isVerticalBoundary );
        }
    }

    @Test
    public void correctGetsConnectingRowsSmallest() {
        int rows = 10, columns = 10, column = columns / 2, size = 1;
        TileMap tileMap = setupMap(rows, columns, true);

        Set<Tile> connectedRows = TileMapAlgorithm.getConnectingRows(tileMap, 0, rows, column, size);
        for (Tile tile : connectedRows) {
            Assert.assertTrue(tile.getColumn() >= column - size);
            Assert.assertTrue(tile.getColumn() < column + size);
            Assert.assertTrue(tile.getRow() >= 0);
            Assert.assertTrue(tile.getRow() < rows);
        }
    }

    @Test
    public void correctGetsConnectingRowsModest() {
        int rows = 10, columns = 10, column = columns / 2, size = 3;
        TileMap tileMap = setupMap(rows, columns, true);

        Set<Tile> connectedRows = TileMapAlgorithm.getConnectingRows(tileMap, 0, rows, column, size);
        for (Tile tile : connectedRows) {
            Assert.assertTrue(tile.getColumn() >= column - size);
            Assert.assertTrue(tile.getColumn() < column + size);
            Assert.assertTrue(tile.getRow() >= 0);
            Assert.assertTrue(tile.getRow() < rows);
        }
    }

    @Test
    public void correctGetsConnectingRowsLargest() {
        int rows = 10, columns = 10, column = columns / 2, size = 10;
        TileMap tileMap = setupMap(rows, columns, true);

        Set<Tile> connectedRows = TileMapAlgorithm.getConnectingRows(tileMap, 0, rows, column, size);
        for (Tile tile : connectedRows) {
            Assert.assertTrue(tile.getColumn() >= column - size);
            Assert.assertTrue(tile.getColumn() < column + size);
            Assert.assertTrue(tile.getRow() >= 0);
            Assert.assertTrue(tile.getRow() < rows);
        }
    }

    @Test
    public void correctGetsConnectingColumnsSmallest() {
        int rows = 10, columns = 10, row = rows / 2, size = 1;
        TileMap tileMap = setupMap(rows, columns, true);

        Set<Tile> connectedRows = TileMapAlgorithm.getConnectingColumns(tileMap, 0, columns, row, size);
        for (Tile tile : connectedRows) {
            Assert.assertTrue(tile.getColumn() >= 0);
            Assert.assertTrue(tile.getColumn() < columns);
            Assert.assertTrue(tile.getRow() >= row - size);
            Assert.assertTrue(tile.getRow() < row + size);
        }
    }

    @Test
    public void correctGetsConnectingColumnsModest() {
        int rows = 10, columns = 10, row = rows / 2, size = 3;
        TileMap tileMap = setupMap(rows, columns, true);

        Set<Tile> connectedRows = TileMapAlgorithm.getConnectingColumns(tileMap, 0, columns, row, size);
        for (Tile tile : connectedRows) {
            Assert.assertTrue(tile.getColumn() >= 0);
            Assert.assertTrue(tile.getColumn() < columns);
            Assert.assertTrue(tile.getRow() >= row - size);
            Assert.assertTrue(tile.getRow() < row + size);
        }
    }

    @Test
    public void correctGetsConnectingColumnsLargest() {
        int rows = 10, columns = 10, row = rows / 2, size = 10;
        TileMap tileMap = setupMap(rows, columns, true);

        Set<Tile> connectedRows = TileMapAlgorithm.getConnectingColumns(tileMap, 0, columns, row, size);
        for (Tile tile : connectedRows) {
            Assert.assertTrue(tile.getColumn() >= 0);
            Assert.assertTrue(tile.getColumn() < columns);
            Assert.assertTrue(tile.getRow() >= row - size);
            Assert.assertTrue(tile.getRow() < row + size);
        }
    }

    @Test
    public void correctCreates2ConnectedRooms() {
        int rows = 10, columns = 10, row = rows / 2, size = 10;
        int roomSize = 3;
        TileMap tileMap = setupMap(rows, columns, true);

        TileMapAlgorithm.debug(tileMap, Tile.COLLIDER);

        Set<Tile> room1 = TileMapAlgorithm.getAllTilesOfRoom(tileMap, new Rectangle(2, 2, roomSize, roomSize));
        Set<Tile> room2 = TileMapAlgorithm.getAllTilesOfRoom(tileMap, new Rectangle(6, 6, roomSize, roomSize));
        Set<Tile> connections = TileMapAlgorithm.connectViaTunneling(tileMap, room1, room2, 1);
        Set<Tile> allTiles = new HashSet<>(connections);
        allTiles.addAll(room1);
        allTiles.addAll(room2);

        TileMapAlgorithm.carveIntoMap(tileMap, allTiles, Tile.COLLIDER, null);
        TileMapAlgorithm.debug(tileMap, Tile.COLLIDER);

        Assert.assertTrue(allTiles.size() > room1.size());
        Assert.assertTrue(allTiles.size() > room2.size());
        Assert.assertTrue(allTiles.size() > connections.size());
        for (Tile tile : connections) {
            Assert.assertTrue(tile.getColumn() >= 0);
            Assert.assertTrue(tile.getColumn() < columns);
            Assert.assertTrue(tile.getRow() >= row - size);
            Assert.assertTrue(tile.getRow() < row + size);
        }
    }

    @Test
    public void createFlatTileMap() {
        int rows = 10, columns = 10, tileHeight = 6;
        String spriteMap = Constants.TILES_SPRITEMAP_FILEPATH;
        TileMapParameters tileMapParameters = TileMapParameters.getDefaultParameters(rows, columns, spriteMap);
        tileMapParameters.put(TileMapParameters.MIN_TERRAIN_HEIGHT_KEY, tileHeight);
        tileMapParameters.put(TileMapParameters.MAX_TERRAIN_HEIGHT_KEY, tileHeight);

        TileMap tileMap = TileMapFactory.create(tileMapParameters);

        for (int row = 0; row < tileMap.getRows(); row++) {
            for (int column = 0; column < tileMap.getColumns(row); column++) {
                Entity tileEntity = tileMap.tryFetchingTileAt(row, column);
                Tile tile = tileEntity.get(Tile.class);
                Assert.assertEquals(tileHeight, tile.getHeight());
            }
        }
    }

    @Test
    public void createVariousHeightTileMap() {
        int rows = 10, columns = 10, tileMinHeight = -10, tileMaxHeight = 10;
        String spriteMap = Constants.TILES_SPRITEMAP_FILEPATH;
        TileMapParameters tileMapParameters = TileMapParameters.getDefaultParameters(rows, columns, spriteMap);
        tileMapParameters.put(TileMapParameters.MIN_TERRAIN_HEIGHT_KEY, tileMinHeight);
        tileMapParameters.put(TileMapParameters.MAX_TERRAIN_HEIGHT_KEY, tileMaxHeight);

        TileMap tileMap = TileMapFactory.create(tileMapParameters);

        for (int row = 0; row < tileMap.getRows(); row++) {
            for (int column = 0; column < tileMap.getColumns(row); column++) {
                Entity tileEntity = tileMap.tryFetchingTileAt(row, column);
                Tile tile = tileEntity.get(Tile.class);
                Assert.assertTrue(tileMinHeight <= tile.getHeight());
                Assert.assertTrue(tileMaxHeight >= tile.getHeight());
            }
        }
    }

    private static TileMap setupMap(int rows, int columns) {
        return setupMap(rows, columns, false);
    }
    private static TileMap setupMap(int rows, int columns, boolean colliderFill) {
        String spriteMap = Constants.TILES_SPRITEMAP_FILEPATH;
        TileMapParameters tileMapParameters = TileMapParameters.getDefaultParameters(rows, columns, spriteMap);
        TileMap tileMap = TileMapFactory.create(tileMapParameters);
        if (colliderFill) {
            tileMap.fill(Tile.COLLIDER, String.valueOf(new Random().nextInt(1, 9)));
        }
        return tileMap;
    }
}
