package com.kakarot.serverclient.app.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;

public class ClientMain {

	public static void main(String[] args) {
		
		if(args.length != 3) {
			System.out.println("Not enough arguments to continue.");
			System.exit(1);
		}
		
		String hostName = args[0];
		int serverPort = Integer.parseInt(args[1]);
		String filePath = args[2];
		
		var clientMachine = new ClientMachine();
		clientMachine.sendDataToServer(Path.of(filePath), getInetAddress(hostName),	serverPort);
		
	}
	
	private static InetAddress getInetAddress(String hostName) {
			try {
				InetAddress hostAddress = InetAddress.getByName(hostName);
				return hostAddress;
			} catch (UnknownHostException e) {
				System.err.println("Input host name must be valid host name.");
				System.exit(1);
			}
			
			//this code can not be reached!
			return null;
	}
	
}
