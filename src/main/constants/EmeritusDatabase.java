package main.constants;

import jsonsql.main.JSONDatabase;
import main.logging.EmeritusLogger;

import java.nio.file.Files;
import java.nio.file.Path;

public class EmeritusDatabase extends JSONDatabase {

    private static EmeritusDatabase mInstance = null;
    public static EmeritusDatabase getInstance() {
        if (mInstance == null) {
            mInstance = new EmeritusDatabase();
        }
        return mInstance;
    }
    private final EmeritusLogger mLogger = EmeritusLogger.create(EmeritusDatabase.class);
    public static final String UNITS_DATABASE = "unit";
    public static final String ABILITY_DATABASE = "ability";
    private EmeritusDatabase() {
        mLogger.info("Started initializing {}", getClass().getSimpleName());

        try {
            addTable(UNITS_DATABASE, Files.readString(Path.of("./res/database/unit.json")));
            addTable(ABILITY_DATABASE, Files.readString(Path.of("./res/database/ability.json")));
        } catch (Exception ex) {
            mLogger.info("Example: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(-1);
        }
        mLogger.info("Finished initializing {}", getClass().getSimpleName());
    }
}
