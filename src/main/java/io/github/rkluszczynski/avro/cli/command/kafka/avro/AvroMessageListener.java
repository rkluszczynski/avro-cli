package io.github.rkluszczynski.avro.cli.command.kafka.avro;

import io.github.rkluszczynski.avro.cli.command.kafka.ExtendedMessageListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class AvroMessageListener extends ExtendedMessageListener<String, byte[]> {

    @Override
    public void onMessage(ConsumerRecord<String, byte[]> record) {
        log.info(String.format("{%d} offset == %d, partition == %d", incrementAndGet(), record.offset(), record.partition()));

        System.out.printf("%s%n%n", record.value());
    }

    private Log log = LogFactory.getLog(AvroMessageListener.class);
}
