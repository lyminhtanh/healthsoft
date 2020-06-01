package com.lmt.exceptions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MsgModel {

	@Builder.Default
	private int status = HttpStatus.BAD_REQUEST.value();

	@Builder.Default
	private List<String> messages = new ArrayList<String>();

	private String trace;

	public boolean hasMessage() {
		return !messages.isEmpty();
	}
}
