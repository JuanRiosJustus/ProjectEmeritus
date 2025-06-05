package main.game.components.tile;

import main.game.components.Component;

public class StructureComponent extends Component {

    private static final String STRUCTURE_NAME = "name";
    public StructureComponent(String structure) {
        put(STRUCTURE_NAME, structure);
    }

    public String getName() { return getString(STRUCTURE_NAME); }
}
