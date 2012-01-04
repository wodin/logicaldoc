package com.logicaldoc.webservice;

import javax.activation.DataHandler;

import com.logicaldoc.webservice.auth.AuthClient;
import com.logicaldoc.webservice.document.DocumentClient;
import com.logicaldoc.webservice.folder.FolderClient;
import com.logicaldoc.webservice.search.SearchClient;
import com.logicaldoc.webservice.security.SecurityClient;
import com.logicaldoc.webservice.system.SystemClient;

public class Main {
	public static void main(String[] args) throws Exception {
		String base = "http://localhost:9080/logicaldoc/services";
		AuthClient auth = new AuthClient(base + "/Auth");
		DocumentClient documentClient = new DocumentClient(base + "/Document");
		FolderClient folderClient = new FolderClient(base + "/Folder");
		SearchClient searchClient = new SearchClient(base + "/Search");
		SystemClient systemClient = new SystemClient(base + "/System");
		SecurityClient securityClient = new SecurityClient(base + "/Security");

		// Open a session
		String sid = auth.login("admin", "12345678");
		System.out.println("sid: " + sid);

		// WSUser wsUserTest = new WSUser();
		// wsUserTest.setName("marco");
		// wsUserTest.setEmail("marco@acme.com");
		// wsUserTest.setUserName("marco");
		// wsUserTest.setFirstName("alle");
		// long[] ids = { 2, 3 };
		// wsUserTest.setGroupIds(ids);
		//
		// Long userId = securityClient.storeUser(sid, wsUserTest);
		// System.out.println("user id: " + userId);
		//
		// securityClient.changePassword(sid, userId, null, "marco1982");

		// securityClient.deleteUser(sid, 4);

		// WSUser user = securityClient.getUser(sid, 2L);
		// user.setCity("Modena");
		// user.setPostalcode("41125");
		// long[] ids = { 4 };
		// user.setGroupIds(ids);
		// securityClient.storeUser(sid, user);

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
		// WSUser[] users = securityClient.listUsers(sid);
		// for (WSUser wsUser : users) {
		// System.out.println("--- " + wsUser.getId());
		// System.out.println("--- " + wsUser.getUserName());
		// System.out.println("--- " + wsUser.getEmail());
		// System.out.println("--- " + wsUser.getStreet());
		// System.out.println("--- " + wsUser.getGroupIds()[0]);
		// System.out.println("------------------------------------");
		// }

		// WSGroup newGroup = new WSGroup();
		// newGroup.setName("gruppo3");
		// newGroup.setDescription("gruppo3 desc");
		// newGroup.setInheritGroupId(2L);
		// newGroup.setUserIds(new long[] { 4, 6 });
		// Long grpId = securityClient.storeGroup(sid, newGroup);
		// System.out.println("group id: " + grpId);

		// WSGroup editGroup = new WSGroup();
		// editGroup.setId(5);
		// editGroup.setName("ciccio");
		// editGroup.setDescription("ciccio desc");
		// securityClient.storeGroup(sid, editGroup);
		//
		// securityClient.deleteGroup(sid, 14);
		// securityClient.deleteGroup(sid, 15);

		// WSGroup group = securityClient.getGroup(sid, 16L);
		// group.setName("pippo");
		// group.setDescription("pippoc desc");
		// securityClient.storeGroup(sid, group);
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

		// for (WSGroup wsGroup : groups) {
		// System.out.println("--- " + wsGroup.getId());
		// System.out.println("--- " + wsGroup.getName());
		// System.out.println("--- " + wsGroup.getDescription());
		// System.out.println("--- " + wsGroup.getUserIds());
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

		// WSDocument doc = documentClient.getDocument(sid, 1);
		// System.out.println("rating: " + doc.getRating());
		// doc.setRating(5);
		// documentClient.update(sid, doc);
		//
		// DataHandler data = documentClient.getContent(sid, 1);
		// doc.setRating(4);
		// doc = documentClient.create(sid, doc, data);
		// System.out.println("rating: " + doc.getRating());

		 DataHandler data = documentClient.getContent(sid, 5561);
		 System.out.println("data: " + data.toString());

		// SystemInfo info = systemClient.getInfo(sid);
		// System.out.println("installation id: " + info.getInstallationId());
		// System.out.println("product name: " + info.getProductName());
		// for (String feature : info.getFeatures()) {
		// System.out.println("feature: " + feature);
		// }
		//
		// for (WSParameter param : systemClient.getStatistics(sid)) {
		// System.out.println("name: " + param.getName());
		// System.out.println("value: " + param.getValue());
		// System.out.println("------------------------------");
		// }
		//
		// for (String lang : systemClient.getLanguages(sid)) {
		// System.out.println("lang: " + lang);
		// System.out.println("++++++++++++++++++++++++++++++++");
		// }

		// WSDocument[] documents = searchClient.findByFilename(sid,
		// "logicaldoc-user_manual-en.odt");
		// System.out.println("---- " + documents.length);
		//
		// List<WSDocument> docsList = Arrays.asList(documents);
		// for (WSDocument doc : docsList) {
		// System.out.println("title: " + doc.getTitle());
		// System.out.println("custom id: " + doc.getCustomId());
		// System.out.println("version: " + doc.getVersion());
		// System.out.println("++++++++++++++++++++++++++++++++");
		// }
		//
		// documents = searchClient.findByFilename(sid, "marketing.txt");
		// System.out.println("---- " + documents.length);

		// WSFolder[] folders = searchClient.findFolders(sid, "xxx");
		// System.out.println("---- " + folders.length);
		// List<WSFolder> foldersList = Arrays.asList(folders);
		// for (WSFolder folder : foldersList) {
		// System.out.println("id: " + folder.getId());
		// System.out.println("title: " + folder.getName());
		// System.out.println("++++++++++++++++++++++++++++++++");
		// }

		// WSDocument[] documents = searchClient.findByTag(sid, "abc");
		// System.out.println("---- " + documents.length);
		// List<WSDocument> docsList = Arrays.asList(documents);
		// for (WSDocument doc : docsList) {
		// System.out.println("title: " + doc.getTitle());
		// System.out.println("custom id: " + doc.getCustomId());
		// System.out.println("version: " + doc.getVersion());
		// System.out.println("++++++++++++++++++++++++++++++++");
		// }

		// for (String tag : searchClient.getTags(sid)) {
		// System.out.println("tag: " + tag);
		// System.out.println("++++++++++++++++++++++++++++++++");
		// }

		// for (TagCloud tag : searchClient.getTagCloud(sid)) {
		// System.out.println("tag: " + tag.getTag());
		// System.out.println("tag count: " + tag.getCount());
		// System.out.println("tag scale: " + tag.getScale());
		// System.out.println("++++++++++++++++++++++++++++++++");
		// }

		// FulltextSearchOptions opt = new FulltextSearchOptions();
		// opt.setLanguage("it");
		// opt.setExpression("documento");
		// opt.setExpressionLanguage("it");
		// opt.setTemplate(-98L);
		// opt.setSizeMin(50000L);
		// opt.setType(SearchOptions.TYPE_FULLTEXT);
		// opt.setMaxHits(2);
		// WSSearchResult result = searchClient.find(sid, opt);
		// System.out.println("---- " + result.getHits().length);
		// for (HitImpl hit : result.getHits()) {
		// System.out.println("hit customid: " + hit.getCustomId());
		// System.out.println("hit folderid: " + hit.getFolderId());
		// System.out.println("hit title: " + hit.getTitle());
		// System.out.println("hit creation: " + hit.getCreation());
		// System.out.println("************************");
		// }

		// WSFolder folder = folderClient.getFolder(sid, 6);
		// System.out.println("parent id: " + folder.getParentId());
		// folderClient.move(sid, 6, 13);
		// folder = folderClient.getFolder(sid, 6);
		// System.out.println("parent id: " + folder.getParentId());

		// WSFolder wsFolderTest = new WSFolder();
		// wsFolderTest.setName("new folder");
		// wsFolderTest.setDescription("descr folder test");
		// wsFolderTest.setParentId(6);
		//
		// WSFolder wsFolder = folderClient.create(sid, wsFolderTest);
		// System.out.println("folder name: "+wsFolder.getName());
		// System.out.println("folder parentid: "+wsFolder.getParentId());

		// folderClient.delete(sid, 6);

		// folderClient.rename(sid, 14, "paperino");
		// WSFolder wsFolder = folderClient.getFolder(sid, 14);
		// System.out.println("folder name: " + wsFolder.getName());

		// WSFolder[] folders = folderClient.listChildren(sid, 13);
		// for (WSFolder wsFolder : folders) {
		// System.out.println("folder id: " + wsFolder.getId());
		// System.out.println("folder name: " + wsFolder.getName());
		// System.out.println("folder descr: " + wsFolder.getDescription());
		// System.out.println("**************************************");
		// }

		// WSFolder[] folders = folderClient.getPath(sid, 14);
		// for (WSFolder wsFolder : folders) {
		// System.out.println("folder id: " + wsFolder.getId());
		// System.out.println("folder name: " + wsFolder.getName());
		// System.out.println("folder descr: " + wsFolder.getDescription());
		// System.out.println("**************************************");
		// }

		// folderClient.grantGroup(sid, 13, 2, 4091, true);
		// folderClient.grantGroup(sid, 13, 3, 4091, true);
		// folderClient.grantGroup(sid, 13, -20, 0, true);
		// folderClient.grantUser(sid, 13, 2, 0, false);
		// Right[] rights = folderClient.getGrantedUsers(sid, 14);
		// System.out.println("--- " + rights.length);
		// for (Right right : rights) {
		// System.out.println("+++ " + right.getId());
		// }

		// WSDocument wsDoc = documentClient.getDocument(sid, 1);
		// wsDoc.setId(0);
		// wsDoc.setTitle("document test");
		// wsDoc.setCustomId("xxxxxxx");
		// wsDoc.setFolderId(14L);
		// DataHandler data = documentClient.getContent(sid, 1);
		// File file = new
		// File("/C:/Users/Matteo/Desktop/doctest/signdoc_en.pdf");
		// documentClient.create(sid, wsDoc, data);
		//
		// WSDocument[] docs = documentClient.list(sid, 14);
		// for (WSDocument wsDocument : docs) {
		// System.out.println("doc id: " + wsDocument.getId());
		// System.out.println("doc title: " + wsDocument.getTitle());
		// }

		// documentClient.delete(sid, 32);

		// WSDocument[] docs = documentClient.getDocuments(sid, new
		// long[]{100,101,102,103});
		// for (WSDocument wsDocument : docs) {
		// System.out.println("doc: "+wsDocument.getTitle());
		// }

		// WSDocument doc = documentClient.getDocument(sid, 1);
		// System.out.println("rating: " + doc.getRating());
		// doc.setRating(5);
		// documentClient.update(sid, doc);
		//
		// DataHandler data = documentClient.getContent(sid, 1);
		// doc.setRating(4);
		// doc = documentClient.create(sid, doc, data);
		// System.out.println("rating: " + doc.getRating());

		// DataHandler data = documentClient.getContent(sid, 68);
		// System.out.println("data: " + data.toString());

		// documentClient.lock(sid, 30);
		// WSDocument doc = documentClient.getDocument(sid, 30);
		// System.out.println("status: " + doc.getStatus());
		// System.out.println("locked user id: " +
		// doc.getLockUserId().longValue());

		// documentClient.move(sid, 30, 13);
		// WSDocument doc = documentClient.getDocument(sid, 30);
		// System.out.println("folderId: " + doc.getFolderId());

		// documentClient.unlock(sid, 30);
		// WSDocument doc = documentClient.getDocument(sid, 30);
		// System.out.println("status: " + doc.getStatus());
		// System.out.println("locked user id: " +
		// doc.getLockUserId().longValue());

		// documentClient.rename(sid, 30, "pluto");
		// WSDocument wsDoc = documentClient.getDocument(sid, 30);
		// System.out.println("doc title: " + wsDoc.getTitle());

		// WSDocument[] docs = documentClient.getDocuments(sid, new Long[] {
		// 55L, 30L, 32L, 29L });
		// for (WSDocument wsDocument : docs) {
		// System.out.println("doc: " + wsDocument.getTitle());
		// }

		// WSDocument doc = documentClient.getDocument(sid, 27);
		// System.out.println("rating: " + doc.getRating());
		// doc.setRating(5);
		// doc.setSource("xyzxxx");
		// doc.setCustomId("aaaabbbbb");
		// documentClient.update(sid, doc);
		// doc = documentClient.getDocument(sid, 27);
		// System.out.println("rating: " + doc.getRating());
		// System.out.println("source: " + doc.getSource());
		// System.out.println("customid: " + doc.getCustomId());

		// documentClient.checkout(sid, 27);
		//
		// WSDocument doc = documentClient.getDocument(sid, 27);
		// System.out.println("status: " + doc.getStatus());
		// System.out.println("locked user id: " +
		// doc.getLockUserId().longValue());
		// System.out.println("indexed: " + doc.getIndexed());
		//
		// DataHandler data = documentClient.getContent(sid, 665);
		// data.writeTo(new FileOutputStream("C:/tmp/buf.doc"));

		// doc = documentClient.getDocument(sid, 30);
		// System.out.println("status: " + doc.getStatus());
		// System.out.println("indexed: " + doc.getIndexed());

		// WSDocument doc = documentClient.getDocument(sid, 29);
		// Assert.assertNull(doc);
		// documentClient.restore(sid, 29, 13);
		//
		// doc = documentClient.getDocument(sid, 29);
		// System.out.println("title: " + doc.getTitle());

		// for (WSDocument wsDocument : documentClient.getVersions(sid, 30)) {
		// System.out.println("title: " + wsDocument.getVersion());
		// }

		// WSDocument[] docs = documentClient.getRecentDocuments(sid, 4);
		// System.out.println("docs: " + docs.length);
		// for (WSDocument wsDocument : docs) {
		// System.out.println("doc id: " + wsDocument.getId());
		// System.out.println("doc title: " + wsDocument.getTitle());
		// System.out.println("doc customid: " + wsDocument.getCustomId());
		// System.out.println("--------------------------------------");
		// }

		// WSDocument doc = documentClient.createAlias(sid, 30, 14);
		// System.out.println("doc id: " + doc.getId());
		// System.out.println("doc title: " + doc.getTitle());
		// System.out.println("doc customid: " + doc.getCustomId());

		// documentClient.sendEmail("ciccio", new Long[] { 690L, 32L, 29L },
		// "m.caruso@logicalobjects.it", "Test Invio Mail 2",
		// "Questa mail � un test");

		auth.logout(sid);
	}
}
