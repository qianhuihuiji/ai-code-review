package com.nofirst.ai.code.review.disruptor;

import com.lmax.disruptor.EventHandler;
import com.nofirst.ai.code.review.service.ReviewDispatcherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MsgEventHandler implements EventHandler<MessageModel> {

    @Autowired
    private ReviewDispatcherService reviewDispatcherService;

    @Override
    public void onEvent(MessageModel messageModel, long sequence, boolean endOfBatch) {
        log.info("消费者处理消息开始");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (messageModel != null) {
            log.info("消费者收到消息，序列号:{},消息内容:{}", sequence, messageModel.getEvent());
            reviewDispatcherService.review(messageModel.getEvent(), messageModel.getGitlabUrl(), messageModel.getGitlabToken());
        }
    }
}

