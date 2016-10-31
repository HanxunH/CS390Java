package cs390.Crawler;

import java.util.HashMap;

/**
 * Created by Curtis on 10/30/16.
 */
public class searchResult {
    private int URLID;
    private HashMap<String,Integer> hm_result;

    public searchResult() {
        hm_result = new HashMap<String,Integer>();
    }

    public int getURLID() {
        return URLID;
    }

    public void setURLID(int URLID) {
        this.URLID = URLID;
    }

    public HashMap<String, Integer> getHm_result() {
        return hm_result;
    }

    public void setHm_result(HashMap<String, Integer> hm_result) {
        this.hm_result = hm_result;
    }
}
