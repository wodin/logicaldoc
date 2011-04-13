package com.logicaldoc.core.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.logicaldoc.core.store.Storer;

public class MockStorer implements Storer {

	@Override
	public void clean(long docId) {

	}

	@Override
	public void delete(long docId) {

	}

	@Override
	public File getContainer(long docId) {
		return null;
	}

	@Override
	public boolean store(InputStream stream, long docId, String filename) {
		return true;
	}

	@Override
	public long getTotalSize() {
		return 0;
	}

	@Override
	public String getResourceName(Document doc, String fileVersion, String suffix) {
		return "pom.xml";
	}

	@Override
	public InputStream getStream(long docId, String fileVersion, String suffix) {
		try {
			return new FileInputStream("pom.xml");
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	@Override
	public InputStream getStream(Document doc, String fileVersion, String suffix) {
		return getStream(0, fileVersion, suffix);
	}

	@Override
	public File getFile(Document doc, String fileVersion, String suffix) {
		return new File("pom.xml");
	}

	@Override
	public File getFile(long docId, String fileVersion, String suffix) {
		return new File("pom.xml");
	}

}
