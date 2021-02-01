package ca.ulaval.glo2004.domain.patio;

import ca.ulaval.glo2004.domain.mesh.Point3D;
import ca.ulaval.glo2004.gui.dto.ParametresDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatioFactoryTest extends PatioFactory {
    private PatioFactory patioFactory;
    private Params params;
    private ParametresDTO parametresDTO;
    private Patio patio;
    private final float LONGUEUR_PORTEAFO = 16;
    private final float ESPACE_SOLIVES = 16;
    private final float LARGEUR_PATIO = 72;
    private final float LONGUEUR_PATIO = 210f;
    private final float HAUTEUR_PATIO = 96f;
    private final int NOMBRE_POUTRES = 5;
    private final float ESPACE_RECOUVREMENT = 0.5f;
    private final TypeBois TYPE_POTEAU = new TypeBois("6X6");
    private final TypeBois TYPE_POUTRE = new TypeBois("2X4");
    private final TypeBois TYPE_RECOUVREMENT = new TypeBois("1X5");
    private final TypeBois TYPE_SOLIVES = new TypeBois("2X8");
    private final float LONGUEUR_POTEAU = HAUTEUR_PATIO - TYPE_RECOUVREMENT.getLargeur().getValeurReelle() - TYPE_SOLIVES.getHauteur().getValeurReelle() - TYPE_POUTRE.getHauteur().getValeurReelle();


    @BeforeEach
    public void setUp() {
        parametresDTO = new ParametresDTO();
        parametresDTO.la_patio = LARGEUR_PATIO;
        parametresDTO.lo_patio = LONGUEUR_PATIO;
        parametresDTO.lo_porteafo = LONGUEUR_PORTEAFO;
        parametresDTO.esp_solives = ESPACE_SOLIVES;
        parametresDTO.nb_poutres = NOMBRE_POUTRES;
        parametresDTO.esp_recouvre = ESPACE_RECOUVREMENT;
        parametresDTO.ha_patio = HAUTEUR_PATIO;
        parametresDTO.typePoteau = TYPE_POTEAU;
        parametresDTO.typePoutre = TYPE_POUTRE;
        parametresDTO.typeRecouvre = TYPE_RECOUVREMENT;
        parametresDTO.typeSolives = TYPE_SOLIVES;

        params = new Params(parametresDTO);
        patioFactory = new PatioFactory();

    }

    @Test
    public void givenAPatioWithoutPorteAFo_whenCalculatingFirstPoutrePosition_thenFirstPoutreIsPlacedOnXAxis() {
        params.lo_porteafo = 0;
        patio = patioFactory.creerPatio(params);
        int noPoutre = 0;

        float expectedXposition = 0;
        float expectedYposition = 0;
        float expectedZposition = LONGUEUR_POTEAU;
        Point3D expectedPosition = new Point3D(expectedXposition, expectedYposition, expectedZposition);
        Point3D returnedPosition = patio.getPoutres().get(noPoutre).getPrisme().get(0).getPoints().get(0);

        assertEquals(expectedPosition, returnedPosition);
    }

    @Test
    public void givenAPatioWithPorteAFo_whenCalculatingFirstPoutrePosition_thenFirstPoutreIsOffsetByPoretafoLenghtOnXAxis() {
        patio = givenDefaultPatio();
        int noPoutre = 0;

        float expectedXposition = LONGUEUR_PORTEAFO;
        float expectedYposition = 0;
        float expectedZposition = LONGUEUR_POTEAU;
        Point3D expectedPosition = new Point3D(expectedXposition, expectedYposition, expectedZposition);
        Point3D returnedPosition = patio.getPoutres().get(noPoutre).getPosition();

        assertEquals(expectedPosition, returnedPosition);
    }

    @Test
    public void givenAPatioBuiltByFactory_whenCalculatingSecondAndSubesquentPoutrePosition_thenReturnsAccuratePosition() {
        int noPoutre = 3;
        patio = givenDefaultPatio();

        float expectedXposition = LONGUEUR_PORTEAFO + noPoutre * params.esp_poutres;
        float expectedYposition = 0;
        float expectedZposition = LONGUEUR_POTEAU;
        Point3D expectedPosition = new Point3D(expectedXposition, expectedYposition, expectedZposition);
        Point3D returnedPosition = patio.getPoutres().get(noPoutre).getPosition();

        assertEquals(expectedPosition, returnedPosition);
    }

    @Test
    public void givenAPatioBuiltByFactory_whenCreatingFirstPli_thenIsInSamePositionAsPoutrePosition() {
        int noPoutre = 1;
        patio = givenDefaultPatio();

        Point3D expectedPosition = patio.getPoutres().get(noPoutre).getPosition();
        Point3D returnedPosition = patio.getPoutres().get(noPoutre).getPlis().get(0).getPosition();

        assertEquals(expectedPosition, returnedPosition);
    }

    private Patio givenDefaultPatio() {
        return patioFactory.creerPatio(params);
    }
}