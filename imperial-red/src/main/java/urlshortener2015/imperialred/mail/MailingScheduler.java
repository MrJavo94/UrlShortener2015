package urlshortener2015.imperialred.mail;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import es.imred.soap.ProcessAlertRequest;
import es.imred.soap.ProcessAlertResponse;
import urlshortener2015.imperialred.objects.Alert;
import urlshortener2015.imperialred.repository.AlertRepository;

@Component
public class MailingScheduler extends WebServiceGatewaySupport {
	
	@Autowired
	protected AlertRepository alertRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(MailingScheduler.class);
	
	
	/**
	 * Every <fixedRate> ms, a new thread is created. It looks for the oldest alert
	 * in the database and, if older than today, sends a mail to the creator.
	 */
	@Async
	@Scheduled(initialDelay=10000, fixedRate=60000)
	public void checkForAlerts() {
		/* Looks for the first existing alert */
		Alert firstAlert = alertRepository.findFirstByOrderByDate();
		Date firstDate = firstAlert.getDate();
		Date now = new Date();
		
		/* If retrieved date is previous to now, process the alert */
		if (firstDate.compareTo(now)<=0) {
			logger.info("Processing alert set for " + firstDate + " at " + now);
			
			/* Creates SOAP request */
			ProcessAlertRequest request = new ProcessAlertRequest();
			request.setMail(firstAlert.getMail());
			request.setHash(firstAlert.getHash());
			logger.info("Created request " + request.getMail() + ", " + request.getHash());
			
			/* Executes SOAP request and waits for a response */
			ProcessAlertResponse response = (ProcessAlertResponse) getWebServiceTemplate()
					.marshalSendAndReceive("http://localhost:8090/ws", request);
			String responseCode = response.getCode();
			logger.info("Received response code " + responseCode);
			
			/*
			 * Only deletes alert if sent
			 * XXX: This may be dangerous if, for some reason, a mail is not sent
			 */
			if (responseCode.equals("0")) {
				alertRepository.delete(firstAlert);
			} else {
				logger.info("Mail could not be sent");
			}
		}
	}
	
}
