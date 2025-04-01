package main.ui.game;

import javafx.scene.CacheHint;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import main.game.components.AssetComponent;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.stores.factories.EntityStore;
import main.game.stores.pools.FontPool;
import main.graphics.AssetPool;
import org.json.JSONObject;

public class GamePanel extends StackPane {

    protected JSONObject mRequestObject = new JSONObject();

    public GamePanel(int x, int y, int width, int height) {
        setPrefSize(width, height);
        setMinSize(width, height);
        setMaxSize(width, height);
        setLayoutX(x);
        setLayoutY(y);

        setCacheHint(CacheHint.SPEED);
        setCache(true);
    }

    public GamePanel(int width, int height) {
        this(0, 0, width, height);
    }

    protected static Font getFontForHeight(int height) {
        return FontPool.getInstance().getFontForHeight(height);
    }
    public void gameUpdate(GameController gameController) { }

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
