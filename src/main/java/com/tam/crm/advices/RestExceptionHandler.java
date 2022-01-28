package com.tam.crm.advices;

import com.tam.crm.exception.CrmDataException;
import com.tam.crm.exception.UnregisteredUserException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	private static Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

	@ExceptionHandler(value = { RuntimeException.class })
	protected ResponseEntity<Object> handleException(RuntimeException ex, WebRequest request) {
		log.error(ex.getMessage(), ex);

		return handleExceptionInternal(ex,
			generateAppError(ex, "Server error", request),
			new HttpHeaders(),
			HttpStatus.INTERNAL_SERVER_ERROR,
			request);
	}

	@ExceptionHandler(value = { CrmDataException.class})
	protected ResponseEntity<Object> handleDataException(CrmDataException ex, WebRequest request) {

		log.error(ex.getMessage(), ex);
		return handleExceptionInternal(ex,
			generateAppError(ex, "Database or query error", request),
			new HttpHeaders(),
			HttpStatus.INTERNAL_SERVER_ERROR,
			request);
	}
	@ExceptionHandler(value = { DataAccessException.class})
	protected ResponseEntity<Object> handleDataException(DataAccessException ex, WebRequest request) {

		log.error(ex.getMessage(), ex);
		return handleExceptionInternal(ex,
			generateAppError(ex, "Database or query error", request),
			new HttpHeaders(),
			HttpStatus.INTERNAL_SERVER_ERROR,
			request);
	}

	@ExceptionHandler(value = { UnregisteredUserException.class })
	protected ResponseEntity<Object> handleForbiddenException(UnregisteredUserException ex, WebRequest request) {

		log.error("forbidden", ex.getMessage());

		return handleExceptionInternal(ex,
			generateAppError(ex, "403: Forbidden", request),
			new HttpHeaders(),
			HttpStatus.FORBIDDEN,
			request);

	}

	@ExceptionHandler(value = { MethodNotAllowedException.class })
	protected ResponseEntity<Object> handleMethodNotAllowedException(MethodNotAllowedException ex, WebRequest request) {

		log.error("constraint", ex.getMessage());

		return handleExceptionInternal(ex,
			generateAppError(ex, "405: Method Not Allowed", request),
			new HttpHeaders(),
			HttpStatus.METHOD_NOT_ALLOWED,
			request);
	}

	private AppError generateAppError(Exception ex, String message, WebRequest request) {
		AppError appError = new AppError();

		appError.setExceptionMessage(ex.getMessage());
		appError.setExceptionClass(ex.getClass().toGenericString());
		appError.setDescription(message);
		appError.setContextPath(request.getDescription(false));
		appError.setTimestamp(LocalDateTime.now());
		return appError;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	private class AppError {
		String description;
		String exceptionClass;
		String exceptionMessage;
		String contextPath;
		LocalDateTime timestamp;
	}
}
