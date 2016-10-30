package cs390.Crawler;

import org.hibernate.Session;

import java.util.HashMap;

/**
 * Created by Curtis on 10/24/16.
 */
public class searchResult {
    private int searchResultID;
    private searchURL sURL;
    private int count;
    private HashMap<String,Integer> result_hash_map;

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

    public void addResult(String rs){
        if(result_hash_map.containsKey(rs)){
            result_hash_map.put(rs,result_hash_map.get(rs)+1);
        }else{
            result_hash_map.put(rs,1);
        }
    }

    public HashMap<String, Integer> getResult_hash_map() {
        return result_hash_map;
    }

    public void setResult_hash_map(HashMap<String, Integer> result_hash_map) {
        this.result_hash_map = result_hash_map;
    }

    public int getSearchResultID() {
        return searchResultID;
    }

    public void setSearchResultID(int searchResultID) {
        this.searchResultID = searchResultID;
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
