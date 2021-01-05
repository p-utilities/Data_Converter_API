package com.kakarot.data.converter.parsers.reader.quicktest;

import com.kakarot.data.converter.dtos.CategoryDto;
import com.kakarot.data.converter.dtos.ProductDto;
import com.kakarot.data.converter.parsers.reader.XmlDocumentReader;
import com.kakarot.data.converter.services.DtoCreatorServiceImpl;

public class XmlUsage {
	public static void main(String[] args) throws Exception {
		var reader = new XmlDocumentReader(new DtoCreatorServiceImpl());

		String abPath = "/home/igor/eclipse-workspace/bosch/sample.xml";

		var collectedData = reader.parse(abPath, "categories", CategoryDto.class);
		System.out.println(collectedData);

		System.out.println();
		System.out.println();
		
		var collectedData2 = reader.parse(abPath, "items", ProductDto.class);
		System.out.println(collectedData2);

	}
}
