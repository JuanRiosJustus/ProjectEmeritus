package game.map.generators.validation;

public class SchemaConfigs {
    public int rows;
    public int columns;
    public int flooring;
    public int walling;
    public int structure ;
    public int type;
    public float zoom;
    public int liquid;
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

//    public int getType() {
//        return type;
//    }
//
//    public int getStructure() {
//        return structure;
//    }
//    public int getLiquid() { return liquid; }
//
//    public int getWalling() {
//        return walling;
//    }
//
//    public int getFlooring() {
//        return flooring;
//    }
//
//    public int getColumns() {
//        return columns;
//    }
//
//    public int getRows() {
//        return rows;
//    }
}
