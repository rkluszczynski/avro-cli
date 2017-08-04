package io.github.rkluszczynski.avro.cli.command.kafka;

import org.springframework.kafka.listener.MessageListener;

import java.util.Map;

public abstract class ExtendedMessageListener<K, V> implements MessageListener<K, V> {
    private volatile long count = 0L;

    public abstract void applyMessageListenerConfig(Map<String, Object> consumerConfig);

    protected long incrementAndGet() {
        return ++count;
    }
}
