package com.tp.jms.config;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;



@EnableJms
@Configuration
@EnableTransactionManagement
public class JmsConfig {

//	@Autowired
//	private ConnectionFactory connectionFactory;
	
	@Value("${spring.activemq.broker-url}")
	private String brokerUrl;	

    @Value("${spring.activemq.user}")
    private String user;

    @Value("${spring.activemq.password}")
    private String password;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JmsConfig.class);
	
	

	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {

		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		
		/*
		 * Uncomment the below line for using the Spring default Connection factory
		 * also uncomment the Autowired connection factory and remove the connectionFactory() method
		 */
		//factory.setConnectionFactory(connectionFactory);
		//factory.setConnectionFactory(activeMqSingleConnectionFactory());
		
		factory.setConnectionFactory(activeMqCachingConnectionFactory());
		factory.setMessageConverter(jacksonMessageConverter());
		factory.setTransactionManager(jmsTransactionManager());
		
		factory.setErrorHandler(t->{
			LOGGER.info("Handling error in Listener for Messages, erro: "+ t.getMessage());;
		});
		return factory;

	}
	
	@Bean
	public  MessageConverter jacksonMessageConverter() {

		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;

	}
	
//    @Bean
//	public SingleConnectionFactory activeMqSingleConnectionFactory() {
//		SingleConnectionFactory factory = new SingleConnectionFactory(new ActiveMQConnectionFactory(user,password,brokerUrl));
//		factory.setReconnectOnException(true);
//		factory.setClientId("StoreFront");
//		return factory;		
//	}
    
    @Bean
	public CachingConnectionFactory activeMqCachingConnectionFactory() {
		CachingConnectionFactory factory = new CachingConnectionFactory(new ActiveMQConnectionFactory(user,password,brokerUrl));
		//factory.setReconnectOnException(true); -- Set to true by default
		factory.setClientId("StoreFront");
		factory.setSessionCacheSize(10);
		return factory;		
	}
    
    @Bean 
    public PlatformTransactionManager jmsTransactionManager() {    	
    	return new JmsTransactionManager(activeMqCachingConnectionFactory());    	
    }
    
    @Bean JmsTemplate jmsTemplate() {
    	JmsTemplate jmsTemplate = new JmsTemplate(activeMqCachingConnectionFactory());
    	jmsTemplate.setMessageConverter(jacksonMessageConverter());
    	jmsTemplate.setDeliveryPersistent(true); // to not delete message
    	jmsTemplate.setSessionTransacted(true);    	
    	return jmsTemplate;
    	
    }


}
