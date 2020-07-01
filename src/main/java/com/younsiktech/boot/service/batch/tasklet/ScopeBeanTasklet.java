package com.younsiktech.boot.service.batch.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@StepScope
public class ScopeBeanTasklet implements Tasklet {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("#{jobParameters[requestDate]}")
    private String requestDate;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        logger.info(">>>>> This is Scope Bean Tasklet");
        logger.info(">>>>> Request Date : {}", requestDate);
        logger.info(">>> scopeStep3 done");
        return RepeatStatus.FINISHED;
    }
}
