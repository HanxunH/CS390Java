package cs390.Crawler;

import org.hibernate.Session;

/**
 * Created by Curtis on 10/24/16.
 */
public class searchResult {
    private int searchResultID;
    private String word;
    private searchURL sURL;
    private int count;

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



    public int getSearchResultID() {
        return searchResultID;
    }

    public void setSearchResultID(int searchResultID) {
        this.searchResultID = searchResultID;
    }


    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public searchURL getsURL() {
        return sURL;
    }

    public void setsURL(searchURL sURL) {
        this.sURL = sURL;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
