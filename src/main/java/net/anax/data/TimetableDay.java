package net.anax.data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class TimetableDay {
    public LessonRow[] lessonRows;
    public String date;
    public String dayOfWeek;

    public TimetableDay(int rowCount){
        this.lessonRows = new LessonRow[rowCount];
    }

    public static TimetableDay parseFromJson(JSONObject data) {
        TimetableDay day = getBlankDay();
        if(data.containsKey("date") && data.get("date") instanceof String){
            day.date = (String)data.get("date");
        }
        if(data.containsKey("dayOfWeek") && data.get("dayOfWeek") instanceof String){
            day.dayOfWeek = (String) data.get("dayOfWeek");
        }

        if(data.containsKey("lessonRows") && data.get("lessonRows") instanceof JSONArray && !((JSONArray)data.get("lessonRows")).isEmpty() && ((JSONArray) data.get("lessonRows")).get(0) instanceof JSONObject){
            JSONArray lesson_row_array = (JSONArray) data.get("lessonRows");
            LessonRow[] lessonRows = new LessonRow[lesson_row_array.size()];
            for(int i = 0; i < lesson_row_array.size(); i++){
                lessonRows[i] = LessonRow.parseFromJson((JSONObject)lesson_row_array.get(i));
            }
            day.lessonRows = lessonRows;
        }
        return day;
    }

    public static TimetableDay getBlankDay(){
        TimetableDay day = new TimetableDay(0);
        day.date = "";
        day.dayOfWeek = "";
        return day;
    }
    public JSONObject getJsonObject() {
        JSONObject data = new JSONObject();
        data.put("date", date);
        data.put("dayOfWeek", dayOfWeek);

        JSONArray rows = new JSONArray();
        for(LessonRow row : lessonRows){
            rows.add(row.getJsonObject());
        }
        data.put("lessonRows", rows);
        return data;
    }
}
