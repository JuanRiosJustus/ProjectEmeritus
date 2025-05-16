package main.ui.scenes.mapeditor;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import main.game.stores.ColorPalette;
import main.game.stores.FontPool;
import main.graphics.AssetPool;
import main.ui.custom.UpwardGrowingUI;
import main.ui.foundation.BeveledLabel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MapEditorSceneLayersPanel extends UpwardGrowingUI.Pane {

    private static final Map<Integer, Background> mBackgroundCache = new HashMap<>();
    private Background mLastBackground = null;
    public MapEditorSceneLayersPanel(double baseX, double baseY, double itemWidth, double itemHeight) {
        super(baseX, baseY, itemWidth, itemHeight);
    }

    public void update(JSONObject hoveredTile, int rowWidth, int rowHeight) {
        clearRows();
        setBackground(new Background(new BackgroundFill(Color.LEMONCHIFFON, CornerRadii.EMPTY, Insets.EMPTY)));
        setItemSize(rowWidth, rowHeight);

        int newPaneWidth = (int) getItemWidth();
        int newPaneHeight = (int) getItemHeight();
        BeveledLabel b = new BeveledLabel(newPaneWidth, newPaneHeight);
        String tileRow = hoveredTile.getString("row");
        String tileColumn = hoveredTile.getString("column");
        String txt = "[Row: " + tileRow + ", Column: " + tileColumn + "]";
        Font fitFont = FontPool.getInstance().getFitFont(txt, b.getFont(), newPaneWidth * .9, newPaneHeight * .9);
        b.setFont(fitFont);
        b.setText(txt);
        addRow(b);

        mLastBackground = null;

        JSONArray layers = hoveredTile.getJSONArray("layers");
        for (int i = 0; i < layers.size(); i++) {
            JSONObject layer = layers.getJSONObject(i);
            HBox hBox = createTileLayerItem(layer);
            addRow(hBox);
        }

        JSONObject structure = hoveredTile.getJSONObject("structure");
        if (structure != null) {
            HBox hBox = createTileLayerStructure(structure);
            addRow(hBox);
        }

        if (mLastBackground != null) { setBackground(mLastBackground); }
    }


    private HBox createTileLayerStructure(JSONObject layer) {
        String asset = layer.getString("asset");

        int newPaneWidth = (int) getItemWidth();
        int newPaneHeight = (int) getItemHeight();
        HBox hBox = new HBox();
        hBox.setPrefSize(newPaneWidth, newPaneHeight);
        hBox.setMinSize(newPaneWidth, newPaneHeight);
        hBox.setMaxSize(newPaneWidth, newPaneHeight);

        int imageWidth = Math.min(newPaneHeight, newPaneWidth);
        int imageHeight = imageWidth;
        String id = AssetPool.getInstance().getOrCreateStaticAsset(
                imageWidth, imageHeight, asset, -1, asset + "_map_editor_scene_structure"
        );
        Image image = AssetPool.getInstance().getImage(id);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(imageWidth);
        imageView.setFitHeight(imageHeight);

        int labelVBoxWidth = newPaneWidth - imageWidth;
        int labelVBoxHeight = newPaneHeight;
        VBox vBox = new VBox();
        vBox.setPrefSize(labelVBoxWidth, labelVBoxHeight);
        vBox.setMinSize(labelVBoxWidth, labelVBoxHeight);
        vBox.setMaxSize(labelVBoxWidth, labelVBoxHeight);
        vBox.setBackground(new Background(new BackgroundFill(ColorPalette.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        int upperButtonWidth = labelVBoxWidth;
        int upperButtonHeight = (int) (labelVBoxHeight * .7);
        BeveledLabel upperBeveledLabel = new BeveledLabel(upperButtonWidth, upperButtonHeight);
        String upperText = "[" + asset + "]";
        upperBeveledLabel.setFitText(upperText);


        int lowerButtonWidth = upperButtonWidth;
        int lowerButtonHeight = labelVBoxHeight - upperButtonHeight;
        BeveledLabel lowerBeveledLabel = new BeveledLabel(lowerButtonWidth, lowerButtonHeight);
        String lowerText = asset;
        if (asset.contains("/")) {
            lowerText = asset.substring(asset.lastIndexOf("/") + 1, asset.lastIndexOf("."));
        }
        upperBeveledLabel.setFitText(lowerText);

        vBox.getChildren().addAll(upperBeveledLabel, lowerBeveledLabel);
        hBox.getChildren().addAll(imageView, vBox);


        Background background = getBackgroundForLayerItem(layer);
        mLastBackground = background;
        vBox.setBackground(background);

        return hBox;
    }


    private HBox createTileLayerItem(JSONObject layer) {
        String asset = layer.getString("asset");
        int depth = layer.getIntValue("depth");
        int lowest = layer.getIntValue("lowest");
        int highest = layer.getIntValue("highest");

        int newPaneWidth = (int) getItemWidth();
        int newPaneHeight = (int) getItemHeight();
        HBox hBox = new HBox();
        hBox.setPrefSize(newPaneWidth, newPaneHeight);
        hBox.setMinSize(newPaneWidth, newPaneHeight);
        hBox.setMaxSize(newPaneWidth, newPaneHeight);

        int imageWidth = Math.min(newPaneHeight, newPaneWidth);
        int imageHeight = imageWidth;
        String id = AssetPool.getInstance().getOrCreateStaticAsset(
                imageWidth, imageHeight, asset, -1, asset + "_map_editor_scene"
        );
        Image image = AssetPool.getInstance().getImage(id);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(imageWidth);
        imageView.setFitHeight(imageHeight);

        int labelVBoxWidth = newPaneWidth - imageWidth;
        int labelVBoxHeight = newPaneHeight;
        VBox vBox = new VBox();
        vBox.setPrefSize(labelVBoxWidth, labelVBoxHeight);
        vBox.setMinSize(labelVBoxWidth, labelVBoxHeight);
        vBox.setMaxSize(labelVBoxWidth, labelVBoxHeight);
        vBox.setBackground(new Background(new BackgroundFill(ColorPalette.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        int upperButtonWidth = labelVBoxWidth;
        int upperButtonHeight = (int) (labelVBoxHeight * .7);
        BeveledLabel upperBeveledLabel = new BeveledLabel(upperButtonWidth, upperButtonHeight);
        String upperText = "Layer (" + lowest + " - " + highest + "] (" + depth + ")";
        upperBeveledLabel.setFitText(upperText);


        int lowerButtonWidth = upperButtonWidth;
        int lowerButtonHeight = labelVBoxHeight - upperButtonHeight;
        BeveledLabel lowerBeveledLabel = new BeveledLabel(lowerButtonWidth, lowerButtonHeight);
        String lowerText = asset;
        if (asset.contains("/")) { lowerText = asset.substring(asset.lastIndexOf("/") + 1, asset.lastIndexOf(".")); }
        lowerBeveledLabel.setFitText(lowerText);

        vBox.getChildren().addAll(upperBeveledLabel, lowerBeveledLabel);
        hBox.getChildren().addAll(imageView, vBox);


        Background background = getBackgroundForLayerItem(layer);
        mLastBackground = background;
        vBox.setBackground(background);

        return hBox;
    }

    private Background getBackgroundForLayerItem(JSONObject layer) {
        String asset = layer.getString("asset");
        String state = layer.getString("state");
        int depth = layer.getIntValue("depth");
        int lowest = layer.getIntValue("lowest");
        int highest = layer.getIntValue("highest");
        int hash = Objects.hash(asset, state, depth, lowest, highest);
        Background background = mBackgroundCache.get(hash);
        if (background != null) { return background; }

        int imageWidth = (int) Math.min(getItemWidth(), getItemHeight());
        int imageHeight = imageWidth;
        String id = AssetPool.getInstance().getOrCreateStaticAsset(
                imageWidth, imageHeight, asset, -1, asset + "_map_layers_backgrounds"
        );

        Image image = AssetPool.getInstance().getImage(id);
        BackgroundSize newBackgroundSize = new BackgroundSize(
                100, 100, true, true, false, false
        );
        BackgroundImage newBackgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                newBackgroundSize
        );
        Background newBackground = new Background(newBackgroundImage);

        mBackgroundCache.put(hash, newBackground);
        return newBackground;
    }
}
