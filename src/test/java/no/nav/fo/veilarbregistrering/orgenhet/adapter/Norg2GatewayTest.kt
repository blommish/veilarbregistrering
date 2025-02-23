package no.nav.fo.veilarbregistrering.orgenhet.adapter

import no.nav.fo.veilarbregistrering.FileToJson
import no.nav.fo.veilarbregistrering.enhet.Kommune
import no.nav.fo.veilarbregistrering.log.CallId.leggTilCallId
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr.Companion.of
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockserver.integration.ClientAndServer
import org.mockserver.junit.jupiter.MockServerExtension
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.MediaType

@ExtendWith(MockServerExtension::class)
class Norg2GatewayTest(private val mockServer: ClientAndServer) {

    @BeforeEach
    fun setup() {
        leggTilCallId()
    }

    private fun buildClient(): Norg2RestClient {
        val baseUrl = "http://" + mockServer.remoteAddress().address.hostName + ":" + mockServer.remoteAddress().port
        return Norg2RestClient(baseUrl)
    }

    @Test
    fun skal_hente_enhetsnr_fra_norg2_for_kommunenummer() {
        val norg2Gateway = Norg2GatewayImpl(buildClient())

        val json = FileToJson.toJson("/orgenhet/orgenhet.json")

        mockServer.`when`(
                HttpRequest
                        .request()
                        .withMethod("POST")
                        .withPath("/api/v1/arbeidsfordeling/enheter/bestmatch"))
                .respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(json, MediaType.JSON_UTF_8))

        val enhetsnr = norg2Gateway.hentEnhetFor(Kommune("0302"))

        Assertions.assertThat(enhetsnr).isNotEmpty
        Assertions.assertThat(enhetsnr).hasValue(of("0393"))
    }
}