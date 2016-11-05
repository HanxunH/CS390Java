package cs390.SearchEngine;

import cs390.Crawler.*;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.LinkedList;
import java.util.List;
import java.util.*;

/**
 * Created by Curtis on 11/3/16.
 */
public class SearchEngine {
    private String searchKeyword;
    public List<searchResult> searchEngineResult;
    final static Logger logger = Logger.getLogger(SearchEngine.class);

    public SearchEngine(String searchKeyword) {
        this.searchKeyword = searchKeyword;
        this.searchKeyword = this.searchKeyword.toLowerCase();
    }


    public void start() {
        Session session = DBConnectionManager.getSession();
        Query q = session.createQuery("FROM searchResult AS rs Left JOIN rs.hm_result WHERE word = :keyword order by word_count DESC");
        q.setParameter("keyword",searchKeyword);
        try{
            List<searchResult> results = q.list();
            for(searchResult rs : results){
                System.out.println(rs.getUrl());
                System.out.println(rs.getHm_result().get(searchKeyword));

            }
            searchEngineResult = results;
        }catch (Exception e){
            e.printStackTrace();
        }



    }







    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }
}
