package no.nav.fo.veilarbregistrering.oppgave;

public interface OppgaveGateway {

    Oppgave opprettOppgave(String aktoerId, String tilordnetRessurs, String beskrivelse);

}
