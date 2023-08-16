package main.game.pathfinding.shadowcasting;

import java.util.ArrayList;
import java.util.List;

public class ShadowLine {
    final List<Shadow> shadows = new ArrayList<>();

    public void add(Shadow shadow) {
        // Figure out where to slot the new shadow in the list.
        var index = 0;
        for (; index < shadows.size(); index++) {
            // Stop when we hit the insertion point.
            if (shadows.get(index).start >= shadow.start) break;
        }

        // The new shadow is going here. See if it overlaps the
        // previous or next.
        Shadow overlappingPrevious = null;
        if (index > 0 && shadows.get(index - 1).end > shadow.start) {
            overlappingPrevious = shadows.get(index - 1);
        }

        Shadow overlappingNext = null;
        if (index < shadows.size() && shadows.get(index).start < shadow.end) {
            overlappingNext = shadows.get(index);
        }

        // Insert and unify with overlapping shadows.
        if (overlappingNext != null) {
            if (overlappingPrevious != null) {
                // Overlaps both, so unify one and delete the other.
                overlappingPrevious.end = overlappingNext.end;
                shadows.remove(index);
            } else {
                // Overlaps the next one, so unify it with that.
                overlappingNext.start = shadow.start;
            }
        } else {
            if (overlappingPrevious != null) {
                // Overlaps the previous one, so unify it with that.
                overlappingPrevious.end = shadow.end;
            } else {
                // Does not overlap anything, so insert.
                shadows.add(index, shadow);
            }
        }
    }
    public boolean isFullShadow() {
        return shadows.size() == 1 && shadows.get(0).start == 0 && shadows.get(0).end == 1;
    }

    public boolean isInShadow(Shadow projection) {
        for (Shadow shadow : shadows) {
            if (shadow.contains(projection)) { return true; }
        }
        return false;
    }
}