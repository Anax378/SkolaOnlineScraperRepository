package net.anax.skolaOnlineScraper.http;

import java.util.HashMap;

public class HttpCookie {
    public String name;
    public String value;
    public HashMap<String, String> properties = new HashMap<>();

    public HttpCookie(){};
    public HttpCookie(String name, String value){
        this.name = name;
        this.value = value;
    }
}