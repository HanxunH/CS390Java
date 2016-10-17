package cs390.Crawler;
import org.jsoup.*;
import org.jsoup.helper.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import java.util.*;
import org.hibernate.Session;
import org.hibernate.*;
import org.hibernate.query.Query;


/**
 * Created by Curtis on 10/15/16.
 */
public class Crawler {
    public int urlID;
    public String baseURL;
    public Properties props;
    public String domain;
    public int maxurls;
    public Session session;
    public int currentURLID;
    public void setMaxurls(int maxurls) {
        this.maxurls = maxurls;
    }

    public Crawler(String base,String domainString) {
        urlID = getUrlIDfromDB();
        baseURL = base;
        domain = domainString;
        maxurls = 1000;
        currentURLID = 0;
        insertURL(baseURL);
    }

    public void startCrawlForURL(){
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
        searchURL temp = findsearchURLbyURL(url);
        if(temp.set){
            try{
                Document document = Jsoup.connect(url).get();
                String title = document.title();
                temp.setDescription(title);
                System.out.format("Update ID: %d title : %s\n",temp.getURLID() , temp.getDescription());
                temp.save();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            return;
        }
        temp.setURLID(urlID);
        temp.setURL(url);
        try{
            Document document = Jsoup.connect(url).get();
            String title = document.title();
            System.out.format("ID: %d title : %s\n",urlID , title);
            temp.setDescription(title);
            temp.save();
            urlID++;
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void fetchURL(String urlScanned) {
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
                //System.out.print(checkDomain(newUrl));
                //System.out.println(checkFormat(newUrl));

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

    public searchURL findsearchURLbyURL(String url){
        Session session = DBConnectionManager.getSession();
        searchURL temp = new searchURL();
        try {
            Query q = session.createQuery("FROM searchURL S WHERE S.URL = :currenturl");
            q.setParameter("currenturl",url);
            List<searchURL> results = q.list();
            if(results.size()>0){
                temp = results.get(0);
                temp.set = true;
            }
            session.beginTransaction();
            session.getTransaction().commit();
        }catch(Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        session.close();
        return temp;
    }

    public int getUrlIDfromDB(){
        Session session = DBConnectionManager.getSession();
        int rs = 0;
        try {
            Query q = session.createQuery("FROM searchURL");

            List<searchURL> results = q.list();
            rs = results.size();
            session.beginTransaction();
            session.getTransaction().commit();
        }catch(Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        session.close();
        return rs;
    }

}
