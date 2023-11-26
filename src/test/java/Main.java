import net.anax.data.TimetableWeek;
import net.anax.scraper.RequestFailedException;
import net.anax.scraper.SkolaOnlineScraper;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException, RequestFailedException {
        File source = new File("test_html_source.html");
        BufferedReader reader = new BufferedReader(new FileReader(source));
        StringBuilder builder = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null){
            builder.append(line);
        }
        TimetableWeek week = TimetableWeek.getTimeTableFromHTML(builder.toString());
        week.printSelf();

    }
}
