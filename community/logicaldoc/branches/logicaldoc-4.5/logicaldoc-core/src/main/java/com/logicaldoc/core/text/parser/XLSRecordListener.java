package com.logicaldoc.core.text.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * 
 * @author Michael Scholz
 */
public class XLSRecordListener implements HSSFListener {
	private SSTRecord sstrec;

	private StringBuffer content = new StringBuffer();

	protected static Log logger = LogFactory.getLog(XLSRecordListener.class);

	public XLSRecordListener() {
	}

	public void processRecord(Record record) {
		switch (record.getSid()) {
		// the BOFRecord can represent either the beginning of a sheet or the
		// workbook
		case BOFRecord.sid:
		case BoundSheetRecord.sid:
		case RowRecord.sid: {
			break;
		}

		case NumberRecord.sid: {
			NumberRecord numrec = (NumberRecord) record;
			content.append(String.valueOf(numrec.getValue()));
			content.append(" ");
			break;
			// SSTRecords store a array of unique strings used in Excel.
		}

		case SSTRecord.sid: {
			sstrec = (SSTRecord) record;

			for (int k = 0; k < sstrec.getNumUniqueStrings(); k++) {
				content.append(sstrec.getString(k));
				content.append(" ");
			}

			break;
		}

		case LabelSSTRecord.sid: {
			LabelSSTRecord lrec = (LabelSSTRecord) record;
			content.append(sstrec.getString(lrec.getSSTIndex()));
			content.append(" ");
			break;
		}
		}
	}

	public StringBuffer parse(File file) {
		try {
			FileInputStream fin = new FileInputStream(file);
			POIFSFileSystem poifs = new POIFSFileSystem(fin);
			InputStream din = poifs.createDocumentInputStream("Workbook");
			HSSFRequest req = new HSSFRequest();
			req.addListenerForAllRecords(this);

			HSSFEventFactory factory = new HSSFEventFactory();
			factory.processEvents(req, din);
			fin.close();
			din.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return content;
	}
}