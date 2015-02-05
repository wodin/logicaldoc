package com.logicaldoc.installer.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WinFirewall {
	
	public static void main(String[] args) throws IOException{
		WinFirewall.openPort(333);
	}
	
	public static void openPort(int port) throws IOException{
		List<String> command=new ArrayList<String>();
		command.add("netsh");
		command.add("advfirewall");
		command.add("firewall");
		command.add("add");
		command.add("rule");
		command.add("name=Port " + port);
		command.add("dir=in");
		command.add("action=allow");
		command.add("service=any");
		command.add("localip=any");
		command.add("remoteip=any");
		command.add("localport=" + port);
		command.add("remoteport=any");
		command.add("protocol=tcp");
		command.add("interfacetype=any");
		command.add("security=notrequired");
		command.add("edge=no");
		command.add("profile=any");
		command.add("enable=yes");
		System.out.println(command.toString().replace(',', ' '));
		
		Exec.exec(command);
	}
}
