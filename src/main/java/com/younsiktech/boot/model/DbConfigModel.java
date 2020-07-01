package com.younsiktech.boot.model;

import lombok.Data;

@Data
public class DbConfigModel {
    private String host;
    private int port;
    private String db;
    private String username;
    private String password;
    private int maxPoolSize;
    private int minimumIdle;
    private long connectionTimeOut;
    private long validationTimeOut;
}
