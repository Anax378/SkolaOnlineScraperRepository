package net.anax;

import net.anax.scraper.RequestFailedException;
import net.anax.scraper.SkolaOnlineScraper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, XPathExpressionException, ParserConfigurationException, SAXException, RequestFailedException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject data = (JSONObject) parser.parse(new FileReader("config.json"));
        String password = (String) data.get("password");
        String username = (String) data.get("username");

        SkolaOnlineScraper scraper = new SkolaOnlineScraper(username, password);
        scraper.login();
        scraper.getTimeTable();

    }
}