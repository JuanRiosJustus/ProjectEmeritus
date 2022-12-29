package game.map.generators.validation;

import game.map.generators.TileMapGenerator;

public class SchemaConfigs {
    private int rows = -1;
    private int columns = -1;
    private int flooring = -1;
    private int walling = -1;
    private int structure = -1;
    private int type = -1;
    private int special = -1;
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
    public SchemaConfigs setSpecial(int value) {
        special = value;
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

    public int getType() {
        return type;
    }

    public int getStructure() {
        return structure;
    }
    public int getSpecial() { return special; }

    public int getWalling() {
        return walling;
    }

    public int getFlooring() {
        return flooring;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }
}
