package main.constants.json;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.stream.Stream;

public class JSONStack implements Iterable<JSONObject> {

    private final JSONArray mStack;

    public JSONStack() {
        mStack = new JSONArray();
    }

    /**
     * Push a JSONObject onto the stack (top is end of array).
     */
    public void push(JSONObject obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Cannot push null onto the stack.");
        }
        mStack.add(obj);
    }

    /**
     * Pop the top JSONObject off the stack.
     */
    public JSONObject pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        int topIndex = mStack.size() - 1;
        JSONObject top = mStack.getJSONObject(topIndex);
        mStack.remove(topIndex);
        return top;
    }

    /**
     * Peek at the top JSONObject without removing it.
     */
    public JSONObject peek() {
        if (isEmpty()) { return null; }
        return mStack.getJSONObject(mStack.size() - 1);
    }

    /**
     * Check if the stack is empty.
     */
    public boolean isEmpty() {
        return mStack.isEmpty();
    }

    /**
     * Get the number of elements in the stack.
     */
    public int size() {
        return mStack.size();
    }

    /**
     * Clear all elements from the stack.
     */
    public void clear() {
        while (!isEmpty()) {
            mStack.removeLast();
        }
    }

    /**
     * Get the underlying JSONArray (bottom to top).
     */
    public JSONArray toJSONArray() {
        return new JSONArray(mStack); // makes a shallow copy
    }

    /**
     * Rebuild the stack from a JSONArray.
     */
    public void fromJSONArray(JSONArray source) {
        clear();
        for (Object item : source) {
            if (!(item instanceof JSONObject)) {
                throw new IllegalArgumentException("All elements must be JSONObjects.");
            }
            mStack.add(item);
        }
    }

    /**
     * Allow for-each iteration from bottom to top of the stack.
     */
    @Override
    public Iterator<JSONObject> iterator() {
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < mStack.size();
            }

            @Override
            public JSONObject next() {
                return mStack.getJSONObject(index++);
            }
        };
    }

    public static void main(String[] args) {
        JSONStack stack = new JSONStack();

        JSONObject one = new JSONObject();
        one.put("action", "move");
        one.put("x", 10);

        JSONObject two = new JSONObject();
        two.put("action", "attack");
        two.put("target", "goblin");

        stack.push(one);
        stack.push(two);

        System.out.println("Top: " + stack.peek().toJSONString());
        System.out.println("Stack size: " + stack.size());

        JSONObject popped = stack.pop();
        System.out.println("Popped: " + popped.toJSONString());

        System.out.println("Now top: " + stack.peek().toJSONString());
    }
}