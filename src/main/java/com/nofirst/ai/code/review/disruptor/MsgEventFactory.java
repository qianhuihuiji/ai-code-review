package com.nofirst.ai.code.review.disruptor;

import com.lmax.disruptor.EventFactory;

public class MsgEventFactory implements EventFactory<MessageModel> {

    @Override
    public MessageModel newInstance() {

        return new MessageModel();
    }
}

