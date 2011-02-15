package com.logicaldoc.webservice;

import com.logicaldoc.webservice.auth.AuthClient;
import com.logicaldoc.webservice.system.SystemClient;
import com.logicaldoc.webservice.system.WSInfo;

public class Main {
	public static void main(String[] args) throws Exception {
		AuthClient auth = new AuthClient(
				"http://localhost:9080/logicaldoc/services/Auth");
		// DocumentClient documentClient = new
		// DocumentClient("http://localhost:9080/logicaldoc/services/Document");
		// FolderClient folderClient = new
		// FolderClient("http://localhost:9080/logicaldoc/services/Folder");
		// SearchClient searchClient = new
		// SearchClient("http://localhost:9080/logicaldoc/services/Search");
		SystemClient systemClient = new SystemClient(
				"http://localhost:9080/logicaldoc/services/System");

		// Open a session
		// This is a user 'author' with different permissions than the authors.
		// String sid = auth.login("matteo", "matteo1982");
		String sid = auth.login("admin", "admin");
		System.out.println("sid: " + sid);

		WSInfo info = systemClient.getInfo(sid);
		System.out.println("vendorAddress: " + info.getVendorAddress());
		System.out.println("id: " + info.getInstallationId());
		System.out.println("release: " + info.getRelease());
		System.out.println("year: " + info.getYear());

		WSParameter[] parameters = systemClient.getStatistics(sid);
		for (WSParameter param : parameters) {
			System.out.println("param name: " + param.getName());
			System.out.println("param value: " + param.getValue());
		}

		// auth.grantGroup(sid, 8, 2, 0, true);
		// auth.logout(sid);
		//
		// sid = auth.login("matteo", "matteo1982");

		// documentClient.sendEmail(sid, new Long[]{13L,9L},
		// "m.meschieri@logicalobjects.it, info@logicalobjects.it", "test ws",
		// "message test ws");

		// TagCloud[] tags = searchClient.getTagCloud(sid);
		// for (TagCloud tag : tags) {
		// if (tag.getTag().equals("pappsa")) {
		// System.out.println("tag: " + tag.getTag());
		// System.out.println("tag: " + tag.getCount());
		// System.out.println("tag: " + tag.getScale());
		// }
		// }
		//
		// WSDocument[] docs = searchClient.findByTag(sid, "pappsa");
		// for (WSDocument doc : docs) {
		// System.out.println("doc id: " + doc.getId());
		// System.out.println("doc title: " + doc.getTitle());
		// System.out.println("doc folderid: " + doc.getFolderId());
		// }
		//
		// WSDocument[] docs = searchClient.findByTag(sid, "pappsa");
		// for (WSDocument doc : docs) {
		// System.out.println("doc id: " + doc.getId());
		// System.out.println("doc title: " + doc.getTitle());
		// System.out.println("doc folderid: " + doc.getFolderId());
		// }

		// WSDocument[] docs = searchClient.findByFilename(sid, "logicaldoc%");
		// for (WSDocument doc : docs) {
		// System.out.println("doc id: " + doc.getId());
		// System.out.println("doc title: " + doc.getTitle());
		// System.out.println("doc folderid: " + doc.getFolderId());
		// }
		//
		// WSFolder[] folders = searchClient.findFolders(sid, "test");
		// for (WSFolder doc : folders) {
		// System.out.println("folder id: " + doc.getId());
		// System.out.println("folder title: " + doc.getName());
		// System.out.println("folder desc: " + doc.getDescription());
		// }

		// WSDocument document = documentClient.getDocument(sid, 3);
		// document.setTitle("test_5");
		// document.setSourceDate("2010-05-01");
		// DataHandler data = documentClient.getContent(sid, 3);
		// WSDocument doc = documentClient.create(sid, document, data);
		// System.out.println("created doc: " + doc.getId());

		// System.out.println("doc id: " + document.getId());
		// System.out.println("doc name: " + document.getTitle());
		// System.out.println("doc template id: " + document.getTemplateId());
		// for (String name : document.getAttributeNames()) {
		// System.out.println("attribute name: " + name);
		// ExtendedAttribute extAttr = document.getAttributes().get(name);
		// System.out.println("attribute pos: " + extAttr.getPosition());
		// System.out.println("attribute type: " + extAttr.getType());
		// System.out.println("attribute mandatory: " + extAttr.getMandatory());
		// System.out.println("attribute value: " +
		// extAttr.getValue().toString());
		// }

		// WSDocument[] docs = documentClient.getDocuments(sid, new long[] {
		// 205, 204, 203, 202, 201, 200, 196, 51, 55 });
		// for (WSDocument doc : docs) {
		// System.out.println("doc id: " + doc.getId());
		// System.out.println("doc name: " + doc.getTitle());
		// System.out.println("doc template id: " + doc.getTemplateId());
		// for (WSAttribute attribute : doc.getExtendedAttributes()) {
		// System.out.println("attribute name: " + attribute.getName());
		// System.out.println("attribute pos: " + attribute.getPosition());
		// System.out.println("attribute type: " + attribute.getType());
		// System.out.println("attribute mandatory: " +
		// attribute.getMandatory());
		// System.out.println("attribute value: " +
		// attribute.getValue().toString());
		// }
		// System.out.println("************************************************");

		// WSDocument[] docs = documentClient.getDocuments(sid, new long[] { 205
		// });
		// WSDocument doc = docs[0];
		// Document document = doc.toDocument(null);

		// documentClient.move(sid, 199, 7);

		// WSDocument[] docs = documentClient.getVersions(sid, 197);
		// for (WSDocument doc : docs) {
		// System.out.println("doc version: " + doc.getVersion());
		// System.out.println("doc file version: " + doc.getFileVersion());
		// System.out.println("doc name: " + doc.getTitle());
		// }
		//
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

		// DataHandler data = documentClient.getContent(sid, 68);
		// System.out.println("data: " + data.toString());

		auth.logout(sid);
	}
}
