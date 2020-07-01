package com.younsiktech.boot.service.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StepConditionalJobConfiguration {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // @RequiredArgsConstructor => 생성자 DI
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    @Bean
    public Job stepConditionalJob() {
        return jobBuilderFactory.get("stepConditionalJob")
                .start(conditionalStep1())
                    .on("FAILED") // stpe1의 종료 상태가 FAILED이면
                    .to(conditionalStep3()) // step3로 이동
                    .on("*") // step3 결과와 상관 없이
                    .end() // step3이동하면 flow 종료
                .from(conditionalStep1()) // step1의 상태
                    .on("*") // step1의 종료 상태가 앞서 설정한 FAILED 외의 모든 경우
                    .to(conditionalStep2()) // step2로 이동
                    .next(conditionalStep3()) // step2 정상 종료 => step3로 이동
                    .on("*") // step3 결과 상관 없이
                    .end() // step3로 이동하면 flow 종료
                .end() // job 종료
                .build();
    }

    @Bean
    public Step conditionalStep1() {
        return stepBuilderFactory.get("conditionalStep1")
                .tasklet((contribution, chunkContext) -> {
                    logger.info(">>>>> This is conditionalStep1");

                    // 임의로 종료 상태 실패로 설정
                    // contribution.setExitStatus(ExitStatus.FAILED);

                    // .on() 코드는 Step의 ExitStatus 참조

                    // BatchStatus : Job 또는 Step의 실행 결과 Spring에서 기록할 때 사용
                    // ExitStatus : Step의 실행 후 상태

                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step conditionalStep2() {
        return stepBuilderFactory.get("conditionalStep2")
                .tasklet((contribution, chunkContext) -> {
                    logger.info(">>>>> This is conditionalStep2");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step conditionalStep3() {
        return stepBuilderFactory.get("conditionalStep3")
                .tasklet((contribution, chunkContext) -> {
                    logger.info(">>>>> This is conditionalStep3");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
