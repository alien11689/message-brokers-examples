package com.github.alien11689.messagenbrokers.jms.soj.server;

import com.github.alien11689.messagenbrokers.jms.soj.impl.AdderImpl;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.jms.ConnectionFactoryFeature;

import javax.xml.ws.Endpoint;

import static com.github.alien11689.messagenbrokers.jms.AmqConnectionFactoryProvider.AMQ_CONNECTION_FACTORY;

public class Server implements Runnable {
    @Override
    public void run() {
        EndpointImpl ep = (EndpointImpl) Endpoint.create(new AdderImpl());
        ep.getFeatures().add(new ConnectionFactoryFeature(AMQ_CONNECTION_FACTORY));
        ep.publish("jms:queue:forCxf?timeToLive=1000");
    }
}
