package src.test;



import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.domain.*;
import java.util.ArrayList;


/**
 * Pruebas del catálogo que se lee de catalogo.txt con reflexión.
 */
public class CatalogTest{
    private Catalog catalog;

    /** Lee el catálogo del juego. */
    @BeforeEach
    public void setUp() throws POOBSoccerException{
        catalog = new Catalog();
    }

    /** Los cuatro países se leen en el orden en que están en el archivo. */
    @Test
    public void shouldReadTheFourCountriesInOrder(){
        ArrayList<String> ids = catalog.getCountryIds();
        assertEquals(4, ids.size());
        assertEquals("Corea del Sur", ids.get(0));
        assertEquals("Camerún", ids.get(1));
        assertEquals("Nigeria", ids.get(2));
        assertEquals("Estados Unidos", ids.get(3));
    }

    /** Cada país queda con el tiro de poder que dice su línea. */
    @Test
    public void shouldCreateEachPowerFromItsClassName() throws POOBSoccerException{
        assertTrue(catalog.getCountry("Corea del Sur").getAbility() instanceof BlueAuraShot);
        assertTrue(catalog.getCountry("Camerún").getAbility() instanceof LightningShot);
        assertTrue(catalog.getCountry("Nigeria").getAbility() instanceof TornadoShot);
        assertTrue(catalog.getCountry("Estados Unidos").getAbility() instanceof DeceptiveShot);
    }

    /** Las canchas se arman con el ancho y el alto de su línea. */
    @Test
    public void shouldBuildTheFieldsFromTheirSizes() throws POOBSoccerException{
        assertEquals(3, catalog.getFieldIds().size());
        Field big = catalog.getField("750x600");
        assertEquals(750, big.getWidth());
        assertEquals(600, big.getHeight());
    }

    /** El travesaño se registra antes que el arco, como en el archivo. */
    @Test
    public void shouldKeepTheElementsInTheFileOrder(){
        ArrayList<FieldElement> elements = catalog.getElements();
        assertEquals(2, elements.size());
        assertTrue(elements.get(0) instanceof Crossbar);
        assertTrue(elements.get(1) instanceof Goal);
    }

    /** Pedir algo que no está en el catálogo lanza la excepción del juego. */
    @Test
    public void shouldFailWithAnUnknownProfile(){
        try{
            catalog.getProfile("Kamikaze");
            fail("Debía lanzar POOBSoccerException");
        }
        catch (POOBSoccerException e){
            assertEquals(POOBSoccerException.UNKNOWN_PROFILE + "Kamikaze", e.getMessage());
        }
    }
}
