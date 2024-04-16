package main.ouput;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import main.constants.Constants;
import main.game.components.Identity;
import main.game.components.Statistics;
import main.game.entity.Entity;
import main.game.stores.pools.unit.UnitPool;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserSave {
    private static final ELogger mLogger = ELoggerFactory.getInstance().getELogger(UserSave.class);
    private static UserSave mInstance = null;
    public static UserSave getInstance() {
        if (mInstance == null) {
            mInstance = new UserSave();
        }
        return mInstance;
    }

    public final String SUB_STATE_NAME = "Name";
    public final String SUB_STATE_UNITS = "Units";
    public final String SUB_STATE_ITEMS = "Items";
    public final String ROOT_STATE_SELECTED = "Selected";
    private JsonObject mSubRootState = null;
    private JsonObject mRootState = null;
    private final String saveLocation = Constants.USER_SAVE_FILE;
    private UserSave() {
        mLogger.info("Started initializing {}", getClass().getSimpleName());
        // 1.) Load the save file. If not found, create a new save file

        // Get available json save state
        JsonObject rootState = getOrDefaultRootState(saveLocation);

        // 2.) Select the sub state
        JsonObject subState = getOrDefaultSubState(rootState, ROOT_STATE_SELECTED, saveLocation);

        // 3.) Ensure the integrity of the sub state
        JsonObject okSubState = getOrNullValidState(rootState, subState, true, saveLocation);
        mSubRootState = okSubState;
        mRootState = rootState;


        mLogger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public List<Entity> loadUnitsFromCollection() {
        // Get the user collection from the subRootState
        JsonObject unitCollection = (JsonObject) mSubRootState.get(SUB_STATE_UNITS);
        if (unitCollection == null) {
            mRootState.put("units", new JsonObject());
            unitCollection = (JsonObject) mSubRootState.get(SUB_STATE_UNITS);
        }

        List<Entity> result = new ArrayList<>();
        for (Object object : unitCollection.values()) {

            if (object instanceof JsonObject jsonObject) {
//                Entity entity = UnitFactory.load(jsonObject, true);
                String uuid = UnitPool.getInstance().create(jsonObject, true);
                Entity entity = UnitPool.getInstance().get(uuid);
                result.add(entity);
            }
        }
        return result;
    }
    public void saveUnitToCollection(List<Entity> entities) {
        // Get the user collection from the subRootState
        JsonObject unitCollection = (JsonObject) mSubRootState.get(SUB_STATE_UNITS);
        if (unitCollection == null) {
            mRootState.put("units", new JsonObject());
            unitCollection = (JsonObject) mSubRootState.get(SUB_STATE_UNITS);
        }

        // add the units to the state
        for (Entity entity : entities) {
            JsonObject object = new JsonObject();

            Statistics statistics = entity.get(Statistics.class);
            object.put("level", statistics.getLevel());
            object.put("experience", statistics.getExperience());
            object.put("species", statistics.getSpecies());

            Identity identity = entity.get(Identity.class);
            object.put("uuid", identity.getUuid());
            object.put("name", identity.getName());

            unitCollection.put(identity.getUuid(), object);
        }
        write(saveLocation, mRootState, "Saved Units", "Couldn't save units");
    }

    private JsonObject getOrNullValidState(JsonObject rootState, JsonObject subState, boolean update, String saveLocation) {
        // List of keys that are expected in the sub state
        Object[][] keysAndValues = new Object[][] {
                new Object[]{SUB_STATE_NAME, "" },
                new Object[]{SUB_STATE_UNITS, new JsonObject() },
                new Object[]{SUB_STATE_ITEMS, new JsonObject() }
        };

        // If an expected key is not in the state, return null
        for (Object[] keyValue : keysAndValues) {
            String key = (String) keyValue[0];
            Object value = keyValue[1];
            if (subState.containsKey(key)) { continue; }
            if (!subState.containsKey(key) && update) {
                subState.put(key, value);
                continue;
            }
            return null;
        }

        if (update) {
            write(saveLocation, rootState,
                    "Successfully validated state", "Unsuccessfully validated state");
        }
        return subState;
    }

    private JsonObject getOrDefaultSubState(JsonObject rootState, String expectedKey, String saveFileLocation) {
        mLogger.info("Trying to load user save.");
        String subSaveName = (String) rootState.get(expectedKey);
        JsonObject subSave = null;

        try {
            // Check the expected key for the sub save state. If available, use that.
            if (subSaveName != null) {
                subSave = (JsonObject) rootState.get(subSaveName);
                mLogger.info("Loaded selected sub state");
            } else {
                // Since there are no save states selected, get first available if possible
                List<String> states = rootState.keySet()
                        .stream()
                        .filter(e -> !e.equalsIgnoreCase(expectedKey))
                        .toList();
                if (!states.isEmpty()) {
                    subSave = (JsonObject) rootState.get(states.get(0));
                    mLogger.warn("Loaded first detected sub state");
                } else {
                    // since there are no save states available, create one
                    subSave = new JsonObject();
                    String pattern = "yyyy_MM_dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    String name = simpleDateFormat.format(new Date()) + "_auto_save";
                    rootState.put(expectedKey, name);
                    rootState.put(name, subSave);

                    write(saveFileLocation, rootState,
                            "Created new default sub state", "Unable to create new sub state");
                }
            }
        } catch (Exception ex) {
            mLogger.error("User's root save is invalid; {}. Creating new user save.", ex.getMessage());
            // since there are no save states available, create one
            subSave = new JsonObject();
            String pattern = "yyyy_MM_dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String name = simpleDateFormat.format(new Date()) + "_auto_save";
            rootState.put(expectedKey, name);
            rootState.put(name, subSave);

            write(saveFileLocation, rootState,
                    "Created new default sub state", "Unable to create new sub state");
            mLogger.error("Finished creating new user save.");
        }

        return subSave;
    }
    private void write(String location, JsonObject toWrite, String success, String failure) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(location, false), true);
            out.write(toWrite.toJson());
            out.close();
            mLogger.info(success);
        } catch (Exception ex) {
            mLogger.error(failure + ", " + ex);
        }
    }

    private JsonObject getOrDefaultRootState(String saveFileLocation) {
        JsonObject state = null;
        try {
            // Try loading the already created file
            FileReader reader = new FileReader(saveFileLocation);
            state = (JsonObject) Jsoner.deserialize(reader);
            mLogger.info("Successfully loaded user save {}", saveFileLocation);
        } catch (Exception ex) {
            // Try creating a new file
            state = new JsonObject();
            write(saveFileLocation, state,
                    "Successfully created new user save " + saveFileLocation,
                    "Unsuccessful loading user save " + saveFileLocation);
        }
        return state;
    }
}
