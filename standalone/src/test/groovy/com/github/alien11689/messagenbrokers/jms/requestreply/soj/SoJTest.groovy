package com.github.alien11689.messagenbrokers.jms.requestreply.soj

import com.github.alien11689.messagenbrokers.helper.Docker
import com.github.alien11689.messagenbrokers.jms.requestreply.soj.api.Adder
import com.github.alien11689.messagenbrokers.jms.requestreply.soj.api.ToAdd
import com.github.alien11689.messagenbrokers.jms.requestreply.soj.server.Server
import org.apache.cxf.frontend.ClientProxy
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean
import org.apache.cxf.transport.jms.ConnectionFactoryFeature
import org.apache.cxf.transport.jms.spec.JMSSpecConstants
import spock.lang.Requires
import spock.lang.Specification

import java.util.concurrent.Executors

import static com.github.alien11689.messagenbrokers.jms.AmqConnectionFactoryProvider.AMQ_CONNECTION_FACTORY

@Requires({ Docker.isRunning('justAmq') })
class SoJTest extends Specification {
    String address = 'jms:queue:forCxf?timeToLive=3000&replyToName=responseFromCxf&receiveTimeout=2000'

    def 'should send soap over jms'() {
        given:
            Executors.newSingleThreadExecutor().submit(new Server())
            JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean(
                transportId: JMSSpecConstants.SOAP_JMS_SPECIFICATION_TRANSPORTID,
                serviceClass: Adder,
                address: address
            )
            factory.getFeatures().add(new ConnectionFactoryFeature(AMQ_CONNECTION_FACTORY))
            Adder client = factory.create() as Adder
        expect:
            client.add(new ToAdd(a: 5, b: 8)).result == 13
        cleanup:
            ClientProxy.getClient(client).destroy()
    }
}
