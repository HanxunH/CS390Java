package cs390.Crawler;

import org.hibernate.Session;

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
}
