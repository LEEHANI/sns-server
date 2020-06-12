package com.may.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler {
	@ExceptionHandler(value = {IllegalArgumentException.class})
	public ExceptionResponse invalidArgumentHandle(Exception e) {
		return new ExceptionResponse(HttpStatus.BAD_REQUEST, e.getMessage());
	}
}
