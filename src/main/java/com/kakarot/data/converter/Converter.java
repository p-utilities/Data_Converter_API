package com.kakarot.data.converter;

import com.kakarot.data.converter.dtos.CategoryDto;
import com.kakarot.data.converter.dtos.ProductDto;
import com.kakarot.data.converter.dtos.validators.CategoryDtoExceptionValidator;
import com.kakarot.data.converter.dtos.validators.CategoryDtoValidator;
import com.kakarot.data.converter.dtos.validators.ProductDtoExceptionValidator;
import com.kakarot.data.converter.dtos.validators.ProductDtoValidator;
import com.kakarot.data.converter.exceptions.CategoryDuplicateProductException;
import com.kakarot.data.converter.exceptions.ProductIdException;
import com.kakarot.data.converter.exceptions.ProductIdNotUniqueException;
import com.kakarot.data.converter.parsers.reader.CsvDocumentReader;
import com.kakarot.data.converter.parsers.reader.DocumentReader;
import com.kakarot.data.converter.parsers.reader.XmlDocumentReader;
import com.kakarot.data.converter.parsers.writer.CsvDocumentWriter;
import com.kakarot.data.converter.parsers.writer.DocumentWriter;
import com.kakarot.data.converter.parsers.writer.PrettyXmlDocumentWriter;
import com.kakarot.data.converter.parsers.writer.XmlDocumentWriter;
import com.kakarot.data.converter.services.DtoCreatorServiceImpl;

public class Converter {

	private String productParentNameFrom;
	private String productParentNameTo;
	private String productNameTo;

	public void convertData(String absolutePathFrom, String absolutePathTo) throws ProductIdException, ProductIdNotUniqueException, CategoryDuplicateProductException, Exception {

		String fromType = absolutePathFrom.substring(absolutePathFrom.lastIndexOf(".") + 1);
		String toType = absolutePathTo.substring(absolutePathTo.lastIndexOf(".") + 1);
		
		// CREATE READER
		DocumentReader reader = createDocumentReader(fromType);

		// CREATE WRITER
		DocumentWriter writer = createDocumentWriter(toType);

		// CREATE SERVICE
		ReaderWriterService service = ReaderWriterService
											.getBuilder()
											.setDocumentReader(reader, absolutePathFrom)
											.setDocumentWriter(writer, absolutePathTo)
											.build();

		// READ DATA FROM THE FILE
		var collectedProductData = service.readFile(productParentNameFrom, ProductDto.class);
		var collectedCategoryData = service.readFile("categories", CategoryDto.class);
		
		// SET FIELD VALUES DIFFERENCES
		collectedProductData.setParrentName(productParentNameTo);
		collectedProductData.setTagName(productNameTo);
		
		// DO VALIDATION
		String productIdRegex = "[a-zA-Z0-9]{4}[\\.][a-zA-Z0-9]{3}[\\.][a-zA-Z0-9]{3}[-][0]{3}";

		ProductDtoValidator productValidator = ProductDtoExceptionValidator.getValidator(productIdRegex);
		productValidator.validate(collectedProductData.getData());

		CategoryDtoValidator categoryValidator = CategoryDtoExceptionValidator.getValidator(productValidator);
		categoryValidator.validate(collectedCategoryData.getData());
		
		// WRITE DATA TO THE FILE
		service.writeToFile(collectedProductData);
		service.writeToFile(collectedCategoryData);

	}

	private DocumentReader createDocumentReader(String fileType) {
		DocumentReader reader;
		if (fileType.endsWith("xml")) {
			productParentNameFrom = "items";
			reader = new XmlDocumentReader(new DtoCreatorServiceImpl());
		} else {
			productParentNameFrom = "products";
			reader = new CsvDocumentReader(new DtoCreatorServiceImpl());
		}

		return reader;
	}

	private DocumentWriter createDocumentWriter(String fileType) {
		DocumentWriter writer;
		if (fileType.endsWith("xml")) {
			productParentNameTo = "items";
			productNameTo = "item";
			writer = new PrettyXmlDocumentWriter(new XmlDocumentWriter());
		} else {
			productParentNameTo = "products";
			productNameTo = "product";
			writer = new CsvDocumentWriter();
		}
		return writer;
	}

}
