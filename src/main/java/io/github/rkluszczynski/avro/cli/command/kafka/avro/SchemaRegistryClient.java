package io.github.rkluszczynski.avro.cli.command.kafka.avro;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.avro.Schema;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SchemaRegistryClient {
    private final String schemaRegistryUrl;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public SchemaRegistryClient(@Value("${schema-registry.url}") String schemaRegistryUrl) {
        this.schemaRegistryUrl = schemaRegistryUrl;
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public Map<Integer, Schema> retrieveSchemas(String subject) {
        return retrieveVersions(subject).stream()
                .collect(
                        Collectors.toMap(Function.identity(), version -> retrieveSchema(subject, version))
                );
    }

    private Schema retrieveSchema(String subject, Integer version) {
        final String schemaUrl = schemaRegistryUrl + "/subjects/" + subject + "/versions/" + version + "/schema";
        final Request schemaRequest = new Request.Builder()
                .url(schemaUrl)
                .build();
        try {
            final Response schemaResponse = httpClient.newCall(schemaRequest).execute();
            final String schemaString = schemaResponse.body().string();
            return new Schema.Parser().parse(schemaString);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("BLE", e);
        }
    }

    private List<Integer> retrieveVersions(String subject) {
        final String versionsUrl = schemaRegistryUrl + "/subjects/" + subject + "/versions";
        final Request versionsRequest = new Request.Builder()
                .url(versionsUrl)
                .build();
        try {
            final Response versionsResponse = httpClient.newCall(versionsRequest).execute();
            final Integer[] versions = objectMapper.readValue(versionsResponse.body().charStream(), Integer[].class);
            return Arrays.asList(versions);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("BLA", e);
        }
    }
}
