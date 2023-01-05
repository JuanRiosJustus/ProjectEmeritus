package game.stores.pools.ability;

import java.util.Map;

public class AbilityUtils {
    public static float getTotalRati0(Map<String, Float> map) {
        float value = 0.0f;
        for (Map.Entry<String, Float> entry : map.entrySet()) {
            value += entry.getValue();
        }
        return value;
    }
}
