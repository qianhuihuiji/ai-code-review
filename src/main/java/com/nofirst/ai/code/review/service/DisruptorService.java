package com.nofirst.ai.code.review.service;

import com.lmax.disruptor.RingBuffer;
import com.nofirst.ai.code.review.disruptor.MessageModel;
import com.nofirst.ai.code.review.repository.entity.ReviewConfigInfo;
import com.nofirst.ai.code.review.repository.entity.ReviewResultInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.webhook.Event;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * The type Disruptor service.
 */
@Slf4j
@Component
@Service
@AllArgsConstructor
public class DisruptorService {

    private RingBuffer<MessageModel> messageModelRingBuffer;

    /**
     * Send msg.
     *
     * @param event            the event
     * @param reviewConfigInfo the review config info
     * @param reviewResultInfo the review result info
     */
    public void sendMsg(Event event, ReviewConfigInfo reviewConfigInfo, ReviewResultInfo reviewResultInfo) {
        log.info("投递消息，消息内容: {}", event);
        //获取下一个Event槽的下标
        long sequence = messageModelRingBuffer.next();
        try {

            //给Event填充数据
            MessageModel messageModel = messageModelRingBuffer.get(sequence);
            messageModel.setEvent(event);
            messageModel.setReviewConfigInfo(reviewConfigInfo);
            messageModel.setReviewResultInfo(reviewResultInfo);
            log.info("往消息队列中添加事件：{}", messageModel);
        } catch (Exception e) {
            log.error("消息发送失败，失败原因 {}", e.getMessage(), e);
        } finally {
            //发布Event，激活观察者去消费，将sequence传递给改消费者
            //注意最后的publish方法必须放在finally中以确保必须得到调用
            // 如果某个请求的sequence未被提交将会堵塞后续的发布操作或者其他的producer
            messageModelRingBuffer.publish(sequence);
        }
    }
}

