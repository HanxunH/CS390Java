package cs390.Crawler;
import org.apache.log4j.varia.NullAppender;
import org.jsoup.*;
import org.jsoup.helper.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import java.util.*;
import org.hibernate.Session;
import org.hibernate.*;
import org.hibernate.query.Query;
import java.io.*;
import org.apache.log4j.Logger;

/**
 * Created by Curtis on 10/15/16.
 */
public class Crawler {
    private int currentURLID;
    private int currentContentID;
    private int currentImageID;
    private int urlID;

    private String baseURL;
    private String domain;
    private int maxurls;
    private Session session;
    final static Logger logger = Logger.getLogger(Crawler.class);

    public void setMaxurls(int maxurls) {
        this.maxurls = maxurls;
    }

    public Crawler(String base, String domainString) {
        urlID = getUrlIDfromDB();
        baseURL = base;
        domain = domainString;
        currentContentID = getContentUrlIDfromDB();
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
                if(logger.isInfoEnabled()){
                    logger.info("Current Crawling ID: " + this.currentURLID);
                    logger.info("Crawler for URL: " + nextURL.getURL());
                }
                fetchURL(nextURL.getURL());
                currentURLID++;
                saveParam(currentURLID,"currentURLID");
            } catch (Exception e) {
                e.printStackTrace();
                session.getTransaction().rollback();
            }
        }
        session.close();
    }



    public void startCrawlForContent() {
        while(currentContentID < getUrlIDfromDB()){
            crawlContent();
            currentContentID = currentContentID + 1;
            saveParam(currentContentID,"currentContentID");
        }
    }


    public void crawlContent() {
        String url_content_body_text;
        searchURL target = findsearchURLbyID(currentContentID);
        searchResult target_rs = findsearchResultbyID(currentContentID);
        if(target_rs.set == true){
            return;
        }
        target_rs.setURLID(target.getURLID());
        target_rs.setUrl(target.getURL());
        Map hm_result = new HashMap<String,Integer>();;
        if(target.set == true) {
            try {
                Document document = Jsoup.connect(target.getURL()).get();
                url_content_body_text = document.text();
                url_content_body_text = url_content_body_text.toLowerCase();
                Scanner scanner = new Scanner(url_content_body_text).useDelimiter("\\s* \\s*");
                target_rs.setContent(url_content_body_text);
                while(scanner.hasNext()){
                    String temp = scanner.next();
                    temp.toLowerCase();
                    if(hm_result.containsKey(temp)){
                        hm_result.put(temp,(int)hm_result.get(temp)+1);
                    }else{
                        hm_result.put(temp,1);
                    }
                }
                target_rs.setHm_result(hm_result);
                target_rs.save();
                if(logger.isInfoEnabled()){
                    logger.info("Content saved for URLID: "+ target_rs.getURLID() +" URL: " + target_rs.getUrl());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public void startCrawlForImage(){
        while(currentImageID < getUrlIDfromDB()){
            crawlForImage();
            currentImageID = currentImageID + 1;
            saveParam(currentImageID,"currentImageID");
        }
    }





    public void crawlForImage(){
        searchURL target = findsearchURLbyID(currentImageID);
        if(target.set){
            try{
                Document document = Jsoup.connect(target.getURL()).get();
                Elements img = document.getElementsByTag("img");
                if(img.size()>0 && img.get(0).hasAttr("alt")){
                    target.setImage_url(img.get(0).absUrl("src"));
                    if(logger.isInfoEnabled()){
                        logger.info("Image saved for URLID: " + currentImageID + " URL: "+ target.getURL());
                    }
                    target.save();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }



    public void insertURL(String url) {
        searchURL temp = findsearchURLbyURL(url);
        try {
            Document document = Jsoup.connect(url).get();
            try {
                String title = document.title();
                temp.setTitle(title);
                String meta_description = document.select("meta[name=description]").get(0).attr("content");
                temp.setDescription(meta_description);
                if (validateDescription(meta_description)) {
                    temp.setDescription(meta_description);
                } else {
                    temp.setDescription(title);
                }
            } catch (Exception e) {
                String title = document.title();
                temp.setDescription(title);
                //System.out.println("No Meta description");
            }
        }catch (Exception e) {
            e.printStackTrace();
            if(logger.isInfoEnabled()){
                logger.info("Exception during insert URL: " + temp.getURL());
            }
            return;
        }

        if (!temp.set) {
            temp.setURLID(urlID);
            temp.setURL(url);
            urlID++;
        }
        try {
            if(logger.isInfoEnabled()){
                logger.info("Save URLID: " + temp.getURLID() + " Saved URL: " + temp.getURL());
            }
            searchResult rs = new searchResult();
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
                String newUrl = link.absUrl("href");
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
            session.beginTransaction();
            Query q = session.createQuery("FROM searchURL S WHERE S.URL = :currenturl");
            q.setParameter("currenturl", url);
            List<searchURL> results = q.list();
            if (results.size() > 0) {
                temp = results.get(0);
                temp.set = true;
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        session.close();
        return temp;
    }

    public searchURL findsearchURLbyID(int id){
        Session session = DBConnectionManager.getSession();
        searchURL temp = new searchURL();
        try {
            temp =  (searchURL) session.get(searchURL.class, id);
            temp.set = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        session.close();
        return temp;
    }


    public searchResult findsearchResultbyID(int id){
        Session session = DBConnectionManager.getSession();
        searchResult temp = null;
        try {
            temp =  (searchResult) session.get(searchResult.class, id);
            if(temp == null){
                return new searchResult();
            }
            temp.set = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        session.close();
        return temp;
    }





    public int getUrlIDfromDB() {
        Session session = DBConnectionManager.getSession();
        int rs = 0;
        try {
            session.beginTransaction();
            Query q = session.createQuery("FROM searchURL");
            List<searchURL> results = q.list();
            rs = results.size();
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


    public void saveParam(int target, String id) {
        try {
            FileInputStream in = new FileInputStream("crawler.properties");
            Properties props = new Properties();
            props.load(in);
            in.close();

            FileOutputStream out = new FileOutputStream("crawler.properties");
            props.setProperty(id, Integer.toString(target));
            props.store(out, null);
            out.close();

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
        currentContentID = getContentUrlIDfromDB();
        currentURLID = new Integer(props.getProperty("currentURLID", "0"));
        currentImageID = new Integer(props.getProperty("currentImageID", "0"));
    }


    public int getContentUrlIDfromDB() {
        Session session = DBConnectionManager.getSession();
        int rs = 0;
        try {
            session.beginTransaction();
            Query q = session.createQuery("FROM searchResult ");
            List<searchURL> results = q.list();
            rs = results.size();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        session.close();
        return rs;
    }
}