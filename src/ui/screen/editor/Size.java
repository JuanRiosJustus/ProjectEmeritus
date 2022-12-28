package ui.screen.editor;

public enum Size {

    Tiny(64, 14, 8),
    Small(40, 20, 10),
    Medium(32, 30, 15),
    Large(24, 40, 25),
    Gigantic(18, 50, 35);

    public final int width;
    public final int height;
    public final int tileSize;

    Size(int mapTileSize, int mapWidth, int mapHeight) {
        width = mapWidth;
        height = mapHeight;
        tileSize = mapTileSize;
    }

    public String dimension() { return width + "(W)/" + height + "(H)"; }
    public String toString() { return name(); }
}
