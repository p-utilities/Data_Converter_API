package com.kakarot.data.converter.exceptions;

public class CategoryDuplicateProductException extends Exception {

	private static final long serialVersionUID = 1L;

	public CategoryDuplicateProductException() {
	}
	
	public CategoryDuplicateProductException(String message) {
		super(message);
	}
	
}
