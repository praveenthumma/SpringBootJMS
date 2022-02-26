# SpringBootJMS
JMS in Spring Examples.

## SingleConnectionFactory
> - A JMS ConnectionFactory adapter that returns the same Connection from all createConnection() calls, and ignores calls to Connection.close().
> - According to the JMS Connection model, this is perfectly thread-safe (in contrast to e.g. JDBC). 
> - The shared Connection can be automatically recovered in case of an Exception. This is done by updating setReconnectOnException(true).

### When is it Used ?
> - Need Connections to Multiple Different Message Oriented Middlewares (Like ActiveMQ, IBM-MQ, RabbitMQ)
> - Which means you need different connections and JmsTemplates to send Messages
> - Not useful when Load Increases
> -  CachingConnectionFactory is recommended for this
