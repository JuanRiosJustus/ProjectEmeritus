package main.json;

import org.json.JSONObject;

import java.util.stream.Stream;

public class JsonObjectValdiator {

    private static final String REQUIRED = "required";
    private static final String OPTIONAL = "optional";
    /**
     * This json object is used as a way to place units on a map
     * @param suspect
     * @return
     */
    public static JSONObject isValidUnitPlacementObject(JSONObject suspect) {
        String[][] keys = new String[][]{
                new String[]{ "name", REQUIRED },
                new String[]{ "uuid", REQUIRED },
                new String[]{ "level", REQUIRED },
                new String[]{ "experience", REQUIRED },
                new String[]{ "class", REQUIRED },

                new String[]{ "items", REQUIRED },

                new String[]{ "row", REQUIRED },
                new String[]{ "column", REQUIRED }
        };
        for (String[] key : keys) {
            String name = key[0];
            String requirement = key[1];
            if (requirement.equals(REQUIRED) && !suspect.has(name)) {
                return null;
            }
        }
        return suspect;
    }
}
