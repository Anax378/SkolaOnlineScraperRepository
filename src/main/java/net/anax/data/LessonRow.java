package net.anax.data;

public class LessonRow {
    public TimetableLesson[] lessons;

    public LessonRow(int lessonCount){
        this.lessons = new TimetableLesson[lessonCount];
    }
}
