package main.game.components;

public class IdentityComponent extends Component {
    private static final String ID_KEY = "id";
    private static final String NICKNAME_KEY = "key";
    private static final String TYPE_KEY = "type";
    public IdentityComponent(String id, String nickname, String type) {
        put(ID_KEY, id);
        put(NICKNAME_KEY, nickname);
        put(TYPE_KEY, type);
    }

    public String getID() { return getString(ID_KEY); }
    public String getNickname() { return getString(NICKNAME_KEY); }
    public String getType() { return getString(TYPE_KEY); }
    public String toString() { return (String) get(NICKNAME_KEY); }
}
