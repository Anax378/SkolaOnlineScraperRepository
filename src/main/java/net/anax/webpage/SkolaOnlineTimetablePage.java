package net.anax.webpage;

import net.anax.browser.BrowserCookieCache;
import net.anax.data.TimetableWeek;
import net.anax.http.HttpCookie;
import net.anax.http.HttpMethod;
import net.anax.http.HttpRequest;
import net.anax.http.HttpResponse;
import net.anax.logging.Logger;
import net.anax.scraper.RequestFailedException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

public class SkolaOnlineTimetablePage extends AbstractSkolaOnlinePage{
    String html;
    TimetableWeek week = null;
    boolean doLogs;
    SkolaOnlineTimetablePage(){}
    SkolaOnlineTimetablePage(BrowserCookieCache cache, HashMap<String, HiddenFormInput> hiddenFromInputs){
        this.cookieCache = cache;
        this.hiddenFormInputs = hiddenFromInputs;
    }
    SkolaOnlineTimetablePage addHtml(String html){
        this.html = html;
        return this;
    }

    /**
     * extracts the timetable from the page.
     * @return returns a new TimetableWeek containing the data for the week contained in the page.
     * @exception RequestFailedException is thrown if the timetable is not contained in the page.
     * this is probably caused a change in the protocol SkolaOnline uses.
     */
    public TimetableWeek getTimetable() throws RequestFailedException {
        if(week == null){
            week = TimetableWeek.getTimeTableFromHTML(html);
        }
        return week;
    }

    /**
     * returns a new SkolaOnlineTimetablePage that has the timetable with the date provided.
     * @param date the datetime to which the timetable week contained will correspond.
     * @return returns a SkolaOnlineTimetablePage, the page can be broken,
     * which cannot be verified until an attempt to extract the timetable is made.
     * @exception IOException ios thrown in case of a failed connection.
     */
    public SkolaOnlineTimetablePage changeDateTo(LocalDate date) throws IOException {
        if(doLogs){
            System.out.println("==================START OF changeDateTo() LOG======================");
        }

        HttpRequest request = new HttpRequest(new URL("https://aplikace.skolaonline.cz/SOL/App/Kalendar/KZK001_KalendarTyden.aspx"), HttpMethod.POST);
        addCommonHeadersToRequest(request);
        request.setHeader("Referer", "https://aplikace.skolaonline.cz/SOL/App/Kalendar/KZK001_KalendarTyden.aspx");
        request.setHeader("Origin", "https://aplikace.skolaonline.cz");
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setBody(constructChangeDateRequestPayload(date));

        request.addCookie(cookieCache.getCookie("ASP.NET_SessionId"));
        request.addCookie(cookieCache.getCookie("SERVERID"));
        request.addCookie(cookieCache.getCookie("ZPUSOB_OVERENI"));
        request.addCookie(cookieCache.getCookie(".ASPXAUTH"));
        request.addCookie(cookieCache.getCookie("SESSION_EXPIRES"));
        request.addCookie(new HttpCookie("cookiesconsent_sol", "{\"level\":[\"necessary\"],\"revision\":0,\"data\":null,\"rfc_cookie\":false}"));

        if(doLogs){
            request.printSelf();
        }

        HttpResponse response = request.send();

        if(doLogs){
            response.printSelf();
        }

        Document doc = Jsoup.parse(response.getBody());
        updateHiddenFormInputs(doc);

        SkolaOnlineTimetablePage timetablePage = new SkolaOnlineTimetablePage(this.cookieCache, this.hiddenFormInputs).addHtml(response.getBody());
        timetablePage.doLogs = doLogs;

        response.addCookiesToCache(cookieCache);

        if(doLogs){
            System.out.println("-----------------------changeDateTo() hidden inputs start--------------------");
            for(String input : hiddenFormInputs.keySet()){
                System.out.println(input + ": " + hiddenFormInputs.get(input).value);
            }

            System.out.println("-----------------------changeDateTo() hidden inputs start--------------------");
            System.out.println("==================END OF changeDateTo() LOG======================");
        }
        response.disconnect();
        return timetablePage;
    }

    private String constructChangeDateRequestPayload(LocalDate date){
        String year = String.valueOf(date.getYear());
        String month = String.valueOf(date.getMonthValue());
        String day = String.valueOf(date.getDayOfMonth());

        String calendar_part = "%253Cx%2520PostData%253D%2522" + year + "x" + month + "x" + year + "x" + month + "x" + day + "x1%2522%253E%253C%2Fx%253E";

        UrlEncodedDataFormBuilder builder = new UrlEncodedDataFormBuilder();

        String __VIEWSTATE_SESSION_KEY = this.hiddenFormInputs.get("__VIEWSTATE_SESSION_KEY") == null ? "" : this.hiddenFormInputs.get("__VIEWSTATE_SESSION_KEY").value;
        String __EVENTVALIDATION = this.hiddenFormInputs.get("__EVENTVALIDATION") == null ? "" : this.hiddenFormInputs.get("__EVENTVALIDATION").value;

        builder.addField("__EVENTTARGET", "calendarPart%24kalendar");
        builder.addField("__EVENTARGUMENT", "");
        builder.addField("__LASTFOCUS", "");
        builder.addField("__VIEWSTATE_SESSION_KEY", __VIEWSTATE_SESSION_KEY);
        builder.addField("__VIEWSTATE", "");
        builder.addField("calendarPart_kalendar", calendar_part);
        builder.addField("calendarPart%24kalendar%24RBSelectionMode", "Week");
        builder.addField("CBZobrazitRozvrh", "on");
        builder.addField("CBZobrazitHodnoceni", "on");
        builder.addField("ctl16%24instantniNapovedaEditor%24rptNapoveda%24ctl01%24txtTitulek", "");
        builder.addField("ctl16%24instantniNapovedaEditor%24rptNapoveda%24ctl01%24txtNapoveda", "");
        builder.addField("ctl16%24instantniNapovedaEditor%24rptNapoveda%24ctl01%24hiddenDruhOrganizaceID", "KS");
        builder.addField("ctl16%24instantniNapovedaEditor%24rptNapoveda%24ctl02%24txtTitulek", "");
        builder.addField("ctl16%24instantniNapovedaEditor%24rptNapoveda%24ctl02%24txtNapoveda", "");
        builder.addField("ctl16%24instantniNapovedaEditor%24rptNapoveda%24ctl02%24hiddenDruhOrganizaceID", "VS");
        builder.addField("ctl16%24instantniNapovedaEditor%24rptNapoveda%24ctl03%24txtTitulek", "");
        builder.addField("ctl16%24instantniNapovedaEditor%24rptNapoveda%24ctl03%24txtNapoveda", "");
        builder.addField("ctl16%24instantniNapovedaEditor%24rptNapoveda%24ctl03%24hiddenDruhOrganizaceID", "ZR");
        builder.addField("ctl16%24instantniNapovedaEditor%24rptNapoveda%24ctl04%24txtTitulek", "");
        builder.addField("ctl16%24instantniNapovedaEditor%24rptNapoveda%24ctl04%24txtNapoveda", "");
        builder.addField("ctl16%24instantniNapovedaEditor%24rptNapoveda%24ctl04%24hiddenDruhOrganizaceID", "NS");
        builder.addField("ctl16%24instantniNapovedaEditor%24hiddenInstantniNapovedaPoleID", "");
        try {
            builder.addField("__EVENTVALIDATION", URLEncoder.encode(__EVENTVALIDATION, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Logger.LOG("failed to encode __EVENTVALIDATION in constructChangeDateRequestPayload()");
        }

        return builder.getData();
    }

}
