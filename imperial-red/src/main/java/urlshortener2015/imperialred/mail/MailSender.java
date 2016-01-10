package urlshortener2015.imperialred.mail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import es.imred.soap.ProcessAlertRequest;
import es.imred.soap.ProcessAlertResponse;

@Endpoint
public class MailSender {
	
	private static final Logger logger = LoggerFactory.getLogger(MailSender.class);

	private static final String NAMESPACE_URI = "http://imred.es/soap";
	
	private final String MSG_1 = "Dear user, your shortened link is about to expire. If you " +
			"desire to extend it, please head to http://imred.es and check your links.\n" +
			"\nThis is your shortened link: http://imred.es/";
	private final String MSG_2 = "\n\nThanks for using Imperial Red's URL Shortener.";
	
	private String username;	
	private String password;
	
	private String mail;
	private String url;
	private String msgBody;
	
	/**
	 * When a MailSender is created, it is configured from the
	 * properties file.
	 */
	public MailSender() {
		Properties p = new Properties();
		try {
			InputStream input = new FileInputStream("src/main/resources/application.properties");
			p.load(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		username = p.getProperty("mail.user");
		password = p.getProperty("mail.password");
	}
	
	/**
	 * Given the url in the alert and the mail introduced by the user, it
	 * sends an email to that address informing of the near expiration.
	 */
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "processAlertRequest")
	@ResponsePayload
	public ProcessAlertResponse send(@RequestPayload ProcessAlertRequest request) {
    	ProcessAlertResponse response = new ProcessAlertResponse();
    	/* Gets url and mail from request */
    	mail = request.getMail();
		url = request.getHash();	
		msgBody = MSG_1 + url + MSG_2;

		/* Configures Gmail properties */
		Properties props = new Properties();
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		
		/* Sets an authenticator for opening session in Gmail account */
        Session session = Session.getDefaultInstance(props, 
        		new Authenticator() {
        			protected PasswordAuthentication getPasswordAuthentication() {
        				return new PasswordAuthentication(username, password);
        			}
        });
        
        try {
        	/* Creates the message sets its data */
        	Message msg = new MimeMessage(session);
        	msg.setFrom(new InternetAddress(username, "Imperial Red Admin"));
        	msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
        			mail, "Dear User"));
        	msg.setSubject("Your shortened link is about to expire");
        	msg.setText(msgBody);
        	
        	/* Sends the message */
        	Transport.send(msg);
        	logger.info("Mail sent to " + mail);
        	
        	/* Response code is set to 0 (OK) */
        	response.setCode("0");
        	return response;
        } catch (AddressException e) {
        	logger.info("AddressException when sending mail");
        	e.printStackTrace();
        } catch (MessagingException e) {
        	logger.info("MessagingException when sending mail");
        	e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
        	logger.info("UnsupportedEncodingException when sending mail");
        	e.printStackTrace();
		}
        /* Response code is set to 1 (error) */
        response.setCode("1");
        return response;
	}
}
