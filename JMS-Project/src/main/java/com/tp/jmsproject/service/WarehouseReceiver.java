package com.tp.jmsproject.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import com.tp.jmsproject.pojo.BookOrder;


@Service
public class WarehouseReceiver {
	
	
	@Autowired
	WarehouseProcessingService wareHouseProcessingService;
	
	 private static final Logger LOGGER =
	  LoggerFactory.getLogger(WarehouseReceiver.class);

		@JmsListener(destination = "book.order.queue")
		public void receive(BookOrder bookOrder) {
			LOGGER.info("Message received!");
			LOGGER.info("Message is == " + bookOrder);
			
			wareHouseProcessingService.processOrder(bookOrder);

			
		}
	 
}
