package com.lmt.exceptions;

import java.util.Arrays;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MsgException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8334523538842386265L;

	private MsgModel msgModel;

	public MsgException(String message, HttpStatus status) {
		msgModel = MsgModel.builder().status(status.value()).messages(Arrays.asList(message)).build();
	}
}
