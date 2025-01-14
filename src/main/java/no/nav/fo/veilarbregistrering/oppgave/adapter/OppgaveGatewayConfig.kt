package no.nav.fo.veilarbregistrering.oppgave.adapter

import no.nav.common.sts.ServiceToServiceTokenProvider
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.fo.veilarbregistrering.config.requireProperty
import no.nav.fo.veilarbregistrering.oppgave.OppgaveGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OppgaveGatewayConfig {
    @Bean
    fun oppgaveRestClient(
        systemUserTokenProvider: SystemUserTokenProvider,
        tokenProvider: ServiceToServiceTokenProvider
    ): OppgaveRestClient {
        val cluster = requireProperty(OPPGAVE_CLUSTER)
        val env = requireProperty("APP_ENVIRONMENT_NAME")
        val serviceName = if (env == "p") "oppgave" else "oppgave-q1"

        return OppgaveRestClient(requireProperty(OPPGAVE_PROPERTY_NAME)) {
            tokenProvider.getServiceToken(
                serviceName,
                "oppgavehandtering",
                cluster
            )
        }
    }

    @Bean
    fun oppgaveGateway(oppgaveRestClient: OppgaveRestClient): OppgaveGateway = OppgaveGatewayImpl(oppgaveRestClient)

    companion object {
        const val OPPGAVE_PROPERTY_NAME = "OPPGAVE_URL"
        const val OPPGAVE_CLUSTER = "OPPGAVE_CLUSTER"
    }
}