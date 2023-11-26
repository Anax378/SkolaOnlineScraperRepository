package net.anax.data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class LessonRow {
    public TimetableLesson[] lessons;

    public LessonRow(int lessonCount){
        this.lessons = new TimetableLesson[lessonCount];
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
