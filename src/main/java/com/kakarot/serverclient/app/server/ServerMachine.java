package com.kakarot.serverclient.app.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.kakarot.data.converter.Converter;
import com.kakarot.data.converter.exceptions.CategoryDuplicateProductException;
import com.kakarot.data.converter.exceptions.ProductIdException;
import com.kakarot.data.converter.exceptions.ProductIdNotUniqueException;

public class ServerMachine {

	public void sendDataToServer(int port, Path destinationPathTo) {

		checkFile(destinationPathTo.toString());

		System.out.println("Starting server socket");
		try (ServerSocket serverSocket = new ServerSocket(port);
				Socket clientSocket = serverSocket.accept();
				BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

			String message;

			var headerValues = readHeader(reader);
			String fileToReadType = headerValues.get("content-type:");
			int fileLength = Integer.parseInt(headerValues.get("content-length:"));
			
			if(fileToReadType == null)
				message = "Message: File type is unknown, please insert header for file type."
						+ " For xml files, use 'content-type: xml' and for csv files, use 'content-type: csv'.";
			else if(fileLength == 0)
				message = "Message: Content length is 0, no files are processed.";
			else {
				String dataToRead = getDataToRead(reader, fileLength);
				
				
				Path tempDataPath = createTempFileFromData(dataToRead, fileToReadType);
				
				message = proccessFile(tempDataPath.toString(), destinationPathTo.toString());

				Files.deleteIfExists(tempDataPath);
			}
			
			writer.append("Message: " + message);

			System.out.println("Shuting down server socket...");
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Please check port number and try again.");
		}

		System.out.println("Server is off.");
	}

	private String proccessFile(String destinationPathFrom, String destinationPathTo) {
		String message;

		Converter converter = new Converter();

		try {
			converter.convertData(destinationPathFrom, destinationPathTo);

			message = "Transfered file is successfuly transfered.";

		} catch (ProductIdException pe) {
			message = pe.getMessage();
		} catch (ProductIdNotUniqueException pnue) {
			message = pnue.getMessage();
		} catch (CategoryDuplicateProductException ce) {
			message = ce.getMessage();
		} catch (Exception e) {
			message = "Server side error";
			e.printStackTrace();
		}

		return message;
	}

	private Map<String, String> readHeader(BufferedReader reader) throws IOException {
		Map<String, String> resultMap = new HashMap<String, String>();
		String inputLine;
		while ((inputLine = reader.readLine()) != null) {
			if (inputLine.contains("content-type:"))
				resultMap.put("content-type:", inputLine.substring(inputLine.indexOf(":") + 1).stripLeading());

			if (inputLine.contains("content-length:"))
				resultMap.put("content-length:", inputLine.substring(inputLine.indexOf(":") + 1).stripLeading());
			
			if(inputLine.equals(""))
				break;
		}

		return resultMap;
	}

	private String getDataToRead(BufferedReader reader, int fileLength) throws IOException {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i <= fileLength; i++) {
			String line = reader.readLine();
			if(builder.length() == 0 && line.length() == 0)
				continue;
			
			builder.append(line);
			builder.append("\n");
		}
		
		String result = builder.toString();
		result.stripLeading();
		result.stripTrailing();
		return result;
	}

	private Path createTempFileFromData(String dataToRead, String fileToReadType) throws IOException {

		File tempFile = new File(
				System.getProperty("user.dir") + "/" + new Random().nextInt(11000) + "." + fileToReadType);

		FileOutputStream outStream = new FileOutputStream(tempFile);

		outStream.write(dataToRead.getBytes());

		outStream.close();

		return Path.of(tempFile.getAbsoluteFile().toString());
	}

	private void checkFile(String filePath) {
		if (!filePath.endsWith("xml")) {
			if (!filePath.endsWith("csv")) {
				System.out.println(filePath.endsWith("xml"));
				System.err.println("Please choose file with 'xml' or 'csv' extension.");
				System.err.println("Reenter the data and try again.");
				System.exit(0);
			}
		}
	}

}
