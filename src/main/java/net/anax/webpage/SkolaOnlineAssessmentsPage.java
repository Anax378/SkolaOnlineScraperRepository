package net.anax.webpage;

import net.anax.browser.BrowserCookieCache;
import net.anax.data.assessment.AssessmentList;
import net.anax.scraper.RequestFailedException;

import java.util.HashMap;

public class SkolaOnlineAssessmentsPage extends AbstractSkolaOnlinePage{
    String html;
    boolean doLogs;
    AssessmentList assessmentList = null;
    public SkolaOnlineAssessmentsPage(BrowserCookieCache cache, HashMap<String, HiddenFormInput> hiddenFormInputs){
        this.cookieCache = cache;
        this.hiddenFormInputs = hiddenFormInputs;
    }
    public void addHtml(String html){
        this.html = html;
    }

    public AssessmentList getAssessmentList() throws RequestFailedException {
        if(assessmentList == null){
            assessmentList = AssessmentList.parseFromHTML(html);
        }
        return assessmentList;
    }
}
