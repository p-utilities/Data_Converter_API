package com.kakarot.desktop.app;

/**
 * Hello world!
 *
 */
public class MainApp {

	public static void main(String[] args) throws Exception {
		
		if(args.length != 2) {
			System.out.println("Not enough arguments to continue.");
			System.exit(1);
		}
		
		new ThisAppFrame(args[0], args[1]);
		
	}
}
