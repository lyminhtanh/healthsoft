package com.lmt.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.lmt.entities.Patient;
import com.lmt.enums.Flag;
import com.lmt.exceptions.MsgException;
import com.lmt.modules.FullTextSearchModule;
import com.lmt.repositories.PatientRepository;


@Service
public class PatientServiceImpl implements PatientService{
	private PatientRepository patientRepository;
	
	@Autowired
	private FullTextSearchModule fullTextSearchModule;

	@Autowired
	public PatientServiceImpl(PatientRepository patientRepository) {
		this.patientRepository = patientRepository;
	}

	@Override
	public Patient create(Patient patient) {
		// Check existence
		if (patientRepository.findByPatientId(patient.getPatientId()) != null) {
			throw new MsgException("Patient exists", HttpStatus.BAD_REQUEST);
		}

		// check existing patientId with soft-delete
		Patient softDeletedPatient = patientRepository.findSoftDeletedByPatientId(patient.getPatientId());
		if (softDeletedPatient != null) {
			System.out.println("dbPatient");
			// Re-active patient
			patient.setId(softDeletedPatient.getId());
			patient.setDeleteFlag(Flag.OFF.getValue());
			return patientRepository.save(patient);
		}
		System.out.println("dbPatient not found");
		patient.setDeleteFlag(Flag.OFF.getValue());
		return patientRepository.save(patient);
	}

	@Override
	public Patient delete(String uuid) {

		Patient patient = validateUUID(uuid);

		patient.setDeleteFlag(Flag.ON.getValue());

		return patientRepository.save(patient);
	}

	@Override
	public Patient get(String uuid) {
		System.out.println("uuid is" + uuid);

		Patient patient = validateUUID(uuid);

		return patient;
	}

	@Override
	public Patient update(String uuid, Patient patient) {

		Patient dbPatient = validateUUID(uuid);
		
		// If the PatientID in the request body is different from the existing
		// PatientID, and the new PatientID already exists in the DB => 400
		if(!dbPatient.getPatientId().equals(patient.getPatientId()) &&
				patientRepository.findByPatientId(patient.getPatientId()) != null) {
			throw new MsgException("invalid update existing patientId", HttpStatus.BAD_REQUEST);
		}

		return patientRepository.save(patient);
	}

	/**
	 * validate UUID and return found Patient entity
	 * 
	 * @param uuid
	 * @return
	 */
	private Patient validateUUID(String uuid) {
		UUID uuidObject = null;
		try {
			uuidObject = UUID.fromString(uuid);
		} catch (IllegalArgumentException e) {
			// invalid UUID => 400
			throw new MsgException("invalid UUID", HttpStatus.BAD_REQUEST);
		}

		Patient patient = patientRepository.findByUUID(uuidObject);
		
		// valid UUID not found in DB => 404
		if(patient == null) {
			throw new MsgException("UUID not found in DB", HttpStatus.NOT_FOUND);
		}
		return patient;
	}

	@Override
	public List<Patient> search(Map<String, String> requestParams) {
		return fullTextSearchModule.search(requestParams);
//		return (List<Patient>) patientRepository.findAll();
	}
}
