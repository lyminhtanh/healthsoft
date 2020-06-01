package com.lmt.constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public interface CommonConst {

	public interface CommonCharacter {

		public static final String ASTERISK = "*";
	}

	public static final Map<String, String> paramColumnMap = Collections.unmodifiableMap(new HashMap<>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6677568885675322868L;

		{
			put(SearchQueryParam.FIRST_NAME, PatientColumnName.FIRST_NAME);
			put(SearchQueryParam.LAST_NAME, PatientColumnName.LAST_NAME);
			put(SearchQueryParam.PATIENT_ID, PatientColumnName.PATIENT_ID);
			put(SearchQueryParam.GENDER, PatientColumnName.GENDER);
			put(SearchQueryParam.DOB, PatientColumnName.DOB);
			put(SearchQueryParam.WITH_DELETED, PatientColumnName.DELETE_FLAG);
		}
	});

	public interface SearchQueryParam {

		public static final String FIRST_NAME = "FirstName";

		public static final String LAST_NAME = "LastName";

		public static final String PATIENT_ID = "PatientID";

		public static final String GENDER = "gender";

		public static final String DOB = "dob";

		public static final String WITH_DELETED = "withDeleted";
	}

	public interface PatientColumnName {

		public static final String FIRST_NAME = "firstName";

		public static final String LAST_NAME = "lastName";

		public static final String PATIENT_ID = "patientId";

		public static final String GENDER = "gender";

		public static final String DOB = "dob";

		public static final String DELETE_FLAG = "deleteFlag";
	}
}
