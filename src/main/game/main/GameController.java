package main.game.main;

import javafx.animation.AnimationTimer;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import main.engine.EngineController;
import main.engine.EngineRunnable;
import main.graphics.AssetPool;
import main.input.InputController;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.*;

public class GameController extends EngineRunnable {
    private final GameAPI mGameAPI;
    private final GameMapEditorAPI mGameMapEditorAPI;
    private final GameModel mGameModel;
    private final GameView mGameView;
    private final InputController mInputController = InputController.getInstance();

    public static GameController create() {
        return new GameController();
    }

    public static GameController createFlatTestMap(int rows, int columns, int width, int height) {
        GameController gameController = GameController.create();
        gameController.generateTileMap(new JSONObject()
                .fluentPut("rows", rows)
                .fluentPut("columns", columns)
                .fluentPut("viewport_width", width)
                .fluentPut("viewport_height", height)
                .fluentPut("viewport_x", 0)
                .fluentPut("viewport_y", 0)
                .fluentPut("foundation_depth", 3)
                .fluentPut("lower_terrain_elevation", 6)
                .fluentPut("liquid_elevation", 7)
                .fluentPut("upper_terrain_elevation", 9)
                .fluentPut("terrain_elevation_noise", 0)
        );
        return gameController;
    }

    public static GameController createFlatTestMapWithLiquid(int rows, int columns, int width, int height) {
        GameController gameController = GameController.create();
        gameController.generateTileMap(new JSONObject()
                .fluentPut("rows", rows)
                .fluentPut("columns", columns)
                .fluentPut("viewport_width", width)
                .fluentPut("viewport_height", height)
                .fluentPut("viewport_x", 0)
                .fluentPut("viewport_y", 0)
                .fluentPut("foundation_depth", 3)
                .fluentPut("lower_terrain_elevation", 6)
                .fluentPut("liquid_elevation", 8)
                .fluentPut("upper_terrain_elevation", 9)
                .fluentPut("terrain_elevation_noise", 0)
        );
        return gameController;
    }

    public static GameController createVariousHeightTestMap(int rows, int columns, int width, int height) {
        GameController gameController = GameController.create();
        gameController.generateTileMap(new JSONObject()
                .fluentPut("rows", rows)
                .fluentPut("columns", columns)
                .fluentPut("viewport_width", width)
                .fluentPut("viewport_height", height)
                .fluentPut("viewport_x", 0)
                .fluentPut("viewport_y", 0)
                .fluentPut("foundation_depth", 3)
                .fluentPut("lower_terrain_elevation", 6)
                .fluentPut("liquid_elevation", 7)
                .fluentPut("upper_terrain_elevation", 9)
                .fluentPut("terrain_elevation_noise", 0)
        );
        return gameController;
    }

    public static GameController createVariousHeightTestMapWithLiquid(int rows, int columns, int width, int height) {
        GameController gameController = GameController.create();
        gameController.generateTileMap(new JSONObject()
                .fluentPut("rows", rows)
                .fluentPut("columns", columns)
                .fluentPut("viewport_width", width)
                .fluentPut("viewport_height", height)
                .fluentPut("viewport_x", 0)
                .fluentPut("viewport_y", 0)
                .fluentPut("foundation_depth", 3)
                .fluentPut("lower_terrain_elevation", 3)
                .fluentPut("liquid_elevation", 4)
                .fluentPut("upper_terrain_elevation", 6)
                .fluentPut("terrain_elevation_noise", .777)
        );
        return gameController;
    }
    public static GameController createBasicTestMap(int rows, int columns, int width, int height) {
        GameController gameController = GameController.create();
        gameController.generateTileMap(new JSONObject()
                .fluentPut("rows", rows)
                .fluentPut("columns", columns)
                .fluentPut("viewport_width", width)
                .fluentPut("viewport_height", height)
                .fluentPut("viewport_x", 0)
                .fluentPut("viewport_y", 0)
                .fluentPut("foundation_depth", 3)
                .fluentPut("lower_terrain_elevation", 6)
                .fluentPut("liquid_elevation", 7)
                .fluentPut("upper_terrain_elevation", 9)
                .fluentPut("terrain_elevation_noise", .6666)
        );
        return gameController;
    }

