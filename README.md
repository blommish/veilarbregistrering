# Veilarbregistrering

Backend-applikasjon for [Arbeidssøkerregistrering](https://github.com/navikt/arbeidssokerregistrering) som tar i mot nyregistrering av arbeidssøkere.

![](https://github.com/navikt/veilarbregistrering/workflows/Build,%20push,%20deploy%20%F0%9F%92%AA/badge.svg)

## API
Se https://veilarbregistrering.nais.adeo.no/veilarbregistrering/internal/swagger/index.html?input_baseurl=/veilarbregistrering/api/swagger.json 
for beskrivelse av APIet til `veilarbregistrering`.

## Bounded context Canvas
Se https://miro.com/miroverse/the-bounded-context-canvas/ for eksempel

## Innkommende kommuniksjon (inbound communication)
| Collaborator | Query/Command/Event | Melding |
| --- | --- | --- |
| Arbeidssokerregistrering | query (REST/GET) | /startregistrering |
| Arbeidssokerregistrering | command (REST/POST) | /startregistrering |
| Arbeidssokerregistrering | query (REST/GET) | /registrering |
| PTO | query (REST/GET) | /registrering |
| Arbeidssokerregistrering | command (REST/POST) | /startreaktivering |
| Arbeidssokerregistrering | command (REST/POST) | /startregistrersykmeldt |
| Arbeidssokerregistrering | query (REST/GET) | /sistearbeidsforhold |
| Arbeidssokerregistrering | query (REST/GET) | /person/kontaktinfo |
| Arbeidssokerregistrering | command (REST/POST) | /oppgave |
| Dagpenger | query (REST/GET) | /arbeidssoker/perioder |
| Arena | event (Kafka) | FormidlingsgruppeEvent |
| Helse | query (REST/GET) | /sykmeldtinfodata |

## Utgående kommunikasjon (outbound communication)
| Melding | Query/Command/Event | Collaborator |
| :--- | :--- | :--- |
| Arbeidssokerperioder | query (REST/GET) | [Arena ORDS](src/main/java/no/nav/fo/veilarbregistrering/arbeidssoker/adapter/README.md) |
| Aktivering og reaktivering | Command (REST/POST) | veilarboppfolging (og Arena) |
| Oppfølgingsstatus | Query (REST/GET) | veilarboppfolging (og Arena) |
| Geografisk tilknytning | Query (REST/GET) | veilarbperson |
| Tilgangskontroll | --- | ABAC |
| Siste arbeidsforhold | Query (REST/GET) | [Aareg](src/main/java/no/nav/fo/veilarbregistrering/arbeidsforhold/adapter/README.md) |
| --- | Query (REST/GET) | [Enhetsregisteret](src/main/java/no/nav/fo/veilarbregistrering/enhet/adapter/README.md) |
| --- | query (REST/GET) | [NAV Organisasjon (for veileder pr ident)](src/main/java/no/nav/fo/veilarbregistrering/orgenhet/adapter/README.md) |
| maksdato | Query (REST/GET) | Infotrygd |
| feature toggle | Query (REST/GET) | Unleash |
| "kontakt bruker"-oppgave | --- | [Oppgave](src/main/java/no/nav/fo/veilarbregistrering/oppgave/adapter/README.md) |
| Personopplysninger og identer | Query (Graphql) | [PDL](src/main/java/no/nav/fo/veilarbregistrering/bruker/pdl/README.md) |
| Kontaktinfo | Query (REST/GET) | [KRR](src/main/java/no/nav/fo/veilarbregistrering/bruker/krr/README.md) |
| ArbeidssokerProfilertEvent | Event | srvveilarbportefolje |
| ArbeidssokerRegistrertEvent | Event | srvveilarbportefolje |
| ArbeidssokerRegistrertEvent | Event | finn-kandidat-api |
| KontaktBrukerOpprettetEvent | Event | veilarbregistrering |

# Komme i gang

```
# bygge
mvn clean install 

# test
mvn test

# starte
# Kjør main-metoden i Main.java
# For lokal test kjøring kjør ApplicationLocal.java
```

---

# Henvendelser

Spørsmål knyttet til koden eller prosjektet kan stilles via issues her på github.

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen `#område-arbeid-paw`.
