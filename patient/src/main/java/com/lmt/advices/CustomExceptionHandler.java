package com.lmt.advices;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.lmt.exceptions.MsgException;
import com.lmt.exceptions.MsgModel;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
	/**
	 * handle all errors from @Valid annotation
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		// Get all errors
		List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(x -> x.getDefaultMessage())
				.collect(Collectors.toList());

		MsgModel msgModel = MsgModel.builder().status(HttpStatus.BAD_REQUEST.value()).messages(errors).trace(ex.getMessage())
				.build();

		return new ResponseEntity<>(msgModel, headers, HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(MsgException.class)
	public final ResponseEntity<Object> handleAllExceptions(MsgException ex) {
		return new ResponseEntity<>(ex.getMsgModel(), HttpStatus.valueOf(ex.getMsgModel().getStatus()));
	}

	@ExceptionHandler({ DataIntegrityViolationException.class, ConstraintViolationException.class })
	public final ResponseEntity<Object> handleCheckConstraint(DataIntegrityViolationException ex) {
		
		MsgModel msgModel = MsgModel.builder().status(HttpStatus.BAD_REQUEST.value())
				.messages(Arrays.asList("Invalid parameters")).trace(ex.getMessage()).build();

		return new ResponseEntity<>(msgModel, HttpStatus.valueOf(msgModel.getStatus()));
	}
}
