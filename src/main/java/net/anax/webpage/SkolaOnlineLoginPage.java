package net.anax.webpage;

import net.anax.browser.BrowserCookieCache;
import net.anax.http.HttpCookie;
import net.anax.http.HttpMethod;
import net.anax.http.HttpRequest;
import net.anax.http.HttpResponse;
import net.anax.logging.Logger;
import net.anax.scraper.RequestFailedException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class SkolaOnlineLoginPage extends AbstractSkolaOnlinePage{
    public static final String url = "https://www.skolaonline.cz/Aktuality.aspx";
    private boolean doLogs = false;
    private SkolaOnlineLoginPage(){
        this.cookieCache = new BrowserCookieCache();
        this.hiddenFormInputs = new HashMap<>();
    }

    /**
     * calls loadNew() with logs disabled
     */
    public static SkolaOnlineLoginPage loadNew() throws IOException {
        return loadNew(false);
    }

    /**
     * loads a new login page
     * @param doLogs specifies if the function should do logs. this argument is propagated to all subsequent pages.
     * @return returns a new login page from which you can than login into SkolaOnline
     * @exception IOException is thrown in case of a failed connection.
     */
    public static SkolaOnlineLoginPage loadNew(boolean doLogs) throws IOException {
        if(doLogs){System.out.println("===========================================START OF loadNew() LOG===========================================");}
        SkolaOnlineLoginPage loginPage = new SkolaOnlineLoginPage();
        loginPage.doLogs = doLogs;

        HttpRequest request = new HttpRequest(new URL(url), HttpMethod.GET);

        addCommonHeadersToRequest(request);

        HttpResponse response = request.send();

        if(doLogs){
            request.printSelf();
        }

        if(doLogs){
            response.printSelf();
        }

        response.addCookiesToCache(loginPage.cookieCache);
        String body = response.getBody();
        Document doc = Jsoup.parse(body);

        loginPage.updateHiddenFormInputs(doc);

        if(doLogs){
            System.out.println("-----------------------loadNew() extracted hidden input values start-----------------------");
            for(String hiddenInput : loginPage.hiddenFormInputs.keySet()){
                System.out.println(hiddenInput + ": " + loginPage.hiddenFormInputs.get(hiddenInput).value);
            }
            System.out.println("-----------------------loadNew() extracted hidden form values end-----------------------");
        }
        response.disconnect();
        if(doLogs){System.out.println("===========================================END OF loadNew() LOG===========================================");}
        return loginPage;
    }

    /**
     * logs in to SkolaOnline
     * @param username the username with which to log in.
     * @param password the password with which to log in.
     * @return returns a new SkolaOnlineModulePage.
     * @exception IOException is thrown in case of failed connection.
     * @exception RequestFailedException is thrown if the response did not contain an authentication token.
     * thi can be caused by invalid credentials or by the protocol SkolaOnline uses changing.*
     *
     */
    public SkolaOnlineModulePage login(String username, String password) throws IOException, RequestFailedException {
        if(doLogs){System.out.println("=========================================START OF login() LOG=========================================");}
        SkolaOnlineModulePage modulePage = new SkolaOnlineModulePage(this.cookieCache, this.hiddenFormInputs);
        modulePage.doLogs = doLogs;

        HttpRequest request = new HttpRequest(new URL(SkolaOnlineModulePage.loginUrl), HttpMethod.POST);

        String boundary = getBoundary();
        String payload = constructLoginRequestBody(boundary, username, password);

        request.setBody(payload);
        addCommonHeadersToRequest(request);
        request.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary);

        request.setHeader("Origin", "https://skolaonline.cz");


        HttpResponse response = request.send();

        if(doLogs){
            request.printSelf();
        }


        response.addCookiesToCache(modulePage.cookieCache);

        if(modulePage.cookieCache.getCookie(".ASPXAUTH") == null){
            throw new RequestFailedException("failed to login");
        }

        String body = response.getBody();
        Document doc = Jsoup.parse(body);

        modulePage.updateHiddenFormInputs(doc);


        if(doLogs){
            response.printSelf();
            System.out.println("--------------------login() hidden inputs start--------------------");
            for(String hiddenInput : modulePage.hiddenFormInputs.keySet()){
                System.out.println(hiddenInput + ": " + modulePage.hiddenFormInputs.get(hiddenInput).value);
            }
            System.out.println("--------------------login() hidden inputs end--------------------");
        }
        response.disconnect();
        if(doLogs){System.out.println("=========================================END OF login() LOG=========================================");}
        return modulePage;
    }

    private String getBoundary(){
        return "---------------------------141200824129006017161243354094";
    }
    private String constructLoginRequestBody(String boundary, String username, String password){
        String __VIEWSTATE = hiddenFormInputs.get("__VIEWSTATE") == null ? "" : hiddenFormInputs.get("__VIEWSTATE").value;
        String __VIEWSTATEGENERATOR = hiddenFormInputs.get("__VIEWSTATEGENERATOR") == null ? "" : hiddenFormInputs.get("__VIEWSTATEGENERATOR").value;
        String __PREVIOUSPAGE = hiddenFormInputs.get("__PREVIOUSPAGE") == null ? "" : hiddenFormInputs.get("__PREVIOUSPAGE").value;
        String __EVENTVALIDATION = hiddenFormInputs.get("__EVENTVALIDATION") == null ? "" : hiddenFormInputs.get("__EVENTVALIDATION").value;
        String __dnnVariable = hiddenFormInputs.get("__dnnVariable") == null ? "" : hiddenFormInputs.get("__dnnVariable").value;
        String __RequestVerificationToken = hiddenFormInputs.get("__RequestVerificationToken") == null ? "" : hiddenFormInputs.get("__RequestVerificationToken").value;

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
                
""" + __VIEWSTATE + "\n" + """                
"""+boundary_delimiter + "\n" +"""
Content-Disposition: form-data; name="__VIEWSTATEGENERATOR"
                
""" + __VIEWSTATEGENERATOR + "\n" + """                
"""+boundary_delimiter + "\n" +"""
Content-Disposition: form-data; name="__VIEWSTATEENCRYPTED"
                
                
"""+boundary_delimiter + "\n" +"""
Content-Disposition: form-data; name="__PREVIOUSPAGE"
                
""" + __PREVIOUSPAGE + "\n" + """                
"""+boundary_delimiter + "\n" +"""
Content-Disposition: form-data; name="__EVENTVALIDATION"
                
""" + __EVENTVALIDATION + "\n" + """                
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
                
106
""" +boundary_delimiter + "\n" +"""
Content-Disposition: form-data; name="__dnnVariable"
                
""" + __dnnVariable + "\n" + """                
"""+boundary_delimiter + "\n" +"""
Content-Disposition: form-data; name="__RequestVerificationToken"
                
""" + __RequestVerificationToken + "\n" + """                
"""+boundary_delimiter + "--\n";
        return body.replace("\n", "\r\n");
    }

}
