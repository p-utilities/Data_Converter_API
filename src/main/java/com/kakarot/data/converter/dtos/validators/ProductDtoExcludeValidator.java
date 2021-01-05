package com.kakarot.data.converter.dtos.validators;

import java.util.List;
import java.util.regex.Pattern;

import com.kakarot.data.converter.dtos.ProductDto;

public class ProductDtoExcludeValidator extends ProductDtoValidator {

	private String regex;
	private static ProductDtoValidator INSTANCE;

	private ProductDtoExcludeValidator() {
	}

	public static ProductDtoValidator getValidator(String regex) {
		if (INSTANCE == null)
			INSTANCE = new ProductDtoExcludeValidator();

		Pattern.compile(regex);

		((ProductDtoExcludeValidator) INSTANCE).regex = regex;

		return INSTANCE;
	}

	@Override
	public void validate(List<ProductDto> dtoList) {
		dtoList.removeIf(dto -> {
			return !isValidId(dto.getId());
		});
	}

	private boolean isValidId(String productId) {
		if (productId.matches(regex))
			return true;

		return false;
	}

	@Override
	public void validateIds(List<String> productIds) {
		productIds.removeIf(productId -> !isValidId(productId));
	}

}
