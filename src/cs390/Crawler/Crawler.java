package cs390.Crawler;
import java.io.*;
import java.net.*;
import java.util.regex.*;
import java.sql.*;
import java.util.*;
/**
 * Created by Curtis on 10/15/16.
 */
public class Crawler {
    int urlID;
    public Properties props;


    public Crawler() {
        urlID = 0;
    }

    public void insertURL(String url){
        searchURL temp = new searchURL();
        temp.setURLID(urlID);
        temp.setURL(url);
        urlID++;
    }

    public void fetchURL(String urlScanned) {
        try {
            URL url = new URL(urlScanned);
            System.out.println("urlscanned="+urlScanned+" url.path="+url.getPath());
            InputStreamReader in = new InputStreamReader(url.openStream());
            // read contents into string builder
            StringBuilder input = new StringBuilder();
            int ch;
            while ((ch = in.read()) != -1) {
                input.append((char) ch);
            }

            // search for all occurrences of pattern
            String patternString =  "<a\\s+href\\s*=\\s*(\"[^\"]*\"|[^\\s>]*)\\s*>";
            Pattern pattern = Pattern.compile(patternString,Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                String match = input.substring(start, end);
                String urlFound = matcher.group(1);
                System.out.println(urlFound);
                // Check if it is already in the database
                System.out.println(match);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
