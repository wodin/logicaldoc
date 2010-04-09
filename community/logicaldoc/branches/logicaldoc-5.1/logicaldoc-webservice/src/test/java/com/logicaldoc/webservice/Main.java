package com.logicaldoc.webservice;

import java.io.File;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import com.logicaldoc.core.searchengine.LuceneDocument;

public class Main {
	public static void main(String[] args) throws Exception {
		DmsClient client = new DmsClient(
				"http://localhost:9080/logicaldoc/services/Dms");

		//Apriamo una sessione
		String sid = client.login("admin", "admin");
		
		
		//Otteniamo i metadati di un documento
		DocumentInfo info = client.downloadDocumentInfo(sid, 296);
		System.out.println("Informazioni sul documento 296");
		System.out.println("titolo=" + info.getTitle());
		System.out.println("version=" + info.getVersion());
		System.out.println("filename=" + info.getFilename());

		//Facciamo il checkout
		client.checkout(sid, 296);

		//Scarichiamo il file del documento
		File docFile = new File("target/" + info.getFilename());
		client.downloadDocumentToFile(sid, 296, null, docFile);

		//Eseguiamo il checkin creando una nuova versione
		client.checkin(sid, 296, info.getFilename(), "test checkin",
				"subversion", docFile);

		//Facciamo una ricerca
		System.out.println("Risultato ricerca:");
		Result[] results = client.search(sid, "archivio", "it", "it", 100, null, new String[] { LuceneDocument.FIELD_CONTENT, LuceneDocument.FIELD_TAGS }).getResult();
		for (int i = 0; i < results.length; i++) {
			System.out.println(results[i].getTitle()+" - "+results[i].getScore());
			System.out.println(results[i].getSummary());
		}
		
		client.logout(sid);
	}
}
