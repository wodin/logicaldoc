package com.logicaldoc.cmis;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.commons.lang.StringUtils;

public class SampleClient {

	private static Session session;

	// org.apache.chemistry.opencmis.binding.spi.type=atompub
	// org.apache.chemistry.opencmis.binding.atompub.url=http://localhost:9080/logicaldoc/service/cmis
	// org.apache.chemistry.opencmis.user=admin
	// org.apache.chemistry.opencmis.password=12345678
	// org.apache.chemistry.opencmis.binding.compression=true
	// org.apache.chemistry.opencmis.binding.cookies=true
	//
	//
	// org.apache.chemistry.opencmis.binding.spi.type=atompub
	// org.apache.chemistry.opencmis.binding.atompub.url=http://192.168.2.6:8080/alfresco/service/cmis
	// org.apache.chemistry.opencmis.user=admin
	// org.apache.chemistry.opencmis.password=admin
	// org.apache.chemistry.opencmis.binding.compression=true
	// org.apache.chemistry.opencmis.binding.cookies=true

	private static final String CONNECTION_URL = "http://localhost:9080/logicaldoc/service/cmis";

	private static final String TEST_FOLDER_NAME = "/Default/pippo";

	private static final String TEST_DOCUMENT_NAME_1 = "Piano 2014.odt";

	public static void main(String[] args) {
		Folder root = connect();
		root.getName();
		// cleanup(root, TEST_FOLDER_NAME);
		// Folder newFolder = createFolder(root, TEST_FOLDER_NAME);
		// createDocument(newFolder, TEST_DOCUMENT_NAME_1);
		// createDocument(newFolder, TEST_DOCUMENT_NAME_2);
		// System.out.println("+++ List Folder +++");
		// listFolder(0, newFolder);
		// DeleteDocument(newFolder, "/" + TEST_DOCUMENT_NAME_2);
		// System.out.println("+++ List Folder +++");
		// listFolder(0, newFolder);
		CmisObject object = session.getObjectByPath(TEST_FOLDER_NAME);
		System.out.println(object.getId() + " " + object.getName());
		
		object = session.getObjectByPath(TEST_FOLDER_NAME + "/" + TEST_DOCUMENT_NAME_1);
		System.out.println(object.getId() + " " + object.getProperty(TypeManager.PROP_TITLE).getValues().get(0));
	}

	/**
	 * Clean up test folder before executing test
	 * 
	 * @param target
	 * @param delFolderName
	 */
	private static void cleanup(Folder target, String delFolderName) {
		try {
			CmisObject object = session.getObjectByPath(target.getPath() + delFolderName);
			Folder delFolder = (Folder) object;
			delFolder.deleteTree(true, UnfileObject.DELETE, true);
		} catch (CmisObjectNotFoundException e) {
			System.err.println("No need to clean up.");
		}
	}

	/**
	 * 
	 * @param target
	 */
	private static void listFolder(int depth, Folder target) {
		String indent = StringUtils.repeat("\t", depth);
		for (Iterator<CmisObject> it = target.getChildren().iterator(); it.hasNext();) {
			CmisObject o = it.next();
			if (BaseTypeId.CMIS_DOCUMENT.equals(o.getBaseTypeId())) {
				System.out.println(indent + "[Docment] " + o.getName());
			} else if (BaseTypeId.CMIS_FOLDER.equals(o.getBaseTypeId())) {
				System.out.println(indent + "[Folder] " + o.getName());
				listFolder(++depth, (Folder) o);
			}
		}

	}

	/**
	 * Delete test document
	 * 
	 * @param target
	 * @param delDocName
	 */
	private static void DeleteDocument(Folder target, String delDocName) {
		try {
			CmisObject object = session.getObjectByPath(target.getPath() + delDocName);
			Document delDoc = (Document) object;
			delDoc.delete(true);
		} catch (CmisObjectNotFoundException e) {
			System.err.println("Document is not found: " + delDocName);
		}
	}

	/**
	 * Create test document with content
	 * 
	 * @param target
	 * @param newDocName
	 */
	private static void createDocument(Folder target, String newDocName) {
		Map<String, String> props = new HashMap<String, String>();
		props.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
		props.put(PropertyIds.NAME, newDocName);
		System.out.println("This is a test document: " + newDocName);
		String content = "aegif Mind Share Leader Generating New Paradigms by aegif corporation.";
		byte[] buf = null;
		try {
			buf = content.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		ByteArrayInputStream input = new ByteArrayInputStream(buf);
		ContentStream contentStream = session.getObjectFactory().createContentStream(newDocName, buf.length,
				"text/plain; charset=UTF-8", input);
		target.createDocument(props, contentStream, VersioningState.MAJOR);
	}

	/**
	 * Create test folder directly under target folder
	 * 
	 * @param target
	 * @param createFolderName
	 * @return newly created folder
	 */
	private static Folder createFolder(Folder target, String newFolderName) {
		Map<String, String> props = new HashMap<String, String>();
		props.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
		props.put(PropertyIds.NAME, newFolderName);
		Folder newFolder = target.createFolder(props);
		return newFolder;
	}

	/**
	 * Connect to alfresco repository
	 * 
	 * @return root folder object
	 */
	private static Folder connect() {
		SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put(SessionParameter.USER, "admin");
		parameter.put(SessionParameter.PASSWORD, "12345678");
		parameter.put(SessionParameter.ATOMPUB_URL, CONNECTION_URL);
		parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
		parameter.put(SessionParameter.REPOSITORY_ID, Long.toString(5L));

		session = sessionFactory.createSession(parameter);
		return session.getRootFolder();
	}
}