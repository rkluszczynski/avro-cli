package io.github.rkluszczynski.avro.cli.command.conversion;

import org.apache.avro.Schema;
import org.apache.commons.compress.utils.IOUtils;
import tech.allegro.schema.json2avro.converter.JsonAvroConverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

final class FriendlyConverterUtil {
    private static final JsonAvroConverter converter = new JsonAvroConverter();

    static void convertAvroToJson(InputStream inputStream, OutputStream outputStream, Schema schema)
            throws IOException {
        outputStream.write(
                converter.convertToJson(
                        IOUtils.toByteArray(inputStream),
                        schema
                )
        );
        outputStream.flush();
    }

    static void convertJsonToAvro(InputStream inputStream, OutputStream outputStream, Schema schema)
            throws IOException {
        outputStream.write(
                converter.convertToAvro(
                        IOUtils.toByteArray(inputStream),
                        schema
                )
        );
        outputStream.flush();
    }

    private FriendlyConverterUtil() {
    }
}
