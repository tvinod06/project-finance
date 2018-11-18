package com.me.init;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.json.JSONArray;

import com.me.utility.PropertyUtil;

public class DownloadStatements {

	public static void main(String[] args) {
		DownloadStatements att = new DownloadStatements();
		att.getStatements();
	}

	protected void getStatements() {

		GMailConnector gmail = new GMailConnector();
		try {

			JSONArray emails = new JSONArray(PropertyUtil.getProperty("EMAILS"));
			for (int j = 0; j < emails.length(); j++) {
				JSONArray email = new JSONArray(PropertyUtil.getProperty(emails.getString(j)));
				gmail.login(email.getString(0), email.getString(1));
				System.out.println("login success");
				Message[] msgs = gmail.getMessages(new Date(1538332200000L), new Date(1541010599000L));
				System.out.println("Message Count: " + msgs.length);
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH mm ss");
				File folder = new File("./" + email.getString(0) + "_" + simpleDateFormat.format(new Date()));
				if (!folder.exists()) {
					folder.mkdirs();
				}
				for (Message msg : msgs) {
					if (msg.getSubject().toLowerCase().contains("statement")) {
						System.out.println(msg.getSubject() + " " + msg.getContent());
						MimeMultipart kk = (MimeMultipart) msg.getContent();
						for (int i = 0; i < kk.getCount(); i++) {
							BodyPart part = kk.getBodyPart(i);
							if (part.getFileName() != null) {
								String disposition = part.getDisposition();

								if ((disposition != null) && ((disposition.equalsIgnoreCase(Part.ATTACHMENT)
										|| (disposition.equalsIgnoreCase(Part.INLINE))))) {
									MimeBodyPart mimeBodyPart = (MimeBodyPart) part;
									File fileToSave = new File(
											folder.getPath() + File.separator + mimeBodyPart.getFileName());
									mimeBodyPart.saveFile(fileToSave);
									System.out.println(mimeBodyPart.getFileName());
								}
							}
						}
					}
				}
				gmail.logout();
				System.out.println("logout success");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (gmail.isLoggedIn()) {
				try {
					gmail.logout();
					System.out.println("logout success");
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
