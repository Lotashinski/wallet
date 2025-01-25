package com.github.lotashinski.wallet.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;


@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundHttpException extends HttpBadRequestException {

	private static final long serialVersionUID = 1L;

	
	public NotFoundHttpException() {
		super();
	}

	public NotFoundHttpException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NotFoundHttpException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFoundHttpException(String message) {
		super(message);
	}

	public NotFoundHttpException(Throwable cause) {
		super(cause);
	}

}
