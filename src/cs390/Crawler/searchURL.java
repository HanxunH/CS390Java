package cs390.Crawler;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Created by Curtis on 10/16/16.
 */
public class searchURL implements java.io.Serializable{
    private int URLID;
    private String URL;
    private String Description;
    public boolean set;
    private String image_url;
    private String title;

    public searchURL() {
        set = false;
    }

    public void save(){
        Session session = null;
        try {
            session = DBConnectionManager.getSession();
            session.beginTransaction();
            session.saveOrUpdate(this);
            session.getTransaction().commit();
            set = true;
        }catch(Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        session.close();
    }




    public int getURLID() {
        return URLID;
    }

    public void setURLID(int URLID) {
        this.URLID = URLID;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
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
