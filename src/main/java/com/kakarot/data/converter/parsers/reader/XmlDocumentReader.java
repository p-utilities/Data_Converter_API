package com.kakarot.data.converter.parsers.reader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.kakarot.data.converter.parsers.CollectedData;
import com.kakarot.data.converter.services.DtoCreatorService;

public class XmlDocumentReader implements DocumentReader {

	private final DtoCreatorService objectCreator;

	public XmlDocumentReader(DtoCreatorService objectCreator) {
		this.objectCreator = objectCreator;
	}

	/**
	 * return Collected data. Return value cannot be null, but it is be empty when
	 * file is empty.
	 */
	@Override
	public <T> CollectedData<T> parse(String filePath, String parentNodeName, Class<T> typeToCollect) throws Exception {

		// CHECK FOR WRONG INPUT
		inputParametersCheck(filePath, parentNodeName, typeToCollect);
		fileCheck(filePath);

		CollectedData<T> result = new CollectedData<T>();

		// GET THE FILE FOR READING
		InputStream inputStream = Files.newInputStream(Paths.get(filePath));

		Document xmlDocument = null;
		try {
			xmlDocument = getXmlDocument(inputStream);
		} catch (SAXParseException ex) {
			// FILE IS EMPTY RETURN EMPTY COLLECTED DATA
			return result;
		}

		XPath xPath = XPathFactory.newInstance().newXPath();

		// NORMALIZE DATA FOR READING
		removeEmptyNodes(xPath, xmlDocument);

		// READ DATA
		Node rootNode = xmlDocument.getFirstChild();

		if (rootNode != null && rootNode.hasChildNodes()) {

			NodeList parentNodeList = xmlDocument.getElementsByTagName(parentNodeName);

			if (parentNodeList.getLength() > 0) {
				result.setParrentName(parentNodeName);
				Node parentNode = parentNodeList.item(0);
				NodeList childNodeList = parentNode.getChildNodes();

				if (childNodeList.getLength() > 0) {
					populateCollectedData(childNodeList, result, typeToCollect);
				}
			}
		}

		// CLOSE INPUT STREAM
		inputStream.close();

		return result;
	}

	private <T> void populateCollectedData(NodeList childNodeList, CollectedData<T> result, Class<T> typeToCollect)
			throws Exception {

		result.setTagName(childNodeList.item(0).getNodeName());

		var orderedFieldList = getOrderedFieldNames(objectCreator.getFieldNames(typeToCollect),
				(Node) childNodeList.item(0));
		result.setFieldNames(orderedFieldList);

		for (int i = 0; i < childNodeList.getLength(); i++) {
			var resultingValues = new HashMap<String, String[]>();
			findChild(childNodeList.item(i), result.getFieldNames(), resultingValues);
			result.setIndividualData(objectCreator.create(typeToCollect, resultingValues));
		}
	}

	private List<String> getOrderedFieldNames(List<String> requestedFieldNames, Node node) {

		List<String> orderedFieldList = new ArrayList<String>();

		NodeList nodes = node.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeName().equals("property")) {
				var nodeAttribute = ((Element) nodes.item(i)).getAttribute("propertyName");

				if (requestedFieldNames.contains(nodeAttribute)) {
					orderedFieldList.add(nodeAttribute);
				}
			}
		}

		if (orderedFieldList.size() < requestedFieldNames.size()) {
			for (String fieldName : requestedFieldNames) {
				if (!orderedFieldList.contains(fieldName))
					orderedFieldList.add(fieldName);
			}
		}

		return orderedFieldList;
	}

	private void removeEmptyNodes(XPath xPath, Document xmlDocument) throws XPathExpressionException {
		var emptyTextNodes = (NodeList) xPath.compile("//text()[normalize-space(.)='']").evaluate(xmlDocument,
				XPathConstants.NODESET);

		for (int i = 0; i < emptyTextNodes.getLength(); i++) {
			Node emptyTextNode = emptyTextNodes.item(i);
			emptyTextNode.getParentNode().removeChild(emptyTextNode);
		}

	}

	private <T> void findChild(Node node, List<String> searchedFields, Map<String, String[]> resultingValues) {

		if (node.hasAttributes()) {
			var propertyArttribute = ((Element) node).getAttribute("propertyName");
			if (searchedFields.contains(propertyArttribute)) {
				var values = collectValues(node.getChildNodes());
				if (values.length > 0) {
					resultingValues.put(propertyArttribute, values);
				}
			}
		} else if (node.hasChildNodes()) {
			var childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				findChild(childNodes.item(i), searchedFields, resultingValues);
			}
		}

	}

	private String[] collectValues(NodeList nodeList) {
		String[] result = null;

		for (int i = 0; i < nodeList.getLength(); i++) {
			var node = nodeList.item(i);
			result = new String[node.getChildNodes().getLength()];
			for (int j = 0; j < node.getChildNodes().getLength(); j++) {
				String value = collectValue(node.getChildNodes().item(j));
				if (value != null) {
					result[j] = value;
				}
			}
		}

		return result;
	}

	private String collectValue(Node node) {
		if (node.hasAttributes()) {
			return ((Element) node).getAttribute(node.getAttributes().item(0).getNodeName());
		} else {
			return node.getTextContent();
		}

	}

	private Document getXmlDocument(InputStream inputStream)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		return builder.parse(inputStream);
	}

	private <T> void inputParametersCheck(String filePath, String parentNodeName, Class<T> typeToCollect) {
		String errorMessage = null;
		if (filePath == null || filePath.isEmpty())
			errorMessage = "File path can not be null or empty";
		if (parentNodeName == null || parentNodeName.isEmpty())
			errorMessage = "Node name can not be null or empty";
		if (typeToCollect == null)
			errorMessage = "Type can not be null";

		if (errorMessage != null) {
			throw new IllegalStateException(errorMessage);
		}
	}

	private void fileCheck(String filePath) {
		if (!filePath.endsWith(".xml"))
			throw new IllegalStateException("File provided is not of type XML");
		Path path = Paths.get(filePath);
		if (!Files.exists(path))
			throw new IllegalAccessError("File provided does not exists");
	}

}