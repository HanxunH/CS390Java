import cs390.Crawler.*;

/**
 * Created by Curtis on 10/15/16.
 */
public class Main {
    public static Crawler c;
    public static void main(final String[] args) throws Exception {
        c = new Crawler("http://cs.purdue.edu","purdue.edu");
        c.setMaxurls(20000);

        crawlerRunnable myRunnable2 = new crawlerRunnable(c);
        myRunnable2.setMode(2);
        Thread[] ta = new Thread[6];
        for(int i=0;i<6;i++){
            ta[i] = new Thread(myRunnable2);
            ta[i].start();
        }

        crawlerRunnable myRunnable3 = new crawlerRunnable(c);
        myRunnable3.setMode(3);
        Thread[] tb = new Thread[3];
        for(int i=0;i<3;i++){
            tb[i] = new Thread(myRunnable3);
            //tb[i].start();
        }
        /*crawlerRunnable myRunnable = new crawlerRunnable(c);
        myRunnable.setMode(0);
        Thread t = new Thread(myRunnable);
        t.start();

        crawlerRunnable myRunnable2 = new crawlerRunnable(c);
        myRunnable2.setMode(1);
        Thread[] ta = new Thread[6];
        for(int i=0;i<6;i++){
            ta[i] = new Thread(myRunnable2);
            ta[i].start();
        }


        crawlerRunnable myRunnable3 = new crawlerRunnable(c);
        myRunnable3.setMode(2);
        Thread t3 = new Thread(myRunnable3);
        t3.start();


        crawlerRunnable myRunnable4 = new crawlerRunnable(c);
        myRunnable4.setMode(3);
        Thread t4 = new Thread(myRunnable4);
        t4.start();  */


    }
}