package cs390.Crawler;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
/**
 * Created by Curtis on 10/17/16.
 */
public class DBConnectionManager {
    private static SessionFactory factory;
    static {
        factory = new Configuration().configure().buildSessionFactory();
    }

    public static Session getSession() {
        return factory.openSession();
    }

    // Call this during shutdown
    public static void close() {
        factory.close();
    }
}
