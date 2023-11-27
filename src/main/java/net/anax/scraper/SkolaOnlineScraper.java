package net.anax.scraper;

import net.anax.data.*;
import net.anax.http.HttpCookie;
import net.anax.http.HttpMethod;
import net.anax.logging.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

public class SkolaOnlineScraper {

    private final String username;
    private final String password;
    private final HashMap<String, HttpCookie> cookies = new HashMap<>();

    public SkolaOnlineScraper(String username, String password){
        this.username = username;
        this.password = password;
    }

    public boolean login(){
        try{
            login(false);
        }catch(RequestFailedException | IOException e){
            e.printStackTrace();
            return false;
        }
        catch(Exception e){
            System.out.println("unexpected exception");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean login(boolean safe) throws IOException, RequestFailedException {
        if(safe){return login();}

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
                    String[] cookiesToSet = headerValue.split("; ?");

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

        return true;

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
            html.append(line).append("\n");
        }

        for(int i = 0; ; i++){
            String name = connection.getHeaderFieldKey(i);
            String value = connection.getHeaderField(i);
            if(name == null && value == null){break;}

            if(Objects.equals(name, "set-cookie")){
                if(value != null){
                    String[] cookiesToSet = value.split(";");
                    for(String cookieToSet : cookiesToSet){
                        String[] header_and_value = cookieToSet.split("=");
                        HttpCookie httpCookie = new HttpCookie(header_and_value[0], header_and_value.length > 1 ? header_and_value[1] : "true");
                        this.cookies.put(httpCookie.name, httpCookie);
                    }
                }
            }
        }

        connection.disconnect();
        TimetableWeek week;
        try {
            week = TimetableWeek.getTimeTableFromHTML(html.toString());
            return week;
        }
        catch(Exception e){
            e.printStackTrace();
            throw new RequestFailedException("a unexpected exception occurred");
        }
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

}
