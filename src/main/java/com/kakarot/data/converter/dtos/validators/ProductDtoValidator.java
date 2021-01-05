package com.kakarot.data.converter.dtos.validators;

import java.util.List;

import com.kakarot.data.converter.dtos.ProductDto;
import com.kakarot.data.converter.exceptions.ProductIdException;

public abstract class ProductDtoValidator {
	
	public ProductDtoValidator() {
		// placeholder
	}
	
	public ProductDtoValidator(String regex) {
		// placeholder
	}

	public void validateIds(List<String> productIds) throws ProductIdException {
		//do nothing by default
	}

	public void validateName(String productName) {
		//do nothing by default
	}
	
	public void validateDescription(String productDescription) {
		//do nothing by default
	}

	public void validate(List<ProductDto> dtoList) throws ProductIdException {
		//do nothing by default
	}

}
