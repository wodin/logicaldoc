package com.logicaldoc.util.system;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarLoader;
import org.hyperic.sigar.cmd.Shell;
import org.hyperic.sigar.cmd.SigarCommandBase;

/**
 * Display cpu information for each cpu found on the system. Also
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.6
 */
public class CpuInfo extends SigarCommandBase {

	public boolean displayTimes = true;

	public CpuInfo(Shell shell) {
		super(shell);
	}

	public CpuInfo() {
		super();
	}

	public String getUsageShort() {
		return "Display cpu information";
	}

	private void output(CpuPerc cpu) {
		println("User Time....." + CpuPerc.format(cpu.getUser()));
		println("Sys Time......" + CpuPerc.format(cpu.getSys()));
		println("Idle Time....." + CpuPerc.format(cpu.getIdle()));
		println("Wait Time....." + CpuPerc.format(cpu.getWait()));
		println("Nice Time....." + CpuPerc.format(cpu.getNice()));
		println("Combined......" + CpuPerc.format(cpu.getCombined()));
		println("Irq Time......" + CpuPerc.format(cpu.getIrq()));
		if (SigarLoader.IS_LINUX) {
			println("SoftIrq Time.." + CpuPerc.format(cpu.getSoftIrq()));
			println("Stolen Time...." + CpuPerc.format(cpu.getStolen()));
		}
		println("");
	}

	public void output(String[] args) throws SigarException {
		org.hyperic.sigar.CpuInfo[] infos = this.sigar.getCpuInfoList();

		CpuPerc[] cpus = this.sigar.getCpuPercList();

		org.hyperic.sigar.CpuInfo info = infos[0];
		long cacheSize = info.getCacheSize();
		println("Vendor........." + info.getVendor());
		println("Model.........." + info.getModel());
		println("Mhz............" + info.getMhz());
		println("Total CPUs....." + info.getTotalCores());
		if ((info.getTotalCores() != info.getTotalSockets()) || (info.getCoresPerSocket() > info.getTotalCores())) {
			println("Physical CPUs.." + info.getTotalSockets());
			println("Cores per CPU.." + info.getCoresPerSocket());
		}

		if (cacheSize != Sigar.FIELD_NOTIMPL) {
			println("Cache size...." + cacheSize);
		}
		println("");

		if (!this.displayTimes) {
			return;
		}

		for (int i = 0; i < cpus.length; i++) {
			println("CPU " + i + ".........");
			output(cpus[i]);
		}

		println("Totals........");
		output(this.sigar.getCpuPerc());
	}

	public static void main(String[] args) throws Exception {
		new CpuInfo().processCommand(args);
	}

	/**
	 * Gets the average CPUs idle percentage.
	 * 
	 * @return The idle percentage, a value of 1 stands for a fully loaded CPU
	 */
	public static double getCpuIdle() {
		Sigar sigar = null;
		try {
			sigar = new Sigar();
			return sigar.getCpuPerc().getIdle();
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			if (sigar != null)
				sigar.close();
		}
		return 0;
	}
}
