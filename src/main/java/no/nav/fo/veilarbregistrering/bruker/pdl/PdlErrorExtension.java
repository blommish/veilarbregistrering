package no.nav.fo.veilarbregistrering.bruker.pdl;

public class PdlErrorExtension {
    private String code;
    private String classification;
    private PdlErrorDetails details;

    public PdlErrorExtension() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public PdlErrorDetails getDetails() {
        return details;
    }

    public void setDetails(PdlErrorDetails details) {
        this.details = details;
    }
}
