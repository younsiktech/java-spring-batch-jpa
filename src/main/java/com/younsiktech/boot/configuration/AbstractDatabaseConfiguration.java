package com.younsiktech.boot.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.younsiktech.boot.model.DbConfigModel;

public class AbstractDatabaseConfiguration {

    protected HikariConfig makeHikariConfig(DbConfigModel dbConfigModel) {
        HikariConfig config = new HikariConfig();

        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://"
                + dbConfigModel.getHost() + ":" + dbConfigModel.getPort()
                + "/" + dbConfigModel.getDb()+ "?autoReconnect=true");
        config.setUsername(dbConfigModel.getUsername());
        config.setPassword(dbConfigModel.getPassword());
        config.setMaximumPoolSize(dbConfigModel.getMaxPoolSize());
        config.setMinimumIdle(dbConfigModel.getMinimumIdle());
        config.setConnectionTimeout(dbConfigModel.getConnectionTimeOut());
        config.setValidationTimeout(dbConfigModel.getValidationTimeOut());
        config.setConnectionTestQuery("SELECT 1");
        config.addDataSourceProperty("autoReconnect", "true");
        config.addDataSourceProperty("serverTimezone", "Asia/Seoul");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("dataSource.useServerPrepStmts", "true");
        config.addDataSourceProperty("allowMultiQueries", "true");
        return config;
    }

}
