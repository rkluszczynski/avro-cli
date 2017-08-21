package io.github.rkluszczynski.avro.cli.command.kafka.avro;

import org.apache.avro.Schema;

public interface SchemaProvider extends Iterable<SchemaProvider.SchemaWrapper> {

    class SchemaWrapper {
        private final Schema schema;
        private final String id;

        public SchemaWrapper(Schema schema, String id) {
            this.schema = schema;
            this.id = id;
        }

        public Schema getSchema() {
            return schema;
        }

        public String getId() {
            return id;
        }
    }
}
