package com.logicaldoc.webservice;

import com.logicaldoc.core.ExtendedAttribute;
import com.logicaldoc.core.searchengine.SearchOptions;
import com.logicaldoc.webservice.auth.AuthClient;
import com.logicaldoc.webservice.document.DocumentClient;
import com.logicaldoc.webservice.document.WSDocument;
import com.logicaldoc.webservice.folder.FolderClient;
import com.logicaldoc.webservice.folder.WSFolder;
import com.logicaldoc.webservice.search.SearchClient;
import com.logicaldoc.webservice.search.WSSearchOptions;
import com.logicaldoc.webservice.search.WSSearchResult;
import com.logicaldoc.webservice.security.SecurityClient;
import com.logicaldoc.webservice.security.WSUser;
import com.logicaldoc.webservice.system.SystemClient;

public class SoapWorgkbench {
	final static String BASE = "http://localhost:9080/services";

	public static void main(String[] args) throws Exception {

		AuthClient auth = new AuthClient(BASE + "/Auth");

		SystemClient systemClient = new SystemClient(BASE + "/System");
		SecurityClient securityClient = new SecurityClient(BASE + "/Security");

		// Open a session
		String sid = auth.login("admin", "12345678");
		System.out.println("Server date: " + systemClient.getInfo().getDate());
		System.out.println("Sid: " + sid);

		try {
			// securityStuff(sid);

			// documentStuff(sid);
			
			folderStuff(sid);

			// searchStuff(sid);

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

			// SystemInfo info = systemClient.getInfo(sid);
			// System.out.println("installation id: " +
			// info.getInstallationId());
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

			// WSFolder[] folders = folderClient.listChildren(sid, 4);
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

		} finally {
			auth.logout(sid);
		}
	}

	private static void folderStuff(String sid) throws Exception {
		FolderClient folderClient = new FolderClient(BASE + "/Folder", 1, false, 50);

		WSFolder newFolder = new WSFolder();
		newFolder.setName("ddddd");
		newFolder.setParentId(4L);
		newFolder.setTemplateId(1L);
		newFolder.setTemplateLocked(1);
		
		WSAttribute[] att=new WSAttribute[1];
		att[0]=new WSAttribute();
		att[0].setName("from");
		att[0].setType(ExtendedAttribute.TYPE_STRING);
		att[0].setStringValue("pippo");
		newFolder.setExtendedAttributes(att);
		
//		newFolder = folderClient.create(sid, newFolder);
		
		System.out.println(folderClient.findByPath(sid, "/Default/Kofax"));
	}

	private static void securityStuff(String sid) throws Exception {
		SecurityClient securityClient = new SecurityClient(BASE + "/Security");

		WSUser wsUserTest = new WSUser();
		wsUserTest.setName("marco2");
		wsUserTest.setEmail("marco@acme.com");
		wsUserTest.setUserName("marco2");
		wsUserTest.setFirstName("alle2");
		long[] ids = { 2, 3 };
		wsUserTest.setGroupIds(ids);

		Long userId = securityClient.storeUser(sid, wsUserTest);
		System.out.println("user id: " + userId);
		securityClient.changePassword(sid, userId, null, "marco1982");

		securityClient.deleteUser(sid, 4);

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
	}

