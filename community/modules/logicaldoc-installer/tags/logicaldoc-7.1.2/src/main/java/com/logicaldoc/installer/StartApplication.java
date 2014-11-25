package com.logicaldoc.installer;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.util.Locale;

import com.izforge.izpack.panels.process.AbstractUIProcessHandler;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.installer.util.Exec;
import com.logicaldoc.installer.util.FileUtil;
import com.logicaldoc.installer.util.Log;
import com.logicaldoc.installer.util.Wget;

public class StartApplication {
	public void run(AbstractUIProcessHandler handler, String[] args) {
		try {
			String root = args[0].trim();
			if (!root.endsWith("/"))
				root = root + "/";
			int port = Integer.parseInt(args[1].trim());
			String service = args[2].trim();
			String lang = args[3].trim();
			

			// Run the service, run two times because the webapp does some
			// initializations
			try {
				if (FileUtil.isWindows())
					Exec.exec("net start " + service, null, null, 60);
				else
					Exec.exec(root + "bin/logicaldoc.sh start", null, null, 60);
			} catch (Throwable e) {
				Log.error(e.getMessage(), e);
			}

			// Wait the startup
			int count = 0;
			while (count < 30 && !Wget.wget("http://localhost:" + port + "/frontend.jsp").contains("UP AND RUNNING")) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				count++;
			}

			try {
				if (FileUtil.isWindows())
					Exec.exec("net stop " + service, null, null, 60);
				else
					Exec.exec(root + "bin/logicaldoc.sh stop", null, null, 120);
			} catch (Throwable e) {
				Log.error(e.getMessage(), e);
			}

			// Wait the shutdown
			count = 0;
			while (count < 30 && Wget.wget("http://localhost:" + port + "/frontend.jsp").contains("UP AND RUNNING")) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				count++;
			}

			try {
				if (FileUtil.isWindows())
					Exec.exec("net start " + service, null, null, 60);
				else
					Exec.exec(root + "bin/logicaldoc.sh start", null, null, 120);
			} catch (Throwable e) {
				Log.error(e.getMessage(), e);
			}

			handler.logOutput("Application started", false);

			/*
			 * Display the tip on how to enter the application
			 */
			String tip = I18N.message("installationend", new Locale(lang), new String[] { "LogicalDOC Community", });
			tip += "\n\nhttp://localhost:" + port;
			handler.logOutput(tip, false);
						
			File file = new File(root + "bin/enter.txt");
			FileWriter writer = new FileWriter(file);
			writer.write(tip);
			writer.flush();
			writer.close();
			
			/*
			 * Try to open the tip with the GUI also
			 */
			try{
				Desktop.getDesktop().open(file);
			}catch(Throwable t){
			}
		} catch (Throwable e) {
			Log.error(e.getMessage(), e);
		}
	}
}