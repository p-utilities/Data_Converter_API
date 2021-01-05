package com.kakarot.data.converter.dtos.validators;

import java.util.List;

import com.kakarot.data.converter.dtos.CategoryDto;
import com.kakarot.data.converter.exceptions.ProductIdException;
import com.kakarot.data.converter.exceptions.ProductIdNotUniqueException;

public abstract class CategoryDtoValidator {
	
	public CategoryDtoValidator() {
		//placeholder
	}
	
	public CategoryDtoValidator(ProductDtoValidator productValidator) {
		//placeholder
	}
	
	public void validateId(String categoryId) {
		//do nothing by default
	}
	
	public void validateName(String categoryName) {
		//do nothing by default
	}
	
	public void validateProductList(List<String> productList)  throws ProductIdException, ProductIdNotUniqueException {
		//do nothing by default
	}
	
	public void validate(List<CategoryDto> categories) throws ProductIdException, ProductIdNotUniqueException {
		//do nothing by default
	}
	
}
