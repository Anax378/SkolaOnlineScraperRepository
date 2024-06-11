package net.anax.skolaOnlineScraper.webpage;

import net.anax.skolaOnlineScraper.browser.BrowserCookieCache;
import net.anax.skolaOnlineScraper.http.HttpRequest;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public abstract class AbstractSkolaOnlinePage {

    BrowserCookieCache cookieCache;
    HashMap<String, HiddenFormInput> hiddenFormInputs = new HashMap<>();

    public static void addCommonHeadersToRequest(HttpRequest request){
        request.setHeader("Host", "www.skolaonline.cz");
        request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:120.0) Gecko/20100101 Firefox/120.0");
        request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
        request.setHeader("Accept-Language", "en-US,en;q=0.5");
        request.setHeader("Accept-Encoding", "gzip");
        request.setHeader("Referer", "https://aplikace.skolaonline.cz/");
        request.setHeader("DNT", "1");
        request.setHeader("Connection", "keep-alive");
        request.setHeader("Upgrade-Insecure-Requests", "1");
        request.setHeader("Sec-Fetch-Dest", "document");
        request.setHeader("Sec-Fetch-Mode", "navigate");
        request.setHeader("Sec-Fetch-Site", "same-site");
        request.setHeader("Sec-Fetch-User", "?1");
    }

    public void updateHiddenFormInputs(Document doc){
        Elements inputs = doc.select("input");
        for(Element input : inputs){
            if(input.attr("type").equals("hidden")){
                this.hiddenFormInputs.put(input.attr("name"), new HiddenFormInput(input.attr("name"), input.attr("value")));
            }
        }
    }

}
