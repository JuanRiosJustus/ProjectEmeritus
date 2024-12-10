package main.constants;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;

public class Constants {

    public static final Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
//    public static final int APPLICATION_WIDTH = size.width;
//    public static final int APPLICATION_HEIGHT = size.height;
//    public static final int APPLICATION_WIDTH = 1920;
//    public static final int APPLICATION_HEIGHT = 1080;

//    public static final int APPLICATION_WIDTH = 1600;
//    public static final int APPLICATION_HEIGHT = 1000;
     public static final int APPLICATION_WIDTH = 1366;
     public static final int APPLICATION_HEIGHT = 768;
    

//     public static final int APPLICATION_WIDTH = 1280;
//     public static final int APPLICATION_HEIGHT = 720 - 40;
    public static final String APPLICATION_NAME = "Project Emeritus v.03";
    public static final int MAC_WINDOW_HANDLE_HEIGHT = 29;

    public static final String START_BUTTON = "Start";
    public static final String CONTINUE_BUTTON = "Continue";
    public static final String EDIT_BUTTON = "Edit";
    public static final String EXIT_BUTTON = "Exit";
    public static final String SETTINGS_BUTTON = "Settings";
    public static final String HP_COST = "Health Cost";
    public static final String MP_COST = "Mana Cost";
    public static final String SP_COST = "Stamina Cost";
    public static final String HP_DAMAGE = "Health Damage";
    public static final String MP_DAMAGE = "Mana Damage";
    public static final String SP_DAMAGE = "Stamina Damage";
    public static final String DAMAGE = "Damage";
    public static final String COST = "Cost";
    public static final String IMPACT = "Impact";
    public static final String TRAVEL = "Travel";
    public static final String ELEVATION = "Elevation";
    public static final String TILE = "Tile";
    public static final String INTELLIGENCE = "Intelligence";
    public static final String STRENGTH = "Strength";
    public static final String DEXTERITY = "Dexterity";
    public static final String WISDOM = "Wisdom";
    public static final String CHARISMA = "Charisma";
    public static final String CONSTITUTION = "Constitution";
    public static final String LUCK = "Luck";
    public static final String RESISTANCE = "Resistance";

    public static int CURRENT_SPRITE_SIZE = 64;
    public static final int NATIVE_SPRITE_SIZE = 64;

    public static final int SIDE_BAR_WIDTH = (int) (APPLICATION_WIDTH * .2);
    public static final int SIDE_BAR_ACTIONS_HEIGHT = (int) (APPLICATION_HEIGHT * .2);
    public static final int SIDE_BAR_LOGS_HEIGHT = (int) (APPLICATION_HEIGHT * .25);
    public static final int SIDE_BAR_MAIN_PANEL_HEIGHT = (int) (APPLICATION_HEIGHT * .5);
//    public static final String FONT_FILEPATH = file("./res/monofonto_rg.otf");
    public static final String FONT_FILEPATH = file("./res/Nouveau_IBM.ttf");
    public static final String GEMS_SPRITESHEET_PATH = file("res/tiles/gems.png");
    public static final String FLOORS_SPRITESHEET_FILEPATH = file("res/tiles/floors.png");
    public static final String ABILITY_DATA_FILE_JSON = file("res/json/abilities.json");
    public static final String ABILITIES_SPRITESHEET_FILEPATH2 = file("./res/graphics/graphics/");

    // public static final String UNITS_DATA_FILE_CSV = file("./res/units/units.csv");
//    public static final String UNITS_DATABASE = file("./res/database/units.csv");
    public static final String UNITS_DATABASE = file("./res/database/units.json");
    public static final String ACTION_DATABASE = file("./res/database/actions.json");;
//    public static final String ABILITIES_DATABASE = file("./res/database/abilities.csv");;
    public static final String UNITS_SPRITEMAP_FILEPATH = file("./res/graphics/units/");
    public static final String TILES_SPRITEMAP_FILEPATH = file("./res/graphics/tiles/");
    public static final String LIQUIDS_SPRITESHEET_FILEPATH = file("./res/graphics/liquids/");
    public static final String STRUCTURES_SPRITESHEET_FILEPATH = file("res/graphics/structures/");
    public static final String ABILITIES_SPRITEMAP_FILEPATH = file("./res/graphics/actions/");
    public static final String MISC_SPRITEMAP_FILEPATH = file("./res/graphics/misc/");
    public static final String TAGS = "Tags";

    public static final String AREA = "Area";
    public static final String RANGE = "Range";
    public static final String NAME = "Name";
    public static final String ACC = "Accuracy";
    public static final String TYPE = "Type";

    public static final String UP = "UP";
    public static final String DOWN = "DOWN";
    public static final String LEVEL = "Level";
    public static final String PHYSICAL_ATTACK = "PhysicalAttack";
    public static final String PHYSICAL_ATTACK_SPACED = "Physical Attack";
    public static final String PHYSICAL_DEFENSE = "PhysicalDefense";
    public static final String PHYSICAL_DEFENSE_SPACED = "Physical Defense";
    public static final String MAGICAL_ATTACK = "MagicalAttack";
    public static final String MAGICAL_ATTACK_SPACED = "Magical Attack";
    public static final String MAGICAL_DEFENSE = "MagicalDefense";
    public static final String MAGICAL_DEFENSE_SPACED = "Magical Defense";
    public static final String CLIMB = "Climb";
    public static final String SPEED = "speed";
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
    public static final String WATER = "Water";
    public static final String DARK = "Dark";
    public static final String FIRE = "Fire";
    public static final String GROUND = "Ground";
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

    public static final String USER_SAVE_FILE = "usersave.json";

    public static String file(String str) {
        return str.replaceAll("\"", File.separator);
    }
}
