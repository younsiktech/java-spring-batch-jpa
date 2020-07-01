package com.younsiktech.boot.configuration.batch;

import com.younsiktech.boot.configuration.AbstractDatabaseConfiguration;
import com.younsiktech.boot.configuration.ReplicationRoutingDataSource;
import com.younsiktech.boot.constant.Constants;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class BatchDataSourceConfiguration extends AbstractDatabaseConfiguration {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BatchConfiguration batchConfiguration;

    @Bean()
    public DataSource batchWriteDataSource() {
        logger.info(">>>>> DataSource :Write");
        return new HikariDataSource(makeHikariConfig(batchConfiguration.getWrite()));
    }

    @Bean()
    public DataSource batchReadDataSource() {
        logger.info(">>>>> DataSource :Read");
        return new HikariDataSource(makeHikariConfig(batchConfiguration.getRead()));
    }

    @Bean
    public DataSource batchRoutingDataSource(
        @Qualifier("batchWriteDataSource") DataSource batchWriteDataSource,
        @Qualifier("batchReadDataSource") DataSource batchReadDataSource
    ) {
        ReplicationRoutingDataSource routingDataSource = new ReplicationRoutingDataSource();
        Map<Object, Object> dataSourceMap = new HashMap<Object, Object>();
        dataSourceMap.put(Constants.DATABASE_WRITE, batchWriteDataSource);
        dataSourceMap.put(Constants.DATABASE_READ, batchReadDataSource);
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(batchReadDataSource);
        return routingDataSource;
    }

    @Primary
    @Bean
    public DataSource batchDataSource(@Qualifier("batchRoutingDataSource") DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

}
