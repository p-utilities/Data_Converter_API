package com.kakarot.data.converter.dtos.validators;

import java.util.List;
import java.util.regex.Pattern;

import com.kakarot.data.converter.dtos.ProductDto;
import com.kakarot.data.converter.exceptions.ProductIdException;

public class ProductDtoExceptionValidator extends ProductDtoValidator {

	private String regex = "";
	private static ProductDtoValidator INSTANCE;

	private ProductDtoExceptionValidator() {
	}

	/**
	 * 
	 * @param regex
	 * @throws PatternSyntaxException if regex can not be combiled
	 * @return
	 */
	public static ProductDtoValidator getValidator(String regex) {
		if (INSTANCE == null)
			INSTANCE = new ProductDtoExceptionValidator();

		Pattern.compile(regex);

		((ProductDtoExceptionValidator) INSTANCE).regex = regex;

		return INSTANCE;
	}

	@Override
	public void validate(List<ProductDto> dtoList) throws ProductIdException {
		for(ProductDto dto: dtoList) 
			validateId(dto.getId());
	}

	private void validateId(String productId) throws ProductIdException {
		if (!productId.matches(regex))
			throw new ProductIdException("Product with product ID : [" + productId + "] has invalid ID");
	}

	@Override
	public void validateIds(List<String> productIds) throws ProductIdException {
		for(String id: productIds)
			validateId(id);
	}

}
