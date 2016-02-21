package com.github.alien11689.messagenbrokers.camel;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

@Slf4j
public class CamelRoute extends RouteBuilder implements Runnable {

    @Override
    public void configure() throws Exception {
        from("rabbitmq:localhost:5672/tasks?username=admin&password=admin&autoDelete=false&durable=true&exchangeType=direct&routingKey=camel&queue=forCamel")
            .log(LoggingLevel.INFO, "Received message ${body}")
            .convertBodyTo(String.class)
            .to("amq:queue:fromCamel");
    }

    @Override
    public void run() {
        DefaultCamelContext context = new DefaultCamelContext();
        context.addComponent("amq", ActiveMQComponent.jmsComponent(new ActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616")));
        try {
            context.addRoutes(this);
            context.start();
        } catch (Exception e) {
            log.error("Error during route starting", e);
        }
    }
}
