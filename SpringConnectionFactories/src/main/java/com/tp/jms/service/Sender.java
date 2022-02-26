package com.tp.jms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class Sender {
    @Autowired
    private JmsTemplate jmsTemplate;

    @Transactional
    public void sendMessage(String destination, String message) {
        jmsTemplate.convertAndSend(destination, message);
    }
    
}