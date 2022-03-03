package com.tp.jms.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.tp.jms.sender.Sender;

@Component
public class Listener {

	 @Autowired
	 private Sender sender;

	void sendMessage(String message) {
		sender.sendMessage("DEV.QUEUE.1", message);

	}

	@JmsListener(destination = "DEV.QUEUE.1")
	public void receiveMessage(String message) {
		System.out.println("Message Received is: " + message);

		// Sender usage, commented to avoid the sender and Listener running in a loop
		//sendMessage(message);

	}

}