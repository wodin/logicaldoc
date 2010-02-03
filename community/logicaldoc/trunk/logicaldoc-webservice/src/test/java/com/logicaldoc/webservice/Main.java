package com.logicaldoc.webservice;

import java.io.File;

import com.logicaldoc.core.searchengine.LuceneDocument;


public class Main {
	public static void main(String[] args) throws Exception {
		DmsClient client = new DmsClient("http://localhost:8080/logicaldoc/services/Dms");

		// Apriamo una sessione
		String sid = client.login("admin", "admin");

		// Otteniamo i metadati di un documento
		DocumentInfo info = client.downloadDocumentInfo(sid, 4);
		System.out.println("Informazioni sul documento 4");
		System.out.println("titolo=" + info.getTitle());
		System.out.println("version=" + info.getVersion());
		System.out.println("filename=" + info.getFilename());
		System.out.println("repipient=" + info.getRecipient());
		System.out.println("docref=" + info.getDocRef());

//		// Facciamo il checkout
//		client.checkout(sid, 2);
//
//		// Scarichiamo il file del documento
//		File docFile = new File("target/" + info.getFilename());
//		client.downloadDocumentToFile(sid, 2, null, docFile);
//
//		// Eseguiamo il checkin creando una nuova versione
//		client.checkin(sid, 2, info.getFilename(), "test checkin", "subversion", docFile);
//
//		// Facciamo una ricerca
//		System.out.println("Risultato ricerca:");
//		Result[] results = client.search(sid, "archivio", "it", "it", 100, null,
//				new String[] { LuceneDocument.FIELD_CONTENT, LuceneDocument.FIELD_TAGS }).getResult();
//		for (int i = 0; i < results.length; i++) {
//			System.out.println(results[i].getTitle() + " - " + results[i].getScore());
//			System.out.println(results[i].getSummary());
//		}

		client.logout(sid);
	}
}