    public static GameController create(int rows, int columns, int width, int height) {

        List<String> floors = AssetPool.getInstance().getFloorTileSets();
        List<String> structures = AssetPool.getInstance().getStructureTileSets();

        GameConfigs configs = GameConfigs.getDefaults()
                .setMapGenerationRows(rows)
                .setMapGenerationColumns(columns)
                .setOnStartupSpriteWidth(96)
                .setOnStartupSpriteHeight(96)
                .setViewportWidth(width)
                .setViewportHeight(height)
                .setMapGenerationFoundationDepth(3)
                .setMapGenerationTerrainHeightNoise(0f)
                .setMapGenerationTerrainStartingElevation(6)
                .setMapGenerationTerrainEndingElevation(9)
//                .setOnStartupCenterCameraOnMap(true)
                .setMapGenerationTerrainAsset(new ArrayList<>(floors).get(new Random().nextInt(floors.size())));
//                .setMapGenerationStructureAssets(structures.stream().toList().stream().findFirst().stream().toList());

        GameController newGameController = new GameController(configs);


        Random random = new Random();

//        for (int row = 0; row < newGameController.getRows(); row++) {
//            for (int column = 0; column < newGameController.getColumns(); column++) {
//                JSONObject request = new JSONObject();
//                request.put("row", row);
//                request.put("column", column);
//                request.put("function", "add");
//                request.put("depth", random.nextInt(2, 5));
//                newGameController.updateLayering(request);
//            }
//        }


//        for (int i = 0; i < 10; i++) {
//            int row = random.nextInt(newGameController.getRows());
//            int column = random.nextInt(newGameController.getColumns());
//            String structure = structures.get(random.nextInt(structures.size()));
//
//            JSONObject request = new JSONObject();
//            request.put("row", row);
//            request.put("column", column);
//            request.put("structure", structure);
//            newGameController.setStructure(request);
//        }
        return newGameController;
    }

    public static GameController create(GameConfigs gc) {

        GameConfigs configs = GameConfigs.getDefaults();
        configs.putAll(gc);

        GameController newGameController = new GameController(configs);
        return newGameController;
    }


    private GameController() {
        mGameModel = new GameModel();
        mGameView = new GameView(mGameModel);
        mGameAPI = new GameAPI(mGameModel);
        mGameMapEditorAPI = new GameMapEditorAPI(mGameModel);
    }

    private GameController(JSONObject configs) {
        mGameModel = new GameModel(configs, null);
        mGameView = new GameView(mGameModel);
        mGameAPI = new GameAPI(mGameModel);
        mGameMapEditorAPI = new GameMapEditorAPI(mGameModel);
    }

    public void start() {

//        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
//        for (GarbageCollectorMXBean gcBean : gcBeans) {
//            System.out.println("Name: " + gcBean.getName());
//            System.out.println("Collection count: " + gcBean.getCollectionCount());
//            System.out.println("Collection time: " + gcBean.getCollectionTime());
//            System.out.println("---------------------------");
//        }
//

        mGameModel.run();;
        mUpdateAnimationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!mGameModel.isRunning()) { return; }
                processInput();
                updateGame();
            }
        };
