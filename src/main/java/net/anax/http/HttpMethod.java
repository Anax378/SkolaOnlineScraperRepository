package net.anax.http;

public enum HttpMethod {
    POST("POST"),
    GET("GET"),
    ;
    String name;
    HttpMethod(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
