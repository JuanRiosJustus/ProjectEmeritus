package game.map.generators.validation;

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
    
    public SchemaConfigs setPath(String value) {
        path = value;
        return this;
    }
    public SchemaConfigs setZoom(float value) {
        zoom = value;
        return this;
    }
    public SchemaConfigs setFlooring(int value) {
        flooring = value;
        return this;
    }
    public SchemaConfigs setWalling(int value) {
        walling = value;
        return this;
    }
    public SchemaConfigs setStructure(int value) {
        structure = value;
        return this;
    }
    public SchemaConfigs setLiquid(int value) {
        liquid = value;
        return this;
    }
    public SchemaConfigs setRows(int rowCount) {
        rows = rowCount;
        return this;
    }
    public SchemaConfigs setColumns(int rowColumns) {
        columns = rowColumns;
        return this;
    }

    public SchemaConfigs setType(int mapType) {
        type = mapType;
        return this;
    }

    public SchemaConfigs setSize(int rowCount, int columnCount) {
        rows = rowCount;
        columns = columnCount;
        return this;
    }

    private SchemaConfigs() { }
    public static SchemaConfigs newConfigs() {
        return new SchemaConfigs();
    }
}
