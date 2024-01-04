package net.anax.data.timetable;

import org.json.simple.JSONObject;

public class FurtherInfoElement {
    public String name;
    public String value;
    public FurtherInfoElement(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static FurtherInfoElement parseFromJson(JSONObject data) {
        if(data.containsKey("name") && data.get("name") instanceof String){
            if(data.containsKey("value") && data.get("value") instanceof String){
                return new FurtherInfoElement((String) data.get("name"), (String) data.get("value"));
            }
            return new FurtherInfoElement((String) data.get("name"), "");
        }
        return new FurtherInfoElement("", "");
    }

    public JSONObject getJSonObject() {
        JSONObject data = new JSONObject();
        data.put("name", name);
        data.put("value", value);
        return data;
    }
}
