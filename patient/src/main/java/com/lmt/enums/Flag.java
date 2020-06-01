package com.lmt.enums;

import lombok.Getter;

public enum Flag {

	ON((short) 1), OFF((short) 0);

	@Getter
	private short value;

	private Flag(short value) {
		this.value = value;
	}
}
