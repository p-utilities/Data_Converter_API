package com.kakarot.data.converter.exceptions;

public class ProductIdNotUniqueException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ProductIdNotUniqueException() {
	}
	
	public ProductIdNotUniqueException(String message) {
		super(message);
	}
}
