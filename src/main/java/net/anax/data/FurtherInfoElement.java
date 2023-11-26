package net.anax.data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class FurtherInfoElement {
    public String name;
    public String value;
    public FurtherInfoElement(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public JSONObject getJSonObject() {
        JSONObject data = new JSONObject();
        data.put("name", name);
        data.put("value", value);
        return data;
    }
}
