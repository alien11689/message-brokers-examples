package com.github.alien11689.messagenbrokers.jms;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.ConnectionFactory;

public class AmqConnectionFactoryProvider {
    public static final ConnectionFactory AMQ_CONNECTION_FACTORY = new ActiveMQConnectionFactory(
        "admin", "admin",
        "tcp://localhost:61616"
    );

    public static final ConnectionFactory AMQ_CONNECTION_FACTORY2 = new ActiveMQConnectionFactory(
        "admin", "admin",
        "tcp://localhost:61617"
    );
}
