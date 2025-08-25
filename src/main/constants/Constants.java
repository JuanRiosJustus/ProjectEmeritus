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
    public static final String MENU_SCENE = "MENU_SCENE";
    public static final String GAME_SCENE = "GAME_SCENE";
    public static final String MAP_EDITOR_SCENE = "MAP_EDITOR_SCENE";


//    public static final String

    public static final String DAMAGE = "Damage";
    public static final String COST = "Cost";
    public static final String USER_SAVE_STATE_FILE_JSON = file("UserSaveState.json");

    public static int CURRENT_SPRITE_SIZE = 64;
    public static final int NATIVE_SPRITE_SIZE = 64;

    public static final int SIDE_BAR_WIDTH = (int) (APPLICATION_WIDTH * .2);
    public static final int SIDE_BAR_ACTIONS_HEIGHT = (int) (APPLICATION_HEIGHT * .2);
    public static final int SIDE_BAR_LOGS_HEIGHT = (int) (APPLICATION_HEIGHT * .25);
    public static final int SIDE_BAR_MAIN_PANEL_HEIGHT = (int) (APPLICATION_HEIGHT * .5);
//    public static final String FONT_FILEPATH = file("./res/monofonto_rg.otf");
//    public static final String FONT_FILEPATH = file("./res/Nouveau_IBM.ttf");
//    public static final String FONT_FILEPATH = file("./res/Pixellari.ttf"); Born2bSportyFS.otf
//    public static final String FONT_FILEPATH = file("./res/Born2bSportyFS.otf");\
//    public static final String FONT_FILEPATH = file("./res/GrapeSoda.ttf");
    public static final String FONT_FILEPATH = file("./res/Lilian.ttf");
//    public static final String FONT_CLASSPATH = file("/resources/GrapeSoda.ttf");
    public static final String FONT_CLASSPATH = file("/resources/Lilian.ttf");
    public static final String FONT_V2_CLASSPATH = file("/resources/easvhs.ttf");
    public static final String GEMS_SPRITESHEET_PATH = file("res/tiles/gems.png");
    public static final String FLOORS_SPRITESHEET_FILEPATH = file("res/tiles/floors.png");
    public static final String ABILITY_DATA_FILE_JSON = file("res/json/abilities.json");
    public static final String ABILITIES_SPRITESHEET_FILEPATH2 = file("./res/graphics/graphics/");

    // public static final String UNITS_DATA_FILE_CSV = file("./res/units/units.csv");
//    public static final String UNITS_DATABASE = file("./res/database/units.csv");
    public static final String UNITS_DATABASE_RESOURCE_PATH = file("./res/database/units.json");
    public static final String TAGS_DATABASE = file("./res/database/tags.json");
    public static final String ABILITY_DATABASE_PATH = file("./res/database/ability.json");
    public static final String TAGS_DATABASE_PATH = file("./res/database/tags.json");
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


    public static final float PERCENT_PER_STAGE = .25f;
    public static final String USER_SAVE_FILE = "usersave.json";

    public static String file(String str) {
        return str.replaceAll("\"", File.separator);
    }
}
