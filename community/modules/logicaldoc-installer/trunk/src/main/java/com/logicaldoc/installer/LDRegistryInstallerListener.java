package com.logicaldoc.installer;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.Variables;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.api.rules.RulesEngine;
import com.izforge.izpack.api.substitutor.VariableSubstitutor;
import com.izforge.izpack.core.os.RegistryDefaultHandler;
import com.izforge.izpack.event.RegistryInstallerListener;
import com.izforge.izpack.installer.data.UninstallData;
import com.izforge.izpack.installer.unpacker.IUnpacker;
import com.izforge.izpack.util.Housekeeper;

/**
 * Overrides the standard uninstall name, we want to be just APP_NAME
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 2.0
 */
public class LDRegistryInstallerListener extends RegistryInstallerListener {

	public LDRegistryInstallerListener(IUnpacker unpacker, VariableSubstitutor substituter, InstallData installData,
			UninstallData uninstallData, Resources resources, RulesEngine rules, Housekeeper housekeeper,
			RegistryDefaultHandler handler) {
		super(unpacker, substituter, installData, uninstallData, resources, rules, housekeeper, handler);
	}

	@Override
	protected String getUninstallName() {
		Variables variables = getInstallData().getVariables();
		return variables.get("APP_NAME");
	}

}
