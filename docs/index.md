# Quickstart

To see usage, you need to have Java 8 installed. Then just download latest release and type:

```bash
java -jar avro-cli-X.X.X.jar 
```

where X.X.X stands for version.

# Commands

### Native Avro validation

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
  * --schemaFile, -f
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
