package net.anax.data.timetable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class LessonRow {
    public TimetableLesson[] lessons;

    public LessonRow(int lessonCount){
        this.lessons = new TimetableLesson[lessonCount];
    }

    public static LessonRow parseFromJson(JSONObject data) {
        LessonRow row = new LessonRow(0);
        if(data.containsKey("lessons") && data.get("lessons") instanceof JSONArray && !((JSONArray) data.get("lessons")).isEmpty() && ((JSONArray) data.get("lessons")).get(0) instanceof JSONObject){
            JSONArray lesson_array = (JSONArray) data.get("lessons");
            TimetableLesson[] lessons = new TimetableLesson[lesson_array.size()];
            for(int i = 0; i < lesson_array.size(); i++){
                lessons[i] = TimetableLesson.parseFromJson((JSONObject)lesson_array.get(i));
            }
            row.lessons = lessons;
        }
        return row;
    }

    public JSONObject getJsonObject() {
        JSONObject data = new JSONObject();
        JSONArray lesson_array = new JSONArray();
        for(TimetableLesson lesson : lessons){
            lesson_array.add(lesson.getJsonObject());
        }
        data.put("lessons", lesson_array);
        return data;
    }
}
