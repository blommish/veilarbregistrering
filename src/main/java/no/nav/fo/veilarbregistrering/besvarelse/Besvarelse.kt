package no.nav.fo.veilarbregistrering.besvarelse

data class Besvarelse(
    val utdanning: UtdanningSvar? = null,
    val utdanningBestatt: UtdanningBestattSvar? = null,
    val utdanningGodkjent: UtdanningGodkjentSvar? = null,
    val helseHinder: HelseHinderSvar? = null,
    val andreForhold: AndreForholdSvar? = null,
    val sisteStilling: SisteStillingSvar? = null,
    val dinSituasjon: DinSituasjonSvar? = null,
    val fremtidigSituasjon: FremtidigSituasjonSvar? = null,
    val tilbakeIArbeid: TilbakeIArbeidSvar? = null,
) {
    fun anbefalerBehovForArbeidsevnevurdering(): Boolean {
        return HelseHinderSvar.JA == helseHinder || AndreForholdSvar.JA == andreForhold
    }

    fun anbefalerStandardInnsats(alder: Int, oppfyllerKravTilArbeidserfaring: Boolean): Boolean {
        return (18 <= alder && alder <= 59
                && oppfyllerKravTilArbeidserfaring
                && UtdanningSvar.INGEN_UTDANNING != utdanning
                && UtdanningBestattSvar.JA == utdanningBestatt && UtdanningGodkjentSvar.JA == utdanningGodkjent && HelseHinderSvar.NEI == helseHinder && AndreForholdSvar.NEI == andreForhold)
    }

    override fun toString(): String {
        return "Besvarelse(utdanning=" + utdanning + ", utdanningBestatt=" + utdanningBestatt + ", utdanningGodkjent=" + utdanningGodkjent + ", helseHinder=" + helseHinder + ", andreForhold=" + andreForhold + ", sisteStilling=" + sisteStilling + ", dinSituasjon=" + dinSituasjon + ", fremtidigSituasjon=" + fremtidigSituasjon + ", tilbakeIArbeid=" + tilbakeIArbeid + ")"
    }
}