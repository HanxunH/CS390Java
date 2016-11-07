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
    private int currentUpdateContentID;
    private int currentImageID;
    private int currentTitleID;
    private int urlID;
    private removeKeyword tester;

    private String baseURL;
    private String domain;
    private int maxurls;
    private Session session;
    final static Logger logger = Logger.getLogger(Crawler.class);

    public void setMaxurls(int maxurls) {
        this.maxurls = maxurls;
    }

    public Crawler(String base, String domainString) {
        tester = new removeKeyword();
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
            synchronized(this) {
                readParam();
                if(logger.isInfoEnabled()){
                    long threadId = Thread.currentThread().getId();
                    logger.info("Thread: " + threadId + " Crawl for content URLID: " + currentContentID);
                }
                saveParam(currentContentID+1,"currentContentID");
            }
            crawlContent();
        }
    }


    public void crawlContent() {
        String url_content_body_text;
        searchURL target = findsearchURLbyID(currentContentID);
        searchResult target_rs = findsearchResultbyID(currentContentID);
        target_rs.setURLID(target.getURLID());
        target_rs.setUrl(target.getURL());
        target_rs.setTitle(target.getTitle());
        if(target.getImage_url() != null){
            target_rs.setImage_url(target.getImage_url());
        }
        target_rs.setDescription(target.getDescription());

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
                    if(tester.is_remove_keyword(temp) == false){
                        if(hm_result.containsKey(temp)){
                            hm_result.put(temp,(int)hm_result.get(temp)+1);
                        }else{
                            hm_result.put(temp,1);
                        }
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
            synchronized(this) {
                readParam();
                crawlForImage();
                saveParam(currentImageID+1,"currentImageID");
            }
        }
    }

    public void startCrawlForTitle(){
        while(currentTitleID < getUrlIDfromDB()){
            synchronized(this) {
                readParam();
                saveParam(currentTitleID+1,"currentTitleID");
            }
            crawlForTitle();

        }
    }


    public void startUpdateContent(){
        while(currentUpdateContentID < getContentUrlIDfromDB()){
            synchronized(this) {
                readParam();
                saveParam(currentUpdateContentID+1,"currentUpdateContentID");
            }
            updateSearchResult();
        }
    }

    public void updateSearchResult(){
        searchResult target = findsearchResultbyID(currentUpdateContentID);
        if(target == null){
            if(logger.isInfoEnabled()){
                logger.info("Error ");
            }
            return;
        }
        try{
            searchURL s = findsearchURLbyID(target.getURLID());
            target.setDescription(target.getDescription());
            target.setImage_url(target.getImage_url());
            target.setTitle(target.getTitle());
            if(logger.isInfoEnabled()){
                logger.info("Update for URLID: " + currentUpdateContentID + " URL: "+ target.getUrl());
            }
            target.save();
        }   catch (Exception e){
            e.printStackTrace();
        }

    }

    public void crawlForImage(){
        searchURL target = findsearchURLbyID(currentImageID);
        if(target.set){
            try{
                Document document = Jsoup.connect(target.getURL()).get();
                Elements img = document.getElementsByTag("img");
                if(img.size()>0){
                    target.setImage_url(img.get(0).absUrl("src"));
                    if(logger.isInfoEnabled()){
                        logger.info("Image saved for URLID: " + currentImageID + " URL: "+ target.getURL());
                        logger.info(target.getImage_url());
                    }
                    target.save();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    public void crawlForTitle(){
        searchURL target = findsearchURLbyID(currentTitleID);
        if(target.set){
            try{
                Document document = Jsoup.connect(target.getURL()).get();
                String title = document.title();
                target.setTitle(title);
                if(logger.isInfoEnabled()){
                    logger.info("Title saved for URLID: " + currentTitleID + " URL: "+ target.getURL());
                }
                target.save();
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
        currentContentID = new Integer(props.getProperty("currentContentID", "0"));
        currentURLID = new Integer(props.getProperty("currentURLID", "0"));
        currentImageID = new Integer(props.getProperty("currentImageID", "0"));
        currentTitleID = new Integer(props.getProperty("currentTitleID", "0"));
        currentUpdateContentID = new Integer(props.getProperty("currentUpdateContentID", "0"));
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

    public void crawlForPeople(){
        String base_url = "https://www.cs.purdue.edu/people/faculty/index.html";
        try{
            Document doc = Jsoup.connect(base_url).get();
            for (Element table : doc.select("table")) {
                for (Element row : table.select("tr")) {
					searchPeople sp = new searchPeople();
					Elements tds = row.select("td");
                    if(tds.size()<1){
                        continue;
                    }
                    int i = 0;
                    for(Element t : tds){
						System.out.print(t.text()+ " ");
                        if(i == 0){
                            String name = tds.get(i).text();
                            Scanner scanner = new Scanner(name).useDelimiter("\\s* \\s*");
                            sp.setFirstName(scanner.next());
                            sp.setLastName(scanner.next());
                        } else if (i == 1) {
                            sp.setPosition(tds.get(i).text());
                        } else if(!t.select("phone").text().equals("")){
							sp.setPhone_number(t.select("phone").text());
						} else if(!t.select("office").text().equals("")){
                            sp.setOffice(t.select("office").text());
                        } else if(t.text().equals("Mail")){
                            sp.setEmail_url(t.select("a[href]").attr("href"));
                        } else if(t.text().equals("Bio")){
                            sp.setHomepage_url(t.select("a[href]").attr("href"));
                        }
                        i++;
                    }
                    Document doc2 = Jsoup.connect(sp.getHomepage_url()).get();
                    Elements target = doc2.select("img");
                    if(target.size() == 3){
                        sp.setImg_url(target.get(2).attr("src"));
                        System.out.println(sp.getImg_url());
                    }
                    sp.save();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }


    }
}