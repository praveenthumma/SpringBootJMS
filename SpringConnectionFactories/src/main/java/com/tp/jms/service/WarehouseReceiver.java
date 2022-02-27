package com.tp.jms.service;

import javax.management.RuntimeErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.listener.adapter.JmsResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import com.tp.jms.pojo.BookOrder;
import com.tp.jms.pojo.ProcessedBookOrder;

@Service
public class WarehouseReceiver {

	@Autowired
	WarehouseProcessingService warehouseProcessingService;

	private static final Logger LOGGER = LoggerFactory.getLogger(WarehouseReceiver.class);
	
	  @JmsListener(destination = "book.order.queue")
	  //@SendTo("book.order.processed.queue")
	    public JmsResponse<Message<ProcessedBookOrder>>  receive(@Payload BookOrder bookOrder,
	                        @Header(name = "orderState") String orderState,
	                        @Header(name = "bookOrderId") String bookOrderId,
	                        @Header(name = "storeId") String storeId
	                //        MessageHeaders messageHeaders
	                        ){
	        LOGGER.info("Message received!");
	        LOGGER.info("Message is == " + bookOrder);
	        LOGGER.info("Message property orderState = {}, bookOrderId = {}, storeId = {}", orderState, bookOrderId, storeId);
	        //LOGGER.info("messageHeaders = {}", messageHeaders);

	        if(bookOrder.getBook().getTitle().startsWith("L")){
	            throw new IllegalArgumentException("bookOrderId=" + bookOrder.getBookOrderId() + " is of a book not allowed!");
	        }
	        return warehouseProcessingService.processOrder(bookOrder, orderState, storeId);
	    }
}
