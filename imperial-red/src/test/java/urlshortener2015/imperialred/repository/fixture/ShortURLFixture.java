package urlshortener2015.imperialred.repository.fixture;


import urlshortener2015.imperialred.objects.ShortURL;

public class ShortURLFixture {

	public static ShortURL url1() {
		return new ShortURL("1", "http://www.unizar.es/", null, null, null, null, null, null, null, null, false, null);
	}

	public static ShortURL url1modified() {
		return new ShortURL("1", "http://www.unizar.org/", null, null, null, null, null, null, null, null, false, null);
	}

	public static ShortURL url2() {
		return new ShortURL("2", "http://www.unizar.es/", null, null, null, null, null, null, null, null, false, null);
	}

	public static ShortURL url3() {
		return new ShortURL("3", "http://www.google.es/", null, null, null, null, null, null, null, null, false, null);
	}


}
