package urlshortener2015.imperialred.objects;

public class WebSocketsData {

	private long clicks;
	private String clicksByCountry;
	private String clicksByCity;
	private boolean filter;

	public WebSocketsData(boolean filter, long clicks, String clicksByCountry,
			String clicksByCity) {
		this.filter = filter;
		this.clicks = clicks;
		this.clicksByCountry = clicksByCountry;
		this.clicksByCity = clicksByCity;
	}

	/**
	 * @return the clicksByCity
	 */
	public String getClicksByCity() {
		return clicksByCity;
	}

	/**
	 * @param clicksByCity the clicksByCity to set
	 */
	public void setClicksByCity(String clicksByCity) {
		this.clicksByCity = clicksByCity;
	}

	/**
	 * @return the filter
	 */
	public boolean isFilter() {
		return filter;
	}

	/**
	 * @param filter
	 *            the filter to set
	 */
	public void setFilter(boolean filter) {
		this.filter = filter;
	}

	/**
	 * @return the clicks
	 */
	public long getClicks() {
		return clicks;
	}

	/**
	 * @param clicks
	 *            the clicks to set
	 */
	public void setClicks(long clicks) {
		this.clicks = clicks;
	}

	/**
	 * @return the clicksByCountry
	 */
	public String getClicksByCountry() {
		return clicksByCountry;
	}

	/**
	 * @param clicksByCountry
	 *            the clicksByCountry to set
	 */
	public void setClicksByCountry(String clicksByCountry) {
		this.clicksByCountry = clicksByCountry;
	}
}
