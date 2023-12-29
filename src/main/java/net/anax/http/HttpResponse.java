package net.anax.http;

import net.anax.browser.BrowserCookieCache;
import net.anax.logging.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

public class HttpResponse {
    HttpURLConnection connection;
    int responseCode;
    String responseMessage;
    Map<String, List<String>> headers;
    String body;
    HttpResponse(HttpURLConnection connection) throws IOException {
        responseCode = connection.getResponseCode();
        responseMessage = connection.getResponseMessage();
        this.connection = connection;
        headers = connection.getHeaderFields();
        this.body = getPayload();
    }

    public void printSelf() throws IOException {
        System.out.println("----------------HttpResponse printSelf() Start----------------");
        System.out.println(this.getResponseCode() + " " + responseMessage);

        for(String header : headers.keySet()){
            System.out.println(header + ": " + Arrays.toString(headers.get(header).toArray()));
        }


        Logger.LOGFOLDEDSECTION("\n" + getBody());

        System.out.println("----------------HttpResponse printSelf() End----------------");
    }

    private String getPayload() throws IOException{
        String payload;
        try{
            payload = getPayloadFromGzip();
        } catch (EOFException | ZipException e) {
            payload = getPayloadFromPlain();
        }
        return payload;
    }

    int getResponseCode() {
        return responseCode;
    }
    Map<String, List<String>> getHeaders(){
        return headers;
    }
    List<String> getHeader(String name){
        return headers.get(name);
    }

   ArrayList<HttpCookie> getSetCookies(){
        ArrayList<HttpCookie> cookies = new ArrayList<>();
        for(String value : headers.get("set-cookie")){
            HttpCookie cookie;
            if((cookie = parseCookie(value)) != null){
                cookies.add(cookie);
            }
        }
        return cookies;
    }
    HttpCookie parseCookie(String cookieHeaderValue){
        String[] parts = cookieHeaderValue.split(";");
        String[] name_value = parts[0].split("=");
        if(name_value.length < 2){
            return null;
        }
        HttpCookie cookie = new HttpCookie(name_value[0], name_value[1]);
        for(int i = 1; i < parts.length; i++){
            String[] property_value = parts[i].split("=");
            cookie.properties.put(property_value[0], property_value.length > 1 ? property_value[1] : "true");
        }
        return cookie;
    }

    public void addCookiesToCache(BrowserCookieCache cache){
        for(HttpCookie cookie : getSetCookies()){
            cache.setCookie(cookie);
        }
    }


    public String getBody(){
        return body;
    }
    private String getPayloadFromGzip() throws IOException {
        return getPayloadFromInputStream(new GZIPInputStream(this.getInputStream()));
    }

    private String getPayloadFromPlain() throws IOException {
        return getPayloadFromInputStream(this.getInputStream());
    }

    private InputStream getInputStream() throws IOException {
        return connection.getInputStream();
    }

    private String getPayloadFromInputStream(InputStream istream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
        String line;
        StringBuilder builder = new StringBuilder();

        while((line = reader.readLine()) != null){
            builder.append(line).append("\n");
        }
        return builder.toString();
    }

    public void disconnect(){
        connection.disconnect();
    }
}
