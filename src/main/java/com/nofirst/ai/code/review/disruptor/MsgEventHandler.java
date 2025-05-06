package com.nofirst.ai.code.review.disruptor;

import com.lmax.disruptor.EventHandler;
import com.nofirst.ai.code.review.model.enums.TaskReviewStatusEnum;
import com.nofirst.ai.code.review.repository.dao.IReviewEventTaskDAO;
import com.nofirst.ai.code.review.repository.entity.ReviewEventTask;
import com.nofirst.ai.code.review.service.ReviewDispatcherService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MsgEventHandler implements EventHandler<MessageModel> {

    private final ReviewDispatcherService reviewDispatcherService;
    private final IReviewEventTaskDAO reviewResultInfoDAO;


    @Override
    public void onEvent(MessageModel messageModel, long sequence, boolean endOfBatch) {
        log.info("消费者处理消息开始");
        try {
            if (messageModel != null) {
                log.info("消费者收到消息，序列号:{},消息内容:{}", sequence, messageModel.getEvent());
                reviewDispatcherService.review(messageModel);
            }
        } catch (Exception e) {
            ReviewEventTask reviewEventTask = messageModel.getReviewEventTask();
            reviewEventTask.setReviewStatus(TaskReviewStatusEnum.FAIL.getStatus());
            reviewEventTask.setFailureMsg(e.getMessage());

            reviewResultInfoDAO.updateById(reviewEventTask);
            log.error("消费者处理异常:{}", e.getMessage());
        }
        log.info("消费者处理消息结束");
    }
}

