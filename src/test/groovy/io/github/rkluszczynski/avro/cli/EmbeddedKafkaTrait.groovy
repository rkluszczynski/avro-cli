package io.github.rkluszczynski.avro.cli

trait EmbeddedKafkaTrait {

    EmbeddedKafkaInstance startEmbeddedKafka(String... topics) {
        def embeddedKafka = new EmbeddedKafkaInstance(topics)
        embeddedKafka.before()

        embeddedKafka
    }
}
