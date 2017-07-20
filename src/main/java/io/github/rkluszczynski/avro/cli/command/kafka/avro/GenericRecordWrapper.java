package io.github.rkluszczynski.avro.cli.command.kafka.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

public class GenericRecordWrapper {
    private final GenericRecord record;
    private final Schema schema;
    private final String versionCode;

    private long timestamp;

    public GenericRecordWrapper(GenericRecord record, Schema schema, String versionCode) {
        this.record = record;
        this.schema = schema;
        this.versionCode = versionCode;
    }

    public GenericRecord getRecord() {
        return record;
    }

    public Schema getSchema() {
        return schema;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
