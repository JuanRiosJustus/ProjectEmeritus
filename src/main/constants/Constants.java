package main.constants;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;

public class Constants {

    public static final Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
    public static final int APPLICATION_WIDTH = size.width;
    public static final int APPLICATION_HEIGHT = size.height - 40;
//    public static final int APPLICATION_WIDTH = 1920;
//    public static final int APPLICATION_HEIGHT = 1080;
//     public static final int APPLICATION_WIDTH = 1366;
//     public static final int APPLICATION_HEIGHT = 768 - 40;
    

    // public static final int APPLICATION_WIDTH = 1280;
    // public static final int APPLICATION_HEIGHT = 720 - 40;
    public static final String APPLICATION_NAME = "Project Emeritus v.03";
    public static final int MAC_WINDOW_HANDLE_HEIGHT = 29;

    public static final String START_BUTTON = "Start";
    public static final String CONTINUE_BUTTON = "Continue";
    public static final String EDIT_BUTTON = "Edit";
    public static final String EXIT_BUTTON = "Exit";
    public static final String SETTINGS_BUTTON = "Settings";

    public static int CURRENT_SPRITE_SIZE = 64;
    public static final int BASE_SPRITE_SIZE = 64;

    public static final int SIDE_BAR_WIDTH = (int) (APPLICATION_WIDTH * .2);
    public static final int SIDE_BAR_ACTIONS_HEIGHT = (int) (APPLICATION_HEIGHT * .2);
    public static final int SIDE_BAR_LOGS_HEIGHT = (int) (APPLICATION_HEIGHT * .25);
    public static final int SIDE_BAR_MAIN_PANEL_HEIGHT = (int) (APPLICATION_HEIGHT * .5);

    public static final String FONT_FILEPATH = file("./res/data/MAKISUPA.ttf");
    public static final String TEST_MAP = file("./res/testMap.txt");

    public static final String SHADOWS_SPRITESHEET_FILEPATH = file("./res/tiles/shadows.png");

    public static final String GEMS_SPRITESHEET_PATH = file("res/tiles/gems.png");
    public static final String FLOORS_SPRITESHEET_FILEPATH = file("res/tiles/floors.png");
    public static final String WALLS_SPRITESHEET_FILEPATH = file("res/tiles/walls.png");
    public static final String STRUCTURES_SPRITESHEET_FILEPATH = file("res/tiles/structures.png");
    public static final String LIQUIDS_SPRITESHEET_FILEPATH = file("res/tiles/liquids.png");

    // public static final String ABILITY_DATA_FILE_CSV = file("./res/abilities/abilities.csv");
    public static final String ABILITY_DATA_FILE_JSON = file("res/jsons/abilities.json");
    public static final String ABILITIES_SPRITESHEET_FILEPATH = file("./res/abilities/graphics/");

    // public static final String UNITS_DATA_FILE_CSV = file("./res/units/units.csv");
    public static final String UNITS_DATA_FILE_JSON = file("res/jsons/units.json");
    public static final String UNITS_SPRITESHEET_FILEPATH = file("./res/units/graphics/");

    public static final String TAGS = "Tags";
    public static final String ABILITIES = "Abilities";
    public static final int FONT_SIZE = 20;

    public static final String AREA_OF_EFFECT = "AreaOfEffect";
    public static final String RANGE = "Range";
    public static final String NAME = "name";
    public static final String ACCURACY = "Accuracy";
    public static final String TYPE = "Type";

    public static final String MISSING = "Missing";
    public static final String CURRENT = "Current";
    public static final String MAX = "Max";
    public static final String SEMICOLON = ";";
    public static final String EQUAL = "=";
    public static final String FLAT = "Flat";
    public static final String PERCENT = "Percent";
    public static final String UP = "UP";
    public static final String DOWN = "DOWN";
    public static final String LEVEL = "Level";
    public static final String PHYSICAL_ATTACK = "PhysicalAttack";
    public static final String PHYSICAL_DEFENSE = "PhysicalDefense";
    public static final String MAGICAL_ATTACK = "MagicalAttack";
    public static final String MAGICAL_DEFENSE = "MagicalDefense";
    public static final String CLIMB = "Climb";
    public static final String SPEED = "Speed";
    public static final String MOVE = "Move";
    public static final String HEALTH = "Health";
    public static final String ENERGY = "Energy";
    public static final String PA = "PA";
    public static final String PD = "PD";
    public static final String MA = "MA";
    public static final String MD = "MD";
    public static final String DESCRIPTION = "Description";
    public static final String STATUS = "Status";
    public static final String CAN_HIT_USER = "CanHitUser";
    public static final String LIGHT = "Light";
    public static final String AIR = "Air";
    public static final String WATER = "Water";
    public static final String DARK = "Dark";
    public static final String FIRE = "Fire";
    public static final String EARTH = "Earth";
    public static final String NORMAL = "Normal";
    public static final String PIERCE = "Pierce";
    public static final String SLASH = "Slash";
    public static final String BLUNT = "Blunt";
    public static final float PERCENT_PER_STAGE = .25f;

    public static final String NEGATE = "Negate";

    public static final String MAIN_MENU_SCENE = "MainMenuScene";
    public static final String GAME_SCENE = "GameScene";
    public static final String EDIT_SCENE = "EditScene";
    public static final String MAIN_MENU = "MainMenu";
    public static final String EXPERIENCE_THRESHOLD = "ExperienceThreshold";
    public static final String EXPERIENCE = "Experience";

    public static final String USER_SAVE_DIRECTORY = "res/data/";

    public static String file(String str) {
        return str.replaceAll("\"", File.separator);
    }
}
