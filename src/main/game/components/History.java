package main.game.components;

import java.util.*;

public class History extends Component {

    private final Queue<String> mBacklog = new LinkedList<>();
    private static final int LIMIT = 5;

    public void log(String str) {
        if (mBacklog.size() == 5) {
            mBacklog.poll();
        }
        mBacklog.add(str);
    }

    public List<String> getLogs() {
        List<String> list = new LinkedList<>(mBacklog);
        Collections.reverse(list);
        return list;
    }
    public int getHashState() {
        int state = 0;
        for (String str : mBacklog) {
            state += str.length();
        }
        return state;
    }
}
