package com.logicaldoc.dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropbox.core.DbxWriteMode;

/**
 * Our Dropbox facade
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.0
 */
public class Dropbox {
	protected Logger log = LoggerFactory.getLogger(Dropbox.class);

	// Get your app key and secret from the Dropbox developers website -
	// https://www.dropbox.com/developers/apps
	final private String APP_KEY = "s7erzz9am7ikbl2";

	final private String APP_SECRET = "9shfsbeilgfizam";

	private String accessToken;

	private DbxClient client;

	/**
	 * Generates the authorization URL where the user has to allow LogicalDOC
	 * and gets an authorization code to be used then with finishAuthorization.
	 * 
	 * @param locale
	 * @return The page to be shown to the user to allow the app to access
	 */
	public String startAuthorization(Locale locale) {
		DbxWebAuthNoRedirect webAuth = prepareWebAuth(locale);
		return webAuth.start();
	}

	/**
	 * Finishes the authorization process. The returned access token can be
	 * saved for future use.
	 * 
	 * @param authorizationCode The authorization code that the user see in the
	 *        authorization page
	 * @param locale
	 * @return The access token
	 */
	public String finishAuthorization(String authorizationCode, Locale locale) {
		DbxWebAuthNoRedirect webAuth = prepareWebAuth(locale);
		try {
			DbxAuthFinish authFinish = webAuth.finish(authorizationCode);
			return authFinish.accessToken;
		} catch (DbxException e) {
			e.printStackTrace();
		}
		return null;
	}

	private DbxWebAuthNoRedirect prepareWebAuth(Locale locale) {
		DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
		DbxRequestConfig config = new DbxRequestConfig("LogicalDOC", locale.toString());
		DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
		return webAuth;
	}

	public boolean login(String accessToken, Locale locale) {
		try {
			DbxRequestConfig config = new DbxRequestConfig("LogicalDOC", locale.toString());
			this.client = new DbxClient(config, accessToken);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			return false;
		}

		// Test the connection
		try {
			list("/");
			return true;
		} catch (Throwable t) {
			return false;
		}
	}

	public List<DbxEntry> list(String path) throws DbxException {
		String normalizedPath = path;
		if (normalizedPath.endsWith("/"))
			normalizedPath = normalizedPath.substring(0, path.length() - 1);
		if (!normalizedPath.startsWith("/"))
			normalizedPath = "/" + normalizedPath;

		List<DbxEntry> list = new ArrayList<DbxEntry>();
		DbxEntry.WithChildren listing = client.getMetadataWithChildren(normalizedPath);
		list = listing.children;
		return list;
	}

	public List<DbxEntry> find(String basePath, String query) throws DbxException {
		return client.searchFileAndFolderNames(basePath, query);
	}

	public List<DbxEntry> listFilesInTree(String basePath) throws DbxException {
		List<DbxEntry> files = new ArrayList<DbxEntry>();
		if (basePath.endsWith("/"))
			basePath.substring(0, basePath.length() - 1);
		if (!basePath.startsWith("/"))
			basePath = "/" + basePath;
		treeList(basePath, files);
		return files;
	}

	private void treeList(String parent, List<DbxEntry> files) throws DbxException {
		List<DbxEntry> list = list(parent);
		for (DbxEntry entry : list) {
			if (entry instanceof DbxEntry.Folder)
				treeList(entry.path, files);
			else
				files.add(entry);
		}
	}

	public DbxEntry get(String path) throws DbxException {
		return client.getMetadata(path);
	}

	public boolean downloadFile(String src, File out) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(out);
		try {
			client.getFile(src, null, outputStream);
			return true;
		} catch (DbxException e) {
			log.error(e.getMessage(), e);
		} finally {
			outputStream.flush();
			outputStream.close();
		}
		return false;
	}

	public boolean uploadFile(File inputFile, String path, boolean overwrite) throws IOException {
		FileInputStream inputStream = new FileInputStream(inputFile);
		try {
			if (!path.startsWith("/"))
				path = "/" + path;
			DbxWriteMode mode = overwrite ? DbxWriteMode.force() : DbxWriteMode.add();
			DbxEntry.File uploadedFile = client.uploadFile(path, mode, inputFile.length(), inputStream);
			return uploadedFile != null;
		} catch (DbxException e) {
			log.error(e.getMessage(), e);
		} finally {
			inputStream.close();
		}
		return false;
	}

	public String getAccountName() {
		try {
			return client.getAccountInfo().displayName;
		} catch (DbxException e) {
			return null;
		}
	}

	public String getAccessToken() {
		return accessToken;
	}
}
