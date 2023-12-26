package main.ouput;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import main.constants.Constants;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
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

    public final String NAME = "name";
    public final String SUB_SAVE = "selected";
    private JsonObject mSaveState = null;
    private UserSave() {
        mLogger.info("Started initializing {}", getClass().getSimpleName());
        // 1.) Load the save file. If not found, create a new save file
        String saveFileLocation = Constants.USER_SAVE_FILE;

        // Get available json save state
        JsonObject rootState = getOrDefaultRootState(saveFileLocation);

        // 2.) Select the sub state
        JsonObject subState = getOrDefaultSubState(rootState, SUB_SAVE, saveFileLocation);

        // 3.) Ensure the integrity of the sub state
        JsonObject okSubState = validateState(subState);


        mLogger.info("Finished initializing {}", getClass().getSimpleName());
    }

    private JsonObject validateState(JsonObject subState) {
        // List of keys that are expected in the sub state
        String[] keys = new String[]{
                "Units"
        };
        // If an expected key is not in the state, return null
        for (String key : keys) {
            if (subState.containsKey(key)) { continue; }
            return null;
        }

        return subState;
    }

    private JsonObject getOrDefaultSubState(JsonObject rootState, String expectedKey, String saveFileLocation) {
        String subSaveName = (String) rootState.get(expectedKey);
        JsonObject subSave = null;

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
