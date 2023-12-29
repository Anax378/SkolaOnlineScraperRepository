package net.anax;

import net.anax.scraper.RequestFailedException;
import net.anax.webpage.SkolaOnlineLoginPage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException, RequestFailedException, ParseException{

        JSONParser parser = new JSONParser();
        JSONObject data = (JSONObject) parser.parse(new FileReader("config.json"));
        String password = (String) data.get("password");
        String username = (String) data.get("username");

        System.out.println(SkolaOnlineLoginPage.loadNew(true).login(username, password).goToTimetable().changeDateTo(LocalDateTime.of(2023, 11, 24, 12, 45)).getTimetable().getJsonObject().toJSONString());


    }
}