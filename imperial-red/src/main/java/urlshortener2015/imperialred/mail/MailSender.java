package urlshortener2015.imperialred.mail;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import urlshortener2015.imperialred.objects.Alert;

public class MailSender {
	
	private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
	
	private final String MSG_1 = "Dear user, your shortened link is about to expire. If you " +
			"desire to extend it, please head to http://ired.ml:8090 and check your links.\n" +
			"\nThis is your shortened link: ";
	private final String MSG_2 = "\n\nThanks for using Imperial Red's URL Shortener.";
	
	private String mail;
	private String url;
	private String msgBody;
	
	public MailSender(Alert a) {
		mail = a.getMail();
		url = a.getUrl();	
		msgBody = MSG_1 + url + MSG_2;
	}
	
	/**
	 * Given the url in the alert and the mail introduced by the user, it
	 * sends an email to that address informing of the near expiration.
	 */
	public void send() {
		/* Message needs a session object. This configures it by default */
		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        
        try {
        	/* Creates the message sets its data */
        	Message msg = new MimeMessage(session);
        	msg.setFrom(new InternetAddress("ired@gmail.com", "Imperial Red Admin"));
        	msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
        			mail, "Dear User"));
        	msg.setSubject("Your shortened link is about to expire");
        	msg.setText(msgBody);
        	
        	/* Sends the message */
        	Transport.send(msg);
        } catch (AddressException e) {
        	logger.info("AddressException when sending mail");
        } catch (MessagingException e) {
        	logger.info("MessagingException when sending mail");
        } catch (UnsupportedEncodingException e) {
        	logger.info("UnsupportedEncodingException when sending mail");
		}
	}
}
