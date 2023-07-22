package game.map.generators.validation;

import java.util.HashMap;
import java.util.Map;

public class SchemaConfigs {

    public String path;

    private final Map<String, Object> map = new HashMap<String, Object>();
    
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
    public int getStructure() { return getInt("structure"); }
    public int getFloor() { return getInt("floor"); }
    public int getLiquid() { return getInt("liquid"); }
    public int getType() { return getInt("mapType"); }

    private int getInt(String key) {
        return (int) map.get(key);
    }

    private float getFloat(String key) {
        return (float) map.get(key);
    }
}
