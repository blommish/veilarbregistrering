package no.nav.fo.veilarbregistrering.orgenhet.adapter;

import no.nav.fo.veilarbregistrering.enhet.Kommune;
import no.nav.fo.veilarbregistrering.orgenhet.Enhetnr;
import no.nav.fo.veilarbregistrering.orgenhet.NavEnhet;
import no.nav.fo.veilarbregistrering.orgenhet.Norg2Gateway;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;
import static no.nav.fo.veilarbregistrering.config.CacheConfig.HENT_ALLE_ENHETER_V2;

class Norg2GatewayImpl implements Norg2Gateway {

    private final Norg2RestClient norg2RestClient;

    Norg2GatewayImpl(Norg2RestClient norg2RestClient) {
        this.norg2RestClient = norg2RestClient;
    }

    @Override
    public Optional<Enhetnr> hentEnhetFor(Kommune kommune) {
        List<RsNavKontorDto> listeMedRsNavKontorDtos = norg2RestClient.hentEnhetFor(kommune);

        return listeMedRsNavKontorDtos.stream()
                .filter(rsNavKontorDtos -> "Aktiv".equals(rsNavKontorDtos.getStatus()))
                .findFirst()
                .map(rsNavKontorDtos -> Enhetnr.Companion.of(rsNavKontorDtos.getEnhetNr()));
    }

    @Override
    @Cacheable(HENT_ALLE_ENHETER_V2)
    public Map<Enhetnr, NavEnhet> hentAlleEnheter() {
        List<RsEnhet> rsEnhets = norg2RestClient.hentAlleEnheter();

        return rsEnhets.stream()
                .map(rs -> new NavEnhet(rs.getEnhetNr(), rs.getNavn()))
                .collect(toMap(navEnhet -> Enhetnr.Companion.of(navEnhet.getId()), navEnhet -> navEnhet));
    }
}
