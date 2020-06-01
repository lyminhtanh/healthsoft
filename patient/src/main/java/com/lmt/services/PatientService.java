package com.lmt.services;

import java.util.List;
import java.util.Map;

import com.lmt.entities.Patient;

public interface PatientService {
	List<Patient> search(final Map<String, String> requestParams);

	Patient create(Patient patient);

	Patient update(String uuid, Patient patient);

	Patient delete(String uuid);

	Patient get(String uuid);
}
