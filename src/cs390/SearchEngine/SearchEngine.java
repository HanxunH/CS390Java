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
    public List<SearchEngineURLDetail> searchEngineURLDetailList;

    final static Logger logger = Logger.getLogger(SearchEngine.class);

    public SearchEngine(String searchKeyword) {
        this.searchKeyword = searchKeyword;
        this.searchKeyword = this.searchKeyword.toLowerCase();
        searchEngineURLDetailList = new ArrayList<SearchEngineURLDetail>();
    }


    public void start() {
        int i = 0;
        Session session = DBConnectionManager.getSession();
        Scanner scanner = new Scanner(searchKeyword).useDelimiter("\\s* \\s*");
        while(scanner.hasNext()) {
            Query q = session.createQuery("FROM searchResult AS rs Left JOIN rs.hm_result WHERE word = :keyword order by word_count DESC");
            q.setParameter("keyword",scanner.next());
            try{
                List<searchResult> temp_rs_list = q.list();
                if(i==0){
                    searchEngineResult = temp_rs_list;
                }else{
                    List<searchResult> temp_rs_list2 = searchEngineResult;
                    temp_rs_list2.retainAll(temp_rs_list);
                    searchEngineResult = temp_rs_list2;
                }
                i++;
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        for(searchResult rs : searchEngineResult){
            searchURL temp_url  = findsearchURLbyID(rs.getURLID());
            SearchEngineURLDetail sers = new SearchEngineURLDetail();
            sers.setSu(temp_url);
            sers.setRs(rs);
            searchEngineURLDetailList.add(sers);
        }
    }




    public searchURL findsearchURLbyID(int id){
        searchURL temp = new searchURL();
        Session session = DBConnectionManager.getSession();
        try {
            temp =  (searchURL) session.get(searchURL.class, id);
            temp.set = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        session.close();
        return temp;
    }







    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }
}
