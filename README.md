# SpringBootJMS
JMS in Spring Examples.

## SingleConnectionFactory
> - A JMS ConnectionFactory adapter that returns the same Connection from all __createConnection()__ calls, and ignores calls to __Connection.close()__.
> - According to the JMS Connection model, this is perfectly __thread-safe__ (in contrast to e.g. JDBC). 
> - The shared Connection can be automatically recovered in case of an Exception. This is done by updating __setReconnectOnException(true)__.
> - By Default __setReconnectOnException(true)__ is true
> - Ignores calls to __connection.close()__

### When is it Used ?
> - Need Connections to Multiple Different Message Oriented Middlewares (Like ActiveMQ, IBM-MQ, RabbitMQ)
> - Which means you need different connections and JmsTemplates to send Messages
> - Not useful when Load Increases
> -  CachingConnectionFactory is recommended for this


## CachingConnectionFactory

SingleConnectionFactory subclass that adds Session caching as well MessageProducer caching. 
> - This ConnectionFactory also switches the "reconnectOnException" property to "true" by default, allowing for automatic recovery of the underlying Connection.
> - By Default __setReconnectOnException(true)__ is true
> - Ignores calls to __connection.close()__
> - By default, only one single Session will be cached, with further requested Sessions being created and disposed on demand. 
> - Consider raising the __"sessionCacheSize"__ value in case of a high-concurrency environment.
> - __This ConnectionFactory requires explicit closing of all Sessions obtained from its shared Connection.__ This is the usual recommendation for native JMS access code anyway. However, with this ConnectionFactory, its use is mandatory in order to actually allow for Session reuse.


