package io.github.rkluszczynski.avro.cli

import com.github.tomakehurst.wiremock.WireMockServer

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

trait WiremockTrait {
    def createWiremockServer() {
        new WireMockServer(
                wireMockConfig().dynamicPort()
        )
    }
}