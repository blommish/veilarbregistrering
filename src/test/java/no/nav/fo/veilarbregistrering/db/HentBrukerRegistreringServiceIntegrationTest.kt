package no.nav.fo.veilarbregistrering.db

import io.mockk.every
import io.mockk.mockk
import no.nav.common.featuretoggle.UnleashService
import no.nav.fo.veilarbregistrering.besvarelse.StillingTestdataBuilder.gyldigStilling
import no.nav.fo.veilarbregistrering.bruker.AktorId
import no.nav.fo.veilarbregistrering.bruker.Bruker
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer
import no.nav.fo.veilarbregistrering.enhet.Kommunenummer
import no.nav.fo.veilarbregistrering.oppfolging.OppfolgingGateway
import no.nav.fo.veilarbregistrering.oppfolging.Oppfolgingsstatus
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway
import no.nav.fo.veilarbregistrering.profilering.ProfileringRepository
import no.nav.fo.veilarbregistrering.profilering.ProfileringService
import no.nav.fo.veilarbregistrering.profilering.ProfileringTestdataBuilder.lagProfilering
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.bruker.BrukerTilstandService
import no.nav.fo.veilarbregistrering.registrering.bruker.HentRegistreringService
import no.nav.fo.veilarbregistrering.registrering.bruker.OrdinaerBrukerRegistreringTestdataBuilder
import no.nav.fo.veilarbregistrering.registrering.manuell.ManuellRegistreringRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringFormidling
import no.nav.fo.veilarbregistrering.registrering.formidling.RegistreringFormidlingRepository
import no.nav.fo.veilarbregistrering.registrering.formidling.Status
import no.nav.fo.veilarbregistrering.sykemelding.SykemeldingService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDate
import java.util.*

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = [RepositoryConfig::class, DatabaseConfig::class, HentBrukerRegistreringServiceIntegrationTest.Companion.TestContext::class])
class HentBrukerRegistreringServiceIntegrationTest(
        @Autowired val brukerRegistreringRepository: BrukerRegistreringRepository,
        @Autowired val registreringFormidlingRepository: RegistreringFormidlingRepository,
        @Autowired val hentRegistreringService: HentRegistreringService,
        @Autowired var oppfolgingGateway: OppfolgingGateway,
        @Autowired var profileringService: ProfileringService
) {

    @BeforeEach
    fun setupEach() {
        every { oppfolgingGateway.hentOppfolgingsstatus(any()) } returns Oppfolgingsstatus(
            false,
            false,
            null,
            null,
            null,
            null
        )
        every { profileringService.profilerBruker(any(), any(), any()) } returns lagProfilering()
    }

    @Test
    fun `henter opp siste brukerregistrering med filtre på tilstand`() {
        brukerRegistreringRepository.lagre(SELVGAENDE_BRUKER, BRUKER).id.let { id ->
            registreringFormidlingRepository.lagre(RegistreringFormidling.medStatus(Status.OVERFORT_ARENA, id))
        }
        brukerRegistreringRepository.lagre(BRUKER_UTEN_JOBB, BRUKER).id.let { id ->
            registreringFormidlingRepository.lagre(RegistreringFormidling.medStatus(Status.OVERFORT_ARENA, id))
        }
        assertEquals(hentRegistreringService.hentOrdinaerBrukerRegistrering(BRUKER).sisteStilling, gyldigStilling())
    }

    companion object {
        private val ident = Foedselsnummer.of("10108000398") //Aremark fiktivt fnr.";
        private val BRUKER = Bruker.of(ident, AktorId.of("AKTØRID"))
        private val BRUKER_UTEN_JOBB = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistreringUtenJobb()
            .setOpprettetDato(LocalDate.of(2014, 12, 8).atStartOfDay())
        private val SELVGAENDE_BRUKER = OrdinaerBrukerRegistreringTestdataBuilder.gyldigBrukerRegistrering()
            .setOpprettetDato(LocalDate.of(2018, 12, 8).atStartOfDay())

        @Configuration
        open class TestContext {
            @Bean
            open fun hentRegistreringService(
                db: JdbcTemplate,
                brukerRegistreringRepository: BrukerRegistreringRepository,
                profileringRepository: ProfileringRepository,
                manuellRegistreringRepository: ManuellRegistreringRepository
            ) = HentRegistreringService(
                brukerRegistreringRepository,
                profileringRepository,
                manuellRegistreringRepository,
                norg2Gateway()
            )

            @Bean
            open fun hentBrukerTilstandService(
                oppfolgingGateway: OppfolgingGateway,
                sykemeldingService: SykemeldingService,
                unleashService: UnleashService,
                brukerRegistreringRepository: BrukerRegistreringRepository,
            ): BrukerTilstandService {
                return BrukerTilstandService(
                    oppfolgingGateway,
                    sykemeldingService,
                    unleashService,
                    brukerRegistreringRepository
                )
            }

            @Bean
            open fun unleashService(): UnleashService = mockk()

            @Bean
            open fun sykemeldingService(): SykemeldingService = mockk()

            @Bean
            open fun oppfolgingGateway(): OppfolgingGateway = mockk()

            @Bean
            open fun profileringService(): ProfileringService = mockk()

            @Bean
            open fun norg2Gateway() = object : Norg2Gateway {
                override fun hentEnhetFor(kommunenummer: Kommunenummer): Optional<Enhetnr> {
                    if (Kommunenummer.of("1241") == kommunenummer) {
                        return Optional.of(Enhetnr.of("232"))
                    }
                    return if (Kommunenummer.of(Kommunenummer.KommuneMedBydel.STAVANGER) == kommunenummer) {
                        Optional.of(Enhetnr.of("1103"))
                    } else Optional.empty()
                }

                override fun hentAlleEnheter(): Map<Enhetnr, NavEnhet> = emptyMap()

            }
        }
    }
}
