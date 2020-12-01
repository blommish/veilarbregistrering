CREATE OR REPLACE VIEW DVH_BRUKER_REGISTRERING AS
  (
  SELECT
    BRUKER_REGISTRERING.BRUKER_REGISTRERING_ID,
    AKTOR_ID,
    BRUKER_REGISTRERING.OPPRETTET_DATO,
    YRKESPRAKSIS,
    NUS_KODE,
    YRKESBESKRIVELSE,
    KONSEPT_ID,
    case UTDANNING_BESTATT when 'JA' then 1 when 'NEI' then 0 else -1 end as UTDANNING_BESTATT,
    case UTDANNING_GODKJENT_NORGE when 'JA' then 1 when 'NEI' then 0 when 'VET_IKKE' then 2 else -1 end as UTDANNING_GODKJENT_NORGE,
    case HAR_HELSEUTFORDRINGER when 'JA' then 1 when 'NEI' then 0 else -1 end as HELSE_UTFORDRINGER,
    case ANDRE_UTFORDRINGER when 'JA' then 1 when 'NEI' then 0 else -1 end as ANDRE_UTFORDRINGER,
    BEGRUNNELSE_FOR_REGISTRERING
  FROM BRUKER_REGISTRERING
  INNER JOIN REGISTRERING_TILSTAND ON REGISTRERING_TILSTAND.BRUKER_REGISTRERING_ID = BRUKER_REGISTRERING.BRUKER_REGISTRERING_ID
  WHERE REGISTRERING_TILSTAND.STATUS IN ('OVERFORT_ARENA', 'PUBLISERT_KAFKA', 'OPPRINNELIG_OPPRETTET_UTEN_TILSTAND')
);

CREATE OR REPLACE VIEW DVH_BRUKER_REGISTRERING_TEKST AS
  (
  SELECT
    BRUKER_REGISTRERING.BRUKER_REGISTRERING_ID,
    TEKSTER_FOR_BESVARELSE
  FROM BRUKER_REGISTRERING
  INNER JOIN REGISTRERING_TILSTAND ON REGISTRERING_TILSTAND.BRUKER_REGISTRERING_ID = BRUKER_REGISTRERING.BRUKER_REGISTRERING_ID
  WHERE REGISTRERING_TILSTAND.STATUS IN ('OVERFORT_ARENA', 'PUBLISERT_KAFKA', 'OPPRINNELIG_OPPRETTET_UTEN_TILSTAND')
);

CREATE OR REPLACE VIEW DVH_BRUKER_PROFILERING AS (
  SELECT
    BRUKER_PROFILERING.BRUKER_REGISTRERING_ID,
    PROFILERING_TYPE,
    VERDI
  FROM BRUKER_PROFILERING
  INNER JOIN REGISTRERING_TILSTAND ON REGISTRERING_TILSTAND.BRUKER_REGISTRERING_ID = BRUKER_PROFILERING.BRUKER_REGISTRERING_ID
  WHERE REGISTRERING_TILSTAND.STATUS IN ('OVERFORT_ARENA', 'PUBLISERT_KAFKA', 'OPPRINNELIG_OPPRETTET_UTEN_TILSTAND')
);