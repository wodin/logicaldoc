package com.logicaldoc.installer;

import java.io.File;

import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.gui.log.Log;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.gui.InstallerFrame;
import com.izforge.izpack.panels.target.TargetPanel;


/**
 * The target directory selection panel, validate if the target folder is not an
 * existing LogicalDOC installation.
 * 
 * @author Alessandro Gasparini - LogicalDOC
 */
public class LDTargetPanel extends TargetPanel {

	private static final long serialVersionUID = 1L;

	public LDTargetPanel(Panel panel, InstallerFrame parent, GUIInstallData installData, Resources resources, Log log) {
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
