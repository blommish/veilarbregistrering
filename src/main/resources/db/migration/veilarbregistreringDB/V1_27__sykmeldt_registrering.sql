CREATE SEQUENCE SYKMELDT_REGISTRERING_SEQ;

CREATE TABLE SYKMELDT_REGISTRERING (
  SYKMELDT_REGISTRERING_ID  NUMBER,
  AKTOR_ID                VARCHAR(20) NOT NULL,
  OPPRETTET_DATO          TIMESTAMP,
  TEKSTER_FOR_BESVARELSE  VARCHAR(4000),
  FREMTIDIG_SITUASJON VARCHAR(30),
  TILBAKE_ETTER_52_UKER VARCHAR(30),
  NUS_KODE VARCHAR(6),
  UTDANNING_BESTATT VARCHAR(30),
  UTDANNING_GODKJENT_NORGE VARCHAR(30),
  ANDRE_UTFORDRINGER VARCHAR(30),
  CONSTRAINT "SYKMELDT_REGISTRERING_PK" PRIMARY KEY ("SYKMELDT_REGISTRERING_ID")
);

CREATE INDEX SYKMELDT_REG_AKTOR_ID_INDEX ON SYKMELDT_REGISTRERING ("AKTOR_ID");