package com.kakarot.data.converter.parsers.writer;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import com.kakarot.data.converter.parsers.CollectedData;

public class PrettyXmlDocumentWriter implements DocumentWriter, XmlDocumentWriterMarker {

	private final DocumentWriter documentWriter;

	public PrettyXmlDocumentWriter(XmlDocumentWriterMarker documentWriter) {
		this.documentWriter = (DocumentWriter) documentWriter;
	}

	@Override
	public <T> String parse(String filePath, CollectedData<T> collectedData) throws Exception {
		String fileName = documentWriter.parse(filePath, collectedData);

		prettyDocument(filePath);

		return fileName;
	}

	private void prettyDocument(String absoluteFilePath) throws Exception {
		fullFilePathCheck(absoluteFilePath);

		Document xmlDocument = getDocument(absoluteFilePath);

		removeEmptyNodes(xmlDocument);

		transformDocument(xmlDocument, absoluteFilePath);
	}

	private Document getDocument(String absoluteFilePath) throws Exception {
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		FileInputStream inputStream = new FileInputStream(new File(absoluteFilePath));

		Document document = null;

		try {
			document = documentBuilder.parse(inputStream);
		} catch (SAXParseException e) {

		}

		return document;
	}

	private void transformDocument(Document xmlDocument, String filePathAndName) throws Exception {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();

		DOMSource domSource = new DOMSource(xmlDocument);

		StreamResult resultStream = new StreamResult(Files.newOutputStream(Paths.get(filePathAndName)));

		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "8");

		transformer.transform(domSource, resultStream);
	}

	private void removeEmptyNodes(Document xmlDocument) throws XPathExpressionException {
		if (xmlDocument != null) {
			XPath xPath = XPathFactory.newInstance().newXPath();
			var emptyTextNodes = (NodeList) xPath.compile("//text()[normalize-space(.)='']").evaluate(xmlDocument,
					XPathConstants.NODESET);

			for (int i = 0; i < emptyTextNodes.getLength(); i++) {
				Node emptyTextNode = emptyTextNodes.item(i);
				emptyTextNode.getParentNode().removeChild(emptyTextNode);
			}
		}
	}

	private void fullFilePathCheck(String absoluteFilePath) {
		String errorMessage = "";

		if (absoluteFilePath == null || absoluteFilePath.isEmpty())
			errorMessage = "File path can not be null or empty";

		if (!absoluteFilePath.endsWith(".xml"))
			errorMessage = "File is not xml type";

		if (!errorMessage.equals(""))
			throw new IllegalStateException(errorMessage);
	}

}
