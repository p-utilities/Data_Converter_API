package com.kakarot.data.converter.parsers.reader;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.kakarot.data.converter.parsers.CollectedData;
import com.kakarot.data.converter.services.DtoCreatorService;

public class CsvDocumentReader implements DocumentReader {

	private DtoCreatorService objectCreator;
	private final String headerValue = "INSERT_UPDATE ";

	public CsvDocumentReader(DtoCreatorService objectCreator) {
		this.objectCreator = objectCreator;
	}

	/**
	 * return Collected data. Return value cannot be null, but it is be empty when
	 * file is empty.
	 */
	@Override
	public <T> CollectedData<T> parse(String filePath, String parentNodeName, Class<T> typeToCollect) throws Exception {
		File file = Paths.get(filePath).toFile();

		CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), CSVFormat.newFormat(';'));

		CollectedData<T> collectedData = new CollectedData<T>();

		if (findCommentValue(parser, parentNodeName)) {

			var headerValues = collectHeaderValues(parser, objectCreator.getFieldNames(typeToCollect));

			if (headerValues != null) {

				collectedData.setParrentName(parentNodeName);

				collectedData.setTagName(headerValues.get(0));

				List<String> fieldNames = new ArrayList<String>();
				for (int i = 1; i < headerValues.size(); i++) {
					fieldNames.add(headerValues.get(i));
				}
				
				collectedData.setFieldNames(fieldNames);

				collectData(parser, headerValues, typeToCollect, collectedData);
			}
		}

		return collectedData;
	}

	private <T> void collectData(CSVParser parser, Map<Integer, String> headerValues, Class<T> typeToCollect,
			CollectedData<T> collectedData) throws Exception {

		for (CSVRecord record : parser) {
			if (record.size() <= 1)
				break;

			Map<String, String[]> resultingValues = new HashMap<String, String[]>();

			for (int i = 1; i < record.size(); i++) {
				if (record.get(i).isEmpty()) {
					break;
				}

				String[] resultingValue = record.get(i).split(",");
				resultingValues.put(headerValues.get(i), resultingValue);

			}
			collectedData.setIndividualData(objectCreator.create(typeToCollect, resultingValues));
		}
	}

	private boolean findCommentValue(CSVParser parser, String parentNodeName) {
		for (CSVRecord record : parser) {
			for (String value : record) {
				if (value.contains("##" + parentNodeName)) {
					return true;
				}
			}
		}

		return false;
	}

	private Map<Integer, String> collectHeaderValues(CSVParser parser, List<String> requestedValues) {
		Map<Integer, String> headerValues = null;

		outer: for (CSVRecord record : parser) {

			for (int i = 0; i < record.size(); i++) {
				if (record.get(i).startsWith(headerValue)) {

					int counter = 0;
					headerValues = new HashMap<Integer, String>();

					for (String value : record) {
						if (value.startsWith(headerValue)) {
							String tagName = value.replace(headerValue, "");
							tagName = tagName.replaceFirst((tagName.charAt(0) + ""),
									(tagName.charAt(0) + "").toLowerCase());
							headerValues.put(counter++, tagName);
						} else if (!value.isEmpty()) {
							headerValues.put(counter++, value);
						}
					}

					break outer;
				}
			}
		}

		if (headerValues == null || headerValues.size() == 0)
			headerValues = null;

		return headerValues;
	}

}
