package com.tam.crm.exception;

public class UnregisteredUserException extends Exception {
	public UnregisteredUserException(String message, Throwable e) {
		super(message, e);
	}
}
