package com.github.alien11689.messagenbrokers.amqp.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.github.alien11689.messagenbrokers.amqp.RmqConnectionFactory.RMQ_CONNECTION_FACTORY;

@Slf4j
public class Send {

    public static void main(String[] args) {

        Connection connection = null;
        Channel channel = null;

        try {
            connection = RMQ_CONNECTION_FACTORY.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare("exchange_for_topic", "topic");
            channel.basicPublish("exchange_for_topic", "simple.tpc.send", null, "Test rabbit".getBytes("UTF-8"));
        } catch (IOException | TimeoutException e) {
            log.error("Exception occured", e);
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException | TimeoutException e) {
                    log.error("Cannot close producer", e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (IOException e) {
                    log.error("Cannot close connection", e);
                }
            }
        }
    }
}
