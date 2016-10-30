import cs390.Crawler.*;

/**
 * Created by Curtis on 10/15/16.
 */
public class Main {
    public static Crawler c;
    public static void main(final String[] args) throws Exception {
        c = new Crawler("http://cs.purdue.edu","purdue.edu");
        c.setMaxurls(15000);

        crawlerRunnable myRunnable = new crawlerRunnable(c);
        Thread t = new Thread(myRunnable);
        t.start();
    }
}