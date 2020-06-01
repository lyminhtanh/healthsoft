package com.lmt.modules;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lmt.constants.CommonConst;
import com.lmt.constants.CommonConst.CommonCharacter;
import com.lmt.constants.CommonConst.PatientColumnName;
import com.lmt.constants.CommonConst.SearchQueryParam;
import com.lmt.entities.Patient;
import com.lmt.enums.Flag;
import com.lmt.utils.CastUtil;

@Repository
@Import({HibernateSearchConfig.class})
@Transactional
public class FullTextSearchModule {

	@PersistenceContext
	private EntityManager entityManager;
	
	public List<Patient> searchProductNameByKeywordQuery(String text) {

		Query keywordQuery = getQueryBuilder().keyword().onField("firstName").matching(text).createQuery();

		List<Patient> results = getJpaQuery(keywordQuery).getResultList();

		return results;
	}

	private FullTextQuery getJpaQuery(org.apache.lucene.search.Query luceneQuery) {

		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

		return fullTextEntityManager.createFullTextQuery(luceneQuery, Patient.class);
	}

	private QueryBuilder getQueryBuilder() {

		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

		return fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Patient.class).get();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Patient> search(Map<String, String> requestParams) {
		BooleanJunction keywordQuery = getQueryBuilder().bool();

		// Build queries from params map
		List<Query> queries = requestParams.entrySet().stream().map(this::buildQueryFromPathParam)
				.filter(Objects::nonNull).collect(Collectors.toList());

		// Shouldn’t include any soft-deleted Patients unless withDeleted=true
		if (!Boolean.valueOf(requestParams.get(SearchQueryParam.WITH_DELETED)).booleanValue()) {
			Query queryWithoutDeleted = getQueryBuilder().keyword()
					.onField(PatientColumnName.DELETE_FLAG)
					.matching(Flag.OFF.getValue()).createQuery();
			keywordQuery = keywordQuery.must(queryWithoutDeleted);
			System.out.println("Qeryyyy add detele = 0");
			System.out.println(keywordQuery);
		}

		// Return all records if no any valid query passed
		if (queries.isEmpty() && keywordQuery.isEmpty()) {
			System.out.println("get ALL-----------------------");
			return getJpaQuery(getQueryBuilder().all().createQuery()).getResultList();
		}

		for (Query query : queries) {
			keywordQuery = keywordQuery.must(query);
		}

		Query createQuery = keywordQuery.createQuery();
		System.out.println("Qeryyyy");
		System.out.println(createQuery);
		return getJpaQuery(createQuery).getResultList();
	}

	private Query buildQueryFromPathParam(Entry<String, String> entry) {
		if(entry.getValue() == null || entry.getValue().isBlank()) {
			return null;
		}
		switch (entry.getKey()) {
		/* wildcard search */
		case SearchQueryParam.FIRST_NAME:
		case SearchQueryParam.LAST_NAME:
			return getQueryBuilder()
					.keyword()
					.wildcard()
					.onField(CommonConst.paramColumnMap.get(entry.getKey()))
					.matching(addWildard(entry.getValue()))
					.createQuery();

		/* Exact match */
		case SearchQueryParam.GENDER:
		case SearchQueryParam.PATIENT_ID:
			return getQueryBuilder()
					.keyword()
					.onField(CommonConst.paramColumnMap.get(entry.getKey()))
					.matching(entry.getValue())
					.createQuery();

		/* search date */
		case SearchQueryParam.DOB:
			return Optional
					.ofNullable(CastUtil.toDate(entry.getValue()))
					.map(date -> getQueryBuilder()
							.keyword()
							.onField(CommonConst.paramColumnMap.get(entry.getKey()))
							.matching(CastUtil.toDate(entry.getValue()))
							.createQuery())
					.orElse(null);
		default:
			return null;
		}
	}

	private String addWildard(String value) {
		return new StringBuilder()
					.append(CommonCharacter.ASTERISK)
					.append(value)
					.append(CommonCharacter.ASTERISK)
					.toString();
	}
}