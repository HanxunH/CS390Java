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
        myRunnable.setMode(1);
        Thread t = new Thread(myRunnable);
        t.start();

        crawlerRunnable myRunnable2 = new crawlerRunnable(c);
        myRunnable2.setMode(0);
        Thread t2 = new Thread(myRunnable2);
        t2.start();

    }
}