package com.github.alien11689.messagenbrokers.jms.soap

import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.cxf.frontend.ClientProxy
import org.apache.cxf.jaxws.EndpointImpl
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean
import org.apache.cxf.transport.jms.ConnectionFactoryFeature
import org.apache.cxf.transport.jms.spec.JMSSpecConstants

import javax.jms.ConnectionFactory
import javax.jws.WebMethod
import javax.jws.WebParam
import javax.jws.WebService
import javax.jws.soap.SOAPBinding
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.ws.Endpoint

class SoJ {

    public static void main(String[] args) {
        ConnectionFactory cf = new ActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616");
        EndpointImpl ep = (EndpointImpl) Endpoint.create(new CalculatorSoapImpl());
        ep.getFeatures().add(new ConnectionFactoryFeature(cf));
        new Thread({
            ep.publish("jms:queue:forCxf?timeToLive=1000");
        } as Runnable).start()
        Thread.sleep(20000)

        ep.service.getEndpointInfo(ep.endpointName).interface.service
        ep.close()
    }
}

@WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
interface CalculatorSoap {
    @WebMethod
    Result add(@WebParam ToAdd toAdd)
}

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
class ToAdd {
    @XmlAttribute
    int a

    @XmlAttribute
    int b
}

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
class Result {
    @XmlAttribute
    int result
}

@WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
class CalculatorSoapImpl implements CalculatorSoap {

    @Override
    @WebMethod
    Result add(@WebParam ToAdd toAdd) {
        return new Result(result: toAdd.a + toAdd.b)
    }
}

class SoJClient {
    public static void main(String[] args) {
        String address = "jms:queue:forCxf?timeToLive=1000&replyToName=responseFromCxf&receiveTimeout=2000";
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setTransportId(JMSSpecConstants.SOAP_JMS_SPECIFICATION_TRANSPORTID);
        factory.setServiceClass(CalculatorSoap.class);
        factory.setAddress(address);
        ConnectionFactory cf = new ActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616");
        factory.getFeatures().add(new ConnectionFactoryFeature(cf))
        CalculatorSoap client = (CalculatorSoap) factory.create();
        int reply = client.add(new ToAdd(a: 5, b: 8)).result;
        println "Server reply: $reply"
        ClientProxy.getClient(client).destroy()
    }
}
