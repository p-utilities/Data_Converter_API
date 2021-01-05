package com.kakarot.serverclient.app.server;

import java.nio.file.Path;

public class ServerMain {
	
	public static void main(String[] args) {
		
		if(args.length != 2) {
			System.out.println("Not enough arguments to continue.");
			System.exit(1);
		}
		
		int port = Integer.parseInt(args[0]);
		String destinationPath = args[1];
		
		var serverMachine = new ServerMachine();
		serverMachine.sendDataToServer(port, Path.of(destinationPath));
		
	}
	
}
