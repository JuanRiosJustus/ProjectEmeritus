package game.map.generators.validation;

import java.util.HashMap;
import java.util.Map;

public class SchemaConfigs {

    public int rows = -1;
    public int columns = -1;
    public int flooring = -1;
    public int walling = -1;
    public int structure = -1;
    public int type = -1;
    public float zoom = -1;
    public int liquid = -1;
    public String path;

    public final Map<String, Object> map = new HashMap<String, Object>();
    
    public SchemaConfigs setPath(String value) {
        map.put("path", value);
        return this;
    }
    public SchemaConfigs setZoom(float value) {
        map.put("zoom", value);
        return this;
    }
    public SchemaConfigs setFlooring(int value) {
        map.put("floor", value);
        return this;
    }
    public SchemaConfigs setWalling(int value) {
        map.put("wall", value);
        return this;
    }
    public SchemaConfigs setStructure(int value) {
        map.put("structure", value);
        return this;
    }
    public SchemaConfigs setLiquid(int value) {
        map.put("liquid", value);
        return this;
    }
    public SchemaConfigs setRows(int rowCount) {
        map.put("rows", rowCount);
        return this;
    }
    public SchemaConfigs setColumns(int columnCount) {
        map.put("columns", columnCount);
        return this;
    }

    public SchemaConfigs setType(int mapType) {
        map.put("mapType", mapType);
        return this;
    }

    public SchemaConfigs setSize(int rowCount, int columnCount) {
        setColumns(columnCount);
        setRows(rowCount);
        return this;
    }

    private SchemaConfigs() { }

    public static SchemaConfigs newConfigs() {
        return new SchemaConfigs();
    }

    public int getWall() { return getInt("wall"); }
    public int getPath() { return getInt("path"); }
    public int getRows() { return getInt("rows"); }
    public int getColumns() { return getInt("columns"); }
    public float getZoom() { return getFloat("zoom"); }

    private int getInt(String key) {
        return (int) map.get(key);
    }

    private float getFloat(String key) {
        return (float) map.get(key);
    }

    private String getString(String key) {
        return (String) map.get(key);
    }
    public int getStructure() { return getInt("structure"); }
    public int getFloor() { return getInt("floor"); }
}
