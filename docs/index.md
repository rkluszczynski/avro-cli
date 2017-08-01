# Quickstart

To see usage, you need to have Java 8 installed. Next, download latest release version from 
[here](https://github.com/rkluszczynski/avro-cli/releases/download/avro-cli-0.2.7/avro-cli-0.2.7.jar)
or using command below.

```bash
curl -L -O https://github.com/rkluszczynski/avro-cli/releases/download/avro-cli-0.2.7/avro-cli-0.2.7.jar
```

Finally, just type:

```bash
java -jar avro-cli-0.2.7.jar 
```

to print help.

# Commands

### convert: Avro <-> JSON conversion

Note, that this conversion is without schema included in Avro files.

```
Avro <-> JSON conversion (without schema included).
Usage: convert [options]
  Options:
    --inputFile, -i
      Source file with message.
      Default: -
    --outputFile, -o
      Target file of converted message.
      Default: -
    --rawAvroConversion, -r
      Using raw Avro conversion.
      Default: false
  * --schema, -s
      Source of schema to read.
    --toAvro, -a
      Convert from JSON to Avro.
      Default: false
    --toJson, -j
      Convert from Avro to JSON.
      Default: false
```

Example of usage printing JSON from Avro message to standard output:

```bash
java -jar avro-cli-0.2.7.jar convert -j -s schema-friendly-union.avsc -i message-friendly-union.avro 

```

where files are [here](https://github.com/rkluszczynski/avro-cli/tree/master/src/test/resources/conversion).

### fingerprint: Prints fingerprint of schema canonical form

```
Usage: fingerprint [options]
  Options:
    --algorithm, -a
      Algorithm code for fingerprint (i.e. CRC-64-AVRO, MD5, SHA-256). Default 
      is Rabin fingerprint.
  * --schema, -s
      Source of schema to read.
```

### kafka-consume: Consume records from Kafka

```bash
Usage: kafka-consume [options]
  Options:
    --bootstrap-servers, -b
      Bootstrap servers.
      Default: localhost:9092
    --duration
      Read duration in ISO-8601 format (PnDTnHnMn.nS).
    --message-type, -m
      Topic message type.
      Default: TEXT
      Possible Values: [TEXT]
    --offset-reset, -o
      Offset reset consumer value.
      Default: LATEST
      Possible Values: [LATEST, EARLIEST]
  * --topic, -t
      Kafka topic name.
      Default: []
```

### normalize: Normalize schema to canonical parsing form

```
Usage: normalize [options]
  Options:
    --outputFile, -o
      Target file of normalized schema. Default is standard output.
      Default: -
  * --schema, -s
      Source of schema to read.
```

### validate: Native Avro validation

```
Usage: validate [options]
  Options:
    --compatibility, -c
      One of compatibility strategy.
      Default: FULL
      Possible Values: [BACKWARD, FORWARD, FULL]
    --latest, -l
      Use only latest validator.
      Default: false
    --previousSchema, -p
      Sources of previous schemas in order of appearance in command line.
      Default: []
  * --schema, -s
      Source of schema to read.
```

Example of usage:

```bash
java -jar avro-cli-0.2.7.jar validate -c backward -s schema2-string-null-field.json -p schema1-string-field.json 
```

where files can bee seen [here](https://github.com/rkluszczynski/avro-cli/tree/master/src/test/resources/validation).


# Schema sources

As of version `0.2.1` one can use URL address as schema source. For testing just start simple HTTP server in 
resources folder to serve schemas files.

```bash
( cd src/test/resources/ && python -m SimpleHTTPServer 8000 )
```

Finally, validate schema against empty history using command below.

```bash
java -jar avro-cli-0.2.7.jar validate -s http://localhost:8000/schema-no-fields.avsc
```

# Credits

 * [Apache Avro? Specification](http://avro.apache.org/docs/current/spec.html)
 * [JCommander](http://jcommander.org)
 * [Spring Boot](https://projects.spring.io/spring-boot)
 * [Spring for Apache Kafka](https://projects.spring.io/spring-kafka)
