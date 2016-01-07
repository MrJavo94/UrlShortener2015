package urlshortener2015.imperialred.repository.fixture;

import urlshortener2015.imperialred.objects.Click;
import urlshortener2015.imperialred.objects.ShortURL;

public class ClickFixture {

	public static Click click(ShortURL su) {
		return new Click(null, su.getHash(), null, null, null, null, null, null,
				null, new Float(0.0), new Float(0.0));
	}
}
