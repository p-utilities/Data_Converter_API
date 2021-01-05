package com.kakarot.data.converter.parsers.writer;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.kakarot.data.converter.parsers.CollectedData;

public class XmlDocumentWriter implements DocumentWriter, XmlDocumentWriterMarker {

	/**
	 * return file name -> example: fileName.xml if collected data is empty, empty
	 * file is created
	 */
	@Override
	public <T> String parse(String filePath, CollectedData<T> collectedData) throws Exception {
		// CHECK FOR WRONG INPUT
		if (collectedData == null)
			throw new IllegalStateException("Collected data can not be null");
		
		fullFilePathCheck(filePath);

		// OPEN INPUT STREAM IF THERE IS EXISTING FILE
		InputStream inputStream = getInputStremIfExists(filePath);

		Document xmlDocument = getDocument(inputStream);

		// INSERT VALUES IN DOCUMENT
		xmlDocument = parseData(xmlDocument, collectedData);

		// OPEN OUTPUT STREAM FOR SAVE
		OutputStream outputStream = Files.newOutputStream(Paths.get(filePath));

		saveToFile(outputStream, xmlDocument);

		// IF THERE IS INPUT STREAM, CLOSE IT
		if (inputStream != null)
			inputStream.close();

		// CLOSE OUTPUT STREAM
		outputStream.flush();
		outputStream.close();

		return filePath.substring(filePath.lastIndexOf("/") + 1);

	}

	private <T> Document parseData(Document xmlDocument, CollectedData<T> collectedData) throws Exception {
		
		if (collectedData.getData() != null && collectedData.getData().size() > 0) {
			Element root = getRootElement(xmlDocument);
			
			List<T> dataList = collectedData.getData();
			List<String> fieldNames = collectedData.getFieldNames();
			String parentNode = collectedData.getParrentName();
			String nodeName = collectedData.getTagName();

			Element parentElement = getParentElement(xmlDocument, root, parentNode);
			root.appendChild(parentElement);

			for (T data : dataList) {
				Element nodeElement = xmlDocument.createElement(nodeName);
				parentElement.appendChild(nodeElement);
				BeanInfo beanInfo = Introspector.getBeanInfo(data.getClass());
				PropertyDescriptor pds[] = beanInfo.getPropertyDescriptors();

				for (String fieldName : fieldNames) {
					for (PropertyDescriptor pd : pds) {
						Method getterMethod = pd.getReadMethod();
						if (getterMethod != null) {
							if (getterMethodFieldName(getterMethod).equals(fieldName)) {
								var value = getterMethod.invoke(data);
								Element propertyNode = xmlDocument.createElement("property");
								propertyNode.setAttribute("propertyName", fieldName);
								Element valueElement = getElementValues(xmlDocument, value, fieldName);
								propertyNode.appendChild(valueElement);
								nodeElement.appendChild(propertyNode);
							}
						}
					}
				}
			}

		}
		xmlDocument.normalize();
		xmlDocument.normalizeDocument();

		return xmlDocument;
	}

	private Element getParentElement(Document xmlDocument, Element root, String parentNode) {
		Element parentElement;
		NodeList rootList = root.getChildNodes();
		if(rootList.getLength() > 0) {
			for(int i = 0; i<rootList.getLength();i++) {
				if(rootList.item(i).getNodeName().equals(parentNode))
					return (Element) rootList.item(i);
			}
		} 
		
		parentElement = xmlDocument.createElement(parentNode);

		return parentElement;
	}

	private Element getElementValues(Document xmlDocument, Object passedValue, String fieldName) {
		Element element = null;
		if (passedValue.getClass().getCanonicalName().equals(String.class.getCanonicalName())) {
			element = xmlDocument.createElement("value");
			element.setTextContent((String) passedValue);
		} else if (passedValue.getClass().getSimpleName().equals(String[].class.getSimpleName())) {
			fieldName = fieldName.substring(0, fieldName.length() - 1);
			var linksNodeName = fieldName.concat("Links");
			var linkNodeName = fieldName.concat("Link");

			element = xmlDocument.createElement(linksNodeName);

			var values = (String[]) passedValue;
			for (String value : values) {
				Element linkNode = xmlDocument.createElement(linkNodeName);
				linkNode.setAttribute("id", value);

				element.appendChild(linkNode);
			}
		}

		return element;
	}

	private String getterMethodFieldName(Method method) {
		String fieldName = method.getName();

		fieldName = fieldName.replaceFirst("get", "");
		fieldName = fieldName.replace(fieldName.charAt(0), Character.toLowerCase(fieldName.charAt(0)));

		return fieldName;
	}

	private Element getRootElement(Document xmlDocument) {
		Element root = null;
		if (xmlDocument.getChildNodes().getLength() > 0) {
			root = (Element) xmlDocument.getFirstChild();
		} else {
			root = xmlDocument.createElement("root");
			xmlDocument.appendChild(root);
		}

		return root;
	}

	private InputStream getInputStremIfExists(String filePath) throws Exception {
		Path path = Paths.get(filePath);
		if (Files.exists(path)) {
			return Files.newInputStream(path);
		}
		return null;
	}

	private <T> Document getDocument(InputStream inputStream) throws Exception {
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document xmlDocument;

		if (inputStream != null) {
			try {
			xmlDocument = documentBuilder.parse(inputStream);
			} catch (SAXException e) {
				//do nothing
			}
		} 
			
		xmlDocument = documentBuilder.newDocument();
		
		return xmlDocument;
	}

	private void saveToFile(OutputStream outputStream, Document xmlDocument) throws Exception {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		DOMSource domSource = new DOMSource(xmlDocument);

		StreamResult resultStream = new StreamResult(outputStream);

		transformer.transform(domSource, resultStream);
	}

	private void fullFilePathCheck(String filePathAndName) {
		String errorMessage = "";

		if (filePathAndName == null || filePathAndName.isEmpty())
			errorMessage = "File path can not be null or empty";

		if (!filePathAndName.endsWith(".xml"))
			errorMessage = "File is not xml type";

		if (!errorMessage.equals(""))
			throw new IllegalStateException(errorMessage);
	}

}
