package com.kakarot.data.converter.parsers.writer.quicktest;

import com.kakarot.data.converter.dtos.CategoryDto;
import com.kakarot.data.converter.dtos.ProductDto;
import com.kakarot.data.converter.parsers.reader.DocumentReader;
import com.kakarot.data.converter.parsers.reader.XmlDocumentReader;
import com.kakarot.data.converter.parsers.writer.DocumentWriter;
import com.kakarot.data.converter.parsers.writer.PrettyXmlDocumentWriter;
import com.kakarot.data.converter.parsers.writer.XmlDocumentWriter;
import com.kakarot.data.converter.services.DtoCreatorServiceImpl;

public class XmlUsage {
	public static void main(String[] args) throws Exception {

		DocumentReader reader = new XmlDocumentReader(new DtoCreatorServiceImpl());

		String path = "/home/igor/eclipse-workspace/application_1/sample.xml";

		var catData = reader.parse(path, "categories", CategoryDto.class);
		System.out.println(catData);
		
		var prodData = reader.parse(path, "items", ProductDto.class);
		System.out.println(prodData);

		
		
		DocumentWriter wr = new PrettyXmlDocumentWriter(new XmlDocumentWriter());
		path = "/home/igor/eclipse-workspace/application_1/ndsadaesto.xml";
		
		String resultingFileName = wr.parse(path, catData);
		System.out.println(resultingFileName);
		
		wr.parse(path, prodData);
		System.out.println(path + resultingFileName);
		
		System.out.println("sve je zavrseno");
	}
}
