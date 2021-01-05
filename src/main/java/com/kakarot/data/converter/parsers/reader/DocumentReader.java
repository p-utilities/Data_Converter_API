package com.kakarot.data.converter.parsers.reader;

import com.kakarot.data.converter.parsers.CollectedData;

public interface DocumentReader {

	<T> CollectedData<T> parse(String filePath, String parentNodeName, Class<T> typeToCollect) throws Exception;

}