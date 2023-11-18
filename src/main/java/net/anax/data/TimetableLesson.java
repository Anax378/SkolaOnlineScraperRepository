package net.anax.data;

public class TimetableLesson {

    public static final TimetableLesson EMPTY_LESSON = getBlankLesson();
    public String subjectShortcut;
    public String subjectFullName;
    public String groupShortcut;
    public String classroomShortcut;
    public FurtherInfoElement[] furtherInfo;
    public TimetableLessonType type;

    public static TimetableLesson getBlankLesson(){
        TimetableLesson lesson = new TimetableLesson();
        lesson.subjectShortcut = "";
        lesson.subjectFullName = "";
        lesson.groupShortcut = "";
        lesson.classroomShortcut = "";
        lesson.furtherInfo = new FurtherInfoElement[0];
        lesson.type = TimetableLessonType.REGULAR;
        return lesson;
    }
}
