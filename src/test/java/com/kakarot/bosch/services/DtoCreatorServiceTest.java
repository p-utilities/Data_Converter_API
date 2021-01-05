package com.kakarot.bosch.services;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.kakarot.data.converter.dtos.CategoryDto;
import com.kakarot.data.converter.dtos.ProductDto;
import com.kakarot.data.converter.services.DtoCreatorService;
import com.kakarot.data.converter.services.DtoCreatorServiceImpl;

class DtoCreatorServiceTest {

	private static DtoCreatorService service;

	@BeforeAll
	static void setup() {
		service = new DtoCreatorServiceImpl();
	}

	@Test
	void testNullEception() {
		assertThrows(NullPointerException.class, () -> service.getFieldNames(null));
		assertThrows(NullPointerException.class, () -> service.create(null, null));
	}

	@Test
	void testProductDtoCreation() {
		String id = "id";
		String name = "name";
		String description = "description";

		ProductDto product = new ProductDto();
		product.setId(id);
		product.setName(name);
		product.setDescription(description);

		Map<String, String[]> values = new HashMap<String, String[]>();
		values.put("id", new String[] { id });
		values.put("name", new String[] { name });
		values.put("description", new String[] { description });

		ProductDto createdDto = null;
		try {
			createdDto = service.create(ProductDto.class, values);
		} catch (Exception e) {
			fail("Should not throw an error");
		}

		assertEquals(product.getId(), createdDto.getId());
		assertEquals(product.getName(), createdDto.getName());
		assertEquals(product.getDescription(), createdDto.getDescription());
	}

	@Test
	void testCategoryDtoCreation() {
		String id = "id";
		String name = "name";
		String[] products = new String[] {};

		CategoryDto category = new CategoryDto();
		category.setId(id);
		category.setName(name);
		category.setProducts(products);

		Map<String, String[]> values = new HashMap<String, String[]>();
		values.put("id", new String[] { id });
		values.put("name", new String[] { name });
		values.put("products", products);

		CategoryDto createdDto = null;

		try {
			createdDto = service.create(CategoryDto.class, values);
		} catch (Exception e) {
			fail("Should not throw an error");
		}

		assertEquals(category.getId(), createdDto.getId(), "Id is not the same");
		assertEquals(category.getName(), createdDto.getName(), "Name is not the same");
		assertTrue("Products is not the same", areArraysEqual(category.getProducts(), category.getProducts()));
	}

	@Test
	void testDtoFields() {
		var productFields = new String[] { "id", "name", "description" };
		var categoryFields = new String[] { "id", "name", "products" };

		// Checking productDto fields
		String[] genFields = null;

		try {
			genFields = getStringArray(service.getFieldNames(ProductDto.class));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Should not throw any exception");
		}

		assertTrue("Product fields are not equal", areArraysEqual(productFields, genFields));

		// Checking categoryDto fields
		try {
			genFields = getStringArray(service.getFieldNames(CategoryDto.class));
		} catch (Exception e) {
			fail("Should not throw any exception");
		}

		assertTrue("Category fields are not equal", areArraysEqual(categoryFields, genFields));

	}

	private String[] getStringArray(List<String> l) {
		String[] result = new String[l.size()];
		for (int i = 0; i < l.size(); i++) {
			result[i] = l.get(i);
		}
		
		return result;
	}

	boolean areArraysEqual(String[] p1, String[] p2) {
		if (p1.length != p2.length)
			return false;

		for (String product1 : p1) {
			boolean result = false;
			for (String product2 : p2) {
				if (product1.equals(product2)) {
					result = true;
					break;
				}
			}
			if (!result)
				return false;
		}

		return true;
	}

	boolean areProductsDtoEqual(ProductDto p1, ProductDto p2) {
		if (!p1.getId().equals(p2.getId()))
			return false;

		if (!p1.getName().equals(p2.getName()))
			return false;

		if (!p1.getDescription().equals(p2.getDescription()))
			return false;

		return true;
	}
}
