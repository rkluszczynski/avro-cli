package io.github.rkluszczynski.avro.cli.command.kafka.text;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

import java.util.List;

public class BatchTextMessageListener implements MessageListener<String, String> {
    private volatile long count = 0L;

    public void onMessage(List<ConsumerRecord<String, String>> data) {
        data.forEach(this::onMessage);
    }

    @Override
    public void onMessage(ConsumerRecord<String, String> record) {
        log.info(String.format("{%d} offset == %d, partition == %d", ++count, record.offset(), record.partition()));

        System.out.printf("%s%n%n", record.value());
    }

    public long getCount() {
        return count;
    }

    private Log log = LogFactory.getLog(BatchTextMessageListener.class);
}
