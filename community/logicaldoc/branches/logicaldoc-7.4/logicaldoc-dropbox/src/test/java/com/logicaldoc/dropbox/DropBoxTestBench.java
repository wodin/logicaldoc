package com.logicaldoc.dropbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;

public class DropBoxTestBench {

	public static void main(String[] args) throws IOException, URISyntaxException, DbxException {
		// String accessToken = authorization();
		// String accessToken =
		// "XgrknyluIWkAAAAAAAAAK66_qsBUhfwiD-NZWyo0mUwPogmvddWFzECtvK2iLiUV";
		String accessToken = "XgrknyluIWkAAAAAAAAAMQSk1zQW0vHhRK_d1pQhj74Iwa-vZck3UzvtVnzmGgNf";

		Dropbox client = new Dropbox();
		boolean entered = client.login(accessToken, Locale.ENGLISH);
		System.out.println("entered " + entered);
		System.out.println(client.getAccountName());

		if (!entered)
			accessToken = authorization();

		List<DbxEntry> entries = null;
		// entries = client.list("/");
		// for (DbxEntry entry : entries)
		// if (entry instanceof DbxEntry.File){
		// System.out.println("File " + entry.path);
		// }else if (entry instanceof DbxEntry.Folder){
		// System.out.println("Folder " + entry.path);
		// }

		entries = client.listFilesInTree("/");
		for (DbxEntry entry : entries)
			System.out.println(entry.path);

		// File file = new File("C:\\tmp\\SPECS.doc");
		// client.downloadFile("/test/" + file.getName(), file);
		// client.uploadFile(file, "/test/" + file.getName(), true);
	}

	public static String authorization() throws IOException, URISyntaxException {
		Dropbox client = new Dropbox();

		// This is for authorizing the LogicalDOC application just the first
		// time
		String authUrl = client.startAuthorization(Locale.ENGLISH);
		System.out.println("1. Go to: " + authUrl);
		System.out.println("2. Click \"Allow\" (you might have to log in first)");
		System.out.println("3. Copy the authorization code.");
		System.out.println("4. Paste the authorization code here:");

		java.awt.Desktop.getDesktop().browse(new java.net.URI(authUrl));

		String authorizationCode = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();

		String accessToken = client.finishAuthorization(authorizationCode, Locale.ENGLISH);
		System.out.println("Your access token is: " + accessToken);

		return accessToken;
	}
}