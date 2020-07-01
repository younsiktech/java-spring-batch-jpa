package com.younsiktech.boot.service.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StepDeciderJobConfiguration {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // @RequiredArgsConstructor => 생성자 DI
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    @Bean
    public Job stepDeciderJob() {
        return jobBuilderFactory.get("stepDeciderJob")
                .start(deciderStartStep())
                .next(decider()) // 홀수 짝수 구분
                .from(decider()) // decider 상태
                    .on("ODD") // 홀수
                    .to(deciderOddStep()) // oddStep
                .from(decider()) // decider 상태
                    .on("EVEN") // 짝수
                    .to(deciderEvenStep()) // evenStep
                .end()// 종료
                .build();
    }

    @Bean
    public Step deciderStartStep() {
        return stepBuilderFactory.get("deciderStartStep")
                .tasklet((contribution, chunkContext) -> {
                    logger.info(">>>>> START");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step deciderOddStep() {
        return stepBuilderFactory.get("deciderOddStep")
                .tasklet((contribution, chunkContext) -> {
                    logger.info(">>>>> This is Odd");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step deciderEvenStep() {
        return stepBuilderFactory.get("deciderEvenStep")
                .tasklet((contribution, chunkContext) -> {
                    logger.info(">>>>> This is Even");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public JobExecutionDecider decider() {
        return new OddDecider();
    }

    public static class OddDecider implements JobExecutionDecider {
        @Override
        public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {

            SecureRandom rand = new SecureRandom();

            int randomNumber = rand.nextInt(50) + 1;

            log.info("Random : {}", randomNumber);

            if(randomNumber % 2 == 0) {
                return new FlowExecutionStatus("EVEN");
            } else {
                return new FlowExecutionStatus("ODD");
            }
        }

    }
}
