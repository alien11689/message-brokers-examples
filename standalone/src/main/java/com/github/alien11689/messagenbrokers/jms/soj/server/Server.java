package com.github.alien11689.messagenbrokers.jms.soj.server;

import com.github.alien11689.messagenbrokers.jms.soj.impl.AdderImpl;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.jms.ConnectionFactoryFeature;

import javax.jms.ConnectionFactory;
import javax.xml.ws.Endpoint;

public class Server implements Runnable {
    @Override
    public void run() {
        ConnectionFactory cf = new ActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616");
        EndpointImpl ep = (EndpointImpl) Endpoint.create(new AdderImpl());
        ep.getFeatures().add(new ConnectionFactoryFeature(cf));
        ep.publish("jms:queue:forCxf?timeToLive=1000");
    }
}
