package com.tp.jms.sender;

import javax.jms.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class Sender {
    @Autowired
    private JmsTemplate jmsTemplate;
    Message message;

  //  @Transactional
    public void sendMessage(String destination, String message) {
        jmsTemplate.convertAndSend(destination, message);
        //jmsTemplate.send
    }
    
}