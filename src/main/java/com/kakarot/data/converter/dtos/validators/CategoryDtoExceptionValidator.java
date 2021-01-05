package com.kakarot.data.converter.dtos.validators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.kakarot.data.converter.dtos.CategoryDto;
import com.kakarot.data.converter.exceptions.ProductIdException;
import com.kakarot.data.converter.exceptions.ProductIdNotUniqueException;

public class CategoryDtoExceptionValidator extends CategoryDtoValidator {
	
	private static CategoryDtoValidator instance;
	private ProductDtoValidator productValidator;
	
	public CategoryDtoExceptionValidator() {
	}
	
	private CategoryDtoExceptionValidator(ProductDtoValidator productValidator) {
		super(productValidator);
		this.productValidator = productValidator;
	}

	public static CategoryDtoValidator getValidator(ProductDtoValidator productValidator) {
		if (instance == null)
			instance = new CategoryDtoExceptionValidator(productValidator);

		return instance;
	}
	
	public static CategoryDtoValidator getValidator() {
		if (instance == null)
			instance = new CategoryDtoExceptionValidator();

		return instance;
	}

	@Override
	public void validate(List<CategoryDto> dtoList) throws ProductIdException, ProductIdNotUniqueException {
		List<String> dejavu = new ArrayList<String>();
		for(CategoryDto dto: dtoList)
			validateDto(dto, dejavu);
	}

	private void validateDto(CategoryDto dto, List<String> dejavu) throws ProductIdException, ProductIdNotUniqueException {
		var products = Arrays.asList(dto.getProducts());
		
		if(productValidator != null) {
			productValidator.validateIds(products);
		}
		
		for(String productId: products) {
			if(dejavu.contains(productId)) {
				throw new ProductIdNotUniqueException("Category " + dto + " has product value which is already present inside category list");
			}
		}

		dejavu.addAll(products);
	}
}
