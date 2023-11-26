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
