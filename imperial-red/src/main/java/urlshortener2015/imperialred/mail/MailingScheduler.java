package urlshortener2015.imperialred.mail;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import urlshortener2015.imperialred.objects.Alert;
import urlshortener2015.imperialred.repository.AlertRepository;

@Component
public class MailingScheduler {
	
	@Autowired
	protected AlertRepository alertRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(MailingScheduler.class);
	
	
	/**
	 * Every 10 seconds, a new thread is created. It looks for the oldest alert
	 * in the database and, if older than today, sends a mail to the creator.
	 */
	@Async
	@Scheduled(fixedRate=10000)
	public void checkForAlerts() {
		Alert firstAlert = alertRepository.findOneOrderByDate();
		Date firstDate = firstAlert.getDate();
		Date now = new Date();
		
		/* If retrieved date is previous to now, process the alert */
		if (firstDate.compareTo(now)<=0) {
			logger.info("Processing alert set for " + firstDate + " at " + now);
			/*
			 * Sends mail
			 * TODO: this interaction should be SOAP
			 */
			MailSender ms = new MailSender(firstAlert);
			ms.send();
		}
	}
	
}
