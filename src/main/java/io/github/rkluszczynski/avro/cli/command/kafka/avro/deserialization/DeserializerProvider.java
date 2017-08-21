package io.github.rkluszczynski.avro.cli.command.kafka.avro.deserialization;

import io.github.rkluszczynski.avro.cli.command.kafka.avro.AvroDeserializer;
import io.github.rkluszczynski.avro.cli.command.kafka.avro.DeserializationMode;

import java.util.ArrayList;
import java.util.Collection;

public final class DeserializerProvider {

    public static Collection<AvroDeserializer> prepareDeserializers(DeserializationMode deserializationMode) {
        final ArrayList<AvroDeserializer> deserializers = new ArrayList<>();
        switch (deserializationMode) {
            case HEURISTIC:
                deserializers.add(new MagicByteDeserializer(null));
                deserializers.add(new RawDataDeserializer());
                deserializers.add(new FingerprintDeserializer());
                deserializers.add(new ConfluentDeserializer());
                break;
            case AVRO_WIRE_FORMAT:
                deserializers.add(new FingerprintDeserializer());
                break;
            case CONFLUENT_FORMAT:
                deserializers.add(new ConfluentDeserializer());
                break;
            case MAGIC_BYTE_FORMAT:
                deserializers.add(new MagicByteDeserializer(null));
                break;
            default:
                deserializers.add(new RawDataDeserializer());
        }
        return deserializers;
    }

    private DeserializerProvider() {
    }
}
