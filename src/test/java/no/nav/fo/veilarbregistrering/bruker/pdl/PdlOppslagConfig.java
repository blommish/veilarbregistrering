package no.nav.fo.veilarbregistrering.bruker.pdl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PdlOppslagConfig {

    private final String PDL_PROPERTY_NAME = "PDL_URL";

    @Bean
    PdlOppslagClient pdlOppslagService() {
        return new PdlOppslagClient(null, null) {
        };
    }
}
