package urlshortener2015.imperialred.repository;

/*import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import urlshortener2015.imperialred.config.MongoConfigTest;
import urlshortener2015.imperialred.config.MongoConfiguration;

import static org.junit.Assert.assertEquals;
import static urlshortener2015.imperialred.repository.fixture.ClickFixture.click;
import static urlshortener2015.imperialred.repository.fixture.ShortURLFixture.url1;

@ContextConfiguration(classes={MongoConfigTest.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ClickRepositoryTests {

    @Autowired
    private ClickRepository clickRepository;

    @Test
    public void thatSavePersistsTheClickURL() throws Exception {
        //Saves the test Click
        clickRepository.save(click(url1()));

        int count = (int) clickRepository.count();

        assertEquals(count,1);

    }


    @After
    //After every test, we destroy the data.
    public void finishTest() throws Exception{
        clickRepository.deleteAll();
    }
}*/