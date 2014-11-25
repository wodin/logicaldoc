package com.logicaldoc.installer;

import java.io.File;
import java.util.List;

import com.izforge.izpack.api.event.ProgressListener;
import com.izforge.izpack.api.event.UninstallerListener;
import com.izforge.izpack.api.handler.AbstractUIProgressHandler;
import com.logicaldoc.installer.util.Exec;
import com.logicaldoc.installer.util.FileUtil;

public class Uninstall implements UninstallerListener {

	private static boolean windowsServiceDeleted = false;

	@Override
	public void afterDelete(File arg0) {

	}

	@Override
	public void afterDelete(List<File> arg0, ProgressListener arg1) {

	}

	@Override
	public void afterDelete(File arg0, AbstractUIProgressHandler arg1) throws Exception {

	}

	@Override
	public void afterDeletion(List arg0, AbstractUIProgressHandler arg1) throws Exception {

	}

	@Override
	public void beforeDelete(List<File> arg0) {
		deleteWindowsService();
	}

	@Override
	public void beforeDelete(File file) {
		deleteWindowsService();
	}

	@Override
	public void beforeDelete(File file, AbstractUIProgressHandler handler) throws Exception {
		deleteWindowsService();
	}

	@Override
	public void beforeDeletion(List arg0, AbstractUIProgressHandler arg1) throws Exception {
		deleteWindowsService();
	}

	@Override
	public void initialise() {

	}

	@Override
	public boolean isFileListener() {
		return true;
	}

	private void deleteWindowsService() {
		if (!FileUtil.isWindows() || Uninstall.windowsServiceDeleted)
			return;

		try {
			Exec.exec("sc stop LogicalDOC-Community", null, null, 120);
		} catch (Throwable e) {
		}
		
		try {
			Exec.exec("sc stop LogicalDOC-Community", null, null, 120);
		} catch (Throwable e) {
		}

		try {
			Exec.exec("sc delete LogicalDOC-Community", null, null, 120);
			Uninstall.windowsServiceDeleted = true;
		} catch (Throwable e) {
		}
	}
}
