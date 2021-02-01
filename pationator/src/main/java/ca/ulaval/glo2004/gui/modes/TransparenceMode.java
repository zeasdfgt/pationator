package ca.ulaval.glo2004.gui.modes;

import ca.ulaval.glo2004.domain.Accessoires.Accessoire;
import ca.ulaval.glo2004.domain.Rampe.Balustre;
import ca.ulaval.glo2004.domain.patio.*;

public enum TransparenceMode {
    POUTRES(PoutrePlanche.class),
    SOLIVES(Solive.class),
    RECOUVREMENT(RecouvrementPlanche.class),
    POTEAUX(Poteau.class),
    RAMPE(Balustre.class),
    ACCESSOIRES(Accessoire.class);

    private final Class<?> classe;

    TransparenceMode(Class<?> classe) { this.classe = classe; }

    public Class<?> getClasse() { return classe; }

}
