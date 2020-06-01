package com.lmt.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CastUtil {

	public static LocalDate toDate(String dateString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		try {
			return LocalDate.parse(dateString, formatter);
		} catch (DateTimeParseException e) {
			// TODO: logging
		}
		return null;
	}

	public static boolean toBoolean(String boolString) {
		return Boolean.valueOf(boolString);
	}
}