//        run();
        mUpdateAnimationTimer.start();
    }

    @Override
    public void stop() {
        super.stop();
        mGameModel.stop();
    }

    private void updateGame() {
        double deltaTime = EngineController.getInstance().getDeltaTime();
        mGameModel.setDeltaTime(deltaTime);
        mGameModel.update();
        mGameView.update();
    }

    private void processInput() {
        mInputController.update();
        mGameModel.input(mInputController);
    }

    public GameModel getGameModel() {
        return mGameModel;
    }

    public StackPane getGamePanel() {
        int width = mGameModel.getGameState().getMainCameraWidth();
        int height = mGameModel.getGameState().getMainCameraHeight();
        StackPane newGamePanel = mGameView.getViewPort(width, height);

        newGamePanel.setCache(true);
        newGamePanel.setCacheHint(CacheHint.SPEED);
        return newGamePanel;
    }

    public Scene render() {
        int sceneWidth = mGameModel.getGameState().getMainCameraWidth();
        int sceneHeight = mGameModel.getGameState().getMainCameraHeight();
        return render(sceneWidth, sceneHeight);
    }

    public Scene render(int width, int height) {
        StackPane gamePanel = getGamePanel();
        int sceneWidth = width;
        int sceneHeight = height;

        gamePanel.setCache(true);
        gamePanel.setCacheHint(CacheHint.SPEED);

        Scene scene = new Scene(gamePanel, sceneWidth, sceneHeight, Color.BLUE);

        return scene;
    }


    public int getRows() { return mGameModel.getRows(); }
    public int getColumns() { return mGameModel.getColumns(); }
    public void run() { mGameModel.run(); }



    public boolean isRunning() { return mGameModel.isRunning(); }


    /**
     * Attempts to place a unit on a tile using the provided {@link JSONObject} data.
     *
     * <p>The input JSON object must include the following fields:</p>
     * <ul>
     *   <li><b>"unit_id"</b> (String): The unique identifier of the unit to be placed.</li>
     *   <li><b>"tile_id"</b> (String): The unique identifier of the destination tile.</li>
     *   <li><b>"team_id"</b> (String, optional): The team to which the unit belongs. Defaults to {@code "neutral"} if absent.</li>
     * </ul>
     *
     * <p>Example input:</p>
     * <pre>{@code
     * {
     *   "unit_id": "fire_dragon_342423-522452-52662-252432",
     *   "tile_id": "4_2_43242-2555223-5234324-25324",
     *   "team_id": "Enemy"
     * }
     * }</pre>
     *
     * @param object the JSON object containing placement data; must not be {@code null}
     * @return a {@link JSONObject} with the unique identifier of the unit on the tile,
     * or {@code null} if no unit is present or the tile does not exist
     */
    public JSONObject setUnit(JSONObject object) {
        return mGameModel.setUnit(object);
    }

    /**
     * Retrieves the unique identifier of the unit currently occupying the tile at the specified row and column.
     *
     * <p>The input JSON object must include the following fields:</p>
     * <ul>
     *   <li><b>"row"</b> (int): The row index of the target tile.</li>
     *   <li><b>"column"</b> (int): The column index of the target tile.</li>
     * </ul>
     *
     * <p>If the specified tile is invalid or unoccupied, this method returns {@code null}.</p>
     *
     * <p>Example input:</p>
     * <pre>{@code
     * {
     *   "row": 5,
     *   "column": 2
     * }
     * }</pre>
     *
     * @param input the JSON object specifying tile coordinates; must not be {@code null}
     * @return the unique identifier of the unit on the tile, or {@code null} if no unit is present or the tile does not exist
     */
    public String getUnitOfTile(JSONObject input) {
        return mGameModel.getUnitOfTile(input);
    }

    /**
     * Retrieves detailed metadata about a tile specified by a row/column or tile ID.
     *
     * <p>This method provides a high-level external representation of a tile's spatial
     * and elevation data. The input {@link JSONObject} must contain either:
     * <ul>
     *   <li><b>"tile_id"</b> (String): the ID of the target tile</li>
     *   <li>or <b>"row"</b> (int) and <b>"column"</b> (int): the coordinates of the tile</li>
     * </ul>
     *
     * <p>The returned {@link JSONObject} contains:
     * <ul>
     *   <li><b>"tile_id"</b> (String): the resolved tile ID</li>
     *   <li><b>"row"</b> (int): the row position of the tile</li>
     *   <li><b>"column"</b> (int): the column position of the tile</li>
     *   <li><b>"base_elevation"</b> (int): the base terrain height of the tile</li>
     *   <li><b>"modified_elevation"</b> (int): the final elevation including all stacked layers</li>
     * </ul>
     *
     * <p>Example usage:
     * <pre>{@code
     * {
     *   "row": 2,
     *   "column": 5
     * }
     * }</pre>
     *
     * or
     *
     * <pre>{@code
     * {
     *   "tile_id": "tile_2_5_abc123"
     * }
     * }</pre>
     *
     * @param input a {@link JSONObject} containing tile coordinates or ID; must not be {@code null}
     * @return a {@link JSONObject} with detailed tile information, or {@code null} if the tile doesn't exist
     */
    public JSONObject getTile(JSONObject input) {
        return mGameModel.getTileWithMetadata(input);
    }



    /**
     * Retrieves the unique identifier of the tile currently occupied by a unit.
     *
     * <p>The input JSON object must include the following field:</p>
     * <ul>
     *   <li><b>"unit_id"</b> (String): The unique identifier of the unit whose tile location is being queried.</li>
     * </ul>
     *
     * <p>If the unit ID is invalid or the unit is not currently placed on a tile, this method returns {@code null}.</p>
     *
     * <p>Example input:</p>
     * <pre>{@code
     * {
     *   "unit_id": "warrior_abc123-xyz987"
     * }
     * }</pre>
     *
     * @param input the JSON object containing the unit ID; must not be {@code null}
     * @return the unique identifier of the tile the unit is on, or {@code null} if the unit is not found or not placed
     */
    public String getTileOfUnit(JSONObject input) {
        return mGameModel.getTileOfUnit(input);
    }

    /**
     * Retrieves a range-based movement graph from the specified tile within the given range.
     *
     * <p>The input JSON object must include the following fields:</p>
     * <ul>
     *   <li><b>"tile_id"</b> (String): The unique identifier of the origin tile.</li>
     *   <li><b>"range"</b> (int): The number of tiles to search outward from the origin tile.</li>
     *   <li><b>"respectfully"</b> (boolean): Whether to respect movement constraints such as tile elevation, terrain, or unit occupancy.</li>
     * </ul>
     *
     * <p>The returned JSON object contains a movement graph where keys are tile IDs and values are maps of neighboring tile IDs and metadata (e.g., cost or direction).</p>
     *
     * <p>Example input:</p>
     * <pre>{@code
     * {
     *   "tile_id": "4_2_3412-2352-6223-2342",
     *   "range": 4,
     *   "respectfully": true
     * }
     * }</pre>
     *
     * @param request the JSON object specifying the origin tile, range, and movement rules; must not be {@code null}
     * @return a {@link JSONArray} representing the tiles available from the origin tile,
     * or an empty object if input is invalid
     */
    public JSONArray getTilesInMovementRange(JSONObject request) {
        return mGameModel.getTilesInMovementRange(request);
    }


    /**
     * Computes the shortest traversable path from a starting tile to a specified destination tile,
     * given a movement range and tile traversal constraints.
     *
     * <p>This method uses a directed graph (generated via {@code TileMap#createDirectedGraph})
     * to trace a backwards path from the destination tile to the origin, following recorded parent links.
     * The graph respects range and optionally tile navigability or obstructions based on the {@code respectfully} flag.</p>
     *
     * <p>The input {@link JSONObject} must include:</p>
     * <ul>
     *   <li><b>"tile_id"</b> (String): The origin tile ID.</li>
     *   <li><b>"end_tile_id"</b> (String): The target destination tile ID.</li>
     *   <li><b>"range"</b> (int): The number of tiles the unit can move.</li>
     * </ul>
     *
     * <p>Optional:</p>
     * <ul>
     *   <li><b>"respectfully"</b> (boolean): Whether to respect terrain and movement rules (default: {@code true}).</li>
     * </ul>
     *
     * <p>If no path exists, or either tile is invalid or unreachable within the constraints, the method returns an empty array.</p>
     *
     * <p>Example input:</p>
     * <pre>{@code
     * {
     *   "tile_id": "start_3_4",
     *   "end_tile_id": "end_5_7",
     *   "range": 5,
     *   "respectfully": true
     * }
     * }</pre>
     *
     * @param request a {@link JSONObject} specifying start tile, end tile, movement range, and constraints
     * @return a {@link JSONArray} of tile IDs from origin to destination (inclusive), or an empty array if no valid path exists
     */
    public JSONArray getTilesInMovementPath(JSONObject request) {
        return mGameModel.getTilesInMovementPath(request);
    }
    /**
     * Computes the line of sight between two tiles and returns all tiles along that path.
     * <p>
     * This method delegates to {@code computeLineOfSightJSON} and returns the tile IDs that form the
     * visible path from a starting tile to an ending tile. The result may be truncated early if an
     * obstructing tile is encountered and the {@code respectfully} flag is enabled.
     *
     * @param request a {@link JSONObject} containing the following required fields:
     *                <ul>
     *                  <li><b>"start_tile_id"</b>: the ID of the tile where the line of sight begins</li>
     *                  <li><b>"end_tile_id"</b>: the ID of the target tile</li>
     *                  <li><b>"respectfully"</b> (optional): if true, the line of sight stops when a non-navigable tile is encountered; defaults to true</li>
     *                </ul>
     *
     * @return a {@link JSONArray} of tile ID strings representing the ordered path of tiles
     *         from the start to the end tile (inclusive), possibly truncated by obstruction.
     */
    public JSONArray getTilesInLineOfSight(JSONObject request) { return mGameModel.getTilesInLineOfSight(request); }
    /**
     * Computes the full area of tiles visible from a given starting tile within a specified range.
     * <p>
     * This method delegates to {@code computeAreaOfSightJSON}, returning all tiles that are within a
     * diamond-shaped Manhattan range and are reachable via line-of-sight from the start tile.
     * <p>
     * The result is the union of all line-of-sight paths from the origin to each tile in range,
     * optionally stopping early for obstructed paths if {@code respectfully} is true.
     *
     * @param request a {@link JSONObject} containing the following fields:
     *                <ul>
     *                    <li><b>"start_tile_id"</b> (String): the ID of the tile at the origin of sight</li>
     *                    <li><b>"range"</b> (int): the maximum Manhattan distance for line-of-sight checks</li>
     *                    <li><b>"respectfully"</b> (boolean, optional): whether to stop lines early when encountering non-navigable tiles; defaults to true</li>
     *                </ul>
     *
     * @return a {@link JSONArray} of tile ID strings representing all tiles visible from the start tile
     *         within the given range, following line-of-sight constraints.
     */
    public JSONArray getTileInAreaOfSight(JSONObject request) { return mGameModel.getTilesInAreaOfSight(request); }

    public JSONObject setStructure(JSONObject request) {
        return mGameModel.setStructure(request);
    }

    public JSONObject updateLayering(JSONObject request) {
        return mGameModel.updateLayering(request);
    }



    public JSONObject createUnit() { return createUnit(new JSONObject()); }
    public JSONObject createUnit(JSONObject request) { return mGameModel.createUnit(request); }
    public JSONObject createStructure(JSONObject request) { return mGameModel.createStructure(request); }
    public JSONObject createTile(JSONObject request) { return mGameModel.createTile(request); }



    /**
     * Adjusts the game's camera zoom level by updating the sprite dimensions accordingly.
     * <p>
     * This method sets the zoom factor for the current game state, which affects how tiles and assets
     * are scaled during rendering. It retrieves the original sprite dimensions and scales them by the
     * new zoom factor, then updates the game state with the resulting dimensions.
     *
     * <p>
     * Expected input format:
     * <pre>{@code
     * {
     *   "zoom": 1.5
     * }
     * }</pre>
     *
     * @param request a {@link JSONObject} containing:
     *                <ul>
     *                  <li><b>"zoom"</b> (float): the new zoom factor (e.g., 1.0 for 100%, 2.0 for 200%)</li>
     *                </ul>
     *
     * @throws IllegalArgumentException if the zoom factor is non-positive
     */
    public void setCameraZoom(JSONObject request) {
        mGameModel.setViewportZoom(request);
    }

    /**
     * Initializes and generates a new tile map based on the given request parameters.
     * <p>
     * This method sets up a grid of tiles using elevation noise, floor/terrain/liquid assets,
     * and foundational configuration. It also prepares the internal game state, viewport,
     * event systems, and input handlers required for gameplay.
     *
     * <p><b>Expected request fields:</b>
     * <ul>
     *   <li><b>"rows"</b> (int): Number of tile rows in the map</li>
     *   <li><b>"columns"</b> (int): Number of tile columns in the map</li>
     *   <li><b>"viewport_width"</b> (int): Width of the game viewport in pixels</li>
     *   <li><b>"viewport_height"</b> (int): Height of the game viewport in pixels</li>
     *   <li><b>"viewport_x"</b> (int): Initial horizontal offset of the viewport</li>
     *   <li><b>"viewport_y"</b> (int): Initial vertical offset of the viewport</li>
     *   <li><b>"foundation_asset"</b> (String, optional): Asset ID for the tile base layer</li>
     *   <li><b>"foundation_depth"</b> (int, optional): Number of foundation layers to apply (default: 3)</li>
     *   <li><b>"liquid_asset"</b> (String, optional): Asset ID for liquid layers</li>
     *   <li><b>"liquid_elevation"</b> (int, optional): Threshold elevation below which liquid is applied (default: 4)</li>
     *   <li><b>"terrain_asset"</b> (String, optional): Asset ID for upper terrain layers</li>
     *   <li><b>"lower_terrain_elevation"</b> (int): Minimum elevation for terrain noise</li>
     *   <li><b>"upper_terrain_elevation"</b> (int): Maximum elevation for terrain noise</li>
     *   <li><b>"terrain_elevation_noise"</b> (float): Amplitude/frequency factor for terrain elevation noise</li>
     *   <li><b>"terrain_elevation_noise_seed"</b> (long, optional): Random seed for deterministic terrain generation</li>
     * </ul>
     *
     * <p>
     * After tile map generation, this method:
     * <ul>
     *   <li>Centers the camera view based on map and viewport dimensions</li>
     *   <li>Initializes gameplay systems: event bus, logger, input, update loop</li>
     *   <li>Designates spawn regions using the generated tile data</li>
     * </ul>
     *
     * @param request a {@link JSONObject} containing the map dimensions, asset settings, and viewport config
     * @return {@code null} (the map is generated internally and accessible through state)
     */
    public void generateTileMap(JSONObject request) {
        mGameModel.generateTileMap(request);
    }

