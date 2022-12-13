package game.stats.detail;

import java.util.Locale;

public enum Detail {

    Name("Name"),
    Type("Type"),
    Background("Background"),
    Tags("Tags"),
    Rarity("Rarity"),
    Passive("Passive");

    public final String m_name;

    Detail(String name) { m_name = name; }

    public static Detail from(String name) {
        name = name.toLowerCase(Locale.ROOT);
        for (Detail stat : values()) {
            String lowercase = stat.m_name.toLowerCase(Locale.ROOT);
            if (lowercase.equals(name)) { return stat; }
        }
        return null;
    }
}
