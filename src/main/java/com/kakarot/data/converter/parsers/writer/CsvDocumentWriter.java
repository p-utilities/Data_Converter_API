package com.kakarot.data.converter.parsers.writer;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.kakarot.data.converter.parsers.CollectedData;

public class CsvDocumentWriter implements DocumentWriter {
	private final String HEADER = "INSERT_UPDATE";

	/**
	 * return file name -> example: fileName.xml if collected data is empty, empty
	 * file is created
	 */
	@Override
	public <T> String parse(String absoluteFilePath, CollectedData<T> collectedData) throws Exception {

		// CHECK FOR WRONG INPUT
		if (collectedData == null)
			throw new IllegalStateException("Collection data cannot be null");

		filePathCheck(absoluteFilePath);

		// CREATE FILE WRITER TO APPEND NEW FILES IF OLD ARE PRESENT
		FileWriter writer = new FileWriter(Paths.get(absoluteFilePath).toFile(), true);

		// IF THERE ARE NO DATA
		if (collectedData.getData() == null || collectedData.getData().size() == 0) {
			writer.flush();
			writer.close();
			return absoluteFilePath.substring(absoluteFilePath.lastIndexOf("/") + 1);
		}

		// IF DATA EXISTS THEN ADD LINE BREAK TO MAKE ROOM FOR NEW DATA
		if (Files.exists(Paths.get(absoluteFilePath))) {
			writer.append("\n");
		}

		// PARSE COLLECTED DATA
		insertPerentName(writer, collectedData.getParrentName());

		String nodeName = getNodeName(collectedData.getTagName());

		insertHeader(writer, nodeName, collectedData.getFieldNames());
		
		insertValues(writer, collectedData.getData(), collectedData.getFieldNames());

		// CLOSE WRITER
		writer.flush();
		writer.close();

		return absoluteFilePath.substring(absoluteFilePath.lastIndexOf("/") + 1);
	}

	private void insertPerentName(FileWriter writer, String parentName) throws Exception {
		String comment = "##" + parentName;
		writer.append(comment + "\n\n");
	}

	private <T> void insertValues(FileWriter writer, List<T> dataList, List<String> fieldNames) throws Exception {

		for (T data : dataList) {
			StringBuilder builder = new StringBuilder();
			builder.append(";");

			BeanInfo beanInfo = Introspector.getBeanInfo(data.getClass());
			PropertyDescriptor pds[] = beanInfo.getPropertyDescriptors();

			for (String fieldName : fieldNames) {
				for (PropertyDescriptor pd : pds) {
					Method getterMethod = pd.getReadMethod();
					if (getterMethod != null) {
						if (getterMethodFieldName(getterMethod).equals(fieldName)) {
							var value = getterMethod.invoke(data);
							if (value instanceof String[]) {
								for (String s : (String[]) value) {
									builder.append(s);
									builder.append(",");
								}
								builder.deleteCharAt(builder.length() - 1);
							} else
								builder.append(value);
							builder.append(";");
						}
					}
				}
			}

			builder.append("\n");

			writer.append(builder.toString());
		}

	}

	private void insertHeader(FileWriter writer, String nodeName, List<String> fieldNames) throws Exception {
		StringBuilder builder = new StringBuilder();
		builder.append(HEADER);
		builder.append(" ");
		builder.append(nodeName);
		builder.append(";");

		for (String field : fieldNames) {
			builder.append(field);
			builder.append(";");
		}

		builder.append("\n");

		writer.append(builder.toString());
	}

	private String getNodeName(String tagName) {
		String nodeName = tagName.replace((tagName.charAt(0) + ""), (tagName.charAt(0) + "").toUpperCase());
		return nodeName;
	}

	private void filePathCheck(String filePath) {
		String errorMessage = "";
		if (filePath == null)
			errorMessage = "File path and file name cannot be null";
		else if (filePath.isEmpty())
			errorMessage = "File path and file name cannot be empty";
		else if (!filePath.endsWith(".csv"))
			errorMessage = "File should be of type CSV";

		if (!errorMessage.equals(""))
			throw new IllegalStateException(errorMessage);
	}

	private String getterMethodFieldName(Method method) {
		String fieldName = method.getName();

		fieldName = fieldName.replaceFirst("get", "");
		fieldName = fieldName.replace(fieldName.charAt(0), Character.toLowerCase(fieldName.charAt(0)));

		return fieldName;
	}

}
