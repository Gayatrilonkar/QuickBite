package com.sit.qb.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import com.sit.qb.dtos.ApiErrorResponseDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiErrorResponseDto> handleResponseStatusException(
			ResponseStatusException exception,
			HttpServletRequest request) {
		HttpStatusCode statusCode = exception.getStatusCode();
		String reason = exception.getReason() != null ? exception.getReason() : "Request failed";
		return buildErrorResponse(statusCode, reason, request, null);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponseDto> handleValidationException(
			MethodArgumentNotValidException exception,
			HttpServletRequest request) {
		Map<String, String> validationErrors = new LinkedHashMap<>();
		for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
			validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}
		return buildErrorResponse(
				HttpStatus.BAD_REQUEST,
				"Validation failed",
				request,
				validationErrors);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiErrorResponseDto> handleConstraintViolationException(
			ConstraintViolationException exception,
			HttpServletRequest request) {
		Map<String, String> validationErrors = new LinkedHashMap<>();
		for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
			validationErrors.put(violation.getPropertyPath().toString(), violation.getMessage());
		}
		return buildErrorResponse(
				HttpStatus.BAD_REQUEST,
				"Validation failed",
				request,
				validationErrors);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiErrorResponseDto> handleTypeMismatchException(
			MethodArgumentTypeMismatchException exception,
			HttpServletRequest request) {
		String name = exception.getName();
		return buildErrorResponse(
				HttpStatus.BAD_REQUEST,
				name + " must be a valid value",
				request,
				null);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiErrorResponseDto> handleMissingRequestParameterException(
			MissingServletRequestParameterException exception,
			HttpServletRequest request) {
		return buildErrorResponse(
				HttpStatus.BAD_REQUEST,
				exception.getParameterName() + " query parameter is required",
				request,
				null);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponseDto> handleUnreadableMessageException(
			HttpMessageNotReadableException exception,
			HttpServletRequest request) {
		return buildErrorResponse(
				HttpStatus.BAD_REQUEST,
				"Request body is missing or malformed",
				request,
				null);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiErrorResponseDto> handleMethodNotSupportedException(
			HttpRequestMethodNotSupportedException exception,
			HttpServletRequest request) {
		return buildErrorResponse(
				HttpStatus.METHOD_NOT_ALLOWED,
				exception.getMessage(),
				request,
				null);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiErrorResponseDto> handleRuntimeException(
			RuntimeException exception,
			HttpServletRequest request) {

		String message = exception.getMessage() != null
				? exception.getMessage()
				: "Something went wrong";

		return buildErrorResponse(
				HttpStatus.BAD_REQUEST,
				message,
				request,
				null);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponseDto> handleException(
			Exception exception,
			HttpServletRequest request) {
		return buildErrorResponse(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"Internal server error",
				request,
				null);
	}

	private ResponseEntity<ApiErrorResponseDto> buildErrorResponse(
			HttpStatusCode statusCode,
			String message,
			HttpServletRequest request,
			Map<String, String> validationErrors) {
		HttpStatus status = HttpStatus.resolve(statusCode.value());
		String error = status != null ? status.getReasonPhrase() : "Error";
		ApiErrorResponseDto response = new ApiErrorResponseDto(
				LocalDateTime.now(),
				statusCode.value(),
				error,
				message,
				request.getRequestURI(),
				validationErrors);
		return ResponseEntity.status(statusCode).body(response);
	}
}
