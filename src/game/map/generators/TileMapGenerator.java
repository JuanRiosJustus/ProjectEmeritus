package game.map.generators;

import game.map.SchemaMap;

import java.util.SplittableRandom;

public class TileMapBuilder {

    protected final SplittableRandom random = new SplittableRandom();
    protected boolean isCompletelyConnected = false;
    protected SchemaMap terrainMap = null;
    protected SchemaMap structureMap = null;
    protected SchemaMap floodMap = null;

    public TileMapBuilder(int rows, int columns) {
        terrainMap = new SchemaMap(rows, columns);
        structureMap = new SchemaMap(rows, columns);
        floodMap = new SchemaMap(rows, columns);
        isCompletelyConnected = false;
    }
}
