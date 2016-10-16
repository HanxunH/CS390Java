import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.Map;

/**
 * Created by Curtis on 10/15/16.
 */
public class Main {

    public static void main(final String[] args) throws Exception {
        Configuration cfg = new Configuration().configure();
        SessionFactory factory = cfg.buildSessionFactory();
        Session session = null;
        try {
            session = factory.openSession();
            //开启事务
            session.beginTransaction();
            session.getTransaction().commit();
        }catch(Exception e) {
            e.printStackTrace();
            //回滚事务
            session.getTransaction().rollback();
        }
    }
}