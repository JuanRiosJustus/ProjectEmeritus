package main.ui.game;

import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import main.game.main.GameController;
import main.game.main.GameModel;
import main.game.stores.pools.FontPoolV2;
import org.json.JSONArray;
import org.json.JSONObject;

public class GamePanel extends Region {

    protected JSONObject mEphemeralObject = new JSONObject();
    protected JSONArray mEphemeralArray = new JSONArray();

    public GamePanel(int x, int y, int width, int height) {
        setPrefSize(width, height);
        setMinSize(width, height);
        setMaxSize(width, height);
        setLayoutX(x);
        setLayoutY(y);
    }

    public GamePanel(int width, int height) {
        this(0, 0, width, height);
    }

    protected static Font getFontForHeight(int height) {
        return FontPoolV2.getInstance().getFontForHeight(height);
    }
    public void gameUpdate(GameController gameController) { }
}
