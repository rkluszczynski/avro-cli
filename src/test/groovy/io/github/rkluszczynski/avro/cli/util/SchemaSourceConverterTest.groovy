package io.github.rkluszczynski.avro.cli.util

import com.github.tomakehurst.wiremock.WireMockServer
import io.github.rkluszczynski.avro.cli.WiremockTrait
import org.apache.avro.Schema
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo

class SchemaSourceConverterTest extends Specification implements WiremockTrait {

    def 'should convert file path to schema'() {
        expect:
        checkConvertedSchema(new SchemaSourceConverter()
                .convert('src/test/resources/schema-no-fields.avsc'))
    }

    def 'should convert url content to schema'() {
        setup:
        WireMockServer wireMockServer = createWiremockServer()
        wireMockServer.start()

        wireMockServer.stubFor(get(urlEqualTo('/schema-no-fields'))
                .willReturn(aResponse().withStatus(200)
                .withBody(new File('src/test/resources/schema-no-fields.avsc').text)))

        expect:
        checkConvertedSchema(new SchemaSourceConverter()
                .convert("http://localhost:${wireMockServer.port()}/schema-no-fields"))

        cleanup:
        wireMockServer.stop()
    }

    private checkConvertedSchema(schema) {
        schema.type == Schema.Type.RECORD && schema.name == 'testRecord' &&
                schema.namespace == 'io.github.rkluszczynski.avro.cli' && schema.fields == []
    }
}
