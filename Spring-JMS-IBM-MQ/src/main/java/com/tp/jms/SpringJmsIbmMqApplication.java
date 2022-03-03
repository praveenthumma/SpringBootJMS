package com.tp.jms;

import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

import com.tp.jms.sender.Sender;


@SpringBootApplication
@EnableJms
public class SpringJmsIbmMqApplication {

	public static void main(String[] args) {
	    // Launch the application
		
		//final String qName = "DEV.QUEUE.1";
	   // ConfigurableApplicationContext context = SpringApplication.run(SpringJmsIbmMqApplication.class, args);
		
		
		SpringApplication.run(SpringJmsIbmMqApplication.class, args);

	    // Create the JMS Template object to control connections and sessions.
	    //JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);

	    // Send a single message with a timestamp
	   // String msg = "Hello from IBM MQ at " + new Date();

	    // The default SimpleMessageConverter class will be called and turn a String
	    // into a JMS TextMessage
	 //   jmsTemplate.convertAndSend(qName, msg);

	   // status();
	}
	/*
	 * static void status() { System.out.println();
	 * System.out.println("========================================");
	 * System.out.println("MQ JMS Sample started. Message sent to queue: ");
	 * System.out.println("========================================"); }
	 */
}
