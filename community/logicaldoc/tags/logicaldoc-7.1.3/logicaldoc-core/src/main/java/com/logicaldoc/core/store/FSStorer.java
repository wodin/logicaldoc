package com.logicaldoc.core.store;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.StringUtil;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.io.FileUtil;

/**
 * This class is an implementation of the Storer interface to persist documents
 * in the filesystem. From the root of the documents storage, this
 * implementation saves all document's files into a defined directory using the
 * following logic. The document's id is tokenized by three chars tokens, than
 * the doc/ dir is appended, so if the docId=12345, the document's path will
 * be:123/45/doc.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class FSStorer implements Storer {

	protected static final int DEFAULT_BUFFER_SIZE = 10240;

	protected static Logger log = LoggerFactory.getLogger(FSStorer.class);

	protected ContextProperties config;

	public FSStorer() {
	}

	public ContextProperties getConfig() {
		return config;
	}

	public void setConfig(ContextProperties config) {
		this.config = config;
	}

	@Override
	public void delete(long docId) {
		File docDir = getContainer(docId);
		try {
			FileUtils.forceDelete(docDir);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * Finds the container where all document's files are stored
	 * 
	 * @param docId The document identifier
	 * @return The document's container
	 */
	public File getContainer(long docId) {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document document = docDao.findById(docId);

		if (document == null) {
			log.warn("Document NOT found: " + docId);
			return null;
		}

		if (document.getDocRef() != null) {
			// The shortcut document doesn't have the 'fileversion' and the
			// 'version'
			document = docDao.findById(document.getDocRef());
		}

		String relativePath = computeRelativePath(docId);
		String path = config.getPropertyWithSubstitutions("store.1.dir") + "/" + relativePath;
		return new File(path);
	}

	@Override
	public long store(InputStream stream, long docId, String resource) {
		File file = null;
		try {
			File dir = getContainer(docId);
			FileUtils.forceMkdir(dir);
			file = new File(new StringBuilder(dir.getPath()).append("/").append(resource).toString());
			FileUtil.writeFile(stream, file.getPath());
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			return -1;
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
		return file.length();
	}

	@Override
	public long store(File file, long docId, String resource) {
		InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
		} catch (FileNotFoundException e) {
			return -1;
		}
		return store(is, docId, resource);
	}

	@Override
	public String getResourceName(Document doc, String fileVersion, String suffix) {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document document = doc;

		/*
		 * All versions of a document are stored in the same directory as the
		 * current version, but the filename is the version number without
		 * extension, e.g. "docId/2.1"
		 */
		String filename;
		if (doc.getDocRef() != null) {
			// The shortcut document doesn't have the 'fileversion' and the
			// 'version'
			document = docDao.findById(doc.getDocRef());
		}

		if (StringUtils.isEmpty(fileVersion))
			filename = document.getFileVersion();
		else
			filename = fileVersion;
		if (StringUtils.isEmpty(filename))
			filename = document.getVersion();

		/*
		 * Document's related resources are stored with a suffix, e.g.
		 * "doc/2.1-thumb.jpg"
		 */
		if (StringUtils.isNotEmpty(suffix))
			filename += "-" + suffix;

		return filename;
	}

	@Override
	public String getResourceName(long docId, String fileVersion, String suffix) {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);
		return getResourceName(doc, fileVersion, suffix);
	}

	@Override
	public InputStream getStream(long docId, String resource) {
		File container = getContainer(docId);
		File file = new File(container, resource);

		try {
			return new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
		} catch (Throwable e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public InputStream getStream(long docId, String resource, long start, long length) {
		byte[] bytes = getBytes(docId, resource, start, length);
		return new BufferedInputStream(new ByteArrayInputStream(bytes));
	}

	@Override
	public long getTotalSize() {
		long size = 0;
		File docDir = new File(config.getPropertyWithSubstitutions("store.1.dir"));
		if (docDir.exists())
			size = FileUtils.sizeOfDirectory(docDir);

		return size;
	}

	@Override
	public byte[] getBytes(long docId, String resource) {
		InputStream is = null;
		try {
			is = getStream(docId, resource);
			byte[] bytes = IOUtils.toByteArray(is);
			return bytes;
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {

				}
		}
		return null;
	}

	@Override
	public byte[] getBytes(long docId, String resource, long start, long length) {
		File container = getContainer(docId);
		File file = new File(container, resource);

		try {
			return FileUtil.toByteArray(file, start, length);
		} catch (Throwable e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public void delete(long docId, String resource) {
		File file = new File(getContainer(docId), resource);
		if (file.exists())
			try {
				FileUtils.forceDelete(file);
			} catch (IOException e) {
				log.error(e.getMessage());
			}
	}

	@Override
	public List<String> listResources(long docId, final String fileVersion) {
		List<String> resources = new ArrayList<String>();
		File container = getContainer(docId);
		File[] buf = container.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.startsWith("."))
					return false;
				else if (StringUtils.isNotEmpty(fileVersion)) {
					return name.startsWith(fileVersion);
				}
				return true;
			}
		});
		if (buf != null)
			for (File file : buf) {
				resources.add(file.getName());
			}
		return resources;
	}

	@Override
	public long size(long docId, String resource) {
		File file = getContainer(docId);
		file = new File(file, resource);
		return file.length();
	}

	@Override
	public boolean exists(long docId, String resource) {
		File file = getContainer(docId);
		file = new File(file, resource);
		return file.exists();
	}

	/**
	 * Computes the relative path of a document's folder inside the storage
	 * root.
	 */
	protected String computeRelativePath(long docId) {
		return StringUtil.split(Long.toString(docId), '/', 3) + "/doc";
	}

	@Override
	public void writeToFile(long docId, String resource, File out) {
		OutputStream os = null;
		InputStream is = null;
		try {
			os = new BufferedOutputStream(new FileOutputStream(out, false), DEFAULT_BUFFER_SIZE);
			is = getStream(docId, resource);
			FileUtil.writeFile(is, out.getPath());
		} catch (Throwable e) {
			log.error(e.getMessage());
		} finally {
			if (os != null) {
				try {
					os.flush();
					os.close();
				} catch (Throwable e) {
				}
			}
			if (is != null)
				try {
					is.close();
				} catch (Throwable e) {
				}
		}
	}
}