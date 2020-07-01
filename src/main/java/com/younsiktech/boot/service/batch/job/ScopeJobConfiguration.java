package com.younsiktech.boot.service.batch.job;

import com.younsiktech.boot.service.batch.tasklet.ScopeBeanTasklet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ScopeJobConfiguration {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // @RequiredArgsConstructor => 생성자 DI
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job scopeJob() {
        return jobBuilderFactory.get("scopeJob")
                .start(scopeStep1())
                .next(scopeStep2())
                .next(scopeStep3())
                .build();
    }

    /*
    Scope 사용 장점
    1. job parameter 의 late binding
        StepContext, JobExecutionContext 레벨에서 할당 가능
        즉, application 실행 시점이 아닌 비지니스 로직 처리 단계 -> job parameter 할당 가능
        * Job Parameter : Double, Long, Date, String 사용 가능 (2020.07.01 기준) / Scope Bean을 생성할 때만 가능
    2. 동일한 컴포넌트 별렬 또는 동시 사용 가능
        StepScope 없음 -> Step 병렬 실행 -> 서로 다른 Step 들이 하나의 Tasklet 상태를 변경 -> 상태 침범 위험
        StepScope 존재 -> 각 Step에서 별도의 Tasklet을 생성, 관리 -> 상태 침범 위험이 없음
     */
    @Bean
    @JobScope // Step 사용 가능, job 실행 시점 -> bean 생성
    public Step scopeStep1() {
        return stepBuilderFactory.get("scopeStep1")
                .tasklet(((contribution, chunkContext) -> {
                    logger.info(">>> scopeStep1 done");
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }

    @Bean
    public Step scopeStep2() {
        return stepBuilderFactory.get("scopeStep2")
                .tasklet(scopeStep2Tasklet(null))
                .build();
    }

    @Bean
    @StepScope // Tasklet, ItemReader, ItemWriter, ItemProcessor 사용 가능, step의 실행 시점 -> bean 생성
    public Tasklet scopeStep2Tasklet(@Value("#{jobParameters[requestDate]}") String requestDate) {

        return (contribution, chunkContext) -> {
            logger.info(">>> request = {}", requestDate);
            logger.info(">>> scopeStep2 done");
            return RepeatStatus.FINISHED;
        };
    }

    // 생성자 DI 통한 job parameter
    // @StepScope 없이 일반 bean으로 생성하면 job parameter 찾을 수 없음
    private final ScopeBeanTasklet scopeBeanTasklet;

    @Bean
    public Step scopeStep3() {
        return stepBuilderFactory.get("scopeStep3")
                .tasklet(scopeBeanTasklet)
                .build();
    }
}
