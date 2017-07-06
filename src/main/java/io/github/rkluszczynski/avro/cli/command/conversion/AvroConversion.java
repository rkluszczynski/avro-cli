package io.github.rkluszczynski.avro.cli.command.conversion;

import io.github.rkluszczynski.avro.cli.CliMainParameters;
import io.github.rkluszczynski.avro.cli.CommandException;
import io.github.rkluszczynski.avro.cli.command.CliCommand;
import io.github.rkluszczynski.avro.cli.command.CliCommandParameters;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.avro.AvroSchemaConverter;
import org.apache.parquet.avro.AvroWriteSupport;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.schema.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.github.rkluszczynski.avro.cli.command.CommandNames.CONVERT;

@Component
public class AvroConversion implements CliCommand {
    private final ConversionParameters conversionParameters = new ConversionParameters();

    private final ConversionService conversionService;

    @Autowired
    public AvroConversion(@Qualifier("AvroCliConversionService") ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public String execute(CliMainParameters mainParameters) {
        if (conversionParameters.isToAvro() == conversionParameters.isToJson()) {
            throw new CommandException("Exactly one of target format should be indicated (Avro or JSON).");
        }

        try {
            if (conversionParameters.isToAvro()) {
                conversionService.convertJsonToAvro(
                        conversionParameters.getInputFile(),
                        conversionParameters.getOutputFile(),
                        conversionParameters.getSchema(),
                        conversionParameters.isRawAvroConversion()
                );
            } else if (conversionParameters.isToJson()) {
                conversionService.convertAvroToJson(
                        conversionParameters.getInputFile(),
                        conversionParameters.getOutputFile(),
                        conversionParameters.getSchema(),
                        conversionParameters.isRawAvroConversion()
                );
            } else {
                throw new IllegalStateException("This should never happens!");
            }
        } catch (IOException | IllegalStateException ex) {
            throw new CommandException("Could not convert!", ex);
        }
        return "DONE";
    }

    @Override
    public String getCommandName() {
        return CONVERT.getCliCommand();
    }

    @Override
    public CliCommandParameters getParameters() {
        return conversionParameters;
    }

    public static void main(String[] args) throws Exception {
        // load your Avro schema
        Schema avroSchema = new Schema.Parser().parse(
                new java.io.File("src/test/resources/validation/schema1-string-field.json"));

        // generate the corresponding Parquet schema
        MessageType parquetSchema = new AvroSchemaConverter().convert(avroSchema);

        // create a WriteSupport object to serialize your Avro objects
        AvroWriteSupport writeSupport = new AvroWriteSupport(parquetSchema, avroSchema);

        // choose compression scheme
        CompressionCodecName compressionCodecName = CompressionCodecName.UNCOMPRESSED;

        // set Parquet file block size and page size values
        int blockSize = 256 * 1024 * 1024;
        int pageSize = 64 * 1024;

        Path outputPath = new Path("qq.parquet");






//        // the ParquetWriter object that will consume Avro GenericRecords
//        try (ParquetWriter parquetWriter = new AvroParquetWriter(outputPath,
//                avroSchema, compressionCodecName, blockSize, pageSize)) {
//            parquetWriter.write(null);
//        }


        List<Schema.Field> fields = new ArrayList<>();
        fields.add(new Schema.Field("myarray", Schema.createArray(
                Schema.create(Schema.Type.INT)), null, null));
        Schema mySchema = Schema.createRecord("name", null, "pl.allegro.ns", false, fields);

//        AvroParquetWriter<GenericRecord> writer =
//                new AvroParquetWriter<GenericRecord>(file, mySchema);

        final ParquetWriter<GenericRecord> writer = AvroParquetWriter.<GenericRecord>builder(outputPath)
                .withSchema(mySchema)
                .withCompressionCodec(compressionCodecName)
                .withPageSize(pageSize)
                .build();


        // Write a record with an empty array.
        List<Integer> emptyArray = new ArrayList<>();
        emptyArray.add(1);
        emptyArray.add(17);
        GenericData.Record record = new GenericRecordBuilder(mySchema)
                .set("myarray", emptyArray).build();
        writer.write(record);
        writer.close();

//        AvroParquetReader<GenericRecord> reader = new AvroParquetReader<GenericRecord>(file);
//        GenericRecord nextRecord = reader.read();

        System.out.println("DONE");


//
//        final String schemaLocation = "src/test/resources/validation/schema1-string-field.json";
//        final Schema avroSchema = new Schema.Parser().parse(new File(schemaLocation));
//
//        final MessageType parquetSchema = new AvroSchemaConverter().convert(avroSchema);
//        final WriteSupport<Pojo> writeSupport = new AvroWriteSupport(parquetSchema, avroSchema);
//        final String parquetFile = "data.parquet";
//        final Path path = new Path(parquetFile);
//        ParquetWriter<GenericRecord> parquetWriter = new ParquetWriter(path, writeSupport, compressionCodecName, blockSize, pageSize);
//        final GenericRecord record = new GenericData.Record(avroSchema);
//        record.put("id", 1);
//        record.put("age", 10);
//        record.put("name", "ABC");
//        record.put("place", "BCD");
//        parquetWriter.write(record);
//        parquetWriter.close();

    }
}
