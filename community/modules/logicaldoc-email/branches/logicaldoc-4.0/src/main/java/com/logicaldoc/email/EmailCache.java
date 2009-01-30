package com.logicaldoc.email;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.util.csv.CSVFileReader;
import com.logicaldoc.util.csv.CSVFileWriter;

/**
 * Implements a cache of imported email, basically a email cache is a Map<mailID,
 * imported date>
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.0
 */
public class EmailCache extends HashMap<String, Date> {
	private static final long serialVersionUID = 1L;

	protected static Log log = LogFactory.getLog(EmailCache.class);

	private File file;

	private String lastMailId = null;

	public EmailCache(File file) throws IOException {
		super();
		this.file = file;
		if (!file.exists()) {
			FileUtils.touch(file);
		}
	}

	public File getFile() {
		return file;
	}

	/**
	 * Reads all data from file
	 */
	public void read() throws IOException {
		clear();
		CSVFileReader reader = new CSVFileReader(file.getPath(), ',');
		try {
			DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			Vector<String> fields = reader.readFields();
			long i = 0;
			while (fields != null) {
				try {
					put(fields.get(0), df.parse(fields.get(1)));
				} catch (ParseException e) {
					log.error("Line " + i + ": Unparseable document Id " + fields.get(0) + " or date " + fields.get(1)
							+ " skip record");
				}
				fields = reader.readFields();
				i++;
			}
		} finally {
			reader.close();
		}
		log.info("Loaded " + size() + " entries");
	}

	/**
	 * Writes all entries into the file
	 */
	public synchronized void write() throws IOException {
		CSVFileWriter writer = new CSVFileWriter(file.getPath(), ',');
		try {
			for (String mailId : keySet()) {
				DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
				Vector<String> fields = new Vector<String>();
				fields.add(mailId);
				fields.add(df.format(get(mailId)));
				writer.writeFields(fields);
			}
		} finally {
			writer.close();
		}
		log.info("Written " + size() + " entries");
	}

	/**
	 * Appends the last entry to the file
	 */
	public synchronized void append() throws IOException {
		CSVFileWriter writer = new CSVFileWriter(file.getPath(), ',', true);
		try {
			DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			Vector<String> fields = new Vector<String>();
			fields.add(lastMailId);
			fields.add(df.format(get(lastMailId)));
			writer.writeFields(fields);
		} finally {
			writer.close();
		}
		log.info("Written " + size() + " entries");
	}
	
	public Date put(String mailID, Date date) {
		lastMailId = mailID;
		return super.put(mailID, date);
	}

}
