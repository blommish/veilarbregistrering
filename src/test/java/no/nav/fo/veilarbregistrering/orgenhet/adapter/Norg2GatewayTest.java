package no.nav.fo.veilarbregistrering.orgenhet.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetsnr;
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class Norg2GatewayTest {

    private Norg2Gateway norg2Gateway;

    @BeforeEach
    public void setUp() {
        Norg2RestClient norg2RestClient = new Norg2StubClient();
        norg2Gateway = new Norg2GatewayImpl(norg2RestClient);
    }

    @Test
    public void skal_hente_enhetsnr_fra_norg2_for_kommunenummer() {
        Optional<Enhetsnr> enhetsnr = norg2Gateway.hentEnhetFor(Kommunenummer.of("0302"));
        assertThat(enhetsnr).isNotEmpty();
        assertThat(enhetsnr).hasValue(Enhetsnr.of("0393"));
    }

    private class Norg2StubClient extends Norg2RestClient {

        private final Gson gson = new GsonBuilder().create();

        private static final String OK_JSON = "/orgenhet/orgenhet.json";

        Norg2StubClient() {
            super(null);
        }

        @Override
        List<RsNavKontorDto> utfoerRequest(RsArbeidsfordelingCriteriaDto rsArbeidsfordelingCriteriaDto) {
            String json = toJson(OK_JSON);
            RsNavKontorDto rsNavKontorDto = gson.fromJson(json, RsNavKontorDto.class);
            return Arrays.asList(rsNavKontorDto);
        }

        private String toJson(String json_file) {
            try {
                byte[] bytes = Files.readAllBytes(Paths.get(Norg2RestClient.class.getResource(json_file).toURI()));
                return new String(bytes);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}