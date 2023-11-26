package net.anax.data;

import net.anax.scraper.RequestFailedException;
import net.anax.util.StringUtilities;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;

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
                    for(TimetableAssessment assessment : lesson.assessments){
                        System.out.println("Assessment " + assessment.subject);
                        for(FurtherInfoElement element : assessment.furtherInfo){
                            System.out.println("\t" + element.name + " " + element.value);
                        }
                    }
                }
            }
        }

        for(String timeInterval : lessonTimeIntervals){
            System.out.println(timeInterval);
        }
    }
    private String pad(int finalLength, String string){
        return string + " ".repeat(Math.max(0, finalLength-string.length()));
    }
    public static TimetableWeek getTimeTableFromHTML(String html) throws RequestFailedException {
        Document doc = Jsoup.parse(html);

        Element timeTableTable = doc.getElementById("CCADynamicCalendarTable");
        if(timeTableTable == null){throw new RequestFailedException("time table not found in html");}

        Element tbody = timeTableTable.select("> tbody").first();
        if(tbody == null){throw new RequestFailedException("time table not found in html");}

        Element infoRow = tbody.select("> tr").first();
        if(infoRow == null){throw new RequestFailedException("time table not in correct format");}

        int lessonCount = infoRow.select("> th").size() -1;
        if(lessonCount < 0){throw new RequestFailedException("time table not in correct format");}

        Elements allRows = tbody.select("> tr");
        int daysDisplayed = 0;
        for(int i = 1; i < allRows.size(); i++){
            if(!allRows.get(i).select("> th").isEmpty()){
                daysDisplayed++;
            }
        }

        TimetableWeek timetable = new TimetableWeek(lessonCount, daysDisplayed);

        timetable.lessonTimeIntervals = getLessonTimeIntervals(allRows.get(0));

        int dayIndex = 0;
        ;System.out.println("Rows: " + allRows.size());
        ;System.out.println("lesson count: " + lessonCount);

        for (int rowIndex = 1; rowIndex < allRows.size(); rowIndex++){
            Element th = allRows.get(rowIndex).select("> th").first();
            if(th == null){continue;}
            Elements dayDateInfo = th.select(" > table > tbody > tr");


            String rowspanAttribute = th.attr("rowspan");
            int rowspan;

            if(!StringUtilities.isInteger(rowspanAttribute)){rowspan = 1;}
            else{rowspan = Integer.parseInt(rowspanAttribute);}

            TimetableDay day = new TimetableDay(rowspan);

            if(!dayDateInfo.isEmpty()){
                Element dayOfWeekElement = dayDateInfo.get(0).select(" > td").first();
                Element dateElement = dayDateInfo.get(1).select(" > td").first();
                if(dayOfWeekElement != null){
                    String dayOfWeek = dayOfWeekElement.text();
                    if(dayOfWeek != null){
                        day.dayOfWeek = dayOfWeek;
                    }
                }
                if(dateElement != null){
                    String date = dateElement.text();
                    if(date != null){
                        day.date = date;
                    }
                }
            }

            for(int rowInDayIndex = 0; rowInDayIndex < rowspan; rowInDayIndex++){
                LessonRow row = new LessonRow(lessonCount);
                int cellCount = lessonCount;
                int offset = 0;
                for(int lessonIndex = 0; lessonIndex < cellCount; lessonIndex++){
                    Element td = allRows.get(rowIndex+rowInDayIndex).select("> td").get(lessonIndex);
                    String colspanString = td.attr("colspan");
                    int colspan = 1;
                    if(StringUtilities.isInteger(colspanString)){
                        colspan = Integer.parseInt(colspanString);
                    }
                    cellCount -= (colspan - 1);
                    if(TimetableAssessment.isAssessment(td)){
                        TimetableAssessment assessment = TimetableAssessment.parseAssessment(td);
                        day.lessonRows[rowInDayIndex-1].lessons[lessonIndex].assessments = new TimetableAssessment[]{assessment};
                        row.lessons[lessonIndex+offset] = TimetableLesson.EMPTY_LESSON;

                    }else{
                        Element cell = allRows.get(rowIndex + rowInDayIndex).select("> td").get(lessonIndex);
                        TimetableLesson lesson = TimetableLesson.parseLessonFromTd(cell);
                        for(int c = 0; c < colspan; c++){
                            row.lessons[lessonIndex+c+offset] = lesson;
                        }
                    }
                    offset += (colspan-1);
                }
                day.lessonRows[rowInDayIndex] = row;
            }
            timetable.days[dayIndex] = day;
            dayIndex++;
        }

        return timetable;

    }
    private static String[] getLessonTimeIntervals(Element tr){
        if(tr == null){return new String[0];}
        Elements ths = tr.select("> th");

        int lessons = ths.size()-1;
        String[] lessonTimeIntervals = new String[lessons];

        for(int i = 1; i < ths.size(); i++){
            lessonTimeIntervals[i-1] = "";
            Elements trs = ths.get(i).select(" > table > tbody > tr");
            if(trs.size() > 1){
                String timeInterval = trs.get(1).text();
                if(timeInterval != null){
                    lessonTimeIntervals[i-1] = timeInterval;
                }
            }
        }
        return lessonTimeIntervals;
    }
    public JSONObject getJsonObject(){
        JSONObject data = new JSONObject();

        JSONArray day_array = new JSONArray();
        JSONArray time_interval_array = new JSONArray();

        for(TimetableDay day : days){
            day_array.add(day.getJsonObject());
        }

        time_interval_array.addAll(Arrays.asList(lessonTimeIntervals));
        data.put("lessonTimeIntervals", time_interval_array);
        data.put("days", day_array);
        return data;
    }

}
