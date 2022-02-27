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
### 3.1 JMS Headers
#### JMS Specifications
> - A JMS message header contains a number of predefined fields that contain values that both clients and providers use to identify and route messages.
> - You can also create and set properties for messages if you need values in addition to those provided by the header fields. 
> - These additional properties or customer headers if you like can be used in a message agnostic manner to help control the flow in your application by reading properties.
#### JMS Headers Usage
> - Use the  __convertAndSend(String destinationName, Object message, MessagePostProcessor postProcessor)__ by adding new __MessagePostProcessor()__
```java
        jmsTemplate.convertAndSend(BOOK_QUEUE, bookOrder, new MessagePostProcessor() {			
			@Override
			public Message postProcessMessage(Message message) throws JMSException {				
				message.setStringProperty("bookOrderId",bookOrder.getBookOrderId());
				message.setStringProperty("storeId", storeId);
                message.setStringProperty("orderState", orderState);				
				return message;
			}
		});
    }
```
### 3.2 JMS Header and response Management
> - Add __@Payload, @Header ...,MessageHeaders__ to your receive method
>> - __@Payload__ Binds the method parameter to the payload of the message
>> - __@Header__ Annotation which indicates that a method parameter should be bound to a message header.
>> - __MessageHeaders__ Has the header info set by the MOM's
#### Response Management Usage

```java
	  @JmsListener(destination = "book.order.queue")
	    public void receive(@Payload BookOrder bookOrder,
	                        @Header(name = "orderState") String orderState,
	                        @Header(name = "bookOrderId") String bookOrderId,
	                        @Header(name = "storeId") String storeId,
	                        MessageHeaders messageHeaders){
	        LOGGER.info("Message received!");
	        LOGGER.info("Message is == " + bookOrder);
	        LOGGER.info("Message property orderState = {}, bookOrderId = {}, storeId = {}", orderState, bookOrderId, storeId);
	        LOGGER.info("messageHeaders = {}", messageHeaders);

	        if(bookOrder.getBook().getTitle().startsWith("L")){
	            throw new RuntimeException("bookOrderId=" + bookOrder.getBookOrderId() + " is of a book not allowed!");
	        }
	        warehouseProcessingService.processOrder(bookOrder, orderState, storeId);
	    }
}
```

### 3.3 Response Management using SendTo('destination')
> - Spring has annotations that allow the default destination to be set along with the JmsListener
> - This allows the message reveived to be sent to a new Queue after processing.
> - to enable this add @SendTo just below the @JmsListener to the receive method 
> - Change thre retrun type from void to the Object that you want to send to the destination Queue.
```java
	  @JmsListener(destination = "book.order.queue")
	  @SendTo("book.order.processed.queue")
	    public ProcessedBookOrder receive(@Payload BookOrder bookOrder,
	                        @Header(name = "orderState") String orderState,
	                        @Header(name = "bookOrderId") String bookOrderId,
	                        @Header(name = "storeId") String storeId,
	                        MessageHeaders messageHeaders){
	        LOGGER.info("Message received!");
	        LOGGER.info("Message is == " + bookOrder);
	        LOGGER.info("Message property orderState = {}, bookOrderId = {}, storeId = {}", orderState, bookOrderId, storeId);
	        LOGGER.info("messageHeaders = {}", messageHeaders);

	        if(bookOrder.getBook().getTitle().startsWith("L")){
	            throw new IllegalArgumentException("bookOrderId=" + bookOrder.getBookOrderId() + " is of a book not allowed!");
	        }
	        return warehouseProcessingService.processOrder(bookOrder, orderState, storeId);
	    }
```
### 3.4 Response Management using MessageBuilder sendinf JMS Headers
> - In the above wxample if you notice the messages in the processed queues does not have any custom properties
> - To add custom properties use the MessageBuilder class
> - Create a message builder class 
```java
    private Message<ProcessedBookOrder> build(ProcessedBookOrder bookOrder, String orderState, String storeId){
        return MessageBuilder
                .withPayload(bookOrder)
                .setHeader("orderState", orderState)
                .setHeader("storeId", storeId)
                .build();
    }    
```
>  - Wrap the return type with __Message<>__ wrapper 
```java
	  @JmsListener(destination = "book.order.queue")
	  @SendTo("book.order.processed.queue")
	    public Message<ProcessedBookOrder>  receive(@Payload BookOrder bookOrder,
	                        @Header(name = "orderState") String orderState,
	                        @Header(name = "bookOrderId") String bookOrderId,
	                        @Header(name = "storeId") String storeId,
	                        MessageHeaders messageHeaders){
	        LOGGER.info("Message received!");
	        LOGGER.info("Message is == " + bookOrder);
	        LOGGER.info("Message property orderState = {}, bookOrderId = {}, storeId = {}", orderState, bookOrderId, storeId);
	        LOGGER.info("messageHeaders = {}", messageHeaders);

	        if(bookOrder.getBook().getTitle().startsWith("L")){
	            throw new IllegalArgumentException("bookOrderId=" + bookOrder.getBookOrderId() + " is of a book not allowed!");
	        }
	        return warehouseProcessingService.processOrder(bookOrder, orderState, storeId);
	    }
 ```
### 3.5 Response Management using JmsResponse  (Dynamic Destination strategy)
> - JmsResponse Object lets you compute Destination response at runtime
>> - If we are using JmsResponse wrapper , we dont need @SendTo()
>>  - remove the MessageHeaders

```java
	  @JmsListener(destination = "book.order.queue")
	    public JmsResponse<Message<ProcessedBookOrder>>  receive(@Payload BookOrder bookOrder,
	                        @Header(name = "orderState") String orderState,
	                        @Header(name = "bookOrderId") String bookOrderId,
	                        @Header(name = "storeId") String storeId
	                        ){
	        LOGGER.info("Message received!");
	        LOGGER.info("Message is == " + bookOrder);
	        LOGGER.info("Message property orderState = {}, bookOrderId = {}, storeId = {}", orderState, bookOrderId, storeId);
	        if(bookOrder.getBook().getTitle().startsWith("L")){
	            throw new IllegalArgumentException("bookOrderId=" + bookOrder.getBookOrderId() + " is of a book not allowed!");
	        }
	        return warehouseProcessingService.processOrder(bookOrder, orderState, storeId);
	    }

```

> -  Use the __JmsResponse.forQueue()__ to specify different destinations.

```java
    
    private static final String PROCESSED_QUEUE = "book.order.processed.queue";
    private static final String CANCELED_QUEUE = "book.order.canceled.queue";

    @Autowired
    private JmsTemplate jmsTemplate;

    @Transactional
    public JmsResponse<Message<ProcessedBookOrder>>  processOrder(BookOrder bookOrder, String orderState, String storeId){
  	
    	Message<ProcessedBookOrder> message;
	
        if("NEW".equalsIgnoreCase(orderState)){
        	message = add(bookOrder, storeId);
        	return JmsResponse.forQueue(message, PROCESSED_QUEUE);
        } else if("UPDATE".equalsIgnoreCase(orderState)){
        	message = update(bookOrder, storeId);
        	return JmsResponse.forQueue(message, PROCESSED_QUEUE);
        } else if("DELETE".equalsIgnoreCase(orderState)){
        	message = delete(bookOrder,storeId);
        	return JmsResponse.forQueue(message, CANCELED_QUEUE);
        } else{
            throw new IllegalArgumentException("WarehouseProcessingService.processOrder(...) - orderState does not match expected criteria!");
        }

    }
```
