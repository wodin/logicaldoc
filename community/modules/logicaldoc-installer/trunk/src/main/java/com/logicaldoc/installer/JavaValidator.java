package com.logicaldoc.installer;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.installer.DataValidator;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.installer.util.FileUtil;
import com.logicaldoc.installer.util.JavaUtil;
import com.logicaldoc.installer.util.Log;

/**
 * Checks the if there is a valid java for the chosen architecture
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.5
 */
public class JavaValidator implements DataValidator {

	@Override
	public boolean getDefaultAnswer() {
		return false;
	}

	@Override
	public String getErrorMessageId() {
		return I18N.message("invalidjava");
	}

	@Override
	public String getWarningMessageId() {
		return I18N.message("invalidjava");
	}

	@Override
	public Status validateData(InstallData data) {
		if (!FileUtil.isWindows()) {
			data.setVariable(Constants.JAVAHOME, "-");
			data.setVariable(Constants.JREHOME, "-");
			return Status.OK;
		}

		String architecture = data.getVariable(Constants.ARCHITECTURE);

		Log.info("Checking Java availability for architecture " + architecture, null);
		String javahome = JavaUtil.getJDK("64bit".equals(architecture));
		if (javahome == null)
			javahome = "-";
		data.setVariable(Constants.JAVAHOME, javahome);
		Log.info("Found JDK home: " + javahome, null);

		String jrehome = JavaUtil.getJRE("64bit".equals(architecture));
		if (jrehome == null)
			jrehome = "-";
		data.setVariable(Constants.JREHOME, jrehome);
		Log.info("Found JRE home: " + jrehome, null);

		if ("-".equals(javahome) && "-".equals(jrehome))
			return Status.WARNING;
		else
			return Status.OK;
	}
}