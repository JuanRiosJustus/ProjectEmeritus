package main.game.systems.texts;

import main.constants.Vector3f;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.FontPool;
import main.game.systems.GameSystem;
import org.json.JSONObject;

import java.awt.*;
import java.util.*;

public class FloatingTextSystem extends GameSystem {

    private final Canvas mFontCalculator = new Canvas();
    private final Font mFont = FontPool.getInstance().getBoldFont(20);
    private final Queue<String> mGarbageCalculator = new LinkedList<>();
    private final BasicStroke mOutlineStroke = new BasicStroke(3f);

    public void enqueue(String text, Vector3f vector, Color color, int spriteWidths) {
        FontMetrics metrics = mFontCalculator.getFontMetrics(mFont);
        int width = metrics.stringWidth(text);
        int height = metrics.getHeight();

        int x = (int) vector.x;
        int y = (int) vector.y;

//        mFloatingText.add(new FloatingText(text, x, y, width, height, color, false, spriteWidths));
    }


    public Font getFont() { return mFont; }
    public BasicStroke getOutlineStroke() { return mOutlineStroke; }

    @Override
    public void update(GameModel model, Entity unit) {

        Map<String, JSONObject> floatingTexts = model.getGameState().getFloatingTexts();
        for (String key : floatingTexts.keySet()) {
            FloatingText floatingText = (FloatingText) floatingTexts.get(key);
            floatingText.update();
            if (!floatingText.hasPassedLifeExpectancy()) { continue; }
            mGarbageCalculator.add(key);
        }

        // remove the floating text that have been collected
        while (!mGarbageCalculator.isEmpty()) {
            String keyToRemove = mGarbageCalculator.poll();
            model.getGameState().removeFloatingText(keyToRemove);
        }
    }
}