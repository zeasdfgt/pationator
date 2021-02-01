package ca.ulaval.glo2004.domain.mesh;

import ca.ulaval.glo2004.domain.drawing.Matrice;
import ca.ulaval.glo2004.domain.drawing.MatriceFactory;

public enum Orientation {

  PARALX(new MatriceFactory().identitee()),
  PARALY(Matrice.PermuterL1L2(new MatriceFactory().identitee())),
  PARALZ(Matrice.PermuterL2L3(Matrice.PermuterL1L2(new MatriceFactory().identitee()))),
  PARALY_PERPZ(Matrice.PermuterL2L3(Matrice.PermuterL1L3(new MatriceFactory().identitee()))),
  PARALX_PERPZ(Matrice.PermuterL2L3(new MatriceFactory().identitee()));

  private final Matrice matrice;

  Orientation(Matrice matrice) {
    this.matrice = matrice;
  }

  public Matrice getMatrice() {
    return this.matrice;
  }
}

