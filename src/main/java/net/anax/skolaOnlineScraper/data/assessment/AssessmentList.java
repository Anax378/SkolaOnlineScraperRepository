package net.anax.skolaOnlineScraper.data.assessment;

import net.anax.skolaOnlineScraper.data.InvalidDataInJsonException;
import net.anax.skolaOnlineScraper.scraper.RequestFailedException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AssessmentList {
    Assessment[] assessments;
    AssessmentList(int assessment_count){
        assessments = new Assessment[assessment_count];
    }

    public static AssessmentList parseFromHTML(String html) throws RequestFailedException {

        Document doc = Jsoup.parse(html);
        Element assessment_table = doc.getElementById("G_ctl00xmainxgridHodnoceni");
        if(assessment_table == null){throw new RequestFailedException("assessments not found in html");}

        Element tableBody = assessment_table.getElementsByTag("tbody").first();
        if(tableBody == null){throw new RequestFailedException("assessments not found in html");}

        Elements assessment_rows = tableBody.getElementsByTag("tr");

        int assessment_count = assessment_rows.size();
        AssessmentList assessmentList = new AssessmentList(assessment_count);

        for(int i = 0; i < assessment_count; i++){
            Element assessment_element = assessment_rows.get(i);
            Assessment assessment = Assessment.parseFromRowElement(assessment_element);
            assessmentList.assessments[i] = assessment;
        }

        return assessmentList;

    }

    public JSONObject getJSONObject(){
        JSONObject data = new JSONObject();
        JSONArray array = new JSONArray();
        for (Assessment assessment : assessments){
            array.add(assessment.getJsonObject());
        }
        data.put("assessments", array);
        return data;
    }

    public AssessmentList parseFromJsonString(JSONObject data) throws ParseException, InvalidDataInJsonException {

        if(!data.containsKey("assessments") || !(data.get("assessments") instanceof JSONArray)){
            throw new InvalidDataInJsonException("necessary data missing");
        }
        JSONArray array = (JSONArray) data.get("assessments");
        AssessmentList assessmentList = new AssessmentList(array.size());

        for(int i = 0; i < array.size(); i++){
            assessmentList.assessments[i] = Assessment.parseFromJson((JSONObject) array.get(i));
        }

        return assessmentList;
    }

    public void printSelf(){
        int maxDateLength = 0;
        int maxSubjectLength = 0;
        int maxThemeLength = 0;
        int maxWeightLength = 0;
        int maxResultLength = 0;
        int maxVerbalAssessment = 0;

        for(Assessment assessment : assessments){
            if(assessment.date.length() > maxDateLength){maxDateLength = assessment.date.length();}
            if(assessment.subject.length() > maxSubjectLength){maxSubjectLength = assessment.subject.length();}
            if(assessment.theme.length() > maxThemeLength){maxThemeLength = assessment.theme.length();}
            if(assessment.weight.length() > maxWeightLength){maxWeightLength = assessment.weight.length();}
            if(assessment.result.length() > maxResultLength){maxResultLength = assessment.result.length();}
            if(assessment.verbalAssessment.length() > maxVerbalAssessment){maxVerbalAssessment = assessment.verbalAssessment.length();}
        }

        for(Assessment assessment : assessments){
            assessment.printSelf(maxDateLength, maxSubjectLength, maxThemeLength, maxWeightLength, maxResultLength, maxVerbalAssessment);
        }
    }
}
