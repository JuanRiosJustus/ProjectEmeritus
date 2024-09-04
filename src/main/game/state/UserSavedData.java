package main.game.state;

import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class UserSavedData extends SaveData {
    
    private static UserSavedData instance = null;
    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(UserSavedData.class);
    private final String mSavedDataFilePath = "usersave.json";
    public static final String UNITS = "units";
    public static final String TILEMAPS = "tilemaps";

    private UserSavedData() { getOrLoadData(mSavedDataFilePath); }

    public static UserSavedData getInstance() {
        if (instance == null) {
            instance = new UserSavedData();
        }
        return instance;
    }
}
