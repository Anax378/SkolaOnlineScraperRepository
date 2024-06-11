package net.anax.skolaOnlineScraper.browser;

import net.anax.skolaOnlineScraper.http.HttpCookie;

import java.util.HashMap;

public class BrowserCookieCache {
    HashMap<String, HttpCookie> stored_cookies = new HashMap<>();
    public BrowserCookieCache(){}
    public void setCookie(HttpCookie cookie) {
        stored_cookies.put(cookie.name, cookie);
    }
    public HttpCookie getCookie(String name){
        return stored_cookies.get(name);
    }
    public void clearCache(){
        stored_cookies.clear();
    }


}
