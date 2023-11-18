package net.anax.data;

public class TimetableDay {
    public LessonRow[] lessonRows;
    public String date;
    public String dayOfWeek;

    public TimetableDay(int rowCount){
        this.lessonRows = new LessonRow[rowCount];
    }
}
