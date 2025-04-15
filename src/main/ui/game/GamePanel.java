package main.ui.game;

import javafx.scene.CacheHint;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import main.game.components.AssetComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.EntityStore;
import main.game.stores.FontPool;
import main.graphics.AssetPool;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class GamePanel extends StackPane {
    private static Map<String, ImageView> specficViews = new LinkedHashMap<>();
    protected JSONObject mEphemeralObject = new JSONObject();
    protected final int mWidth;
    protected final int mHeight;
    protected final int mX;
    protected final int mY;

    public GamePanel(int x, int y, int width, int height) {
        setPrefSize(width, height);
        setMinSize(width, height);
        setMaxSize(width, height);
        setLayoutX(x);
        setLayoutY(y);

        setCache(true);
        setCacheHint(CacheHint.SPEED);

        mWidth = width;
        mHeight = height;
        mX = x;
        mY = y;
    }

    public GamePanel(int width, int height) {
        this(0, 0, width, height);
    }

    protected static Font getFontForHeight(int height) {
        return FontPool.getInstance().getFontForHeight(height);
    }
    public void gameUpdate(GameModel gameModel) {

    }


//    protected ImageView createAndCacheEntityIcon(String entityID) {
//        Entity entity = EntityStore.getInstance().get(entityID);
//        AssetComponent assetComponent = entity.get(AssetComponent.class);
//        String id = assetComponent.getMainID();
//        AssetV2 asset = AssetPool.getInstance().getAsset(id);
//        if (asset == null) return null;
//
//        Animation animation = asset.getAnimation();
//        Image image = SwingFXUtils.toFXImage(animation.toImage(), null);
//
//
//        ImageView view = new ImageView(image);
//        view.setPickOnBounds(false);
//        view.setFocusTraversable(false);
//
//        return view;
//    }

    protected ImageView createAndCacheEntityIcon(String entityID) {
        Entity entity = EntityStore.getInstance().get(entityID);
        if (entity == null) { return null; }
        AssetComponent assetComponent = entity.get(AssetComponent.class);
        String id = assetComponent.getMainID();
        Image image = AssetPool.getInstance().getImage(id);

        if (id == null) { return null; }

        ImageView view = new ImageView(image);
        view.setPickOnBounds(false);
        view.setFocusTraversable(false);

        return view;
    }
}
