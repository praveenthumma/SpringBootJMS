# SpringBootJMS
JMS in Spring Examples.

## 2. Config Pooling and Transactions

### 2.1 Connection Config using Application Properties


### 2.2 SingleConnectionFactory
> - A JMS ConnectionFactory adapter that returns the same Connection from all __createConnection()__ calls, and ignores calls to __Connection.close()__.
> - According to the JMS Connection model, this is perfectly __thread-safe__ (in contrast to e.g. JDBC). 
> - The shared Connection can be automatically recovered in case of an Exception. This is done by updating __setReconnectOnException(true)__.
> - By Default __setReconnectOnException(true)__ is true
> - Ignores calls to __connection.close()__

#### When is it Used ?
> - Need Connections to Multiple Different Message Oriented Middlewares (Like ActiveMQ, IBM-MQ, RabbitMQ)
> - Which means you need different connections and JmsTemplates to send Messages
> - Not useful when Load Increases
> -  CachingConnectionFactory is recommended for this


### 2.3 CachingConnectionFactory

SingleConnectionFactory subclass that adds Session caching as well MessageProducer caching. 
> - This ConnectionFactory also switches the __"reconnectOnException"__ property to __"true" by default__, allowing for automatic recovery of the underlying Connection.
>> - __setReconnectOnException(true)__ is true
> - Ignores calls to __connection.close()__
> - By default, only one single Session will be cached, with further requested Sessions being created and disposed on demand. 
> - Consider raising the __"sessionCacheSize"__ value in case of a high-concurrency environment.
> - __This ConnectionFactory requires explicit closing of all Sessions obtained from its shared Connection.__ This is the usual recommendation for native JMS access code anyway. However, with this ConnectionFactory, its use is mandatory in order to actually allow for Session reuse.

### Usage
> - Create a method that returns SingleConnectionFactory/CachingConnectionFactory and annotate it with @Bean
> - pass the MOM connectionfactory to the SingleConnectionFactory/CachingConnectionFactory, In this case its ActiveMQConnectionFactory.
>> - SingleConnectionFactory
```java
        @Bean
	public SingleConnectionFactory activeMqSingleConnectionFactory() {
		SingleConnectionFactory factory = new SingleConnectionFactory(new ActiveMQConnectionFactory(user,password,brokerUrl));
		factory.setReconnectOnException(true);
		factory.setClientId("StoreFront");
		return factory;		
	}
```
>> - CachingConnectionFactory
```java
	@Bean
	public CachingConnectionFactory activeMqCachingConnectionFactory() {
		CachingConnectionFactory factory = new CachingConnectionFactory(
				new ActiveMQConnectionFactory(user, password, brokerUrl));
		// factory.setReconnectOnException(true); -- Set to true by default
		factory.setClientId("StoreFront");
		factory.setSessionCacheSize(10);
		return factory;
	}

```

> - set this in the ListenerContainerFactory
```java
	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {

		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(activeMqCachingConnectionFactory());
		factory.setMessageConverter(jacksonMessageConverter());
		factory.setTransactionManager(jmsTransactionManager());

		factory.setErrorHandler(t -> {
			LOGGER.info("Handling error in Listener for Messages, erro: " + t.getMessage());
		});
		
		return factory;
	}
```



### 2.4 Transaction Management
> - Allows for comitting and rollbacking
> - __PlatformTransactionManager__ Interface implemented for JDBC, JPA, Hibernate and JMS
> - We will use JmsTransactionManager

#### Rollback
> - Now for JMS transactions there really are only two things that can happen on rollback. 
>> - For a send on rollback the message is not sent. 
>> - For a receive however the message is re-queued for retrieval again on the MOM. 
>> - __The re-queuing of messages is broker dependent.__
>>> - For active MQ on rollback you can actually configure after a specified number of retries to move messages to a different Queue, which is often called __dead-letter Queue__

#### Usage
> - Create a method that returns PlatformTransactionManager and annotate it with @Bean
> - __factory.setTransactionManager(jmsTransactionManager());__ in listnerContainerFactory

### 2.5 Transaction Management using DeadLetterQueue Usage

> -  Create a method that returns JmsTemplate and annotate it with
> - When a runtime exception occurs while receiving message , the message in active MQ is put in ActiveMQ.DLQ
>> - __The re-queuing of messages is broker dependent.__
```java
 	@Bean
 	JmsTemplate jmsTemplate() {
 		JmsTemplate jmsTemplate = new JmsTemplate(activeMqCachingConnectionFactory());
 		jmsTemplate.setMessageConverter(jacksonMessageConverter());
 		jmsTemplate.setDeliveryPersistent(true); // to not delete message
 		jmsTemplate.setSessionTransacted(true);
 		return jmsTemplate;
 	}
```
    
> - In listnerFactory set __factory.setErrorHandler()__
```	java
	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {

		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(activeMqCachingConnectionFactory());
		factory.setMessageConverter(jacksonMessageConverter());
		factory.setTransactionManager(jmsTransactionManager());

		factory.setErrorHandler(t -> {
			LOGGER.info("Handling error in Listener for Messages, erro: " + t.getMessage());
		});
		
		return factory;
	}

```
## 3. Sessions, Headers , and Response Management
