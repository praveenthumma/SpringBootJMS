package com.tp.jms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tp.jms.pojo.BookOrder;

@Service
public class BookOrderService {
	private static final String BOOK_QUEUE ="book.order.queue";
	
	@Autowired
	private JmsTemplate  jmsTemplate;
	
	@Transactional
    public void send(BookOrder bookOrder){
        jmsTemplate.convertAndSend(BOOK_QUEUE, bookOrder);
    }
	

}
