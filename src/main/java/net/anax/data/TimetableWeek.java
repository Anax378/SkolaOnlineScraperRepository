package net.anax.data;

public class TimetableWeek {

    public TimetableDay[] days;
    public String[] lessonTimeIntervals;

    public TimetableWeek(int maxLessons, int daysDisplayed){
        lessonTimeIntervals = new String[maxLessons];
        days = new TimetableDay[daysDisplayed];
    }

    public void printSelf(){
        for(int i = 0; i < days.length; i++){
            if(days[i] == null){
                System.out.println(i + " is null");
            }
            for(int j = 0; j < days[i].lessonRows[0].lessons.length; j++){
                String shortcut = days[i].lessonRows[0].lessons[j].subjectShortcut;
                System.out.print("| " + pad(5, shortcut) + " |");
            }
            System.out.println();
        }

        for(TimetableDay day : days){
            System.out.println("===============" + day.dayOfWeek + " " + day.date + "============");
            for(LessonRow row : day.lessonRows){
                for(TimetableLesson lesson : row.lessons){
                    if(lesson == TimetableLesson.EMPTY_LESSON){continue;}
                    System.out.println(lesson.subjectShortcut + " --------------------------");
                    System.out.println("What: " + lesson.subjectFullName);
                    System.out.println("Who: " + lesson.groupShortcut);
                    System.out.println("Where: " + lesson.classroomShortcut);
                    System.out.println("Type: " + lesson.type.name());

                    for(FurtherInfoElement element : lesson.furtherInfo){
                        System.out.println(element.name + ": " + element.value);
                    }
                }
            }
        }
    }

    private String pad(int finalLength, String string){
        return string + " ".repeat(Math.max(0, finalLength-string.length()));
    }

}
