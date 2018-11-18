package com.me.starter;

import java.util.Date;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;

import com.me.utility.PropertyUtil;

public class GMailConnector {

	private Session session;
	private Store store;
	private Folder folder;
	private String protocol;
	private String folderToAccess;
	private String host;
	
	public GMailConnector(){
		protocol = PropertyUtil.getProperty("PROTOCOL");
		folderToAccess = PropertyUtil.getProperty("FOLDER_TO_ACCESS");
		host = PropertyUtil.getProperty("HOST");
	}
	
	public boolean isLoggedIn() {
		if(store == null){
			return false;
		} else {
			System.out.println("Already logged in");
		}
		return store.isConnected();
	}

	/**
	 * to login to the mail host server
	 */
	public void login(String username, String password)
			throws Exception {
		URLName url = new URLName(protocol, host, 993, folderToAccess, username, password);

		if (!isLoggedIn()) {
			if (session == null) {
				Properties props = null;
				props = System.getProperties();
				props.setProperty("mail.imaps.partialfetch", "false");
				props.setProperty("mail.mime.base64.ignoreerrors", "true");
				session = Session.getInstance(props, null);
			}
			store = session.getStore(url);
			store.connect();
		}
		if (null == folder || !folder.isOpen()) {
			folder = store.getFolder(url);
			folder.open(Folder.READ_WRITE);
		}
	}

	/**
	 * to logout from the mail host server
	 */
	public void logout() throws MessagingException {
		if(null != folder && folder.isOpen()){
			folder.close(false);
		}
		if(null != store && store.isConnected()){
			store.close();
		}
		store = null;
		session = null;
	}

	public Message[] getMessages(Date frmoDate, Date toDate) throws MessagingException {
		SearchTerm olderThan = new ReceivedDateTerm(ComparisonTerm.LT, toDate);
		SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT, frmoDate);
		SearchTerm andTerm = new AndTerm(olderThan, newerThan);
		return folder.search(andTerm);
	}
}