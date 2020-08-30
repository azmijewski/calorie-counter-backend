package com.sda.caloriecounterbackend.configuration;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@Component
public class HibernateSearchInit implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(HibernateSearchInit.class);


    private final EntityManager em;

    public HibernateSearchInit(EntityManagerFactory em) {
        this.em = em.createEntityManager();
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
        log.debug("Start creating Hibernate Search Indexes");

        try {
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException A) {
            log.error("Error occurred while building Hibernate Search Indexes", A);
        }
    }
}
