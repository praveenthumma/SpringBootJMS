package com.tp.jmsproject.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MarshallingMessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.oxm.xstream.XStreamMarshaller;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import com.tp.jmsproject.BookOrderProcessingMessageListner;
import com.tp.jmsproject.pojo.Book;
import com.tp.jmsproject.pojo.BookOrder;
import com.tp.jmsproject.pojo.Customer;

@EnableJms
@Configuration
public class JmsConfig implements  JmsListenerConfigurer{

    //@Bean
    public MessageConverter jacksonJmsMessageConverter(){
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @Bean
    public ActiveMQConnectionFactory connectionFactory(){
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("admin","admin","tcp://localhost:61616");
        return factory;
    }


    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(){
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        //factory.setMessageConverter(jacksonJmsMessageConverter());
        factory.setMessageConverter(xmlMarshallingMessageConverter());
        return factory;
    }
    
    @Bean
    public XStreamMarshaller xmlMarshaller(){
        XStreamMarshaller marshaller =  new XStreamMarshaller();
        marshaller.setSupportedClasses(Book.class, Customer.class, BookOrder.class);
        
        //Adding these to resolve the unmarshelling errors.
        XStream xstream = marshaller.getXStream();
        xstream.alias("book",Book.class);
        xstream.alias("customer",Customer.class);
        xstream.alias("bookorder",BookOrder.class);
        xstream.addPermission(AnyTypePermission.ANY);
        return marshaller;
    }
    
    @Bean
    public MessageConverter xmlMarshallingMessageConverter(){
        MarshallingMessageConverter converter = new MarshallingMessageConverter(xmlMarshaller());
        converter.setTargetType(MessageType.TEXT);
        return converter;
    }
    
    @Bean
    public BookOrderProcessingMessageListner jmsMessageListener() {
    	BookOrderProcessingMessageListner listener = new BookOrderProcessingMessageListner();
    	return listener;
    }
	@Override
	public void configureJmsListeners(JmsListenerEndpointRegistrar registrar) {
		// TODO Auto-generated method stub
		SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
		endpoint.setMessageListener(jmsMessageListener());
		endpoint.setDestination("book.order.processed.queue");
		endpoint.setId("bookorder-processed-queue");
		endpoint.setSubscription("MySubscription");
		endpoint.setConcurrency("1");
		registrar.setContainerFactory(jmsListenerContainerFactory());
		registrar.registerEndpoint(endpoint, jmsListenerContainerFactory());
	}
  
}
