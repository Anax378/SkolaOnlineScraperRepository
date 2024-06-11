package net.anax.skolaOnlineScraper.webpage;

import net.anax.skolaOnlineScraper.browser.BrowserCookieCache;
import net.anax.skolaOnlineScraper.data.assessment.AssessmentList;
import net.anax.skolaOnlineScraper.scraper.RequestFailedException;

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
