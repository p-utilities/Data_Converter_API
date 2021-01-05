package com.kakarot.data.converter.parsers.writer;

import com.kakarot.data.converter.parsers.CollectedData;

public interface DocumentWriter {

	<T> String parse(String filePath, CollectedData<T> collectedData) throws Exception;
	
}