//    public JSONArray getSpawnRegions() { return mGameModel.getSpawnRegions(); }
    public JSONObject getSpawnRegionsData() { return mGameModel.getSpawnRegionsData(); }

    public JSONObject getHoveredTile() { return mGameMapEditorAPI.getHoveredTile(); }

    public void setTileToGlideToAPI(JSONObject request) { mGameAPI.setTileToGlideTo(mGameModel, request); }

    //    public void setTileToGlideToID(String request) { setTileToGlideToID(new JSONObject().put(request)); }


    public void setSelectedTileIDsAPI(JSONArray request) { mGameAPI.setSelectedTileIDs(mGameModel, request); }








    public void updateSpawners(JSONObject request) { mGameAPI.updateSpawners(mGameModel, request); }
    public void updateTileLayers(JSONObject request) { mGameAPI.updateTileLayers(mGameModel, request); }
    public void updateStructures(JSONObject request) { mGameAPI.updateStructures(mGameModel, request); }
//    public JSONArray getTilesAtRowColumn(JSONObject request) { return mGameAPI.getTilesAtRowColumn(mGameModel, request); }
//    public JSONArray getTilesAtXY(JSONObject request) { return mGameAPI.getTilesAtXY(mGameModel, request); }



    public void getSelectedTilesChecksumAPI(JSONObject ephemeral) { mGameAPI.getSelectedTilesChecksum(mGameModel, ephemeral); }

    public JSONObject getStatisticsForUnit(JSONObject request) {
        return mGameAPI.getStatisticsForUnit(mGameModel, request);
    }

