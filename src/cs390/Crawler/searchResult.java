package cs390.Crawler;

/**
 * Created by Curtis on 10/24/16.
 */
public class searchResult {
    private int searchResultID;
    private int URLID;
    private String word;


    public int getSearchResultID() {
        return searchResultID;
    }

    public void setSearchResultID(int searchResultID) {
        this.searchResultID = searchResultID;
    }

    public int getURLID() {
        return URLID;
    }

    public void setURLID(int URLID) {
        this.URLID = URLID;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
