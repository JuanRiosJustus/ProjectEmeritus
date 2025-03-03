package main.game.state;

import main.logging.EmeritusLogger;


public class GameSavedData extends SaveData{

    private static GameSavedData instance = null;
    private static final EmeritusLogger logger = EmeritusLogger.create(GameSavedData.class);
    private final String mSavedDataFilePath = "usersave.json";
    public static final String UNITS = "units";
    public static final String TILEMAPS = "tilemaps";

    private GameSavedData() { getOrLoadData(mSavedDataFilePath); }

    public static GameSavedData getInstance() {
        if (instance == null) {
            instance = new GameSavedData();
        }
        return instance;
    }
}
