package com.seleniumProject.Utils;


import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendMailSSLWithAttachment {
	private static Logging logger = new Logging();

	public void sendTestReportEmail() {

		// Create object of Property file
		Properties props = new Properties();

		props.put("mail.smtp.host", "smtp.gmail.com");

		props.put("mail.smtp.socketFactory.port", "25");

		// set socket factory
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		// set the authentication to true
		props.put("mail.smtp.auth", "true");

		// set the port of SMTP server
		props.put("mail.smtp.port", "25");

		props.put("mail.smtp.starttls.enable", "true");

		// This will handle the complete authentication
		Session session = Session.getDefaultInstance(props,

				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication("yashsrivastav89@gmail.com", "xxx");
					}

				});

		try {

			SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
			String current_time_str = time_formatter.format(System.currentTimeMillis());

			Message message = new MimeMessage(session);

			message.setFrom(new InternetAddress("yashsrivastav89@gmail.com"));

			// Set the recipient address
		
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("richa.bhatt@seleniumProject.com , pradeep.fuloria@seleniumProject.com"));
			// message.setRecipients(Message.RecipientType.TO,
			// InternetAddress.parse(ConstantLabel.emailToAyan));

			// message.setRecipients(Message.RecipientType.CC,
			// ConstantLabel.emailArray);

			message.setSubject("Today Test Report by =====QA Team === Report Time is ==== " + current_time_str);

			// Create object to add multi media type content
			BodyPart messageBodyPart1 = new MimeBodyPart();

			// Set the body of email
			messageBodyPart1.setText("Please find the attached Test Report_ " + current_time_str);

			// Create another object to add another content
			MimeBodyPart messageBodyPart2 = new MimeBodyPart();
			// MimeBodyPart messageBodyPart3 = new MimeBodyPart();
			MimeBodyPart extentRepoMessage = new MimeBodyPart();

			// Mention the file which you want to send
		
			
			String filename = DeleteLogsAndReports.lastmodifiedFilePath("logs");
			String extentReport = DeleteLogsAndReports.lastmodifiedFilePath("Reports");

			// Create data source and pass the filename
			DataSource source = new FileDataSource(filename);
			// DataSource logfile = new FileDataSource(logFile);
			DataSource extentRepo = new FileDataSource(extentReport);

			// set the handler
			messageBodyPart2.setDataHandler(new DataHandler(source));
			// messageBodyPart3.setDataHandler(new DataHandler(logfile));
			extentRepoMessage.setDataHandler(new DataHandler(extentRepo));

			messageBodyPart2.setFileName(filename);
			extentRepoMessage.setFileName(extentReport);

			// Create object of MimeMultipart class
			Multipart multipart = new MimeMultipart();

			// add body part 1
			multipart.addBodyPart(messageBodyPart2);
			// multipart.addBodyPart(messageBodyPart3);
			multipart.addBodyPart(extentRepoMessage);

			// add body part 2
			multipart.addBodyPart(messageBodyPart1);

			// set the content ..//
			message.setContent(multipart);

			// finally send the email
			Transport.send(message);
			logger.logInfo("=====Test Report Sent By =====");

		} catch (MessagingException e) {
			Logging.log.error("Some connection Issue while Report Email Process");

		}

	}
}