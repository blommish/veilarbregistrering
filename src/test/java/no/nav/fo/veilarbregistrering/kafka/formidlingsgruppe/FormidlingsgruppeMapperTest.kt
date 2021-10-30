package no.nav.fo.veilarbregistrering.kafka.formidlingsgruppe

import no.nav.fo.veilarbregistrering.FileToJson.toJson
import no.nav.fo.veilarbregistrering.arbeidssoker.Formidlingsgruppe
import no.nav.fo.veilarbregistrering.arbeidssoker.Operation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class FormidlingsgruppeMapperTest {
    @Test
    fun `skal feile ved manglende mod dato`() {
        val json = toJson("/kafka/formidlingsgruppe_uten_mod_dato.json")
        assertThrows<NullPointerException> { FormidlingsgruppeMapper.map(json) }
    }

    @Test
    fun `skal mappe json med mod dato fra gg arena formidlingsgruppe v1 til formidlingsgruppeEvent`() {
        val json = toJson("/kafka/formidlingsgruppe_med_mod_dato.json")
        val formidlingsgruppeEvent = FormidlingsgruppeMapper.map(json)
        assertThat(formidlingsgruppeEvent.foedselsnummer?.stringValue()).isEqualTo("***********")
        assertThat(formidlingsgruppeEvent.personId).isEqualTo("3226568")
        assertThat(formidlingsgruppeEvent.formidlingsgruppe).isEqualTo(Formidlingsgruppe.of("ARBS"))
        assertThat(formidlingsgruppeEvent.formidlingsgruppeEndret)
            .isEqualTo(LocalDateTime.of(2020, 6, 19, 9, 31, 50))
    }

    @Test
    fun `skal mappe json uten fnr fra gg arena formidlingsgruppe v1 til formidlingsgruppeEvent`() {
        val json = toJson("/kafka/formidlingsgruppe_uten_fnr.json")
        val formidlingsgruppeEvent = FormidlingsgruppeMapper.map(json)
        assertNull(formidlingsgruppeEvent.foedselsnummer)
        assertThat(formidlingsgruppeEvent.personId).isEqualTo("1652")
        assertThat(formidlingsgruppeEvent.operation).isEqualTo(Operation.INSERT)
        assertThat(formidlingsgruppeEvent.formidlingsgruppe).isEqualTo(Formidlingsgruppe.of("ISERV"))
        assertThat(formidlingsgruppeEvent.formidlingsgruppeEndret)
            .isEqualTo(LocalDateTime.of(2007, 12, 3, 3, 5, 54))
    }

    @Test
    fun `skal mappe bade after og before`() {
        val json = toJson("/kafka/formidlingsgruppe_med_mod_dato.json")
        val formidlingsgruppeEvent = FormidlingsgruppeMapper.map(json)
        assertThat(formidlingsgruppeEvent.foedselsnummer?.stringValue()).isEqualTo("***********")
        assertThat(formidlingsgruppeEvent.personId).isEqualTo("3226568")
        assertThat(formidlingsgruppeEvent.personIdStatus).isEqualTo("AKTIV")
        assertThat(formidlingsgruppeEvent.operation).isEqualTo(Operation.UPDATE)
        assertThat(formidlingsgruppeEvent.formidlingsgruppe).isEqualTo(Formidlingsgruppe.of("ARBS"))
        assertThat(formidlingsgruppeEvent.formidlingsgruppeEndret)
            .isEqualTo(LocalDateTime.of(2020, 6, 19, 9, 31, 50))
        assertThat(formidlingsgruppeEvent.forrigeFormidlingsgruppe).isEqualTo(Formidlingsgruppe.of("ISERV"))
        assertThat(formidlingsgruppeEvent.forrigeFormidlingsgruppeEndret).isEqualTo(
            LocalDateTime.of(2020, 6, 18, 11, 13, 1))
    }

    @Test
    fun `mapping av op type d for delete`() {
        val json = toJson("/kafka/formidlingsgruppe_op_type_D.json")
        val formidlingsgruppeEvent = FormidlingsgruppeMapper.map(json)
        assertNull(formidlingsgruppeEvent.foedselsnummer)
        assertThat(formidlingsgruppeEvent.personId).isEqualTo("1365747")
        assertThat(formidlingsgruppeEvent.personIdStatus).isEqualTo("AKTIV")
        assertThat(formidlingsgruppeEvent.operation).isEqualTo(Operation.DELETE)
        assertThat(formidlingsgruppeEvent.formidlingsgruppe).isEqualTo(Formidlingsgruppe.of("IJOBS"))
        assertThat(formidlingsgruppeEvent.formidlingsgruppeEndret)
            .isEqualTo(LocalDateTime.of(2016, 3, 12, 0, 47, 50))
    }
}
