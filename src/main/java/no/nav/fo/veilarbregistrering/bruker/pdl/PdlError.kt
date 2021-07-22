package no.nav.fo.veilarbregistrering.bruker.pdl

data class PdlError(
    val message: String? = null,
    val locations: List<PdlErrorLocation>? = null,
    val path: List<String>? = null,
    val extensions: PdlErrorExtension? = null
)