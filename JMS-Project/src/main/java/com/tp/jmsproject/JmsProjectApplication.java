package com.tp.jmsproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.jms.annotation.EnableJms;



/*
 * 1) Add @Enable Jms
 * 2) Create a config class for JMSConfig 
 * 3) Add ConnectionFactory , ListnerFactory and Message Converters in the JMS Config
 * 		
 * 
 */
@SpringBootApplication
@EnableJms
public class JmsProjectApplication extends SpringBootServletInitializer {
	
	
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(JmsProjectApplication.class);
    }

	public static void main(String[] args) {
		 SpringApplication.run(JmsProjectApplication.class, args);
		
	//	Sender sender = context.getBean(Sender.class);
		//sender.sendMessage("order-queue", "XXXXXXXXXXXXXXXTTTTTTTTTTTRRRRRRRRR");
		 
	}
		
		
	/*
	 * @Bean public ActiveMQConnectionFactory connectionFactory () { String userName
	 * = "admin"; String password = "admin"; String brokerURL=
	 * "tcp://localhost:61616"; ActiveMQConnectionFactory factory = new
	 * ActiveMQConnectionFactory(userName,password,brokerURL); return factory; }
	 * 
	 * 
	 * @Bean public DefaultJmsListenerContainerFactory
	 * defaultJmsListenerContainerFactory () { DefaultJmsListenerContainerFactory
	 * listnerFactory = new DefaultJmsListenerContainerFactory();
	 * listnerFactory.setConnectionFactory(connectionFactory());
	 * listnerFactory.setConcurrency("1-1"); return listnerFactory; }
	 */
	

}
