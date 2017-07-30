package io.github.rkluszczynski.avro.cli.command.kafka;

import org.springframework.kafka.listener.MessageListener;

public abstract class ExtendedMessageListener<K, V> implements MessageListener<K, V> {
    private volatile long count = 0L;

    protected long incrementAndGet() {
        return ++count;
    }
}
