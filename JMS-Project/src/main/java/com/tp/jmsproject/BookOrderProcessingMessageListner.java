package com.tp.jmsproject;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BookOrderProcessingMessageListner  implements MessageListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BookOrderProcessingMessageListner.class);

	@Override
	public void onMessage(Message message) {
		

		try {
			String text  =  ((TextMessage) message).getText();
			
			LOGGER.info(text);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
