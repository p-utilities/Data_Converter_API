package com.kakarot.data.converter.parsers.reader.quicktest;

import com.kakarot.data.converter.dtos.CategoryDto;
import com.kakarot.data.converter.dtos.ProductDto;
import com.kakarot.data.converter.parsers.reader.CsvDocumentReader;
import com.kakarot.data.converter.services.DtoCreatorServiceImpl;

public class CsvUsage {
	public static void main(String[] args) throws Exception {
		CsvDocumentReader r = new CsvDocumentReader(new DtoCreatorServiceImpl());

		String filePath = "/home/igor/eclipse-workspace/bosch/sample.csv";

		var data = r.parse(filePath, "products", ProductDto.class);
		System.out.println(data);

		System.out.println();
		System.out.println();

		var data2 = r.parse(filePath, "categories", CategoryDto.class);
		System.out.println(data2);
	}
}
