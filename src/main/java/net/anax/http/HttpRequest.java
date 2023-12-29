package net.anax.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class HttpRequest {
   HttpURLConnection connection;
   String body;
   ArrayList<HttpCookie> cookies = new ArrayList<>();
   HttpMethod method;
   URL url;
   ArrayList<HttpHeader> headers = new ArrayList<>();
   HttpRequest(URL url){
      this.url = url;
      this.method = HttpMethod.GET;
   }

   public void printSelf(){
      System.out.println("----------------HttpRequest printSelf() Start----------------");
      System.out.println(method.getName() + " " + url.toString());

      for(HttpHeader header : headers){
         System.out.println(header.header + ": " + header.value);
      }

      if(!cookies.isEmpty()){
         System.out.println("Cookie: " + constructCookieHeader());
      }

      System.out.println("\n" + body);

      System.out.println("----Cookies Start----");
      for(HttpCookie cookie : cookies){
         System.out.println(cookie.name + ": " + cookie.value);
         for(String property : cookie.properties.keySet()){
            System.out.println("\t" + property + ": " + cookie.properties.get(property));
         }
      }

      System.out.println("----Cookies End----");
      System.out.println("----------------HttpRequest printSelf() End ----------------");

   }
   public HttpRequest(URL url, HttpMethod method) throws IOException {
      this(url);
      this.setMethod(method);
   }
   void setMethod(HttpMethod method) throws ProtocolException {
      this.method = method;
   };
   public void setHeader(String header, String value){
      headers.add(new HttpHeader(header, value));
   }
   public void setBody(String body){
      this.body = body;
   }
   public void addCookie(HttpCookie cookie){
      if(cookie != null){
         cookies.add(cookie);
      }
   }
   public HttpResponse send() throws IOException {
      this.connection = (HttpURLConnection) this.url.openConnection();
      connection.setRequestMethod(method.getName());
      connection.setInstanceFollowRedirects(false);

      for(HttpHeader header : headers){
         connection.setRequestProperty(header.header, header.value);
      }

      connection.setDoInput(true);

      if(!cookies.isEmpty()){
        connection.setRequestProperty("Cookie", constructCookieHeader());
      }

      if(body != null && !body.isEmpty()){
         int contentLength = body.length();

         this.setHeader("Content-Length", String.valueOf(contentLength));

         connection.setDoOutput(true);
         connection.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
      }

      return new HttpResponse(connection);
   }

   private String constructCookieHeader(){
      StringBuilder cookieHeaderBuilder = new StringBuilder();
      for(HttpCookie cookie : cookies){
         cookieHeaderBuilder.append(cookie.name).append("=").append(cookie.value).append(";");
      }
      cookieHeaderBuilder.deleteCharAt(cookieHeaderBuilder.length()-1);
      return cookieHeaderBuilder.toString();
   }
}
