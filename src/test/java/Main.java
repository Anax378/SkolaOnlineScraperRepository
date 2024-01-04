import net.anax.data.InvalidDataInJsonException;
import net.anax.data.assessment.AssessmentList;
import net.anax.data.timetable.TimetableWeek;
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
        String html = builder.toString();

        AssessmentList list = AssessmentList.parseFromHTML(html);
        System.out.println(list.getJSONObject().toJSONString());
        list.printSelf();

    }
}
