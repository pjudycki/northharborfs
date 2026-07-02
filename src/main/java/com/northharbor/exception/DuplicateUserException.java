package com.northharbor.exception;

public class DuplicateUserException extends RuntimeException {
	
	private String field;
	
	public DuplicateUserException(String field, String message) {
		super(message);
		this.field = field;
	}
	
	public String getField() {
		return field;
	}
	

}
