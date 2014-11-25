package com.logicaldoc.installer;

import java.io.File;
import java.util.Properties;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.installer.panel.PanelView;
import com.izforge.izpack.panels.target.TargetPanelHelper;
import com.izforge.izpack.util.Console;

/**
 * Console implementation of the {@link TargetPanel}.
 */
public class TargetConsolePanel extends com.izforge.izpack.panels.target.TargetConsolePanel {

	private static final long serialVersionUID = 1L;

	public TargetConsolePanel(PanelView<Console> panel) {
		super(panel);
	}

	@Override
	public boolean run(InstallData installData, Properties properties) {
		String path = properties.getProperty(InstallData.INSTALL_PATH);
		if (TargetPanel.isExistingInstallation(path)) {
			System.err.println(installData.getMessages().get("TargetPanel.existingLogicalDOC"));
			return false;
		} else {
			return super.run(installData, properties);
		}
	}

	@Override
	public boolean run(InstallData installData, Console console) {
		String defaultPath = TargetPanelHelper.getPath(installData);
		if (defaultPath == null) {
			defaultPath = "";
		}

		String path = console.prompt("Select target path [" + defaultPath + "] ", null);
		if (path != null) {
			path = path.trim();
			if (path.isEmpty()) {
				path = defaultPath;
			}
			path = installData.getVariables().replace(path);

			if (TargetPanel.isExistingInstallation(path)) {
				console.println(installData.getMessages().get("TargetPanel.existingLogicalDOC"));
				return false;
			}
			
			if (TargetPanelHelper.isIncompatibleInstallation(path)) {
				console.println(getIncompatibleInstallationMsg(installData));
				return run(installData, console);
			} else if (!path.isEmpty()) {
				File selectedDir = new File(path);
				if (selectedDir.exists() && selectedDir.isDirectory() && selectedDir.list().length > 0) {
					console.println(installData.getMessages().get("TargetPanel.warn"));
				}
				installData.setInstallPath(path);
				return promptEndPanel(installData, console);
			}
			return run(installData, console);
		} else {
			return false;
		}
	}

	private String getIncompatibleInstallationMsg(InstallData installData) {
		return installData.getMessages().get("TargetPanel.incompatibleInstallation");
	}
}
