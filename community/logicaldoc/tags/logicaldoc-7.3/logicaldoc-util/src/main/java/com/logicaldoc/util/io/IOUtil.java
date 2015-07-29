package com.logicaldoc.util.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtil {
	private static final int DEFAULT_BUFFER_SIZE = 10240; // ..bytes = 10KB.

	/**
	 * Close the given resource.
	 * 
	 * @param resource The resource to be closed.
	 */
	public static void close(Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (IOException ignore) {
				// Ignore IOException. If you want to handle this anyway, it
				// might be useful to know
				// that this will generally only be thrown when the client
				// aborted the request.
			}
		}
	}

	public static void write(InputStream input, OutputStream output) throws IOException {
		int letter = 0;
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		while ((letter = input.read(buffer)) != -1) {
			output.write(buffer, 0, letter);
		}
	}
}
