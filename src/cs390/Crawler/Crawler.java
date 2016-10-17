package cs390.Crawler;
import org.jsoup.*;
import org.jsoup.helper.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import java.util.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
/**
 * Created by Curtis on 10/15/16.
 */
public class Crawler {
    public int urlID;
    public String baseURL;
    public Properties props;
    public String domain;
    public HashMap hm;
    public int maxurls;
    public Session session;
    public int currentURLID;
    public void setMaxurls(int maxurls) {
        this.maxurls = maxurls;
    }

    public Crawler(String base,String domainString) {
        urlID = 0;
        baseURL = base;
        hm = new HashMap();
        domain = domainString;
        maxurls = 1000;
        currentURLID = 0;
        insertURL(baseURL);

    }

    public void startCrawlForURL(){
        fetchURL(baseURL);
        session = DBConnectionManager.getSession();
        while (urlID < maxurls){
            try{
                session.beginTransaction();
                searchURL nextURL = (searchURL) session.get(searchURL.class, currentURLID);
                session.getTransaction().commit();
                System.out.println(nextURL.getURL());
                fetchURL(nextURL.getURL());
            }catch(Exception e){
                e.printStackTrace();
                session.getTransaction().rollback();
            }
        }
        session.close();
    }

    public void insertURL(String url){
        if(hm.containsKey(url)){
            return;
        }

        searchURL temp = new searchURL();
        temp.setURLID(urlID);
        temp.setURL(url);
        try{
            Document document = Jsoup.connect(url).get();
            String title = document.title();
            System.out.format("ID: %d title : %s\n",urlID , title);
            temp.setDescription(title);
            temp.save();
            hm.put(url,urlID);
            urlID++;
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void fetchURL(String urlScanned) {
        currentURLID = currentURLID+1;
        try{
            Document document = Jsoup.connect(urlScanned).get();
            Elements links = document.select("a[href]");
            for (Element link : links) {
                if(urlID>maxurls){
                    return;
                }
                // get the value from href attribute
                String newUrl = link.absUrl("href");
                System.out.println("\nlink : " + newUrl);
                System.out.print(checkDomain(newUrl));
                System.out.println(checkFormat(newUrl));

                if(checkDomain(newUrl)&&checkFormat(newUrl)){
                    insertURL(newUrl);
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public boolean checkDomain(String url){
        return url.toLowerCase().contains(domain.toLowerCase());
    }

    public boolean checkFormat(String target){
        if(target.length()>7){
            if(target.substring(0,7).equals("http://") || target.substring(0,8).equals("https://")){
                return true;
            }
        }
        return false;
    }

}
