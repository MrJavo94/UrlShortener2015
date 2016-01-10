package urlshortener2015.imperialred.objects;

import java.util.Date;

public class StatsURL {

    private String target;
    private String date;
    private long clicks;
    private Date from;
    private Date to;
    private Float minLat;
    private Float maxLat;
    private Float minLon;
    private Float maxLon;
    private String countryList;
    private String cityList;

    public StatsURL(String target, String date, long clicks, Date from, Date to,
    		Float minLat, Float maxLat, Float minLon, Float maxLon, 
    		String countryList, String cityList) {
        this.target = target;
        this.date = date;
        this.clicks = clicks;
        this.from = from;
        this.to = to;
        this.minLat = minLat;
        this.maxLat = maxLat;
        this.minLon = minLon;
        this.maxLon = maxLon;
        this.countryList = countryList;
        this.cityList = cityList;
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

	public Float getMinLat() {
		return minLat;
	}

	public void setMinLat(Float minLat) {
		this.minLat = minLat;
	}

	public Float getMaxLat() {
		return maxLat;
	}

	public void setMaxLat(Float maxLat) {
		this.maxLat = maxLat;
	}

	public Float getMinLon() {
		return minLon;
	}

	public void setMinLon(Float minLon) {
		this.minLon = minLon;
	}

	public Float getMaxLon() {
		return maxLon;
	}

	public void setMaxLon(Float maxLon) {
		this.maxLon = maxLon;
	}
	
	public String getCountryList() {
		return countryList;
	}

	public void setCountryList(String countryList) {
		this.countryList = countryList;
	}

	public String getCityList() {
		return cityList;
	}

	public void setCityList(String cityList) {
		this.cityList = cityList;
	}
	
	@Override
    public String toString() {
        return "\"StatsURL\" : {" +
                "\"longURI\" :" + target +
                ", \"date\" :" + date +
                ", \"clicks\" :" + clicks +
                ", \"from\" :" + from +
                ", \"to\" :" + to +
                ", \"minLat\" :" + minLat +
                ", \"maxLat\" :" + maxLat +
                ", \"minLon\" :" + minLon +
                ", \"maxLon\" :" + maxLon +
                ", \"countryList\" :" + countryList +
                ", \"cityList\" :" + cityList +
                '}';
    }
}