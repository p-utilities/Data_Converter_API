package com.kakarot.serverclient.app.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientMachine {

	public void sendDataToServer(Path filePath, InetAddress serverAddress, int serverPort) {

		checkFile(filePath);

		String fileType = getFileType(filePath);

		try (
				Socket socket = new Socket(serverAddress.getHostName(), serverPort);
				PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
			) {
			System.out.println("Client socket is up and running...");
			
			//PREPARE DATA
			StringBuilder builder = new StringBuilder();
			BufferedReader dataReader = new BufferedReader(new FileReader(filePath.toFile()));
			int counter = 0;
			String dataLine;
			
			while((dataLine = dataReader.readLine()) != null) {
				builder.append(dataLine);
				builder.append("\r\n");
				counter++;
			}
			
			dataReader.close();
			
			// WRITING DATA TO THE SERVER
			sendHeader(writer, fileType, counter);

			writer.write(builder.toString());
			writer.flush();
			
			// READING DATA FROM SERVER
			String responseDataLine;

			while ((responseDataLine = reader.readLine()) != null) {
				readResponseHeader(responseDataLine);
			}

			System.out.println("Shuting down client socket...");
		} catch (UnknownHostException e) {
			System.err.println("Ovde ide greska da host ne postoji");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("ovde ide greska da socket nije mogao da otvori input ili output");
			e.printStackTrace();
		}
	}
	
	private void readResponseHeader(String dataLine) {
		if (dataLine.startsWith("Message:")) {
			System.out.println(dataLine.substring(dataLine.indexOf(":")).stripLeading());
		}
	}

	private void sendHeader(PrintWriter writer, String fileType, int fileLength) {

		writer.append("content-type: " + fileType + "\r\n");
		writer.append("content-length: " + fileLength + "\r\n");
		writer.append("\r\n\r\n");

	}

	private String getFileType(Path filePath) {
		if (filePath.toString().endsWith("xml"))
			return "xml";
		else
			return "csv";
	}

	private void checkFile(Path filePath) {
		if (!Files.exists(filePath)) {
			System.err.println("File with filepath : " + filePath + " does not exists.");
			System.err.println("Reenter data and try again.");
			System.exit(0);
		}

		if (!filePath.endsWith("xml") && filePath.endsWith("csv")) {
			if (!filePath.endsWith("csv")) {
				System.out.println(filePath.toString());
				System.out.println(filePath.endsWith("csv"));
				System.err.println("Please choose file with 'xml' or 'csv' extension.");
				System.err.println("Reenter the data and try again.");
				System.exit(0);
			}
		}
	}
}
