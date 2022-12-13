package game.map;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public record MapRoom (Rectangle blueprint, Set<Point> tiles) {

}
