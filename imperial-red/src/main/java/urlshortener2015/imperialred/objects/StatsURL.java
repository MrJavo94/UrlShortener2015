package urlshortener2015.imperialred.objects;

import java.math.BigInteger;

public class StatsURL {

    private String target;
    private String date;
    private long clicks;

    public StatsURL(String target, String date, long clicks) {

        this.target = target;
        this.date = date;
        this.clicks = clicks;
    }

    public String getTarget() { return target; }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getUsesCount() {
        return clicks;
    }

    public void setClicks(long clicks) {
        this.clicks = clicks;
    }

    @Override
    public String toString() {
        return "\"StatsURL\" : {" +
                "\"longURI\" :" + target +
                ", \"date\" :" + date +
                ", \"clicks\" :" + clicks +
                '}';
    }
}