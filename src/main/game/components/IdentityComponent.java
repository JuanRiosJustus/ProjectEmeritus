package main.game.components;

public class IdentityComponent extends Component {
    private static final String ID_KEY = "id";
    private static final String NICKNAME_KEY = "key";
    public IdentityComponent(String id, String nickname) {
        put(ID_KEY, id);
        put(NICKNAME_KEY, nickname);
    }

    public String getID() { return (String) get(ID_KEY); }
    public String getNickname() { return (String) get(NICKNAME_KEY); }
    public String toString() { return (String) get(NICKNAME_KEY); }
}
