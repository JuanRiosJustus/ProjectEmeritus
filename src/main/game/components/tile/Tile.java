package main.game.components.tile;

import com.github.cliftonlabs.json_simple.JsonObject;
import main.constants.Vector3f;
import main.game.components.*;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.map.base.TileMap;

import java.util.*;

public class Tile extends Component {

    public int row;
    public int column;

    public Entity mUnit;
    private Gem gem;
    public final static String ROW = "row";
    public final static String COLUMN = "column";
    public final static String COLLIDER = "collider";
    public final static String HEIGHT = "height";
    public final static String TERRAIN = "terrain";
    public final static String LIQUID = "liquid";
    public final static String OBSTRUCTION = "obstruction";
    public final static String OBSTRUCTION_DESTROYABLE_BLOCKER = "destroyable_blocker_structure";
    public final static String OBSTRUCTION_ROUGH_TERRAIN = "rough_terrain_structure";
    public final static String SPAWN_REGION = "spawn_region";

    public Tile(int tileRow, int tileColumn) {
        this(tileRow, tileColumn, null, null, null, null);
    }

    public Tile(int tileRow, int tileColumn, Object collider, Object height, Object terrain, Object liquid) {
        this(new JsonObject()
                .putChain(ROW, tileRow)
                .putChain(COLUMN, tileColumn)
                .putChain(COLLIDER, collider)
                .putChain(HEIGHT, height)
                .putChain(TERRAIN, terrain)
                .putChain(LIQUID, liquid)
                .putChain(OBSTRUCTION, null)
                .putChain(SPAWN_REGION, null));
    }
    public Tile(JsonObject jsonData) {
        putAll(jsonData);
    }

    /**
     *
     ___ ___ ___ ___ _  _ _____ ___   _   _
     | __/ __/ __| __| \| |_   _|_ _| /_\ | |
     | _|\__ \__ \ _|| .` | | |  | | / _ \| |__
     |___|___/___/___|_|\_| |_| |___/_/ \_\____|
     */
    public int getRow() { return (int) getOrDefault(ROW, -1); }
    public int getColumn() { return (int) getOrDefault(COLUMN, -1); }
    public String getCollider() { return (String) get(COLLIDER); }
    public int getHeight() { return (int)getOrDefault(Tile.HEIGHT, 0); }
    public String getTerrain() { return (String) get(TERRAIN); }
    public String getLiquid() { return (String) get(LIQUID); }
    public String getObstruction() { return (String) get(OBSTRUCTION); }

    public void clear(String key) { put(key, null); }
    public void set(String key, Object value) { put(key, value); }

    public boolean isPath() { return getCollider() == null; }
    public boolean isWall() { return getCollider() != null; }
    public boolean isOccupied() { return mUnit != null; }
    public void setSpawnRegion(int value) { put(SPAWN_REGION, value); }
    public String getSpawnRegion() { return (String) get(SPAWN_REGION); }
    public Entity getUnit() { return mUnit; }

    public void removeUnit() {
        if (mUnit != null) {
            MovementComponent movementComponent = mUnit.get(MovementComponent.class);
            movementComponent.mCurrentTile = null;
        }
        mUnit = null;
    }

    public void setUnit(Entity incomingUnitEntity) {
        // Ensure the current associated unit is removed
        if (mUnit != null) {
            // Remove the tile reference of the outgoing unit
            Entity outgoingUnitEntity = mUnit;
            MovementComponent movementComponent = outgoingUnitEntity.get(MovementComponent.class);
            movementComponent.setCurrentTile(null);
        }

        mUnit = incomingUnitEntity;
        if (mUnit == null) { return; }

        // Remove the tile reference of the incoming unit
        MovementComponent movementComponent = mUnit.get(MovementComponent.class);
        Entity currentTileEntity = movementComponent.getCurrentTile();
        if (currentTileEntity != null) {
            Tile outgoingTileEntity = currentTileEntity.get(Tile.class);
            outgoingTileEntity.setUnit(null);
        }
        movementComponent.setCurrentTile(mOwner);
    }

    public void removeStructure() {
        put(OBSTRUCTION, "");
    }

    public boolean isRoughTerrain() {
        return false;
    }

    public boolean isDestroyableBlocker() {
        return hasObstruction();
    }

    public boolean hasObstruction() {
        return get(OBSTRUCTION) != null;
    }

    public boolean isNotNavigable() {
        return isWall() || isOccupied() || hasObstruction();
    }
    public Gem getGem() { return gem; }

    public String toString() {
        return "[row: " + getRow() + ", column: " + getColumn() +"]";
    }

    public void setGem(Gem b) {
        gem = b;
    }

    public Vector3f getLocalVector(GameModel model) {
        int spriteWidth = model.getSettings().getSpriteWidth();
        int spriteHeight = model.getSettings().getSpriteHeight();
        int localTileX = getColumn() * spriteWidth;
        int localTileY = getRow() * spriteHeight;
        return new Vector3f(localTileX, localTileY);
    }

    public Vector3f getWorldVector(GameModel model) {
        int spriteWidth = model.getSettings().getSpriteWidth();
        int spriteHeight = model.getSettings().getSpriteHeight();
        int localTileX = getColumn() * spriteWidth;
        int localTileY = getRow() * spriteHeight;
        int globalTileX = model.getCamera().globalX(localTileX);
        int globalTileY = model.getCamera().globalY(localTileY);
        return new Vector3f(globalTileX, globalTileY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tile tile)) return false;
        return row == tile.row && column == tile.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
