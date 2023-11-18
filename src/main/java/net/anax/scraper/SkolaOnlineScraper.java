package net.anax.scraper;

import net.anax.config.Configuration;
import net.anax.config.ConfigurationManager;
import net.anax.data.*;
import net.anax.http.HttpCookie;
import net.anax.http.HttpMethod;
import net.anax.logging.Logger;
import net.anax.util.StringUtilities;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

public class SkolaOnlineScraper {
    private final String base_url = "https://www.skolaonline.cz";

    private String username;
    private String password;
    private HashMap<String, HttpCookie> cookies = new HashMap<>();
    private Configuration configuration;

    public SkolaOnlineScraper(String username, String password) throws IOException {
        this.username = username;
        this.password = password;

        configuration = ConfigurationManager.getInstance().getConfiguration();
    }

    public void login() throws IOException, RequestFailedException {
        URL url = new URL("https://aplikace.skolaonline.cz/SOL/Prihlaseni.aspx");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(HttpMethod.POST.getName());

        connection.setInstanceFollowRedirects(false);

        String boundary = "---------------------------141200824129006017161243354094";

        String body = constructLoginRequestBody(boundary);

        connection.setRequestProperty("Host", "aplikace.skolaonline.cz");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/119.0");
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Accept-Encoding", "deflate");
        connection.setRequestProperty("Referer", "https://www.skolaonline.cz/");
        connection.setRequestProperty("Content-Length", String.valueOf(body.length()));
        connection.setRequestProperty("Origin", "https://www.skolaonline.cz");
        connection.setRequestProperty("DNT", "1");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
        connection.setRequestProperty("Sec-Fetch-Dest", "document");
        connection.setRequestProperty("Sec-Fetch-Mode", "navigate");
        connection.setRequestProperty("Sec-Fetch-Site", "same-origin");
        connection.setRequestProperty("Sec-Fetch-User", "?1");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setRequestProperty("Pragma", "no-cache");
        connection.setRequestProperty("Cache-Control", "no-cache");

        connection.setDoOutput(true);

        DataOutputStream os = new DataOutputStream(connection.getOutputStream());
        os.writeBytes(body);

        int responseCode = connection.getResponseCode();

        for (int i = 0; ; i++) {
            String headerName = connection.getHeaderFieldKey(i);
            String headerValue = connection.getHeaderField(i);

            if (headerName == null && headerValue == null) {
                break;
            }

            if(headerName != null){
                if(headerName.equals("set-cookie")){
                    String[] cookiesToSet = headerValue.split("; ");

                    String[] cookie_name_and_value = cookiesToSet[0].split("=");

                    HttpCookie cookie = new HttpCookie();
                    cookie.name = cookie_name_and_value[0];
                    cookie.value = cookie_name_and_value[1];

                    for(int j = 1; j < cookiesToSet.length; j++){
                        String[] property = cookiesToSet[j].split("=");
                        if(property.length > 1){
                            cookie.properties.put(property[0], property[1]);
                        }else if (property.length == 1){
                            cookie.properties.put(property[0], "true");
                        }
                    }
                    cookies.put(cookie.name, cookie);
                }
            }

            Logger.LOG((headerName != null ? headerName + ": " : "") + headerValue);
        }

        Logger.LOG("-------Cookies-----");
        for(HttpCookie cookie : cookies.values()){
            Logger.LOG(cookie.name + ": " + cookie.value);
            for(String key : cookie.properties.keySet()){
                Logger.LOG("\t" + key + ": " + cookie.properties.get(key));
            }
        }

        if(cookies.get(".ASPXAUTH") == null ||
                cookies.get("ASP.NET_SessionId") == null ||
                cookies.get("ZPUSOB_OVERENI") == null ||
                cookies.get("SERVERID") == null

        ){
            throw new RequestFailedException("did not receive necessary cookies, response code " + responseCode);
        }

        connection.disconnect();

    }
    private String constructLoginRequestBody(String boundary){
        String boundary_delimiter = "--" + boundary;
        String body =
"""
"""+boundary_delimiter + "\n" +"""
Content-Disposition: form-data; name="__EVENTTARGET"
                                                
dnn$ctr994$SOLLogin$btnODeslat
"""+boundary_delimiter + "\n" +"""
Content-Disposition: form-data; name="__EVENTARGUMENT"
                
                
"""+boundary_delimiter + "\n" +"""
Content-Disposition: form-data; name="__VIEWSTATE"
                
                
"""+boundary_delimiter + "\n" +"""
Content-Disposition: form-data; name="__VIEWSTATEGENERATOR"
                
                
"""+boundary_delimiter + "\n" +"""
Content-Disposition: form-data; name="__VIEWSTATEENCRYPTED"
                
                
"""+boundary_delimiter + "\n" +"""
Content-Disposition: form-data; name="__PREVIOUSPAGE"
                
                
"""+boundary_delimiter + "\n" +"""
Content-Disposition: form-data; name="__EVENTVALIDATION"
                
                
"""+boundary_delimiter + "\n" +"""
Content-Disposition: form-data; name="dnn$dnnSearch$txtSearch"
                
                
"""+boundary_delimiter + "\n" +"""
Content-Disposition: form-data; name="JmenoUzivatele"
                
""" + username + "\n" + """
"""+boundary_delimiter + "\n" +"""
Content-Disposition: form-data; name="HesloUzivatele"
                
""" + password + "\n" +"""
"""+boundary_delimiter + "\n" +"""
Content-Disposition: form-data; name="ScrollTop"
                
                
"""+boundary_delimiter + "\n" +"""
Content-Disposition: form-data; name="__dnnVariable"
                
                
"""+boundary_delimiter + "\n" +"""
Content-Disposition: form-data; name="__RequestVerificationToken"
                
                
"""+boundary_delimiter + "--\n";
        return body.replace("\n", "\r\n");
    }
    public TimetableWeek getTimeTable() throws IOException, RequestFailedException, NotLoggedInException {
        URL url = new URL("https://aplikace.skolaonline.cz/SOL/App/Kalendar/KZK001_KalendarTyden.aspx");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(HttpMethod.GET.getName());
        connection.setInstanceFollowRedirects(false);

        String cookie = constructAuthCookieHeaderValue();

        connection.setRequestProperty("Host", "aplikace.skolaonline.cz");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/119.0");
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("DNT", "1");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Referer", "https://aplikace.skolaonline.cz/sol/App/Spolecne/KZZ010_RychlyPrehled.aspx");
        connection.setRequestProperty("Cookie", cookie);
        connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
        connection.setRequestProperty("Sec-Fetch-Dest", "document");
        connection.setRequestProperty("Sec-Fetch-Mode", "navigate");
        connection.setRequestProperty("Sec-Fetch-Site", "same-origin");
        connection.setRequestProperty("Sec-Fetch-User", "?1");

        int responseCode = connection.getResponseCode();

        if(connection.getInputStream() == null){
            throw new RequestFailedException("Input Stream Not Received, response code " + responseCode);
        }

        InputStream is = new GZIPInputStream(connection.getInputStream());
        String line;

        StringBuilder html = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        while((line = reader.readLine()) != null){
            html.append(line + "\n");
        }

        connection.disconnect();

        return getTimeTableFromHTML(html.toString());
    }
    private String constructAuthCookieHeaderValue() throws NotLoggedInException {
        String cookie;
        HttpCookie ASP_NET_SessionId = cookies.get("ASP.NET_SessionId");
        HttpCookie SERVERID = cookies.get("SERVERID");
        HttpCookie ZPUSOB_OVERENI = cookies.get("ZPUSOB_OVERENI");
        HttpCookie ASPXAUTH = cookies.get(".ASPXAUTH");

        if(ASP_NET_SessionId == null || SERVERID == null || ZPUSOB_OVERENI == null || ASPXAUTH == null){
            throw new NotLoggedInException("Necessary Cookies Not Found");
        }

        LocalDateTime time = LocalDateTime.now().plusMinutes(30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        HttpCookie SESSION_EXPIRES = new HttpCookie("SESSION_EXPIRES", time.format(formatter));
        HttpCookie cookieconsent_sol = new HttpCookie("cookieconsent_sol", "{\"level\":[\"necessary\"],\"revision\":0,\"data\":null,\"rfc_cookie\":false}");

        cookie = ASP_NET_SessionId.name + "=" + ASP_NET_SessionId.value + "; "
        + SERVERID.name + "=" + SERVERID.value + "; "
        + ZPUSOB_OVERENI.name + "=" + ZPUSOB_OVERENI.value + "; "
        + ASPXAUTH.name + "=" + ASPXAUTH.value + "; "
        + SESSION_EXPIRES + "=" + SESSION_EXPIRES.value + "; "
        + cookieconsent_sol.name + "=" + cookieconsent_sol.value;

        return  cookie;
    }
    private TimetableWeek getTimeTableFromHTML(String html) throws RequestFailedException {
        Document doc = Jsoup.parse(html);

        Element timeTableTable = doc.select("table.DctTable").first();
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
        int dayIndex = 0;
        ;System.out.println("Rows: " + allRows.size());
        ;System.out.println("lesson count: " + lessonCount);
        for (int i = 1; i < allRows.size(); i++){
            Element th = allRows.get(i).select("> th").first();
            if(th == null){continue;}

            String rowspanAttribute = th.attr("rowspan");
            int rowspan;

            if(!StringUtilities.isInteger(rowspanAttribute)){rowspan = 1;}
            else{rowspan = Integer.parseInt(rowspanAttribute);}

            TimetableDay day = new TimetableDay(rowspan);
            for(int r = 0; r < rowspan; r++){
                LessonRow row = new LessonRow(lessonCount);
                int cellCount = lessonCount;
                int offset = 0;
                for(int l = 0; l < cellCount; l++){
                    Element td = allRows.get(i+r).select("> td").get(l);
                    String colspanString = td.attr("colspan");
                    int colspan = 1;
                    if(StringUtilities.isInteger(colspanString)){
                        colspan = Integer.parseInt(colspanString);
                    }
                    cellCount -= (colspan - 1);
                    TimetableLesson lesson = getLessonFromTd(allRows.get(i+r).select("> td").get(l));
                    for(int c = 0; c < colspan; c++){
                        row.lessons[l+c+offset] = lesson;
                    }
                    offset += (colspan-1);
                }
                day.lessonRows[r] = row;
            }
            timetable.days[dayIndex] = day;
            dayIndex++;
        }

        return timetable;

    }

    private TimetableLesson getLessonFromTd(Element td){
        Element innerTd = td.select("> table > tbody > tr > td").first();
        if(innerTd == null){return TimetableLesson.EMPTY_LESSON;}

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
        }
        return lesson;
    }

}