//    public String getUnitAtSelectedTiles() { return mGameAPI.getUnitAtSelectedTiles(mGameModel); }

    public JSONArray getEntityIDsAtSelectedTiles() { return mGameAPI.getEntityIDsAtSelectedTiles(mGameModel); }

//    public JSONArray getActionsOfUnitOfCurrentTurn() { return mGameAPI.getActionsOfUnitOfCurrentTurn(mGameModel); }
//    public JSONObject getMovementStatsOfUnitOfCurrentTurn() { return
//            mGameAPI.getMovementStatsOfUnitOfCurrentTurn(mGameModel);
//    }
//    public JSONObject getUnitsOnSelectedTiles() { return mGameAPI.getUnitsOnSelectedTiles(mGameModel); }
    public boolean consumeShouldAutomaticallyGoToHomeControls() {
        return mGameAPI.consumeShouldAutomaticallyGoToHomeControls(mGameModel);
    }



    /**
     *
     * For Operations involving the turn order queue
     *
     * ████████╗██╗   ██╗██████╗ ███╗   ██╗     ██████╗ ██╗   ██╗███████╗██╗   ██╗███████╗     █████╗ ██████╗ ██╗
     * ╚══██╔══╝██║   ██║██╔══██╗████╗  ██║    ██╔═══██╗██║   ██║██╔════╝██║   ██║██╔════╝    ██╔══██╗██╔══██╗██║
     *    ██║   ██║   ██║██████╔╝██╔██╗ ██║    ██║   ██║██║   ██║█████╗  ██║   ██║█████╗      ███████║██████╔╝██║
     *    ██║   ██║   ██║██╔══██╗██║╚██╗██║    ██║▄▄ ██║██║   ██║██╔══╝  ██║   ██║██╔══╝      ██╔══██║██╔═══╝ ██║
     *    ██║   ╚██████╔╝██║  ██║██║ ╚████║    ╚██████╔╝╚██████╔╝███████╗╚██████╔╝███████╗    ██║  ██║██║     ██║
     *    ╚═╝    ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═══╝     ╚══▀▀═╝  ╚═════╝ ╚══════╝ ╚═════╝ ╚══════╝    ╚═╝  ╚═╝╚═╝     ╚═╝
     */
    public void turnOrderAPI() { }
    public JSONArray getAllEntitiesInTurnQueueFinishedTurn() {
        return mGameAPI.getAllEntitiesInTurnQueueFinishedTurn(mGameModel);
    }
    public JSONArray getAllEntitiesInTurnQueuePendingTurn() {
        return mGameAPI.getAllEntitiesInTurnQueuePendingTurn(mGameModel);
    }

    public void getTurnQueueChecksumsAPI(JSONObject out) { mGameAPI.getTurnQueueChecksums(mGameModel, out); }



    public void setMapEditorHoveredTilesCursorSizeAPI(JSONObject request) {
        mGameMapEditorAPI.setMapEditorHoveredTilesCursorSizeAPI(mGameModel, request);
    }


    public void setConfigurableStateGameplayHudIsVisible(boolean value) {
        mGameAPI.setConfigurableStateGameplayHudIsVisible(mGameModel, value);
    }
    public boolean getConfigurableStateGameplayHudIsVisible() {
        return mGameAPI.getConfigurableStateGameplayHudIsVisible(mGameModel);
    }










    public JSONObject getMainCameraInfoAPI() { return mGameAPI.getMainCameraInfo(mGameModel); }
    public JSONObject getSecondaryCameraInfoAPI() { return mGameAPI.getSecondaryCameraInfo(mGameModel); }





    public JSONArray getEntityTileID(JSONObject request) { return mGameAPI.getEntityTileID(request); }
    public JSONArray getCurrentActiveEntityTileID(JSONObject request) {
        return mGameAPI.getCurrentActiveEntityTileID(request);
    }

    public void setCameraZoomAPI(JSONObject request) {
        mGameAPI.setCameraZoomAPI(mGameModel, request);
    }

    public JSONObject getCenterTileEntityAPI() {
        return mGameAPI.getCenterTileEntity(mGameModel);
    }


    public void publishEvent(JSONObject event) {
        mGameAPI.publishEvent(mGameModel, event);
    }

    public static JSONObject createEvent(String event_id, Object... values) {
        JSONObject event = new JSONObject();
        if (values.length % 2 != 0) { return null; }
        for (int i = 0; i < values.length; i += 2) {
            String key = String.valueOf(values[i]);
            Object value = values[i + 1];
            event.put(key, value);
        }
        event.put("event_id", event_id);
        return event;
    }

    public JSONObject focusCamerasAndSelectionsOnActiveEntity(JSONObject request) {
        return mGameAPI.focusCamerasAndSelectionsOnActiveEntity(request);
    }

    public void forcefullyEndTurn() {
        mGameAPI.forcefullyEndTurn();
    }

    public void addLayersToHoveredTileIDs(String asset, String state, String depth) {
        mGameMapEditorAPI.addLayersToHoveredTileIDs(asset, state, depth);
    }

    public void removeLayersOfHoveredTileIDs(String depth) {
        mGameMapEditorAPI.removeLayersOfHoveredTileIDs(depth);
    }
}
