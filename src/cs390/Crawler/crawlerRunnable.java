package cs390.Crawler;

/**
 * Created by Curtis on 10/26/16.
 */
public class crawlerRunnable implements Runnable  {
    private Crawler c;
    private int mode;/*0 for url ---- 1 for word*/
    public crawlerRunnable(Crawler c) {
        this.c = c;
    }

    public void run() {
        if(mode==0){
            c.startCrawlForURL();
        }else if(mode == 1){
            c.crawlContent();
        }
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
