package com.lmt.modules;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * The only meaning for this class is to build the Lucene index at application
 * startup. This is needed in this example because the database is filled 
 * before and each time the web application is started. In a normal web 
 * application probably you don't need to do this.

 */
@Component
public class BuildSearchIndex implements ApplicationRunner {

	@PersistenceContext
	private EntityManager entityManager;
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {

//    	onApplicationEvent();

    }

    /**
     * Create an initial Lucene index for the data already present in the
     * database.
     * This method is called during Spring's startup.
     * 
     * @param event Event raised when an ApplicationContext gets initialized or
     * refreshed.
     */
//    @PostConstruct
    public void onApplicationEvent() {
        try {
            FullTextEntityManager fullTextEntityManager =
                    Search.getFullTextEntityManager(entityManager);
            fullTextEntityManager.createIndexer().startAndWait();
        }
        catch (InterruptedException e) {
            System.out.println(
                    "An error occurred trying to build the serach index: " +
                            e.toString());
        }
        return;
    }


} 