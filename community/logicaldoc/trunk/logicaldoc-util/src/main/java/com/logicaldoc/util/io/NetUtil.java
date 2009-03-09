package com.logicaldoc.util.io;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

/**
 * Some network utility methods.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class NetUtil {

	/**
	 * Checks if a port is available
	 * 
	 * @param port The port to check
	 * @return
	 */
	public static boolean available(int port) {
		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}

			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					/* should not be thrown */
				}
			}
		}

		return false;
	}
}
