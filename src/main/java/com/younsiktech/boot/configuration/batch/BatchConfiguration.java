package com.younsiktech.boot.configuration.batch;

import com.younsiktech.boot.model.DbConfigModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "databases.batch")
public class BatchConfiguration {
    // mysql database (application-env.yml) 추가할 때마다 DbConfigModel을 yml과 동일하게 등록
    private DbConfigModel write;
    private DbConfigModel read;
}
