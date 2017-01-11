package io.github.rkluszczynski.avro.cli.command.conversion;

import org.apache.avro.Schema;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Service("AvroCliConversionService")
class ConversionService {

    void convertAvroToJson(String avroFilePath, String jsonFilePath, Schema schema) throws IOException {
        final FileInputStream inputStream = new FileInputStream(avroFilePath);
        final FileOutputStream outputStream = new FileOutputStream(jsonFilePath);

        try {
            ConverterUtil.convertAvroToJson(inputStream, outputStream, schema);
        } finally {
            closeStreams(inputStream, outputStream);
        }
    }

    void convertJsonToAvro(String jsonFilePath, String avroFilePath, Schema schema) throws IOException {
        final FileInputStream inputStream = new FileInputStream(jsonFilePath);
        final FileOutputStream outputStream = new FileOutputStream(avroFilePath);

        try {
            ConverterUtil.convertJsonToAvro(inputStream, outputStream, schema);
        } finally {
            closeStreams(inputStream, outputStream);
        }
    }

    private void closeStreams(InputStream inputStream, OutputStream outputStream) throws IOException {
        inputStream.close();
        outputStream.close();
    }
}
