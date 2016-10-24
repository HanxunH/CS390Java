package cs390.Crawler;
import org.jsoup.*;
import org.jsoup.helper.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import java.util.*;
import org.hibernate.Session;
import org.hibernate.*;
import org.hibernate.query.Query;
import java.io.*;

/**
 * Created by Curtis on 10/15/16.
 */
public class Crawler {
    private int crawlerID;
    private int urlID;
    private String baseURL;
    public Properties props;
    private String domain;
    private int maxurls;
    private Session session;
    public int currentURLID;

    public void setMaxurls(int maxurls) {
        this.maxurls = maxurls;
    }

    public Crawler(String base, String domainString) {
        urlID = getUrlIDfromDB();
        baseURL = base;
        domain = domainString;
        maxurls = 1000;
        readParam();
        insertURL(baseURL);
    }

    public void startCrawlForURL() {
        session = DBConnectionManager.getSession();
        while (urlID < maxurls) {
            try {
                session.beginTransaction();
                searchURL nextURL = (searchURL) session.get(searchURL.class, currentURLID);
                session.getTransaction().commit();
                System.out.println(nextURL.getURL());
                fetchURL(nextURL.getURL());
                currentURLID++;
                saveParam();
            } catch (Exception e) {
                e.printStackTrace();
                session.getTransaction().rollback();
            }
        }
        session.close();
    }

    public void insertURL(String url) {
        System.out.format("Current crawling id: %d\n", this.currentURLID);
        searchURL temp = findsearchURLbyURL(url);
        try {
            Document document = Jsoup.connect(url).get();
            try {
                String meta_description = document.select("meta[name=description]").get(0).attr("content");
                temp.setDescription(meta_description);
                if (validateDescription(meta_description)) {
                    temp.setDescription(meta_description);
                } else {
                    String title = document.title();
                    temp.setDescription(title);
                }
            } catch (Exception e) {
                String title = document.title();
                temp.setDescription(title);
                System.out.println("No Meta description");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!temp.set) {
            temp.setURLID(urlID);
            temp.setURL(url);
            urlID++;
        }
        try {
            System.out.format("ID: %d title : %s\n", temp.getURLID(), temp.getDescription());
            temp.save();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void fetchURL(String urlScanned) {
        try {
            Document document = Jsoup.connect(urlScanned).get();
            Elements links = document.select("a[href]");
            for (Element link : links) {
                if (urlID > maxurls) {
                    return;
                }
                // get the value from href attribute
                String newUrl = link.absUrl("href");
                System.out.println("\nlink : " + newUrl);
                //System.out.print(checkDomain(newUrl));
                //System.out.println(checkFormat(newUrl));

                if (checkDomain(newUrl) && checkFormat(newUrl)) {
                    insertURL(newUrl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean checkDomain(String url) {
        return url.toLowerCase().contains(domain.toLowerCase());
    }

    public boolean checkFormat(String target) {
        if (target.length() > 7) {
            if (target.substring(0, 7).equals("http://") || target.substring(0, 8).equals("https://")) {
                return true;
            }
        }
        return false;
    }

    public searchURL findsearchURLbyURL(String url) {
        Session session = DBConnectionManager.getSession();
        searchURL temp = new searchURL();
        try {
            Query q = session.createQuery("FROM searchURL S WHERE S.URL = :currenturl");
            q.setParameter("currenturl", url);
            List<searchURL> results = q.list();
            if (results.size() > 0) {
                temp = results.get(0);
                temp.set = true;
            }
            session.beginTransaction();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        session.close();
        return temp;
    }

    public int getUrlIDfromDB() {
        Session session = DBConnectionManager.getSession();
        int rs = 0;
        try {
            Query q = session.createQuery("FROM searchURL");

            List<searchURL> results = q.list();
            rs = results.size();
            session.beginTransaction();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        session.close();
        return rs;
    }

    public boolean validateDescription(String d) {
        if (d.equals("") || d.equals("$description.value")) {
            return false;
        }
        if (d.isEmpty() == true) {
            return false;
        }
        return true;
    }

    public void crawlContent() {

    }

    public void saveParam() {
        try {
            Properties props = new Properties();
            StringBuilder sb = new StringBuilder();
            sb.append(currentURLID);
            String str = sb.toString();
            props.setProperty("currentURLID", str);
            File f = new File("crawler.properties");
            OutputStream out = new FileOutputStream(f);
            props.store(out, "This is an optional header comment string");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readParam(){
        Properties props = new Properties();
        InputStream is = null;

        // First try loading from the current directory
        try {
            File f = new File("crawler.properties");
            is = new FileInputStream( f );
        }
        catch ( Exception e ) {
            is = null;
            e.printStackTrace();
        }

        try {
            if ( is == null ) {
                // Try loading from classpath
                is = getClass().getResourceAsStream("crawler.properties");
            }
            // Try loading properties from the file (if found)
            props.load( is );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }

        currentURLID = new Integer(props.getProperty("currentURLID", "0"));
    }
}