package net.anax.http;

import java.net.HttpURLConnection;

public class HttpHeader {
    public String header;
    public String value;

    public HttpHeader(String header, String value) {
        this.header = header;
        this.value = value;
    }
}
