package com.logicaldoc.util;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import javax.management.MBeanServerConnection;

import com.sun.management.OperatingSystemMXBean;

public class SystemLoad {


	public static void main(String[] args) throws IOException {
		// long CommittedVirtualMemorySize =
		// osMBean.getCommittedVirtualMemorySize();
		// System.out.println("amount of virtual memory that is guaranteed to be available to the running process in bytes: "+CommittedVirtualMemorySize);
		// long FreePhysicalMemorySize = osMBean.getFreePhysicalMemorySize();
		// System.out.println("amount of free physical memory in bytes: "+FreePhysicalMemorySize);
		// long FreeSwapSpaceSize = osMBean.getFreeSwapSpaceSize();
		// System.out.println("the amount of free swap space in bytes: "+FreeSwapSpaceSize);
		// long ProcessCpuTime = osMBean.getProcessCpuTime();
		// System.out.println("the CPU time used by the process on which the Java virtual machine is running in nanoseconds: "+ProcessCpuTime);
		// long TotalPhysicalMemorySize = osMBean.getTotalPhysicalMemorySize();
		// System.out.println("the total amount of physical memory in bytes: "+TotalPhysicalMemorySize);
		// long TotalSwapSpaceSize = osMBean.getTotalSwapSpaceSize();
		// System.out.println("the total amount of swap space in bytes: "+TotalSwapSpaceSize);
		// double SystemLoadAverage = osMBean.getSystemLoadAverage();
		// System.out.println("the system load average for the last minute: "
		// + nf.format(SystemLoadAverage));

		while (true) {
			int[] cpuLoad = getCpuLoad();
			System.out.println("the 'recent cpu usage' for the whole system: " + cpuLoad[0]);
			System.out.println("the 'recent cpu usage' for the JVM  process: " + cpuLoad[1]);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * Retrieve the CPU load:<br>
	 * <ol>
	 * <li>first value: the 'recent cpu usage' for the whole system</li>
	 * <li>second value: the 'recent cpu usage' for the JVM process</li>
	 * </ol>
	 */
	public static int[] getCpuLoad() {
		MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();
		OperatingSystemMXBean osMBean = null;
		try {
			osMBean = ManagementFactory.newPlatformMXBeanProxy(mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME,
					OperatingSystemMXBean.class);
		} catch (Throwable e) {
			return new int[] { 0, 0 };
		}

		int systemCpuLoad = 0;
		double tmp = osMBean.getSystemCpuLoad();
		if (tmp > 0) {
			systemCpuLoad = (int) Math.round(tmp*100);
		}

		int processCpuLoad = 0;
		tmp = osMBean.getProcessCpuLoad();
		if (tmp > 0) {
			processCpuLoad = (int) Math.round(tmp*100);
		}

		return new int[] { systemCpuLoad, processCpuLoad };
	}
}