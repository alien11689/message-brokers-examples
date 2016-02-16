package com.github.alien11689.messagenbrokers.jms.requestreply

import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.cxf.frontend.ClientProxy
import org.apache.cxf.jaxws.EndpointImpl
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean
import org.apache.cxf.transport.jms.ConnectionFactoryFeature
import org.apache.cxf.transport.jms.spec.JMSSpecConstants

import javax.jms.ConnectionFactory
import javax.jws.WebService
import javax.xml.ws.Endpoint

class SoJ {

    public static void main(String[] args) {
        ConnectionFactory cf = new ActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616");
        EndpointImpl ep = (EndpointImpl) Endpoint.create(new HelloImpl());
        ep.getFeatures().add(new ConnectionFactoryFeature(cf));
        new Thread({
            ep.publish("jms:queue:forCxf?timeToLive=1000");
        } as Runnable).start()
        Thread.sleep(60000)
        ep.close()
    }
}

@WebService
interface Hello {
    String sayHello(String name)
}

class HelloImpl implements Hello {
    @Override
    String sayHello(String name) {
        return "Hello $name"
    }
}

class SoJClient {
    public static void main(String[] args) {
        String address = "jms:queue:forCxf?timeToLive=1000&replyToName=responseFromCxf&receiveTimeout=2000";
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setTransportId(JMSSpecConstants.SOAP_JMS_SPECIFICATION_TRANSPORTID);
        factory.setServiceClass(Hello.class);
        factory.setAddress(address);
        ConnectionFactory cf = new ActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616");
        factory.getFeatures().add(new ConnectionFactoryFeature(cf))
        Hello client = (Hello) factory.create();
        String reply = client.sayHello("Alien 123");
        println "Server reply: $reply"
        ClientProxy.getClient(client).destroy()
    }
}
