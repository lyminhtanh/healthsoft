package com.lmt.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lmt.entities.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

	@Query("SELECT p FROM Patient p WHERE p.patientId = ?1 AND deleteFlag = 1")
	Patient findSoftDeletedByPatientId(String patientId);

	@Query("SELECT p FROM Patient p WHERE p.patientId = ?1 AND deleteFlag = 0")
	Patient findByPatientId(String patientId);

	@Query("SELECT p FROM Patient p WHERE p.id = ?1")
	Patient findByUUID(UUID uuid);

}
