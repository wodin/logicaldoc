package com.logicaldoc.webservice;

import com.logicaldoc.webservice.auth.AuthClient;
import com.logicaldoc.webservice.document.DocumentClient;
import com.logicaldoc.webservice.folder.FolderClient;
import com.logicaldoc.webservice.search.SearchClient;
import com.logicaldoc.webservice.security.SecurityClient;
import com.logicaldoc.webservice.system.SystemClient;

public class Main {
	public static void main(String[] args) throws Exception {
		AuthClient auth = new AuthClient("http://localhost:9080/logicaldoc/services/Auth");
		DocumentClient documentClient = new DocumentClient("http://localhost:9080/logicaldoc/services/Document");
		FolderClient folderClient = new FolderClient("http://localhost:9080/logicaldoc/services/Folder");
		SearchClient searchClient = new SearchClient("http://localhost:9080/logicaldoc/services/Search");
		SystemClient systemClient = new SystemClient("http://localhost:9080/logicaldoc/services/System");
		SecurityClient securityClient = new SecurityClient("http://localhost:9080/logicaldoc/services/Security");

		// Open a session
		// This is a user 'author' with different permissions than the authors.
		// String sid = auth.login("matteo", "matteo1982");
		String sid = auth.login("admin", "admin");
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

		// DataHandler data = documentClient.getContent(sid, 68);
		// System.out.println("data: " + data.toString());

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

		auth.logout(sid);
	}
}
