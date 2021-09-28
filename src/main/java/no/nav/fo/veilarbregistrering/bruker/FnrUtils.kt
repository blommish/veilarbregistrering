package no.nav.fo.veilarbregistrering.bruker

import no.nav.fo.veilarbregistrering.metrics.RequestTimeFilter.Companion.LOG
import java.time.LocalDate
import java.time.Period

internal object FnrUtils {
    fun utledAlderForFnr(fnr: String, dagensDato: LocalDate): Int {
        return antallAarSidenDato(utledFodselsdatoForFnr(fnr), dagensDato)
    }

    fun utledFodselsdatoForFnr(fnr: String): LocalDate {
        val aar = utledFoedselsaar(fnr)
        val maaned = utledMaaned(fnr)
        val dag = fnr.slice(0..1).toInt()
        return LocalDate.of(
            aar,
            maaned,
            dag,
        )
    }

    fun validerFoedselsnummer(foedselsnummer: Foedselsnummer) =
        validerFoedselsnummer(foedselsnummer.stringValue())

    fun validerFoedselsnummer(foedselsnummer: String): Boolean {
        if (foedselsnummer.length != 11) return false

        val k1 = calculateK1(foedselsnummer.slice(0..8))
        val k2 = calculateK2(foedselsnummer.slice(0..9))

        return k1 == foedselsnummer[9].toString().toInt() && k2 == foedselsnummer[10].toString().toInt()
    }

    internal fun calculateK1(nr: String): Int =
        11 - nr.chunked(1)
            .mapIndexed { i, siffer ->
                k1Factors[i] * siffer.toInt()
            }.sum() % 11

    internal fun calculateK2(nr: String): Int =
        11 - nr.chunked(1)
            .mapIndexed { i, siffer ->
                k2Factors[i] * siffer.toInt()
            }.sum() % 11

    private fun utledMaaned(fnr: String): Int {
        var maaned = fnr.slice(2..3).toInt()
        if (maaned in 1..12) return maaned

        LOG.info("Utleder fødselsmåned for NPID eller syntetisk fnr")

        while (maaned > 12) {
            maaned -= 10
        }
        return maaned
    }

    private fun utledFoedselsaar(fnr: String): Int {
        val individnummer = fnr.slice(6..8).toInt()
        val tiaar = fnr.slice(4..5).toInt()
        return tiaar + when {
            individnummer <= 499 -> 1900

            individnummer >= 500 && tiaar < 40 -> 2000

            individnummer in 500..749 && tiaar >= 54 -> 1800

            individnummer >= 900 && tiaar > 39 -> 1900

            else -> throw IllegalStateException("Kunne ikke utlede Foedselsaar fra fnr: $fnr")
        }
    }

    fun antallAarSidenDato(dato: LocalDate?, dagensDato: LocalDate?): Int {
        return Period.between(dato, dagensDato).years
    }

    private val k1Factors = listOf(3,7,6,1,8,9,4,5,2)
    private val k2Factors = listOf(5,4,3,2,7,6,5,4,3,2)
}