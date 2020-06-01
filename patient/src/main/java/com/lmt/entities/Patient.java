package com.lmt.entities;

import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.NumericField;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TermVector;

import lombok.Data;

@Data
@Entity
@Indexed
@Table(name = "patients", uniqueConstraints = @UniqueConstraint(columnNames = { "patientId" }))
@Check(constraints = "gender IN ('M', 'F', 'O')")
public class Patient {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
	private UUID id;
	
	@NotBlank(message = "patientId is mandatory")
	@Field
	@Analyzer(impl=KeywordAnalyzer.class)
	private String patientId;

	@NotBlank(message = "firstName is mandatory")
	@Field(termVector = TermVector.YES)
	@Analyzer(impl=KeywordAnalyzer.class)
	private String firstName;
	
	@Field
	private String middleName;

	@NotBlank(message = "lastName is mandatory")
	@Field(termVector = TermVector.YES)
	@Analyzer(impl=KeywordAnalyzer.class)
	private String lastName;
	
	@NotNull(message = "dob is mandatory")
	@Field
	private LocalDate dob;
	
	@NotNull(message = "gender is mandatory")
	@Field
	private String gender;

	@Field(store = Store.YES)
	@NumericField
	private short deleteFlag = 0;
}
