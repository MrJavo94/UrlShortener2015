package urlshortener2015.imperialred.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MailingScheduler {
	
	private static final Logger logger = LoggerFactory.getLogger(MailingScheduler.class);
	
	@Async
	@Scheduled(fixedRate=10000)
	public void checkForAlerts() {
		logger.info("Hello! Im asynchronous :3");
	}
	
}
