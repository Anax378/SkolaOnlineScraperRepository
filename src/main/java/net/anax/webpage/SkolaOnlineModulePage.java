package net.anax.webpage;

import net.anax.browser.BrowserCookieCache;
import net.anax.http.HttpCookie;
import net.anax.http.HttpMethod;
import net.anax.http.HttpRequest;
import net.anax.http.HttpResponse;
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
        HttpRequest request = new HttpRequest(new URL("https://aplikace.skolaonline.cz/SOL/App/Kalendar/KZK001_KalendarTyden.aspx"), HttpMethod.GET);
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

}
