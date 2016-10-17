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
    public SessionFactory sessionFactory;
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
        sessionFactory = new Configuration().configure().buildSessionFactory();
        session = sessionFactory.openSession();

    }

    public void startCrawlForURL(){
        fetchURL(baseURL);
        if(urlID<maxurls){
            try{
                session.beginTransaction();
                searchURL nextURL = (searchURL) session.get(searchURL.class, currentURLID);
                fetchURL(nextURL.getURL());
            }catch(Exception e){

            }
        }
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
            System.out.println("title : " + title);
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
                String newUrl = connectURL(link.attr("href"),urlScanned);
                System.out.println("\nlink : " + newUrl);
                if(checkDomain(newUrl)){
                    insertURL(newUrl);
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String connectURL(String target, String base){
        if(target.length()>7){
            if(target.substring(0,7).equals("http://") || target.substring(0,8).equals("https://")){
                return target;
            }
        }
        String rs;
        if(base.charAt(base.length()-1)=='/'){
            rs = base+target;
        }else {
            rs = base + "/" + target;
        }
        return rs;
    }

    public boolean checkDomain(String url){
        return url.toLowerCase().contains(domain.toLowerCase());
    }

}
