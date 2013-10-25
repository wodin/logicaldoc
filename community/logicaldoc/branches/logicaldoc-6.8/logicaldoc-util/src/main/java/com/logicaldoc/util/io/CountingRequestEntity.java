package com.logicaldoc.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.httpclient.methods.RequestEntity;

/**
 * RequestEntity used to track the upload progress, mainly intended to give
 * better experience in the GUI.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.2
 */
public class CountingRequestEntity implements RequestEntity {
	private final RequestEntity delegate;

	private final ProgressListener listener;

	public CountingRequestEntity(final RequestEntity entity, final ProgressListener listener) {
		super();
		this.delegate = entity;
		this.listener = listener;
	}

	public long getContentLength() {
		return this.delegate.getContentLength();
	}

	public String getContentType() {
		return this.delegate.getContentType();
	}

	public boolean isRepeatable() {
		return this.delegate.isRepeatable();
	}

	public void writeRequest(final OutputStream out) throws IOException {
		this.delegate.writeRequest(new CountingOutputStream(out, this.listener));
	}

	public static interface ProgressListener {
		void transferred(long total, long increment);
	}

	public static class CountingOutputStream extends FilterOutputStream {

		private final ProgressListener listener;

		private long transferred;

		public CountingOutputStream(final OutputStream out, final ProgressListener listener) {
			super(out);
			this.listener = listener;
			this.transferred = 0;
		}

		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
			this.transferred += len;
			this.listener.transferred(this.transferred, len);
		}

		public void write(int b) throws IOException {
			out.write(b);
			this.transferred++;
			this.listener.transferred(this.transferred, 1);
		}
	}
}