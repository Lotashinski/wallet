package com.github.lotashinski.wallet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class HttpBadRequestException extends RuntimeException {

	private static final long serialVersionUID = 9083702015186035430L;

	
	public HttpBadRequestException() {
		super();
	}

	public HttpBadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public HttpBadRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpBadRequestException(String message) {
		super(message);
	}

	public HttpBadRequestException(Throwable cause) {
		super(cause);
	}
	
}
