# Quickstart

To see usage, you need to have Java 8 installed. Then just download latest release version 
[here](https://github.com/rkluszczynski/avro-cli/releases/download/avro-cli-X.X.X/avro-cli-X.X.X.jar).

Next, just type:

```bash
java -jar avro-cli-X.X.X.jar 
```

for help.

# Commands

### convert: Avro <-> JSON conversion

Note, that this conversion is without schema included in Avro files.

```
Usage: convert [options]
  Options:
    --inputFile, -i
      Source file with message.
    --outputFile, -o
      Target file of converted message.
  * --schemaFile, -s
      Source of schema to read.
    --toAvro, -a
      Convert from JSON to Avro.
      Default: false
    --toJson, -j
      Convert from Avro to JSON.
      Default: false
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
    --previousSchemaFile, -p
      Sources of previous schemas in order of appearance in command line.
      Default: []
  * --schemaFile, -s
      Source of schema to read.
```

Example of usage:

```bash
java -jar avro-cli-X.X.X.jar validate -c backward -f schema2-string-null-field.json -p schema1-string-field.json 
```

where files can bee seen [here](https://github.com/rkluszczynski/avro-cli/tree/master/src/test/resources/validation).

# Credits

 * [Apache Avro™ 1.8.1 Specification](http://avro.apache.org/docs/1.8.1/spec.html)
 * [Spring boot](https://projects.spring.io/spring-boot)
 * [JCommander](http://jcommander.org)
