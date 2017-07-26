package io.github.rkluszczynski.avro.cli

import kafka.admin.AdminClient
import org.apache.kafka.clients.CommonClientConfigs
import org.junit.rules.ExternalResource
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.kafka.test.rule.KafkaEmbedded
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.util.concurrent.ListenableFuture

class EmbeddedKafkaInstance extends ExternalResource {
    private final KafkaEmbedded embeddedKafka

    private KafkaTemplate kafkaTemplate
    private AdminClient kafkaAdminClient

    String brokersAsString

    EmbeddedKafkaInstance(String... topics) {
        embeddedKafka = new KafkaEmbedded(1, true, 1, topics)
    }

    ListenableFuture<SendResult<Integer, String>> send(String topic, String message) {
        kafkaTemplate.send(topic, message)
    }

    boolean hasAnyConsumerEver() {
        kafkaAdminClient.listAllConsumerGroupsFlattened().size() != 0
    }

    void shutdown() {
        after()
    }

    @Override
    void before() throws Exception {
        super.before()
        embeddedKafka.before()

        kafkaTemplate = createKafkaTemplate()
        kafkaAdminClient = createAdminClient()

        brokersAsString = embeddedKafka.brokersAsString
    }

    @Override
    void after() {
        super.after()
        embeddedKafka.after()
    }

    private createKafkaTemplate() {
        def senderProperties = KafkaTestUtils.senderProps(embeddedKafka.brokersAsString)
        def producerFactory = new DefaultKafkaProducerFactory<Integer, String>(senderProperties)

        new KafkaTemplate<Integer, String>(producerFactory)
    }

    private createAdminClient() {
        Properties props = new Properties()
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, embeddedKafka.brokersAsString)

        AdminClient.create(props)
    }
}
