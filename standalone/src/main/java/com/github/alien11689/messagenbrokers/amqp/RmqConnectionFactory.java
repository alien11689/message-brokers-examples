package com.github.alien11689.messagenbrokers.amqp;

import com.rabbitmq.client.ConnectionFactory;

public class RmqConnectionFactory {
    public static final ConnectionFactory RMQ_CONNECTION_FACTORY = createConnection();

    private static ConnectionFactory createConnection() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        return connectionFactory;
    }

}
