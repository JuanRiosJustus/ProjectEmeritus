package main.json;

import com.github.cliftonlabs.json_simple.JsonObject;
import main.game.entity.Entity;

import java.io.Serializable;

public abstract class JsonSerializable {
    protected JsonObject mJsonData = new JsonObject();
    public JsonObject toJsonObject(JsonObject toWriteTo) { return mJsonData; }
    public JsonObject toJsonObject() { return toJsonObject(mJsonData); }
    public String toJsonString() { return toJsonObject(mJsonData).toJson(); }
}