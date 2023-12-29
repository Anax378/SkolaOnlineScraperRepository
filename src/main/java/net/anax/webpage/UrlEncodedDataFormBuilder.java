package net.anax.webpage;

public class UrlEncodedDataFormBuilder {
    StringBuilder data = new StringBuilder();
    public UrlEncodedDataFormBuilder(){}

    public void addField(String fieldName, String value){
        if(!data.isEmpty()){data.append('&');}
        data.append(fieldName).append("=").append(value == null ? "" : value);
    }

    public String getData(){
        return data.toString();
    }
}
