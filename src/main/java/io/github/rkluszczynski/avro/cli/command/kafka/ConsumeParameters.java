package io.github.rkluszczynski.avro.cli.command.kafka;

import avro.shaded.com.google.common.collect.Lists;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.EnumConverter;
import io.github.rkluszczynski.avro.cli.command.CliCommandParameters;
import io.github.rkluszczynski.avro.cli.command.kafka.avro.DeserializationMode;
import io.github.rkluszczynski.avro.cli.command.kafka.avro.SchemaProvider;
import io.github.rkluszczynski.avro.cli.command.kafka.util.SchemaWrapperSourceConverter;
import io.github.rkluszczynski.avro.cli.util.DurationGuessConverter;

import java.time.Duration;
import java.util.List;

import static io.github.rkluszczynski.avro.cli.command.kafka.MessageTypeParameter.TEXT;
import static io.github.rkluszczynski.avro.cli.command.kafka.OffsetResetParameter.LATEST;
import static io.github.rkluszczynski.avro.cli.command.kafka.avro.DeserializationMode.HEURISTIC;

@Parameters(
        commandDescription = "Consume records from Kafka."
)
class ConsumeParameters extends CliCommandParameters {
    @Parameter(
            names = {"--bootstrap-servers", "-b"},
            description = "Bootstrap servers."
    )
    private String bootstrapServers = "localhost:9092";

    @Parameter(
            names = {"--topic", "-t"},
            description = "Kafka topic name.",
            required = true
    )
    private List<String> topics = Lists.newArrayList();

    @Parameter(
            names = {"--message-type", "-m"},
            converter = MessageTypeParameterConverter.class,
            description = "Topic message type."
    )
    private MessageTypeParameter messageType = TEXT;

    @Parameter(
            names = {"--offset-reset", "-o"},
            converter = OffsetResetParameterConverter.class,
            description = "Offset reset consumer value."
    )
    private OffsetResetParameter offsetReset = LATEST;

    @Parameter(
            names = {"--deserialization-mode", "-d"},
            converter = DeserializationModeConverter.class,
            description = "Deserialization mode (applies only to Avro message type)."
    )
    private DeserializationMode deserializationMode = HEURISTIC;

    @Parameter(
            names = {"--schema", "-s"},
            converter = SchemaWrapperSourceConverter.class,
            description = "Source of schema to read."
    )
    private List<SchemaProvider.SchemaWrapper> schemas;

    @Parameter(
            names = {"--duration"},
            converter = DurationGuessConverter.class,
            description = "Read duration in ISO-8601 format (PnDTnHnMn.nS)."
    )
    private Duration duration;

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public List<String> getTopics() {
        return topics;
    }

    public MessageTypeParameter getMessageType() {
        return messageType;
    }

    public OffsetResetParameter getOffsetReset() {
        return offsetReset;
    }

    public DeserializationMode getDeserializationMode() {
        return deserializationMode;
    }

    public List<SchemaProvider.SchemaWrapper> getSchemas() {
        return schemas;
    }

    public Duration getDuration() {
        return duration;
    }

    private static class MessageTypeParameterConverter extends EnumConverter<MessageTypeParameter> {
        private MessageTypeParameterConverter(String optionName, Class<MessageTypeParameter> clazz) {
            super(optionName, clazz);
        }
    }

    private static class OffsetResetParameterConverter extends EnumConverter<OffsetResetParameter> {
        private OffsetResetParameterConverter(String optionName, Class<OffsetResetParameter> clazz) {
            super(optionName, clazz);
        }
    }

    private static class DeserializationModeConverter extends EnumConverter<DeserializationMode> {
        private DeserializationModeConverter(String optionName, Class<DeserializationMode> clazz) {
            super(optionName, clazz);
        }

        @Override
        public DeserializationMode convert(String value) {
            return super.convert(value.replace('-', '_'));
        }
    }
}