	private static void searchStuff(String sid) throws Exception {
		SearchClient searchClient = new SearchClient(BASE + "/Search");

		// WSDocument[] documents = searchClient.findByFilename(sid,
		// "pizzo.ods");
		// System.out.println("---- " + documents.length);
		//
		// List<WSDocument> docsList = Arrays.asList(documents);
		// for (WSDocument doc : docsList) {
		// System.out.println("title: " + doc.getTitle());
		// System.out.println("custom id: " + doc.getCustomId());
		// System.out.println("version: " + doc.getVersion());
		// System.out.println("date: " + doc.getDate());
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

		// for (WSTagCloud tag : searchClient.getTagCloud(sid)) {
		// System.out.println("tag: " + tag.getTag());
		// System.out.println("tag count: " + tag.getCount());
		// System.out.println("tag scale: " + tag.getScale());
		// System.out.println("++++++++++++++++++++++++++++++++");
		// }
		//
		WSSearchOptions opt = new WSSearchOptions();
		opt.setLanguage("en");
		opt.setExpression("paper");
		opt.setExpressionLanguage("en");
		opt.setType(SearchOptions.TYPE_FULLTEXT);
		opt.setMaxHits(10);
		opt.setFolderId(4L);
		opt.setSearchInSubPath(1);
		WSSearchResult result = searchClient.find(sid, opt);
		System.out.println("---- " + result.getHits().length);
		for (WSDocument hit : result.getHits()) {
			System.out.println("hit customid: " + hit.getCustomId());
			System.out.println("hit score: " + hit.getScore());
			System.out.println("hit folderid: " + hit.getFolderId());
			System.out.println("hit title: " + hit.getTitle());
			System.out.println("hit creation: " + hit.getCreation());
			System.out.println("hit summary: " + hit.getSummary());
			System.out.println("************************");
		}
	}

	private static void documentStuff(String sid) throws Exception {
		DocumentClient documentClient = new DocumentClient(BASE + "/Document");

		// WSLink link = documentClient.link(sid, 3621L, 3176L, "testws");
		// System.out.println("Created link "+link.getId());

		// documentClient.deleteLink(sid, 30081024L);
		//
		// WSLink[] links = documentClient.getLinks(sid, 3176L);
		// for (WSLink lnk : links) {
		// System.out.println("Link " + lnk.getType() + " - > " + lnk.getDoc2()
		// + " (" + lnk.getId() + ")");
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
		// DataHandler data = documentClient.getVersionContent(sid, 12724,
		// "1.1");
		// data.writeTo(new FileOutputStream("C:/tmp/buf.txt"));

		// doc = documentClient.getDocument(sid, 30);
		// System.out.println("status: " + doc.getStatus());
		// System.out.println("indexed: " + doc.getIndexed());

		// WSDocument doc = documentClient.getDocument(sid, 29);
		// Assert.assertNull(doc);
		// documentClient.restore(sid, 29, 13);
		//
		// doc = documentClient.getDocument(sid, 29);
		// System.out.println("title: " + doc.getTitle());

		// for (WSDocument wsDocument : documentClient.getVersions(sid, 30))
		// {
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
		// "Questa mail è un test");

		// WSHistory[] history = documentClient.getHistory(sid, 12724);
		// for (WSHistory h : history) {
		// System.out.println(h.getDate()+"->"+h.getEvent()+" - "+h.getVersion());
		// }

		// WSFolder f = folderClient.findByPath(sid,
		// "/Default/scomar/folder1x3z/folder6");
		// System.out.println(f.getId() + " - " + f.getName());

		// WSDocument doc = documentClient.getDocument(sid, 535494657L);
		// for (WSAttribute att : doc.getExtendedAttributes()) {
		// if (att.getName().equals("utente")) {
		// WSUser user = new WSUser();
		// user.setId(51L);
		// user.setName("Meschieri");
		// user.setFirstName("Marco");
		// att.setValue(user);
		// }
		// }
		//
		// documentClient.update(sid, doc);
		//
		// for (WSAttribute att : doc.getExtendedAttributes()) {
		// System.out.println(att.getName() + "(" + att.getType() + ")=" +
		// att.getValue()
		// + (att.getType() == WSAttribute.TYPE_USER ? " " +
		// att.getStringValue() : ""));
		// }

		// documentClient.createPdf(sid, 669286400L,"1.0");
		// documentClient.getResourceContent(sid, 669286400L, "1.0",
		// "conversion.pdf", new File("D:/tmp/conversion.pdf"));

		System.out.println(documentClient.getExtractedText(sid, 643L));
	}
}
