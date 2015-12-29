package urlshortener2015.imperialred.web.fixture;

import java.util.Date;

import urlshortener2015.imperialred.objects.ShortURL;


public class ShortURLFixture {

	public static ShortURL someUrl() {
		return new ShortURL("someKey", "http://example.com/", null, new Date(), null,
				null, 307, "52.29.234.196", null);
	}
}
