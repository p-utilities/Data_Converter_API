package com.kakarot.bosch.dtos.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.kakarot.data.converter.dtos.CategoryDto;
import com.kakarot.data.converter.dtos.validators.CategoryDtoExceptionValidator;
import com.kakarot.data.converter.dtos.validators.CategoryDtoExcludeValidator;
import com.kakarot.data.converter.dtos.validators.CategoryDtoValidator;
import com.kakarot.data.converter.exceptions.ProductIdException;
import com.kakarot.data.converter.exceptions.ProductIdNotUniqueException;

class CategoryDtoValidatorTest {

	private static CategoryDtoValidator excludeValidator;
	private static CategoryDtoValidator exceptionValidator;

	@BeforeAll
	static void setup() {
		excludeValidator = CategoryDtoExcludeValidator.getValidator();
		exceptionValidator = CategoryDtoExceptionValidator.getValidator();
	}

	@Test
	void testCategoryDtoExcludeValidatorWithValidCategories() {
		var dtoList = validListOfCategories();
		int startingSize = dtoList.size();

		try {
			excludeValidator.validate(dtoList);
		} catch (ProductIdNotUniqueException e) {
			// this is unreachable exception
			fail("Exclude validator should never throw ProductIdNotUniqueException");
		} catch (ProductIdException pe) {
			fail("In this test there should not be any product id validators");
		}

		assertEquals(startingSize, dtoList.size());

	}

	@Test
	void testCategoryDtoExcludeValidatorWithInvalidCategories() {
		var dtoList = invalidListOfCategories();

		try {
			excludeValidator.validate(dtoList);
		} catch (ProductIdNotUniqueException e) {
			// this is unreachable exception
			fail("Exclude validator should never throw ProductIdNotUniqueException");
		} catch (ProductIdException pe) {
			fail("In this test there should not be any product id validators");
		}

		assertEquals(1, dtoList.size());
	}

	@Test
	void testCategoryDtoExcludeValidatorWithMixedCategories() {
		var dtoList = invalidListOfCategories();
		dtoList.addAll(validListOfCategories());

		try {
			excludeValidator.validate(dtoList);
		} catch (ProductIdNotUniqueException e) {
			// this is unreachable exception
			fail("Exclude validator should never throw ProductIdNotUniqueException");
		} catch (ProductIdException pe) {
			fail("In this test there should not be any product id validators");
		}

		assertEquals(validListOfCategories().size() + 1, dtoList.size());
	}

	@Test
	void testCategoryDtoExceptionValidatorWithValidCategories() {
		var dtoList = validListOfCategories();
		
		try {
			exceptionValidator.validate(dtoList);
		} catch (ProductIdNotUniqueException e) {
			// this is unreachable exception
			fail("This test includes valid categories with no unique products. Should not throw this error");
		} catch (ProductIdException pe) {
			fail("In this test there should not be any product id validators");
		}
	}

	@Test
	void testCategoryDtoExceptionValidatorWithInvalidCategories() {
		assertThrows(ProductIdNotUniqueException.class, () -> exceptionValidator.validate(invalidListOfCategories()));
	}

	@Test
	void testCategoryDtoExceptionValidatorWithMixedCategories() {
		var dtoList = validListOfCategories();
		dtoList.addAll(invalidListOfCategories());
		assertThrows(ProductIdNotUniqueException.class, () -> exceptionValidator.validate(dtoList));
	}

	private List<CategoryDto> validListOfCategories() {
		List<CategoryDto> dtoList = new ArrayList<CategoryDto>();

		dtoList.add(newCategoryDtoProductIdsOnly("xxxx.xxx.xxx-000", "xxx1.xx1.xx1-000", "xxx2.xx2.xx2-000"));
		dtoList.add(newCategoryDtoProductIdsOnly("xxx3.xx3.xx3-000", "xxx4.xx4.xx4-000"));
		dtoList.add(newCategoryDtoProductIdsOnly("xxx5.xx5.xx5-000"));

		return dtoList;
	}

	private List<CategoryDto> invalidListOfCategories() {
		List<CategoryDto> dtoList = new ArrayList<CategoryDto>();

		dtoList.add(newCategoryDtoProductIdsOnly("xxx6.xx6.xx6-000", "xxx7.xx7.xx7-000", "xxx8.xx8.xx8-000"));
		dtoList.add(newCategoryDtoProductIdsOnly("xxx9.xx9.xx9-000", "xxx7.xx7.xx7-000"));

		return dtoList;
	}

	private CategoryDto newCategoryDtoProductIdsOnly(String... productIds) {
		var categoryDto = new CategoryDto();
		categoryDto.setProducts(productIds);
		return categoryDto;
	}

}
