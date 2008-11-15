package com.logicaldoc.webdav.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.server.io.IOListener;
import org.apache.jackrabbit.server.io.IOUtil;
import org.apache.jackrabbit.server.io.MimeResolver;
import org.apache.jackrabbit.webdav.io.InputContext;

import com.logicaldoc.webdav.resource.model.Resource;
public class ImportContextImpl implements ImportContext {

	protected static Log log = LogFactory.getLog(ImportContextImpl.class);
	
	private final Resource resource;
    private final String systemId;
    private final File inputFile;
    private final MimeResolver mimeResolver;

    private InputContext inputCtx;
    private boolean completed;

    /**
     * Creates a new item import context with the given root item and the
     * specified <code>InputContext</code>. If the input context provides an
     * input stream, the stream is written to a temporary file in order to avoid
     * problems with multiple IOHandlers that try to run the import but fail.
     * The temporary file is deleted as soon as this context is informed that
     * the import has been completed and it will not be used any more.
     *
     * @param importRoot the import root node
     * @param systemId
     * @param inputCtx wrapped by this <code>ImportContext</code>
     */
    public ImportContextImpl(Resource resource, String systemId,
			InputContext inputCtx) throws IOException {
		this(resource, systemId, inputCtx, null);
	}

    /**
     * Creates a new item import context with the given root item and the
     * specified <code>InputContext</code>. If the input context provides an
     * input stream, the stream is written to a temporary file in order to avoid
     * problems with multiple IOHandlers that try to run the import but fail.
     * The temporary file is deleted as soon as this context is informed that
     * the import has been completed and it will not be used any more.
     *
     * @param importRoot the import root node
     * @param systemId
     * @param inputCtx wrapped by this <code>ImportContext</code>
     * @param mimeResolver
     */
    public ImportContextImpl(Resource resource, String systemId,
			InputContext inputCtx, MimeResolver mimeResolver)
			throws IOException {
		this(resource, systemId, (inputCtx != null) ? inputCtx.getInputStream()
				: null, mimeResolver);
		this.inputCtx = inputCtx;
	}

    /**
     * Creates a new item import context. The specified InputStream is written
     * to a temporary file in order to avoid problems with multiple IOHandlers
     * that try to run the import but fail. The temporary file is deleted as soon
     * as this context is informed that the import has been completed and it
     * will not be used any more.
     *
     * @param importRoot
     * @param systemId
     * @param in
     * @param ioListener
     * @param mimeResolver
     * @throws IOException
     * @see ImportContext#informCompleted(boolean)
     */
    public ImportContextImpl(Resource resource, String systemId,
			InputStream in, MimeResolver mimeResolver)
			throws IOException {
		this.resource = resource;
		this.systemId = systemId;
		this.inputFile = IOUtil.getTempFile(in);
		
		this.mimeResolver = (mimeResolver == null) ? IOUtil.MIME_RESOLVER
				: mimeResolver;
	}

    /**
     * @see ImportContext#getIOListener()
     */
    public IOListener getIOListener() {
        return null;
    }

    /**
     * @see ImportContext#getImportRoot()
     */
    public MimeResolver getMimeResolver() {
        return mimeResolver;
    }

    /**
     * @see ImportContext#hasStream()
     */
    public boolean hasStream() {
        return inputFile != null;
    }

    /**
     * Returns a new <code>InputStream</code> to the temporary file created
     * during instanciation or <code>null</code>, if this context does not
     * provide a stream.
     *
     * @see ImportContext#getInputStream()
     * @see #hasStream()
     */
    public InputStream getInputStream() {
        checkCompleted();
        InputStream in = null;
        if (inputFile != null) {
            try {
                in = new FileInputStream(inputFile);
            } catch (IOException e) {
                // unexpected error... ignore and return null
            }
        }
        return in;
    }

    /**
     * @see ImportContext#getSystemId()
     */
    public String getSystemId() {
        return systemId;
    }

    /**
	 * @see ImportContext#getModificationTime()
	 */
	public long getModificationTime() {
		return (inputCtx != null) ? inputCtx.getModificationTime() : new Date()
				.getTime();
	}

	/**
	 * @see ImportContext#getContentLanguage()
	 */
	public String getContentLanguage() {
		return (inputCtx != null) ? inputCtx.getContentLanguage() : null;
	}

    /**
     * @see ImportContext#getContentLength()
     */
    public long getContentLength() {
		if (inputCtx != null) {
			return inputCtx.getContentLength();
		} else if (inputFile != null) {
			return inputFile.length();
		} else {
			log.debug("Unable to determine content length -> default value = "
					+ IOUtil.UNDEFINED_LENGTH);
			return IOUtil.UNDEFINED_LENGTH;
		}
	}

    /**
     * @return the content type present on the <code>InputContext</code> or
     * <code>null</code>
     * @see InputContext#getContentType()
     */
    private String getContentType() {
        return (inputCtx != null) ? inputCtx.getContentType() : null;
    }

    /**
     * @see ImportContext#getMimeType()
     */
    public String getMimeType() {
        String contentType = getContentType();
        String mimeType = null;
        if (contentType != null) {
            mimeType = IOUtil.getMimeType(contentType);
        } else if (getSystemId() != null) {
            mimeType = mimeResolver.getMimeType(getSystemId());
        }
        return mimeType;
    }

    /**
	 * @see ImportContext#getEncoding()
	 */
	public String getEncoding() {
		String contentType = getContentType();
		return (contentType != null) ? IOUtil.getEncoding(contentType) : null;
	}

	/**
	 * @see ImportContext#getProperty(Object)
	 */
	public Object getProperty(Object propertyName) {
		return (inputCtx != null) ? inputCtx.getProperty(propertyName
				.toString()) : null;
	}

    /**
     * @see ImportContext#informCompleted(boolean)
     */
    public void informCompleted(boolean success) {
        checkCompleted();
        completed = true;
        if (inputFile != null) {
            inputFile.delete();
        }
    }

    /**
     * @see ImportContext#isCompleted()
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * @throws IllegalStateException if the context is already completed.
     * @see #isCompleted()
     * @see #informCompleted(boolean)
     */
    private void checkCompleted() {
        if (completed) {
            throw new IllegalStateException("ImportContext has already been consumed.");
        }
    }

	public Resource getResource() {
		return this.resource;
	}
}
