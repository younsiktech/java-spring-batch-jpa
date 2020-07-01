package com.younsiktech.boot.configuration.batch;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {"com.younsik.tech.service.batch"},
        entityManagerFactoryRef = "batchEntityManagerFactory",
        transactionManagerRef = "batchTransactionManager"
)
public class BatchDataManagerConfiguration {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean batchEntityManagerFactory(
        @Qualifier("batchDataSource") DataSource dataSource
    ) {

        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setShowSql(true);
        jpaVendorAdapter.setGenerateDdl(false);

        LocalContainerEntityManagerFactoryBean managerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        managerFactoryBean.setDataSource(dataSource);
        managerFactoryBean.setPersistenceProvider(new HibernatePersistenceProvider());
        managerFactoryBean.setPersistenceUnitName("batchEntityManagerFactory");
        managerFactoryBean.setPackagesToScan("com.younsik.tech.service.batch");
        managerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);

        logger.info(">>>>> EntityManagerFactory : batchEntityManagerFactory");
        return managerFactoryBean;
    }

    @Primary
    @Bean
    public PlatformTransactionManager batchTransactionManager(
        @Qualifier("batchEntityManagerFactory") EntityManagerFactory batchEntityManagerFactory
    ) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(batchEntityManagerFactory);

        logger.info(">>>>> TransactionManager : batchTransactionManager");
        return jpaTransactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor batchExceptionTranslationPostProcessor() {
        logger.info(">>>>> PersistenceExceptionTranslationPostProcessor : batchExceptionTranslationPostProcessor");
        return new PersistenceExceptionTranslationPostProcessor();
    }

}
