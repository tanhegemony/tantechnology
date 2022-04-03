package edu.poly.thtechnology.exception;

public class StorageException extends RuntimeException{

	// source + contrustor from superclass
	public StorageException(String message, Exception e) {
		super(message, e);
	}

	public StorageException(String message) {
		super(message);
	}
	
}
