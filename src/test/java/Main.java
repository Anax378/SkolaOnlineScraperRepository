import net.anax.data.InvalidDataInJsonException;
import net.anax.data.TimetableWeek;
import net.anax.scraper.RequestFailedException;
import org.json.simple.parser.ParseException;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException, RequestFailedException, InvalidDataInJsonException, ParseException {
        File source = new File("test_html_source.html");
        BufferedReader reader = new BufferedReader(new FileReader(source));
        StringBuilder builder = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null){
            builder.append(line);
        }
        TimetableWeek week = TimetableWeek.getTimeTableFromHTML(builder.toString());
        String json = week.getJsonObject().toJSONString();
        TimetableWeek reconstructedWeek = TimetableWeek.parseFromJsonString(json);
        System.out.println(json);
        System.out.println(reconstructedWeek.getJsonObject().toJSONString());

    }
}
