package com.logicaldoc.installer;

import java.lang.management.ManagementFactory;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.installer.DataValidator;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.installer.util.FileUtil;
import com.logicaldoc.installer.util.Log;
import com.logicaldoc.installer.util.ServiceUtil;
import com.logicaldoc.installer.util.WindowsReqistry;

/**
 * Checks the database engine, warning if the embedded one is chosen.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.8.1
 */
public class DatabaseEngineValidator implements DataValidator {

	@Override
	public boolean getDefaultAnswer() {
		return false;
	}

	@Override
	public String getErrorMessageId() {
		return I18N.message("embeddeddbwarn");
		// An IzPack bug doesn't allow the resolution of the id in the langpack
		// file
		// return "embeddeddbwarn";
	}

	@Override
	public String getWarningMessageId() {
		return I18N.message("embeddeddbwarn");
		// An IzPack bug doesn't allow the resolution of the id in the langpack
		// file
		// return "embeddeddbwarn";
	}

	@Override
	public Status validateData(InstallData data) {
		String engine = data.getVariable(Constants.DBENGINE);

		String dbport = ((String[]) DatabaseValidator.dbDefaults.get(engine))[4];
		data.setVariable(Constants.DBPORT, dbport);

		String url = ((String[]) DatabaseValidator.dbDefaults.get(engine))[5];
		data.setVariable(Constants.DBURL, url);

		/*
		 * Now some elaborations not related to database validaton
		 */

		// Set a tentative maximum memory assignment
		data.setVariable(Constants.MAXMEMORY, memoryProposal());

		// Set the service name
		data.setVariable(Constants.SERVICENAME, serviceName());

		// Try to detect the OpenOffice path
		data.setVariable(Constants.OPENOFFICE, openOfficePath());

		// Put some external tools paths
		if (FileUtil.isWindows()) {
			String root = data.getVariable("INSTALL_PATH");
			if (!root.endsWith("\\"))
				root = root + "\\";
			data.setVariable(Constants.CONVERT, root + "imagemagick\\convert.exe");
			data.setVariable(Constants.GHOSTSCRIPT, root + "ghostscript\\bin\\gs.exe");
			data.setVariable(Constants.TESSERACT, root + "tesseract\\tesseract.exe");
			data.setVariable(Constants.SWFTOOLS, root + "swftools");
			data.setVariable(Constants.CLAMSCAN, root + "clamav\\clamscan.exe");
			data.setVariable(Constants.OPENSSL, root + "openssl\\bin\\openssl.exe");
		} else {
			data.setVariable(Constants.CONVERT, "/usr/bin/convert");
			data.setVariable(Constants.GHOSTSCRIPT, "/usr/bin/gs");
			data.setVariable(Constants.TESSERACT, "/usr/local/bin/tesseract");
			data.setVariable(Constants.SWFTOOLS, "/usr/local/bin");
			data.setVariable(Constants.CLAMSCAN, "/usr/bin/clamscan");
			data.setVariable(Constants.OPENSSL, "/usr/bin/openssl");
		}

		return Status.OK;
	}

	/**
	 * Not related to database validation but it is useful to compute now a
	 * proposal for the application server memory setting
	 */
	private String memoryProposal() {
		try {
			// Use at maximum the 3/4 of the total ram
			long maxMem = (((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean())
					.getTotalPhysicalMemorySize()) * 3 / 4;
			maxMem = maxMem / 1024 / 1024;
			maxMem = maxMem * 3 / 4;
			if (maxMem < 900)
				maxMem = 900;
			return Long.toString(maxMem);
		} catch (Throwable t) {
			Log.error(t.getMessage(), t);
			return "900";
		}
	}

	/**
	 * Not related to database validation but it is useful to compute now the
	 * path of OpenOffice
	 */
	private String openOfficePath() {
		try {
			if (FileUtil.isWindows()) {
				String path = WindowsReqistry.readRegistry("HKEY_CLASSES_ROOT\\Software\\OpenOffice\\OpenOffice",
						"Path");
				return path.endsWith("\\") ? path.substring(0, path.length() - 1) : path;
			} else
				return "/opt/openoffice4";
		} catch (Throwable t) {
			return "";
		}
	}

	/**
	 * Not related to database validation but it is useful to compute now the
	 * name of the service
	 */
	private String serviceName() {
		if (FileUtil.isWindows()) {
			return ServiceUtil.findSuitableName("LogicalDOC-Community");
		} else
			return "";

	}
}