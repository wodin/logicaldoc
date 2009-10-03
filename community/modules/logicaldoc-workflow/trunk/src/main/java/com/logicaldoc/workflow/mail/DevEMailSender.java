package com.logicaldoc.workflow.mail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import javax.mail.internet.InternetAddress;

import com.logicaldoc.core.communication.EMail;
import com.logicaldoc.core.communication.EMailSender;

public class DevEMailSender extends EMailSender{

	private String destination;
	
	private PrintStream out;

	public void setDestination(String destination) {
		this.destination = destination;
	}
	
	private void println(String msg){
		out.println(msg);
	}
	
	private void println(){
		out.println();
	}
	 
	public void init(){
		try {
			File f = new File(this.destination);
			
			if(f.exists() == false)
				f.createNewFile();
			
			
			out = new PrintStream(new File(this.destination ));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	
	
	@Override
	public void send(EMail eMail) throws Exception {
		
		try {
			println("*** Dev-Message: New Message Arriving");
			println("=========================================");
			InternetAddress[] addresses = eMail.getAddresses();
			
			String sfx = "";
			for(InternetAddress address : addresses){
				sfx+=address.getAddress()+",";
			}
			
			println("Mail-To: " + sfx);
			println("Mail-Subject: " + eMail.getSubject() );
			println("Mail-Message: " + eMail.getMessageText() );
			
			println("**** ");
			println();
			println();
		}
		finally {
			
		}
	}
}
