package ca.ulaval.glo2004.domain;

public class NormeResultat {
    private String nomNorme;
    private final boolean respecte;
    private final String message;
    private final String correction;

    NormeResultat(String nomNorme, boolean respecte, String message, String correction){
        this.nomNorme = nomNorme;
        this.respecte = respecte;
        this.message = message;
        this.correction = correction;
    }

    public boolean isRespecte() {
        return respecte;
    }

    public String getMessage() {
        return message;
    }

    public String getCorrection() {
        return correction;
    }

    public String getNomNorme() {
        return nomNorme;
    }

    public void setNomNorme(String nomNorme) {
        this.nomNorme = nomNorme;
    }
}
