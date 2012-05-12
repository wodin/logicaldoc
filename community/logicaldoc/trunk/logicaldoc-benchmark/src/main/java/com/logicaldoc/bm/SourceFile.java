package com.logicaldoc.bm;

import java.io.File;

import com.sun.mail.iap.ByteArray;

public class SourceFile {

	File file;
	ByteArray content;
	
	public File getFile() {
		return file;
	}

	public ByteArray getContent() {
		return content;
	}

	public SourceFile(File file2, ByteArray byteArray) {
		this.file = file2;
		this.content = byteArray;
	}

}
