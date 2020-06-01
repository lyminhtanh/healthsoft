package com.lmt.controllers;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lmt.entities.Patient;
import com.lmt.services.PatientService;

@RestController
@RequestMapping(path = "/patients")
public class PatientController {

	@Autowired
	private PatientService patientService;
	
	/**
	 * Create new patient
	 * 
	 * @param patient
	 * @return
	 */
	@PostMapping(path = "")
	public ResponseEntity<Patient> create(@Valid @RequestBody(required = true) Patient patient) {
		return new ResponseEntity<>(patientService.create(patient), HttpStatus.CREATED);
	}

	/**
	 * List all patients
	 * 
	 * @return
	 */
	@GetMapping(path = "")
	public ResponseEntity<List<Patient>> search(@RequestParam Map<String, String> requestParams) {
		System.out.println(requestParams.toString());

		return ResponseEntity.ok(patientService.search(requestParams));
	}
	
	
	/**
	 * Update patient info
	 * 
	 * @param patientId
	 * @param patient
	 * @return
	 */
	@PutMapping(path = "/{uuid}")
	public ResponseEntity<Patient> update(
			@PathVariable(required = true, name = "uuid") String uuid,
			@Valid @RequestBody(required = true) Patient patient) {
		return ResponseEntity.ok(patientService.update(uuid, patient));
	}

	/**
	 * Get single patient info
	 * 
	 * @param uuid
	 * @return
	 */
	@GetMapping(path = "/{uuid}")
	public ResponseEntity<Patient> get(@PathVariable(required = true, name = "uuid") String uuid) {
		Patient patient = null;
			patient = patientService.get(uuid);
		return ResponseEntity.ok(patient);
	}

	@DeleteMapping(path = "/{uuid}")
	public ResponseEntity<HttpStatus> delete(@PathVariable(required = true, name = "uuid") String uuid) {
		patientService.delete(uuid);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
