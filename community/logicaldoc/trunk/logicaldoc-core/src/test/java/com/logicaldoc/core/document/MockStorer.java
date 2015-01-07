package com.logicaldoc.core.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.logicaldoc.core.store.Storer;

public class MockStorer implements Storer {

	@Override
	public void delete(long docId) {

	}

	public File getContainer(long docId) {
		return null;
	}

	@Override
	public long store(InputStream stream, long docId, String filename) {
		return 12;
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
	public InputStream getStream(long docId, String resource) {
		try {
			return new FileInputStream(new File("pom.xml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public byte[] getBytes(long docId, String resource) {
		try {
			return IOUtils.toByteArray(getStream(docId, resource));
		} catch (IOException e) {
		}
		return new byte[0];
	}

	@Override
	public void delete(long docId, String resourceName) {
	}

	@Override
	public List<String> listResources(long docId, String fileVersion) {
		return new ArrayList<String>();
	}

	@Override
	public long size(long docId, String resourceName) {
		return 0;
	}

	@Override
	public boolean exists(long docId, String resourceName) {
		return "pom.xml".equals(resourceName);
	}

	@Override
	public String getResourceName(long docId, String fileVersion, String suffix) {
		return getResourceName(new Document(), fileVersion, suffix);
	}

	@Override
	public void writeToFile(long docId, String resource, File out) {

	}

	@Override
	public long store(File file, long docId, String resource) {
		try {
			return store(new FileInputStream(file), docId, resource);
		} catch (FileNotFoundException e) {
			return 12;
		}
	}

	@Override
	public byte[] getBytes(long docId, String resource, long start, long length) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getStream(long docId, String resource, long start, long length) {
		// TODO Auto-generated method stub
		return null;
	}
}
