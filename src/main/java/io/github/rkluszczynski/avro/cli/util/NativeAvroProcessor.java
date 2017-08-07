package io.github.rkluszczynski.avro.cli.util;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonDecoder;
import org.apache.avro.io.JsonEncoder;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class NativeAvroProcessor {

    public static void convertAvroToJson(InputStream inputStream,
                                         OutputStream outputStream,
                                         Schema readerSchema,
                                         Schema writerSchema,
                                         boolean prettyPrint) throws IOException {
        DatumReader<Object> reader = new GenericDatumReader<>(readerSchema);
        DatumWriter<Object> writer = new GenericDatumWriter<>(writerSchema);

        BinaryDecoder binaryDecoder = DecoderFactory.get().binaryDecoder(inputStream, null);

        JsonEncoder jsonEncoder = EncoderFactory.get().jsonEncoder(writerSchema, outputStream, prettyPrint);
        Object datum = null;
        while (!binaryDecoder.isEnd()) {
            datum = reader.read(datum, binaryDecoder);
            writer.write(datum, jsonEncoder);
            jsonEncoder.flush();
        }
        outputStream.flush();
    }

    public static void convertJsonToAvro(InputStream inputStream,
                                         OutputStream outputStream,
                                         Schema readerSchema,
                                         Schema writerSchema) throws IOException {
        DatumReader<Object> reader = new GenericDatumReader<>(readerSchema);
        DatumWriter<Object> writer = new GenericDatumWriter<>(writerSchema);

        Encoder binaryEncoder = EncoderFactory.get().binaryEncoder(outputStream, null);

        JsonDecoder jsonDecoder = DecoderFactory.get().jsonDecoder(readerSchema, inputStream);
        Object datum = null;
        while (true) {
            try {
                datum = reader.read(datum, jsonDecoder);
            } catch (EOFException eofException) {
                break;
            }
            writer.write(datum, binaryEncoder);
            binaryEncoder.flush();
        }
        outputStream.flush();
    }

    private NativeAvroProcessor() {
    }
}
