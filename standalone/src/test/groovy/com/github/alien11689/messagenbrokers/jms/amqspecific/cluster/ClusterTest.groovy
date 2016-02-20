package com.github.alien11689.messagenbrokers.jms.amqspecific.cluster

import com.github.alien11689.messagenbrokers.helper.Docker
import spock.lang.Requires
import spock.lang.Specification

import javax.jms.Connection
import javax.jms.MessageConsumer
import javax.jms.MessageProducer
import javax.jms.Session
import javax.jms.TextMessage

import static com.github.alien11689.messagenbrokers.jms.AmqConnectionFactoryProvider.AMQ_CONNECTION_FACTORY_CLUSTER

@Requires({ Docker.isRunning('postgres') })
class ClusterTest extends Specification {

    @Requires({ Docker.isRunning('amqWithDb1') && !Docker.isRunning('amqWithDb2') })
    def 'should send message to amq1'() {
        given:
            Connection connection = AMQ_CONNECTION_FACTORY_CLUSTER.createConnection()
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
            MessageProducer messageProducer = session.createProducer(session.createQueue('queue1'))
            TextMessage message = session.createTextMessage('Cluster test')
        when:
            messageProducer.send(message)
        then:
            noExceptionThrown()
    }

    @Requires({ !Docker.isRunning('amqWithDb1') && Docker.isRunning('amqWithDb2') })
    def 'should receive message from amq2'() {
        given:
            Connection connection = AMQ_CONNECTION_FACTORY_CLUSTER.createConnection()
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
            MessageConsumer consumer = session.createConsumer(session.createQueue('queue1'))
            connection.start()
        when:
            TextMessage message = consumer.receive(1000) as TextMessage
        then:
            message?.text == 'Cluster test'
    }
}