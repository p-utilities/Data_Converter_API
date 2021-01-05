package com.kakarot.bosch.dtos.validators;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.kakarot.data.converter.dtos.ProductDto;
import com.kakarot.data.converter.dtos.validators.ProductDtoExceptionValidator;
import com.kakarot.data.converter.dtos.validators.ProductDtoExcludeValidator;
import com.kakarot.data.converter.dtos.validators.ProductDtoValidator;
import com.kakarot.data.converter.exceptions.ProductIdException;

class ProductDtoValidatorTest {

	private static ProductDtoValidator excludeValidator;
	private static ProductDtoValidator exceptionValidator;
	private static final String VALID_REGEX = "[a-zA-Z0-9]{4}[\\.][a-zA-Z0-9]{3}[\\.][a-zA-Z0-9]{3}[-][0]{3}";

	@BeforeAll
	static void setup() {
		excludeValidator = ProductDtoExcludeValidator.getValidator(VALID_REGEX);
		exceptionValidator = ProductDtoExceptionValidator.getValidator(VALID_REGEX);

	}

	@Test
	void testProductDtoExcludeValidatorWithValidProducts() {
		var dtoList = listOfValidProducts();
		int startingSize = dtoList.size();

		try {
			excludeValidator.validate(dtoList);
		} catch (ProductIdException e) {
			// this is unreachable exception
		}

		assertEquals(startingSize, dtoList.size());
	}

	@Test
	void testProductDtoExcludeValidatorWithInvalidProducts() {
		// only one valid id => after validation size should be 1
		var dtoList = listOfInvalidProducts();

		try {
			excludeValidator.validate(dtoList);
		} catch (ProductIdException e) {
			// this is unreachable exception
		}

		int afterValidationSize = dtoList.size();
		assertEquals(0, afterValidationSize);
	}

	@Test
	void testProductDtoExcludeValidatorWithMixedProducts() {
		// only one valid id => after validation size should be 1
		var dtoList = listOfInvalidProducts();
		dtoList.addAll(listOfValidProducts());

		try {
			excludeValidator.validate(dtoList);
		} catch (ProductIdException e) {
			// this is unreachable exception
		}

		var validList = listOfValidProducts();
		assertEquals(validList.size(), dtoList.size());

		if (validList.size() == dtoList.size()) {
			for (ProductDto dto : validList) {
				assertTrue(dtoList.contains(dto));
			}
		} else {
			fail("size must be equal");
		}

	}

	@Test
	void testProductDtoExceptionValidatorWithValidProducts() {
		var dtoList = listOfValidProducts();

		try {
			for (ProductDto dto : dtoList) {
				exceptionValidator.validate(newListOfProducts(dto));
			}
		} catch (ProductIdException e) {
			fail("Should not throw any exception");
		}
	}

	@Test
	void testProductDtoExceptionValidatorWithInvalidProducts() {
		var dtoList = listOfInvalidProducts();

		for (ProductDto dto : dtoList)
			assertThrows(ProductIdException.class, () -> exceptionValidator.validate(newListOfProducts(dto)),
					"Invalid dto: " + dto);
	}

	@Test
	void testProductDtoExceptionValidatorWithMixedProducts() {
		var dtoList = listOfInvalidProducts();
		dtoList.addAll(listOfValidProducts());

		assertThrows(ProductIdException.class, () -> exceptionValidator.validate(dtoList));
	}

	private List<ProductDto> listOfValidProducts() {
		// XXXX.XXX.XXX-000
		return newListOfProducts(
					newProductDtoIdOnly("xxxx.xxx.xxx-000"), 
					newProductDtoIdOnly("1234.123.123-000"),
					newProductDtoIdOnly("aaaa.aaa.aaa-000"), 
					newProductDtoIdOnly("AAAA.AAA.AAA-000"),
					newProductDtoIdOnly("A1A1.A1A.A1A-000"), 
					newProductDtoIdOnly("a1a1.a1a.a1a-000")
				);
	}

	private List<ProductDto> listOfInvalidProducts() {
		return newListOfProducts(
					newProductDtoIdOnly("xxx.xx.xx-00"), // all parts are invalid
					newProductDtoIdOnly("2d5.A22.2FA-000"), // first part has only three characters - needs to be excluded
					newProductDtoIdOnly("5tqs.2A2.2A3-001"), // last part is not in 000 form - needs to be excluded
					newProductDtoIdOnly("EFAS.22A.A1A1-0001"), // third part has four characters - needs to be excluded
					newProductDtoIdOnly("2222.6081.225-000"), // second part has four characters - needs to be excluded
					newProductDtoIdOnly("PPPP1.PPP.PPP-000"), // first part has four characters - needs to be excluded
					newProductDtoIdOnly("PP1.PPP.PPP-000"), // first part has three characters - needs to be excluded
					newProductDtoIdOnly("PPP1,PPP.PPP-000"), // use of , after first part - needs to be excluded
					newProductDtoIdOnly("PPP1.PPP,PPP-000"), // use od , after second part- needs to be excluded
					newProductDtoIdOnly("PPP1.PPP.PPP*000") // use of * after third part - needs to be excluded
				);
	}

	private List<ProductDto> newListOfProducts(ProductDto... dtos) {
		List<ProductDto> dtoList = new ArrayList<ProductDto>();
		for (ProductDto dto : dtos)
			dtoList.add(dto);
		return dtoList;
	}

	private ProductDto newProductDtoIdOnly(String id) {
		var productDto = new ProductDto();
		productDto.setId(id);
		return productDto;
	}

}
