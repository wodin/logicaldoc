package com.logicaldoc.installer;

import java.io.File;

import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.gui.log.Log;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.gui.InstallerFrame;

/**
 * The target directory selection panel, validate if the target folder is not an
 * existing LogicalDOC installation.
 * 
 * @author Marco Meschieri - Logical Objects
 */
public class TargetPanel extends com.izforge.izpack.panels.target.TargetPanel {

	private static final long serialVersionUID = 1L;

	public TargetPanel(Panel panel, InstallerFrame parent, GUIInstallData installData, Resources resources, Log log) {
		super(panel, parent, installData, resources, log);
	}

	@Override
	public boolean isValidated() {
		if (isExistingInstallation(getPath())) {
			emitError(getString("installer.error"), getString("TargetPanel.existingLogicalDOC"));
			return false;
		}

		return super.isValidated();
	}

	/*
	 * Checks if the target folder is a LogicalDOC installation
	 */
	public static boolean isExistingInstallation(String path) {
		if (path == null || path.equals(""))
			return false;

		File targetPath = new File(path);
		File conf = new File(targetPath, "conf");
		File contextFile = new File(conf, "context.properties");

		com.logicaldoc.installer.util.Log.info("Checking the existence of " + contextFile, null);
		boolean exists = contextFile.exists();
		if (exists)
			com.logicaldoc.installer.util.Log.error("Detected existing installation", null);
		return exists;
	}
}
