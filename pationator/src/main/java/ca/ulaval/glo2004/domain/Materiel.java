package ca.ulaval.glo2004.domain;

import ca.ulaval.glo2004.domain.patio.TypeBois;

import java.io.Serializable;
import java.util.Objects;

public class Materiel implements Serializable {

   private final TypeBois typeMateriel;
   private final String typePlanche;
   private int quantite;
   private final float longueur;

   public Materiel(TypeBois type, int quantite, float longueur, String typePlanche) {
      this.typeMateriel = type;
      this.quantite = quantite;
      this.longueur = longueur;
      this.typePlanche = typePlanche;
   }

   public String getTypePlanche() {
      return typePlanche;
   }

   public void upQuantite() {
      this.quantite++;
   }

   public TypeBois getTypeMateriel() {
      return typeMateriel;
   }

   public int getQuantite() {
      return quantite;
   }

   public float getLongueur() {
      return longueur;
   }

   @Override
   public String toString() {
      String longueur_string = floatToStringFraction(this.longueur);
      return String.format("  - %sx un %s de longueur %s", quantite, typeMateriel.toString(), longueur_string);
   }

   public static String floatToStringFraction(float longueur) {
      int int_of_longueur = (int)longueur;
      float decimal_point = longueur - int_of_longueur;
      float decimal_as_64 = decimal_point * 64;
      int decimal_as_64_int = Math.round(decimal_as_64);

      if(decimal_as_64_int == 0) {
         return String.valueOf(int_of_longueur);
      } else if(decimal_as_64_int == 64) {
         return String.valueOf(int_of_longueur + 1);
      } else {
         int gcd = getGCD(decimal_as_64_int, 64);
         decimal_as_64_int = decimal_as_64_int / gcd;
         int denominator = 64 / gcd;

         return String.format("%s %s/%s", int_of_longueur, decimal_as_64_int, denominator);
      }
   }

   public static int getGCD(int a, int b){
      if(b==0) return a;
      return getGCD(b, a%b);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Materiel materiel = (Materiel) o;
      return quantite == materiel.quantite &&
          Float.compare(materiel.longueur, longueur) == 0 &&
          Objects.equals(typeMateriel, materiel.typeMateriel) &&
          Objects.equals(typePlanche, materiel.typePlanche);
   }

   @Override
   public int hashCode() {
      return Objects.hash(typeMateriel, quantite, longueur, typePlanche);
   }
}
