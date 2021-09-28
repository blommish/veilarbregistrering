package no.nav.fo.veilarbregistrering.bruker

import no.nav.fo.veilarbregistrering.bruker.FnrUtils.calculateK1
import no.nav.fo.veilarbregistrering.bruker.FnrUtils.calculateK2
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.random.Random

object FoedselsnummerTestdataBuilder {
    /**
     * Returnerer fødselsnummer til Aremark som er fiktivt
     */
    @JvmStatic
    fun aremark(): Foedselsnummer {
        return Foedselsnummer.of("10108000398")
    }

    fun getFodselsnummerAsStringOnDateMinusYears(localDate: LocalDate, minusYears: Int): String {
        val date = localDate.atStartOfDay(ZoneId.systemDefault()).minusYears(minusYears.toLong()).toLocalDate()
        return getFoedselsnummerForDate(date)
    }

    fun foedselsnummerOnDateMinusYears(localDate: LocalDate, minusYears: Int): Foedselsnummer {
        val date = localDate.atStartOfDay(ZoneId.systemDefault()).minusYears(minusYears.toLong()).toLocalDate()
        return Foedselsnummer.of(getFoedselsnummerForDate(date))
    }
    @Test
    fun d() {
        println(getFoedselsnummerForDate(date = LocalDate.now().minusYears(30), true))
    }

    private fun getFoedselsnummerForDate(date: LocalDate, dNumber: Boolean = false): String {
        while (true) {
            val datostreng = date.format(DateTimeFormatter.ofPattern("ddMMuu"))

            val datogruppe = if (dNumber) tilDNummer(datostreng) else datostreng
            val individnummer = tilfeldigIndividnummerFor(date.year).toString().padStart(3, '0')

            val k1 = calculateK1("$datogruppe$individnummer")
            val k2 = calculateK2("$datogruppe$individnummer${k1 % 10}") // mod 10: Hvis k1 var 10, bruk 0

            if (k1 != 10 && k2 != 10) // I enkelte tilfeller blir checksum 10, slike fnr forkastes
                return "$datogruppe$individnummer$k1$k2"
        }
    }

    private fun tilDNummer(dato: String) =
        "${dato.take(1).toInt() + 4}${dato.substring(1)}"


    private fun tilfeldigIndividnummerFor(year: Int): Int = when (year) {
        in 1854..1899 -> Random.nextInt(500, 750)
        in 1900..1999 -> Random.nextInt(0, 500)
        in 2000..2039 -> Random.nextInt(500, 999)
        else -> throw IllegalArgumentException("Personen er for gammel eller ikke født ennå")
    }
}
