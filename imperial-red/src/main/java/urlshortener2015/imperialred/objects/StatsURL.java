package urlshortener2015.imperialred.objects;

import java.util.Date;

public class StatsURL {

    private String target;
    private String date;
    private long clicks;
    private Date from;
    private Date to;

    public StatsURL(String target, String date, long clicks, Date from, Date to) {
        this.target = target;
        this.date = date;
        this.clicks = clicks;
        this.from = from;
        this.to = to;
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

    public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

	@Override
    public String toString() {
        return "\"StatsURL\" : {" +
                "\"longURI\" :" + target +
                ", \"date\" :" + date +
                ", \"clicks\" :" + clicks +
                ", \"from\" :" + from +
                ", \"to\" :" + to +
                '}';
    }
}