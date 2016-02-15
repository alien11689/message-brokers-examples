package com.github.alien11689.messagenbrokers.camel;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class CamelRoute extends RouteBuilder {


    @Override
    public void configure() throws Exception {

        from("rabbitmq:localhost:5672/tasks?username=admin&password=admin&autoDelete=false&durable=true&exchangeType=direct&routingKey=camel&queue=forCamel")
            .log(LoggingLevel.INFO, "Received message ${body}")
            .to("amq:topic:fromCamel");
    }

    public static void main(String[] args) throws Exception {
        DefaultCamelContext context = new DefaultCamelContext();
        context.addComponent("amq", ActiveMQComponent.jmsComponent(new ActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616")));
        context.addRoutes(new CamelRoute());
        context.start();
        Thread.sleep(60000);
    }
}
