FROM rabbitmq:3.6.0

RUN curl http://www.rabbitmq.com/community-plugins/v3.6.x/rabbitmq_delayed_message_exchange-0.0.1.ez > rabbitmq_delayed_message_exchange-0.0.1.ez && \
    mv rabbitmq_delayed_message_exchange-0.0.1.ez /usr/lib/rabbitmq/lib/rabbitmq_server-3.6.0/plugins/ && \
    rabbitmq-plugins enable --offline rabbitmq_delayed_message_exchange && \
    rabbitmq-plugins enable --offline rabbitmq_management


ENTRYPOINT ["/docker-entrypoint.sh"]

EXPOSE 5672 4369 25672 15672
CMD ["rabbitmq-server"]
