package io.github.rkluszczynski.avro.cli.command.conversion;

import org.apache.avro.Schema;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Service("AvroCliConversionService")
class ConversionService {

    void convertAvroToJson(String avroFilePath, String jsonFilePath, Schema schema, boolean rawConversion)
            throws IOException {
        final InputStream inputStream = createInputStream(avroFilePath);
        final OutputStream outputStream = createOutputStream(jsonFilePath);

        try {
            if (rawConversion) {
                RawConverterUtil.convertAvroToJson(inputStream, outputStream, schema);
            } else {
                FriendlyConverterUtil.convertAvroToJson(inputStream, outputStream, schema);
            }
        } finally {
            closeStreams(inputStream, outputStream);
        }
    }

    void convertJsonToAvro(String jsonFilePath, String avroFilePath, Schema schema, boolean rawConversion)
            throws IOException {
        final InputStream inputStream = createInputStream(jsonFilePath);
        final OutputStream outputStream = createOutputStream(avroFilePath);

        try {
            if (rawConversion) {
                RawConverterUtil.convertJsonToAvro(inputStream, outputStream, schema);
            } else {
                FriendlyConverterUtil.convertJsonToAvro(inputStream, outputStream, schema);
            }
        } finally {
            closeStreams(inputStream, outputStream);
        }
    }

    private InputStream createInputStream(String filePath) throws FileNotFoundException {
        return filePath == "-" ? System.in : new FileInputStream(filePath);
    }

    private OutputStream createOutputStream(String filePath) throws FileNotFoundException {
        return filePath == "-" ? System.out : new FileOutputStream(filePath);
    }

    private void closeStreams(InputStream inputStream, OutputStream outputStream) throws IOException {
        inputStream.close();
        outputStream.close();
    }
}
