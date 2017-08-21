package io.github.rkluszczynski.avro.cli.command.kafka;

import io.github.rkluszczynski.avro.cli.command.kafka.avro.AvroDeserializer;
import io.github.rkluszczynski.avro.cli.command.kafka.avro.AvroMessageListener;
import io.github.rkluszczynski.avro.cli.command.kafka.avro.CommandLineSchemaProvider;
import io.github.rkluszczynski.avro.cli.command.kafka.avro.SchemaProvider;
import io.github.rkluszczynski.avro.cli.command.kafka.text.TextMessageListener;
import org.springframework.kafka.listener.MessageListener;

import java.util.Collection;
import java.util.Map;

import static io.github.rkluszczynski.avro.cli.command.kafka.MessageTypeParameter.AVRO;
import static io.github.rkluszczynski.avro.cli.command.kafka.avro.deserialization.DeserializerProvider.prepareDeserializers;

public abstract class ExtendedMessageListener<K, V> implements MessageListener<K, V> {
    private volatile long count = 0L;

    public abstract void applyMessageListenerConfig(Map<String, Object> consumerConfig);

    protected long incrementAndGet() {
        return ++count;
    }

    static ExtendedMessageListener ofConsumeParameters(ConsumeParameters consumeParameters) {
        final MessageTypeParameter messageType = consumeParameters.getMessageType();

        if (AVRO.equals(messageType)) {
            final SchemaProvider schemaProvider = prepareSchemaProvider(consumeParameters);
            final Collection<AvroDeserializer> deserializers =
                    prepareDeserializers(consumeParameters.getDeserializationMode());

            return new AvroMessageListener(schemaProvider, deserializers);
        }
        return new TextMessageListener();
    }

    private static SchemaProvider prepareSchemaProvider(ConsumeParameters consumeParameters) {
        return new CommandLineSchemaProvider(consumeParameters.getSchemas());
    }
}
