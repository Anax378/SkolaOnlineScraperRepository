package net.anax.data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TimetableLesson {

    public static final TimetableLesson EMPTY_LESSON = getBlankLesson();
    public String subjectShortcut;
    public String subjectFullName;
    public String groupShortcut;
    public String classroomShortcut;
    public FurtherInfoElement[] furtherInfo;
    public TimetableLessonType type;
    public TimetableAssessment[] assessments;

    public static TimetableLesson getBlankLesson(){
        TimetableLesson lesson = new TimetableLesson();
        lesson.subjectShortcut = "";
        lesson.subjectFullName = "";
        lesson.groupShortcut = "";
        lesson.classroomShortcut = "";
        lesson.furtherInfo = new FurtherInfoElement[0];
        lesson.type = TimetableLessonType.REGULAR;
        lesson.assessments = new TimetableAssessment[0];
        return lesson;
    }
    static TimetableLesson parseLessonFromTd(Element td) {
        Element innerTd = td.select("> table > tbody > tr > td").first();
        if(innerTd == null){return TimetableLesson.EMPTY_LESSON;}
        if(!innerTd.select("> img").isEmpty()){return TimetableLesson.EMPTY_LESSON;}

        TimetableLesson lesson = TimetableLesson.getBlankLesson();

        Elements spans = innerTd.select("> span");

        String innerTdClass = innerTd.className();
        if(innerTdClass != null){
            for(TimetableLessonType type : TimetableLessonType.values()){
                if(type.identifier.equals(innerTdClass)){
                    lesson.type = type;
                }
            }
        }


        if(!spans.isEmpty()){
            String innerText = spans.first().text();
            lesson.subjectShortcut = (innerText == null) ? "" : innerText;
        }
        if(spans.size() > 1){
            String innerText = spans.get(1).text();
            if(innerText != null){
                String[] info = innerText.split(" ");
                lesson.groupShortcut = info[0];
                if(info.length > 1){
                    lesson.classroomShortcut = info[1];
                }
            }
        }

        String mouseover = innerTd.attr("onmouseover");
        if(mouseover != null){
            mouseover = mouseover.replace("onMouseOverTooltip('", "");
            if(mouseover.length() > 2){
                mouseover = mouseover.substring(0, mouseover.length()-2);
                String[] arguments = mouseover.split("' ?, ?'");
                lesson.subjectFullName = arguments[0];
                if(arguments.length > 1){
                    String[] furtherInfo = arguments[1].split("~");
                    FurtherInfoElement[] furtherInfoElements = new FurtherInfoElement[furtherInfo.length/2];
                    for(int i = 0; i+1 < furtherInfo.length; i+=2){
                        FurtherInfoElement element = new FurtherInfoElement(furtherInfo[i].replace(":", ""), furtherInfo[i+1]);
                        furtherInfoElements[i/2] = element;
                    }
                    lesson.furtherInfo = furtherInfoElements;
                }
            }else{
                ;System.out.println("faulty mouseover: " + mouseover + " innerTd: " + innerTd + "innderTd: " + innerTd.text());
            }
        }
        return lesson;
    }

    public JSONObject getJsonObject() {
        JSONObject data = new JSONObject();
        if(this == EMPTY_LESSON){
            data.put("isEmpty", 1);
            return data;
        }
        data.put("subjectShortcut", subjectShortcut);
        data.put("subjectFullName", subjectFullName);
        data.put("groupShortcut", groupShortcut);
        data.put("classroomShortcut", classroomShortcut);

        JSONArray info_array = new JSONArray();
        JSONArray assessment_array = new JSONArray();

        for(FurtherInfoElement element : furtherInfo){
            info_array.add(element.getJSonObject());
        }
        for(TimetableAssessment assessment : assessments){
            assessment_array.add(assessment.getJsonObject());
        }

        data.put("assessments", assessment_array);
        data.put("furtherInfo", info_array);

        return data;
    }
}
