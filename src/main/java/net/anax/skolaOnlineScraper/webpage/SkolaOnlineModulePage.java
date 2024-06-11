package net.anax.skolaOnlineScraper.webpage;

import net.anax.skolaOnlineScraper.browser.BrowserCookieCache;
import net.anax.skolaOnlineScraper.http.HttpCookie;
import net.anax.skolaOnlineScraper.http.EHttpMethod;
import net.anax.skolaOnlineScraper.http.HttpRequest;
import net.anax.skolaOnlineScraper.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class SkolaOnlineModulePage extends AbstractSkolaOnlinePage{
    boolean doLogs;

    public static final String loginUrl = "https://aplikace.skolaonline.cz/SOL/Prihlaseni.aspx";
    SkolaOnlineModulePage(){
        this.cookieCache = new BrowserCookieCache();
        this.hiddenFormInputs = new HashMap<>();
    }
    SkolaOnlineModulePage(BrowserCookieCache cache, HashMap<String, HiddenFormInput> hiddenFormInputs){
        this.cookieCache = cache;
        this.hiddenFormInputs = hiddenFormInputs;
    }

    /**
     * returns a new SkolaOnlineTimetablePage. the date on this page is not guaranteed to be any specific value.
     * @exception IOException in case the connection fails.
     * @return returns a new SkolaOnlineTimetablePage, this page may be broken,
     * which cannot be confirmed until an attempt to extract the timetable is made.
     */
    public SkolaOnlineTimetablePage goToTimetable() throws IOException {
        if(doLogs){
            System.out.println("==================================START OF goToTimetable() log==============================");
        }
        HttpRequest request = new HttpRequest(new URL("https://aplikace.skolaonline.cz/SOL/App/Kalendar/KZK001_KalendarTyden.aspx"), EHttpMethod.GET);
        addCommonHeadersToRequest(request);
        request.setHeader("Referrer", "https://aplikace.skolaonline.cz/SOL/App/Spolecne/KZZ010_RychlyPrehled.aspx");

        request.addCookie(this.cookieCache.getCookie("ASP.NET_SessionId"));
        request.addCookie(this.cookieCache.getCookie("ZPUSOB_OVERENI"));
        request.addCookie(this.cookieCache.getCookie(".ASPXAUTH"));
        request.addCookie(this.cookieCache.getCookie("SERVERID"));
        request.addCookie(this.cookieCache.getCookie("SESSION_EXPIRES"));
        request.addCookie(new HttpCookie("cookieconsent_sol", "{\"level\":[\"necessary\"],\"revision\":0,\"data\":null,\"rfc_cookie\":false}"));


        if(doLogs){
            request.printSelf();
        }

        HttpResponse response = request.send();

        if(doLogs){
            response.printSelf();
        }

        SkolaOnlineTimetablePage timetablePage = new SkolaOnlineTimetablePage(this.cookieCache, this.hiddenFormInputs).addHtml(response.getBody());
        timetablePage.doLogs = doLogs;

        Document doc = Jsoup.parse(response.getBody());

        updateHiddenFormInputs(doc);
        response.addCookiesToCache(this.cookieCache);

        if(doLogs){
            System.out.println("-------------------goToTimetable() hidden inputs start-------------------");
            for(String input : hiddenFormInputs.keySet()){
                System.out.println(input + ": " + hiddenFormInputs.get(input).value);
            }
            System.out.println("-------------------goToTimetable() hidden inputs end-------------------");
        }
        if(doLogs){
            System.out.println("==================================END OF goToTimetable() log==============================");
        }
        response.disconnect();
        return timetablePage;
    }

    public SkolaOnlineAssessmentsPage goToAssessments() throws IOException {

        if(doLogs){
            System.out.println("========================= goToAssessments() start =====================");
        }

        HttpRequest request = new HttpRequest(new URL("https://aplikace.skolaonline.cz/SOL/App/Hodnoceni/KZH003_PrubezneHodnoceni.aspx"), EHttpMethod.GET);
        addCommonHeadersToRequest(request);
        request.setHeader("Referrer", "https://aplikace.skolaonline.cz/SOL/App/Spolecne/KZZ010_RychlyPrehled.aspx");
        request.addCookie(this.cookieCache.getCookie("ASP.NET_SessionId"));
        request.addCookie(this.cookieCache.getCookie("ZPUSOB_OVERENI"));
        request.addCookie(this.cookieCache.getCookie("SERVERID"));
        request.addCookie(this.cookieCache.getCookie(".ASPXAUTH"));
        request.addCookie(new HttpCookie("cookieconsent_sol", "{\"level\":[\"necessary\"],\"revision\":0,\"data\":null,\"rfc_cookie\":false}"));

        if(doLogs){request.printSelf();}

        HttpResponse response = request.send();

        if(doLogs){response.printSelf();}

        SkolaOnlineAssessmentsPage assessmentsPage = new SkolaOnlineAssessmentsPage(this.cookieCache, this.hiddenFormInputs);
        assessmentsPage.addHtml(response.getBody());
        assessmentsPage.doLogs = doLogs;

        response.addCookiesToCache(this.cookieCache);

        Document doc = Jsoup.parse(response.getBody());
        updateHiddenFormInputs(doc);

        if(doLogs){
            System.out.println("-------------------goToAssessments() hidden inputs start-------------------");
            for(String input : hiddenFormInputs.keySet()){
                System.out.println(input + ": " + hiddenFormInputs.get(input).value);
            }
            System.out.println("-------------------goToAssessments() hidden inputs end-------------------");
        }

        if(doLogs){
            System.out.println("========================= goToAssessments() end =====================");
        }

        return assessmentsPage;
    }



}
