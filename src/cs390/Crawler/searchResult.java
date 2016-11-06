package cs390.Crawler;

import org.hibernate.Session;
import java.util.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Curtis on 10/30/16.
 */
public class searchResult {
    private int searchResultID;
    private int URLID;
    private String url;
    private Map hm_result;
    private String content;
    public boolean set;
    private LinkedList<String> keywords;

    private String Description;
    private String image_url;
    private String title;


    public void save(){
        Session session = null;
        try {
            session = DBConnectionManager.getSession();
            session.beginTransaction();
            session.saveOrUpdate(this);
            session.getTransaction().commit();
        }catch(Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        session.close();
    }

    public searchResult() {
        set = false;
        hm_result = new HashMap<String,Integer>();
    }

    public int getURLID() {
        return URLID;
    }

    public void setURLID(int URLID) {
        this.URLID = URLID;
    }

    public Map getHm_result() {
        return hm_result;
    }

    public void setHm_result(Map hm_result) {
        this.hm_result = hm_result;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSearchResultID() {
        return searchResultID;
    }

    public void setSearchResultID(int searchResultID) {
        this.searchResultID = searchResultID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LinkedList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(LinkedList<String> keywords) {
        this.keywords = keywords;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
