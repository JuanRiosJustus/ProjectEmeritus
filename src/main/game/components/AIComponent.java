package main.game.components;

public class AIComponent extends Component {
    private static final String IS_AI = "is_ai";
    private static final String FOCUSED_ENTITY_ID = "FOCUSED_ENTITY_ID";
    public AIComponent(boolean isAI) { put(IS_AI, isAI); }
    public boolean isAI() { return getBooleanValue(IS_AI, false); }
    public boolean isUserControlled() { return !isAI(); }
    public void focusedEntity(String val) { put(FOCUSED_ENTITY_ID, val); }
    public String getFocusedEntityID() { return getString(FOCUSED_ENTITY_ID); }
}
