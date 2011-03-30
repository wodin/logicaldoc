package com.logicaldoc.webservice;

import javax.activation.DataHandler;

import com.logicaldoc.webservice.auth.AuthClient;
import com.logicaldoc.webservice.document.DocumentClient;
import com.logicaldoc.webservice.document.WSDocument;
import com.logicaldoc.webservice.security.SecurityClient;

public class Main {
	public static void main(String[] args) throws Exception {
		AuthClient auth = new AuthClient("http://localhost:9080/logicaldoc/services/Auth");
		DocumentClient documentClient = new DocumentClient("http://localhost:9080/logicaldoc/services/Document");
		// FolderClient folderClient = new
		// FolderClient("http://localhost:9080/logicaldoc/services/Folder");
		// SearchClient searchClient = new
		// SearchClient("http://localhost:9080/logicaldoc/services/Search");
		// SystemClient systemClient = new
		// SystemClient("http://localhost:9080/logicaldoc/services/System");
//		SecurityClient securityClient = new SecurityClient("http://localhost:9080/logicaldoc/services/Security");

		// Open a session
		// This is a user 'author' with different permissions than the authors.
		// String sid = auth.login("matteo", "matteo1982");
		String sid = auth.login("admin", "admin");
		System.out.println("sid: " + sid);

		// WSUser newUser = new WSUser();
		// newUser.setId(0);
		// newUser.setName("pippo");
		// newUser.setEmail("ciccio@acme.com");
		// newUser.setUserName("pippo");
		// newUser.setFirstName("ciccio");
		// securityClient.storeUser(sid, newUser);

		// WSUser[] users = securityClient.listUsers(sid);
		// WSUser editingUser = null;
		// for (WSUser wsUser : users) {
		// if (wsUser.getId() == 3) {
		// editingUser = wsUser;
		// break;
		// }
		// }
		//
		// if (editingUser != null) {
		// editingUser.setGroupIds(new long[] { 3 });
		// securityClient.storeUser(sid, editingUser);
		// }
		//
		// users = securityClient.listUsers(sid);
		// for (WSUser wsUser : users) {
		// System.out.println("--- " + wsUser.getId());
		// System.out.println("--- " + wsUser.getUserName());
		// System.out.println("--- " + wsUser.getEmail());
		// System.out.println("--- " + wsUser.getStreet());
		// System.out.println("--- " + wsUser.getGroupIds()[0]);
		// System.out.println("------------------------------------");
		// }

		// WSGroup newGroup = new WSGroup();
		// newGroup.setName("pippo");
		// newGroup.setDescription("pippo desc");
		// newGroup.setInheritGroupId(2L);
		// securityClient.storeGroup(sid, newGroup);

		// WSGroup editGroup = new WSGroup();
		// editGroup.setId(5);
		// editGroup.setName("ciccio");
		// editGroup.setDescription("ciccio desc");
		// securityClient.storeGroup(sid, editGroup);

		// securityClient.deleteGroup(sid, 6);
		//
		// WSGroup[] groups = securityClient.listGroups(sid);
		// WSGroup editingGroup = null;
		// for (WSGroup wsGroup : groups) {
		// if (wsGroup.getId() == 2) {
		// editingGroup = wsGroup;
		// break;
		// }
		// }
		// if (editingGroup != null) {
		// editingGroup.setUserIds(new long[] { 2, 3 });
		// securityClient.storeGroup(sid, editingGroup);
		// }
		//
		// for (WSGroup wsGroup : groups) {
		// System.out.println("--- " + wsGroup.getId());
		// System.out.println("--- " + wsGroup.getName());
		// System.out.println("--- " + wsGroup.getDescription());
		// System.out.println("+++++++++++++++++++++++++++++++++++++");
		// }

		// WSFolder[] path = folderClient.getPath(sid, 20L);
		// System.out.println("\n");
		// for (WSFolder wsFolder : path) {
		// System.out.print(wsFolder.getName() + "/");
		// }
		//
		// path = folderClient.getPath(sid, Folder.ROOTID);
		// System.out.println("\n");
		// for (WSFolder wsFolder : path) {
		// System.out.print(wsFolder.getName() + "/");
		// }

		// WSFolder newFolder = new WSFolder();
		// newFolder.setName("ddddd");
		// newFolder.setDescription("new folder ddddd");
		// newFolder.setParentId(5);
		// newFolder = folderClient.create(sid, newFolder);
		// WSFolder[] folders = folderClient.list(sid, 5);
		// for (WSFolder folder : folders) {
		// System.out.println("folder id: " + folder.getId());
		// System.out.println("folder name : " + folder.getName());
		// }
		//
		// System.out.println("folder id : " + newFolder.getId());
		// System.out.println("folder desc: " + newFolder.getDescription());

		// WSDocument[] docs = documentClient.getDocuments(sid, new
		// long[]{100,101,102,103});
		// for (WSDocument wsDocument : docs) {
		// System.out.println("doc: "+wsDocument.getTitle());
		// }

		WSDocument doc = documentClient.getDocument(sid, 1);
		System.out.println("rating: " + doc.getRating());
		doc.setRating(5);
		documentClient.update(sid, doc);

		DataHandler data = documentClient.getContent(sid, 1);
		doc.setRating(4);
		doc = documentClient.create(sid, doc, data);
		System.out.println("rating: " + doc.getRating());

		// DataHandler data = documentClient.getContent(sid, 68);
		// System.out.println("data: " + data.toString());

		auth.logout(sid);
	}
}
