package no.nav.fo.veilarbregistrering.db.arbeidssoker;

import no.nav.fo.veilarbregistrering.arbeidssoker.*;
import no.nav.fo.veilarbregistrering.bruker.Foedselsnummer;
import no.nav.sbl.sql.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.fo.veilarbregistrering.db.arbeidssoker.ArbeidssokerperioderMapper.map;

public class ArbeidssokerRepositoryImpl implements ArbeidssokerRepository {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidssokerRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ArbeidssokerRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public long lagre(EndretFormidlingsgruppeCommand endretFormidlingsgruppeCommand) {
        long id = nesteFraSekvens("FORMIDLINGSGRUPPE_SEQ");
        try {
            SqlUtils.insert(jdbcTemplate, "FORMIDLINGSGRUPPE")
                    .value("ID", id)
                    .value("FOEDSELSNUMMER", endretFormidlingsgruppeCommand.getFoedselsnummer()
                            .orElseThrow(() -> new IllegalStateException("Foedselsnummer var ikke satt. Skulle vært filtrert bort i forkant!"))
                            .stringValue())
                    .value("PERSON_ID", endretFormidlingsgruppeCommand.getPersonId())
                    .value("PERSON_ID_STATUS", endretFormidlingsgruppeCommand.getPersonIdStatus())
                    .value("OPERASJON", endretFormidlingsgruppeCommand.getOperation().name())
                    .value("FORMIDLINGSGRUPPE", endretFormidlingsgruppeCommand.getFormidlingsgruppe().stringValue())
                    .value("FORMIDLINGSGRUPPE_ENDRET", Timestamp.valueOf(endretFormidlingsgruppeCommand.getFormidlingsgruppeEndret()))
                    .value("FORR_FORMIDLINGSGRUPPE", endretFormidlingsgruppeCommand.getForrigeFormidlingsgruppe()
                            .map(Formidlingsgruppe::stringValue)
                            .orElse(null))
                    .value("FORR_FORMIDLINGSGRUPPE_ENDRET", endretFormidlingsgruppeCommand.getForrigeFormidlingsgruppeEndret()
                            .map(Timestamp::valueOf)
                            .orElse(null))
                    .value("FORMIDLINGSGRUPPE_LEST", Timestamp.valueOf(LocalDateTime.now()))
                    .execute();
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(
                    String.format("Lagring av følgende formidlingsgruppeendring feilet: %s", endretFormidlingsgruppeCommand),
                    e
            );
        }
        return id;
    }

    private long nesteFraSekvens(String sekvensNavn) {
        return jdbcTemplate.queryForObject("select " + sekvensNavn + ".nextval from dual", Long.class);
    }

    @Override
    public Arbeidssokerperioder finnFormidlingsgrupper(Foedselsnummer foedselsnummer) {
        String sql = "SELECT * FROM FORMIDLINGSGRUPPE WHERE FOEDSELSNUMMER = ?";

        List<ArbeidssokerperiodeRaaData> arbeidssokerperioder = jdbcTemplate.query(
                sql,
                new Object[]{foedselsnummer.stringValue()}, new ArbeidssokerperiodeRaaDataRowMapper()
        );

        return map(arbeidssokerperioder);
    }

    @Override
    public Arbeidssokerperioder finnFormidlingsgrupper(List<Foedselsnummer> listeMedFoedselsnummer) {
        String sql = "SELECT * FROM FORMIDLINGSGRUPPE WHERE FOEDSELSNUMMER IN (:foedselsnummer)";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("foedselsnummer", listeMedFoedselsnummer.stream().map(f -> f.stringValue()).collect(Collectors.toList()));

        List<ArbeidssokerperiodeRaaData> raaData = namedParameterJdbcTemplate.query(sql, parameters, new ArbeidssokerperiodeRaaDataRowMapper());

        LOG.info(String.format("Fant følgende rådata med formidlingsgruppeendringer: %s", raaData));

        return map(raaData);
    }

    private class ArbeidssokerperiodeRaaDataRowMapper implements RowMapper<ArbeidssokerperiodeRaaData> {

        @Override
        public ArbeidssokerperiodeRaaData mapRow(ResultSet rs, int i) throws SQLException {
            return new ArbeidssokerperiodeRaaData(
                    rs.getString("FORMIDLINGSGRUPPE"),
                    rs.getInt("PERSON_ID"),
                    rs.getString("PERSON_ID_STATUS"),
                    rs.getTimestamp("FORMIDLINGSGRUPPE_ENDRET"));
        }
    }
}
