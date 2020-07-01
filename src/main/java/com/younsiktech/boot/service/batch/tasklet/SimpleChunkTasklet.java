package com.younsiktech.boot.service.batch.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.item.Chunk;
import org.springframework.batch.core.step.item.ChunkProcessor;
import org.springframework.batch.core.step.item.ChunkProvider;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
public class SimpleChunkTasklet<T> implements Tasklet {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String INPUT_KEY = "INPUTS";

    private final ChunkProvider<T> chunkProvider;
    private final ChunkProcessor<T> chunkProcessor;

    private boolean isBuffering = true;

    public SimpleChunkTasklet(ChunkProvider<T> chunkProvider, ChunkProcessor<T> chunkProcessor) {
        this.chunkProvider = chunkProvider;
        this.chunkProcessor = chunkProcessor;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // chunk 단위 작업 코드

        Chunk<T> inputs = (Chunk<T>) chunkContext.getAttribute(INPUT_KEY);

        if(inputs == null) {
            // reader에서 chunk 사이즈 만큼 데이터 가져 오기
            // chunk size = 트랜잭션 단위
            // page size = item 양
            // 따라서 chunk size != page size
            // 하지만 chunk size 와 page size는 동일하게 두는 것이 성능상 이슈와 영속성 컨텍스트 면에서 좋음
            inputs = chunkProvider.provide(contribution);
            if(isBuffering) {
                chunkContext.setAttribute(INPUT_KEY, inputs);
            }
        }

        chunkProcessor.process(contribution, inputs); // reader에서 받은 데이터 processor, writer 처리

        chunkProvider.postProcess(contribution, inputs);

        if(inputs.isBusy()) {
            // chunk still work
            logger.info("Inputs Still Busy");
            return RepeatStatus.CONTINUABLE;
        }

        chunkContext.removeAttribute(INPUT_KEY);
        chunkContext.setComplete();

        logger.info("Inputs Not Busy, Finished : {}", inputs.isEnd());

        return RepeatStatus.continueIf(!inputs.isEnd());
    }
}
