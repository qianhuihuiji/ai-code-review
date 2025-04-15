package com.nofirst.ai.code.review.service;

import com.lmax.disruptor.RingBuffer;
import com.nofirst.ai.code.review.disruptor.MessageModel;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.webhook.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Component
@Service
public class DisruptorMqService {


    @Autowired
    private RingBuffer<MessageModel> messageModelRingBuffer;


    public void sendMsg(Event event, String gitlabUrl, String gitlabToken) {

        log.info("投递消息，消息内容: {}", event);
        //获取下一个Event槽的下标
        long sequence = messageModelRingBuffer.next();
        try {

            //给Event填充数据
            MessageModel messageModel = messageModelRingBuffer.get(sequence);
            messageModel.setEvent(event);
            messageModel.setGitlabUrl(gitlabUrl);
            messageModel.setGitlabToken(gitlabToken);
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

