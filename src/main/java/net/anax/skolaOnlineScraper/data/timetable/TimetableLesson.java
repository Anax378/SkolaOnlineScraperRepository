package net.anax.skolaOnlineScraper.data.timetable;

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
    public ETimetableLessonType type;
    public TimetableAssessment[] assessments;
    public static TimetableLesson getBlankLesson(){
        TimetableLesson lesson = new TimetableLesson();
        lesson.subjectShortcut = "";
        lesson.subjectFullName = "";
        lesson.groupShortcut = "";
        lesson.classroomShortcut = "";
        lesson.furtherInfo = new FurtherInfoElement[0];
        lesson.type = ETimetableLessonType.REGULAR;
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
            for(ETimetableLessonType type : ETimetableLessonType.values()){
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

    public static TimetableLesson parseFromJson(JSONObject data) {
        if(data.containsKey("isEmpty")){
            return EMPTY_LESSON;
        }
        TimetableLesson lesson = getBlankLesson();

        if(data.containsKey("subjectShortcut") && data.get("subjectShortcut") instanceof String) {
            lesson.subjectShortcut = (String) data.get("subjectShortcut");
        }
        if(data.containsKey("subjectFullName") && data.get("subjectFullName") instanceof String) {
            lesson.subjectFullName = (String) data.get("subjectFullName");
        }
        if(data.containsKey("groupShortcut") && data.get("groupShortcut") instanceof String) {
            lesson.groupShortcut = (String) data.get("groupShortcut");
        }
        if(data.containsKey("classroomShortcut") && data.get("classroomShortcut") instanceof String) {
            lesson.classroomShortcut = (String) data.get("classroomShortcut");
        }

        if(data.containsKey("type") && data.get("type") instanceof String){
            String type_name = (String) data.get("type");
            for(ETimetableLessonType type : ETimetableLessonType.values()){
                if(type.name().equals(type_name)){
                    lesson.type = type;
                    break;
                }
            }
        }

        if(data.containsKey("assessments") && data.get("assessments") instanceof JSONArray && !((JSONArray) data.get("assessments")).isEmpty() && ((JSONArray) data.get("assessments")).get(0) instanceof JSONObject){
            JSONArray assessment_array = (JSONArray) data.get("assessments");
            TimetableAssessment[] assessments = new TimetableAssessment[assessment_array.size()];
            for(int i = 0; i < assessments.length; i++){
                assessments[i] = TimetableAssessment.parseFromJson((JSONObject) assessment_array.get(i));
            }
            lesson.assessments = assessments;
        }

        if(data.containsKey("furtherInfo") && data.get("furtherInfo") instanceof JSONArray && !((JSONArray) data.get("furtherInfo")).isEmpty() && ((JSONArray) data.get("furtherInfo")).get(0) instanceof JSONObject){
            JSONArray info_array = (JSONArray) data.get("furtherInfo");
            FurtherInfoElement[] elements = new FurtherInfoElement[info_array.size()];
            for(int i = 0; i < elements.length; i++){
                elements[i] = FurtherInfoElement.parseFromJson((JSONObject)info_array.get(i));
            }
            lesson.furtherInfo = elements;
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
        data.put("type", type.name());

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
