package com.kakarot.data.converter;

import com.kakarot.data.converter.parsers.CollectedData;
import com.kakarot.data.converter.parsers.reader.DocumentReader;
import com.kakarot.data.converter.parsers.writer.DocumentWriter;

public class ReaderWriterService {

	private DocumentReader documentReader;
	private DocumentWriter documentWriter;
	private String absoulutePathFrom;
	private String absolutePathTo;

	private ReaderWriterService() {

	}

	public static Builder getBuilder() {
		return new Builder();
	}

	public static class Builder {
		private final ReaderWriterService instance = new ReaderWriterService();

		public Builder setDocumentReader(DocumentReader documentReader, String absoulutePathFrom) {
			instance.documentReader = documentReader;
			instance.absoulutePathFrom = absoulutePathFrom;
			return this;
		}

		public Builder setDocumentWriter(DocumentWriter documentWriter, String absolutePathTo) {
			instance.documentWriter = documentWriter;
			instance.absolutePathTo = absolutePathTo;
			return this;
		}

		public ReaderWriterService build() {
			return instance;
		}
	}

	public <T> CollectedData<T> readFile(String parentName, Class<T> typeToCollect) throws Exception {
		return documentReader.parse(absoulutePathFrom, parentName, typeToCollect);
	}

	public <T> void writeToFile(CollectedData<T> collectedData) throws Exception {
		documentWriter.parse(absolutePathTo, collectedData);
	}

	public DocumentReader getDocumentReader() {
		return documentReader;
	}

	public void setDocumentReader(DocumentReader documentReader) {
		this.documentReader = documentReader;
	}

	public DocumentWriter getDocumentWriter() {
		return documentWriter;
	}

	public void setDocumentWriter(DocumentWriter documentWriter) {
		this.documentWriter = documentWriter;
	}

	public String getAbsoulutePathFrom() {
		return absoulutePathFrom;
	}

	public void setAbsoulutePathFrom(String absoulutePathFrom) {
		this.absoulutePathFrom = absoulutePathFrom;
	}

	public String getAbsolutePathTo() {
		return absolutePathTo;
	}

	public void setAbsolutePathTo(String absolutePathTo) {
		this.absolutePathTo = absolutePathTo;
	}

}
