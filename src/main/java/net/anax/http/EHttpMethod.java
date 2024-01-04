package net.anax.http;

public enum EHttpMethod {
    POST("POST"),
    GET("GET"),
    ;
    String name;
    EHttpMethod(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
