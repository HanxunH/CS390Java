
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import cs390.Crawler.*;

/**
 * Created by Curtis on 10/15/16.
 */
public class Main {

    public static void main(final String[] args) throws Exception {
        Crawler c = new Crawler();
        c.fetchURL("http://cs.purdue.edu");
    }
}