package com.kakarot.data.converter.dtos.validators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.kakarot.data.converter.dtos.CategoryDto;
import com.kakarot.data.converter.exceptions.ProductIdException;

public class CategoryDtoExcludeValidator extends CategoryDtoValidator {

	private static CategoryDtoValidator instance;
	private ProductDtoValidator productValidator;
	
	public CategoryDtoExcludeValidator() {
	}
	
	private CategoryDtoExcludeValidator(ProductDtoValidator productValidator) {
		super(productValidator);
		this.productValidator = productValidator;
	}

	public static CategoryDtoValidator getValidator(ProductDtoValidator productValidator) {
		if (instance == null)
			instance = new CategoryDtoExcludeValidator(productValidator);

		return instance;
	}
	
	public static CategoryDtoValidator getValidator() {
		if (instance == null)
			instance = new CategoryDtoExcludeValidator();

		return instance;
	}

	@Override
	public void validate(List<CategoryDto> dtoList) {
		List<String> dejavu = new ArrayList<String>();
		dtoList.removeIf(dto -> {
			try {
				return validateDto(dto, dejavu);
			} catch (ProductIdException e) {
				return true;
			}
		});
	}

	private boolean validateDto(CategoryDto dto, List<String> dejavu) throws ProductIdException {
		var products = Arrays.asList(dto.getProducts());
		
		if(productValidator != null) {
			productValidator.validateIds(products);
		}
		
		boolean isProductSeen = false;
		
		for(String productId: products) {
			if(dejavu.contains(productId)) {
				isProductSeen = true;
				break;
			}
		}
		
		if(!isProductSeen)
			dejavu.addAll(products);

		return isProductSeen;
	}

}
