package com.github.alien11689.messagenbrokers.jms.soj

import com.github.alien11689.messagenbrokers.jms.soj.api.Adder
import com.github.alien11689.messagenbrokers.jms.soj.api.ToAdd
import com.github.alien11689.messagenbrokers.jms.soj.server.Server
import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.cxf.frontend.ClientProxy
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean
import org.apache.cxf.transport.jms.ConnectionFactoryFeature
import org.apache.cxf.transport.jms.spec.JMSSpecConstants
import spock.lang.Specification

import javax.jms.ConnectionFactory
import java.util.concurrent.Executors

class SoJTest extends Specification {
    String address = "jms:queue:forCxf?timeToLive=1000&replyToName=responseFromCxf&receiveTimeout=2000"

    def 'should send soap over jms'() {
        given:
            Executors.newSingleThreadExecutor().submit(new Server())
            JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean(
                transportId: JMSSpecConstants.SOAP_JMS_SPECIFICATION_TRANSPORTID,
                serviceClass: Adder,
                address: address
            )
            ConnectionFactory cf = new ActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616")
            factory.getFeatures().add(new ConnectionFactoryFeature(cf))
            Adder client = factory.create() as Adder
        expect:
            client.add(new ToAdd(a: 5, b: 8)).result == 13
        cleanup:
            ClientProxy.getClient(client).destroy()
    }
}
