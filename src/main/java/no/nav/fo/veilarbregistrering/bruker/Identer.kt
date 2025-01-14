package no.nav.fo.veilarbregistrering.bruker

import no.nav.fo.veilarbregistrering.bruker.feil.ManglendeBrukerInfoException

class Identer(val identer: List<Ident>) {
    fun finnGjeldendeFnr(): Foedselsnummer {
        val gjeldendeFnr = identer
            .filter { it.gruppe == Gruppe.FOLKEREGISTERIDENT }
            .firstOrNull { !it.isHistorisk }
            ?: throw ManglendeBrukerInfoException("Kunne ikke finne et gjeldende fødselsnummer")
        return Foedselsnummer.of(gjeldendeFnr.ident)
    }

    fun finnGjeldendeAktorId(): AktorId {
        val gjeldendeAktorId = identer
            .filter { it.gruppe == Gruppe.AKTORID }
            .firstOrNull { !it.isHistorisk }
            ?: throw ManglendeBrukerInfoException("Kunne ikke finne en gjeldende aktørId")
        return AktorId(gjeldendeAktorId.ident)
    }

    fun finnHistoriskeFoedselsnummer(): List<Foedselsnummer> =
        identer
            .filter { it.gruppe == Gruppe.FOLKEREGISTERIDENT && it.isHistorisk}
            .map { it.ident }
            .map { Foedselsnummer.of(it) }

    companion object {
        @JvmStatic
        fun of(identer: List<Ident>): Identer {
            return Identer(identer)
        }
    }
}