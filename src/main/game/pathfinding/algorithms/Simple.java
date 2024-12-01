package main.game.pathfinding.algorithms;

import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.util.Set;

public class Simple {

    //        raycast(gameModel, startEntity, endEntity, distance, result);
//        Entity mousedAt = gameModel.tryFetchingTileMousedAt();
//        digitalDifferentialAnalyzerV2(gameModel, startEntity, endEntity,Constants.CURRENT_SPRITE_SIZE * distance, result);
//        digitalDifferentialAnalyzer(gameModel, startEntity, endEntity, distance, result);

    private static void rayCastV3(GameModel model, Entity start, Entity end, int length, Set<Entity> result) {
        Tile startTile = start.get(Tile.class);
        Tile endTile = end.get(Tile.class);

        int columnIncrement = (endTile.column > startTile.column) ? 1 : -1;
        int rowIncrement = (endTile.row > startTile.row) ? 1 : -1;

        int columnDelta = Math.abs(endTile.column - startTile.column);
        int rowDelta = Math.abs(endTile.row - startTile.row);

        int error = columnDelta - rowDelta;
        int errorCorrectColumn = columnDelta * 2;
        int errorCorrectRow = rowDelta * 2;

        Entity current = null;
        Tile currentTile = startTile;

        int column = currentTile.column;
        int row = currentTile.row;

//        for (int iteration = 0; iteration < length; iteration++) {
        while (currentTile != endTile) {


            current = model.tryFetchingEntityAt(row, column);
            if (current == null) { return; }
            result.add(current);

            currentTile = current.get(Tile.class);
            if (currentTile.isNotNavigable() && currentTile != startTile) { return; }
            if (currentTile == endTile) { return; }

            if (error > 0) {
                column = column + columnIncrement;
                error = error - errorCorrectRow;
            } else if (error < 0) {
                row = row + rowIncrement;
                error = error + errorCorrectColumn;
            } else {
//                row = row + rowIncrement;
//                column = column + columnIncrement;
                if (row != endTile.row) {
                    row = row + rowIncrement;
                }
                if (column != endTile.column) {
                    column = column + columnIncrement;
                }
//                iteration++; // Diagonal tiles traversal cost 2 movement instead of 1
            }
        }
    }

    private static void rayCastV1(GameModel model, Entity start, Entity end, int length, Set<Entity> result) {
        Tile startDetails = start.get(Tile.class);
        Tile endDetails = end.get(Tile.class);
        int columnDelta = Math.abs(endDetails.column - startDetails.column) * 2;
        int rowDelta = Math.abs(endDetails.row - startDetails.row) * 2;
        int column = startDetails.column;
        int row = startDetails.row;
        int column_inc = (endDetails.column > startDetails.column) ? 1 : -1;
        int row_inc = (endDetails.row > startDetails.row) ? 1 : -1;
        int error = columnDelta - rowDelta;
        for (int iteration = 1 + columnDelta + rowDelta; iteration > 0; --iteration) {
            if (length < 0) { return; }
            length--;
            // TODO if iteration is the first, continue
            Entity entity = model.tryFetchingEntityAt(row, column);
            if (entity != null) {
                Tile tile = entity.get(Tile.class);
                result.add(entity);
                if (tile.isNotNavigable() && entity != start) { return; }
            }

            if (error > 0) {
                column += column_inc;
                error -= rowDelta;
            } else {
                row += row_inc;
                error += columnDelta;
            }
        }
    }

    public static void rayCastV2(GameModel model, Entity start, Entity end, int length, Set<Entity> result) {
        Tile startDetails = start.get(Tile.class);
        Tile endDetails = end.get(Tile.class);
        int columnDelta = Math.abs(endDetails.column - startDetails.column) * 2;
        int rowDelta = Math.abs(endDetails.row - startDetails.row) * 2;
        int column = startDetails.column;
        int row = startDetails.row;
        int column_inc = (endDetails.column > startDetails.column) ? 1 : -1;
        int row_inc = (endDetails.row > startDetails.row) ? 1 : -1;
        int error = columnDelta - rowDelta;
        result.clear();
        for (int iteration = 1 + columnDelta + rowDelta; iteration > 0; --iteration) {
            if (length < 0) { return; }
            length--;
            Entity entity = model.tryFetchingEntityAt(row, column);
            if (entity != null) {
                Tile tile = entity.get(Tile.class);
                result.add(entity);
                if (tile.isNotNavigable() && entity != start) { return; }
            }

            if (error > 0) {
                column += column_inc;
                error -= rowDelta;
            } else if (error < 0) {
                row += row_inc;
                error += columnDelta;
            } else {
                row += row_inc;
                column += column_inc;
            }
        }
    }
}
